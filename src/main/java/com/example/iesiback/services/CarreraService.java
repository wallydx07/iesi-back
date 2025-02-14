package com.example.iesiback.services;

import com.example.iesiback.entities.Carrera;
import com.example.iesiback.repositories.CarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarreraService {

    @Autowired
    private CarreraRepository carreraRepository;

    public List<Carrera> obtenerCarreras() {
        return carreraRepository.findAll();
    }
}