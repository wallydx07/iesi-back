package com.example.iesiback.services;

import com.example.iesiback.entities.ExamenHorario;
import com.example.iesiback.dto.InscripcionExamenDTO;
import com.example.iesiback.entities.*;
import com.example.iesiback.repositories.CursadaExamenRepository;
import com.example.iesiback.repositories.ExamenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
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
            for (Nota nota : notas) {
                if (nota.getNotaCondicion().equals("Cursada")) {
                    if (nota.getNotaEstado().equals("Regular")) {
                        inscripcion.setCondicion("Regular");
                    } else {
                        inscripcion.setCondicion("Libre");
                    }
                }
            }
            Boolean estado = examenRepository.getEstadoExamen(turno, cursada.getMateriaId(), legajoId);
            boolean inscripto = estado != null && estado;  // ✅ Si es null, devuelve false
            inscripcion.setInscripto(inscripto);
//            String fecha = cursadaExamenService.obtenerFechaPorMateriaYTurno(cursada.getMateriaId(), turno);
//            String hora=cursadaExamenService.obtenerHoraPorMateriaYTurno(cursada.getMateriaId(), turno);

            String fecha = "-";
            String hora = "-";

            Optional<CursadaExamen> cursadaExamen =
                    cursadaExamenRepository.findByMateriaIdAndTurno_TurnoId(
                            cursada.getMateriaId(), turno
                    );

            if (cursadaExamen.isPresent()) {
                CursadaExamen ce = cursadaExamen.get();

                // Fecha segura
                fecha = (ce.getFecha() != null)
                        ? ce.getFecha().toString()
                        : "-";

                // Hora segura
                hora = (ce.getHora() != null)
                        ? ce.getHora()
                        : "-";
            }


//            String fecha="-";
//            String hora="-";
//

//
//            ExamenHorario examenHorario = examenHorarioService.findByMateriaIdAndTurnoId(cursada.getMateriaId(), turno)
//                    .orElse(null);
//
//            if (examenHorario != null) {
//                fecha = examenHorario.getFecha().toString();
//                hora = examenHorario.getHora().toString();
//                // Continuás la lógica
//            } else {
//             Optional<CursadaExamen> cursadaExamen=cursadaExamenRepository.findByMateriaIdAndTurno_TurnoId(cursada.getMateriaId(), turno);
//                fecha = cursadaExamen.get().getFecha().toString();
//                hora = cursadaExamen.get().getHora();
//            }

            inscripcion.setHora(hora);
            inscripcion.setFecha(fecha);
            inscripcion.setCorrelativas(cursadaService.obtenerCorrelativasPendientesMateriaId(cursada.getLegajo().getLegajoId(), cursada.getMateriaCarrera().getMateria()));
            inscripciones.add(inscripcion);
        });

        return inscripciones;
    }

    @Override
    public Examen registrarExamen(String legajoId, String turnoId, String materiaId, String condicionExamen, Integer cursadaId) {
        // ✅ 1. Obtener o crear el permiso
        Permiso permiso = permisoService.obtenerOCrearPermiso(legajoId, turnoId);
        // ✅ 2. Verificar si la cursada_examen existe
        Optional<CursadaExamen> cursadaExistente = cursadaExamenRepository.findByMateriaIdAndTurno_TurnoId(materiaId, turnoId);

        // 📌 Depuración: imprimir los parámetros de búsqueda
        System.out.println("🔍 Buscando CursadaExamen con:");
        System.out.println("   🔹 Materia ID: " + materiaId);
        System.out.println("   🔹 Turno ID: " + turnoId);

        if (cursadaExistente.isPresent()) {
            System.out.println("✅ Se encontró CursadaExamen:");
            System.out.println("   🔹 ID: " + cursadaExistente.get().getId());
            System.out.println("   🔹 Fecha: " + cursadaExistente.get().getFecha());
            System.out.println("   🔹 Materia: " + cursadaExistente.get().getMateriaId());
        } else {
            System.out.println("❌ No se encontró ninguna CursadaExamen para los datos especificados.");
        }
        if (cursadaExistente.isEmpty()) {
            throw new RuntimeException("❌ No existe una cursada para la materia y turno especificados.");
        }
        CursadaExamen cursadaExamen = cursadaExistente.get();
        Optional<Examen> examenExistente = examenRepository.findByPermisoAndCursadaExamen(permiso, cursadaExamen);


        if (examenExistente.isPresent()) {
            Examen examenActualizado = examenExistente.get();
            examenActualizado.setExamenInscripto(!examenActualizado.getExamenInscripto()); // Alternar estado
            return examenRepository.save(examenActualizado);
        }else {
            Cursada cursada= this.cursadaService.getCursadaById(cursadaId).get();

            Nota nota = new Nota();
            Nota aux = new Nota();
            nota.setNotaCondicion(condicionExamen);
            nota.setNotaEstado("Pendiente");
            nota.setCursada(cursada);
            aux=notaService.guardarNota(nota);
            Examen examen = new Examen();
            examen.setStatus("Pendiente");
            examen.setNota(aux);
            examen.setPermiso(permiso);
            examen.setCursadaExamen(cursadaExamen);
            examen.setExamenInscripto(true); // Se inscribe por primera vez
            notaService.guardarNota(nota);
            return examenRepository.save(examen);
        }



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





}