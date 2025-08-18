package com.example.iesiback.services;

import com.example.iesiback.entities.Equivalencia;
import com.example.iesiback.repositories.EquivalenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquivalenciaServiceImpl implements EquivalenciaService {

    @Autowired
    private EquivalenciaRepository equivalenciaRepository;

    @Override
    public List<Equivalencia> findAll() {
        return equivalenciaRepository.findAll();
    }

    @Override
    public Optional<Equivalencia> findById(Long id) {
        return equivalenciaRepository.findById(id);
    }

    @Override
    public Equivalencia save(Equivalencia equivalencia) {
        return equivalenciaRepository.save(equivalencia);
    }

    @Override
    public void deleteById(Long id) {
        equivalenciaRepository.deleteById(id);
    }

    @Override
    public List<Equivalencia> findByLegajoId(Integer legajoId) {
        return equivalenciaRepository.findByLegajoId(legajoId);
    }

    @Override
    public List<Equivalencia> findByMateriaId(Integer materiaId) {
        return equivalenciaRepository.findByMateriaId(materiaId);
    }
}