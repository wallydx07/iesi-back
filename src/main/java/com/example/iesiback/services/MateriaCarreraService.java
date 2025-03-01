package com.example.iesiback.services;

import com.example.iesiback.entities.MateriaCarrera;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MateriaCarreraService {
    List<MateriaCarrera> obtenerMateriaCarreras();

    List<MateriaCarrera> obtenerMateriasPorCarrera(String carreraId);

    // 🔹 Nuevo método para buscar por ID
    Optional<MateriaCarrera> obtenerMateriaCarreraPorId(Long id);
}
