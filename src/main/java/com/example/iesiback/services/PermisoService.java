package com.example.iesiback.services;

import com.example.iesiback.entities.Permiso;
import org.apache.pdfbox.pdmodel.PDDocument;


public interface PermisoService {
    Permiso obtenerOCrearPermiso(String legajoId, String turnoId);
    public PDDocument generaPermiso(
            String libreta,
            String turno,
            String usuarioNombre
    );
}
