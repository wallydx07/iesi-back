package com.example.iesiback.services;

import com.example.iesiback.entities.Alumno;
import com.example.iesiback.repositories.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlumnoServiceImpl implements AlumnoService {

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Override
    public List<Alumno> obtenerAlumnos() {
        return alumnoRepository.findAll();
    }

    @Override
    public Optional<Alumno> findById(String id) {
        return alumnoRepository.findById(id);
    }

    @Override
    public Alumno save(Alumno alumno) {
        return alumnoRepository.save(alumno);
    }

    @Override
    public boolean delete(String id) {
        return alumnoRepository.findById(id).map(alumno -> {
            alumnoRepository.delete(alumno);
            return true;
        }).orElse(false);
    }
}
