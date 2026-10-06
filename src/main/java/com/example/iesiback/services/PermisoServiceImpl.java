package com.example.iesiback.services;

import com.example.iesiback.entities.Permiso;
import com.example.iesiback.entities.Tramite;
import com.example.iesiback.repositories.PermisoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

@Service
public class PermisoServiceImpl implements PermisoService {


    private final PermisoRepository permisoRepository;
    private final TramiteService tramiteService;

    // Constructor completo: inyección de todas las dependencias
    public PermisoServiceImpl(PermisoRepository permisoRepository, TramiteService tramiteService) {
        this.permisoRepository = permisoRepository;
        this.tramiteService = tramiteService;
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

    //===============================================

    @Transactional(readOnly = true)
    @Override
    public Permiso buscarPorId(Long id) {
        return permisoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Permiso no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Permiso> buscarPorLegajo(String legajoId) {
        return permisoRepository.findByPermisoLegajoIdOrderByPermisoFechaDesc(legajoId);
    }

    @Transactional(readOnly = true)
    @Override
    public Permiso buscarPorTramite(Integer tramiteId) {
        return permisoRepository.findByTramite_Id(tramiteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El trámite " + tramiteId + " no tiene permiso asociado"));
    }


    // ---------- Alta ----------

    @Transactional
    @Override
    public Permiso crear(Permiso req) {
        Permiso permiso = new Permiso();
        permiso.setPermisoFecha(req.getPermisoFecha() != null ? req.getPermisoFecha() : LocalDate.now());
        permiso.setPermisoObs(req.getPermisoObs());
        permiso.setPermisoLegajoId(req.getPermisoLegajoId());

        if (req.getTramite() != null) {
            Tramite tramite = tramiteService.findById(req.getTramite().getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Trámite no encontrado: " + req.getTramite().getId()));

            if (permisoRepository.existsByTramite_Id(req.getTramite().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "El trámite ya tiene un permiso asociado");
            }

            // Si no mandaron legajo, se toma el del trámite
            if (permiso.getPermisoLegajoId() == null) {
                permiso.setPermisoLegajoId(tramite.getLegajoId());
            }

            permiso.setTramite(tramite); // llena tramite_id
        }

        if (permiso.getPermisoLegajoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Falta el legajo del permiso");
        }

        return permisoRepository.save(permiso);
    }

    // ---------- Modificación (solo fecha y observación) ----------

    @Transactional
    @Override
    public Permiso actualizar(Long id, Permiso req) {
        Permiso permiso = buscarPorId(id);

        if (req.getPermisoFecha() != null) {
            permiso.setPermisoFecha(req.getPermisoFecha());
        }
        if (req.getPermisoObs() != null) {
            permiso.setPermisoObs(req.getPermisoObs());
        }
        // El legajo y el trámite no se cambian una vez creado el permiso

        return permisoRepository.save(permiso);
    }

    // ---------- Baja ----------

    @Transactional
    @Override
    public void eliminar(Long id) {
        Permiso permiso = buscarPorId(id);

        if (permiso.getExamen() != null && !permiso.getExamen().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "No se puede eliminar: el permiso tiene exámenes asociados");
        }

        permisoRepository.delete(permiso);
    }

    @Transactional
    @Override
    public Permiso vincularTramite(Long permisoId, Integer tramiteId) {
        Permiso permiso = buscarPorId(permisoId);

        if (permiso.getTramite() != null) {
            if (permiso.getTramite().getId().equals(tramiteId)) {
                return permiso; // ya estaba vinculado a este mismo trámite
            }
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El permiso ya está vinculado a otro trámite");
        }

        if (permisoRepository.existsByTramite_Id(tramiteId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El trámite ya tiene otro permiso asociado");
        }

        Tramite tramite = tramiteService.findById(tramiteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Trámite no encontrado: " + tramiteId));

        permiso.setTramite(tramite);
        return permisoRepository.save(permiso);
    }
}
