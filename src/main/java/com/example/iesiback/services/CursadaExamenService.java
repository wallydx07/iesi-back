package com.example.iesiback.services;

import com.example.iesiback.dto.ExamenCursadaDTO;
import com.example.iesiback.entities.CursadaExamen;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CursadaExamenService {
    String obtenerFechaPorMateriaYTurno(String materiaId, String turnoId);
    boolean existePorTurnoYMateria(String turnoId, String materiaId);
     List<CursadaExamen> obtenerTodasLasCursadas();
    List<ExamenCursadaDTO> obtenerCursadasPorTurno(String turnoId);
    Optional<CursadaExamen> obtenerPorId(Integer id);
//    CursadaExamen crearCursadaExamen(String turnoId, String materiaId);
}
