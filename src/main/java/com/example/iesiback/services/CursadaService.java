package com.example.iesiback.services;

import com.example.iesiback.entities.Cursada;
import java.util.List;
import java.util.Optional;

public interface CursadaService {
    List<Cursada> getAllCursadas();
    Optional<Cursada> getCursadaById(Integer id);
    Cursada saveCursada(Cursada cursada);
    void deleteCursada(Integer id);
    List<Cursada> findByLegajoId(String legajoId);
    List<Cursada> getCursadasNoAprobadas(String legajoId);
}
