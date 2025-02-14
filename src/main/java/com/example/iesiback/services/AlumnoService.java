package com.example.iesiback.services;

import com.example.iesiback.entities.Alumno;

import java.util.List;
import java.util.Optional;

public interface AlumnoService {
    List<Alumno> obtenerAlumnos();
    Optional<Alumno> findById(String id);
    Alumno save(Alumno alumno);
    boolean delete(String id);
}
