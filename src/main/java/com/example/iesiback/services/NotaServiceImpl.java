package com.example.iesiback.services;
import com.example.iesiback.dto.*;
import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.enums.EstadoNota;
import com.example.iesiback.enums.EstadoCondicion;
import com.example.iesiback.exception.ResourceNotFoundException;
import com.example.iesiback.repositories.NotaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class NotaServiceImpl implements NotaService {

    private final CursadaService cursadaService;
    private final NotaRepository notaRepository;

    public NotaServiceImpl(
            CursadaService cursadaService,
            NotaRepository notaRepository
    ) {
        this.cursadaService = cursadaService;
        this.notaRepository = notaRepository;
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // ⚠️ Define el formato esperado

    @Override
    public List<Nota> obtenerNotas() {
        return List.of();
    }

//    public String correlativasCursadaId(int cursadaId) {
//        Cursada cursada = cursadaService.getCursadaById(cursadaId).orElse(null);
//        return cursadaService.obtenerCorrelativasPendientes(cursada.getMateriaCarrera().getMateria().getMateriaId(), cursada.getLegajo().getLegajoId());
//    }


    public List<NotaMateriaDTO> mapResultsToDTO(List<Object[]> results) {
        List<NotaMateriaDTO> dtoList = new ArrayList<>();
        for (Object[] row : results) {
            NotaMateriaDTO dto = new NotaMateriaDTO();
//            dto.setNotaId((Integer) row[0]);
            Long id = ((Number) row[0]).longValue(); // más genérico y seguro
            dto.setNotaId(id);
            dto.setMateriaOrden((Integer) row[1]);
            dto.setMateriaNombre((String) row[2]);
            dto.setNotaCalificacionNumero((Double) row[3]);
            dto.setNotaCalificacionLetra((String) row[4]);
            dto.setNotaCondicion((EstadoCondicion) row[5]);
            dto.setNotaEstado((EstadoNota) row[6]);
            dto.setNotaLibro((String) row[7]);
            dto.setNotaFolio((String) row[8]);
            LocalDate fecha = row[9] != null ? ((java.sql.Date) row[9]).toLocalDate() : null;
            dto.setNotaFecha(fecha);
            dto.setNotaObservaciones((String) row[10]);
            dto.setNotaUsuario((String) row[11]);
            dto.setCursadaId((Integer) row[12]);
            dto.setMateriaId((String) row[13]);
            dto.setMateriaNivel((String) row[14]);
            dto.setIsfirma((Boolean) row[16]);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public static LocalDate formatearFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            return null;
        }
        List<DateTimeFormatter> formatos = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE,  // yyyy-MM-dd
                DateTimeFormatter.ofPattern("dd/MM/yyyy"), // dd/MM/yyyy
                DateTimeFormatter.ofPattern("dd-MM-yyyy")  // dd-MM-yyyy
        );
        for (DateTimeFormatter formato : formatos) {
            try {
                return LocalDate.parse(fecha, formato);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

//    @Override
//    public List<NotaMateriaDTO> obtenerTodasNotasPorLegajo(String legajoId, String condicion) {
//        List<Object[]> results = notaRepository.findNotasPorLegajo(legajoId);
//        System.out.println(results.size());
//        List<NotaMateriaDTO> resultados = mapResultsToDTO(results);
//
//        return resultados.stream()
//                .filter(Objects::nonNull) // Asegurarse de que no sea null
//                .map(obj -> {
//                    Integer cursadaId = obj.getCursadaId();
//                    List<String> correlativas = (cursadaId != null) ?
//                            // Convertir el string separado por comas en una lista de String
////                            Arrays.asList(correlativasCursadaId(cursadaId).split(",")) :
//                            Arrays.asList(evaluarCorrelativaNotaId(obj.getNotaId().toString(),condicion).split(",")) :
//                            List.of();
//                    obj.setCorrelativas(correlativas);
//                    return obj;
//                })
//                .collect(Collectors.toList());
//
//
////
////        return resultados;
//    }


@Override
public List<NotaMateriaDTO> obtenerTodasNotasPorLegajo(String legajoId, EstadoCondicion condicion) {
  List<NotaMateriaDTO> resultados = notaRepository.findNotasPorLegajo(legajoId);
//    List<Object[]> results = notaRepository.findNotasPorLegajo(legajoId);
//    List<NotaMateriaDTO> resultados = mapResultsToDTO(results);
    return resultados.stream()
            .filter(Objects::nonNull)
            .map(obj -> {
                EvaluacionCorrelativaResponse resp =
                        evaluarCorrelativaIndividual(legajoId, obj.getMateriaOrden(), EstadoCondicion.EXAMEN);//decia vacio
                obj.setCorrelativas(resp.getCorrelativasDesaprobadas());
                return obj;
            })
            .collect(Collectors.toList());
}



@Override
public List<NotaMateriaDTO> obtenerTodasNotasPorLegajoSinCorrelativas(String legajoId) {

    List<NotaMateriaDTO> results = notaRepository.findNotasPorLegajo(legajoId);

        System.out.println(results.size());

        return results;
    }













//    public List<NotaMateriaDTO> obtenerNotasNoAprobadasPorLegajo(String legajoId) {
//        List<Object[]> results = notaRepository.findNotasPorLegajo(legajoId);
//        List<NotaMateriaDTO> resultados=mapResultsToDTO(results);
//        return resultados.stream()
//                .filter(obj -> {
//                    String estadoNota = obj.getNotaEstado(); // Acceso directo al estado de la nota
//                    return !estadoNota.equalsIgnoreCase("Aprobado")
//                            && !estadoNota.equalsIgnoreCase("Cursando");
//                })
//                .map(obj -> {
//                    // 📌 Manejamos la fecha de forma más segura
//                    LocalDate fechaNota = null;
//                    try {
//                        if (obj.getNotaFecha() != null) {
//                            fechaNota = obj.getNotaFecha(); // Ya es un objeto LocalDate, no necesitamos parsear
//                        }
//                    } catch (DateTimeParseException e) {
//                        System.err.println("Error al parsear la fecha: " + obj.getNotaFecha());
//                    }
//
//                    // 📌 Obtener cursadaId correctamente
//                    Integer cursadaId = obj.getCursadaId(); // Obtención directa de cursadaId
//                    List<String> correlativas = (cursadaId != null) ? correlativasCursadaId(cursadaId) : List.of(); // Evitar null
//
//                    // Retornar el objeto NotaMateriaDTO con las correlativas
//                    obj.setCorrelativas(correlativas); // Seteamos las correlativas en el mismo objeto
//
//                    return obj;
//                }).collect(Collectors.toList());
//    }


    @Override
    public List<NotaMateriaDTO> obtenerNotasNoAprobadasCursadas(String legajoId) {
        List<NotaMateriaDTO> resultados = notaRepository.findNotasPorLegajo(legajoId);
        return resultados.stream()
                .filter(obj -> {
                    EstadoNota estadoNota = obj.getNotaEstado();
                    EstadoCondicion condicionNota = obj.getNotaCondicion();

                    return condicionNota == EstadoCondicion.CURSADA
                            && estadoNota != EstadoNota.APROBADO;
                })
                .map(obj -> {
                    // 📌 Manejamos la fecha de forma más segura
                    LocalDate fechaNota = null;
                    try {
                        if (obj.getNotaFecha() != null) {
                            fechaNota = obj.getNotaFecha(); // Ya es un objeto LocalDate, no necesitamos parsear
                        }
                    } catch (DateTimeParseException e) {
                        System.err.println("Error al parsear la fecha: " + obj.getNotaFecha());
                    }
                    // 📌 Obtener cursadaId correctamente
//                    Integer cursadaId = obj.getCursadaId(); // Obtención directa de cursadaId
//                    List<String> correlativas = (cursadaId != null) ?
//                            // Convertir el string separado por comas en una lista de String
//                            Arrays.asList(correlativasCursadaId(cursadaId).split(",")) :
//                            List.of(); // Evitar null
                    // Retornar el objeto NotaMateriaDTO con las correlativas
//                    obj.setCorrelativas(correlativas); // Seteamos las correlativas en el mismo objeto
                    return obj;
                }).collect(Collectors.toList());


    }

    @Override
    public List<NotaMateriaDTO> obtenerNotasNoAprobadasPorLegajo(String legajoId) {
        List<NotaMateriaDTO> resultados = notaRepository.findNotasPorLegajo(legajoId);
        return resultados.stream()
                .filter(obj -> {
                    EstadoNota estadoNota = obj.getNotaEstado();

                    return estadoNota != EstadoNota.APROBADO
                            && estadoNota != EstadoNota.CURSANDO;
                })
                .map(obj -> {
                    // 📌 Manejamos la fecha de forma más segura
                    LocalDate fechaNota = null;
                    try {
                        if (obj.getNotaFecha() != null) {
                            fechaNota = obj.getNotaFecha(); // Ya es un objeto LocalDate, no necesitamos parsear
                        }
                    } catch (DateTimeParseException e) {
                        System.err.println("Error al parsear la fecha: " + obj.getNotaFecha());
                    }
                    // 📌 Obtener cursadaId correctamente
//                    Integer cursadaId = obj.getCursadaId(); // Obtención directa de cursadaId
//                    List<String> correlativas = (cursadaId != null) ?
//                            // Convertir el string separado por comas en una lista de String
//                            Arrays.asList(correlativasCursadaId(cursadaId).split(",")) :
//                            List.of(); // Evitar null
//                    // Retornar el objeto NotaMateriaDTO con las correlativas
//                    obj.setCorrelativas(correlativas); // Seteamos las correlativas en el mismo objeto
                    return obj;
                }).collect(Collectors.toList());
    }

    @Override
    public boolean isMateriaAprobada(String legajoId, String materiaId) {
        List<NotaMateriaDTO> resultados = notaRepository.findNotasPorLegajo(legajoId);
        return resultados.stream()
                .anyMatch(obj -> {
                    String thisMateriaId = obj.getMateriaId();
                    EstadoNota estadoNota = obj.getNotaEstado();

                    return thisMateriaId != null
                            && thisMateriaId.equals(materiaId)
                            && estadoNota == EstadoNota.APROBADO;
                });
    }

    @Override
    public List<NotaCursadaDTO> findNotasByCarreraAndMateria(String carreraId, String materaId, String division, boolean cursadaInscripto) {
        List<NotaCursadaDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateria(carreraId, materaId, cursadaInscripto, EstadoCondicion.CURSADA,division);
        return todasLasNotas;
    }


//    @Override
//    public List<NotaCursadaDTO> findNotasByCarreraAndMateria(String carreraId, String materaId, boolean cursadaInscripto) {
//        List<NotaCursadaDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateriaNew(carreraId, materaId, "Cursada");
//        return todasLasNotas;
//    }

    @Override
    public List<NotaCursadaDTO> findNotasByCarreraAndMateriaAll(String carreraId, String materaId, String division,  boolean cursadaInscripto) {
        List<NotaCursadaDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateriaAll(carreraId, materaId, EstadoCondicion.CURSADA, division);
        return todasLasNotas;
    }


    @Override
    public List<NotaExamenDTO> findExamenesByCursadaExamenIdMateriaCarrera(
            Long cursadaExamenId, Boolean examenInscripto) {
        List<NotaExamenDTO> todosLosExamenes = obtenerNotasPorCondicionDTO(cursadaExamenId, examenInscripto);
        List<NotaExamenDTO> aux = new ArrayList<>();
        for (NotaExamenDTO dto : todosLosExamenes) {
            System.out.println("DTO: " + dto);
            String status = evaluarCorrelativaIndividual(dto.getLegajoId(), dto.getMateriaOrden(), EstadoCondicion.EXAMEN)
                    .getStatus();
            dto.setStatus(status);
            aux.add(dto);
        }
        return aux;
    }

    public List<NotaExamenDTO> obtenerNotasPorCondicionDTO(Long cursadaExamenId, boolean examenInscripto) {

        List<Map<String, Object>> rows =
                notaRepository.findExamenesByCursadaExamenIdMateriaCarrera(cursadaExamenId, examenInscripto);

        List<NotaExamenDTO> salida = new ArrayList<>();

        for (Map<String, Object> r : rows) {

            NotaExamenDTO dto = new NotaExamenDTO(
                    String.valueOf(r.get("personaDni")),
                    (String) r.get("personaNombre"),
                    (String) r.get("legajoId"),
                    (String) r.get("personaApellido"),
                    ((Number) r.get("notaId")).longValue(),
                    r.get("notaCalificacionNotaNumero") != null ? ((Number) r.get("notaCalificacionNotaNumero")).intValue() : null,
                    (String) r.get("notaCalificacionNotaLetra"),
                    (String) r.get("notaCondicion"),
                    (String) r.get("notaEstado"),
                    (String) r.get("notaLibro"),
                    (String) r.get("notaFolio"),
                    r.get("notaFechaNota") != null ? ((java.sql.Date) r.get("notaFechaNota")).toLocalDate() : null,
                    (String) r.get("notaObservaciones"),
                    (String) r.get("notaUsuario"),
                    ((Number) r.get("permisoId")).longValue(),
                    (String) r.get("status"),
                    r.get("materiaOrden") != null ? ((Number) r.get("materiaOrden")).intValue() : null
            );

            salida.add(dto);
        }

        return salida;
    }


    @Override
    public Nota guardarNota(Nota nota) {
        return notaRepository.save(nota);
    }

    @Override
    public Nota obtenerNotaPorId(Long notaId) {
        return notaRepository.findById(notaId).orElse(null);
    }

    @Override
    public Nota actualizarNota(Long id, Nota nota) throws ResourceNotFoundException {
        // Verifica si existe la nota en la base de datos
        Nota notaExistente = notaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrada para el id :: " + id));
        // Actualiza los campos de la nota existente (opcional: puedes actualizar campo por campo)
        notaExistente.setNotaCalificacionNotaNumero(nota.getNotaCalificacionNotaNumero());
        notaExistente.setNotaCalificacionNotaLetra(nota.getNotaCalificacionNotaLetra());
        notaExistente.setNotaEstado(nota.getNotaEstado());
        notaExistente.setNotaLibroNota(nota.getNotaLibroNota());
        notaExistente.setNotaFolioNota(nota.getNotaFolioNota());
        notaExistente.setNotaFechaNota(nota.getNotaFechaNota());
        notaExistente.setNotaObservaciones(nota.getNotaObservaciones());

        // Guarda la nota actualizada y la retorna
        return notaRepository.save(notaExistente);
    }


//
//    @Override
//    public List<NotaMateriaDTO> obtenerTodasNotasPorLegajoAnalitico(String legajoId) {
//        List<NotaMateriaDTO> notasOrigen = this.obtenerTodasNotasPorLegajo(legajoId);
//        List<NotaMateriaDTO> notasRefinadas = new ArrayList<>();
//        Set<Integer> materiasProcesadas = new HashSet<>(); // Almacena los órdenes de materias ya procesadas
//        boolean checkCorrelativas = true;
//        for (NotaMateriaDTO nota : notasOrigen) {
//            nota.setNotaFinal(definirNotaFinal(nota));
//            int ordenMateria = nota.getMateriaOrden(); // Suponiendo que hay un campo que indica el orden de la materia
//            if (materiasProcesadas.contains(ordenMateria)) {
//                notasRefinadas = validadorAnalitico(notasRefinadas, nota);
//            } else {
//                materiasProcesadas.add(ordenMateria);
//                notasRefinadas.add(nota);
//            }
//        }
//        if (checkCorrelativas) {
//            validarCorrelativas(notasRefinadas);
//        }
//        return notasRefinadas;
//    }

//
//    @Override
//    public List<NotaMateriaDTO> obtenerTodasNotasPorLegajoAnalitico(String legajoId) {
//        List<NotaMateriaDTO> notasOrigen = this.obtenerTodasNotasPorLegajo(legajoId,"");
//        Map<Integer, NotaMateriaDTO> materiasMap = new HashMap<>();
//        boolean checkCorrelativas = true;
//        for (NotaMateriaDTO nota : notasOrigen) {
//            nota.setNotaFinal(definirNotaFinal(nota));
//            int ordenMateria = nota.getMateriaOrden();
//            if (materiasMap.containsKey(ordenMateria)) {
//                NotaMateriaDTO mejorNota = validadorAnalitico(materiasMap.get(ordenMateria), nota);
//                materiasMap.put(ordenMateria, mejorNota);
//            } else {
//                materiasMap.put(ordenMateria, nota);
//            }
//        }
//
//        List<NotaMateriaDTO> notasRefinadas = new ArrayList<>(materiasMap.values());
//
//        if (checkCorrelativas) {
//            validarCorrelativas(notasRefinadas);
//        }
//
//        return notasRefinadas;
//    }


    @Override
    public List<NotaMateriaDTO> obtenerTodasNotasPorLegajoAnalitico(String legajoId) {

        List<NotaMateriaDTO> resultados = notaRepository.findNotasPorLegajo(legajoId);

        // 1. Deduplicar por orden, quedándose con la mejor nota
        Map<Integer, NotaMateriaDTO> materiasMap = new HashMap<>();
        for (NotaMateriaDTO obj : resultados) {
            if (obj == null) continue;

            obj.setNotaFinal(definirNotaFinal(obj)); // ← setear notaFinal antes de todo

            int orden = obj.getMateriaOrden();
            if (materiasMap.containsKey(orden)) {
                NotaMateriaDTO mejor = validadorAnalitico(materiasMap.get(orden), obj);
                materiasMap.put(orden, mejor);
            } else {
                materiasMap.put(orden, obj);
            }
        }

        // 2. Evaluar correlativas sobre la lista ya deduplicada
        return materiasMap.values().stream()
                .filter(Objects::nonNull)
                .map(obj -> {
                    EvaluacionCorrelativaResponse resp =
                            evaluarCorrelativaIndividual(legajoId, obj.getMateriaOrden(), EstadoCondicion.EXAMEN);//dfecia vbacio

                    obj.setCorrelativas(resp.getCorrelativasDesaprobadas());

                    String status = resp.getStatus();
                    EstadoCondicion condicion = obj.getNotaCondicion();

                    EstadoNota estadoNota = obj.getNotaEstado();

                    if ("Provisoria".equalsIgnoreCase(status) &&
                            estadoNota == EstadoNota.APROBADO) {

                        obj.setNotaFinal("(-)");
                    }
//                    if ("Provisoria".equalsIgnoreCase(resp.getStatus())) {
//                        if(!obj.getNotaCondicion().equals("Regular")){
//                        obj.setNotaFinal("(-)");
//                    }}
                    obj.setNotaEstado(
                            EstadoNota.valueOf(resp.getStatus().toUpperCase())
                    );
                    return obj;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<NotaMateriaDTO> obtenerTodasNotasPorLegajoAnaliticoSINREF(String legajoId) {
        List<NotaMateriaDTO> notasOrigen = this.obtenerTodasNotasPorLegajo(legajoId,EstadoCondicion.EXAMEN);
        Map<Integer, NotaMateriaDTO> materiasMap = new HashMap<>(); // Evita duplicados y almacena la mejor nota
        boolean checkCorrelativas = false;
        for (NotaMateriaDTO nota : notasOrigen) {
            nota.setNotaFinal(definirNotaFinal(nota));
            int ordenMateria = nota.getMateriaOrden();
            if (materiasMap.containsKey(ordenMateria)) {
                NotaMateriaDTO mejorNota = validadorAnalitico(materiasMap.get(ordenMateria), nota);
                materiasMap.put(ordenMateria, mejorNota);
            } else {
                materiasMap.put(ordenMateria, nota);
            }
        }
        List<NotaMateriaDTO> notasRefinadas = new ArrayList<>(materiasMap.values());
        if (checkCorrelativas) {
            validarCorrelativas(notasRefinadas);
        }
        return notasRefinadas;
    }


//    private void validarCorrelativas(List<NotaMateriaDTO> notasRefinadas) {
//        for (NotaMateriaDTO nota : notasRefinadas) {
//            System.out.println("Validando nota de la materia: " + nota.getMateriaNombre());
//
//            List<String> correlativas = nota.getCorrelativas();
//            for (String correlativa : correlativas) {
//                boolean esValido = false;
//                System.out.println("  Verificando correlativa: " + correlativa);
//
//                // Verificar si la correlativa es "Aprobadas"
//                if ("Aprobadas".equalsIgnoreCase(correlativa)) {
//                    esValido = true;
//                    System.out.println("    Correlativa es 'Aprobadas', válida automáticamente.");
//                } else {
//                    // Si no es "Aprobadas", tratar de convertirlo a número y verificar
//                    try {
//                        int correlativaInt = Integer.parseInt(correlativa);
//                        esValido = correlativas(correlativaInt, notasRefinadas, "aprobado");
//                        System.out.println("    Correlativa numérica: " + correlativaInt + " -> " + (esValido ? "aprobada" : "pendiente/invalida"));
//                    } catch (NumberFormatException e) {
//                        // Si no es un número, bloquear
//                        esValido = false;
//                        System.out.println("    Correlativa inválida (no es número ni 'Aprobadas')");
//                    }
//                }
//
//                if (!esValido) {
//                    nota.setNotaFinal("(-)"); // Se bloquea si no cumple correlativas
//                    System.out.println("  La nota final se bloquea por correlativa incumplida.");
//                    break;
//                }
//            }
//
//            if (nota.getNotaFinal() == null) {
//                System.out.println("  Todas las correlativas cumplidas. Nota final sin bloquear.");
//            }
//        }
//    }



    //llega desde modo analñitico y llega desde el certificado analitico, la


    private void validarCorrelativas(List<NotaMateriaDTO> notasRefinadas) {
        for (NotaMateriaDTO nota : notasRefinadas) {
            log.info("Validando nota de la materia: {}", nota.getMateriaNombre());
            List<String> correlativas = nota.getCorrelativas();
            for (String correlativa : correlativas) {
                boolean esValido = false;
                log.info("Verificando correlativa: {}", correlativa);
                // Verificar si la correlativa es "Aprobadas"
                if ("Ninguna".equalsIgnoreCase(correlativa)) {
                    esValido = true;
                    log.info("Correlativa es 'Aprobadas', válida automáticamente.");
                } else {
                    try {
                        int correlativaInt = Integer.parseInt(correlativa);
                        esValido = correlativas(correlativaInt, notasRefinadas, "aprobado");
                        log.info(
                                "Correlativa numérica: {} -> {}",
                                correlativaInt,
                                esValido ? "aprobada" : "pendiente/invalida"
                        );
                    } catch (NumberFormatException e) {
                        esValido = false;
                        log.warn("Correlativa inválida (no es número ni 'Aprobadas'): {}", correlativa);
                    }
                }
                if (!esValido) {
                    nota.setNotaFinal("(-)");
                    log.warn(
                            "Nota final bloqueada por correlativa incumplida. Materia: {} - Correlativa: {}",
                            nota.getMateriaNombre(),
                            correlativa
                    );
                    break;
                }
            }

            if (nota.getNotaFinal() == null) {

                log.info("Todas las correlativas cumplidas para la materia: {}", nota.getMateriaNombre());

            }
        }
    }



    private String definirNotaFinal(NotaMateriaDTO nota) {
        Set<EstadoNota> desaprobados = Set.of(
                EstadoNota.DESAPROBADO,
                EstadoNota.LIBRE,
                EstadoNota.AUSENTE
        );
        EstadoNota estado = nota.getNotaEstado();
        if (desaprobados.contains(estado)) {
            return "Desaprobado";
        }
        // Validar si notaFecha es null antes de getYear()
        int anioNota = (nota.getNotaFecha() != null)
                ? nota.getNotaFecha().getYear()
                : -1;
        int anioActual = LocalDate.now().getYear();

        if (estado == EstadoNota.CURSANDO) {
            return (anioNota == anioActual)
                    ? "Cursando"
                    : "(-)";
        }
        switch (estado) {
            case REGULAR:
                return "Regular";
            case APROBADO:
                return nota.getNotaCalificacionNumero()
                        + " ("
                        + nota.getNotaCalificacionLetra()
                        + ")";
            default:
                return "(-)";
        }
    }


    /**
     * Valida las correlativas de las materias en las notas refinadas.
     */
//    private void validarCorrelativas(List<NotaMateriaDTO> notasRefinadas) {
//        for (NotaMateriaDTO nota : notasRefinadas) {
//            List<String> correlativas = nota.getCorrelativas();
//            for (String correlativa : correlativas) {
//                if (!correlativas(Integer.parseInt(correlativa), notasRefinadas, "aprobado")) {
//                    nota.setNotaFinal("(-)"); // Se bloquea si no cumple correlativas
//                    break;
//                }
//            }
//        }
//    }

//
//    public boolean correlativas(int materia_orden, List<NotaMateriaDTO> analitico, String tipo) {
//        boolean aux = false;
//        if (analitico != null && !analitico.isEmpty() && materia_orden >= 0 && materia_orden < analitico.size()) {
//            // Asegurarse de que materia_orden no sea 0 para evitar index out of bounds
//            if (materia_orden <= 0) {
//                return aux;
//            }
//
//            // Intentar obtener el objeto NotaMateriaDTO
//            NotaMateriaDTO xd = analitico.get(materia_orden - 1);
//
//            // Comprobar si el objeto es nulo
//            if (xd != null) {
//
//                String cond = xd.getNotaEstado() != null ? xd.getNotaEstado() : "";
//                String valorString = xd.getNotaCalificacionNumero() != null ? xd.getNotaCalificacionNumero().toString() : "";
//
//                double nota;
//                try {
//                    nota = Double.parseDouble(valorString);
//                } catch (NumberFormatException e) {
//                    // Si no se puede convertir a número, mostrar el error y establecer valor por defecto
//                    nota = 1.0; // Valor por defecto en caso de error
//                }
//
//                // Evaluar en función del tipo
//                switch (tipo) {
//                    case "regular":
//                        System.out.println("Evaluando tipo: regular");
//                        if (nota >= 4) {
//                            System.out.println("Correlativa aceptada, materia_orden: " + materia_orden + ", Nota: " + nota);
//                            aux = true;
//                        } else {
//                            System.out.println("Correlativa rechazada, materia_orden: " + materia_orden + ", Nota: " + nota);
//                        }
//                        break;
//                    case "aprobado":
//                        System.out.println("Evaluando tipo: aprobado");
//                        if (nota >= 4 && cond.equals("Aprobado")) {
//                            aux = true;
//                            System.out.println("Correlativa aprobada, materia_orden: " + materia_orden + ", Nota: " + nota);
//                        } else {
//                            System.out.println("Correlativa desaprobada, materia_orden: " + materia_orden + ", Nota: " + nota + ", Estado: " + cond);
//                        }
//                        break;
//                    default:
//                        System.out.println("Tipo no reconocido: " + tipo);
//                        break;
//                }
//            } else {
//                System.out.println("El objeto xd no tiene la estructura esperada.");
//            }
//        } else {
//            System.out.println("Los datos de entrada no son válidos.");
//            System.out.println("Condiciones: analitico = " + analitico + ", materia_orden = " + materia_orden);
//        }
//        return aux;
//    }


    public boolean correlativas(int materiaOrden, List<NotaMateriaDTO> analitico, String tipo) {
        boolean aux = false;
        if (analitico != null && !analitico.isEmpty() && materiaOrden >= 0 && materiaOrden < analitico.size()) {
            if (materiaOrden <= 0) {
                log.warn("materiaOrden inválido: {}", materiaOrden);
                return aux;
            }
            NotaMateriaDTO xd = analitico.get(materiaOrden - 1);
            if (xd != null) {
                String cond = xd.getNotaEstado() != null
                        ? xd.getNotaEstado().name()
                        : "";
                String valorString = xd.getNotaCalificacionNumero() != null
                        ? xd.getNotaCalificacionNumero().toString()
                        : "";
                double nota;
                try {
                    nota = Double.parseDouble(valorString);
                } catch (NumberFormatException e) {
                    log.warn("No se pudo convertir la nota a número. Valor recibido: {}", valorString);
                    nota = 1.0;
                }
                switch (tipo) {
                    case "regular":
                        log.info("Evaluando correlativa tipo REGULAR - materiaOrden: {}", materiaOrden);
                        if (nota >= 4) {
                            log.info("Correlativa aceptada - materiaOrden: {}, Nota: {}", materiaOrden, nota);
                            aux = true;
                        } else {
                            log.info("Correlativa rechazada - materiaOrden: {}, Nota: {}", materiaOrden, nota);
                        }
                        break;
                    case "aprobado":
                        log.info("Evaluando correlativa tipo APROBADO - materiaOrden: {}", materiaOrden);
                        if (nota >= 4 && "Aprobado".equals(cond)) {
                            aux = true;
                            log.info("Correlativa aprobada - materiaOrden: {}, Nota: {}", materiaOrden, nota);
                        } else {
                            log.info(
                                    "Correlativa desaprobada - materiaOrden: {}, Nota: {}, Estado: {}",
                                    materiaOrden,
                                    nota,
                                    cond
                            );
                        }
                        break;
                    default:
                        log.warn("Tipo de correlativa no reconocido: {}", tipo);
                        break;
                }

            } else {
                log.warn("El objeto NotaMateriaDTO es null para materiaOrden: {}", materiaOrden);
            }
        } else {
            log.warn("Datos de entrada inválidos. analitico: {}, materiaOrden: {}",
                    analitico,
                    materiaOrden
            );
        }
        return aux;
    }


    public boolean buscarClaveAnalitico(List<NotaMateriaDTO> analitico, NotaMateriaDTO materia) {
        return analitico.stream()
                .anyMatch(xd -> Objects.equals(xd.getMateriaNombre(), materia.getMateriaNombre()));
    }

//    public List<NotaMateriaDTO> validadorAnalitico(List<NotaMateriaDTO> analitico, NotaMateriaDTO materia) {
//        List<NotaMateriaDTO> nuevalista = new ArrayList<>();
//        System.out.println(".-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.");
//        for (NotaMateriaDTO xd : analitico) {
//            System.out.println("_________________________________________________________________________");
//           // System.out.println("materias son " + xd.getMateriaNombre() + "|" + materia.getNombre());
//
//            if (xd.getMateriaNombre().equals(materia.getMateriaNombre())) {
//                // Si las materias se repiten
//                String cond1 = xd.getNotaEstado();
//                String cond2 = materia.getNotaEstado();
//                System.out.println("condicion 1: " + cond1 + " | condicion 2: " + cond2);
//
//                if (cond1.equals("Aprobado")) {
//                    // Si la primera materia está aprobada, no hacer nada y conservar su estado
//                    System.out.println("La primera materia está aprobada, se conserva");
//                    nuevalista.add(xd);
//                } else if (cond2.equals("Aprobado")) {
//                    // Si la segunda materia está aprobada, actualizar y conservar su estado
//                    System.out.println("La segunda materia está aprobada, se reemplaza");
//                    nuevalista.add(materia);
//                } else if (cond1.equals("Cursando") && (cond2.equals("Regular") || cond2.equals("Libre") || cond2.equals("Desaprobado") || cond2.equals("Ausente"))) {
//                    // La primera materia está cursando y la segunda está en estado regular o libre, actualizar a cursando
//                    System.out.println("La primera materia está cursando y la segunda está en estado regular o libre, se conserva");
//                    nuevalista.add(xd);
//                } else if (cond1.equals("Regular") && cond2.equals("Regular")) {
//                    // Ambas materias están regulares, actualizar a la última
//                    System.out.println("Ambas materias están regulares, se reemplaza");
//                    nuevalista.add(materia);
//                } else if (cond1.equals("Desaprobado") && cond2.equals("Desaprobado")) {
//                    // Ambas materias están desaprobadas, actualizar a la última
//                    System.out.println("Ambas materias están desaprobadas, se reemplaza");
//                    nuevalista.add(materia);
//                } else if (cond1.equals("Regular") && cond2.equals("Desaprobado")) {
//                    // La primera materia está en estado regular y la segunda está desaprobada, conservar el estado de la primera
//                    System.out.println("La primera materia está regular y la segunda está desaprobada, se conserva");
//                    nuevalista.add(xd);
//                } else if (cond1.equals("Regular") && cond2.equals("Cursando")) {
//                    // La primera materia está en estado regular y la segunda está cursando, conservar el estado de la primera
//                    System.out.println("La primera materia está regular y la segunda está cursando, se conserva");
//                    nuevalista.add(xd);
//                } else if (cond1.equals("Regular") && cond2.equals("Ausente")) {
//                    // La primera materia está en estado regular y la segunda está cursando, conservar el estado de la primera
//                    System.out.println("La primera materia está regular y la segunda está Ausente, se conserva");
//                    nuevalista.add(xd);
//                } else if (cond1.equals("Desaprobado") && cond2.equals("Regular")) {
//                    // La primera materia está desaprobada y la segunda está en estado regular, actualizar a regular
//                    System.out.println("La primera materia está desaprobada y la segunda está regular, se reemplaza");
//                    nuevalista.add(materia);
//                } else if (cond1.equals("Desaprobado") && cond2.equals("Cursando")) {
//                    // La primera materia está desaprobada y la segunda está cursando, conservar el estado de la primera
//                    System.out.println("La primera materia está desaprobada y la segunda está cursando, se reemplaza");
//                    nuevalista.add(materia);
//                } else if (cond1.equals("Libre") && cond2.equals("Cursando")) {
//                    // La primera materia está libre y la segunda está cursando, conservar el estado de la primera
//                    System.out.println("La primera materia está libre y la segunda está cursando, se reemplaza");
//                    nuevalista.add(materia);
//                } else if (cond1.equals("Libre") && cond2.equals("Ausente")) {
//                    // La primera materia está libre y la segunda está cursando, conservar el estado de la primera
//                    System.out.println("La primera materia está libre y la segunda está Ausente, se conserva");
//                    nuevalista.add(xd);
//                } else if (cond1.equals("Ausente") && cond2.equals("Libre")) {
//                    // La primera materia está libre y la segunda está cursando, conservar el estado de la primera
//                    System.out.println("La primera materia está Ausente y la segunda está Libre, se reemplaza");
//                    nuevalista.add(materia);
//                } else if (cond1.equals("Ausente") && cond2.equals("Regular")) {
//                    // La primera materia está libre y la segunda está cursando, conservar el estado de la primera
//                    System.out.println("La primera materia está Ausente y la segunda está Libre, se reemplaza");
//                    nuevalista.add(materia);
//                } else {
//                    // Agregar cualquier otra combinación de estados
//                    System.out.println("Combinación de estados no contemplada, se conserva");
//                    nuevalista.add(xd);
//                }
//            } else {
//                nuevalista.add(xd);
//            }
//        }
//
//        return nuevalista;
//    }


//    public NotaMateriaDTO validadorAnalitico(NotaMateriaDTO nota1, NotaMateriaDTO nota2) {
//        String cond1 = nota1.getNotaEstado();
//        String cond2 = nota2.getNotaEstado();
//
//        // Dar prioridad a la materia aprobada
//        if (cond1.equals("Aprobado")) return nota1;
//        if (cond2.equals("Aprobado")) return nota2;
//
//        // Si ambas están en estado regular, quedarse con la última
//        if (cond1.equals("Regular") && cond2.equals("Regular")) return nota2;
//
//        // Si una es Regular y la otra Desaprobado, quedarse con la Regular
//        if (cond1.equals("Regular") && cond2.equals("Desaprobado")) return nota1;
//        if (cond1.equals("Desaprobado") && cond2.equals("Regular")) return nota2;
//
//        // Si una es Regular y la otra Cursando, quedarse con la Regular
//        if (cond1.equals("Regular") && cond2.equals("Cursando")) return nota1;
//        if (cond1.equals("Cursando") && cond2.equals("Regular")) return nota2;
//
//        // Si una es Libre y la otra Cursando, quedarse con la Cursando
//        if (cond1.equals("Libre") && cond2.equals("Cursando")) return nota2;
//        if (cond1.equals("Cursando") && cond2.equals("Libre")) return nota1;
//
//        // Si una es Ausente y la otra tiene otro estado, quedarse con el otro estado
//        if (cond1.equals("Ausente")) return nota2;
//        if (cond2.equals("Ausente")) return nota1;
//
//        // En cualquier otro caso, quedarse con la última
//        return nota2;
//    }

    public NotaMateriaDTO validadorAnalitico(NotaMateriaDTO nota1, NotaMateriaDTO nota2) {
        if (nota1 == null || nota2 == null) {
            throw new IllegalArgumentException("Las notas no pueden ser nulas");
        }

        // NORMALIZAMOS ANTES DE TODO
        String cond1 = normalizarEstado(nota1.getNotaEstado());
        String cond2 = normalizarEstado(nota2.getNotaEstado());
        System.out.println("Estado recibido cond1: " + cond1);
        System.out.println("Estado recibido cond2: " + cond2);

        // VALIDAMOS
        if (!esEstadoValido(cond1) || !esEstadoValido(cond2)) {
            throw new IllegalArgumentException("Estado de la nota no válido");
        }

        // PRIORIDADES
        int prioridad1 = obtenerPrioridad(cond1);
        int prioridad2 = obtenerPrioridad(cond2);

        if (prioridad1 < prioridad2) {
            return nota1;
        }
        if (prioridad2 < prioridad1) {
            return nota2;
        }
        return nota2;
    }


    private String normalizarEstado(EstadoNota estado) {

        if (estado == null) {
            return "Pendiente";
        }

        return switch (estado) {
            case APROBADO -> "Aprobado";
            case REGULAR -> "Regular";
            case CURSANDO -> "Cursando";
            case DESAPROBADO -> "Desaprobado";
            case LIBRE -> "Libre";
            case AUSENTE -> "Ausente";
            case PENDIENTE -> "Pendiente";
        };
    }

    // Método para validar si el estado es uno de los valores esperados
//    private boolean esEstadoValido(String estado) {
//        return estado.equals("Aprobado") || estado.equals("Regular") || estado.equals("Cursando") ||
//               estado.equals("Desaprobado") || estado.equals("Ausente") || estado.equals("Pendiente") || estado.equals("Libre");
//    }


    private boolean esEstadoValido(String estado) {
        return estado.equals("Aprobado") ||
                estado.equals("Regular") ||
                estado.equals("Cursando") ||
                estado.equals("Desaprobado") ||
                estado.equals("Ausente") ||
                estado.equals("Pendiente") ||
                estado.equals("Libre");
    }


    // Método para obtener la prioridad del estado (cuanto menor es el número, más alta es la prioridad)
    private int obtenerPrioridad(String estado) {
        switch (estado) {
            case "Aprobado":
                return 1;
            case "Regular":
                return 2;
            case "Cursando":
                return 3;
            case "Desaprobado":
                return 4;
            case "Ausente":
                return 5;
            case "Pendiente":
                return 6;
            case "Libre":  // Agregar "Libre" aquí
                return 7;
            default:
                throw new IllegalArgumentException("Estado no reconocido: " + estado);
        }
    }

//
//    @Override
//    public List<NotaExamenDTO> obtenerNotasPorCondicion(Long cursadaExamenId, boolean examenInscripto, String notaCondicion) {
//
//
//
//        return notaRepository.findExamenesByCursadaExamenIdMateriaCarrera(cursadaExamenId, examenInscripto, notaCondicion);
//    }


    @Override
    public List<NotaExamenDTO> obtenerNotasPorCondicion(Long cursadaExamenId, boolean examenInscripto, EstadoCondicion notaCondicion) {
        List<NotaExamenDTO> aux = new ArrayList<>();
        List<NotaExamenDTO> lista =
                findExamenesByCursadaExamenIdMateriaCarrera(cursadaExamenId, examenInscripto, notaCondicion);

        // Iteramos la lista y procesamos cada item
        for (NotaExamenDTO dto : lista) {
            String status = evaluarCorrelativaIndividual(dto.getLegajoId(), dto.getMateriaOrden(),EstadoCondicion.EXAMEN).getStatus();
            if (status.equals("Aceptada")) {
                aux.add(dto);
            }
        }
        return aux;
    }

    public List<NotaExamenDTO> findExamenesByCursadaExamenIdMateriaCarrera(
            Long cursadaExamenId,
            boolean examenInscripto,
            EstadoCondicion notaCondicion
    ) {

        List<Object[]> rows = notaRepository.findExamenesRaw(cursadaExamenId, examenInscripto, notaCondicion);

        List<NotaExamenDTO> lista = new ArrayList<>();

        for (Object[] r : rows) {

            NotaExamenDTO dto = new NotaExamenDTO(
                    r[3].toString(),       // personaDni
                    (String) r[5],       // personaNombre
                    r[2].toString(),     // legajoId (Long → String)
                    (String) r[4],       // personaApellido
                    ((Number) r[0]).longValue(),   // notaId
                    r[6] != null ? ((Number) r[6]).intValue() : null,
                    (String) r[7],       // calificacion letra
                    (String) r[8],       // nota condicion
                    (String) r[9],       // nota estado
                    (String) r[10],      // libro
                    (String) r[11],      // folio
                    (r[12] != null ? ((java.sql.Date) r[12]).toLocalDate() : null), // fecha
                    (String) r[13],      // observaciones
                    (String) r[15],      // usuario
                    ((Number) r[1]).longValue(), // permisoId
                    (String) r[14],      // status examen
                    (Integer) r[16]      // materiaOrden
            );
            lista.add(dto);
        }

        return lista;
    }


    @Transactional
    @Override
    public void eliminarNota(Long id) {
        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota no encontrada con ID: " + id));
           Long cursadaId = Long.valueOf(nota.getCursada().getId()); // Guardamos el ID de la cursada antes de eliminar la nota
        // Eliminar la nota
        notaRepository.delete(nota);
        // Verificar si hay otras notas con la misma cursada_id
        int countNotas = Math.toIntExact(notaRepository.countByCursadaId(Math.toIntExact(cursadaId)));
        if (countNotas == 0) {
            // Si no hay más notas asociadas, eliminar la cursada
            cursadaService.eliminarCursada(Math.toIntExact(cursadaId));
        }
    }


    @Transactional
    @Override
    public void eliminarNotaIndividual(Long id) {
        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota no encontrada con ID: " + id));
        notaRepository.delete(nota);
    }


    @Override
    public NotaMateriaDTO obtenerUltimaNota(String legajoId) {
        List<NotaMateriaDTO> todas = notaRepository.findUltimaNotaPorLegajo(legajoId);
        return todas.isEmpty() ? null : todas.get(0); // Solo la más reciente
    }



    @Override
    public Cursada obtenerCursadaPorNotaId(Long notaId) {
        return notaRepository.findCursadaByNotaId(notaId);
    }

    //evalua columna regularizada para cursar;
    //requiere que el alumno ya este matriculado!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @Override
    public EvaluacionCorrelativaResponse evaluarCorrelativaIndividual(String legajoId, Integer materiaOrden, EstadoCondicion condicion) {

        List<NotaCursandoProjection> notas = notaRepository.findNotaCursandoByLegajoAndMateria(legajoId, materiaOrden);
        if (notas == null || notas.isEmpty()) {
            System.out.println("❌ No se encontró la materia para este alumno.-" + legajoId + "-" + materiaOrden);
            List<String> correlativas = new ArrayList<>();
            correlativas.add("Ninguna");
            List<String> fechas = new ArrayList<>();
            fechas.add("coherente");
            return new EvaluacionCorrelativaResponse("-", correlativas, fechas);
        }
        Optional<NotaCursandoProjection> optNotaSeleccionada = notas.stream()
                .filter(n -> n.getNotaFechaNota() != null)
                .max(Comparator.comparing(NotaCursandoProjection::getNotaFechaNota));
        NotaCursandoProjection notaSeleccionada = optNotaSeleccionada.orElseThrow(() ->
                new RuntimeException("No se encontró ninguna nota con fecha válida.")
        );
        System.out.println("✅ Nota selecionada: " +
                notaSeleccionada.getMateriaNombre() +
                "-" + notaSeleccionada.getMateriaOrden() +"-"
            + notaSeleccionada.getNotaCondicion());
        String listaCorrelativas;

        switch (notaSeleccionada.getNotaCondicion()) {
            case CURSADA:
                listaCorrelativas = notaSeleccionada.getMateriaCursada();
                break;
            case EXAMEN_LIBRE:
            case EXAMEN_REGULAR:
            case EXAMEN:
                listaCorrelativas = notaSeleccionada.getMateriaExamen();
                break;

            case EQUIVALENCIA:
                listaCorrelativas = "-";
                break;

            default:
                listaCorrelativas = "-";
                break;
        }
        if (listaCorrelativas.equals("-")) {
            System.out.println("✅ No tiene correlativas, se acepta directamente.");
            List<String> correlativas = new ArrayList<>();
            correlativas.add("Ninguna");
            List<String> fechas = new ArrayList<>();
            fechas.add("No");
            return new EvaluacionCorrelativaResponse("Aceptada", correlativas, fechas);
        } else {
            // Procesar correlativas
            System.out.println("---------------------------------------------------------------------------------------");
            System.out.println("⚙️ Procesando correlativas para la materia: " + notaSeleccionada.getMateriaCursada());
            return procesarCorrelativasIndividual(notaSeleccionada, listaCorrelativas);
        }
    }


    @Override
    public EvaluacionCorrelativaResponse evaluarCorrelativaNotaId(Long  notaId, EstadoCondicion condicion) {
        List<NotaCursandoProjection> notas = notaRepository.findNotaNotaCursandoProjectionbyNotyaId(notaId);
        if (notas == null || notas.isEmpty()) {
            System.out.println("❌ No se encontró la materia para este materiaId.-" + notaId);
            List<String> correlativas = new ArrayList<>();
            correlativas.add("Ninguna");
            List<String> fechas = new ArrayList<>();
            fechas.add("coherente");
            return new EvaluacionCorrelativaResponse("-", correlativas, fechas);
        }
        Optional<NotaCursandoProjection> optNotaSeleccionada = notas.stream()
                .filter(n -> n.getNotaFechaNota() != null)
                .max(Comparator.comparing(NotaCursandoProjection::getNotaFechaNota));
        NotaCursandoProjection notaSeleccionada = optNotaSeleccionada.orElseThrow(() ->
                new RuntimeException("No se encontró ninguna nota con fecha válida.")
        );
        System.out.println("✅ Nota selecionada: " +
                notaSeleccionada.getMateriaNombre() +
                "-" + notaSeleccionada.getMateriaOrden() +"-"
                + notaSeleccionada.getNotaCondicion());
        String listaCorrelativas;

        switch (condicion) {
            case CURSADA:
                listaCorrelativas = notaSeleccionada.getMateriaCursada();
                break;

            case EXAMEN_LIBRE:
            case EXAMEN_REGULAR:
            case EXAMEN:
                listaCorrelativas = notaSeleccionada.getMateriaExamen();
                break;

            case EQUIVALENCIA:
                listaCorrelativas = "-";
                break;

            default:
                listaCorrelativas = "-";
                break;
        }
        if (listaCorrelativas.equals("-")) {
            System.out.println("✅ No tiene correlativas, se acepta directamente.");
            List<String> correlativas = new ArrayList<>();
            correlativas.add("Ninguna");
            List<String> fechas = new ArrayList<>();
            fechas.add("No");
            return new EvaluacionCorrelativaResponse("Aceptada", correlativas, fechas);
        } else {
            // Procesar correlativas
            System.out.println("⚙️ Procesando correlativas para la materia: " + notaSeleccionada.getMateriaCursada());
            return procesarCorrelativasIndividual(notaSeleccionada, listaCorrelativas);
        }
    }

    private EvaluacionCorrelativaResponse procesarCorrelativasIndividual(NotaCursandoProjection nota, String listaCorrelativas) {
        String[] correlativas = listaCorrelativas.split("-");
        System.out.println("🔗 Correlativas a evaluar: " + Arrays.toString(correlativas));
        List<String> materiasDesaprobadas = new ArrayList<>();
        List<String> materiasConFechaInvalida = new ArrayList<>();
        boolean tieneDesaprobadas = false;
        boolean tieneFechaIncoherente = false;
        List<NotaCursandoProjection> notasDelAlumno = notaRepository.findAllNotasByLegajo(nota.getLegajoId());
        System.out.println("📚 Notas del alumno: " + notasDelAlumno.size());

        Map<String, List<NotaCursandoProjection>> notasMap = notasDelAlumno.stream()
                .collect(Collectors.groupingBy(n -> String.valueOf(n.getMateriaOrden())));
        for (String numero : correlativas) {
            if (numero == null || numero.trim().isEmpty()) {
                continue; // Saltar correlativas vacías
            }
            numero = numero.trim();
            List<NotaCursandoProjection> correlativasNotas = notasMap.get(numero);
            System.out.println("➡️ Evaluando correlativa: " + numero);
            if (correlativasNotas != null && !correlativasNotas.isEmpty()) {
                boolean correlativaAprobadaValida = false;
                for (NotaCursandoProjection correlativa : correlativasNotas) {
                    System.out.println("🔍 Analizando correlativa: " + correlativa.getMateriaNombre());
                    boolean estaAprobada = correlativa.getNotaEstado() == EstadoNota.APROBADO;
                    if (correlativa.getNotaFechaNota() == null || nota.getNotaFechaNota() == null) {
                        System.out.println("⚠️ Una de las fechas es nula. Se considera fecha incoherente.");
                        tieneFechaIncoherente = true;
                        materiasConFechaInvalida.add(numero + " (fecha nula)");
                        continue;
                    }
//                    boolean fechaCoherente = correlativa.getNotaFechaNota().isBefore(nota.getNotaFechaNota())
//                            || correlativa.getNotaFechaNota().isEqual(nota.getNotaFechaNota());
//                    System.out.println("📌 Estado: " + correlativa.getNotaEstado() + ", Fecha coherente: " + fechaCoherente);
//
//                    if(!fechaCoherente){
//                        materiasConFechaInvalida.add(numero + " (fecha adelantada)");
//                    }


                    //  if (estaAprobada && fechaCoherente) {
                    if (estaAprobada) {
                        correlativaAprobadaValida = true;
                        System.out.println("✅ Correlativa válida encontrada para: " + numero);
                        boolean fechaCoherente = correlativa.getNotaFechaNota().isBefore(nota.getNotaFechaNota())
                                || correlativa.getNotaFechaNota().isEqual(nota.getNotaFechaNota());
                        System.out.println("📌 Estado: " + correlativa.getNotaEstado() + ", Fecha coherente: " + fechaCoherente);

                        if (!fechaCoherente) {
                            materiasConFechaInvalida.add(numero + " (fecha adelantada)");
                        }
                        break; // Ya encontramos una válida, no seguimos buscando
                    }
                }
                if (!correlativaAprobadaValida) {
                    System.out.println("❌ No se encontró correlativa válida para: " + numero);
                    tieneDesaprobadas = true;
                    materiasDesaprobadas.add(numero);
                }
            } else {
                System.out.println("⚠️ No se encontró nota para la correlativa: " + numero);
                tieneDesaprobadas = true;
                materiasDesaprobadas.add(numero);
            }
        }
        String status;
        if (tieneDesaprobadas) {
            status = "Provisoria";
        } else if (tieneFechaIncoherente) {
            status = "Aceptada"; // Puede ajustarse a tu lógica si querés un estado diferente
        } else {
            status = "Aceptada";
        }

        if (materiasDesaprobadas.isEmpty()) {
            materiasDesaprobadas.add("Ninguna");
        }
        if (materiasConFechaInvalida.isEmpty()) {
            materiasConFechaInvalida.add("No");
        }

        System.out.println("✅ Resultado final: " + status);
        System.out.println("❌ Materias desaprobadas: " + materiasDesaprobadas);
        System.out.println("📅 Materias con fecha inválida: " + materiasConFechaInvalida);
        return new EvaluacionCorrelativaResponse(status, materiasDesaprobadas, materiasConFechaInvalida);
    }


    @Override
    public void permitirEdicionMateria(String carreraId, String materiaId, boolean editable, String division) {
        List<NotaCursadaDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateriaAll(carreraId, materiaId, EstadoCondicion.CURSADA,division);
        for (NotaCursadaDTO n : todasLasNotas) {
            Optional<Nota> notaOpt = notaRepository.findById(n.getNotaId());
            if (notaOpt.isPresent()) {
                Nota nota = notaOpt.get();
                nota.setEditable(editable); // Suponiendo que Nota tiene este campo
                notaRepository.save(nota);  // Persistir el cambio
            }
        }
    }

    @Override
    public Nota saveNotaWithCursadsa(Nota nota, Integer cursadaId) {
        Cursada cursada = cursadaService.getCursadaById(cursadaId).get();
        nota.setCursada(cursada);
        return notaRepository.save(nota);
    }



    @Override
    public ResultadoRegularidad evaluarRegularidad(List<Nota> notas) {

        Nota cursada = null;
        List<Nota> examenes = new ArrayList<>();

        for (Nota nota : notas) {
            if (nota.getNotaCondicion() == EstadoCondicion.CURSADA) {
                cursada = nota;
            } else if (
                    nota.getNotaCondicion() == EstadoCondicion.EXAMEN_REGULAR ||
                            nota.getNotaCondicion() == EstadoCondicion.EXAMEN_LIBRE ||
                            nota.getNotaCondicion() == EstadoCondicion.EXAMEN
            ) {
                examenes.add(nota);
            }
        }

        if (cursada == null) {
            return new ResultadoRegularidad("Libre", "No se encontró registro de cursada.");
        }

        if (cursada.getNotaEstado() == EstadoNota.LIBRE) {
            return new ResultadoRegularidad("Libre", "La cursada tiene estado 'Libre'.");
        }

        LocalDate fechaCursada = cursada.getNotaFechaNota();
        long anios = ChronoUnit.YEARS.between(fechaCursada, LocalDate.now());

        if (anios > 3) {
            return new ResultadoRegularidad("Libre",
                    String.format("Cursada vencida: fue el %s, pasaron %d años (máximo: 3).", fechaCursada, anios));
        }

        boolean aprobado = examenes.stream()
                .anyMatch(e -> e.getNotaEstado() == EstadoNota.APROBADO);

        if (aprobado) {
            return new ResultadoRegularidad("Regular", "Examen final aprobado.");
        }

        long desaprobados = examenes.stream()
                .filter(e -> e.getNotaEstado() == EstadoNota.DESAPROBADO)
                .count();

        long pendientes = examenes.stream()
                .filter(e -> e.getNotaEstado() == EstadoNota.PENDIENTE)
                .count();

        long totalIntentos = desaprobados + pendientes;

        if (totalIntentos >= 3) {
            return new ResultadoRegularidad("Libre",
                    String.format("Agotó los 3 intentos. Desaprobados: %d, Pendientes: %d. Debe recursar.",
                            desaprobados, pendientes));
        }

        long restantes = 3 - totalIntentos;

        return new ResultadoRegularidad("Regular",
                String.format("Regular vigente. Cursada: %s. Intentos usados: %d/3 (%d restantes, %d pendiente/s de nota). Vence: %s.",
                        fechaCursada, totalIntentos, restantes, pendientes, fechaCursada.plusYears(3)));
    }

    record ResultadoRegularidad(String condicion, String justificacion) {}



        @Override
        public List<AlumnoCursadaMateriaNotaDTO> getAlumnosPorCarrera(String carreraId) {
            List<AlumnoCursadaMateriaNotaDTO> alumnos = notaRepository.findAlumnosPorCarrera(carreraId);
        for (AlumnoCursadaMateriaNotaDTO alumno : alumnos) {
            List<NotaMateriaDTO> notas = notaRepository.findBylegajoId(alumno.getLegajoId());
            alumno.setNotaMateriaDTO(notas);
        }
            return alumnos;
        }


    @Transactional(readOnly = true)
    @Override
    public Nota findByCursadaId(Integer cursadaId) {

        return notaRepository.findByCursadaId(cursadaId)
                .orElseThrow(() ->
                        new RuntimeException("Nota no encontrada para la cursada ID: " + cursadaId));
    }

    @Override
    public Nota obtenerExamenPorCursadaYPermiso(Integer cursadaId, Integer permisoId) {

        Optional<Nota> optionalNota =
                notaRepository.findExamenPorCursadaYPermiso(cursadaId, permisoId);

        if (optionalNota.isPresent()) {
            Nota nota = optionalNota.get();
            log.info("Examen encontrado: ID {}", nota.getNotaId());
            return nota;
        }

        log.info("No se encontró examen para cursadaId {} y permisoId {}",
                cursadaId, permisoId);

        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Nota findByCursadaIdMateriaCondicion(Integer cursadaId, Integer permisoId) {

        return notaRepository.findExamenPorCursadaYPermiso(cursadaId, permisoId)
                .orElseThrow(() ->
                        new RuntimeException("Nota no encontrada para la cursada ID: " + cursadaId
                                + " y permiso: " + permisoId));
    }




    @Override
    public Nota findExamenPorCursadaYTurnoId(Integer cursadaId, Integer turnoId) {

        Optional<Nota> optionalNota =
                notaRepository.findExamenPorCursadaYTurnoId(cursadaId, turnoId);

        if (optionalNota.isPresent()) {
            Nota nota = optionalNota.get();
            log.info("Examen encontrado: ID {}", nota.getNotaId());
            return nota;
        }

        log.info("No se encontró examen para cursadaId {} y permisoId {}",
                cursadaId, turnoId);

        return null;
    }


    @Override
    public boolean evaluarSancion(Integer cursadaId, Integer turnoId) {

        // Si no hay turno anterior, no puede haber sanción
        if (turnoId == null || turnoId <= 1) {
            return false;
        }

        Nota notaAnterior = findExamenPorCursadaYTurnoId(cursadaId, turnoId - 1);

        // Si no existe nota anterior, no hay sanción
        if (notaAnterior == null) {
            return false;
        }

        Double calificacion = notaAnterior.getNotaCalificacionNotaNumero();

        // Si la nota del turno anterior fue 0 → ausente → sanción
        return calificacion != null && calificacion == 0;
    }
}
