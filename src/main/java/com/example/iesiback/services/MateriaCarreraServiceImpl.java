package com.example.iesiback.services;

import com.example.iesiback.entities.MateriaCarrera;
import com.example.iesiback.repositories.MateriaCarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MateriaCarreraServiceImpl implements MateriaCarreraService {

    @Autowired
    private MateriaCarreraRepository materiaCarreraRepository;

    @Override
    public List<MateriaCarrera> obtenerMateriaCarreras() {
        return materiaCarreraRepository.findAll();
    }
}
