package com.example.iesiback.services;

import com.example.iesiback.entities.Turno;
import com.example.iesiback.repositories.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurnoServiceImpl implements TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;



    @Override
    public List<Turno> obtenerTurnos() {
        return turnoRepository.findAllByOrderByTurnoLimiteDesc();
    }
}
