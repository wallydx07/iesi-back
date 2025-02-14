package com.example.iesiback.controllers;

import com.example.iesiback.entities.MateriaCarrera;
import com.example.iesiback.entities.Turno;
import com.example.iesiback.services.MateriaCarreraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api/materiacarreras")
public class MateriaCarreraController {
    @Autowired
    private MateriaCarreraService materiaCarreraService;

    @GetMapping
    public List<MateriaCarrera> obtenerMateriaCarreras() {
        return materiaCarreraService.obtenerMateriaCarreras();
    }
}

