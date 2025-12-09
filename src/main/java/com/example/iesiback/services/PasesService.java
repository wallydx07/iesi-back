package com.example.iesiback.services;


import com.example.iesiback.entities.Pases;
import java.util.List;
import java.util.UUID;

public interface PasesService {

    List<Pases> findAll();

    Pases findById(Long id);

    List<Pases> findByTramiteId(Long tramiteId);

    List<Pases> findByUsuarioOrigen(Long usuarioId);

    List<Pases> findByUsuarioDestino(Long usuarioId);

    Pases save(Pases pase);

    // ✅ Implementación del método para destino (área)
    List<Pases> getByParaDestino(Long destinoId);
}
