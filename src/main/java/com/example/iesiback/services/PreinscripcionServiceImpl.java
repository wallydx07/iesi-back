package com.example.iesiback.services;

import com.example.iesiback.entities.Preinscripcion;
import com.example.iesiback.repositories.PreinscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PreinscripcionServiceImpl implements PreinscripcionService {

    @Autowired
    private PreinscripcionRepository preinscripcionRepository;

    @Override
    public List<Preinscripcion> obtenerPreinscripcion() {
        return preinscripcionRepository.findAll();
    }

    @Override
    public Optional<Preinscripcion> obtenerPreinscripcionPorId(int id) {
        return preinscripcionRepository.findById(id);
    }

    @Override
    public Preinscripcion guardarPreinscripcion(Preinscripcion preinscripcion) {
        return preinscripcionRepository.save(preinscripcion);
    }

    @Override
    public boolean eliminarPreinscripcion(int id) {
        if (preinscripcionRepository.existsById(id)) {
            preinscripcionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
