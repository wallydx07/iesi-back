package com.example.iesiback.services;

import com.example.iesiback.entities.Alumno;

import java.util.List;
import java.util.Optional;

public interface AlumnoService {
    Alumno findAlumnoById(String id);

    List<Alumno> obtenerAlumnos();
    Optional<Alumno> findById(String id);
    Alumno save(Alumno alumno);
    boolean delete(String id);
    List<String> buscarAlumnosPorApellido(String apellido);
    List<Alumno> buscarPorDni(String dni);
    Alumno createAlumno(Alumno alumno);
}
