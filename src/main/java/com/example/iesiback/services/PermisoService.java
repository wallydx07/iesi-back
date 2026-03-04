package com.example.iesiback.services;

import com.example.iesiback.entities.Permiso;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public interface PermisoService {
    Permiso obtenerOCrearPermiso(String legajoId, String turnoId);

    Optional<Permiso> findByLegajoAndFecha(String legajoId, LocalDate fecha);

    Optional<Permiso> findPermisoByLegajoAndTurnoOrdered(String legajoId, String turnoId);

    String obtenerCarreraPorLibreta(String libreta);

    int obtenerDniPorLibreta(String libreta);

    String obtenerNombrePorDni(int dni);

    String obtenerApellidoPorDni(int dni);

    Permiso save(Permiso permiso);

    void delete(Permiso permiso);
}
