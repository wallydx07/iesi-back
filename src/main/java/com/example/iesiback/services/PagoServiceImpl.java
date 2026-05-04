package com.example.iesiback.services;

import com.example.iesiback.dto.*;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.entities.User;
import com.example.iesiback.repositories.PagoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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




//    @Override
//    public Pago guardar(Pago pago) {
//        User user = userService.getAuthenticatedUser().get();
//        pago.setResponsable(user.getUsername());
//        return pagoRepository.save(pago);
//    }

    @Override
    public Pago guardar(Pago pago) {
        User user = userService.getAuthenticatedUser().get();
        pago.setResponsable(user.getUsername());

        if (pago.getDetalles() != null) {
            pago.getDetalles().forEach(detalle -> {
                detalle.setPago(pago); // 🔥 CLAVE
            });
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
    public Optional<Pago> findByAtencionId(Integer id) {
        return pagoRepository.findByTramiteId(id);
    }


    public PagoServiceImpl(MercadoPagoService mercadoPagoService, TramiteService tramiteService, UserService userService, PersonaService personaService, PagoDetalleService pagoDetalleService) {
        this.mercadoPagoService = mercadoPagoService;
        this.tramiteService = tramiteService;
        this.userService = userService;
        this.personaService = personaService;
        this.pagoDetalleService = pagoDetalleService;
    }

    @Override
    public Map<String, String> crearPreferencia(ProductoDTO producto) {
        MercadoPagoConfig.setAccessToken(accessToken);

        try {
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id("1234")
                    .title(producto.getNombre())
                    .description(producto.getDescripcion())
                    .pictureUrl(producto.getImagenUrl())
                    .categoryId("games")
                    .quantity(1)
                    .currencyId("ARS")
                    .unitPrice(producto.getPrecio())
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            Map<String, String> datos = new HashMap<>();
            datos.put("preferenceId", preference.getId());
            datos.put("init_point", preference.getInitPoint()); // ✅ agregar esto
            return datos;

        } catch (MPApiException e) {
            System.err.println("⚠️ API ERROR: " + e.getApiResponse().getContent());
            throw new RuntimeException("Mercado Pago API error: " + e.getApiResponse().getContent());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear la preferencia: " + e.getMessage());
        }
    }


    @Override
    public void procesarWebhook(Map<String, Object> payload) throws Exception {
        String topic = (String) payload.get("type");
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        Long paymentId = Long.valueOf(data.get("id").toString());

        // Consultar datos reales del pago desde Mercado Pago
        PaymentDTO payment = mercadoPagoService.consultarPagoPorId(paymentId);

        String preferenceId = payment.getPreference_id();
        String externalRef = payment.getExternal_reference();

        // Buscar o crear entidad Pago
        Pago pago = pagoRepository.findByPreferenceId(preferenceId)
                .orElse(new Pago());
        pago.setMpPaymentId(paymentId);
        pago.setPreferenceId(preferenceId);
        pago.setExternalReference(externalRef);
        pago.setEstado(payment.getStatus());
        pago.setStatusDetail(payment.getStatus_detail());
        pago.setMetodoPago(payment.getPayment_method_id());
        pago.setTipoPago(payment.getPayment_type_id());
        pago.setMontoTotal(payment.getTransaction_amount());
        pago.setMoneda(payment.getCurrency_id());
        pago.setFechaPago(payment.getDate_approved());
        // Convertimos el DTO a Map para guardar en rawResponse (opcional, pero útil para debug)
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> rawMap = mapper.convertValue(payment, Map.class);
        pago.setRawResponse(rawMap);
        pagoRepository.save(pago);
    }

    @Override
    public Optional<ResumenRecaudacionDTO> ResumenRecaudacionDTO(LocalDate fechaPago) {
        ZoneId zona = ZoneId.systemDefault();
        Instant inicio = fechaPago.atStartOfDay(zona).toInstant();
        Instant fin = fechaPago.plusDays(1).atStartOfDay(zona).toInstant();
        List<Pago> pagos = pagoRepository.findByFechaPagoBetween(inicio, fin);

        if (pagos.isEmpty()) {
            return Optional.empty();
        }

        // 💰 total recaudado
        BigDecimal totalRecaudado = pagos.stream()
                .map(Pago::getMontoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 🔢 total operaciones
        int totalOperaciones = pagos.size();

        // ✅ validados (ej: estado = APROBADO)
        int totalValidados = (int) pagos.stream()
                .filter(p -> "APROBADO".equalsIgnoreCase(p.getEstado()))
                .count();

        // ⏳ pendientes
        int totalPendientes = (int) pagos.stream()
                .filter(p -> "PENDIENTE".equalsIgnoreCase(p.getEstado()))
                .count();

        // 🧾 recibos
        List<ReciboDTO> recibos = pagos.stream().map(p -> {
            ReciboDTO r = new ReciboDTO();

            r.setAporteId(p.getId().longValue());
            r.setAporteMonto(p.getMontoTotal());
            r.setConcepto(p.getTipoPago()); // o metodoPago

            // ⚠️ depende de tu modelo

            if (p.getTramite() != null && p.getTramite().getTramiteDni() != null) {
                PersonaDTO personaDTO=  personaService.findPersonaDTOById(p.getTramite().getTramiteDni());

                r.setAlumnoApellido(personaDTO.getPersonaApellido());
                r.setAlumnoNombre(personaDTO.getPersonaNombre());
                r.setAlumnoDni(personaDTO.getPersonaDni().toString());
            }
            r.setPagoDetalles(pagoDetalleService.obtenerPorPago(p.getId()));
            r.setValidado("APROBADO".equalsIgnoreCase(p.getEstado()));

            return r;
        }).toList();

        // 📊 agrupado por concepto
        Map<String, BigDecimal> agrupado = pagos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getTipoPago() != null ? p.getTipoPago() : "OTROS",
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Pago::getMontoTotal,
                                BigDecimal::add
                        )
                ));

        List<ConceptoDTO> porConcepto = agrupado.entrySet().stream().map(e -> {
            ConceptoDTO c = new ConceptoDTO();
            c.setNombre(e.getKey());
            c.setTotal(e.getValue());
            return c;
        }).toList();

        // 📦 armar DTO
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
    public List<ResumenOperadorDTO> obtenerResumenPorOperador(LocalDate fechaPago) {

        System.out.println("======================================");
        System.out.println("📥 INICIO obtenerResumenPorOperador");
        System.out.println("📅 Fecha recibida: " + fechaPago);

        // 🔥 Forzamos zona correcta (evita bugs con Instant)
        ZoneId zona = ZoneId.of("America/Argentina/Buenos_Aires");

        Instant inicio = fechaPago.atStartOfDay(zona).toInstant();
        Instant fin = fechaPago.plusDays(1).atStartOfDay(zona).toInstant();

        System.out.println("⏱ Rango búsqueda:");
        System.out.println("➡️ Inicio: " + inicio);
        System.out.println("➡️ Fin: " + fin);

        // 🔎 QUERY
        List<Pago> pagos = pagoRepository.findByFechaPagoBetween(inicio, fin);

        System.out.println("📦 Pagos encontrados: " + pagos.size());

        if (pagos.isEmpty()) {
            System.out.println("⚠️ No hay pagos en ese rango");
            System.out.println("======================================");
            return List.of();
        }

        // 🔍 Log de pagos
        pagos.forEach(p -> {
            System.out.println("💰 Pago ID: " + p.getId()
                    + " | Fecha: " + p.getFechaPago()
                    + " | Responsable: " + p.getResponsable()
                    + " | Estado: " + p.getEstado());
        });

        // 1️⃣ Convertir Pago → ReciboDTO
        List<ReciboDTO> recibos = pagos.stream().map(pago -> {

            System.out.println("➡️ Procesando pago ID: " + pago.getId());

            ReciboDTO dto = new ReciboDTO();

            dto.setAporteId(Long.valueOf(pago.getId()));
            dto.setAporteMonto(pago.getMontoTotal() != null ? pago.getMontoTotal() : BigDecimal.ZERO);
            dto.setMetodo(pago.getMetodoPago());

            // 🕒 Fecha
            if (pago.getFechaPago() != null) {
                dto.setAporteFecha(pago.getFechaPago().atZone(zona).toLocalDate());
                dto.setHora(pago.getFechaPago().atZone(zona).toLocalTime().toString());
            } else {
                System.out.println("⚠️ Pago sin fecha: " + pago.getId());
            }

            dto.setUsuario(pago.getResponsable() != null ? pago.getResponsable() : "SIN_USUARIO");

            // ✅ Estado
            dto.setValidado("APROBADO".equalsIgnoreCase(pago.getEstado()));

            // 👤 Alumno
            if (pago.getTramite() != null && pago.getTramite().getTramiteDni() != null) {
                try {
                    PersonaDTO personaDTO = personaService
                            .findPersonaDTOById(pago.getTramite().getTramiteDni());

                    if (personaDTO != null) {
                        dto.setAlumnoApellido(personaDTO.getPersonaApellido());
                        dto.setAlumnoNombre(personaDTO.getPersonaNombre());
                        dto.setAlumnoDni(
                                personaDTO.getPersonaDni() != null
                                        ? personaDTO.getPersonaDni().toString()
                                        : ""
                        );
                    } else {
                        System.out.println("⚠️ Persona no encontrada para trámite");
                    }

                } catch (Exception e) {
                    System.out.println("❌ Error al buscar persona: " + e.getMessage());
                }
            }

            // 📄 Concepto
            if (pago.getTramite() != null) {
                dto.setConcepto(pago.getTramite().getTramiteTipo());
            }

            dto.setAporteNroRecibo(pago.getExternalReference());

            return dto;

        }).toList();

        System.out.println("📊 Recibos generados: " + recibos.size());

        // 2️⃣ Agrupar por usuario
        Map<String, List<ReciboDTO>> agrupado = recibos.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getUsuario() != null ? r.getUsuario() : "SIN_USUARIO"
                ));

        System.out.println("👥 Usuarios agrupados:");
        agrupado.forEach((user, list) ->
                System.out.println("➡️ " + user + " → " + list.size() + " recibos")
        );

        // 3️⃣ Construir respuesta
        List<ResumenOperadorDTO> resultado = agrupado.entrySet().stream()
                .map(entry -> new ResumenOperadorDTO(
                        entry.getKey(),
                        entry.getValue()
                ))
                .toList();

        System.out.println("✅ Resultado final: " + resultado.size() + " operadores");
        System.out.println("======================================");

        return resultado;
    }



    @Override
    public void actualizarEstadoValidacion(Long id) {

    }

}
