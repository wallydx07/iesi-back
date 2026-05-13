package com.example.iesiback.services;

import com.example.iesiback.dto.*;
import com.example.iesiback.enums.EstadoPago;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.entities.User;
import com.example.iesiback.repositories.PagoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PagoServiceImpl implements PagoService {

    private final MercadoPagoService mercadoPagoService;
    private final TramiteService tramiteService;
    private final UserService userService;
    private final PersonaService personaService;
    private final PagoDetalleService pagoDetalleService;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Autowired
    private PagoRepository pagoRepository;

    public PagoServiceImpl(
            MercadoPagoService mercadoPagoService,
            TramiteService tramiteService,
            UserService userService,
            PersonaService personaService,
            PagoDetalleService pagoDetalleService
    ) {
        this.mercadoPagoService = mercadoPagoService;
        this.tramiteService = tramiteService;
        this.userService = userService;
        this.personaService = personaService;
        this.pagoDetalleService = pagoDetalleService;
    }

    @Override
    public Pago guardar(Pago pago) {

        User user = userService.getAuthenticatedUser()
                .orElseThrow(() ->
                        new RuntimeException("Usuario no autenticado"));

        pago.setResponsable(user.getUsername());

        if (pago.getDetalles() != null) {
            pago.getDetalles().forEach(detalle -> {
                detalle.setPago(pago);
            });
        }

        // Estado por defecto
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
    public Map<String, String> crearPreferencia(ProductoDTO producto) {

        MercadoPagoConfig.setAccessToken(accessToken);

        try {

            PreferenceItemRequest itemRequest =
                    PreferenceItemRequest.builder()
                            .id("1234")
                            .title(producto.getNombre())
                            .description(producto.getDescripcion())
                            .pictureUrl(producto.getImagenUrl())
                            .categoryId("services")
                            .quantity(1)
                            .currencyId("ARS")
                            .unitPrice(producto.getPrecio())
                            .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            PreferenceRequest preferenceRequest =
                    PreferenceRequest.builder()
                            .items(items)
                            .build();

            PreferenceClient client = new PreferenceClient();

            Preference preference =
                    client.create(preferenceRequest);

            Map<String, String> datos = new HashMap<>();

            datos.put("preferenceId", preference.getId());
            datos.put("init_point", preference.getInitPoint());

            return datos;

        } catch (MPApiException e) {

            System.err.println(
                    "⚠️ API ERROR: "
                            + e.getApiResponse().getContent()
            );

            throw new RuntimeException(
                    "Mercado Pago API error: "
                            + e.getApiResponse().getContent()
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Error al crear la preferencia: "
                            + e.getMessage()
            );
        }
    }

    @Override
    public void procesarWebhook(Map<String, Object> payload)
            throws Exception {

        Map<String, Object> data =
                (Map<String, Object>) payload.get("data");

        Long paymentId =
                Long.valueOf(data.get("id").toString());

        PaymentDTO payment =
                mercadoPagoService.consultarPagoPorId(paymentId);

        String preferenceId = payment.getPreference_id();
        String externalRef = payment.getExternal_reference();

        Pago pago = pagoRepository.findByPreferenceId(preferenceId)
                .orElse(new Pago());

        pago.setMpPaymentId(paymentId);
        pago.setPreferenceId(preferenceId);
        pago.setExternalReference(externalRef);

        // MAPEO DE ESTADOS MP → ENUM
        pago.setEstado(mapearEstadoMercadoPago(
                payment.getStatus()
        ));

        pago.setStatusDetail(payment.getStatus_detail());
        pago.setMetodoPago(payment.getPayment_method_id());
        pago.setTipoPago(payment.getPayment_type_id());
        pago.setMontoTotal(payment.getTransaction_amount());
        pago.setMoneda(payment.getCurrency_id());
        pago.setFechaPago(payment.getDate_approved());

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> rawMap =
                mapper.convertValue(payment, Map.class);

        pago.setRawResponse(rawMap);

        pagoRepository.save(pago);
    }

    private EstadoPago mapearEstadoMercadoPago(String estadoMp) {

        if (estadoMp == null) {
            return EstadoPago.PENDIENTE;
        }

        return switch (estadoMp.toLowerCase()) {

            case "approved" -> EstadoPago.APROBADO;

            case "rejected" -> EstadoPago.RECHAZADO;

            case "cancelled" -> EstadoPago.CANCELADO;

            default -> EstadoPago.PENDIENTE;
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

        List<Pago> pagos =
                pagoRepository.findByFechaPagoBetween(
                        inicio,
                        fin
                );

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

            r.setValidado(
                    p.getEstado() == EstadoPago.APROBADO
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
            LocalDate fechaPago
    ) {

        ZoneId zona =
                ZoneId.of("America/Argentina/Buenos_Aires");

        Instant inicio =
                fechaPago.atStartOfDay(zona).toInstant();

        Instant fin =
                fechaPago.plusDays(1)
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

            dto.setValidado(
                    pago.getEstado() == EstadoPago.APROBADO
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
    public void cambiarEstadoPago(
            Integer pagoId,
            EstadoPago nuevoEstado
    ) {

        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Pago no encontrado"
                        ));

        EstadoPago estadoActual = pago.getEstado();

        // =========================================
        // VALIDACIONES DE TRANSICIÓN
        // =========================================

        switch (estadoActual) {

            case PENDIENTE -> {

                if (
                        nuevoEstado != EstadoPago.APROBADO
                                && nuevoEstado != EstadoPago.RECHAZADO
                                && nuevoEstado != EstadoPago.CANCELADO
                ) {

                    throw new RuntimeException(
                            "Cambio de estado inválido"
                    );
                }
            }

            case APROBADO -> {

                if (nuevoEstado != EstadoPago.CANCELADO) {

                    throw new RuntimeException(
                            "Un pago aprobado solo puede cancelarse"
                    );
                }
            }

            case RECHAZADO -> {

                if (nuevoEstado != EstadoPago.PENDIENTE) {

                    throw new RuntimeException(
                            "Un pago rechazado solo puede volver a pendiente"
                    );
                }
            }

            case CANCELADO -> {

                throw new RuntimeException(
                        "Un pago cancelado no puede modificarse"
                );
            }
        }

        // =========================================
        // ACTUALIZAR ESTADO
        // =========================================

        pago.setEstado(nuevoEstado);

        // =========================================
        // FECHA APROBACIÓN
        // =========================================

        if (
                nuevoEstado == EstadoPago.APROBADO
                        && pago.getFechaPago() == null
        ) {

            pago.setFechaPago(Instant.now());
        }

        // =========================================
        // RESPONSABLE
        // =========================================

        User user = userService.getAuthenticatedUser()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no autenticado"
                        ));

        pago.setResponsable(user.getUsername());

        pagoRepository.save(pago);
    }
}