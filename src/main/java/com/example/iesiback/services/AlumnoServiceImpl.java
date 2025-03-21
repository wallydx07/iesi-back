package com.example.iesiback.services;

import com.example.iesiback.dto.AlumnoExamenDTO;
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
    public Alumno createAlumno(Alumno alumno) {
        return alumnoRepository.save(alumno);
    }

    @Override
    public Alumno findAlumnoById(String id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));
    }


@Override
public Alumno obtenerAlumnoPorLegajoId(String legajoId) {
        return alumnoRepository.findAlumnoByLegajoId(legajoId);
    }

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

@Override
    public List<String> buscarAlumnosPorApellido(String apellido) {
        return alumnoRepository.buscarPorApellido(apellido);
    }

    @Override
    public List<Alumno> buscarPorDni(String dni) {
        return alumnoRepository.buscarPorDni(dni);
    }

@Override
public List<AlumnoExamenDTO> buscarAlumnos(String apellido, String carreraNombre) {
        return alumnoRepository.buscarAlumnos(apellido, carreraNombre);
    }
}
