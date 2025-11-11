package com.example.iesiback.services;

import com.example.iesiback.dto.AlumnoAsistenciaDTO;
import com.example.iesiback.dto.AsistenciaAlumnoDTO;
import com.example.iesiback.dto.AsistenciaResumenDTO;
import com.example.iesiback.dto.InformeAsistenciaDTO;
import com.example.iesiback.entities.AsistenciaAlumno;

import java.util.List;
import java.util.Optional;

public interface AsistenciaAlumnoService {
    List<AsistenciaAlumno> findAll();
    Optional<AsistenciaAlumno> findById(Integer id);
    AsistenciaAlumno save(AsistenciaAlumno asistenciaAlumno);
    void deleteById(Integer id);
    List<AlumnoAsistenciaDTO> obtenerAsistenciasConDetalle(Integer idInforme);
    void guardarTodas(List<AsistenciaAlumno> asistencias);
    List<AsistenciaAlumnoDTO> obtenerAsistencias(String materiaCarreraId);
    List<InformeAsistenciaDTO> obtenerFechasAsistencia(String materiaCarreraId);
    List<AsistenciaResumenDTO> obtenerResumenAsistencia(String legajoId, int anioActual);
}
