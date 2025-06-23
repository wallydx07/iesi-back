package com.example.iesiback.services;

import com.example.iesiback.entities.Alumno;
import com.example.iesiback.repositories.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
    public List<String> buscarPorDniApellidoNombre(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return alumnoRepository.buscarPorDniApellidoNombre(null);
        }
        // Armamos patrón de búsqueda con múltiples palabras
        String[] palabras = busqueda.trim().split("\\s+");
        String busquedaParam = "%" + String.join("%", palabras) + "%"; // Ej: %juan%perez%

        return alumnoRepository.buscarPorDniApellidoNombre(busquedaParam.toLowerCase());
    }



    @Override
    public List<Alumno> buscarPorDni(String dni) {
        return alumnoRepository.buscarPorDni(dni);
    }



    @Override
    public List<String> buscarPorApellidoYCarrera(String busqueda, String carreraNombre) {
        String busquedaParam = null;
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            String[] palabras = busqueda.trim().split("\\s+");
            busquedaParam = "%" + String.join("%", palabras) + "%";
        }
        return alumnoRepository.buscarPorApellidoYCarrera(
                busquedaParam != null ? busquedaParam.toLowerCase() : null,
                carreraNombre != null && !carreraNombre.trim().isEmpty() ? carreraNombre.toLowerCase() : null
        );
    }




//@Override
//public List<AlumnoExamenDTO> AlumnoExamenDTO(String apellido, String carreraNombre) {
//        return alumnoRepository.buscarAlumnos(apellido, carreraNombre);
//    }
}
