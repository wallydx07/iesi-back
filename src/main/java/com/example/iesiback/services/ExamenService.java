package com.example.iesiback.services;

import com.example.iesiback.dto.InscripcionExamenDTO;
import com.example.iesiback.entities.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ExamenService {
    List<Examen> getAllExamenes();
    Examen getExamenById(Long id);
    Examen saveExamen(Examen examen);
    void deleteExamen(Long id);

    List<Examen> findByPermisoLegajoIdAndTurnoId(String permisoLegajoId, String turnoId);
    List<InscripcionExamenDTO> completarCursadas(String legajoId, String turno);
    boolean verificarPermisoParaTurno(String permisoLegajoId, String turnoId);
    //Examen registrarExamen(String legajoId, String turnoId, String materiaId, String examenCondicion);
    Examen registrarExamen(String legajoId, Turno turno, Materia materia, String condicionExamen, Integer cursada);

    void darDeBajaExamen(Long examenId);
    void activarExamen(Long examenId);

    Optional<CursadaExamen> obtenerPorMateriaYTurno(String materiaId, String turnoId);

    void deleteExamenByNotaId(Long id);
}
