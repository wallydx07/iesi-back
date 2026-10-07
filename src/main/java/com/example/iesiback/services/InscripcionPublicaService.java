package com.example.iesiback.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static ar.edu.iesi.inscripcionpublica.model.EstadoPreinscripcion.*;
import static ar.edu.iesi.inscripcionpublica.web.InscripcionPublicaException.*;

/**
 * Flujo público. Regla de transacciones: nada de llamadas externas (captcha, correo, Mercado Pago)
 * dentro de una transacción abierta. Se usa TransactionTemplate para cortar.
 */
@Slf4j
@Service
public class InscripcionPublicaService {

    static final Duration RESERVA = Duration.ofHours(48);
    static final String AREA_TRAMITE = "Inscripciones";
    static final String CANAL_TRAMITE = "WEB";
    static final String DESTINO_REVISION = "Secretaría";

    private final PreinscripcionRepository preRepo;
    private final OfertaPublicaRepository ofertaRepo;
    private final PersonaRepository personaRepo;
    private final CaptchaService captcha;
    private final CodigoVerificacionService codigos;
    private final PromocionService promocion;
    private final IntegracionIesi iesi;
    private final TransactionTemplate tx;

    public InscripcionPublicaService(PreinscripcionRepository preRepo, OfertaPublicaRepository ofertaRepo,
                                     PersonaRepository personaRepo, CaptchaService captcha,
                                     CodigoVerificacionService codigos, PromocionService promocion,
                                     IntegracionIesi iesi, TransactionTemplate tx) {
        this.preRepo = preRepo;
        this.ofertaRepo = ofertaRepo;
        this.personaRepo = personaRepo;
        this.captcha = captcha;
        this.codigos = codigos;
        this.promocion = promocion;
        this.iesi = iesi;
        this.tx = tx;
    }

    private record Envio(String token, String destino, String codigo, TipoInscripcion tipo) {}

    // ======================= Paso 1: datos =======================

    public AspiranteRegistrado registrarAspirante(DatosAspiranteRequest req, String ip) {
        if (!captcha.verificar(req.captchaToken(), ip)) {
            throw invalida("No se pudo validar la verificación de seguridad. Volvé a intentarlo.");
        }
        if (!Boolean.TRUE.equals(req.consentimiento())) {
            throw invalida("Necesitamos tu conformidad con el uso de los datos para continuar.");
        }
        Map<String, Object> persona = PersonaAspiranteValidator.limpiar(req.tipo(), req.persona());
        String dni = (String) persona.get("personaDni");
        String correo = (String) persona.get("personaCorreo");

        Envio envio = tx.execute(st -> {
            Preinscripcion p;
            if (req.token() != null) {
                if (!Seguridad.tokenValido(req.token())) throw noEncontrada();
                p = preRepo.bloquearPorToken(req.token())
                        .filter(x -> x.getEstado() == ASPIRANTE || x.getEstado() == VERIFICADA)
                        .orElseThrow(InscripcionPublicaException::noEncontrada);
                if (p.getTramite() != null && !dni.equals(p.getDni())) {
                    throw conflicto("No se puede cambiar el DNI de una inscripción ya iniciada. Empezá una nueva.");
                }
            } else {
                p = new Preinscripcion();
                p.setToken(Seguridad.nuevoToken());
                p.setTipo(req.tipo());
            }

            p.setDni(dni);
            p.setPersona(persona);
            p.setEstado(ASPIRANTE);

            // Si el DNI ya existe, el código va al correo que ya tiene el instituto:
            // así nadie puede iniciar una inscripción a nombre de otro con su propio correo.
            Optional<Persona> existente = personaRepo.findById(dni);
            p.setDniExistente(existente.isPresent());
            String destino = existente.map(Persona::getPersonaCorreo)
                    .filter(s -> s != null && !s.isBlank())
                    .orElse(correo);
            p.setEmailDestino(destino);

            String codigo = codigos.preparar(p);
            preRepo.save(p);
            return new Envio(p.getToken(), destino, codigo, p.getTipo());
        });

        codigos.enviar(envio.destino(), envio.codigo(), envio.tipo());
        return new AspiranteRegistrado(envio.token(), Seguridad.enmascararEmail(envio.destino()),
                CodigoVerificacionService.SEGUNDOS_ENTRE_ENVIOS);
    }

    // ======================= Paso 2: verificación =======================

    /** noRollbackFor: el intento fallido tiene que quedar contado. */
    @Transactional(noRollbackFor = InscripcionPublicaException.class)
    public void verificar(String token, String codigo) {
        Preinscripcion p = bloquear(token);
        if (p.getEstado() == VERIFICADA) return;
        if (p.getEstado() != ASPIRANTE) throw conflicto("Esta inscripción ya fue verificada.");

        codigos.validar(p, codigo);
        p.setEstado(VERIFICADA);

        // Una sola inscripción activa por DNI y tipo: las anteriores liberan su cupo.
        preRepo.findByDniAndTipoAndEstadoInAndIdNot(p.getDni(), p.getTipo(),
                        EnumSet.of(VERIFICADA, PENDIENTE_PAGO), p.getId())
                .forEach(vieja -> anular(vieja, "Reemplazada por una inscripción más reciente"));

        if (p.getTramite() == null) {
            p.setTramite(iesi.crearTramite(new IntegracionIesi.NuevoTramite(
                    p.getDni(),
                    p.apellidoNombre(),
                    p.dato("personaCorreo"),
                    p.dato("personaDomicilioCelular"),
                    p.getTipo().tipoTramite(),
                    AREA_TRAMITE,
                    CANAL_TRAMITE,
                    "Inscripción web iniciada")));
        }
    }

    public ReenvioResponse reenviarCodigo(String token) {
        Envio envio = tx.execute(st -> {
            Preinscripcion p = bloquear(token);
            if (p.getEstado() != ASPIRANTE) throw conflicto("Esta inscripción ya fue verificada.");
            if (!codigos.puedeReenviar(p)) {
                throw new InscripcionPublicaException(HttpStatus.TOO_MANY_REQUESTS,
                        "Esperá un minuto antes de pedir otro código.");
            }
            String codigo = codigos.preparar(p);
            return new Envio(p.getToken(), p.getEmailDestino(), codigo, p.getTipo());
        });
        codigos.enviar(envio.destino(), envio.codigo(), envio.tipo());
        return new ReenvioResponse(CodigoVerificacionService.SEGUNDOS_ENTRE_ENVIOS);
    }

    // ======================= Paso 3: selección =======================

    @Transactional(readOnly = true)
    public List<OfertaDto> ofertas(TipoInscripcion tipo) {
        LocalDate hoy = LocalDate.now();
        return ofertaRepo.findByTipoAndAbiertaTrueOrderByIdAsc(tipo).stream()
                .filter(o -> !o.inscripcionCerrada(hoy))
                .map(o -> aDto(o, disponibles(o, null)))
                .toList();
    }

    @Transactional
    public ResumenDto registrarSeleccion(String token, List<String> ofertaIds) {
        Preinscripcion p = bloquear(token);
        if (p.getEstado() != VERIFICADA && p.getEstado() != PENDIENTE_PAGO) {
            throw conflicto("Esta inscripción ya no se puede modificar.");
        }

        List<Long> ids = parsearIds(ofertaIds);
        if (ids.isEmpty() || ids.size() > p.getTipo().maxSeleccion()) {
            throw invalida("Elegí entre 1 y " + p.getTipo().maxSeleccion() + " opciones.");
        }

        List<OfertaPublica> ofertas = ofertaRepo.bloquear(ids);
        if (ofertas.size() != ids.size()) throw invalida("Una de las opciones elegidas ya no está disponible.");

        LocalDate hoy = LocalDate.now();
        for (OfertaPublica o : ofertas) {
            if (o.getTipo() != p.getTipo() || o.inscripcionCerrada(hoy)) {
                throw conflicto("La inscripción a " + o.nombreVisible() + " ya cerró.");
            }
            Integer libres = disponibles(o, p.getId());
            if (libres != null && libres <= 0) {
                throw conflicto("Ya no quedan lugares en " + o.nombreVisible() + ".");
            }
        }

        p.getItems().clear();
        ofertas.forEach(p::agregarItem);
        p.setTotal(p.getItems().stream().map(PreinscripcionItem::getArancel).reduce(BigDecimal.ZERO, BigDecimal::add));
        p.setVenceEl(Instant.now().plus(RESERVA));
        p.setEstado(PENDIENTE_PAGO);
        // La preferencia anterior (si había) ya no sirve: el monto puede ser otro.
        p.setInitPoint(null);
        p.setMontoPago(null);

        p.getTramite().setTramiteAsunto(resumenAsunto(p));
        return resumen(p);
    }

    // ======================= Paso 4: pago =======================

    private record DatosPago(Long id, Tramite tramite, BigDecimal total, String descripcion,
                             String token, Instant venceEl, String initPointExistente) {}

    public PagoResponse iniciarPago(String token) {
        DatosPago d = tx.execute(st -> {
            Preinscripcion p = bloquear(token);
            if (p.getEstado() != PENDIENTE_PAGO) throw conflicto("Esta inscripción no tiene un pago pendiente.");
            if (p.getVenceEl().isBefore(Instant.now())) {
                throw new InscripcionPublicaException(HttpStatus.GONE, "La reserva venció. Iniciá una nueva inscripción.");
            }
            String reuso = (p.getInitPoint() != null && p.getMontoPago() != null
                    && p.getMontoPago().compareTo(p.getTotal()) == 0) ? p.getInitPoint() : null;
            return new DatosPago(p.getId(), p.getTramite(), p.getTotal(), resumenAsunto(p),
                    p.getToken(), p.getVenceEl(), reuso);
        });

        if (d.initPointExistente() != null) return new PagoResponse(d.initPointExistente());

        IntegracionIesi.PagoIniciado pi = iesi.iniciarPago(d.tramite(), d.total(), d.descripcion(), d.token(), d.venceEl());

        tx.executeWithoutResult(st -> preRepo.findById(d.id()).ifPresent(p -> {
            p.setPagoId(pi.pagoId());
            p.setInitPoint(pi.initPoint());
            p.setMontoPago(d.total());
        }));
        return new PagoResponse(pi.initPoint());
    }

    @Transactional(readOnly = true)
    public ResumenDto retomar(String token) {
        if (!Seguridad.tokenValido(token)) throw noEncontrada();
        Preinscripcion p = preRepo.findByToken(token).orElseThrow(InscripcionPublicaException::noEncontrada);
        if (p.getItems().isEmpty() || p.getEstado() == ASPIRANTE || p.getEstado() == VERIFICADA) {
            throw noEncontrada();
        }
        return resumen(p);
    }

    // ======================= Webhook =======================

    /**
     * Llamalo desde tu webhook de Mercado Pago, DESPUÉS de actualizar el Pago con mapearEstadoMercadoPago.
     * Corre en su propia transacción: si algo falla acá, el Pago igual queda registrado
     * y la conciliación lo puede reprocesar. Es idempotente: MP repite notificaciones.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPagoActualizado(Pago pago) {
        if (pago.getTramite() == null || !iesi.esAprobado(pago)) return;
        Optional<Preinscripcion> op = preRepo.bloquearPorTramite(pago.getTramite());
        if (op.isEmpty()) return; // no es una inscripción pública
        Preinscripcion p = op.get();
        if (PAGO_YA_PROCESADO.contains(p.getEstado())) return;

        BigDecimal pagado = pago.getMontoTotal() == null ? null : new BigDecimal(String.valueOf(pago.getMontoTotal()));
        if (pagado == null || p.getTotal() == null || pagado.compareTo(p.getTotal()) != 0) {
            aRevision(p, "El monto pagado (" + pagado + ") no coincide con el total de la inscripción (" + p.getTotal() + ").");
            return;
        }

        // Pagó con la reserva vencida o reemplazada: solo sigue si todavía hay lugar.
        if (p.getEstado() != PENDIENTE_PAGO && !hayCupo(p)) {
            aRevision(p, "Pagó con la reserva vencida y ya no queda cupo. Definir sobrecupo, cambio o devolución.");
            return;
        }

        if (p.getTipo().requiereValidacionManual()) {
            p.setEstado(PAGADA_PENDIENTE_DOCUMENTACION);
            iesi.cambiarEstadoTramite(p.getTramite(), IntegracionIesi.TRAMITE_EN_TRAMITE, "Pago acreditado");
            iesi.derivarTramite(p.getTramite(), DESTINO_REVISION, "Pago acreditado: validar documentación del aspirante.");
            return;
        }

        PromocionService.Resultado r = promocion.promover(p, false);
        if (!r.ok()) {
            aRevision(p, r.motivo());
            return;
        }
        p.setEstado(PAGADA);
        iesi.cambiarEstadoTramite(p.getTramite(), IntegracionIesi.TRAMITE_RESUELTO,
                "Inscripción confirmada automáticamente al acreditarse el pago.");
    }

    // ======================= Utilidades =======================

    Preinscripcion bloquear(String token) {
        if (!Seguridad.tokenValido(token)) throw noEncontrada();
        return preRepo.bloquearPorToken(token).orElseThrow(InscripcionPublicaException::noEncontrada);
    }

    Integer disponibles(OfertaPublica o, Long excluir) {
        if (o.getCupo() == null) return null;
        long ocupados = preRepo.contarOcupados(o.getId(), OCUPAN_CUPO, Instant.now(), excluir == null ? -1L : excluir);
        return (int) Math.max(0, o.getCupo() - ocupados);
    }

    boolean hayCupo(Preinscripcion p) {
        List<Long> ids = p.getItems().stream().map(i -> i.getOferta().getId()).toList();
        for (OfertaPublica o : ofertaRepo.bloquear(ids)) {
            Integer libres = disponibles(o, p.getId());
            if (libres != null && libres <= 0) return false;
        }
        return true;
    }

    void aRevision(Preinscripcion p, String motivo) {
        log.warn("Preinscripción {} a revisión: {}", p.getId(), motivo);
        p.setEstado(EN_REVISION);
        p.setMotivoRevision(motivo);
        if (p.getTramite() != null) {
            iesi.cambiarEstadoTramite(p.getTramite(), IntegracionIesi.TRAMITE_EN_TRAMITE, motivo);
            iesi.derivarTramite(p.getTramite(), DESTINO_REVISION, motivo);
        }
    }

    void anular(Preinscripcion p, String motivo) {
        p.setEstado(ANULADA);
        p.setMotivoRevision(motivo);
        if (p.getTramite() != null) iesi.cambiarEstadoTramite(p.getTramite(), IntegracionIesi.TRAMITE_ANULADO, motivo);
    }

    ResumenDto resumen(Preinscripcion p) {
        return new ResumenDto(
                p.getToken(),
                p.getTipo(),
                p.getTramite() == null ? null : p.getTramite().getCodigoSeguimiento(),
                p.getEstado(),
                p.apellidoNombre(),
                p.getDni(),
                p.getItems().stream()
                        .map(i -> new ItemResumen(String.valueOf(i.getOferta().getId()), i.getNombre(), i.getArancel()))
                        .toList(),
                p.getTotal(),
                p.getVenceEl());
    }

    private static String resumenAsunto(Preinscripcion p) {
        String nombres = String.join(", ", p.getItems().stream().map(PreinscripcionItem::getNombre).toList());
        String s = p.getTipo().tipoTramite() + ": " + nombres;
        return s.length() > 250 ? s.substring(0, 247) + "..." : s;
    }

    private static List<Long> parsearIds(List<String> ids) {
        if (ids == null) return List.of();
        try {
            return ids.stream().map(String::trim).map(Long::valueOf).distinct().sorted().toList();
        } catch (NumberFormatException e) {
            throw invalida("Una de las opciones elegidas no es válida.");
        }
    }

    private static OfertaDto aDto(OfertaPublica o, Integer libres) {
        MateriaCarrera mc = o.getMateriaCarrera();
        String horario = null, turno = null, fechaLimite = null;
        if (mc != null) {
            if (mc.getDia() != null && mc.getInicio() != null && mc.getFin() != null) {
                horario = mc.getDia() + " de " + mc.getInicio() + " a " + mc.getFin();
            }
            turno = mc.getTurno() == null ? null : String.valueOf(mc.getTurno());
            fechaLimite = mc.getFechaLimite() == null ? null : String.valueOf(mc.getFechaLimite());
        }
        return new OfertaDto(String.valueOf(o.getId()), o.nombreVisible(), o.getDescripcion(), o.getModalidad(),
                horario, turno, o.getFechaInicio(), fechaLimite, o.getArancel(), libres);
    }
}