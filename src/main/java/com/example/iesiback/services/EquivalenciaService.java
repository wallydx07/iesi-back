package com.example.iesiback.services;

import com.example.iesiback.dto.EquivalenciaDTO;
import com.example.iesiback.entities.Equivalencia;

import java.util.List;
import java.util.Optional;

public interface EquivalenciaService {


    List<Equivalencia> findAll();

    Optional<Equivalencia> findById(Long id);

    Equivalencia save(Equivalencia equivalencia);

    void deleteById(Long id);

    List<Equivalencia> findByLegajoId(Integer legajoId);

    List<Equivalencia> findByMateriaId(Integer materiaId);
}