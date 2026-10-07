package com.example.iesiback.services;

import com.example.iesiback.dto.DatosPreinscripcion;
import com.example.iesiback.dto.PreinscripcionDtos.*;
import com.example.iesiback.dto.ProductoDTO;
import com.example.iesiback.entities.*;
import com.example.iesiback.enums.EstadoPago;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.*;
import java.util.*;

/**
 * Flujo público de preinscripción a cursos de Capacitación.
 *
 * Nunca crea ni modifica Persona, Legajo, Inscripcion o Cursada:
 * eso lo hace únicamente FinalizacionInscripcionService después de un pago aprobado.
 *
 * El identificador público del proceso es el código de seguimiento (16 caracteres
 * aleatorios con SecureRandom). El id interno del trámite nunca se expone.
 */
@Service
public class PreinscripcionPublicaService {

    public static final String TIPO_TRAMITE = "Inscripción Capacitación";
    public static final String ESTADO_TRAMITE_INICIAL = "Pendiente";

    /** Ajustar al área que usa la bandeja de trámites (las pestañas se generan por tramiteArea). */
    private static final String AREA_TRAMITE = "Alumnado";
    private static final String CANAL_TRAMITE = "Web";

    private static final ZoneId ZONA = ZoneId.of("America/Argentina/Jujuy");
    private static final String ALFABETO_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // sin 0/O/1/I
    private static final int LARGO_CODIGO = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final TramiteService tramiteService;
    private final PagoService pagoService;
    private final MateriaCarreraService materiaCarreraService;
    private final CursadaService cursadaService;

    @Value("${app.preinscripcion.carrera-id}")
    private String carreraCapacitacionId;

    @Value("${app.front-url}")
    private String frontUrl;

    public PreinscripcionPublicaService(TramiteService tramiteService, PagoService pagoService, MateriaCarreraService materiaCarreraService, CursadaService cursadaService) {
        this.tramiteService = tramiteService;
        this.pagoService = pagoService;
        this.materiaCarreraService = materiaCarreraService;
        this.cursadaService = cursadaService;
    }

    // =====================================================================
    // OFERTAS DISPONIBLES
    // =====================================================================

    @Transactional(readOnly = true)
    public List<OfertaPublicaDTO> listarOfertas() {
        return materiaCarreraService.findOfertasAbiertas(carreraCapacitacionId, hoy()).stream()
                .filter(this::tienePrecioValido)
                .map(this::toOferta)
                .toList();
    }

    // =====================================================================
    // CREAR PREINSCRIPCIÓN (solo Tramite: nada académico, nada de Persona)
    // =====================================================================

    @Transactional
    public PreinscripcionCreadaResponse crear(PreinscripcionPublicaRequest req) {
        Long dni = Long.valueOf(req.dni());
        MateriaCarrera oferta = ofertaAbierta(req.ofertaId());

        if (cursadaService.existsByLegajo_LegajoPersonaDni_PersonaDniAndMateriaCarrera_Id(dni, oferta.getId())) {
            throw error(HttpStatus.CONFLICT, "Ya existe una inscripción para este DNI en el curso seleccionado.");
        }

        DatosPreinscripcion datos = new DatosPreinscripcion(
                normalizar(req.apellido()),
                normalizar(req.nombre()),
                req.correo().trim().toLowerCase(Locale.ROOT),
                req.celular().trim()
        );

        Tramite tramite = new Tramite();
        tramite.setTramiteTipo(TIPO_TRAMITE);
        tramite.setTramiteEstado(ESTADO_TRAMITE_INICIAL);
        tramite.setTramiteArea(AREA_TRAMITE);
        tramite.setTramiteCanal(CANAL_TRAMITE);
        tramite.setTramiteFecha(LocalDateTime.now(ZONA));
        tramite.setTramiteDni(dni);
        tramite.setTramiteApellidoNombre(truncar(datos.apellido() + ", " + datos.nombre(), 100));
        tramite.setTramiteCorreo(datos.correo());
        tramite.setTramiteCelular(celularComoLong(datos.celular()));
        tramite.setTramiteAsunto("Preinscripción online: " + oferta.getMateria().getMateriaNombre());
        tramite.setDatosPreinscripcion(datos);
        tramite.setOfertaMateriaCarreraId(Long.valueOf(oferta.getId()));
        tramite.setCodigoSeguimiento(generarCodigo()); // TramiteServiceImpl.save lo respeta si ya viene cargado

        Tramite guardado = tramiteService.save(tramite);
        return new PreinscripcionCreadaResponse(guardado.getCodigoSeguimiento(), guardado.getTramiteEstado());
    }

    // =====================================================================
    // INICIAR PAGO: el monto lo decide el backend (oferta → concepto → precioMp)
    // =====================================================================

    @Transactional
    public IniciarPagoResponse iniciarPago(String codigo) {
        // Bloquea el trámite: dos clics simultáneos en "Pagar" no generan dos pagos
        Tramite tramite = tramiteService.lockByCodigoSeguimiento(codigo)
                .filter(t -> TIPO_TRAMITE.equals(t.getTramiteTipo()))
                .orElseThrow(this::noEncontrado);

        if (FinalizacionInscripcionService.estaFinalizado(tramite)
                || !ESTADO_TRAMITE_INICIAL.equals(tramite.getTramiteEstado())) {
            throw error(HttpStatus.CONFLICT, "Este trámite ya no admite pagos.");
        }

        MateriaCarrera oferta = ofertaAbierta((tramite.getOfertaMateriaCarreraId().intValue()));

        if (cursadaService.existsByLegajo_LegajoPersonaDni_PersonaDniAndMateriaCarrera_Id(
                tramite.getTramiteDni(), oferta.getId())) {
            throw error(HttpStatus.CONFLICT, "Ya existe una inscripción para este DNI en el curso seleccionado.");
        }

        List<Pago> pagos = pagoService.findByAtencionId(tramite.getId());
        if (pagos.stream().anyMatch(p -> p.getEstado() == EstadoPago.APROBADO)) {
            throw error(HttpStatus.CONFLICT, "El trámite ya tiene un pago aprobado.");
        }
        if (pagos.stream().anyMatch(this::pagoEnProceso)) {
            throw error(HttpStatus.CONFLICT,
                    "Hay un pago en proceso de acreditación. Esperá su confirmación antes de intentar de nuevo.");
        }

        // Intentos anteriores que nunca llegaron a pagarse: quedan dados de baja.
        // Si alguien igual paga esa preferencia vieja, el webhook acepta CANCELADO → APROBADO.
        pagos.stream()
                .filter(p -> p.getEstado() == EstadoPago.PENDIENTE && p.getMpPaymentId() == null)
                .forEach(p -> {
                    p.setEstado(EstadoPago.CANCELADO);
                    p.setStatusDetail("reemplazado_por_nuevo_intento");
                });

        ConstanciaPrecio concepto = oferta.getConstanciaPrecio();
        BigDecimal monto = concepto.getPrecioMp();

        Pago pago = new Pago();
        pago.setTramite(tramite);
        pago.setEstado(EstadoPago.PENDIENTE);
        pago.setMontoTotal(monto);
        pago.setMoneda("ARS");
        pago.setMetodoPago("checkout_pro");
        pago.setTipoPago(concepto.getNombre());
        pago.setResponsable("WEB");
        pago.setDniPagador(String.valueOf(tramite.getTramiteDni()));
        pago.setNombrePagador(tramite.getTramiteApellidoNombre());

        PagoDetalle detalle = new PagoDetalle();
        detalle.setPago(pago);
        detalle.setConcepto(concepto.getNombre());
        detalle.setMonto(monto);
        detalle.setCantidad(1);
        pago.setDetalles(new ArrayList<>(List.of(detalle)));

        pagoService.guardar(pago);

        ProductoDTO producto = new ProductoDTO();
        producto.setNombre(concepto.getNombre());
        producto.setDescripcion(oferta.getMateria().getMateriaNombre() + " - Trámite " + codigo);
        producto.setPrecio(monto);

        // La preferencia vence con la inscripción: MP no acepta pagos después de la fecha límite
        OffsetDateTime vence = oferta.getFechaLimite().atTime(LocalTime.of(23, 59, 59))
                .atZone(ZONA).toOffsetDateTime();
        String urlRetorno = frontUrl + "/preinscripcion/resultado?codigo=" + codigo;

        // Si MP falla, la excepción revierte toda la transacción: no queda un Pago huérfano
        Map<String, String> datos = pagoService.crearPreferencia(
                producto, pago.getId(), urlRetorno, vence, tramite.getTramiteCorreo());

        return new IniciarPagoResponse(datos.get("preferenceId"), datos.get("init_point"));
    }

    // =====================================================================
    // ESTADO PÚBLICO (sin datos personales)
    // =====================================================================

    @Transactional(readOnly = true)
    public PreinscripcionEstadoResponse estado(String codigo) {
        Tramite tramite = tramiteService.findByCodigoSeguimiento(codigo)
                .filter(t -> TIPO_TRAMITE.equals(t.getTramiteTipo()))
                .orElseThrow(this::noEncontrado);

        List<Pago> pagos = pagoService.findAllByTramiteId(tramite.getId());
        boolean finalizada = FinalizacionInscripcionService.estaFinalizado(tramite);

        Optional<MateriaCarrera> oferta = Optional.ofNullable(tramite.getOfertaMateriaCarreraId())
                .flatMap(materiaCarreraService::findOfertaById);

        boolean puedeReintentar = !finalizada
                && ESTADO_TRAMITE_INICIAL.equals(tramite.getTramiteEstado())
                && pagos.stream().noneMatch(p -> p.getEstado() == EstadoPago.APROBADO)
                && pagos.stream().noneMatch(this::pagoEnProceso)
                && oferta.map(this::estaAbierta).orElse(false);

        return new PreinscripcionEstadoResponse(
                tramite.getCodigoSeguimiento(),
                estadoPagoRelevante(pagos),
                finalizada,
                puedeReintentar,
                oferta.map(o -> o.getMateria().getMateriaNombre()).orElse(null),
                oferta.map(MateriaCarrera::getFechaInicio).orElse(null)
        );
    }

    // =====================================================================
    // Auxiliares
    // =====================================================================

    private MateriaCarrera ofertaAbierta(Integer ofertaId) {
        MateriaCarrera oferta = Optional.ofNullable(ofertaId)
                .flatMap(materiaCarreraService::findOfertaById)
                .orElseThrow(() -> error(HttpStatus.UNPROCESSABLE_ENTITY, "El curso seleccionado no existe."));
        if (!estaAbierta(oferta)) {
            throw error(HttpStatus.UNPROCESSABLE_ENTITY, "El curso seleccionado no está disponible para inscripción.");
        }
        return oferta;
    }

    private boolean estaAbierta(MateriaCarrera oferta) {
        return oferta.getCarrera() != null
                && carreraCapacitacionId.equals(oferta.getCarrera().getCarreraId())
                && oferta.getFechaLimite() != null
                && !hoy().isAfter(oferta.getFechaLimite())
                && tienePrecioValido(oferta);
    }

    private boolean tienePrecioValido(MateriaCarrera oferta) {
        ConstanciaPrecio c = oferta.getConstanciaPrecio();
        // precio_mp tiene DEFAULT 0 en la base: 0 significa "sin precio cargado"
        return c != null && c.getPrecioMp() != null && c.getPrecioMp().signum() > 0;
    }

    /** Un PENDIENTE con payment asociado es un pago real en curso (ej.: ticket de Rapipago sin pagar aún). */
    private boolean pagoEnProceso(Pago p) {
        return p.getEstado() == EstadoPago.PENDIENTE && p.getMpPaymentId() != null;
    }

    private String estadoPagoRelevante(List<Pago> pagos) {
        if (pagos.isEmpty()) {
            return "SIN_PAGO";
        }
        for (EstadoPago e : List.of(EstadoPago.APROBADO, EstadoPago.CONTRACARGO, EstadoPago.REEMBOLSADO)) {
            if (pagos.stream().anyMatch(p -> p.getEstado() == e)) {
                return e.name();
            }
        }
        if (pagos.stream().anyMatch(this::pagoEnProceso)) {
            return EstadoPago.PENDIENTE.name();
        }
        return pagos.stream()
                .max(Comparator.comparing(Pago::getId))
                .map(p -> p.getEstado().name())
                .orElse("SIN_PAGO");
    }

    private OfertaPublicaDTO toOferta(MateriaCarrera mc) {
        String horario = String.join(" ",
                Objects.toString(mc.getDia(), ""),
                mc.getInicio() != null && mc.getFin() != null ? mc.getInicio() + " a " + mc.getFin() : "").trim();
        return new OfertaPublicaDTO(
                mc.getId(),
                mc.getMateria().getMateriaNombre(),
                mc.getTurno(),
                horario.isEmpty() ? null : horario,
                mc.getFechaInicio(),
                mc.getFechaLimite(),
                mc.getConstanciaPrecio().getPrecioMp()
        );
    }

    private static String generarCodigo() {
        StringBuilder sb = new StringBuilder(LARGO_CODIGO);
        for (int i = 0; i < LARGO_CODIGO; i++) {
            sb.append(ALFABETO_CODIGO.charAt(RANDOM.nextInt(ALFABETO_CODIGO.length())));
        }
        return sb.toString();
    }

    private static String normalizar(String s) {
        return s.trim().replaceAll("\\s+", " ");
    }

    private static String truncar(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }

    /** tramite_celular es BIGINT: se guardan solo los dígitos. El valor completo queda en el snapshot. */
    private static Long celularComoLong(String celular) {
        String digitos = celular.replaceAll("\\D", "");
        return (digitos.isEmpty() || digitos.length() > 18) ? null : Long.valueOf(digitos);
    }

    private static LocalDate hoy() {
        return LocalDate.now(ZONA);
    }

    private ResponseStatusException noEncontrado() {
        return error(HttpStatus.NOT_FOUND, "Trámite no encontrado.");
    }

    private static ResponseStatusException error(HttpStatus status, String mensaje) {
        return new ResponseStatusException(status, mensaje);
    }
}