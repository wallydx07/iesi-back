package com.example.iesiback.services;
import com.example.iesiback.dto.*;
import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Materia;
import com.example.iesiback.entities.MateriaCarrera;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.enums.EstadoNota;
import com.example.iesiback.enums.EstadoCondicion;
import com.example.iesiback.exception.ResourceNotFoundException;
import com.example.iesiback.repositories.NotaRepository;
import jakarta.persistence.EntityNotFoundException;
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
            dto.setNotaCalificacionNumero((String) row[3]);
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

            if(dto.getNotaEstado() == EstadoNota.CURSANDO){
                dto.setNotaCalificacionNumero("Cur");
            }
            if(dto.getNotaEstado() == EstadoNota.REGULAR){
                dto.setNotaCalificacionNumero("Reg");
            }
            if(dto.getNotaEstado() == EstadoNota.LIBRE){
                dto.setNotaCalificacionNumero("Lib");
            }
            if(dto.getNotaEstado() == EstadoNota.DESAPROBADO){
                dto.setNotaCalificacionNumero("Des");
            }
            if(dto.getNotaEstado() == EstadoNota.PENDIENTE){
                dto.setNotaCalificacionNumero("Pend");
            }





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



//de aqui salken para ver las planillas en el navegador/pdf
@Override
public List<NotaCursadaConEstadoDTO> findNotasByCarreraAndMateria(
        String carreraId, String materiaId, String division, boolean cursadaInscripto) {

    Materia mnateria= materiaService.findMateriaById(materiaId);

    List<NotaCursadaDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateria(
            carreraId, materiaId, cursadaInscripto, EstadoCondicion.CURSADA, division);


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


            int anioActual = LocalDate.now().getYear();

            entry.getValue().forEach(nota -> {
                if (nota.getNotaFechaNota() != null &&
                        nota.getNotaFechaNota().getYear() == anioActual) {
                    eliminarNotaIndividual(nota.getNotaId());
                }
            });
        }


        entry.getValue().forEach(nota ->
                resultado.add(new NotaCursadaConEstadoDTO(nota, vd.estado()))
        );



    }

    // Al final antes del return, reordenar por apellido y nombre
    resultado.sort(Comparator.comparing(NotaCursadaConEstadoDTO::getPersonaApellido)
            .thenComparing(NotaCursadaConEstadoDTO::getPersonaNombre));




    for (NotaCursadaConEstadoDTO dto : resultado) {

        if(dto.getNotaEstado() == EstadoNota.CURSANDO){
            dto.setNotaCalificacionNotaLetra("CURSANDO");
            dto.setNotaCalificacionNotaNumero("-");
        }
        if(dto.getNotaEstado() == EstadoNota.REGULAR){
            dto.setNotaCalificacionNotaLetra("REGULAR");
            dto.setNotaCalificacionNotaNumero("R");
        }
        if(dto.getNotaEstado() == EstadoNota.LIBRE){
            dto.setNotaCalificacionNotaLetra("LIBRE");
            dto.setNotaCalificacionNotaNumero("L");
        }
        if(dto.getNotaEstado() == EstadoNota.DESAPROBADO){
            dto.setNotaCalificacionNotaLetra("DESAPROBADO");
            dto.setNotaCalificacionNotaNumero("D-");
        }
        if(dto.getNotaEstado() == EstadoNota.PENDIENTE){
            dto.setNotaCalificacionNotaLetra("PENDIENTE");
            dto.setNotaCalificacionNotaNumero("P");
        }

    }



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



        if(obj.getNotaEstado() == EstadoNota.CURSANDO){
            obj.setNotaCalificacionNumero("-");
        }
        if(obj.getNotaEstado() == EstadoNota.REGULAR){
            obj.setNotaCalificacionNumero("R");
        }
        if(obj.getNotaEstado() == EstadoNota.LIBRE){
            obj.setNotaCalificacionNumero("L");
        }
        if(obj.getNotaEstado() == EstadoNota.DESAPROBADO){
            obj.setNotaCalificacionNumero("D");
        }
        if(obj.getNotaEstado() == EstadoNota.PENDIENTE){
            obj.setNotaCalificacionNumero("P");
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


//
//    @Override
//    public List<NotaCursadaConEstadoDTO> findNotasByCarreraAndMateriaAll(String carreraId, String materaId, String division,  boolean cursadaInscripto) {
//        List<NotaCursadaConEstadoDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateriaAll(carreraId, materaId, "CURSADA", division);
//        return todasLasNotas;
//    }
//


    //de aqui salken para ver las planillas en el pdf
    @Override
    public List<NotaCursadaConEstadoDTO> findNotasByCarreraAndMateriaAll(String carreraId, String materaId, String division,  boolean cursadaInscripto) {

    List<NotaCursadaDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateriaAll(carreraId, materaId, "CURSADA", division);


        Materia mnateria= materiaService.findMateriaById(materaId);

        int ordenMateria =mnateria.getMateriaOrden();

        List<NotaMateriaDTO> materiaWrapper = List.of(
                new NotaMateriaDTO() {{
                    setMateriaOrden(ordenMateria);
                    setMateriaId(materaId); // <--- Agrega esta línea
                }}
        );

        Map<String, List<NotaCursadaDTO>> porLegajo = todasLasNotas.stream()
                .filter(nota -> nota != null && nota.getPersonaLegajoId() != null) // <--- Filtro de seguridad
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




        for (NotaCursadaConEstadoDTO dto : resultado) {

            if(dto.getNotaEstado() == EstadoNota.CURSANDO){
                dto.setNotaCalificacionNotaLetra("CURSANDO");
                dto.setNotaCalificacionNotaNumero("-");
            }
            if(dto.getNotaEstado() == EstadoNota.REGULAR){
                dto.setNotaCalificacionNotaLetra("REGULAR");
                dto.setNotaCalificacionNotaNumero("R");
            }
            if(dto.getNotaEstado() == EstadoNota.LIBRE){
                dto.setNotaCalificacionNotaLetra("LIBRE");
                dto.setNotaCalificacionNotaNumero("L");
            }
            if(dto.getNotaEstado() == EstadoNota.DESAPROBADO){
                dto.setNotaCalificacionNotaLetra("DESAPROBADO");
                dto.setNotaCalificacionNotaNumero("D-");
            }
            if(dto.getNotaEstado() == EstadoNota.PENDIENTE){
                dto.setNotaCalificacionNotaLetra("PENDIENTE");
                dto.setNotaCalificacionNotaNumero("P");
            }
        }
        return resultado;
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

    @Override
    public List<NotaMateriaDTO> obtenerTodasNotasPorLegajoAnalitico(String legajoId) {
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
        Map<Integer, CorrelativaService.Veredicto> veredictos =
                correlativaService.evaluarLote(legajoId, notasRefinadas, EstadoCondicion.EXAMEN);
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


    @Override
    public List<NotaMateriaDTO> obtenerTodasNotasPorLegajoCalificador(String legajoId) {
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
        Map<Integer, CorrelativaService.Veredicto> veredictos =
                correlativaService.evaluarLote(legajoId, notasRefinadas, EstadoCondicion.EXAMEN);
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
            if (obj.getNotaEstado() == EstadoNota.CURSANDO) {
                obj.setNotaCalificacionNumero("Cur");
            }
            if (obj.getNotaEstado() == EstadoNota.REGULAR) {
                obj.setNotaCalificacionNumero("Reg");
            }
            if (obj.getNotaEstado() == EstadoNota.LIBRE) {
                obj.setNotaCalificacionNumero("Lib");
            }
            if (obj.getNotaEstado() == EstadoNota.DESAPROBADO) {
                obj.setNotaCalificacionNumero("Des");
            }
            if (obj.getNotaEstado() == EstadoNota.PENDIENTE) {
                obj.setNotaCalificacionNumero("Pend");
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
        String cond1 = normalizarEstado(nota1.getNotaEstado());
        String cond2 = normalizarEstado(nota2.getNotaEstado());
        System.out.println("Estado recibido cond1: " + cond1);
        System.out.println("Estado recibido cond2: " + cond2);
        if (!esEstadoValido(cond1) || !esEstadoValido(cond2)) {
            throw new IllegalArgumentException("Estado de la nota no válido");
        }

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

        List<Object[]> rows = notaRepository.findExamenesRaw(cursadaExamenId, examenInscripto, notaCondicion.name());

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
//        List<NotaCursadaConEstadoDTO> todasLasNotas = notaRepository.findNotasByCarreraAndMateriaAll(carreraId, materiaId, "CURSADA",division);

        List<NotaCursadaConEstadoDTO> todasLasNotas = this.findNotasByCarreraAndMateriaAll(carreraId, materiaId, division, true);

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


    /**
     * Genera un reporte académico completo del alumno para un legajo dado.
     * Evalúa cursada y examen en una sola query usando el motor de correlativas.
     */

    @Override
    public ReporteAcademicoDTO generarReporteAcademico(String legajoId) {

        // Todas las notas del legajo (puede haber varias por materia)
        List<NotaMateriaDTO> todasLasNotas = notaRepository
                .findNotasPorLegajo(legajoId)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Agrupar por materia y quedarse con la nota de mayor prioridad
        List<NotaMateriaDTO> notasRepresentativas = todasLasNotas.stream()
                .collect(Collectors.groupingBy(NotaMateriaDTO::getMateriaOrden))
                .values().stream()
                .map(this::resolverNotaRepresentativa)
                .sorted(Comparator.comparing(NotaMateriaDTO::getMateriaOrden))
                .collect(Collectors.toList());

        // El resto del método trabaja solo con notasRepresentativas
        Map<Integer, CorrelativaService.VeredictoDoble> veredictos =
                correlativaService.evaluarLoteDoble(legajoId, notasRepresentativas);

        List<ReporteAcademicoDTO.MateriaEstadoDTO> materias = notasRepresentativas.stream()
                .map(nota -> construirMateriaEstado(nota, veredictos.get(nota.getMateriaOrden())))
                .collect(Collectors.toList());

        return ReporteAcademicoDTO.builder()
                .legajoId(legajoId)
                .totalMaterias(materias.size())
                .puedeCursar((int) materias.stream().filter(ReporteAcademicoDTO.MateriaEstadoDTO::isPuedeCursar).count())
                .puedeRendir((int) materias.stream().filter(ReporteAcademicoDTO.MateriaEstadoDTO::isPuedeRendir).count())
                .conDeuda((int) materias.stream()
                        .filter(m -> !m.isPuedeCursar() && !m.isPuedeRendir()).count())
                .materias(materias)
                .build();
    }
// ─────────────────────────────────────────────────────────────────────────────

    private ReporteAcademicoDTO.MateriaEstadoDTO construirMateriaEstado(
            NotaMateriaDTO nota,
            CorrelativaService.VeredictoDoble veredicto) {

        // Veredictos individuales (con fallback si la materia no tiene correlativas cargadas)
        CorrelativaService.Veredicto vCursada = veredicto != null ? veredicto.getCursada() : null;
        CorrelativaService.Veredicto vExamen  = veredicto != null ? veredicto.getExamen()  : null;

        boolean puedeCursar = vCursada != null && vCursada.esAceptada();
        boolean puedeRendir = vExamen  != null && vExamen.esAceptada();

        return ReporteAcademicoDTO.MateriaEstadoDTO.builder()
                .materiaOrden(nota.getMateriaOrden())
                .materiaNombre(nota.getMateriaNombre())
                .notaEstado(nota.getNotaEstado() != null ? nota.getNotaEstado().name() : null)
                .calificacion(resolverCalificacion(nota))

                // cursada
                .puedeCursar(puedeCursar)
                .estadoCursada(vCursada != null ? vCursada.toLegacy().getStatus() : "-")
                .correlativasCursadaPendientes(vCursada != null ? vCursada.desaprobadas() : List.of())
                .observacionesCursada(vCursada != null ? vCursada.conFechaIncoherente() : List.of())
                .explicacionCursada(explicarCursada(nota, vCursada))

                // examen
                .puedeRendir(puedeRendir)
                .estadoExamen(vExamen != null ? vExamen.toLegacy().getStatus() : "-")
                .correlativasExamenPendientes(vExamen != null ? vExamen.desaprobadas() : List.of())
                .observacionesExamen(vExamen != null ? vExamen.conFechaIncoherente() : List.of())
                .explicacionExamen(explicarExamen(nota, vExamen))

                .build();
    }

    private String explicarCursada(NotaMateriaDTO nota, CorrelativaService.Veredicto v) {
        String nombre = nota.getMateriaNombre();
        EstadoNota estado = nota.getNotaEstado();

        // Casos terminales — no hay nada que evaluar
        if (estado == EstadoNota.APROBADO) {
            return nombre + " ya está aprobada. No necesita volver a cursarla.";
        }
//        if (estado == EstadoNota.EQUIVALENCIA) {
//            return nombre + " fue acreditada por equivalencia. No requiere cursada.";
//        }
        if (estado == EstadoNota.CURSANDO) {
            return "Actualmente está cursando " + nombre + ". No puede volver a inscribirse mientras tenga la cursada activa.";
        }

        if (v == null) {
            return "No se pudo determinar el estado de correlativas para cursar " + nombre + ".";
        }

        return switch (v.getTipo()) {

            case SIN_CORRELATIVAS ->
                    nombre + " no tiene correlativas de cursada. " +
                            "El alumno puede inscribirse en cualquier momento sin restricciones previas.";

            case ACEPTADA -> {
                if (estado == EstadoNota.REGULAR) {
                    yield "El alumno ya tiene " + nombre + " regularizada y cumple todas las correlativas. " +
                            "Si quisiera volver a cursarla, está habilitado para hacerlo.";
                }
                yield "Cumple todas las correlativas necesarias para cursar " + nombre + ". " +
                        "Puede inscribirse sin inconvenientes.";
            }

            case PROVISORIA -> {
                List<String> pendientes = v.desaprobadas().stream()
                        .filter(s -> !s.equals("Ninguna"))
                        .toList();
                String listaPendientes = String.join(", ", pendientes);
                yield "No puede inscribirse a cursar " + nombre + " todavía. " +
                        "Para hacerlo necesita tener regularizadas o aprobadas las siguientes materias: " +
                        listaPendientes + ". " +
                        "Una vez que regularice " + (pendientes.size() == 1 ? "esa materia" : "esas materias") +
                        ", quedará habilitado automáticamente.";
            }

            case SIN_CURSADA ->
                    "El alumno nunca cursó " + nombre + " y tampoco tiene correlativas aprobadas. " +
                            "Deberá regularizar las materias previas antes de poder inscribirse.";

            case FECHA_INCOHERENTE -> {
                List<String> inconsistentes = v.conFechaIncoherente().stream()
                        .filter(s -> !s.equals("No"))
                        .toList();
                yield "Las correlativas de cursada para " + nombre + " figuran como aprobadas, " +
                        "pero se detectaron inconsistencias en las fechas de algunas de ellas: " +
                        String.join("; ", inconsistentes) + ". " +
                        "Se lo habilita provisionalmente para cursar, pero se recomienda revisar el historial.";
            }
        };
    }



private String explicarExamen(NotaMateriaDTO nota, CorrelativaService.Veredicto v) {
    String nombre   = nota.getMateriaNombre();
    EstadoNota estado = nota.getNotaEstado();

    // ── Casos terminales ─────────────────────────────────────────────────────

    if (estado == EstadoNota.APROBADO) {
        return nombre + " ya está aprobada. No tiene examen ni promoción pendiente.";
    }
    if (estado == EstadoNota.LIBRE) {
        return "El alumno figura como libre en " + nombre + ". " +
                "Perdió la regularidad y deberá volver a cursar la materia desde el principio " +
                "antes de poder rendir o promocionar.";
    }
    if (estado == EstadoNota.DESAPROBADO) {
        return "El alumno desaprobó el examen final de " + nombre + ". " +
                "Puede volver a inscribirse a rendir en el próximo turno, " +
                "siempre que su regularidad siga vigente. " +
                "Si la regularidad venció, deberá recursar.";
    }

    if (v == null) {
        return "No se pudo determinar el estado de correlativas para " + nombre + ".";
    }

    // ── Con correlativas pendientes: bloqueado en ambas vías ─────────────────

    if (v.getTipo() == CorrelativaService.TipoVeredicto.PROVISORIA) {
        List<String> pendientes = v.desaprobadas().stream()
                .filter(s -> !s.equals("Ninguna"))
                .toList();
        String listaPendientes = String.join(", ", pendientes);

        if (estado == EstadoNota.CURSANDO) {
            return "Está cursando " + nombre + " pero tiene correlativas de examen pendientes: " +
                    listaPendientes + ". " +
                    "Aunque llegue a los requisitos de promoción, no podrá promocionar ni rendir el final " +
                    "hasta que apruebe " + (pendientes.size() == 1 ? "esa materia." : "esas materias.");
        }
        if (estado == EstadoNota.REGULAR) {
            return "Tiene " + nombre + " regularizada, pero no puede rendir ni promocionar todavía. " +
                    "Le falta tener aprobadas las siguientes correlativas de examen: " +
                    listaPendientes + ". " +
                    "Una vez que las apruebe, quedará habilitado para el próximo turno.";
        }
    }

    if (v.getTipo() == CorrelativaService.TipoVeredicto.SIN_CURSADA) {
        return "El alumno no tiene cursada registrada para " + nombre + ". " +
                "Para poder rendir o promocionar primero debe cursar y regularizar la materia.";
    }

    // ── Correlativas ok: evaluar si puede promocionar o solo rendir ──────────

    boolean correlativasOk = v.getTipo() == CorrelativaService.TipoVeredicto.ACEPTADA
            || v.getTipo() == CorrelativaService.TipoVeredicto.SIN_CORRELATIVAS
            || v.getTipo() == CorrelativaService.TipoVeredicto.FECHA_INCOHERENTE;

    if (!correlativasOk) {
        return "No se pudo verificar el estado de correlativas para " + nombre + ".";
    }

    // Advertencia de fecha incoherente (no bloquea pero se menciona)
    String advertenciaFecha = "";
    if (v.getTipo() == CorrelativaService.TipoVeredicto.FECHA_INCOHERENTE) {
        List<String> inconsistentes = v.conFechaIncoherente().stream()
                .filter(s -> !s.equals("No"))
                .toList();
        advertenciaFecha = " Nota: se detectaron fechas inconsistentes en las correlativas (" +
                String.join("; ", inconsistentes) +
                "), secretaría debería revisarlo antes del turno.";
    }



    // Fallback
    return "Cumple las correlativas de examen para " + nombre + ". " +
            "Puede anotarse a rendir en el próximo turno disponible." + advertenciaFecha;
}

    private String resolverCalificacion(NotaMateriaDTO nota) {
        if (nota.getNotaEstado() == null) return "-";
        return switch (nota.getNotaEstado()) {
            case CURSANDO    -> "-";
            case REGULAR     -> "R";
            case LIBRE       -> "L";
            case DESAPROBADO -> "D";
            case PENDIENTE   -> "P";
            case APROBADO    -> nota.getNotaCalificacionNumero() != null
                    ? nota.getNotaCalificacionNumero() : "A";
            default          -> "-";
        };
    }


    // En NotaService o como helper en CorrelativaService

    private static final Map<EstadoNota, Integer> PRIORIDAD_ESTADO = Map.of(
            EstadoNota.APROBADO,     5,
            EstadoNota.REGULAR,      4,
            EstadoNota.CURSANDO,     3,
            EstadoNota.LIBRE,        2,
            EstadoNota.DESAPROBADO,  1,
            EstadoNota.PENDIENTE,    0
    );

    private NotaMateriaDTO resolverNotaRepresentativa(List<NotaMateriaDTO> notasDeMateria) {
        return notasDeMateria.stream()
                .max(Comparator.comparingInt(n ->
                        PRIORIDAD_ESTADO.getOrDefault(n.getNotaEstado(), -1)))
                .orElseThrow();
    }


    @Override
    public List<Nota> updateFechaNotasByMateriaCarreraId(Integer materiaCarreraId, EstadoCondicion estadoCondicion, LocalDate fecha) {

        List<Nota> notas = switch (estadoCondicion) {
            case CURSADA -> notaRepository.findByCursadaMateriaCarreraIdAndEstado(
                    materiaCarreraId,
                    EstadoCondicion.CURSADA
            );
            case EXAMEN -> notaRepository.findByCursadaExamenId(
                    materiaCarreraId
            );
            default -> throw new IllegalArgumentException(
                    "EstadoCondicion no soportado: " + estadoCondicion
            );
        };

        if (notas.isEmpty()) {
            throw new EntityNotFoundException(
                    "No se encontraron notas en estado " + estadoCondicion + " para id: " + materiaCarreraId
            );
        }
        notas.forEach(nota -> nota.setNotaFechaNota(fecha));
        return notaRepository.saveAll(notas);
    }

}
