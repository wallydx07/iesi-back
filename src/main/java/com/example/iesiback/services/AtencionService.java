package com.example.iesiback.services;

import com.example.iesiback.entities.Atencion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AtencionService {
    List<Atencion> findAll();
    Optional<Atencion> findById(Integer id);
    Atencion save(Atencion atencion);
    void deleteById(Integer id);

    // Búsquedas específicas
    List<Atencion> findByDni(Long dni);
    List<Atencion> findByApellidoNombre(String apellidoNombre);
    List<Atencion> findByCorreo(String correo);
    List<Atencion> findByDestino(String destino);
    List<Atencion> findByUsuario(String usuario);
    List<Atencion> findByResuelto(Boolean resuelto);
    List<Atencion> findByFecha(LocalDate fecha);
}