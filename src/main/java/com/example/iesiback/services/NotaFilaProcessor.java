package com.example.iesiback.services;

import com.example.iesiback.dto.NotaMateriaDTO;
import com.example.iesiback.dto.TurnoExamenDTO;
import com.example.iesiback.entities.*;
import com.example.iesiback.enums.EstadoNota;
import com.example.iesiback.enums.EstadoCondicion;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.iesiback.dto.NotaImportDTO;
import com.example.iesiback.exception.NotaNoEditableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotaFilaProcessor {

    private final LegajoService legajoService;
    private final NotaService notaService;
    private final MateriaCarreraService materiaCarreraService;
    private final TurnoService turnoService;
    private final CursadaService cursadaService;
    private final ExamenService examenService;
    private final MateriaService materiaService;
    private final PersonaService personaService;
    private final EquivalenciaService equivalenciaService;


    /**
     * Procesa una única fila del import de forma atómica.
     * Si falla, solo hace rollback de esta fila — el resto del lote no se ve afectado.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void procesarFila(NotaImportDTO fila) {

        Persona persona = new Persona();
        persona.setPersonaDni(fila.getDni());
        persona.setPersonaNombre(fila.getNombre());
        persona.setPersonaApellido(fila.getApellido());

        log.info("Persona armada -> DNI: {}", persona.getPersonaDni());

        Legajo legajo = legajoService.findOrCreateLegajo(persona, fila.getCarreraId());
        log.info("Legajo obtenido/creado: {}", legajo.getLegajoId());

        MateriaCarrera materiaCarrera = materiaCarreraService.findMateriaCarreraByFechaAndMateriaOrden(
                fila.getFecha(),
                fila.getMateriaOrden(),
                fila.getCarreraId());

        if (materiaCarrera == null) {
            throw new IllegalArgumentException(
                    "MateriaCarrera no encontrada -> Fecha: " + fila.getFecha()
                            + " | Orden: " + fila.getMateriaOrden()
                            + " | Carrera: " + fila.getCarreraId());
        }

        log.info("MateriaCarrera encontrada ID: {}", materiaCarrera.getId());

        List<NotaMateriaDTO> notasExistentes =
                notaService.obtenerTodasNotasPorLegajoSinCorrelativas(legajo.getLegajoId())
                        .stream()
                        .filter(n -> Objects.equals(
                                n.getMateriaId(),
                                materiaCarrera.getMateria() != null
                                        ? materiaCarrera.getMateria().getMateriaId()
                                        : null))
                        .toList();

        log.info("Notas existentes encontradas para la materia: {}", notasExistentes.size());

        switch (fila.getCondicion()) {

            case CURSADA -> {
                log.info("---- Flujo PROMOCION ----");
                procesarPromocion(legajo, materiaCarrera, fila, notasExistentes);
            }

            case EXAMEN_REGULAR -> {
                log.info("---- Flujo FINAL ----");
                procesarFinalRegular(legajo, materiaCarrera, fila, notasExistentes);
            }

            case EXAMEN_LIBRE -> {
                log.info("---- Flujo LIBRE ----");
                procesarFinalLibre(legajo, materiaCarrera, fila, notasExistentes);
            }

            case EQUIVALENCIA -> {
                log.info("---- Flujo EQUIVALENCIA ----");
                procesarEquivalencia(legajo,materiaCarrera, fila, notasExistentes);
            }

            default -> throw new IllegalArgumentException(
                    "Condición desconocida: " + fila.getCondicion());
        }
    }

// ─────────────────────────────────────────────────────────────
    // Flujos internos (sin @Transactional propio — heredan REQUIRES_NEW)
    // ─────────────────────────────────────────────────────────────

    private void procesarPromocion(Legajo legajo,
                                   MateriaCarrera materiaCarrera,
                                   NotaImportDTO fila,
                                   List<NotaMateriaDTO> existentes) {

        log.info("Procesando PROMOCION para Legajo {} - Materia {}",
                legajo.getLegajoId(), materiaCarrera.getId());

        List<NotaMateriaDTO> deEstaMateria = existentes.stream()
                .filter(n -> Objects.equals(n.getMateriaCarreraId(), materiaCarrera.getId()))
                .toList();

        NotaMateriaDTO promocionExistente = deEstaMateria.stream()
                .filter(n -> n.getNotaCondicion() == EstadoCondicion.CURSADA)
                .findFirst()
                .orElse(null);

        eliminarOtrasNotas(deEstaMateria,
                promocionExistente != null ? promocionExistente.getNotaId() : null);

        Nota nota;
        if (promocionExistente != null) {
            nota = notaService.obtenerNotaPorId(promocionExistente.getNotaId());
        } else {
            Cursada cursada = cursadaService.obtenerORegistrarCursada(
                    legajo,
                    materiaCarrera.getId().longValue());
            log.info("Cursada obtenida/creada ID: {}", cursada.getId());
            nota = notaService.findByCursadaIdAndNotaCondicion(cursada.getId(), EstadoCondicion.CURSADA)
                    .orElseThrow(() -> new IllegalStateException(
                            "La cursada " + cursada.getId() + " no tiene nota con condición CURSADA"));
        }

        actualizarNota(nota, fila);
        // Se fuerza DESPUÉS de actualizarNota: si la fila trae otra condición,
        // la próxima importación no encontraría esta nota y la borraría.
        nota.setNotaCondicion(EstadoCondicion.CURSADA);
        notaService.guardarNota(nota);

        log.info("Promoción registrada. Nota ID: {} | Estado: {}", nota.getNotaId(), nota.getNotaEstado());
    }

    private void procesarFinalRegular(Legajo legajo,
                                      MateriaCarrera materiaCarrera,
                                      NotaImportDTO fila,
                                      List<NotaMateriaDTO> existentes) {
        procesarFinal(legajo, materiaCarrera, fila, existentes, EstadoCondicion.EXAMEN_REGULAR);
    }

    private void procesarFinalLibre(Legajo legajo,
                                    MateriaCarrera materiaCarrera,
                                    NotaImportDTO fila,
                                    List<NotaMateriaDTO> existentes) {
        procesarFinal(legajo, materiaCarrera, fila, existentes, EstadoCondicion.EXAMEN_LIBRE);
    }

    private void procesarFinal(Legajo legajo,
                               MateriaCarrera materiaCarrera,
                               NotaImportDTO fila,
                               List<NotaMateriaDTO> existentes,
                               EstadoCondicion condicion) {

        log.info("Procesando {} para Legajo {} - Materia {}",
                condicion, legajo.getLegajoId(), materiaCarrera.getId());

        TurnoExamenDTO turno = turnoService.obtenerTurnoMasCercano(fila.getFecha());
        if (turno == null) {
            throw new IllegalStateException("No hay turno de examen cercano a la fecha " + fila.getFecha());
        }
        log.info("Turno más cercano encontrado: {}", turno.getTurnoId());

        // Solo interesa el examen de ESTE turno. Los exámenes de otros turnos
        // (p. ej. desaprobados anteriores) son historial y se conservan.
        NotaMateriaDTO examenMismoTurno = existentes.stream()
                .filter(n -> Objects.equals(n.getMateriaCarreraId(), materiaCarrera.getId()))
                .filter(n -> esExamen(n.getNotaCondicion()))
                .filter(n -> esDelTurno(n.getNotaId(), turno))
                .findFirst()
                .orElse(null);

        Nota notaExamen;
        Cursada cursada;

        if (examenMismoTurno != null) {
            log.info("Examen existente en el mismo turno, ID: {}. Se actualiza.", examenMismoTurno.getNotaId());
            notaExamen = notaService.obtenerNotaPorId(examenMismoTurno.getNotaId());
            cursada = notaExamen.getCursada();
        } else {
            cursada = cursadaService.buscarOMasCercanaORegistrar(legajo, materiaCarrera);
            log.info("Cursada encontrada ID: {}", cursada.getId());

            examenService.registrarExamen(
                    legajo.getLegajoId(),
                    turnoService.obtenerTurnoPorId(turno.getTurnoId()),
                    materiaCarrera.getMateria(),
                    condicion,
                    cursada.getId());

            notaExamen = notaService.findExamenPorCursadaYTurnoId(
                    cursada.getId(), Integer.valueOf(turno.getTurnoId()));
            if (notaExamen == null) {
                throw new IllegalStateException("No se generó la nota del examen para la cursada "
                        + cursada.getId() + " en el turno " + turno.getTurnoId());
            }
        }

        actualizarNota(notaExamen, fila);
        // Se fuerza DESPUÉS de actualizarNota para que la fila no la pise.
        // El estado (aprobado/desaprobado) sí viene de la planilla.
        notaExamen.setNotaCondicion(condicion);
        notaService.guardarNota(notaExamen);

        if (notaExamen.getNotaEstado() == EstadoNota.APROBADO) {
            cerrarNotaCursada(cursada, condicion);
        } else {
            log.info("Examen con estado {}: la cursada no se cierra.", notaExamen.getNotaEstado());
        }

        log.info("Examen {} registrado correctamente. Nota ID: {}", condicion, notaExamen.getNotaId());
    }


    private void eliminarOtrasNotas(List<NotaMateriaDTO> notas, Long idAConservar) {
        List<NotaMateriaDTO> aEliminar = notas.stream()
                .filter(n -> !Objects.equals(n.getNotaId(), idAConservar))
                .toList();

        aEliminar.forEach(n -> verificarEditable(n.getNotaId()));

        aEliminar.forEach(n -> {
            if (esExamen(n.getNotaCondicion()) && n.getNotaEstado() == EstadoNota.APROBADO) {
                log.warn("Se elimina un examen APROBADO (nota {}) al reemplazar la materia", n.getNotaId());
            }
            log.info("Eliminando nota previa ID: {} | Condición: {}", n.getNotaId(), n.getNotaCondicion());
            if (esExamen(n.getNotaCondicion())) {
                examenService.deleteExamenByNotaId(n.getNotaId());
            }
            notaService.eliminarNota(n.getNotaId());
        });
    }

    private void cerrarNotaCursada(Cursada cursada, EstadoCondicion condicion) {
        Optional<Nota> notaOpt = notaService.findByCursadaIdAndNotaCondicion(
                cursada.getId(), EstadoCondicion.CURSADA);
        if (notaOpt.isEmpty()) {
            log.warn("No se encontró una nota con condición CURSADA para la cursada {}", cursada.getId());
            return;
        }
        Nota nota = notaOpt.get();

        // Misma regla que la edición manual: una nota bloqueada no se toca.
        // No se lanza excepción porque el examen en sí es válido.
        if (Boolean.FALSE.equals(nota.getEditable())) {
            log.warn("Nota de cursada {} bloqueada: no se modifica al cerrar con {}", nota.getNotaId(), condicion);
            return;
        }

        // Solo se completa la calificación si la cursada no tiene una real
        boolean sinCalificacion = nota.getNotaCalificacionNotaNumero() == null
                || nota.getNotaCalificacionNotaNumero() == 0.0;

        switch (condicion) {
            case EXAMEN_REGULAR -> {
                nota.setNotaEstado(EstadoNota.REGULAR);
                if (sinCalificacion) {
                    nota.setNotaCalificacionNotaNumero(6.0);
                    nota.setNotaCalificacionNotaLetra("SEIS");
                }
            }
            // En ambos casos la cursada no se evaluó: queda LIBRE
            case EXAMEN_LIBRE, EQUIVALENCIA -> {
                nota.setNotaEstado(EstadoNota.LIBRE);
                if (sinCalificacion) {
                    nota.setNotaCalificacionNotaNumero(1.0);
                    nota.setNotaCalificacionNotaLetra("UNO");
                }
            }
            default -> {
                return;
            }
        }

        notaService.guardarNota(nota);
    }

    private void procesarEquivalencia(Legajo legajo,
                                      MateriaCarrera materiaCarrera,
                                      NotaImportDTO fila,
                                      List<NotaMateriaDTO> existentes) {
        log.info("Procesando EQUIVALENCIA para materia {}", materiaCarrera.getId());

        List<NotaMateriaDTO> deEstaMateria = existentes.stream()
                .filter(n -> Objects.equals(n.getMateriaCarreraId(), materiaCarrera.getId()))
                .toList();

        Optional<NotaMateriaDTO> eqExistente = deEstaMateria.stream()
                .filter(n -> n.getNotaCondicion() == EstadoCondicion.EQUIVALENCIA)
                .findFirst();

        // Se borran exámenes y otras equivalencias; las notas CURSADA no se borran, se cierran
        List<NotaMateriaDTO> aReemplazar = deEstaMateria.stream()
                .filter(n -> n.getNotaCondicion() != EstadoCondicion.CURSADA)
                .toList();
        eliminarOtrasNotas(aReemplazar, eqExistente.map(NotaMateriaDTO::getNotaId).orElse(null));

        Nota notaEq;
        Cursada cursada;

        if (eqExistente.isPresent()) {
            Long notaId = eqExistente.get().getNotaId();
            log.info("Equivalencia existente encontrada ID: {}", notaId);
            notaEq = notaService.obtenerNotaPorId(notaId);
            actualizarNota(notaEq, fila);
            notaEq.setNotaCondicion(EstadoCondicion.EQUIVALENCIA);
            notaService.guardarNota(notaEq);
            cursada = notaEq.getCursada();
            log.info("Equivalencia actualizada.");
        } else {
            log.info("No existe equivalencia previa. Creando nueva nota.");

            cursada = cursadaService.obtenerORegistrarCursada(legajo, materiaCarrera.getId().longValue());
            notaEq = crearNota(fila, EstadoCondicion.EQUIVALENCIA, EstadoNota.APROBADO, cursada);
            notaEq = notaService.guardarNota(notaEq);

            Equivalencia equivalencia = new Equivalencia();
            equivalencia.setNota(notaEq);
            equivalencia.setLegajoId(legajo.getLegajoId());
            equivalencia.setMateriaId(materiaCarrera.getMateria().getMateriaId());

            equivalencia.setInstitucionOrigen("Pendiente");
            equivalencia.setMateriaOrigen("Pendiente");
            equivalencia.setCarreraOrigen("Pendiente");
            equivalencia.setResolucion("Pendiente");
            equivalencia.setStatus("ACEPTADA");

            notaEq.getEquivalencias().add(equivalencia);

            equivalenciaService.crearEquivalenciaNota(equivalencia, notaEq);
            log.info("Nueva equivalencia creada con éxito. Nota ID: {}", notaEq.getNotaId());
        }

        // La nota CURSADA (incluida la automática "CURSANDO") queda cerrada por equivalencia
        cerrarNotaCursada(cursada, EstadoCondicion.EQUIVALENCIA);
    }
    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    private Nota crearNota(NotaImportDTO fila, EstadoCondicion condicion, EstadoNota estado, Cursada cursada) {
        Nota nota = new Nota();
        nota.setNotaFechaNota(fila.getFecha());
        nota.setNotaLibroNota(fila.getLibro());
        nota.setNotaFolioNota(fila.getFolio());
        nota.setNotaCalificacionNotaNumero(fila.getNota());
        nota.setNotaCalificacionNotaLetra(convertirANotaEnLetras(fila.getNota()));
        nota.setNotaEstado(estado);
        nota.setNotaCondicion(condicion);
        nota.setCursada(cursada);
        return nota;
    }

    private void actualizarNota(Nota nota, NotaImportDTO fila) {
        // La importación masiva actualiza notas ya existentes en varios flujos
        // (promoción, final regular/libre, equivalencia). Antes de pisar los
        // campos, respetamos el mismo bloqueo que en la edición manual: si la
        // nota fue marcada como no editable (editable=false), se rechaza.
        if (Boolean.FALSE.equals(nota.getEditable())) {
            throw new NotaNoEditableException(nota.getNotaId());
        }

        nota.setNotaFechaNota(fila.getFecha());
        nota.setNotaLibroNota(fila.getLibro());
        nota.setNotaFolioNota(fila.getFolio());
        nota.setNotaCalificacionNotaNumero(fila.getNota());
        nota.setNotaCalificacionNotaLetra(convertirANotaEnLetras(fila.getNota()));
        nota.setNotaCondicion(fila.getCondicion());
        nota.setNotaEstado(fila.getNotaEstado());
    }

    /** El bloqueo de edición también protege contra el borrado. */
    private void verificarEditable(Long notaId) {
        Nota nota = notaService.obtenerNotaPorId(notaId);
        if (Boolean.FALSE.equals(nota.getEditable())) {
            throw new NotaNoEditableException(notaId);
        }
    }

    private boolean esExamen(EstadoCondicion condicion) {
        return condicion == EstadoCondicion.EXAMEN
                || condicion == EstadoCondicion.EXAMEN_REGULAR
                || condicion == EstadoCondicion.EXAMEN_LIBRE;
    }

    private boolean esDelTurno(Long notaId, TurnoExamenDTO turno) {
        TurnoExamenDTO turnoNota = turnoService.obtenerTurnoActualbyNotaId(notaId);
        return turnoNota != null && turnoNota.getTurnoId().equals(turno.getTurnoId());
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    private String convertirANotaEnLetras(Double nota) {
        if (nota == null) return null;
        int parteEntera = nota.intValue();
        int parteDecimal = (int) Math.round((nota - parteEntera) * 100);
        if (parteDecimal == 0) {
            return numeroALetras(parteEntera);
        } else {
            return numeroALetras(parteEntera) + " con " + numeroALetras(parteDecimal);
        }
    }

    private String numeroALetras(double numero) {

        String[] unidades = {
                "CERO", "UNO", "DOS", "TRES", "CUATRO", "CINCO",
                "SEIS", "SIETE", "OCHO", "NUEVE", "DIEZ",
                "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE",
                "DIECISÉIS", "DIECISIETE", "DIECIOCHO", "DIECINUEVE"
        };

        String[] decenas = {
                "", "", "VEINTE", "TREINTA", "CUARENTA",
                "CINCUENTA", "SESENTA", "SETENTA", "OCHENTA", "NOVENTA"
        };

        int parteEntera = (int) numero;
        int parteDecimal = (int) Math.round((numero - parteEntera) * 100);

        String resultado;

        if (parteEntera < 20) {
            resultado = unidades[parteEntera];
        } else if (parteEntera < 100) {
            int unidad = parteEntera % 10;
            resultado = (unidad == 0)
                    ? decenas[parteEntera / 10]
                    : decenas[parteEntera / 10] + " Y " + unidades[unidad];
        } else {
            resultado = String.valueOf(parteEntera);
        }

        // Agrega decimales si existen
        if (parteDecimal > 0) {
            resultado += " CON " + String.format("%02d", parteDecimal) + "/100";
        }

        return resultado;
    }
}