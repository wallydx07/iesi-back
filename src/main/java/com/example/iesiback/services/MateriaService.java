package com.example.iesiback.services;

import com.example.iesiback.dto.MateriaDTO;
import com.example.iesiback.dto.ProcesadoReinscripcionMateriaDTO;
import com.example.iesiback.dto.ReinscripcionMateriaDTO;
import com.example.iesiback.entities.Materia;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MateriaService {
    List<Materia> obtenerTodasMaterias();
    Optional<Materia> obtenerMateriaPorId(String id);
    Materia guardarMateria(Materia materia);
    Materia actualizarMateria(String id, Materia materia);
    void eliminarMateria(String id);
    List<MateriaDTO> obtenerMateriasPorCarrera(String carreraId);
    Materia findMateriaById(String materiaId);

    Optional<Materia> obtenerMateriaPorOrden(Integer orden);

    List<ReinscripcionMateriaDTO> findReinscripciones(Integer cicloLectivo, String carreraNombre, String division);

    List<Materia> findAllByIds(Set<String> ids);
}
