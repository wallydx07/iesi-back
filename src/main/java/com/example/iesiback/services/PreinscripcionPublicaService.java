package com.example.iesiback.services;

import com.example.iesiback.dto.DatosPreinscripcion;
import com.example.iesiback.dto.PreinscripcionDtos.*;
import com.example.iesiback.dto.ProductoDTO;
import com.example.iesiback.entities.*;
import com.example.iesiback.enums.EstadoPago;
import com.example.iesiback.repositories.CursadaRepository;
import com.example.iesiback.repositories.MateriaCarreraRepository;
import com.example.iesiback.repositories.PagoRepository;
import com.example.iesiback.repositories.PersonaRepository;
import com.example.iesiback.repositories.TramiteRepository;
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
 * Preinscripción a cursos de Capacitación: canal web (público) y canal presencial (ventanilla).
 *
 * Ninguno de los dos crea ni modifica Persona, Legajo, Inscripcion o Cursada:
 * eso lo hace únicamente FinalizacionInscripcionService cuando se aprueba el pago,
 * venga de Checkout Pro o de la caja.
 */
@Service
public class PreinscripcionPublicaService {

    public static final String TIPO_TRAMITE = "Inscripción Capacitación";
    /** Mismo estado inicial que el resto de los trámites del sistema. */
    public static final String ESTADO_TRAMITE_INICIAL = "No Asignado";

    /** Ajustar al área que usa la bandeja de trámites (las pestañas se generan por tramiteArea). */
    private static final String AREA_TRAMITE = "Ingreso y Admision";
    private static final String CANAL_WEB = "Web";
    private static final String CANAL_PRESENCIAL = "Presencial";

    private static final ZoneId ZONA = ZoneId.of("America/Argentina/Jujuy");
    private static final String ALFABETO_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // sin 0/O/1/I
    private static final int LARGO_CODIGO = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final TramiteService tramiteService;
    private final MateriaCarreraService materiaCarreraService;
    private final CursadaService cursadaService;
    private final PersonaService personaService;
    private final PagoService pagoService;

    @Value("${app.preinscripcion.carrera-id}")
    private String carreraCapacitacionId;

    @Value("${app.front-url}")
    private String frontUrl;

    public PreinscripcionPublicaService(TramiteService tramiteService, MateriaCarreraService materiaCarreraService, CursadaService cursadaService, PersonaService personaService, PagoService pagoService) {
        this.tramiteService = tramiteService;
        this.materiaCarreraService = materiaCarreraService;
        this.cursadaService = cursadaService;
        this.personaService = personaService;
        this.pagoService = pagoService;
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
    // CANAL WEB: crear preinscripción (solo Tramite)
    // =====================================================================

    @Transactional
    public PreinscripcionCreadaResponse crear(PreinscripcionPublicaRequest req) {
        Long dni = Long.valueOf(req.dni());
        MateriaCarrera oferta = ofertaAbierta(req.ofertaId());
        validarNoInscripto(dni, oferta);

        DatosPreinscripcion datos = new DatosPreinscripcion(
                normalizar(req.apellido()),
                normalizar(req.nombre()),
                req.correo().trim().toLowerCase(Locale.ROOT),
                req.celular().trim()
        );

        Tramite guardado = tramiteService.save(nuevoTramite(dni, oferta, datos, CANAL_WEB));
        return new PreinscripcionCreadaResponse(guardado.getCodigoSeguimiento(), guardado.getTramiteEstado());
    }

    // =====================================================================
    // CANAL PRESENCIAL: ventanilla (usuario autenticado)
    // =====================================================================

    /**
     * Crea el trámite de preinscripción desde ventanilla. Después se cobra en la caja;
     * cuando ese pago queda APROBADO, el PagoAprobadoListener finaliza la inscripción.
     *
     * Si la Persona ya existe se usan SUS datos (no los del formulario) y no se modifica nada.
     * Si no existe, se exigen apellido, nombre y celular para poder crearla después del pago.
     */
    @Transactional
    public Tramite crearPresencial(PreinscripcionPresencialRequest req) {
        Long dni = Long.valueOf(req.dni());
        MateriaCarrera oferta = ofertaAbierta(req.ofertaId());
        validarNoInscripto(dni, oferta);

        DatosPreinscripcion datos = Optional.ofNullable(personaService.findAlumnoById(dni.toString()))
                .map(p -> new DatosPreinscripcion(
                        p.getPersonaApellido(),
                        p.getPersonaNombre(),
                        p.getPersonaCorreo(),
                        p.getPersonaDomicilioCelular()))
                .orElseGet(() -> datosDePersonaNueva(req));
        return tramiteService.save(nuevoTramite(dni, oferta, datos, CANAL_PRESENCIAL));
    }

    private DatosPreinscripcion datosDePersonaNueva(PreinscripcionPresencialRequest req) {
        if (vacio(req.apellido()) || vacio(req.nombre()) || vacio(req.celular())) {
            throw error(HttpStatus.UNPROCESSABLE_ENTITY,
                    "El DNI no está registrado: completá apellido, nombre y celular.");
        }
        String correo = vacio(req.correo()) ? null : req.correo().trim().toLowerCase(Locale.ROOT);
        return new DatosPreinscripcion(
                normalizar(req.apellido()),
                normalizar(req.nombre()),
                correo,
                req.celular().trim()
        );
    }

    // =====================================================================
    // CANAL WEB: iniciar pago (el monto lo decide el backend)
    // =====================================================================

    @Transactional
    public IniciarPagoResponse iniciarPago(String codigo) {
        Tramite tramite = tramiteService.lockByCodigoSeguimiento(codigo)
                .filter(t -> TIPO_TRAMITE.equals(t.getTramiteTipo()))
                .orElseThrow(this::noEncontrado);

        if (FinalizacionInscripcionService.estaFinalizado(tramite)
                || !ESTADO_TRAMITE_INICIAL.equals(tramite.getTramiteEstado())) {
            throw error(HttpStatus.CONFLICT, "Este trámite ya no admite pagos.");
        }

        MateriaCarrera oferta = ofertaAbierta(tramite.getOfertaMateriaCarreraId().intValue());
        validarNoInscripto(tramite.getTramiteDni(), oferta);

        List<Pago> pagos = pagoService.findByAtencionId(tramite.getId());
        if (pagos.stream().anyMatch(p -> p.getEstado() == EstadoPago.APROBADO)) {
            throw error(HttpStatus.CONFLICT, "El trámite ya tiene un pago aprobado.");
        }
        if (pagos.stream().anyMatch(this::pagoEnProceso)) {
            throw error(HttpStatus.CONFLICT,
                    "Hay un pago en proceso de acreditación. Esperá su confirmación antes de intentar de nuevo.");
        }

        // Intentos anteriores que nunca llegaron a pagarse quedan dados de baja
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

        OffsetDateTime vence = oferta.getFechaLimite().atTime(LocalTime.of(23, 59, 59))
                .atZone(ZONA).toOffsetDateTime();
        String urlRetorno = frontUrl + "/preinscripcion/resultado?codigo=" + codigo;

        Map<String, String> datos = pagoService.crearPreferencia(
                producto, pago.getId(), urlRetorno, vence, tramite.getTramiteCorreo());

        return new IniciarPagoResponse(datos.get("preferenceId"), datos.get("init_point"));
    }

    // =====================================================================
    // CANAL WEB: estado público (sin datos personales)
    // =====================================================================

    @Transactional(readOnly = true)
    public PreinscripcionEstadoResponse estado(String codigo) {
        Tramite tramite = tramiteService.findByCodigoSeguimiento(codigo)
                .filter(t -> TIPO_TRAMITE.equals(t.getTramiteTipo()))
                .orElseThrow(this::noEncontrado);

        List<Pago> pagos = pagoService.findByAtencionId(tramite.getId());
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

    /** Arma el Tramite de preinscripción, igual para ambos canales salvo el canal. */
    private Tramite nuevoTramite(Long dni, MateriaCarrera oferta, DatosPreinscripcion datos, String canal) {
        Tramite tramite = new Tramite();
        tramite.setTramiteTipo(TIPO_TRAMITE);
        tramite.setTramiteEstado(ESTADO_TRAMITE_INICIAL);
        tramite.setTramiteArea(AREA_TRAMITE);
        tramite.setTramiteCanal(canal);
        tramite.setTramiteFecha(LocalDateTime.now(ZONA));
        tramite.setTramiteDni(dni);
        tramite.setTramiteApellidoNombre(truncar(
                Objects.toString(datos.apellido(), "") + ", " + Objects.toString(datos.nombre(), ""), 100));
        tramite.setTramiteCorreo(datos.correo());
        tramite.setTramiteCelular(celularComoLong(datos.celular()));
        tramite.setTramiteAsunto("Preinscripción " + canal.toLowerCase(Locale.ROOT) + ": "
                + oferta.getMateria().getMateriaNombre());
        tramite.setDatosPreinscripcion(datos);
        tramite.setOfertaMateriaCarreraId(oferta.getId().longValue());
        tramite.setCodigoSeguimiento(generarCodigo()); // TramiteServiceImpl.save lo respeta si ya viene cargado
        return tramite;
    }

    private void validarNoInscripto(Long dni, MateriaCarrera oferta) {
        if (cursadaService.existsByLegajo_LegajoPersonaDni_PersonaDniAndMateriaCarrera_Id(dni, oferta.getId())) {
            throw error(HttpStatus.CONFLICT, "Ya existe una inscripción para este DNI en el curso seleccionado.");
        }
    }

    private MateriaCarrera ofertaAbierta(Integer ofertaId) {
        MateriaCarrera oferta = Optional.ofNullable(ofertaId)
                .flatMap(id -> materiaCarreraService.findOfertaById(id.longValue()))
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
        return c != null && c.getPrecioMp() != null && c.getPrecioMp().signum() > 0;
    }

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

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }

    private static String normalizar(String s) {
        return s.trim().replaceAll("\\s+", " ");
    }

    private static String truncar(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }

    /** tramite_celular es BIGINT: se guardan solo los dígitos. El valor completo queda en el snapshot. */
    private static Long celularComoLong(String celular) {
        if (celular == null) return null;
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