package com.example.iesiback.services;
import com.example.iesiback.entities.Preinscripcion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface PreinscripcionService {
    List<Preinscripcion> obtenerPreinscripcion();
    Optional<Preinscripcion> obtenerPreinscripcionPorId(int id);
    Preinscripcion guardarPreinscripcion(Preinscripcion preinscripcion);
    boolean eliminarPreinscripcion(int id);
}
