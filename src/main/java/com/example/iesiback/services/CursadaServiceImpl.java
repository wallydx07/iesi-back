package com.example.iesiback.services;
import com.example.iesiback.dto.CorrelativasFaltantesEstadoDTO;
import com.example.iesiback.entities.*;
import com.example.iesiback.repositories.CursadaRepository;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CursadaServiceImpl implements CursadaService {

    private final MateriaCarreraService materiaCarreraService;
    private final CursadaRepository cursadaRepository;
    private final LegajoService legajoService;

    @Autowired
    public CursadaServiceImpl(CursadaRepository cursadaRepository,
                              MateriaCarreraService materiaCarreraService, LegajoService legajoService, LegajoService legajoService1) {
        this.cursadaRepository = cursadaRepository;
        this.materiaCarreraService = materiaCarreraService;
        this.legajoService = legajoService1;
    }




    @Transactional
    @Override
    public void agregarMateriasACursadaPorCarrera(String carreraId, Legajo legajo) {
        List<MateriaCarrera> materias = materiaCarreraService.obtenerMateriasPorCarrera(carreraId);
        for (MateriaCarrera materia : materias) {
            if(materia.getMateria().getMateriaNivel().equals("1ro")) {
                Cursada nuevaCursada = new Cursada();
                nuevaCursada.setMateriaCarrera(materia);
                nuevaCursada.setLegajo(legajo);
                nuevaCursada.setCursadaInscripto(true);
                cursadaRepository.save(nuevaCursada);
            }

        }
    }

    @Override
    public List<Cursada> getAllCursadas() {
        return cursadaRepository.findAll();
    }

    @Override
    public Optional<Cursada> getCursadaById(Integer id) {
        return cursadaRepository.findById(id);
    }

    @Override
    public Optional<Cursada> obtenerCursadaPorLegajoMateriaCarrera(String legajoId, String materiaId, String carreraId) {
        return Optional.empty();
    }

    @Override
    public Optional<Cursada> findByLegajo_LegajoIdAndMateriaCarrera_Id(String legajoId, int materiaCarreraId) {
        return cursadaRepository.findByLegajo_LegajoIdAndMateriaCarrera_Id(legajoId, materiaCarreraId);
    }

//    @Override
//    public Cursada saveCursada(Cursada cursada) {
//    //public Cursada saveCursada(String legajoId, String materiaCarreraId, Cursada cursada) {
//
//        //Optional<Cursada> cursadaOpt = cursadaRepository.findByLegajo_LegajoIdAndMateriaCarrera_Id(legajoId, materiaCarreraId);
//        Optional<Cursada> cursadaOpt = cursadaRepository.findByLegajo_LegajoIdAndMateriaCarrera_Id(cursada.getLegajo().getLegajoId(), cursada.getMateriaCarrera().getId());
//        if (cursadaOpt.isPresent()) {
//            Cursada cursadaExistente = cursadaOpt.get();
//            cursadaExistente.setCursadaInscripto(!cursadaExistente.getCursadaInscripto());
//            return cursadaRepository.save(cursadaExistente);
//        } else {
//            return cursadaRepository.save(cursada);
//
//        }
//    }

    @Override
    public Cursada saveCursada(Cursada cursada) {
        // 1. EL PASO CLAVE: Recuperar el legajo real desde la base de datos de producción
        // Esto hace que Hibernate pase el objeto de "Transient" a "Managed"
        Legajo legajoPersistido = legajoService.findById(cursada.getLegajo().getLegajoId())
                .orElseThrow(() -> new RuntimeException("Error: El Legajo no existe en la base de datos"));

        // 2. Usar el legajo que Hibernate SÍ conoce
        cursada.setLegajo(legajoPersistido);

        // 3. Ahora sí, buscar o guardar
        Optional<Cursada> cursadaOpt = cursadaRepository.findByLegajo_LegajoIdAndMateriaCarrera_Id(
                cursada.getLegajo().getLegajoId(),
                cursada.getMateriaCarrera().getId()
        );

        if (cursadaOpt.isPresent()) {
            Cursada cursadaExistente = cursadaOpt.get();
            cursadaExistente.setCursadaInscripto(!cursadaExistente.getCursadaInscripto());
            return cursadaRepository.save(cursadaExistente);
        } else {
            // Al haber hecho el 'setLegajo' con una entidad persistida, esto ya no fallará
            return cursadaRepository.save(cursada);
        }
    }


    @Override
    public void deleteCursada(Integer id) {
        cursadaRepository.deleteById(id);
    }


    @Override
    public List<Cursada> findByLegajoId(String legajoId) {
        return cursadaRepository.findByLegajoId(legajoId);
    }

    @Override
    public List<Cursada> findByLegajoAndMateria(String legajoId, String materiaId) {
        return cursadaRepository.findByLegajoAndMateria(legajoId, materiaId);
    }

    @Override
    public Optional<Boolean> obtenerEstadoCursada(String legajoId, String materiaId, String materiaYear, String division) {
        return cursadaRepository.findEstadoByLegajoAndMateria(legajoId, materiaId, Integer.parseInt(materiaYear),division);
    }

//    @Override
//    public List<Cursada> getCursadasNoAprobadas(String legajoId) {
//        List<Cursada> cursadas = cursadaRepository.findByLegajoId(legajoId);
//
//        return cursadas.stream()
//                .filter(cursada -> cursada.getNotas() != null && !cursada.getNotas().isEmpty()) // Asegura que tenga notas
//                .filter(cursada -> cursada.getNotas().stream()
//                        .noneMatch(nota -> "Aprobado".equalsIgnoreCase(nota.getNotaEstado()) ||
//                                "Cursando".equalsIgnoreCase(nota.getNotaEstado()))) // Ninguna está aprobada ni cursando
//                .collect(Collectors.toList());
//        //para la fecha es: nota.getNotaFechaNota()
//    }


    @Override
    public List<Cursada> getCursadasNoAprobadas(String legajoId) {
        List<Cursada> cursadas = cursadaRepository.findByLegajoId(legajoId);
        // Filtrar cursadas con notas, y que no tengan ninguna "Aprobado" ni "Cursando"
        List<Cursada> filtradas = cursadas.stream()
                .filter(cursada -> cursada.getNotas() != null && !cursada.getNotas().isEmpty())
                .filter(cursada -> cursada.getNotas().stream()
                        .noneMatch(nota -> "Aprobado".equalsIgnoreCase(nota.getNotaEstado()) ||
                                "Cursando".equalsIgnoreCase(nota.getNotaEstado())))
                .collect(Collectors.toList());

        // Quedarse con la cursada más reciente por materia
        Map<String, Cursada> cursadaMasRecientePorMateria = filtradas.stream()
                .collect(Collectors.toMap(
                        Cursada::getMateriaId, // Agrupar por materia
                        cursada -> cursada,    // Valor inicial
                        (c1, c2) -> {          // Resolver duplicados: quedarse con la más reciente
                            LocalDate fecha1 = c1.getNotas().stream()
                                    .map(nota -> nota.getNotaFechaNota())
                                    .filter(Objects::nonNull) // ✅ Ignorar fechas nulas
                                    .max(LocalDate::compareTo)
                                    .orElse(LocalDate.MIN);

                            LocalDate fecha2 = c2.getNotas().stream()
                                    .map(nota -> nota.getNotaFechaNota())
                                    .filter(Objects::nonNull) // ✅ Ignorar fechas nulas
                                    .max(LocalDate::compareTo)
                                    .orElse(LocalDate.MIN);


                            return fecha1.isAfter(fecha2) ? c1 : c2;
                        }
                ));

        return new ArrayList<>(cursadaMasRecientePorMateria.values());
    }


    //llega desde modoHistoria dfeberia llamarse regular poara
    @Override
    public String obtenerCorrelativasPendientes(String materiaId, String legajoId) {
        List<Cursada> cursadas = this.cursadaRepository.findByLegajoAndMateria(legajoId, materiaId);
        if (cursadas.isEmpty()) {
            return "Sin cursada";  // Si no se encuentra la cursada, retornar "Sin cursada"
        }

        Cursada cursada = cursadas.get(0);

        String correlativas = cursada.getMateriaCarrera().getMateria().getMateriaCursada();
           // Si no hay correlativas, retornar "Aprobadas"
        if (correlativas == null || correlativas.isEmpty() || correlativas.equals("-")) {
            return "Aprobadas";
        }
        StringBuilder correlativasPendientes = new StringBuilder();
        String[] correlativasArray = correlativas.split("-");

        // Recorremos las correlativas
        for (String materiaOrden : correlativasArray) {

            // Obtener cursadas de la correlativa actual
            List<Cursada> correlativaCursadas = cursadaRepository.findByMateriaOrdenAndLegajoId(
                    String.valueOf(Integer.parseInt(materiaOrden)), cursada.getLegajo().getLegajoId());

            // Se considera aprobada si está en estado "Aprobada" o "Regular"
            boolean aprobadaORegular = correlativaCursadas.stream()
                    .anyMatch(this::tieneNotaAprobadaORegular);

            // Si no está aprobada o regular, la agregamos a la lista de correlativas pendientes
            if (correlativaCursadas.isEmpty() || !aprobadaORegular) {
                if (correlativasPendientes.length() > 0) {
                    correlativasPendientes.append(", ");
                }
                correlativasPendientes.append(materiaOrden);
            }
        }

        // Si no hay correlativas pendientes, devolver "Aprobadas"
        if (correlativasPendientes.length() == 0) {
            return "Aprobadas";
        }

        return correlativasPendientes.toString();
    }


    @Override
    public List<CorrelativasFaltantesEstadoDTO> obtenerCorrelativasPendientesMateriaId(String legajoId, Materia materia) {

        String correlativas = materia.getMateriaCursada();

        if (correlativas == null || correlativas.isEmpty() || correlativas.equals("-")) {
            return Collections.emptyList();
        }

        List<CorrelativasFaltantesEstadoDTO> resultado = new ArrayList<>();

        String[] correlativasArray = correlativas.split("-");

        for (String materiaOrden : correlativasArray) {


            List<Cursada> cursadas = cursadaRepository
                    .findByMateriaOrdenAndLegajoId(materiaOrden, legajoId);

//            Materia materiaM=materiaCarreraService.findMateriaCarreraByMateriaOrdenCarreraId(materiaOrden)

            boolean estaAprobada = cursadas.stream()
                    .flatMap(c -> c.getNotas().stream())
                    .anyMatch(n -> "APROBADO".equalsIgnoreCase(n.getNotaEstado()));

            // 👉 Si está aprobada, NO la agregamos
            if (estaAprobada) {
                continue;
            }

            boolean estaRegular = cursadas.stream()
                    .flatMap(c -> c.getNotas().stream())
                    .anyMatch(n -> "REGULAR".equalsIgnoreCase(n.getNotaEstado()));

            CorrelativasFaltantesEstadoDTO dto = new CorrelativasFaltantesEstadoDTO();
            dto.setMateriaOrden(materiaOrden);
//            dto.setMateriaNombre(materiaM.getMateriaNombre());
//            dto.setMateriaId(materiaM.getMateriaId());

            if (estaRegular) {
                dto.setStatus("regular");
            } else {
                dto.setStatus("libre");
            }

            resultado.add(dto);
        }

        return resultado;
    }

//    private boolean tieneNotaAprobadaORegular(Cursada cursada) {
//        return cursada.getNotas().stream()
//                .peek(nota -> System.out.println("Estado de la nota: " + nota.getNotaEstado()))  // Imprime el estado de cada nota
//                .anyMatch(nota -> "Aprobado".equalsIgnoreCase(nota.getNotaEstado()));
//    }

    private static final Set<String> ESTADOS_VALIDOS = Set.of("APROBADO", "REGULAR");
    private boolean tieneNotaAprobadaORegular(Cursada cursada) {
        return cursada.getNotas().stream()
                .anyMatch(nota -> ESTADOS_VALIDOS.contains(nota.getNotaEstado().toUpperCase()));
    }


@Transactional
@Override
public void eliminarCursada(Integer id) {
        Cursada cursada = cursadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cursada no encontrada con ID: " + id));

        cursadaRepository.delete(cursada);
    }

    @Override
    public Optional<Cursada> buscarPorId(Integer cursadaId) {
        return cursadaRepository.findById(cursadaId);
    }
    @Override
    public Cursada actualizarCursada(Cursada cursada, Cursada cursadaPost) {
        cursada.setPrimerParcial(cursadaPost.getPrimerParcial());
        cursada.setRecuperatorio1(cursadaPost.getRecuperatorio1());
        cursada.setSegundoParcial(cursadaPost.getSegundoParcial());
        cursada.setRecuperatorio2(cursadaPost.getRecuperatorio2());
        cursada.setTrabajosPracticos(cursadaPost.getTrabajosPracticos());
        cursada.setAsistencia(cursadaPost.getAsistencia());
        cursada.setColoquio(cursadaPost.getColoquio());
        cursada.setTrabajoInstitucional(cursadaPost.getTrabajoInstitucional());
        cursada.setCursadaInscripto(cursadaPost.getCursadaInscripto());
        cursada.setStatus(cursadaPost.getStatus());

//        cursada.setMateriaCarrera(cursadaPost.getMateriaCarrera());

//        // Actualizar notas sin reemplazar la colección
//        actualizarNotas(cursada, cursadaPost.getNotas());

        return cursadaRepository.save(cursada);
    }


    @Override
    public List<Cursada> findByMateriaCarrera_Carrera_CarreraId(String materiaCarreraId) {
        return cursadaRepository.findByCarreraId(materiaCarreraId);
    }

    @Transactional
    @Override
    public Cursada obtenerORegistrarCursada(
            Legajo legajo,
            Long materiaCarreraId) {

        return cursadaRepository.findByLegajoAndMateriaCarreraId(
                        legajo.getLegajoId(),
                        materiaCarreraId
                )
                .orElseGet(() -> {
                    MateriaCarrera materiaCarrera =
                            materiaCarreraService
                                    .obtenerMateriaCarreraPorId(materiaCarreraId)
                                    .orElseThrow(() ->
                                            new RuntimeException("MateriaCarrera no encontrada"));

                    Cursada nueva = new Cursada();
                    nueva.setLegajo(legajo);
                    nueva.setMateriaCarrera(materiaCarrera);
                    nueva.setCursadaInscripto(true);

                    return cursadaRepository.save(nueva);
                });
    }


    @Transactional
    @Override
    public Cursada buscarOMasCercanaORegistrar(
            Legajo legajo,
            MateriaCarrera materiaCarrera) {

        LocalDate hoy = LocalDate.now();

        // Traemos todas las cursadas del alumno para esa materia
        List<Cursada> cursadas = cursadaRepository.findByLegajoAndMateria(
                legajo.getLegajoId(),
                materiaCarrera.getMateria().getMateriaId()
        );

        Cursada masCercana = null;

        if (!cursadas.isEmpty()) {

            // Filtramos cursadas con al menos una nota Regular
            List<Cursada> regulares = cursadas.stream()
                    .filter(c -> c.getNotas().stream()
                            .anyMatch(n -> "Regular".equalsIgnoreCase(n.getNotaEstado())))
                    .collect(Collectors.toList());

            List<Cursada> aEvaluar = regulares.isEmpty() ? cursadas : regulares;

            // Obtenemos la cursada más cercana a la fecha actual
            masCercana = aEvaluar.stream()
                    .min(Comparator.comparing(c ->
                            Math.abs(ChronoUnit.DAYS.between(c.getMateriaCarrera().getFechaInicio(), hoy))
                    ))
                    .orElse(null);
        }

        // Si no hay ninguna cursada, creamos una nueva
        if (masCercana == null) {
            masCercana = obtenerORegistrarCursada(legajo, materiaCarrera.getId().longValue());
        }

        return masCercana;
    }
}
