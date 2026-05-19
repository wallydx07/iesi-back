package com.example.iesiback.services;

import com.example.iesiback.dto.EquivalenciaDetalleDTO;
import com.example.iesiback.entities.Equivalencia;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.enums.EstadoNota;
import com.example.iesiback.enums.EstadoCondicion;
import com.example.iesiback.repositories.EquivalenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EquivalenciaServiceImpl implements EquivalenciaService {

    @Autowired
    private EquivalenciaRepository equivalenciaRepository;

    @Autowired
    NotaService notaService;

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
    public List<Equivalencia> findByLegajoId(String legajoId) {
        return equivalenciaRepository.findByLegajoId(legajoId);
    }

    @Override
    public List<Equivalencia> findByMateriaId(String materiaId) {
        return equivalenciaRepository.findByMateriaId(materiaId);
    }

    @Transactional
    @Override
    public Equivalencia crearEquivalenciaConNota(Equivalencia equivalencia, Integer cursadaId) {
        // 1️⃣ Crear la nota
        Nota nuevaNota = new Nota();
        nuevaNota.setNotaCondicion(EstadoCondicion.EQUIVALENCIA);
        nuevaNota.setNotaEstado(EstadoNota.PENDIENTE);
        nuevaNota.setNotaFechaNota(LocalDate.now());
        notaService.saveNotaWithCursadsa(nuevaNota, cursadaId);

        // 2️⃣ Asignar el ID de la nota a la equivalencia
        equivalencia.setNota(nuevaNota);
        return equivalenciaRepository.save(equivalencia);
    }

    @Override
    public List<EquivalenciaDetalleDTO> obtenerEquivalenciasConDetalle() {
        return equivalenciaRepository.obtenerEquivalenciasConDetalle();
    }
}