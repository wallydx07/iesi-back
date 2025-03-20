package com.example.iesiback.services;

import com.example.iesiback.entities.Turno;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface TurnoService {
    List<Turno> obtenerTurnos();
  Turno obtenerTurnoPorId(String turnoId);
}
