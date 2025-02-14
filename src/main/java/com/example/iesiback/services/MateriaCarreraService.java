package com.example.iesiback.services;

import com.example.iesiback.entities.MateriaCarrera;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MateriaCarreraService {
    List<MateriaCarrera> obtenerMateriaCarreras();
}
