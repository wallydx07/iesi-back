package com.example.iesiback.services;
import com.example.iesiback.entities.Observacione;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ObservacionesService {
    @Autowired
    private com.example.iesiback.repositories.ObservacionesRepository ObservacionesRepository;

    public List<Observacione> findByLegajoId(String legajoId) {
        // return ObservacionesRepository.findByLegajoId(legajoId);
        return ObservacionesRepository.findByLegajo_LegajoId(legajoId);
    }
    public Observacione guardarObservacion(Observacione observacion) {
        return ObservacionesRepository.save(observacion);
    }

    public Optional<Observacione> obtenerUltimaObservacionPorLegajo(String legajoId) {
        return Optional.ofNullable(ObservacionesRepository.findTopByLegajo_LegajoIdOrderByFechaDesc(legajoId));
    }
}
