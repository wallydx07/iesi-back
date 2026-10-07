package com.example.iesiback.services;
package com.example.iesiback.services;

import com.example.iesiback.dto.DatosPreinscripcion;
import com.example.iesiback.entities.*;
import com.example.iesiback.enums.EstadoPago;
import com.example.iesiback.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Transacción 2 del webhook: convierte un trámite de preinscripción PAGADO
 * en una inscripción académica real.
 *
 *   Persona (buscar por DNI; si no existe, crear desde el snapshot del trámite)
 *     → Legajo + Inscripcion a la carrera de la oferta (buscar; si no existe, crear)
 *       → Cursada en la oferta (buscar; si no existe, crear)
 *
 * IDEMPOTENTE: puede ejecutarse N veces para el mismo pago sin duplicar nada.
 *  - El Tramite se bloquea con SELECT ... FOR UPDATE: dos ejecuciones simultáneas se serializan
 *    y la segunda encuentra el trámite ya finalizado.
 *  - La Persona existente también se bloquea: serializa trámites distintos del mismo DNI.
 *  - Cada paso es "buscar o crear".
 *  - Los UNIQUE de la base son la red final (PK persona, PK legajo, cursada legajo+oferta).
 *
 * TRANSACCIONAL: si cualquier paso falla se revierte todo (no queda inscripción a medias)
 * y la excepción sube al webhook, que responde 500 para que Mercado Pago reintente.
 * El Pago ya quedó APROBADO en la transacción anterior, así que el cobro nunca se pierde.
 */
@Service
public class FinalizacionInscripcionService {

    private static final Logger log = LoggerFactory.getLogger(FinalizacionInscripcionService.class);

    public static final String ESTADO_TRAMITE_FINALIZADO = "Resuelto";
    public static final String USUARIO_SISTEMA = "WEB-MP";

    public enum Resultado { FINALIZADA, YA_FINALIZADA, NO_CORRESPONDE, REQUIERE_REVISION }

    private final PagoRepository pagoRepository;
    private final TramiteRepository tramiteRepository;
    private final PersonaRepository personaRepository;
    private final MateriaCarreraRepository materiaCarreraRepository;
    private final LegajoRepository legajoRepository;
    private final LegajoService legajoService;
    private final CursadaService cursadaService;

    @PersistenceContext
    private EntityManager entityManager;

    public FinalizacionInscripcionService(PagoRepository pagoRepository,
                                          TramiteRepository tramiteRepository,
                                          PersonaRepository personaRepository,
                                          MateriaCarreraRepository materiaCarreraRepository,
                                          LegajoRepository legajoRepository,
                                          LegajoService legajoService,
                                          CursadaService cursadaService) {
        this.pagoRepository = pagoRepository;
        this.tramiteRepository = tramiteRepository;
        this.personaRepository = personaRepository;
        this.materiaCarreraRepository = materiaCarreraRepository;
        this.legajoRepository = legajoRepository;
        this.legajoService = legajoService;
        this.cursadaService = cursadaService;
    }

    public static boolean estaFinalizado(Tramite tramite) {
        return ESTADO_TRAMITE_FINALIZADO.equals(tramite.getTramiteEstado()) && tramite.getLegajoId() != null;
    }

    @Transactional
    public Resultado finalizar(Integer pagoId) {

        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new IllegalStateException("Pago inexistente: " + pagoId));

        // Única condición que dispara la inscripción
        if (pago.getEstado() != EstadoPago.APROBADO || pago.getTramite() == null) {
            return Resultado.NO_CORRESPONDE;
        }

        Tramite tramite = tramiteRepository.lockById(pago.getTramite().getId())
                .orElseThrow(() -> new IllegalStateException("Trámite inexistente para el pago " + pagoId));

        // Pagos de caja, constancias, etc.: no hay inscripción que finalizar
        if (!PreinscripcionPublicaService.TIPO_TRAMITE.equals(tramite.getTramiteTipo())) {
            return Resultado.NO_CORRESPONDE;
        }

        if (estaFinalizado(tramite)) {
            advertirSiHayDobleCobro(tramite);
            return Resultado.YA_FINALIZADA;
        }

        // El importe acreditado debe cubrir lo que el backend fijó al crear el pago
        BigDecimal esperado = montoEsperado(pago);
        if (pago.getMontoTotal() == null || pago.getMontoTotal().compareTo(esperado) < 0) {
            log.error("Pago {} aprobado por {} pero se esperaba {}. Trámite {} queda para revisión.",
                    pago.getId(), pago.getMontoTotal(), esperado, tramite.getId());
            agregarObservacion(tramite, "Pago #" + pago.getId() + " aprobado por " + pago.getMontoTotal()
                    + " (esperado " + esperado + "). Requiere revisión manual.");
            return Resultado.REQUIERE_REVISION;
        }

        if (tramite.getTramiteDni() == null || tramite.getOfertaMateriaCarreraId() == null) {
            log.error("Trámite {} sin DNI u oferta. No se puede finalizar automáticamente.", tramite.getId());
            agregarObservacion(tramite, "Pago #" + pago.getId() + " aprobado pero el trámite no tiene DNI u oferta. "
                    + "Requiere revisión manual.");
            return Resultado.REQUIERE_REVISION;
        }

        MateriaCarrera oferta = materiaCarreraRepository.findOfertaById(tramite.getOfertaMateriaCarreraId())
                .orElseThrow(() -> new IllegalStateException("Oferta inexistente: " + tramite.getOfertaMateriaCarreraId()));
        Carrera carrera = oferta.getCarrera();

        // 1) Persona: si existe se usa TAL CUAL (no se modifica ningún dato)
        Persona persona = personaRepository.lockByDni(tramite.getTramiteDni())
                .orElseGet(() -> crearPersona(tramite));

        // 2) Legajo + Inscripcion a la carrera de la oferta
        Legajo legajo = legajoRepository
                .findByLegajoPersonaDni_PersonaDniAndInscripcionCarrera_Carrera_CarreraId(
                        persona.getPersonaDni(), carrera.getCarreraId())
                .orElseGet(() -> legajoService.crearLegajoConInscripcion(persona, carrera, USUARIO_SISTEMA));

        // 3) Cursada en el curso elegido (find-or-create ya existente en CursadaService)
        cursadaService.obtenerORegistrarCursada(legajo, oferta.getId().longValue());

        // 4) Cierre del trámite
        tramite.setLegajoId(legajo.getLegajoId());
        tramite.setTramiteEstado(ESTADO_TRAMITE_FINALIZADO);
        tramite.setTramiteRespuesta("Inscripción confirmada automáticamente por pago aprobado (pago #"
                + pago.getId() + ").");

        log.info("Inscripción finalizada: trámite {} → legajo {} → oferta {} (pago {})",
                tramite.getId(), legajo.getLegajoId(), oferta.getId(), pago.getId());
        return Resultado.FINALIZADA;
    }

    /**
     * persist() y no save(): con id asignado (DNI), save() hace merge, y si otra transacción
     * creara la misma Persona entre la búsqueda y el guardado, merge la SOBRESCRIBIRÍA dejando
     * en null domicilio, CUIL, etc. persist() siempre intenta INSERT: en ese caso falla por PK,
     * se revierte y el reintento del webhook encuentra la Persona existente.
     */
    private Persona crearPersona(Tramite tramite) {
        DatosPreinscripcion datos = tramite.getDatosPreinscripcion();
        if (datos == null) {
            throw new IllegalStateException("Trámite " + tramite.getId() + " sin datos de preinscripción");
        }
        Persona persona = new Persona();
        persona.setPersonaDni(tramite.getTramiteDni());
        persona.setPersonaApellido(datos.apellido());
        persona.setPersonaNombre(datos.nombre());
        persona.setPersonaCorreo(datos.correo());
        persona.setPersonaDomicilioCelular(datos.celular());
        entityManager.persist(persona);
        log.info("Persona creada desde preinscripción: dni={} (trámite {})", persona.getPersonaDni(), tramite.getId());
        return persona;
    }

    private BigDecimal montoEsperado(Pago pago) {
        if (pago.getDetalles() == null || pago.getDetalles().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return pago.getDetalles().stream()
                .map(d -> d.getMonto().multiply(BigDecimal.valueOf(d.getCantidad() != null ? d.getCantidad() : 1)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void advertirSiHayDobleCobro(Tramite tramite) {
        List<Pago> aprobados = pagoRepository.findAllByTramiteId(tramite.getId()).stream()
                .filter(p -> p.getEstado() == EstadoPago.APROBADO)
                .toList();
        if (aprobados.size() > 1) {
            log.warn("Trámite {} tiene {} pagos aprobados: posible doble cobro.", tramite.getId(), aprobados.size());
            agregarObservacion(tramite, "Atención: el trámite tiene " + aprobados.size()
                    + " pagos aprobados. Verificar posible doble cobro y reembolsar si corresponde.");
        }
    }

    private void agregarObservacion(Tramite tramite, String texto) {
        String actual = tramite.getTramiteObservaciones();
        if (actual != null && actual.contains(texto)) {
            return; // los reintentos del webhook no duplican la observación
        }
        tramite.setTramiteObservaciones(actual == null || actual.isBlank() ? texto : actual + "\n" + texto);
    }
}