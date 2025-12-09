package com.example.iesiback.services;

import com.example.iesiback.entities.Destino;
import java.util.List;

public interface DestinoService {

    List<Destino> findAll();

    List<Destino> findActivos();

    Destino findById(Long id);

    Destino save(Destino destino);

    Destino update(Long id, Destino destino);

    void delete(Long id); // Baja lógica: activo = false
}
