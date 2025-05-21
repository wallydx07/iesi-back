package com.example.iesiback.services;

import com.example.iesiback.dto.EquivalenciaDTO;
import com.example.iesiback.entities.Equivalencia;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.entities.Cursada;
import com.example.iesiback.repositories.CursadaRepository;
import com.example.iesiback.repositories.EquivalenciaRepository;
import com.example.iesiback.repositories.NotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EquivalenciaServiceImpl implements EquivalenciaService {

    @Autowired
    private EquivalenciaRepository equivalenciaRepository;

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private CursadaRepository cursadaRepository;

    @Override
    public List<EquivalenciaDTO> obtenerTodas() {
    return null;
    }

    @Override
    public Optional<Equivalencia> obtenerPorId(Long id) {
        return equivalenciaRepository.findById(id);
    }

    @Override
    public Equivalencia registrarEquivalencia(EquivalenciaDTO dto) {
  return null;
    }

    @Override
    public boolean eliminarEquivalencia(Long id) {
        if (!equivalenciaRepository.existsById(id)) return false;
        equivalenciaRepository.deleteById(id);
        return true;
    }
}