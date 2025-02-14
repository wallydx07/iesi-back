package com.example.iesiback.services;
import com.example.iesiback.entities.Alumno;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.repositories.LegajoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LegajoService {

@Autowired
private LegajoRepository LegajoRepository;

public List<Legajo> obtenerLegajos() {
    return LegajoRepository.findAll();
}

    public Optional<Legajo> findById(String id) {
        return LegajoRepository.findById(id);
    }
}