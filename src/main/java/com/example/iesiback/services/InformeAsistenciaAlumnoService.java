package com.example.iesiback.services;

import com.example.iesiback.entities.InformeAsistenciaAlumno;

import java.util.List;
import java.util.Optional;

public interface InformeAsistenciaAlumnoService {
    InformeAsistenciaAlumno guardar(InformeAsistenciaAlumno informe);

    InformeAsistenciaAlumno actualizar(Integer id, InformeAsistenciaAlumno informe);

    void eliminar(Integer id);

    Optional<InformeAsistenciaAlumno> obtenerPorId(Integer id);

    List<InformeAsistenciaAlumno> listarTodos();

    List<InformeAsistenciaAlumno> buscarPorMateriaCarreraId(Integer id);

}
