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
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.preference.Preference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


/*
 * CAMBIOS RESPECTO DE LA VERSIÓN ANTERIOR
 *
 * 1. crearPreferencia: nueva sobrecarga con URL de retorno, vencimiento y email del pagador.
 *    La firma vieja (pagoId, tramiteId) se mantiene y delega en la nueva: lo que ya la usa sigue igual.
 *
 * 2. procesarWebhook: ELIMINADO. Lo reemplaza MercadoPagoWebhookService (consulta MP fuera de
 *    transacción → RegistroPagoMpService → FinalizacionInscripcionService). Quitarlo también de
 *    la interfaz PagoService. No se inyecta MercadoPagoWebhookService acá para evitar una
 *    dependencia circular: Finalización → TramiteService → PagoService.
 *
 * 3. mapearEstadoMercadoPago: ELIMINADO. Ahora es MercadoPagoEstados.mapear(...), compartido
 *    con el webhook de Checkout Pro.
 *
 * 4. procesarWebhookPresencial: usa el ObjectMapper inyectado (antes creaba uno nuevo, que no
 *    tiene registrado el módulo de fechas de Java 8+) y logger en lugar de System.err.
 *
 * 5. MercadoPagoService ya no se inyecta: solo lo usaba procesarWebhook.
 *
 * El resto del archivo no cambió.
 */
@Service
public class PagoServiceImpl implements PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoServiceImpl.class);

    private final UserService userService;
    private final PersonaService personaService;
    private final PagoDetalleService pagoDetalleService;

    private final PagoPresencialService pagoPresencialService;

    private final ObjectMapper objectMapper; // Spring te inyecta el autoconfigurado

    @Value("${app.front-url}")
    private String frontUrl;

    @Value("${app.back-url}")
    private String backUrl;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Autowired
    private PagoRepository pagoRepository;

    private final ApplicationEventPublisher eventos;
    public PagoServiceImpl(
            UserService userService,
            PersonaService personaService,
            PagoDetalleService pagoDetalleService,
            ObjectMapper objectMapper,
            PagoPresencialService pagoPresencialService, ApplicationEventPublisher eventos
    ) {
        this.userService = userService;
        this.personaService = personaService;
        this.pagoDetalleService = pagoDetalleService;
        this.objectMapper = objectMapper;
        this.pagoPresencialService = pagoPresencialService;
        this.eventos = eventos;
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

        Pago guardado = pagoRepository.save(pago);
        if (guardado.getEstado() == EstadoPago.APROBADO) {
            eventos.publishEvent(new PagoAprobadoEvent(guardado.getId()));
        }
        return guardado;
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

    // =====================================================================
    // PREFERENCIAS CHECKOUT PRO
    // =====================================================================

    /** Firma original: retorno a /pago/resultado, sin vencimiento. La usa /api/pagos/iniciar. */
    @Override
    public Map<String, String> crearPreferencia(ProductoDTO producto, Integer pagoId, Integer tramiteId) {
        String urlRetorno = frontUrl + "/pago/resultado?pagoId=" + pagoId + "&tramiteId=" + tramiteId;
        return crearPreferencia(producto, pagoId, urlRetorno, null, null);
    }

    /**
     * @param urlRetorno   URL del front, ya con "?" y sus parámetros; se le agrega "&resultado=..."
     * @param vence        fin de la validez de la preferencia (null = no vence)
     * @param emailPagador para precompletar el checkout (opcional)
     */
    @Override
    public Map<String, String> crearPreferencia(ProductoDTO producto,
                                                Integer pagoId,
                                                String urlRetorno,
                                                OffsetDateTime vence,
                                                String emailPagador) {

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

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(urlRetorno + "&resultado=exito")
                    .pending(urlRetorno + "&resultado=pendiente")
                    .failure(urlRetorno + "&resultado=error")
                    .build();

            PreferenceRequest.PreferenceRequestBuilder builder =
                    PreferenceRequest.builder()
                            .items(List.of(itemRequest))
                            .backUrls(backUrls)
                            .autoReturn("approved")
                            .externalReference(pagoId.toString())            // 👈 clave: id de TU tabla
                            .notificationUrl(backUrl + "/api/pagos/webhook");

            if (vence != null) {
                builder.expires(true)
                        .expirationDateTo(vence)
                        .dateOfExpiration(vence);   // también vence tickets de pago en efectivo
            }
            if (emailPagador != null && !emailPagador.isBlank()) {
                builder.payer(PreferencePayerRequest.builder().email(emailPagador).build());
            }

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(builder.build());

            // persistir la referencia ANTES de redirigir
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
    @SuppressWarnings("unchecked")
    public void procesarWebhookPresencial(Map<String, Object> payload) throws Exception {

        String action = (String) payload.get("action");
        // processed/refunded/expired/canceled: así el pago local se cancela aunque el operador cierre la pestaña
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
            log.warn("Webhook presencial sin Pago asociado. orderId={} externalRef={}", orderId, externalRef);
            return;
        }

        EstadoPago nuevoEstado = MercadoPagoEstados.mapear(status, statusDetail);

        // Nunca degradar un APROBADO: solo refund/contracargo lo cambian
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

        pago.setRawResponse(objectMapper.convertValue(order, Map.class));

        pagoRepository.save(pago);
    }

    // =====================================================================
    // RESÚMENES (sin cambios)
    // =====================================================================

    @Override
    public Optional<ResumenRecaudacionDTO> ResumenRecaudacionDTO(
            LocalDate fechaPago
    ) {

        // 1. Fijar la zona horaria explícita para evitar desfases si el servidor corre en UTC
        ZoneId zona = ZoneId.of("America/Argentina/Jujuy");

        // Inicio del día local: 00:00:00.000 (ej: 2026-10-02T03:00:00Z)
        Instant inicio = fechaPago.atStartOfDay(zona).toInstant();

        // Fin del día local: 23:59:59.999999999 (ej: 2026-10-03T02:59:59.999999999Z)
        Instant fin = fechaPago.atTime(LocalTime.MAX).atZone(zona).toInstant();

        User user = this.userService.getAuthenticatedUser()
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado"));

        String usernameClean = user.getUsername() != null ? user.getUsername().trim() : "";

        boolean esDirectivo = user.getRoles().stream()
                .anyMatch(r -> r.getRoleNombre().equals("ROLE_DIRECTIVO"));

        System.out.println("=== VARIABLES BÚSQUEDA ===");
        System.out.println("esDirectivo: " + esDirectivo);
        System.out.println("inicio (UTC): " + inicio);
        System.out.println("fin (UTC): " + fin);
        System.out.println("username: '" + usernameClean + "'");
        System.out.println("==========================");

        List<Pago> pagos = esDirectivo
                ? pagoRepository.findByFechaPagoBetween(inicio, fin)
                : pagoRepository.findByFechaPagoBetweenAndResponsable(inicio, fin, usernameClean);

        System.out.println("Resultados obtenidos (pagos.size): " + pagos.size());

        // --- IMPRESIÓN DETALLADA DE PAGOS RECUPERADOS ---
        if (!pagos.isEmpty()) {
            System.out.println("--- DETALLE DE PAGOS RECUPERADOS ---");
            pagos.forEach(p -> {
                System.out.printf("ID: %d | FechaPago: %s | Monto: %s | Responsable: '%s' | Estado: %s%n",
                        p.getId(),
                        p.getFechaPago(),
                        p.getMontoTotal(),
                        p.getResponsable(),
                        p.getEstado()
                );
            });
            System.out.println("------------------------------------");
        } else {
            System.out.println("No se encontraron pagos en el rango de fechas para la consulta.");
        }

        if (pagos.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal totalRecaudado = pagos.stream()
                .map(Pago::getMontoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalOperaciones = pagos.size();

        int totalValidados = (int) pagos.stream()
                .filter(p -> p.getEstado() == EstadoPago.APROBADO)
                .count();

        int totalPendientes = (int) pagos.stream()
                .filter(p -> p.getEstado() == EstadoPago.PENDIENTE)
                .count();

        List<ReciboDTO> recibos = pagos.stream().map(p -> {
            ReciboDTO r = new ReciboDTO();

            r.setAporteId(p.getId().longValue());
            r.setAporteMonto(p.getMontoTotal());
            r.setConcepto(p.getTipoPago());

            if (p.getTramite() != null && p.getTramite().getTramiteDni() != null) {
                PersonaDTO personaDTO = personaService.findPersonaDTOById(
                        p.getTramite().getTramiteDni()
                );

                r.setAlumnoApellido(personaDTO.getPersonaApellido());
                r.setAlumnoNombre(personaDTO.getPersonaNombre());
                r.setAlumnoDni(personaDTO.getPersonaDni().toString());
            }

            r.setPagoDetalles(pagoDetalleService.obtenerPorPago(p.getId()));
            r.setEstado(p.getEstado());
            r.setMetodo(p.getMetodoPago());

            if (p.getTramite() != null) {
                r.setTramiteId(p.getTramite().getId());
            }

            return r;
        }).toList();

        Map<String, BigDecimal> agrupado = pagos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getTipoPago() != null ? p.getTipoPago() : "OTROS",
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Pago::getMontoTotal,
                                BigDecimal::add
                        )
                ));

        List<ConceptoDTO> porConcepto = agrupado.entrySet().stream()
                .map(e -> {
                    ConceptoDTO c = new ConceptoDTO();
                    c.setNombre(e.getKey());
                    c.setTotal(e.getValue());
                    return c;
                }).toList();

        ResumenRecaudacionDTO resumen = new ResumenRecaudacionDTO();
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
            dto.setAporteMonto(pago.getMontoTotal() != null ? pago.getMontoTotal() : BigDecimal.ZERO);
            dto.setMetodo(convertirMetodoPago(pago.getMetodoPago()));
            if (pago.getFechaPago() != null) {
                dto.setAporteFecha(pago.getFechaPago().atZone(zona).toLocalDate());
                dto.setHora(pago.getFechaPago().atZone(zona).toLocalTime().toString());
            }
            dto.setUsuario(
                    pago.getResponsable() != null
                            ? (pago.getResponsable().matches("\\d+")
                               ? userService.findById(Long.valueOf(pago.getResponsable()))
                            .map(u -> u.getUserApellido() + " " + u.getUserNombre())
                            .orElse("SIN_USUARIO")
                               : pago.getResponsable()) // ya viene como "Alumno" u otro texto
                            : "SIN_USUARIO"
            );
            dto.setEstado(pago.getEstado());
            if (pago.getTramite() != null) {
                dto.setConcepto(pago.getTramite().getTramiteTipo());
                if (pago.getTramite().getTramiteDni() != null) {
                    try {
                        PersonaDTO personaDTO = personaService.findPersonaDTOById(pago.getTramite().getTramiteDni());
                        if (personaDTO != null) {
                            dto.setAlumnoApellido(personaDTO.getPersonaApellido());
                            dto.setAlumnoNombre(personaDTO.getPersonaNombre());
                            dto.setAlumnoDni(personaDTO.getPersonaDni() != null ? personaDTO.getPersonaDni().toString() : "");
                        }
                    } catch (Exception e) {
                        System.out.println("Error al buscar persona: " + e.getMessage());
                    }
                    try {
                        dto.setCurso(obtenerAnioCursada(pago.getTramite().getLegajoId()));
                        dto.setCarrera(pagoRepository.findCarrera(pago.getTramite().getLegajoId()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            dto.setAporteNroRecibo(pago.getExternalReference());
            dto.setPagoDetalles(pagoDetalleService.obtenerPorPago(pago.getId()));
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

    private String convertirMetodoPago(String metodoPago) {
        if (metodoPago == null) {
            return "-";
        }

        return switch (metodoPago.toLowerCase()) {
            case "efectivo" -> "EFECTIVO";
            case "transferencia" -> "TRANSFERENCIA";
            case "mercadopago_qr" -> "QR";
            case "mercadopago_point" -> "POSNET";
            case "account_money",
                 "debmaster",
                 "debvisa",
                 "naranja",
                 "pagofacil",
                 "rapipago",
                 "visa" -> "LINK";
            default -> metodoPago.toUpperCase();
        };
    }

    public String obtenerAnioCursada(String libretaEstudiantil) throws Exception {
        // Obtener el año actual
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);

        // Obtener el año de inicio usando el repositorio
        Integer anioInicio = pagoRepository.findCurso(libretaEstudiantil);
        Legajo legajo = pagoRepository.findLegajo(libretaEstudiantil);

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

    // =====================================================================
    // VALIDACIÓN Y CAMBIO DE ESTADO MANUAL (sin cambios; protegidos en SecurityConfig)
    // =====================================================================

    @Override
    public void actualizarEstadoValidacion(Long id) {

        Pago pago = pagoRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        if (pago.getEstado() == EstadoPago.APROBADO) {
            throw new RuntimeException("El pago ya fue validado");
        }

        pago.setEstado(EstadoPago.APROBADO);

        if (pago.getFechaPago() == null) {
            pago.setFechaPago(Instant.now());
        }

        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        pago.setResponsable(user.getUsername());

        pagoRepository.save(pago);
    }

    @Override
    public void actualizarEstadoValidacionPorTramite(Integer tramiteId) {

        List<Pago> pagos = pagoRepository.findAllByTramiteId(tramiteId);

        if (pagos.isEmpty()) {
            throw new RuntimeException("No existen pagos para el trámite");
        }

        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

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

    @Transactional
    @Override
    public void cambiarEstadoPago(Integer pagoId, EstadoPago nuevoEstado) {

        // 1. Validar autenticación PRIMERO
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new BusinessException("Usuario no autenticado"));

        // 2. Buscar la entidad
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new BusinessException("Pago no encontrado con el ID: " + pagoId));

        // 3. Validar transición usando el Enum
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
    public List<Pago> findByFechaPagoBetween(Instant desde, Instant hasta) {
        return pagoRepository.findByFechaPagoBetween(desde, hasta);
    }
}