package com.example.iesiback.controllers;

import com.example.iesiback.entities.Pases;
import com.example.iesiback.services.PasesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pases")
@CrossOrigin(origins = "*")
public class PasesController {

    @Autowired
    private PasesService pasesService;

    // Obtener todos los pases
    @GetMapping
    public List<Pases> getAll() {
        return pasesService.findAll();
    }

    // Crear un nuevo pase
    @PostMapping
    public Pases create(@RequestBody Pases pase) {

        return pasesService.save(pase);
    }

    // Buscar pases por ID del trámite
    @GetMapping("/tramite/{tramiteId}")
    public List<Pases> getByTramite(@PathVariable Long tramiteId) {
        return pasesService.findByTramiteId(tramiteId);
    }

    // Buscar pases por usuario origen
    @GetMapping("/de/{usuarioId}")
    public List<Pases> getByUserFrom(@PathVariable Long usuarioId) {
        return pasesService.findByUsuarioOrigen(usuarioId);
    }

    // Buscar pases por usuario destino (personal)
    @GetMapping("/para-usuario/{usuarioId}")
    public List<Pases> getByUserTo(@PathVariable Long usuarioId) {
        return pasesService.findByUsuarioDestino(usuarioId);
    }

    // Buscar pases por destino (área)
    @GetMapping("/para-destino/{destinoId}")
    public List<Pases> getByDestino(@PathVariable Long destinoId) {
        return pasesService.getByParaDestino(destinoId);
    }
}