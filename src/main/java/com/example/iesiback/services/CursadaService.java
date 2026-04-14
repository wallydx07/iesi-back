package com.example.iesiback.services;

import com.example.iesiback.dto.CorrelativasFaltantesEstadoDTO;
import com.example.iesiback.dto.NotaCursadaDTO;
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
    Cursada saveCursada(Cursada cursada);
    Optional<Cursada> obtenerCursadaPorLegajoMateriaCarrera(String legajoId, String materiaId, String carreraId);
    Optional<Cursada> findByLegajo_LegajoIdAndMateriaCarrera_Id(String legajoId, int materiaCarreraId);
    void deleteCursada(Integer id);
    List<Cursada> findByLegajoId(String legajoId);

    List<Cursada> getCursadasNoAprobadas(String legajoId);
    List<Cursada> findByLegajoAndMateria(String legajo, String materia);
    Optional<Boolean> obtenerEstadoCursada(String legajoId, String materiaId, String materiaYear, String division);

    String obtenerCorrelativasPendientes(String materiaId, String legajoId);

    List<CorrelativasFaltantesEstadoDTO> obtenerCorrelativasPendientesMateriaId(String legajoId, Materia materia);

    @Transactional
    void eliminarCursada(Integer id);

    Optional<Cursada> buscarPorId(Integer cursadaId);

    Cursada actualizarCursada(Cursada cursada, Cursada cursadaPost);


    List<Cursada> findByMateriaCarrera_Carrera_CarreraId(String carreraId);

    @Transactional
    Cursada obtenerORegistrarCursada(
            Legajo legajo,
            Long materiaCarreraId);


    @Transactional
    Cursada buscarOMasCercanaORegistrar(
            Legajo legajo,
            MateriaCarrera materiaCarrera);
}
