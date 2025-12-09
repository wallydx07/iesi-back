package com.example.iesiback.services;

import com.example.iesiback.dto.ExamenCursadaDTO;
import com.example.iesiback.entities.CursadaExamen;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public interface CursadaExamenService {
    String obtenerFechaPorMateriaYTurno(String materiaId, String turnoId);
    boolean existePorTurnoYMateria(String turnoId, String materiaId);
    String obtenerHoraPorMateriaYTurno(String materiaId, String turnoId);
    List<CursadaExamen> obtenerTodasLasCursadas();
    List<ExamenCursadaDTO> obtenerCursadasPorTurno(String turnoId);
    Optional<CursadaExamen> obtenerPorId(Integer id);
    CursadaExamen crearCursadaExamen(String turnoId, String materiaId, LocalDate fecha, String hora);
    CursadaExamen actualizar(Long id, CursadaExamen dto);
    Optional<CursadaExamen> findById(Integer cursadaExamenId);
}
