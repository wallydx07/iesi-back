package com.example.iesiback.services;

import com.example.iesiback.dto.NotaImportDTO;
import com.example.iesiback.dto.NotaMateriaDTO;
import com.example.iesiback.dto.TurnoExamenDTO;
import com.example.iesiback.entities.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TrayectoriaAcademicaServiceImpl implements TrayectoriaAcademicaService {

    private final LegajoService legajoService;
    private final NotaService notaService;
    private final MateriaCarreraService materiaCarreraService;
    private final TurnoService turnoService;
    private final CursadaService cursadaService;
    private final CursadaExamenService cursadaExamenService;
    private final ExamenService examenService;
    private final PermisoService permisoService;
    private final MateriaService materiaService;
    private final PersonaService personaService;



    @Override
    public void procesarNotas(List<NotaImportDTO> filas) {

        log.info("========== INICIO PROCESAMIENTO ==========");
        log.info("Cantidad de filas recibidas: {}", filas.size());

        int filaNumero = 0;
        int filasIgnoradas = 0;

        for (NotaImportDTO fila : filas) {

            filaNumero++;

            log.info("--------------------------------------------------");
            log.info("Fila #{}", filaNumero);
            log.info("Datos crudos -> DNI: {} | Nombre: {} | Apellido: {} | CarreraId: {} | MateriaOrden: {} | Fecha: {} | Condición: {}",
                    fila.getDni(),
                    fila.getNombre(),
                    fila.getApellido(),
                    fila.getCarreraId(),
                    fila.getMateriaOrden(),
                    fila.getFecha(),
                    fila.getCondicion());

            // 🔎 VALIDACIONES CRÍTICAS (sin cambiar lógica)
            if (fila.getDni() == null) {
                log.warn("⚠ Fila #{} ignorada -> DNI nulo", filaNumero);
                filasIgnoradas++;
                continue;
            }

            if (fila.getCarreraId() == null) {
                log.warn("⚠ Fila #{} ignorada -> CarreraId nulo", filaNumero);
                filasIgnoradas++;
                continue;
            }

            if (fila.getMateriaOrden() == null) {
                log.warn("⚠ Fila #{} ignorada -> MateriaOrden nulo", filaNumero);
                filasIgnoradas++;
                continue;
            }

            if (fila.getCondicion() == null || fila.getCondicion().isBlank()) {
                log.warn("⚠ Fila #{} ignorada -> Condición nula o vacía", filaNumero);
                filasIgnoradas++;
                continue;
            }

            if (fila.getFecha() == null) {
                LocalDate aux=materiaCarreraService.obtenerFechaVigencia(fila.getCarreraId(),fila.getMateriaOrden());
                if (aux == null) {
                    log.warn("⚠ Fila #{} ignorada -> Condición nula o vacía", filaNumero);
                    filasIgnoradas++;
                    continue;
                }else {
                    log.warn("⚠ fecha #{} nula, se completo fecha", aux);
                    fila.setFecha(aux);

                }

            }

            log.info("Procesando DNI: {} | MateriaOrden: {} | Condición: {}",
                    fila.getDni(),
                    fila.getMateriaOrden(),
                    fila.getCondicion());

            try {

                Persona persona = new Persona();
                persona.setPersonaDni(fila.getDni());
                persona.setPersonaNombre(fila.getNombre());
                persona.setPersonaApellido(fila.getApellido());

                log.info("Persona armada -> DNI: {}", persona.getPersonaDni());

                Legajo legajo =
                        legajoService.findOrCreateLegajo(persona, fila.getCarreraId());

                log.info("Legajo obtenido/creado: {}", legajo.getLegajoId());

                MateriaCarrera materiaCarrera =
                        materiaCarreraService.findMateriaCarreraByFechaAndMateriaOrden(
                                fila.getFecha(),
                                fila.getMateriaOrden(),
                                fila.getCarreraId());

                if (materiaCarrera == null) {
                    log.error("❌ MateriaCarrera no encontrada -> Fecha: {} | Orden: {} | Carrera: {}",
                            fila.getFecha(),
                            fila.getMateriaOrden(),
                            fila.getCarreraId());
                    continue;
                }

                log.info("MateriaCarrera encontrada ID: {}", materiaCarrera.getId());

//                List<NotaMateriaDTO> notasExistentes =
//                        notaService.obtenerTodasNotasPorLegajoSinCorrelativas(legajo.getLegajoId())
//                                .stream()
//                                .filter(n -> Objects.equals(
//                                        n.getMateriaId(),
//                                        materiaCarrera.getMateria().getMateriaId()))
//                                .toList();

                List<NotaMateriaDTO> notasExistentes =
                        notaService.obtenerTodasNotasPorLegajoSinCorrelativas(legajo.getLegajoId())
                                .stream()
                                .filter(n -> Objects.equals(
                                        n.getMateriaId(),
                                        materiaCarrera.getMateria() != null ? materiaCarrera.getMateria().getMateriaId() : null
                                ))
                                .toList();
                log.info("Notas existentes encontradas para la materia: {}", notasExistentes.size());

                switch (fila.getCondicion()) {

                    case "PROMOCION" -> {
                        log.info("---- Flujo PROMOCION ----");
                        procesarPromocion(legajo, materiaCarrera, fila, notasExistentes);
                    }

                    case "FINAL" -> {
                        log.info("---- Flujo FINAL ----");
                        procesarFinalRegular(legajo, materiaCarrera, fila, notasExistentes);
                    }

                    case "LIBRE" -> {
                        log.info("---- Flujo FINAL ----");
                        procesarFinalLibre(legajo, materiaCarrera, fila, notasExistentes);
                    }


                    case "EQUIVALENCIA" -> {
                        log.info("---- Flujo EQUIVALENCIA ----");
                        procesarEquivalencia(materiaCarrera, fila, notasExistentes);
                    }

                    default -> log.warn("⚠ Condición desconocida en fila #{}: {}", filaNumero, fila.getCondicion());
                }

            } catch (Exception e) {

                log.error("💥 Error procesando fila #{} -> DNI: {} | Error: {}",
                        filaNumero,
                        fila.getDni(),
                        e.getMessage(),
                        e);

                filasIgnoradas++;
            }
        }

        log.info("========== FIN PROCESAMIENTO ==========");
        log.info("Total filas procesadas: {}", filas.size());
        log.info("Total filas ignoradas: {}", filasIgnoradas);
    }

    private void procesarPromocion(Legajo legajo,
                                   MateriaCarrera materiaCarrera,
                                   NotaImportDTO fila,
                                   List<NotaMateriaDTO> existentes) {

        log.info("Procesando PROMOCION para Legajo {} - Materia {}",
                legajo.getLegajoId(),
                materiaCarrera.getId());

        NotaMateriaDTO promocionExistente = existentes.stream()
                .filter(n -> Objects.equals(n.getMateriaCarreraId(), materiaCarrera.getId())
                        && "Cursada".equals(n.getNotaCondicion()))
                .findFirst()
                .orElse(null);

        if (promocionExistente != null) {
            log.info("Promoción existente encontrada. Nota ID: {}", promocionExistente.getNotaId());
        } else {
            log.info("No existe promoción previa. Se generará/convertirá nota base.");
        }

        existentes.forEach(n -> {
            if (promocionExistente == null ||
                    !n.getNotaId().equals(promocionExistente.getNotaId())) {
                log.info("Eliminando nota previa ID: {}", n.getNotaId());
//                notaService.eliminarNotaIndividual(n.getNotaId());
                log.info("Nota -> Condición: {} | Estado: {} | ID: {} | MateriaOrden: {}",
                        n.getNotaCondicion(),
                        n.getNotaEstado(),
                        n.getNotaId(),
                        n.getMateriaOrden());
                if(n.getNotaCondicion().equals("Examen Regular")||n.getNotaCondicion().equals("Examen Libre")){
//                examenService.deleteExamen(n.getNotaId());
                    log.info("Eliminando exámenes asociados a nota {}", n.getNotaId());
                    examenService.deleteExamenByNotaId(n.getNotaId());
                }
                notaService.eliminarNota(n.getNotaId());

            }
        });

        if (promocionExistente != null) {

            Nota nota = notaService.obtenerNotaPorId(promocionExistente.getNotaId());
            actualizarNota(nota, fila);
            nota.setNotaCondicion("Cursada");
            nota.setNotaEstado("Aprobado");

            notaService.guardarNota(nota);
            log.info("Promoción actualizada correctamente.");

        } else {

            Cursada cursada = cursadaService.obtenerORegistrarCursada(
                    legajo,
                    materiaCarrera.getId().longValue()
            );

            log.info("Cursada obtenida/creada ID: {}", cursada.getId());

            log.info("notas de la cursada: {}", cursada.getNotas().size());

            Nota nota = notaService.findByCursadaId(cursada.getId());

            actualizarNota(nota, fila);
            nota.setNotaCondicion("Cursada");
            nota.setNotaEstado("Aprobado");
            log.info("seva a giuardar lan nota: {}", nota.getNotaId());
            notaService.guardarNota(nota);

            log.info("Nota base convertida a PROMOCION y marcada como Aprobado.");
        }
    }

    private void procesarFinalRegular(
            Legajo legajo,
            MateriaCarrera materiaCarrera,
            NotaImportDTO fila,
            List<NotaMateriaDTO> existentes) {

        log.info("Procesando FINAL para Legajo {} - Materia {}",
                legajo.getLegajoId(),
                materiaCarrera.getId());

        TurnoExamenDTO turno =
                turnoService.obtenerTurnoMasCercano(fila.getFecha());

        log.info("Turno más cercano encontrado: {}", turno.getTurnoId());

        //Permiso permiso =permisoService.obtenerOCrearPermiso(legajo.getLegajoId(),turno.getTurnoId());

        NotaMateriaDTO notaFinal = existentes.stream()
                .filter(n -> n.getNotaCondicion() != null
                        && (n.getNotaCondicion().equalsIgnoreCase("FINAL")
                        || n.getNotaCondicion().equalsIgnoreCase("Examen Regular")
                        || n.getNotaCondicion().equalsIgnoreCase("Examen Libre"))
                        && Objects.equals(n.getMateriaCarreraId(), materiaCarrera.getId()))
                .findFirst()
                .orElse(null);

        if (notaFinal != null) {
            log.info("Nota FINAL existente encontrada ID: {}", notaFinal.getNotaId());
            TurnoExamenDTO turnoActual =
                    turnoService.obtenerTurnoActualbyNotaId(notaFinal.getNotaId());
            if (turnoActual != null &&
                    turnoActual.getTurnoId().equals(turno.getTurnoId())) {
                log.info("Mismo turno detectado. Actualizando nota.");
                Nota nota = notaService.obtenerNotaPorId(notaFinal.getNotaId());
                nota.setNotaCondicion("Examen Regular");
                actualizarNota(nota, fila);
                notaService.guardarNota(nota);
                return;
            }
            log.info("Turno distinto. Eliminando nota FINAL anterior ID: {}",
                    notaFinal.getNotaId());
            notaService.eliminarNotaIndividual(notaFinal.getNotaId());
        }

        Cursada  cursada=
                cursadaService.buscarOMasCercanaORegistrar(
                        legajo,
                        materiaCarrera);

        log.info("Cursada encontrada cursadaID: {}",
                cursada.getId());

        log.info("Registrando examen FINAL para cursada ID: {}", cursada.getId());

        examenService.registrarExamen(
                legajo.getLegajoId(),
                turnoService.obtenerTurnoPorId(turno.getTurnoId()),
                materiaCarrera.getMateria(),
                "Examen Regular",
                cursada.getId()
        );

        log.info("Obteniendo  - Cursada ID: {}, turno ID: {}",
                cursada.getId(),turno.getTurnoId());

        Nota notaexamen =notaService.findExamenPorCursadaYTurnoId(cursada.getId(),Integer.valueOf(turno.getTurnoId()));
        notaexamen.setNotaCondicion("Examen Regular");
        notaexamen.setNotaEstado("Aprobado");
        actualizarNota(notaexamen, fila);
        notaService.guardarNota(notaexamen);

        log.info("Examen FINAL registrado correctamente.");
    }


    private void procesarFinalLibre(
            Legajo legajo,
            MateriaCarrera materiaCarrera,
            NotaImportDTO fila,
            List<NotaMateriaDTO> existentes) {

        log.info("Procesando FINAL para Legajo {} - Materia {}",
                legajo.getLegajoId(),
                materiaCarrera.getId());

        TurnoExamenDTO turno =
                turnoService.obtenerTurnoMasCercano(fila.getFecha());

        log.info("Turno más cercano encontrado: {}", turno.getTurnoId());

        //Permiso permiso =permisoService.obtenerOCrearPermiso(legajo.getLegajoId(),turno.getTurnoId());

        NotaMateriaDTO notaFinal = existentes.stream()
                .filter(n -> n.getNotaCondicion() != null
                        && (n.getNotaCondicion().equalsIgnoreCase("FINAL")
                        || n.getNotaCondicion().equalsIgnoreCase("Examen Regular")
                        || n.getNotaCondicion().equalsIgnoreCase("Examen Libre"))
                        && Objects.equals(n.getMateriaCarreraId(), materiaCarrera.getId()))
                .findFirst()
                .orElse(null);

        if (notaFinal != null) {
            log.info("Nota FINAL existente encontrada ID: {}", notaFinal.getNotaId());
            TurnoExamenDTO turnoActual =
                    turnoService.obtenerTurnoActualbyNotaId(notaFinal.getNotaId());
            if (turnoActual != null &&
                    turnoActual.getTurnoId().equals(turno.getTurnoId())) {
                log.info("Mismo turno detectado. Actualizando nota.");
                Nota nota = notaService.obtenerNotaPorId(notaFinal.getNotaId());
                nota.setNotaCondicion("Examen Libre");
                actualizarNota(nota, fila);
                notaService.guardarNota(nota);
                return;
            }
            log.info("Turno distinto. Eliminando nota FINAL anterior ID: {}",
                    notaFinal.getNotaId());
            notaService.eliminarNotaIndividual(notaFinal.getNotaId());
        }

        Cursada  cursada=
                cursadaService.buscarOMasCercanaORegistrar(
                        legajo,
                        materiaCarrera);

        log.info("Cursada encontrada cursadaID: {}",
                cursada.getId());

        log.info("Registrando examen FINAL para cursada ID: {}", cursada.getId());

        examenService.registrarExamen(
                legajo.getLegajoId(),
                turnoService.obtenerTurnoPorId(turno.getTurnoId()),
                materiaCarrera.getMateria(),
                "Examen Libre",
                cursada.getId()
        );

        log.info("Obteniendo  - Cursada ID: {}, turno ID: {}",
                cursada.getId(),turno.getTurnoId());

        Nota notaexamen =notaService.findExamenPorCursadaYTurnoId(cursada.getId(),Integer.valueOf(turno.getTurnoId()));
        notaexamen.setNotaCondicion("Examen Regular");
        notaexamen.setNotaEstado("Aprobado");
        actualizarNota(notaexamen, fila);
        notaService.guardarNota(notaexamen);

        log.info("Examen FINAL registrado correctamente.");
    }

    private void procesarEquivalencia(MateriaCarrera materiaCarrera,
                                      NotaImportDTO fila,
                                      List<NotaMateriaDTO> existentes) {

        log.info("Procesando EQUIVALENCIA para materia {}",
                materiaCarrera.getId());

        NotaMateriaDTO eq = existentes.stream()
                .filter(n -> "Equivalencia".equals(n.getNotaCondicion())
                        && n.getMateriaCarreraId().equals(materiaCarrera.getId()))
                .findFirst()
                .orElse(null);

        if (eq != null) {

            log.info("Equivalencia existente encontrada ID: {}", eq.getNotaId());

            Nota nota = notaService.obtenerNotaPorId(eq.getNotaId());
            actualizarNota(nota, fila);
            notaService.guardarNota(nota);

            log.info("Equivalencia actualizada.");

        } else {

            log.info("No existe equivalencia previa. Creando nueva nota.");

            Nota nueva = crearNota(fila, "Equivalencia");
            notaService.guardarNota(nueva);
        }
    }

    private Nota crearNota(NotaImportDTO fila, String condicion) {
        Nota nota = new Nota();
        nota.setNotaFechaNota(fila.getFecha());
        nota.setNotaLibroNota(fila.getLibro());
        nota.setNotaFolioNota(fila.getFolio());
        nota.setNotaCalificacionNotaNumero(fila.getNota());
        nota.setNotaCondicion(condicion);
        return nota;
    }

    private void actualizarNota(Nota nota, NotaImportDTO fila) {
        nota.setNotaFechaNota(fila.getFecha());
        nota.setNotaLibroNota(fila.getLibro());
        nota.setNotaFolioNota(fila.getFolio());
        nota.setNotaCalificacionNotaNumero(fila.getNota());
        nota.setNotaCalificacionNotaLetra(convertirANotaEnLetras(fila.getNota()));
    }

    /**
     * Convierte un número a nota en letra con dos decimales
     * Ejemplo: 8.5 -> "8.50"
     */
    private String convertirANotaEnLetras(Double nota) {
        if (nota == null) return null;

        int parteEntera = nota.intValue();
        int parteDecimal = (int) Math.round((nota - parteEntera) * 100);

        if (parteDecimal == 0) {
            // Nota entera
            return numeroALetras(parteEntera);
        } else {
            // Nota con decimales
            return numeroALetras(parteEntera) + " con " + numeroALetras(parteDecimal);
        }
    }

    private String numeroALetras(int numero) {
        String[] unidades = {
                "Cero", "Uno", "Dos", "Tres", "Cuatro", "Cinco",
                "Seis", "Siete", "Ocho", "Nueve", "Diez",
                "Once", "Doce", "Trece", "Catorce", "Quince",
                "Dieciséis", "Diecisiete", "Dieciocho", "Diecinueve"
        };

        String[] decenas = {
                "", "", "Veinte", "Treinta", "Cuarenta",
                "Cincuenta", "Sesenta", "Setenta", "Ochenta", "Noventa"
        };

        if (numero < 20) return unidades[numero];
        if (numero < 100) {
            int unidad = numero % 10;
            return unidad == 0 ? decenas[numero / 10] : decenas[numero / 10] + " y " + unidades[unidad].toLowerCase();
        }

        return String.valueOf(numero); // fallback si supera 99
    }
}