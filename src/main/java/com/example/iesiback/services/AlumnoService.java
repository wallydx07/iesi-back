package com.example.iesiback.services;

import com.example.iesiback.dto.AlumnoAsistenciaDTO;
import com.example.iesiback.entities.Alumno;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AlumnoService {
    Alumno findAlumnoById(String id);
    Alumno obtenerAlumnoPorLegajoId(String legajoId);
    List<Alumno> obtenerAlumnos();
    Optional<Alumno> findById(String id);
    Alumno save(Alumno alumno);
    boolean delete(String id);

    List<String> buscarPorDniApellidoNombre(String busqueda);

    List<Alumno> buscarPorDni(String dni);
    Alumno createAlumno(Alumno alumno);
    List<String> buscarPorApellidoYCarrera(String apellido, String carreraNombre);

    @Transactional
    void cambioDNI(String dniActual, Long dniCorrecto);

//    List<AlumnoExamenDTO> AlumnoExamenDTO(String apellido, String carreraNombre);



}
