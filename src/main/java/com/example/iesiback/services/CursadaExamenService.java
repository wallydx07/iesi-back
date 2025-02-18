package com.example.iesiback.services;

import com.example.iesiback.entities.CursadaExamen;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CursadaExamenService {
    String obtenerFechaPorMateriaYTurno(String materiaId, String turnoId);
    boolean existePorTurnoYMateria(String turnoId, String materiaId);
     List<CursadaExamen> obtenerTodasLasCursadas();

}
