package com.example.iesiback.services;

import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.entities.Materia;
import com.example.iesiback.entities.MateriaCarrera;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CursadaService {
    @Transactional
    void agregarMateriasACursadaPorCarrera(String carreraId, Legajo legajo);
    List<Cursada> getAllCursadas();
    Optional<Cursada> getCursadaById(Integer id);

    Optional<Cursada> obtenerCursadaPorLegajoMateriaCarrera(String legajoId, String materiaId, String carreraId);

    Cursada saveCursada(Cursada cursada);

    Optional<Cursada> findByLegajo_LegajoIdAndMateriaCarrera_Id(String legajoId, int materiaCarreraId);

    void deleteCursada(Integer id);


    List<Cursada> findByLegajoId(String legajoId);
    List<Cursada> getCursadasNoAprobadas(String legajoId);
    List<String> obtenerCorrelativasPendientes(Cursada cursada);
    List<Cursada> findByLegajoAndMateria(String legajo, String materia);
    Optional<Boolean> obtenerEstadoCursada(String legajoId, String materiaId, String materiaYear);
    List<String> obtenerCorrelativasPendientesMateriaId(String legajoId, Materia materia);

    @Transactional
    void eliminarCursada(Integer id);
}
