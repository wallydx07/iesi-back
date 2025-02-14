package com.example.iesiback.services;
import com.example.iesiback.entities.Preinscripcion;
import java.util.List;
import java.util.Optional;

public interface PreinscripcionService {
    List<Preinscripcion> obtenerPreinscripcion();
    Optional<Preinscripcion> obtenerPreinscripcionPorId(int id);
    Preinscripcion guardarPreinscripcion(Preinscripcion preinscripcion);
    boolean eliminarPreinscripcion(int id);
}
