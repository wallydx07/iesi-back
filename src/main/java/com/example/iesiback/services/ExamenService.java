package com.example.iesiback.services;

import com.example.iesiback.dto.InscripcionExamenDTO;
import com.example.iesiback.entities.Examen;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ExamenService {
    List<Examen> getAllExamenes();
    Examen getExamenById(Long id);
    Examen saveExamen(Examen examen);
    void deleteExamen(Long id);
    List<Examen> findByPermisoLegajoIdAndTurnoId(String permisoLegajoId, String turnoId);
    List<InscripcionExamenDTO> completarCursadas(String legajoId, String turno);
    boolean verificarPermisoParaTurno(String permisoLegajoId, String turnoId);
    Examen registrarExamen(Examen examen, String legajoId, String turnoId, String materiaId);
    void darDeBajaExamen(Long examenId);
    void activarExamen(Long examenId);

}
