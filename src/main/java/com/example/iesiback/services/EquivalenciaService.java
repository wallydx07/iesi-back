package com.example.iesiback.services;

import com.example.iesiback.dto.EquivalenciaDTO;
import com.example.iesiback.entities.Equivalencia;

import java.util.List;
import java.util.Optional;

public interface EquivalenciaService {

    List<EquivalenciaDTO> obtenerTodas();

    Optional<Equivalencia> obtenerPorId(Long id);

    Equivalencia registrarEquivalencia(EquivalenciaDTO dto);

    boolean eliminarEquivalencia(Long id);
}