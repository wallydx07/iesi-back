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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

        NotaMateriaDTO promocionExistente = existentes.stream()
                .filter(n -> Objects.equals(n.getMateriaCarreraId(), materiaCarrera.getId())
                        && n.getNotaCondicion() == EstadoCondicion.CURSADA)
                .findFirst()
                .orElse(null);

        existentes.forEach(n -> {
            if (promocionExistente == null ||
                    !n.getNotaId().equals(promocionExistente.getNotaId())) {
                log.info("Eliminando nota previa ID: {} | Condición: {}", n.getNotaId(), n.getNotaCondicion());
                if (n.getNotaCondicion() == EstadoCondicion.EXAMEN_REGULAR
                        || n.getNotaCondicion() == EstadoCondicion.EXAMEN_LIBRE) {
                    log.info("Eliminando exámenes asociados a nota {}", n.getNotaId());
                    examenService.deleteExamenByNotaId(n.getNotaId());
                }
                notaService.eliminarNota(n.getNotaId());
            }
        });


        if (promocionExistente != null) {
            Nota nota = notaService.obtenerNotaPorId(promocionExistente.getNotaId());
            actualizarNota(nota, fila);
//            nota.setNotaCondicion("Cursada");
//            nota.setNotaEstado(estado);
//            notaService.guardarNota(nota);
            log.info("Promoción actualizada correctamente.");
        } else {
            Cursada cursada = cursadaService.obtenerORegistrarCursada(
                    legajo,
                    materiaCarrera.getId().longValue());
            log.info("Cursada obtenida/creada ID: {}", cursada.getId());
            Nota nota = notaService.findByCursadaId(cursada.getId());
            actualizarNota(nota, fila);
//            nota.setNotaCondicion("Cursada");
//            nota.setNotaEstado("Aprobado");
//            notaService.guardarNota(nota);

            log.info("Nota base convertida a PROMOCION y marcada como Aprobado.");
        }
    }

    private void procesarFinalRegular(Legajo legajo,
                                      MateriaCarrera materiaCarrera,
                                      NotaImportDTO fila,
                                      List<NotaMateriaDTO> existentes) {

        log.info("Procesando FINAL REGULAR para Legajo {} - Materia {}",
                legajo.getLegajoId(), materiaCarrera.getId());

        TurnoExamenDTO turno = turnoService.obtenerTurnoMasCercano(fila.getFecha());
        log.info("Turno más cercano encontrado: {}", turno.getTurnoId());

        NotaMateriaDTO notaFinal = existentes.stream()
                .filter(n -> n.getNotaCondicion() != null
                        && (
                        n.getNotaCondicion() == EstadoCondicion.EXAMEN
                                || n.getNotaCondicion() == EstadoCondicion.EXAMEN_REGULAR
                                || n.getNotaCondicion() == EstadoCondicion.EXAMEN_LIBRE
                )
                        && Objects.equals(n.getMateriaCarreraId(), materiaCarrera.getId()))
                .findFirst()
                .orElse(null);

        if (notaFinal != null) {
            log.info("Nota FINAL existente encontrada ID: {}", notaFinal.getNotaId());
            TurnoExamenDTO turnoActual = turnoService.obtenerTurnoActualbyNotaId(notaFinal.getNotaId());

            if (turnoActual != null && turnoActual.getTurnoId().equals(turno.getTurnoId())) {
                log.info("Mismo turno detectado. Actualizando nota.");
                Nota nota = notaService.obtenerNotaPorId(notaFinal.getNotaId());
                nota.setNotaCondicion(EstadoCondicion.EXAMEN_REGULAR);
                actualizarNota(nota, fila);
                notaService.guardarNota(nota);
                return;
            }

            log.info("Turno distinto. Eliminando nota FINAL anterior ID: {}", notaFinal.getNotaId());
            notaService.eliminarNotaIndividual(notaFinal.getNotaId());
        }

        Cursada cursada = cursadaService.buscarOMasCercanaORegistrar(legajo, materiaCarrera);
        log.info("Cursada encontrada ID: {}", cursada.getId());

        examenService.registrarExamen(
                legajo.getLegajoId(),
                turnoService.obtenerTurnoPorId(turno.getTurnoId()),
                materiaCarrera.getMateria(),
                EstadoCondicion.EXAMEN_REGULAR,
                cursada.getId());

        Nota notaExamen = notaService.findExamenPorCursadaYTurnoId(
                cursada.getId(), Integer.valueOf(turno.getTurnoId()));
        notaExamen.setNotaCondicion(EstadoCondicion.EXAMEN_REGULAR);
        notaExamen.setNotaEstado(EstadoNota.APROBADO);
        actualizarNota(notaExamen, fila);
        notaService.guardarNota(notaExamen);

        log.info("Examen FINAL REGULAR registrado correctamente.");
    }

    private void procesarFinalLibre(Legajo legajo,
                                    MateriaCarrera materiaCarrera,
                                    NotaImportDTO fila,
                                    List<NotaMateriaDTO> existentes) {

        log.info("Procesando FINAL LIBRE para Legajo {} - Materia {}",
                legajo.getLegajoId(), materiaCarrera.getId());

        TurnoExamenDTO turno = turnoService.obtenerTurnoMasCercano(fila.getFecha());
        log.info("Turno más cercano encontrado: {}", turno.getTurnoId());

        NotaMateriaDTO notaFinal = existentes.stream()
                .filter(n -> n.getNotaCondicion() != null
                        && (
                        n.getNotaCondicion() == EstadoCondicion.EXAMEN
                                || n.getNotaCondicion() == EstadoCondicion.EXAMEN_REGULAR
                                || n.getNotaCondicion() == EstadoCondicion.EXAMEN_LIBRE
                )
                        && Objects.equals(n.getMateriaCarreraId(), materiaCarrera.getId()))
                .findFirst()
                .orElse(null);

        if (notaFinal != null) {
            log.info("Nota FINAL existente encontrada ID: {}", notaFinal.getNotaId());
            TurnoExamenDTO turnoActual = turnoService.obtenerTurnoActualbyNotaId(notaFinal.getNotaId());

            if (turnoActual != null && turnoActual.getTurnoId().equals(turno.getTurnoId())) {
                log.info("Mismo turno detectado. Actualizando nota.");
                Nota nota = notaService.obtenerNotaPorId(notaFinal.getNotaId());
                nota.setNotaCondicion(EstadoCondicion.EXAMEN_LIBRE);
                actualizarNota(nota, fila);
                notaService.guardarNota(nota);
                return;
            }

            log.info("Turno distinto. Eliminando nota FINAL anterior ID: {}", notaFinal.getNotaId());
            notaService.eliminarNotaIndividual(notaFinal.getNotaId());
        }

        Cursada cursada = cursadaService.buscarOMasCercanaORegistrar(legajo, materiaCarrera);
        log.info("Cursada encontrada ID: {}", cursada.getId());

        examenService.registrarExamen(
                legajo.getLegajoId(),
                turnoService.obtenerTurnoPorId(turno.getTurnoId()),
                materiaCarrera.getMateria(),
                EstadoCondicion.EXAMEN_LIBRE,
                cursada.getId());

        Nota notaExamen = notaService.findExamenPorCursadaYTurnoId(
                cursada.getId(), Integer.valueOf(turno.getTurnoId()));
        notaExamen.setNotaCondicion(EstadoCondicion.EXAMEN_LIBRE);
        notaExamen.setNotaEstado(EstadoNota.APROBADO);
        actualizarNota(notaExamen, fila);
        notaService.guardarNota(notaExamen);

        log.info("Examen FINAL LIBRE registrado correctamente.");
    }

//    private void procesarEquivalencia(Legajo legajo, MateriaCarrera materiaCarrera,
//                                      NotaImportDTO fila,
//                                      List<NotaMateriaDTO> existentes) {
//
//        log.info("Procesando EQUIVALENCIA para materia {}", materiaCarrera.getId());
//
//        NotaMateriaDTO eq = existentes.stream()
//                .filter(n -> "Equivalencia".equals(n.getNotaCondicion())
//                        && n.getMateriaCarreraId().equals(materiaCarrera.getId()))
//                .findFirst()
//                .orElse(null);
//
//        if (eq != null) {
//            log.info("Equivalencia existente encontrada ID: {}", eq.getNotaId());
//            Nota nota = notaService.obtenerNotaPorId(eq.getNotaId());
//            actualizarNota(nota, fila);
//            notaService.guardarNota(nota);
//            log.info("Equivalencia actualizada.");
//        } else {
//            log.info("No existe equivalencia previa. Creando nueva nota.");
//            Nota nueva = crearNota(fila, EstadoCondicion.EQUIVALENCIA);
//            notaService.guardarNota(nueva);
//
//            Equivalencia equivalencia = new Equivalencia();
//            equivalencia.setNota(nueva);
//            equivalencia.setLegajoId(legajo.getLegajoId());
//            equivalencia.setMateriaId(materiaCarrera.getMateria().getMateriaId()); // Asegúrate de pasarle el ID correspondiente
//            equivalencia.setInstitucionOrigen("Pendiente");
//            equivalencia.setMateriaOrigen("Pendiente");
//            equivalencia.setCarreraOrigen("Pendiente");
//            equivalencia.setResolucion("Pendiente");
//            equivalencia.setStatus("ACEPTADA");
//       equivalenciaService.crearEquivalenciaNota(equivalencia, nueva);
//
//        }
//    }


    private void procesarEquivalencia(Legajo legajo, MateriaCarrera materiaCarrera, NotaImportDTO fila, List<NotaMateriaDTO> existentes) {
        log.info("Procesando EQUIVALENCIA para materia {}", materiaCarrera.getId());

        // Buscar si ya existe una equivalencia para esta materia
        Optional<NotaMateriaDTO> eqExistente = existentes.stream()
                .filter(n -> "Equivalencia".equals(n.getNotaCondicion()) && n.getMateriaCarreraId().equals(materiaCarrera.getId()))
                .findFirst();

        if (eqExistente.isPresent()) {
            Long notaId = eqExistente.get().getNotaId();
            log.info("Equivalencia existente encontrada ID: {}", notaId);
            Nota nota = notaService.obtenerNotaPorId(notaId);
            actualizarNota(nota, fila);
            notaService.guardarNota(nota);

            log.info("Equivalencia actualizada.");
        } else {
            log.info("No existe equivalencia previa. Creando nueva nota.");

            Cursada cursada = cursadaService.obtenerORegistrarCursada(legajo, materiaCarrera.getId().longValue());
//            cursada.getNotas()



            Nota nuevaNota = crearNota(fila, EstadoCondicion.EQUIVALENCIA, EstadoNota.APROBADO,cursada);
            // Si el servicio de equivalencia guarda en cascada, podrías omitir este guardarNota
            nuevaNota = notaService.guardarNota(nuevaNota);

            Equivalencia equivalencia = new Equivalencia();
            equivalencia.setNota(nuevaNota);
            equivalencia.setLegajoId(legajo.getLegajoId());
            equivalencia.setMateriaId(materiaCarrera.getMateria().getMateriaId());

            // Valores iniciales por defecto
            equivalencia.setInstitucionOrigen("Pendiente");
            equivalencia.setMateriaOrigen("Pendiente");
            equivalencia.setCarreraOrigen("Pendiente");
            equivalencia.setResolucion("Pendiente");
            equivalencia.setStatus("ACEPTADA");

            // Sincronizar el lado @OneToMany en memoria
            nuevaNota.getEquivalencias().add(equivalencia);

            equivalenciaService.crearEquivalenciaNota(equivalencia, nuevaNota);
            log.info("Nueva equivalencia creada con éxito.");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    private Nota crearNota(NotaImportDTO fila, EstadoCondicion condicion, EstadoNota estado,Cursada cursada) {
        Nota nota = new Nota();
        nota.setNotaFechaNota(fila.getFecha());
        nota.setNotaLibroNota(fila.getLibro());
        nota.setNotaFolioNota(fila.getFolio());
        nota.setNotaCalificacionNotaNumero(fila.getNota());
        nota.setNotaEstado(estado);
        nota.setNotaCondicion(condicion);
        nota.setCursada(cursada);
        return nota;
    }

    private void actualizarNota(Nota nota, NotaImportDTO fila) {
        nota.setNotaFechaNota(fila.getFecha());
        nota.setNotaLibroNota(fila.getLibro());
        nota.setNotaFolioNota(fila.getFolio());
        nota.setNotaCalificacionNotaNumero(fila.getNota());
        nota.setNotaCalificacionNotaLetra(convertirANotaEnLetras(fila.getNota()));
        nota.setNotaCondicion(fila.getCondicion());
        nota.setNotaEstado(fila.getNotaEstado());
    }

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