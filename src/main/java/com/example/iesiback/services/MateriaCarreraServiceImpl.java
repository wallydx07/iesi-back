package com.example.iesiback.services;

import com.example.iesiback.entities.MateriaCarrera;
import com.example.iesiback.repositories.MateriaCarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MateriaCarreraServiceImpl implements MateriaCarreraService {

    @Autowired
    private MateriaCarreraRepository materiaCarreraRepository;

    @Override
    public List<MateriaCarrera> obtenerMateriaCarreras() {
        return materiaCarreraRepository.findAll();
    }

@Override
public int obtenerCantidadMateriasPorNivel(String carreraId, String nivel) {
        return materiaCarreraRepository.countMateriasPorNivel(carreraId, nivel) - 1;
    }

    @Override
    public MateriaCarrera obtenerMateriaCarrera(String carreraId, String materiaId) {
        return materiaCarreraRepository.findByCarrera_CarreraIdAndMateria_MateriaId(carreraId, materiaId)
                .orElseThrow(() -> new RuntimeException("MateriaCarrera no encontrada para carreraId: "
                        + carreraId + " y materiaId: " + materiaId));
    }
    @Override
    public List<MateriaCarrera> obtenerMateriasPorCarrera(String carreraId) {
        return materiaCarreraRepository.findByCarrera_CarreraId(carreraId);
    }
    // 🔹 Nuevo método para buscar por ID
    public Optional<MateriaCarrera> obtenerMateriaCarreraPorId(Long id) {
        return materiaCarreraRepository.findById(id);
    }
}
