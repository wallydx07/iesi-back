package com.example.iesiback.services;

import com.example.iesiback.entities.Turno;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TurnoService {
    List<Turno> obtenerTurnos();
  Turno obtenerTurnoPorId(String turnoId);

    Optional<Turno> findById(String turnoId);

    Turno save(Turno turno);
}
