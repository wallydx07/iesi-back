package com.example.iesiback.services;

import com.example.iesiback.entities.Permiso;
import com.example.iesiback.entities.Turno;
import com.example.iesiback.repositories.PermisoRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class PermisoServiceImpl implements PermisoService {


    private final PermisoRepository permisoRepository;

    // Constructor completo: inyección de todas las dependencias
    public PermisoServiceImpl(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }



    @Override
    public Permiso obtenerOCrearPermiso(String legajoId, String turnoId) {
        Optional<Permiso> permiso = permisoRepository.findPermisoByLegajoAndTurnoOrdered(legajoId, turnoId);
        return permiso.orElseGet(() -> {
            Permiso nuevoPermiso = new Permiso();
            nuevoPermiso.setPermisoLegajoId(legajoId);
            nuevoPermiso.setPermisoFecha(LocalDate.now());
            nuevoPermiso.setPermisoObs("Generado automáticamente");
            return permisoRepository.save(nuevoPermiso);
        });
    }




    @Override
    public Optional<Permiso> findByLegajoAndFecha(String legajoId, LocalDate fecha) {
        return permisoRepository.findByPermisoLegajoIdAndPermisoFecha(legajoId, fecha);
    }

    @Override
    public Optional<Permiso> findPermisoByLegajoAndTurnoOrdered(String legajoId, String turnoId) {
        return permisoRepository.findPermisoByLegajoAndTurnoOrdered(legajoId, turnoId);
    }

    @Override
    public String obtenerCarreraPorLibreta(String libreta) {
        return permisoRepository.obtenerCarreraPorLibreta(libreta);
    }

    @Override
    public int obtenerDniPorLibreta(String libreta) {
        return permisoRepository.obtenerDniPorLibreta(libreta);
    }

    @Override
    public String obtenerNombrePorDni(int dni) {
        return permisoRepository.obtenerNombrePorDni(dni);
    }

    @Override
    public String obtenerApellidoPorDni(int dni) {
        return permisoRepository.obtenerApellidoPorDni(dni);
    }

    @Override
    public Permiso save(Permiso permiso) {
        return permisoRepository.save(permiso);
    }

    @Override
    public void delete(Permiso permiso) {
        permisoRepository.delete(permiso);
    }

}
