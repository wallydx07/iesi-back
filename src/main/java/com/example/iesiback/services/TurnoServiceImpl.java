package com.example.iesiback.services;

import com.example.iesiback.entities.Turno;
import com.example.iesiback.repositories.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TurnoServiceImpl implements TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Override
    public List<Turno> obtenerTurnos() {
        return turnoRepository.findAllByOrderByTurnoLimiteDesc();
    }

    @Override
    public Turno obtenerTurnoPorId(String turnoId) {
        return turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + turnoId));
    }

    @Override
    public Optional<Turno> findById(String turnoId) {
        return turnoRepository.findById(turnoId);
    }

    @Override
    public Turno save(Turno turno) {
        return turnoRepository.save(turno);
    }


}
