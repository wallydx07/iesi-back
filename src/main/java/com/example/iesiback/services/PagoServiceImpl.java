package com.example.iesiback.services;

import com.example.iesiback.dto.*;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.enums.EstadoPago;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.entities.User;
import com.example.iesiback.exception.BusinessException;
import com.example.iesiback.repositories.PagoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PagoServiceImpl implements PagoService {

    private final MercadoPagoService mercadoPagoService;
    private final UserService userService;
    private final PersonaService personaService;
    private final PagoDetalleService pagoDetalleService;

    private final ObjectMapper objectMapper; // Spring te inyecta el autoconfigurado

    private final PagoPresencialService pagoPresencialService;

    @Value("${app.front-url}")
    private String frontUrl;

    @Value("${app.back-url}")
    private String backUrl;


    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Autowired
    private PagoRepository pagoRepository;


    public PagoServiceImpl(
            MercadoPagoService mercadoPagoService,
            UserService userService,
            PersonaService personaService,
            PagoDetalleService pagoDetalleService, ObjectMapper objectMapper, PagoPresencialService pagoPresencialService
    ) {
        this.mercadoPagoService = mercadoPagoService;
        this.userService = userService;
        this.personaService = personaService;
        this.pagoDetalleService = pagoDetalleService;
        this.objectMapper = objectMapper;
        this.pagoPresencialService = pagoPresencialService;
    }

    @Override
    public Pago guardar(Pago pago) {
        userService.getAuthenticatedUser().ifPresentOrElse(
                user -> pago.setResponsable(user.getUsername()),
                () -> {
                    if (pago.getResponsable() == null) {
                        pago.setResponsable("Mercado pago"); // o "SISTEMA", lo que prefieras como marca
                    }
                }
        );

        if (pago.getDetalles() != null) {
            pago.getDetalles().forEach(detalle -> detalle.setPago(pago));
        }
        if (pago.getEstado() == null) {
            pago.setEstado(EstadoPago.PENDIENTE);
        }

        return pagoRepository.save(pago);
    }

    @Override
    public Optional<Pago> buscarPorId(Integer id) {
        return pagoRepository.findById(id);
    }

    @Override
    public List<Pago> listarTodos() {
        return pagoRepository.findAll();
    }

    @Override
    public void eliminar(Integer id) {
        pagoRepository.deleteById(id);
    }

    @Override
    public List<Pago> findByAtencionId(Integer id) {
        return pagoRepository.findAllByTramiteId(id);
    }

    @Override
    public Optional<Pago> findByOrderId(String id) {
        return pagoRepository.findByOrderId(id);
    }

    @Override
    public Map<String, String> crearPreferencia(ProductoDTO producto, Integer pagoId, Integer tramiteId) {

        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new BusinessException("Pago no encontrado: " + pagoId));

        MercadoPagoConfig.setAccessToken(accessToken);

        try {
            PreferenceItemRequest itemRequest =
                    PreferenceItemRequest.builder()
                            .id(pagoId.toString())
                            .title(producto.getNombre())
                            .description(producto.getDescripcion())
                            .pictureUrl(producto.getImagenUrl())
                            .categoryId("services")
                            .quantity(1)
                            .currencyId("ARS")
                            .unitPrice(producto.getPrecio())
                            .build();

            String successUrl = frontUrl + "/pago/resultado?pagoId=" + pagoId + "&tramiteId=" + tramiteId + "&resultado=exito";
            String pendingUrl = frontUrl + "/pago/resultado?pagoId=" + pagoId + "&tramiteId=" + tramiteId + "&resultado=pendiente";
            String failureUrl = frontUrl + "/pago/resultado?pagoId=" + pagoId + "&tramiteId=" + tramiteId + "&resultado=error";
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(successUrl)
                    .pending(pendingUrl)
                    .failure(failureUrl)
                    .build();

            PreferenceRequest preferenceRequest =
                    PreferenceRequest.builder()
                            .items(List.of(itemRequest))
                            .backUrls(backUrls)
                            .autoReturn("approved")
                            .externalReference(pagoId.toString())            // 👈 clave: id de TU tabla
                            .notificationUrl(backUrl + "/api/pagos/webhook")
                            .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // 👈 esto es lo que faltaba: persistir la referencia ANTES de redirigir
            pago.setPreferenceId(preference.getId());
            pago.setExternalReference(pagoId.toString());
            pagoRepository.save(pago);

            Map<String, String> datos = new HashMap<>();
            datos.put("preferenceId", preference.getId());
            datos.put("init_point", preference.getInitPoint());
            return datos;

        } catch (MPApiException e) {
            throw new RuntimeException("Mercado Pago API error: " + e.getApiResponse().getContent());
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la preferencia: " + e.getMessage());
        }
    }

    @Override
    public void procesarWebhook(Map<String, Object> payload) throws Exception {

        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        Long paymentId = Long.valueOf(data.get("id").toString());

        PaymentDTO payment = mercadoPagoService.consultarPagoPorId(paymentId);

        String preferenceId = payment.getPreference_id();
        String externalRef = payment.getExternal_reference();

        Pago pago = null;

        if (externalRef != null) {
            pago = pagoRepository.findById(Integer.valueOf(externalRef)).orElse(null);
        }
        if (pago == null && preferenceId != null) {
            pago = pagoRepository.findByPreferenceId(preferenceId).orElse(null);
        }

        if (pago == null) {
            // No deberia pasar si crearPreferencia se llamó bien, pero no inventamos un Pago fantasma
            System.err.println("⚠️ Webhook recibido sin Pago asociado. paymentId=" + paymentId
                    + " externalRef=" + externalRef + " preferenceId=" + preferenceId);
            return;
        }

        pago.setMpPaymentId(paymentId);
        pago.setPreferenceId(preferenceId);
        pago.setExternalReference(externalRef);
        pago.setEstado(mapearEstadoMercadoPago(payment.getStatus(), payment.getStatus_detail()));
        pago.setStatusDetail(payment.getStatus_detail());
        pago.setMetodoPago(payment.getPayment_method_id());
        pago.setTipoPago(payment.getPayment_type_id());
        pago.setMontoTotal(payment.getTransaction_amount());
        pago.setMoneda(payment.getCurrency_id());
        pago.setFechaPago(payment.getDate_approved());
        pago.setRawResponse(objectMapper.convertValue(payment, Map.class));

        if (payment.getPayer() != null) {
            PaymentDTO.Payer payer = payment.getPayer();

            String nombreCompleto = ((payer.getFirst_name() != null ? payer.getFirst_name() : "") + " "
                    + (payer.getLast_name() != null ? payer.getLast_name() : "")).trim();

            pago.setNombrePagador(nombreCompleto.isEmpty() ? null : nombreCompleto);
            pago.setDniPagador(payer.getIdentification() != null ? payer.getIdentification().getNumber() : null);
        }

        pagoRepository.save(pago);
    }

    @Override
    public void procesarWebhookPresencial(Map<String, Object> payload) throws Exception {

        String action = (String) payload.get("action");
        // 👇 antes solo processed/refunded; ahora también expired y canceled,
        // así el pago local se cancela aunque el operador cierre la pestaña
        if (!"order.processed".equals(action)
                && !"order.refunded".equals(action)
                && !"order.expired".equals(action)
                && !"order.canceled".equals(action)) {
            return;
        }

        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        String orderId = (String) data.get("id");

        // Consultamos la order fresca a MP (no confiamos en el payload del webhook)
        Map<String, Object> order = pagoPresencialService.consultarOrder(orderId);

        String externalRef = (String) order.get("external_reference");
        String status = (String) order.get("status");
        String statusDetail = (String) order.get("status_detail");

        Pago pago = null;
        if (externalRef != null) {
            try {
                pago = pagoRepository.findById(Integer.valueOf(externalRef)).orElse(null);
            } catch (NumberFormatException e) {
                // external_reference no numérico → probamos por orderId
            }
        }
        if (pago == null) {
            pago = pagoRepository.findByOrderId(orderId).orElse(null);
        }
        if (pago == null) {
            System.err.println("⚠️ Webhook presencial sin Pago asociado. orderId=" + orderId
                    + " externalRef=" + externalRef);
            return;
        }

        EstadoPago nuevoEstado = mapearEstadoMercadoPago(status, statusDetail); // 👈 el cambio clave

        // 🛡️ Nunca degradar un APROBADO: un webhook viejo o duplicado no puede
        // pisarlo con PENDIENTE/CANCELADO. Solo refund/contracargo lo cambian.
        if (pago.getEstado() == EstadoPago.APROBADO
                && nuevoEstado != EstadoPago.REEMBOLSADO
                && nuevoEstado != EstadoPago.CONTRACARGO) {
            return;
        }

        Map<String, Object> transactions = (Map<String, Object>) order.get("transactions");
        List<Map<String, Object>> payments = transactions != null
                ? (List<Map<String, Object>>) transactions.get("payments")
                : null;
        Map<String, Object> firstPayment =
                (payments != null && !payments.isEmpty()) ? payments.get(0) : null;

        pago.setOrderId(orderId);
        pago.setExternalReference(externalRef);
        pago.setEstado(nuevoEstado);
        pago.setStatusDetail(statusDetail);
        pago.setMontoTotal(order.get("total_amount") != null
                ? new BigDecimal(order.get("total_amount").toString())
                : null);
        pago.setMoneda((String) order.get("currency"));

        if (firstPayment != null) {
            pago.setMpPaymentIdStr((String) firstPayment.get("id"));
        }

        ObjectMapper mapper = new ObjectMapper();
        pago.setRawResponse(mapper.convertValue(order, Map.class));

        pagoRepository.save(pago);
    }

    private EstadoPago mapearEstadoMercadoPago(String estadoMp, String statusDetail) {

        if (estadoMp == null) {
            return EstadoPago.PENDIENTE;
        }

        return switch (estadoMp.toLowerCase()) {
            // Checkout Pro / Payments API
            case "approved" -> EstadoPago.APROBADO;
            case "rejected" -> EstadoPago.RECHAZADO;

            // Orders API v2 (QR estático / Point)
            case "processed" -> "accredited".equalsIgnoreCase(statusDetail)
                    ? EstadoPago.APROBADO
                    : EstadoPago.PENDIENTE;
            case "expired" -> EstadoPago.CANCELADO;

            // comunes a ambas APIs
            case "cancelled", "canceled" -> EstadoPago.CANCELADO;   // 👈 MP usa las dos grafías
            case "refunded" -> EstadoPago.REEMBOLSADO;
            case "charged_back" -> EstadoPago.CONTRACARGO;
            case "pending", "in_process", "created",
                 "action_required", "processing" -> EstadoPago.PENDIENTE;

            default -> {
                System.err.println("⚠️ Estado MP desconocido: " + estadoMp + " / " + statusDetail);
                yield EstadoPago.PENDIENTE;
            }
        };
    }

    @Override
    public Optional<ResumenRecaudacionDTO> ResumenRecaudacionDTO(
            LocalDate fechaPago
    ) {

        ZoneId zona = ZoneId.systemDefault();

        Instant inicio =
                fechaPago.atStartOfDay(zona).toInstant();

        Instant fin =
                fechaPago.plusDays(1)
                        .atStartOfDay(zona)
                        .toInstant();


//        List<Pago> pagos =
//                pagoRepository.findByFechaPagoBetween(
//                        inicio,
//                        fin
//                );


        User user=this.userService.getAuthenticatedUser().get();

        boolean esDirectivo = user.getRoles().stream()
                .anyMatch(r -> r.getRoleNombre().equals("ROLE_DIRECTIVO"));

        List<Pago> pagos = esDirectivo
                ? pagoRepository.findByFechaPagoBetween(inicio, fin)
                : pagoRepository.findByFechaPagoBetweenAndResponsable(inicio, fin, (user.getUsername()));



        if (pagos.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal totalRecaudado = pagos.stream()
                .map(Pago::getMontoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalOperaciones = pagos.size();

        int totalValidados = (int) pagos.stream()
                .filter(p ->
                        p.getEstado() == EstadoPago.APROBADO
                )
                .count();

        int totalPendientes = (int) pagos.stream()
                .filter(p ->
                        p.getEstado() == EstadoPago.PENDIENTE
                )
                .count();

        List<ReciboDTO> recibos = pagos.stream().map(p -> {

            ReciboDTO r = new ReciboDTO();

            r.setAporteId(p.getId().longValue());
            r.setAporteMonto(p.getMontoTotal());
            r.setConcepto(p.getTipoPago());

            if (
                    p.getTramite() != null
                            && p.getTramite().getTramiteDni() != null
            ) {

                PersonaDTO personaDTO =
                        personaService.findPersonaDTOById(
                                p.getTramite().getTramiteDni()
                        );

                r.setAlumnoApellido(
                        personaDTO.getPersonaApellido()
                );

                r.setAlumnoNombre(
                        personaDTO.getPersonaNombre()
                );

                r.setAlumnoDni(
                        personaDTO.getPersonaDni().toString()
                );
            }

            r.setPagoDetalles(
                    pagoDetalleService.obtenerPorPago(
                            p.getId()
                    )
            );

//            r.setValidado(
//                    p.getEstado() == EstadoPago.APROBADO
//            );

            r.setEstado(
                    p.getEstado()
            );



            r.setMetodo(
                    p.getMetodoPago()
            );

            return r;

        }).toList();

        Map<String, BigDecimal> agrupado = pagos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getTipoPago() != null
                                ? p.getTipoPago()
                                : "OTROS",

                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Pago::getMontoTotal,
                                BigDecimal::add
                        )
                ));

        List<ConceptoDTO> porConcepto =
                agrupado.entrySet()
                        .stream()
                        .map(e -> {

                            ConceptoDTO c =
                                    new ConceptoDTO();

                            c.setNombre(e.getKey());
                            c.setTotal(e.getValue());

                            return c;

                        }).toList();

        ResumenRecaudacionDTO resumen =
                new ResumenRecaudacionDTO();

        resumen.setTotalRecaudado(totalRecaudado);
        resumen.setTotalOperaciones(totalOperaciones);
        resumen.setTotalValidados(totalValidados);
        resumen.setTotalPendientes(totalPendientes);
        resumen.setRecibos(recibos);
        resumen.setPorConcepto(porConcepto);

        return Optional.of(resumen);
    }




    @Override
    public List<ResumenOperadorDTO> obtenerResumenPorOperador(
            LocalDate desde, LocalDate hasta, User user
    ) {
        ZoneId zona = ZoneId.of("America/Argentina/Buenos_Aires");
        Instant inicio = desde.atStartOfDay(zona).toInstant();
        Instant fin = hasta.plusDays(1)
                .atStartOfDay(zona)
                .toInstant();
        List<Pago> pagos =
                pagoRepository.findByFechaPagoBetween(
                        inicio,
                        fin
                );
        if (pagos.isEmpty()) {
            return List.of();
        }
        List<ReciboDTO> recibos = pagos.stream().map(pago -> {





            ReciboDTO dto = new ReciboDTO();
            dto.setAporteId(Long.valueOf(pago.getId()));
            dto.setAporteMonto(
                    pago.getMontoTotal() != null
                            ? pago.getMontoTotal()
                            : BigDecimal.ZERO
            );

            dto.setMetodo(pago.getMetodoPago());
            if (pago.getFechaPago() != null) {
                dto.setAporteFecha(
                        pago.getFechaPago()
                                .atZone(zona)
                                .toLocalDate()
                );
                dto.setHora(
                        pago.getFechaPago()
                                .atZone(zona)
                                .toLocalTime()
                                .toString()
                );
            }

            dto.setUsuario(
                    pago.getResponsable() != null
                            ? pago.getResponsable()
                            : "SIN_USUARIO"
            );

            dto.setEstado(
                    pago.getEstado()
            );

            if (
                    pago.getTramite() != null
                            && pago.getTramite().getTramiteDni() != null
            ) {
                try {
                    PersonaDTO personaDTO =
                            personaService.findPersonaDTOById(
                                    pago.getTramite()
                                            .getTramiteDni()
                            );
                    if (personaDTO != null) {
                        dto.setAlumnoApellido(
                                personaDTO.getPersonaApellido()
                        );
                        dto.setAlumnoNombre(
                                personaDTO.getPersonaNombre()
                        );
                        dto.setAlumnoDni(
                                personaDTO.getPersonaDni() != null
                                        ? personaDTO.getPersonaDni()
                                        .toString()
                                        : ""
                        );
                    }
                } catch (Exception e) {
                    System.out.println(
                            "❌ Error al buscar persona: "
                                    + e.getMessage()
                    );
                }


//================================================================================
                //================================================================================
                //================================================================================
//                tramiteService


                try {
                    dto.setCurso(obtenerAnioCursada(pago.getTramite().getLegajoId()));
                    dto.setCarrera(pagoRepository.findCarrera(pago.getTramite().getLegajoId()));

                    System.out.println(dto.getCurso());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


                //================================================================================
                //================================================================================
                //================================================================================



            }
            if (pago.getTramite() != null) {
                dto.setConcepto(
                        pago.getTramite().getTramiteTipo()
                );
            }
            dto.setAporteNroRecibo(
                    pago.getExternalReference()
            );
            dto.setPagoDetalles(
                    pagoDetalleService.obtenerPorPago(
                            pago.getId()
                    )
            );
            return dto;


        }).toList();
        Map<String, List<ReciboDTO>> agrupado =
                recibos.stream()
                        .collect(Collectors.groupingBy(
                                r -> r.getUsuario() != null
                                        ? r.getUsuario()
                                        : "SIN_USUARIO"
                        ));

        return agrupado.entrySet()
                .stream()
                .map(entry ->
                        new ResumenOperadorDTO(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();
    }



    public String obtenerAnioCursada(String libretaEstudiantil) throws Exception {
        // Obtener el año actual
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);

        // Obtener el año de inicio usando el repositorio
        Integer anioInicio = pagoRepository.findCurso(libretaEstudiantil);
        Legajo legajo=pagoRepository.findLegajo(libretaEstudiantil);

        if (anioInicio == null) {
            throw new Exception("No se encontró el año de inicio para la libreta: " + libretaEstudiantil);
        }

        int diferencia = anioActual - anioInicio;

        switch (diferencia) {
            case 0:
                return "1er año";
            case 1:
                return "2do año";
            case 2:
                return "3er año";
            default:
                return "Activo".equals(legajo.getLegajoEstado())
                        ? "Recursante"
                        : "Egresado/Pasivo";
        }
    }


    @Override
    public void actualizarEstadoValidacion(Long id) {

        Pago pago = pagoRepository.findById(id.intValue())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Pago no encontrado"
                        ));

        if (pago.getEstado() == EstadoPago.APROBADO) {
            throw new RuntimeException(
                    "El pago ya fue validado"
            );
        }

        pago.setEstado(EstadoPago.APROBADO);

        if (pago.getFechaPago() == null) {
            pago.setFechaPago(Instant.now());
        }

        User user = userService.getAuthenticatedUser()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no autenticado"
                        ));

        pago.setResponsable(user.getUsername());

        pagoRepository.save(pago);
    }

    @Override
    public void actualizarEstadoValidacionPorTramite(
            Integer tramiteId
    ) {

        List<Pago> pagos =
                pagoRepository.findAllByTramiteId(tramiteId);

        if (pagos.isEmpty()) {
            throw new RuntimeException(
                    "No existen pagos para el trámite"
            );
        }

        User user = userService.getAuthenticatedUser()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no autenticado"
                        ));

        for (Pago pago : pagos) {

            if (pago.getEstado() == EstadoPago.APROBADO) {
                continue;
            }

            pago.setEstado(EstadoPago.APROBADO);

            if (pago.getFechaPago() == null) {
                pago.setFechaPago(Instant.now());
            }

            pago.setResponsable(user.getUsername());
        }

        pagoRepository.saveAll(pagos);
    }

    @Override
    @Transactional // Súper importante para asegurar la consistencia en operaciones de escritura
    public void cambiarEstadoPago(Integer pagoId, EstadoPago nuevoEstado) {

        // 1. Validar autenticación PRIMERO (Usa BusinessException)
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new BusinessException("Usuario no autenticado"));

        // 2. Buscar la entidad (Usa BusinessException)
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new BusinessException("Pago no encontrado con el ID: " + pagoId));

        // 3. Validar transición usando el Enum (Usa BusinessException)
        if (!pago.getEstado().puedeTransicionarA(nuevoEstado)) {
            throw new BusinessException("Cambio de estado inválido de " + pago.getEstado() + " a " + nuevoEstado);
        }

        // 4. Aplicar cambios
        pago.setEstado(nuevoEstado);
        pago.setResponsable(user.getUsername());

        if (nuevoEstado == EstadoPago.APROBADO && pago.getFechaPago() == null) {
            pago.setFechaPago(Instant.now());
        }

        pagoRepository.save(pago);
    }

    @Override
    public List<Pago> findByFechaPagoBetween(
            Instant desde, Instant hasta
    ) {
        List<Pago> pagos = pagoRepository.findByFechaPagoBetween(
                desde,
                hasta
        );

        return pagos;
    }
}