package com.example.iesiback.services;
import com.example.iesiback.dto.*;
import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Materia;
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
    private final MateriaService materiaService;
    private final InscripcionService inscripcionService;
    private final CorrelativaService correlativaService;


    public NotaServiceImpl(
            CursadaService cursadaService,
            NotaRepository notaRepository, MateriaService materiaService, InscripcionService inscripcionService, CorrelativaService correlativaService
    ) {
        this.cursadaService = cursadaService;
        this.notaRepository = notaRepository;
        this.materiaService = materiaService;
        this.inscripcionService = inscripcionService;
        this.correlativaService = correlativaService;
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
public List<NotaCursadaConEstadoDTO> findNotasByCarreraAndMateria(
        String carreraId, String materiaId, String division, boolean cursadaInscripto) {

    List<NotaCursadaDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateria(
            carreraId, materiaId, cursadaInscripto, EstadoCondicion.CURSADA, division);
    Materia mnateria= materiaService.findMateriaById(materiaId);

  int ordenMateria =mnateria.getMateriaOrden();

    List<NotaMateriaDTO> materiaWrapper = List.of(
            new NotaMateriaDTO() {{
                setMateriaOrden(ordenMateria);
                setMateriaId(materiaId); // <--- Agrega esta línea
            }}
    );

    Map<String, List<NotaCursadaDTO>> porLegajo = todasLasNotas.stream()
            .collect(Collectors.groupingBy(NotaCursadaDTO::getPersonaLegajoId));

    List<NotaCursadaConEstadoDTO> resultado = new ArrayList<>();

    for (Map.Entry<String, List<NotaCursadaDTO>> entry : porLegajo.entrySet()) {
        String legajoId = entry.getKey();

        // Una sola query al repo por alumno — evalúa CURSADA y EXAMEN juntos
        Map<Integer, CorrelativaService.VeredictoDoble> veredictos =
                correlativaService.evaluarLoteDoble(legajoId, materiaWrapper);

        CorrelativaService.VeredictoDoble vd = veredictos.get(ordenMateria);

        if (vd == null || vd.rechazado()) {
            log.debug("Omitido por correlativas de cursada. Legajo={} Materia={}",
                    legajoId, ordenMateria);
            continue;
        }

        entry.getValue().forEach(nota ->
                resultado.add(new NotaCursadaConEstadoDTO(nota, vd.estado()))
        );
    }

    // Al final antes del return, reordenar por apellido y nombre
    resultado.sort(Comparator.comparing(NotaCursadaConEstadoDTO::getPersonaApellido)
            .thenComparing(NotaCursadaConEstadoDTO::getPersonaNombre));

    return resultado;
}

@Override
public List<NotaMateriaDTO> obtenerTodasNotasPorLegajo(String legajoId, EstadoCondicion condicion) {
    List<NotaMateriaDTO> resultados = notaRepository.findNotasPorLegajo(legajoId)
            .stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    // Una sola query para todas las correlativas del legajo
    Map<Integer, CorrelativaService.Veredicto> veredictos =
            correlativaService.evaluarLote(legajoId, resultados, condicion); // ← usa condicion real

    resultados.forEach(obj -> {
        CorrelativaService.Veredicto veredicto = veredictos.get(obj.getMateriaOrden());
        if (veredicto != null) {
            obj.setCorrelativas(veredicto.desaprobadas());
            obj.setNotaStatus(veredicto.toLegacy().getStatus());
            obj.setNotaIsFecha(veredicto.conFechaIncoherente());
        }
    });

    return resultados;
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

//    @Override
//    public List<NotaCursadaDTO> findNotasByCarreraAndMateria(String carreraId, String materaId, String division, boolean cursadaInscripto) {
//        List<NotaCursadaDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateria(carreraId, materaId, cursadaInscripto, EstadoCondicion.CURSADA,division);
//        return todasLasNotas;
//    }



    @Override
    public List<NotaCursadaConEstadoDTO> findNotasByCarreraAndMateriaAll(String carreraId, String materaId, String division,  boolean cursadaInscripto) {
        List<NotaCursadaConEstadoDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateriaAll(carreraId, materaId, EstadoCondicion.CURSADA, division);
        return todasLasNotas;
    }


    @Override
    public List<NotaExamenDTO> findExamenesByCursadaExamenIdMateriaCarrera(
            Long cursadaExamenId, Boolean examenInscripto) {
        List<NotaExamenDTO> todosLosExamenes = obtenerNotasPorCondicionDTO(cursadaExamenId, examenInscripto);
        List<NotaExamenDTO> aux = new ArrayList<>();
        for (NotaExamenDTO dto : todosLosExamenes) {
            System.out.println("DTO: " + dto);
            CorrelativaService.Veredicto veredicto = correlativaService.evaluar(
                    dto.getLegajoId(),
                    dto.getMateriaOrden(),
                    EstadoCondicion.EXAMEN
            );

            String status = veredicto.getTipo().name();
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

        // 1. Cargar y deduplicar
        List<NotaMateriaDTO> resultados = notaRepository.findNotasPorLegajo(legajoId);
        Map<Integer, NotaMateriaDTO> materiasMap = new HashMap<>();

        for (NotaMateriaDTO obj : resultados) {
            if (obj == null) continue;
            obj.setNotaFinal(definirNotaFinal(obj));
            int orden = obj.getMateriaOrden();
            if (materiasMap.containsKey(orden)) {
                materiasMap.put(orden, validadorAnalitico(materiasMap.get(orden), obj));
            } else {
                materiasMap.put(orden, obj);
            }
        }

        List<NotaMateriaDTO> notasRefinadas = new ArrayList<>(materiasMap.values())
                .stream().filter(Objects::nonNull).collect(Collectors.toList());

        // 2. Evaluar correlativas en lote (una sola query)
        Map<Integer, CorrelativaService.Veredicto> veredictos =
                correlativaService.evaluarLote(legajoId, notasRefinadas, EstadoCondicion.EXAMEN);

        // 3. Aplicar veredicto a cada nota
        for (NotaMateriaDTO obj : notasRefinadas) {
            CorrelativaService.Veredicto veredicto = veredictos.get(obj.getMateriaOrden());

            if (veredicto == null) continue;

            obj.setCorrelativas(veredicto.desaprobadas());

            boolean aprobada = obj.getNotaEstado() == EstadoNota.APROBADO;
            boolean correlativasPendientes = !veredicto.esAceptada();

            if (correlativasPendientes && aprobada) {
                obj.setNotaFinal("(-)");
                log.warn("Bloqueada por correlativas. Materia: {} - Pendientes: {}",
                        obj.getMateriaNombre(), veredicto.desaprobadas());
            }
        }

        return notasRefinadas;
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


    public boolean buscarClaveAnalitico(List<NotaMateriaDTO> analitico, NotaMateriaDTO materia) {
        return analitico.stream()
                .anyMatch(xd -> Objects.equals(xd.getMateriaNombre(), materia.getMateriaNombre()));
    }


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



    @Override
    public List<NotaExamenDTO> obtenerNotasPorCondicion(Long cursadaExamenId, boolean examenInscripto, EstadoCondicion notaCondicion) {
        List<NotaExamenDTO> lista =
                findExamenesByCursadaExamenIdMateriaCarrera(cursadaExamenId, examenInscripto, notaCondicion);

        return lista.stream()
                .filter(dto -> {
                    CorrelativaService.Veredicto veredicto =
                            correlativaService.evaluar(
                                    dto.getLegajoId(),
                                    dto.getMateriaOrden(),
                                    EstadoCondicion.EXAMEN
                            );
                    return veredicto.esAceptada();
                })
                .collect(Collectors.toList());
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


    @Override
    public void permitirEdicionMateria(String carreraId, String materiaId, boolean editable, String division) {
        List<NotaCursadaConEstadoDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateriaAll(carreraId, materiaId, EstadoCondicion.CURSADA,division);
        for (NotaCursadaConEstadoDTO n : todasLasNotas) {
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


//    //Regularizado para cursar
//    @Override
//    public List<ProcesadoReinscripcionMateriaDTO> obtenerReinscripciones(Integer cicloLectivo, String legajoId, String division) {
//
//        List<ReinscripcionMateriaDTO> reinscripciones = new ArrayList<>();
//        List<ProcesadoReinscripcionMateriaDTO> procesados = new ArrayList<>();
//        String carreraNombre = inscripcionService.findByLegajoId(legajoId).getCarrera().getCarreraNombre();
//        reinscripciones=materiaService.findReinscripciones(cicloLectivo, carreraNombre,division);
//
//        for (ReinscripcionMateriaDTO dto : reinscripciones) {
//
//            if(!this.isMateriaAprobada(legajoId,dto.getMateriaId())) {
//                ProcesadoReinscripcionMateriaDTO procesado = new ProcesadoReinscripcionMateriaDTO();
//                procesado.setMateriaId(dto.getMateriaId());
//                procesado.setMateriaOrden(dto.getMateriaOrden());
//                procesado.setMateriaNivel(dto.getMateriaNivel());
//                procesado.setMateriaNombre(dto.getMateriaNombre());
//                procesado.setCarreraNombre(dto.getCarreraNombre());
//                procesado.setMateriaRegimen(dto.getMateriaRegimen());
//                procesado.setMateriaModalidad(dto.getMateriaModalidad());
//                procesado.setCarreraYear(dto.getCarreraYear());
//                procesado.setMateriaCarreraId(dto.getMateriaCarreraId());
//                procesado.setDivision(dto.getDivision());
//                Materia materia=materiaService.findMateriaById(dto.getMateriaId());
//
//                List<CorrelativasFaltantesEstadoDTO> correlativas = cursadaService.obtenerCorrelativasPendientesMateriaId(legajoId, materia);
//
//                Optional<Boolean> estadoOpt = cursadaService.obtenerEstadoCursada(legajoId, dto.getMateriaId(), String.valueOf(dto.getCarreraYear()),division);
//                if (estadoOpt.isPresent()) {
//                    Boolean estado = estadoOpt.get();
//                    procesado.setCursadaInscripto(estado);
//                } else {
//                    procesado.setCursadaInscripto(false);
//                }
//                procesado.setCorrelativas(correlativas);
//                procesados.add(procesado);
//            }
//        }
//        return procesados;
//    }
//


    //Regularizado para cursar
    @Override
    public List<ProcesadoReinscripcionMateriaDTO> obtenerReinscripciones(Integer cicloLectivo, String legajoId, String division) {

        String carreraNombre = inscripcionService.findByLegajoId(legajoId)
                .getCarrera().getCarreraNombre();

        List<ReinscripcionMateriaDTO> reinscripciones =
                materiaService.findReinscripciones(cicloLectivo, carreraNombre, division);

        // 1. Pre-cargar todas las materias en una sola query
        Set<String> ids = reinscripciones.stream()
                .map(ReinscripcionMateriaDTO::getMateriaId)
                .collect(Collectors.toSet());
        Map<String, Materia> materiasMap = materiaService.findAllByIds(ids)
                .stream()
                .collect(Collectors.toMap(Materia::getMateriaId, m -> m));

        List<ProcesadoReinscripcionMateriaDTO> procesados = new ArrayList<>();

        for (ReinscripcionMateriaDTO dto : reinscripciones) {

            // 2. Saltar materias ya aprobadas
            if (this.isMateriaAprobada(legajoId, dto.getMateriaId())) continue;

            Materia materia = materiasMap.get(dto.getMateriaId());
            if (materia == null) {
                log.warn("Materia no encontrada para id={}", dto.getMateriaId());
                continue;
            }

            // 3. Mapeo
            ProcesadoReinscripcionMateriaDTO procesado = mapearReinscripcion(dto);

            // 4. Correlativas con el nuevo service
            procesado.setCorrelativas(correlativaService.pendientes(legajoId, materia));

            procesado.setCursadaStatus(correlativaService.estadoCondicion(legajoId, materia.getMateriaOrden(), EstadoCondicion.CURSADA));
            // 5. Estado cursada
            boolean inscripto = cursadaService
                    .obtenerEstadoCursada(legajoId, dto.getMateriaId(),
                            String.valueOf(dto.getCarreraYear()), division)
                    .orElse(false);
            procesado.setCursadaInscripto(inscripto);

            procesados.add(procesado);
        }

        return procesados;
    }

    private ProcesadoReinscripcionMateriaDTO mapearReinscripcion(ReinscripcionMateriaDTO dto) {
        ProcesadoReinscripcionMateriaDTO procesado = new ProcesadoReinscripcionMateriaDTO();
        procesado.setMateriaId(dto.getMateriaId());
        procesado.setMateriaOrden(dto.getMateriaOrden());
        procesado.setMateriaNivel(dto.getMateriaNivel());
        procesado.setMateriaNombre(dto.getMateriaNombre());
        procesado.setCarreraNombre(dto.getCarreraNombre());
        procesado.setMateriaRegimen(dto.getMateriaRegimen());
        procesado.setMateriaModalidad(dto.getMateriaModalidad());
        procesado.setCarreraYear(dto.getCarreraYear());
        procesado.setMateriaCarreraId(dto.getMateriaCarreraId());
        procesado.setDivision(dto.getDivision());
        return procesado;
    }
}
