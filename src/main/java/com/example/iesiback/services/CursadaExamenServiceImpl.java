package com.example.iesiback.services;
import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.repositories.CursadaExamenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CursadaExamenServiceImpl implements CursadaExamenService {

    @Autowired
    private CursadaExamenRepository cursadaExamenRepository;

    @Override
    public boolean existePorTurnoYMateria(String turnoId, String materiaId) {
        return cursadaExamenRepository.existsByTurno_TurnoIdAndMateriaId(turnoId,materiaId);
    }
    @Override
    public String obtenerFechaPorMateriaYTurno(String materiaId, String turnoId) {
        Optional<LocalDate> fechaOpt = cursadaExamenRepository.findFechaByMateriaIdAndTurnoId(materiaId, turnoId);
        return fechaOpt.map(LocalDate::toString).orElse("No asignado");
    }

    public List<CursadaExamen> obtenerTodasLasCursadas() {
        return cursadaExamenRepository.findAll();
    }


}
