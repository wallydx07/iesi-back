package com.example.iesiback.services;

import com.example.iesiback.entities.ExamenHorario;
import com.example.iesiback.dto.InscripcionExamenDTO;
import com.example.iesiback.entities.*;
import com.example.iesiback.repositories.CursadaExamenRepository;
import com.example.iesiback.repositories.ExamenRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Slf4j
public class ExamenServiceImpl implements ExamenService {
    private final NotaService notaService;
    private final PermisoService permisoService; // ✅ Inyectamos PermisoService
    private final ExamenRepository examenRepository;
    private final CursadaService cursadaService;
    private final CursadaExamenRepository cursadaExamenRepository;
    private final ExamenHorarioService examenHorarioService;

    @Autowired
    public ExamenServiceImpl(
            ExamenRepository examenRepository,
            CursadaService cursadaService,
            CursadaExamenRepository cursadaExamenRepository,
            PermisoService permisoService, NotaService notaService, ExamenHorarioService examenHorarioService) {
        this.examenRepository = examenRepository;
        this.cursadaService = cursadaService;
        this.cursadaExamenRepository = cursadaExamenRepository;
        this.permisoService = permisoService;
        this.notaService = notaService;
        this.examenHorarioService = examenHorarioService;
    }

    @Override
    public boolean verificarPermisoParaTurno(String permisoLegajoId, String turnoId) {
        return examenRepository.existsByPermisoLegajoIdAndTurnoId(permisoLegajoId, turnoId);
    }

    @Override
    public List<Examen> getAllExamenes() {
        return examenRepository.findAll();
    }

    @Override
    public Examen getExamenById(Long id) {
        Optional<Examen> examen = examenRepository.findById(id);
        return examen.orElse(null);
    }

    @Override
    public Examen saveExamen(Examen examen) {
        return examenRepository.save(examen);
    }

    @Override
    public void deleteExamen(Long id) {
        examenRepository.deleteById(id);
    }

    @Override
    public List<Examen> findByPermisoLegajoIdAndTurnoId(String permisoLegajoId, String turnoId) {
        return examenRepository.findByPermiso_PermisoLegajoIdAndCursadaExamen_Turno_TurnoId(permisoLegajoId, turnoId);
    }

    @Transactional
    public List<InscripcionExamenDTO> completarCursadas(String legajoId, String turno) {
        List<Cursada> cursadas = cursadaService.getCursadasNoAprobadas(legajoId);
        List<InscripcionExamenDTO> inscripciones = new ArrayList<>();

        cursadas.forEach(cursada -> {
            InscripcionExamenDTO inscripcion = new InscripcionExamenDTO();
            inscripcion.setCursadaId(cursada.getId());
            inscripcion.setMateriaCarreraId(cursada.getMateriaCarrera().getId());
            inscripcion.setMateriaOrden(cursada.getMateriaCarrera().getMateria().getMateriaOrden());
            inscripcion.setCurso(cursada.getMateriaCarrera().getMateria().getMateriaNivel());
            inscripcion.setMateriaId(cursada.getMateriaCarrera().getMateria().getMateriaId());
            inscripcion.setMateriaNombre(cursada.getMateriaCarrera().getMateria().getMateriaNombre());

            List<Nota> notas = new ArrayList<>(cursada.getNotas());

            // ── REEMPLAZÁS ESTE BLOQUE ──────────────────────────────────────
            List<Map<String, Object>> notasMapeadas = notas.stream()
                    .map(n -> Map.<String, Object>of(
                            "cursadaId",              cursada.getId(),
                            "materiaNombre",          cursada.getMateriaCarrera().getMateria().getMateriaNombre(),
                            "notaCondicion",          n.getNotaCondicion(),
                            "notaEstado",             n.getNotaEstado(),
                            "notaFecha",              n.getNotaFechaNota() != null ? n.getNotaFechaNota().toString() : "",
                            "notaCalificacionNumero", n.getNotaCalificacionNotaNumero() != null ? n.getNotaCalificacionNotaNumero() : 0.0
                    ))
                    .toList();


            NotaServiceImpl.ResultadoRegularidad resultado = notaService.evaluarRegularidad(notas);
            inscripcion.setCondicion(resultado.condicion());
            inscripcion.setJustificacion(resultado.justificacion());
            // ────────────────────────────────────────────────────────────────

            Boolean estado = examenRepository.getEstadoExamen(turno, cursada.getMateriaId(), legajoId);
            boolean inscripto = estado != null && estado;
            inscripcion.setInscripto(inscripto);

            String fecha = "-";
            String hora = "-";

            Optional<CursadaExamen> cursadaExamen =
                    cursadaExamenRepository.findByMateriaIdAndTurno_TurnoId(
                            cursada.getMateriaId(), turno
                    );

            Optional<ExamenHorario> optional =
                    examenHorarioService.findByMateriaIdAndTurnoId(
                            cursada.getMateriaId(), turno
                    );

            if (cursadaExamen.isPresent()) {
                CursadaExamen ce = cursadaExamen.get();
                fecha = ce.getFecha() != null ? ce.getFecha().toString() : "-";
                hora  = ce.getHora()  != null ? ce.getHora()            : "-";
            } else if (optional.isPresent()) {
                ExamenHorario eh = optional.get();
                fecha = eh.getFecha() != null ? eh.getFecha().toString() : "-";
                hora  = eh.getHora()  != null ? eh.getHora().toString()  : "-";
            }

            inscripcion.setHora(hora);
            inscripcion.setFecha(fecha);
            inscripcion.setCorrelativas(cursadaService.obtenerCorrelativasPendientesMateriaId(
                    cursada.getLegajo().getLegajoId(),
                    cursada.getMateriaCarrera().getMateria()
            ));

            inscripciones.add(inscripcion);
        });

        return inscripciones;
    }






//    @Override
//    public Examen registrarExamen(String legajoId, String turnoId, String materiaId, String condicionExamen, Integer cursadaId) {
//        Permiso permiso = permisoService.obtenerOCrearPermiso(legajoId, turnoId);
//        Optional<CursadaExamen> cursadaExistente = cursadaExamenRepository.findByMateriaIdAndTurno_TurnoId(materiaId, turnoId);
//        if (cursadaExistente.isEmpty()) {
//            throw new RuntimeException("❌ No existe una cursada para la materia y turno especificados.");
//        }
//        CursadaExamen cursadaExamen = cursadaExistente.get();
//        Optional<Examen> examenExistente = examenRepository.findByPermisoAndCursadaExamen(permiso, cursadaExamen);
//
//        if (examenExistente.isPresent()) {
//            Examen examenActualizado = examenExistente.get();
//            examenActualizado.setExamenInscripto(!examenActualizado.getExamenInscripto()); // Alternar estado
//            return examenRepository.save(examenActualizado);
//        }else {
//            Cursada cursada= this.cursadaService.getCursadaById(cursadaId).get();
//            Nota nota = new Nota();
//            Nota aux = new Nota();
//            nota.setNotaCondicion(condicionExamen);
//            nota.setNotaEstado("Pendiente");
//            nota.setCursada(cursada);
//            nota.setNotaFechaNota(cursadaExistente.get().getFecha());
//            aux=notaService.guardarNota(nota);
//            Examen examen = new Examen();
//            examen.setStatus("Pendiente");
//            examen.setNota(aux);
//            examen.setPermiso(permiso);
//            examen.setCursadaExamen(cursadaExamen);
//            examen.setExamenInscripto(true); // Se inscribe por primera vez
//            notaService.guardarNota(nota);
//            return examenRepository.save(examen);
//        }
//    }

//    @Override
//    @Transactional
//    public Examen registrarExamen(
//            String legajoId,
//            Turno turno,
//            Materia materia,
//            String condicionExamen,
//            Integer cursadaId) {
//
//        // 1️⃣ Obtener o crear permiso
//        Permiso permiso = permisoService.obtenerOCrearPermiso(legajoId, turno.getTurnoId());
//
//        // 2️⃣ Buscar cursadaExamen
//        CursadaExamen cursadaExamen =
//                this.obtenerOCrearCursadaExamen(
//                        materia,
//                        turno);
//
//        // 3️⃣ Verificar si ya existe examen
//        Optional<Examen> examenExistente =
//                examenRepository.findByPermisoAndCursadaExamen(permiso, cursadaExamen);
//
//        if (examenExistente.isPresent()) {
//
//            Examen examenActualizado = examenExistente.get();
//            examenActualizado.setExamenInscripto(!examenActualizado.getExamenInscripto());
//
//            return examenRepository.save(examenActualizado);
//        }
//
//        // 4️⃣ Obtener cursada (seguro)
//        Cursada cursada = cursadaService
//                .getCursadaById(cursadaId)
//                .orElseThrow(() ->
//                        new RuntimeException("Cursada no encontrada con ID: " + cursadaId));
//
//        // 5️⃣ Crear nota
//        Nota nota = new Nota();
//        nota.setNotaCondicion(condicionExamen);
//        nota.setNotaEstado("Pendiente");
//        nota.setCursada(cursada);
//        nota.setNotaFechaNota(cursadaExamen.getFecha());
//
//        Nota notaGuardada = notaService.guardarNota(nota);
//
//        // 6️⃣ Crear examen
//        Examen examen = new Examen();
//        examen.setStatus("Pendiente");
//        examen.setNota(notaGuardada);
//        examen.setPermiso(permiso);
//        examen.setCursadaExamen(cursadaExamen);
//        examen.setExamenInscripto(true);
//
//        return examenRepository.save(examen);
//    }


    @Override
    @Transactional
    public Examen registrarExamen(
            String legajoId,
            Turno turno,
            Materia materia,
            String condicionExamen,
            Integer cursadaId) {

        log.info("==== INICIO registrarExamen ====");
        log.info("LegajoId: {}, TurnoId: {}, MateriaId: {}, CursadaId: {}, Condicion: {}",
                legajoId,
                turno.getTurnoId(),
                materia.getMateriaId(),
                cursadaId,
                condicionExamen);

        // 1️⃣ Obtener o crear permiso
        log.info("Buscando u obteniendo Permiso...");
        Permiso permiso = permisoService.obtenerOCrearPermiso(legajoId, turno.getTurnoId());
        log.info("Permiso obtenido/creado con ID: {}", permiso.getId());

        // 2️⃣ Buscar u obtener cursadaExamen
        log.info("Buscando u obteniendo CursadaExamen...");
        CursadaExamen cursadaExamen =
                this.obtenerOCrearCursadaExamen(materia, turno);
        log.info("CursadaExamen ID: {}, Fecha: {}",
                cursadaExamen.getId(),
                cursadaExamen.getFecha());

        // 3️⃣ Verificar si ya existe examen
        log.info("Verificando si ya existe Examen para Permiso ID {} y CursadaExamen ID {}",
                permiso.getId(),
                cursadaExamen.getId());

        Optional<Examen> examenExistente =
                examenRepository.findByPermisoAndCursadaExamen(permiso, cursadaExamen);

        if (examenExistente.isPresent()) {

            Examen examenActualizado = examenExistente.get();

            log.info("Examen existente encontrado. ID: {}. Estado actual inscripto: {}",
                    examenActualizado.getId(),
                    examenActualizado.getExamenInscripto());

            examenActualizado.setExamenInscripto(!examenActualizado.getExamenInscripto());

            log.info("Nuevo estado inscripto: {}",
                    examenActualizado.getExamenInscripto());

            Examen examenGuardado = examenRepository.save(examenActualizado);

            log.info("Examen actualizado correctamente. ID: {}", examenGuardado.getId());
            log.info("==== FIN registrarExamen (actualización) ====");

            return examenGuardado;
        }

        log.info("No existe examen previo. Se procederá a crear uno nuevo.");

        // 4️⃣ Obtener cursada
        log.info("Buscando Cursada con ID: {}", cursadaId);
        Cursada cursada = cursadaService
                .getCursadaById(cursadaId)
                .orElseThrow(() -> {
                    log.error("Cursada no encontrada con ID: {}", cursadaId);
                    return new RuntimeException("Cursada no encontrada con ID: " + cursadaId);
                });

        log.info("Cursada encontrada. ID: {}", cursada.getId());

        // 5️⃣ Crear nota
        log.info("Creando nueva Nota...");

        Nota nota = new Nota();
        nota.setNotaCondicion(condicionExamen);
        nota.setNotaEstado("Pendiente");
        nota.setCursada(cursada);
        nota.setNotaFechaNota(cursadaExamen.getFecha());

        Nota notaGuardada = notaService.guardarNota(nota);

        log.info("Nota creada correctamente. ID: {}, Estado: {}",
                notaGuardada.getNotaId(),
                notaGuardada.getNotaEstado());

        // 6️⃣ Crear examen
        log.info("Creando nuevo Examen...");

        Examen examen = new Examen();
        examen.setStatus("Pendiente");
        examen.setNota(notaGuardada);
        examen.setPermiso(permiso);
        examen.setCursadaExamen(cursadaExamen);
        examen.setExamenInscripto(true);

        Examen examenGuardado = examenRepository.save(examen);

        log.info("Examen creado correctamente. ID: {}, NotaID: {}",
                examenGuardado.getId(),
                notaGuardada.getNotaId());

        log.info("==== FIN registrarExamen (creación) ====");

        return examenGuardado;
    }

    @Transactional
    public CursadaExamen obtenerOCrearCursadaExamen(
            Materia materia,
            Turno turno) {

        return cursadaExamenRepository
                .findByMateriaIdAndTurno_TurnoId(materia.getMateriaId(), turno.getTurnoId())
                .orElseGet(() -> {

                    CursadaExamen nueva = new CursadaExamen();
                    nueva.setTurno(turno);
                    nueva.setMateriaId(materia.getMateriaId());
                    nueva.setFecha(turno.getTurnoLimite()); // o lógica que uses

                    return cursadaExamenRepository.save(nueva);
                });
    }

    @Override
    public void darDeBajaExamen(Long examenId) {
        Examen examen = examenRepository.findById(examenId)
                .orElseThrow(() -> new RuntimeException("❌ El examen con ID " + examenId + " no existe."));

        examen.setExamenInscripto(false); // ✅ Se marca como dado de baja
        examenRepository.save(examen);
    }

    @Override
    public void activarExamen(Long examenId) {
        Examen examen = examenRepository.findById(examenId)
                .orElseThrow(() -> new RuntimeException("❌ El examen con ID " + examenId + " no existe."));

        examen.setExamenInscripto(true); // ✅ Se reactiva el examen
        examenRepository.save(examen);
    }

    @Override
    public Optional<CursadaExamen> obtenerPorMateriaYTurno(String materiaId, String turnoId) {
        return cursadaExamenRepository.findByMateriaIdAndTurno_TurnoId(materiaId, turnoId);
    }



    @Override
    public void deleteExamenByNotaId(Long id) {
        examenRepository.deleteByNota_NotaId(id);
    }

}