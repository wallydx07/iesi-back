package com.example.iesiback.services;
import com.example.iesiback.dto.ExamenCursadaDTO;
import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.repositories.CursadaExamenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Override
    public List<ExamenCursadaDTO> obtenerCursadasPorTurno(String turnoId) {
        List<Object[]> resultados = cursadaExamenRepository.findByTurno(turnoId);

        return resultados.stream().map(obj -> new ExamenCursadaDTO(
                obj[0].toString(),  // cursadaExamenId
                obj[1].toString(),  // materiaId
                obj[2].toString()   // materiaNombre
        )).collect(Collectors.toList());
    }


}
