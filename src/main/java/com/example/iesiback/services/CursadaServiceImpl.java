package com.example.iesiback.services.impl;

import com.example.iesiback.entities.Cursada;
import com.example.iesiback.repositories.CursadaRepository;
import com.example.iesiback.services.CursadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursadaServiceImpl implements CursadaService {

    @Autowired
    private CursadaRepository cursadaRepository;

    @Override
    public List<Cursada> getAllCursadas() {
        return cursadaRepository.findAll();
    }

    @Override
    public Optional<Cursada> getCursadaById(Integer id) {
        return cursadaRepository.findById(id);
    }

    @Override
    public Cursada saveCursada(Cursada cursada) {
        return cursadaRepository.save(cursada);
    }

    @Override
    public void deleteCursada(Integer id) {
        cursadaRepository.deleteById(id);
    }

    @Override
    public List<Cursada> findByLegajoId(String legajoId) {
        return cursadaRepository.findByCursadaLegajoId(legajoId);
    }

    public List<Cursada> getCursadasNoAprobadas(String legajoId) {
        List<Cursada> cursadas = cursadaRepository.findByCursadaLegajoId(legajoId);

        return cursadas.stream()
                .filter(cursada -> cursada.getCursadaNota() != null) // Evitar NPE si no hay nota
                .filter(cursada -> !cursada.getCursadaNota().getNotaEstado().equalsIgnoreCase("Aprobado")) // Filtrar notas no aprobadas
                .collect(Collectors.toList());
    }

}
