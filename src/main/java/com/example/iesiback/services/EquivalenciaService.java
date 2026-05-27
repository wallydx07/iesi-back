package com.example.iesiback.services;

import com.example.iesiback.dto.EquivalenciaDTO;
import com.example.iesiback.dto.EquivalenciaDetalleDTO;
import com.example.iesiback.entities.Equivalencia;
import com.example.iesiback.entities.Nota;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EquivalenciaService {


    List<Equivalencia> findAll();

    Optional<Equivalencia> findById(Long id);

    Equivalencia save(Equivalencia equivalencia);

    void deleteById(Long id);

    List<Equivalencia> findByLegajoId(String legajoId);

    List<Equivalencia> findByMateriaId(String materiaId);

    @Transactional
    Equivalencia crearEquivalenciaConNota(Equivalencia equivalencia, Integer cursadaId);

    @Transactional
    Equivalencia crearEquivalenciaNota(Equivalencia equivalencia, Nota nuevaNota);

    List<EquivalenciaDetalleDTO> obtenerEquivalenciasConDetalle();
}