package com.example.iesiback.services;

import com.example.iesiback.entities.Destino;
import com.example.iesiback.repositories.DestinoRepository;
import com.example.iesiback.services.DestinoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DestinoServiceImpl implements DestinoService {

    private final DestinoRepository destinoRepository;

    public DestinoServiceImpl(DestinoRepository destinoRepository) {
        this.destinoRepository = destinoRepository;
    }

    @Override
    public List<Destino> findAll() {
        return destinoRepository.findAll();
    }

    @Override
    public List<Destino> findActivos() {
        return destinoRepository.findByActivoTrue();
    }

    @Override
    public Destino findById(Long id) {
        return destinoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destino no encontrado con ID: " + id));
    }

    @Override
    public Destino save(Destino destino) {
        destino.setCreatedAt(LocalDateTime.now());
        destino.setActivo(true); // por defecto activo
        return destinoRepository.save(destino);
    }

    @Override
    public Destino update(Long id, Destino datos) {
        Destino destino = findById(id);

        destino.setNombre(datos.getNombre());
        destino.setDescripcion(datos.getDescripcion());
        destino.setActivo(datos.isActivo());

        return destinoRepository.save(destino);
    }

    @Override
    public void delete(Long id) {
        Destino destino = findById(id);
        destino.setActivo(false); // baja lógica
        destinoRepository.save(destino);
    }
}
