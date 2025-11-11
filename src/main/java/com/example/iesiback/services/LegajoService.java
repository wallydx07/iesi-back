package com.example.iesiback.services;

import com.example.iesiback.entities.Alumno;
import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.Legajo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
public interface LegajoService {
    Legajo findLegajoById(String id);
    List<Legajo> obtenerLegajos();
    Optional<Legajo> findById(String id);
    Legajo guardarLegajo(Legajo legajo, Carrera carrera);
    Legajo updateLegajo(Legajo legajo);
    String generaLegajo(String prefijo);


    @Transactional
    void actualizarAlumnoDNI(Alumno alumnoViejo, Alumno nuevoAlumno);
}
