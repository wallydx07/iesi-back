package com.example.iesiback.services;

import com.example.iesiback.entities.Inscripcion;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InscripcionService {
    Inscripcion crearInscripcion(Inscripcion inscripcion);
    Inscripcion findByLegajoId(String legajoId);
    boolean existsByAlumnoDniAndCarreraNombre(Long alumnoDni, String carreraId);
    List<String> rellenarAnyo(String legajoId);
}