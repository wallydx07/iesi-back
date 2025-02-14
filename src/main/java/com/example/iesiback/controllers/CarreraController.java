package com.example.iesiback.controllers;

import com.example.iesiback.entities.Carrera;
import com.example.iesiback.services.CarreraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins={"http://localhost:4200"})
@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    @Autowired
    private CarreraService carreraService;

    @GetMapping
    public List<Carrera> obtenerCarreras() {
        return carreraService.obtenerCarreras();
    }
}
