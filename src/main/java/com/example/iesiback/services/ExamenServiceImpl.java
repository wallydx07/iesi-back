package com.example.iesiback.services;

import com.example.iesiback.dto.InscripcionExamenDTO;
import com.example.iesiback.entities.*;
import com.example.iesiback.repositories.CursadaExamenRepository;
import com.example.iesiback.repositories.CursadaRepository;
import com.example.iesiback.repositories.ExamenRepository;
import com.example.iesiback.repositories.NotaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExamenServiceImpl implements ExamenService {
    private PermisoService permisoService; // ✅ Inyectamos PermisoService
    private final ExamenRepository examenRepository;
    private final CursadaExamenService cursadaExamenService;
    private final NotaRepository notaRepository;
    private final CursadaRepository cursadaRepository;
    private final CursadaService cursadaService;
    private final CursadaExamenRepository cursadaExamenRepository;


    @Autowired
    public ExamenServiceImpl(
            ExamenRepository examenRepository,
            CursadaExamenService cursadaExamenService,
            NotaRepository notaRepository,
            CursadaRepository cursadaRepository,
            CursadaService cursadaService,
            CursadaExamenRepository cursadaExamenRepository,
            PermisoService permisoService) {
        this.examenRepository = examenRepository;
        this.cursadaExamenService = cursadaExamenService; // ✅ Ahora está correctamente inyectado
        this.notaRepository = notaRepository;
        this.cursadaRepository = cursadaRepository;
        this.cursadaService = cursadaService;
        this.cursadaExamenRepository = cursadaExamenRepository;
        this.permisoService = permisoService;
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
        System.out.println("Cursada ID: " + cursada.getId() + " - Estado: " + cursada.getStatus());
        InscripcionExamenDTO inscripcion= new InscripcionExamenDTO();
        inscripcion.setCursadaId(cursada.getId());
        inscripcion.setMateriaCarreraId(cursada.getCursadaMateriaCarrera().getId());
        inscripcion.setMateriaOrden(cursada.getCursadaMateriaCarrera().getMateria().getMateriaOrden());
        inscripcion.setCurso(cursada.getCursadaMateriaCarrera().getMateria().getMateriaNivel());
        inscripcion.setMateriaId(cursada.getCursadaMateriaCarrera().getMateria().getMateriaId());
        inscripcion.setMateriaNombre(cursada.getCursadaMateriaCarrera().getMateria().getMateriaNombre());
        List<Nota> notas = new ArrayList<>(cursada.getNotas());
            for (Nota nota : notas) {
        //    if (nota.getNotaCondicion().equals("Cursada")||nota.getNotaCondicion().equals("Cursando")) {
                if (nota.getNotaCondicion().equals("Cursada")) {
                if (nota.getNotaEstado().equals("Regular")) {
                    inscripcion.setCondicion("Regular");
                }else{
                    inscripcion.setCondicion("Libre");
                }
            }
        }
            inscripcion.setFecha(
                    cursada.getCursadaMateriaCarrera().getFecha() != null
                            ? cursada.getCursadaMateriaCarrera().getFecha().toString()
                            : "Fecha no disponible"
            );

            Boolean estado = examenRepository.getEstadoExamen(turno,cursada.getMateriaId(),legajoId);
            boolean inscripto = estado != null && estado;  // ✅ Si es null, devuelve false
                inscripcion.setInscripto(inscripto);

            String fecha = cursadaExamenService.obtenerFechaPorMateriaYTurno(cursada.getMateriaId(), turno);
            inscripcion.setFechaHoraMesa(fecha);
           inscripcion.setCorrelativas(cursadaService.obtenerCorrelativasPendientes(cursada));
            inscripciones.add(inscripcion);
        });

        return inscripciones;
    }

    @Override
    public Examen registrarExamen(Examen examen, String legajoId, String turnoId, String materiaId) {
        // ✅ 1. Obtener o crear el permiso
        Permiso permiso = permisoService.obtenerOCrearPermiso(legajoId, turnoId);

        // ✅ 2. Verificar si la cursada_examen existe
        Optional<CursadaExamen> cursadaExistente = cursadaExamenRepository.findByMateriaIdAndTurno_TurnoId(materiaId, turnoId);

        // 📌 Depuración: imprimir los parámetros de búsqueda
        System.out.println("🔍 Buscando CursadaExamen con:");
        System.out.println("   🔹 Materia ID: " + materiaId);
        System.out.println("   🔹 Turno ID: " + turnoId);

        // 📌 Depuración: imprimir si se encontró o no la cursada
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

        // ✅ 3. Verificar si ya existe un examen para este permiso y cursada
        Optional<Examen> examenExistente = examenRepository.findByPermisoAndCursadaExamen(permiso, cursadaExamen);

        if (examenExistente.isPresent()) {
            // ✅ Si existe, alternar el estado de examen_inscripto
            Examen examenActualizado = examenExistente.get();
            examenActualizado.setExamenInscripto(!examenActualizado.getExamenInscripto()); // Alternar estado
            return examenRepository.save(examenActualizado);
        }

        // ✅ 4. Si no existe, crear un nuevo examen con examen_inscripto = true
        examen.setPermiso(permiso);
        examen.setCursadaExamen(cursadaExamen);
        examen.setExamenInscripto(true); // Se inscribe por primera vez
        return examenRepository.save(examen);
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



}