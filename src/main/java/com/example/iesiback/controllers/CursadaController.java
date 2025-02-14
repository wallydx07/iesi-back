package com.example.iesiback.controllers;

import com.example.iesiback.entities.Cursada;
import com.example.iesiback.services.CursadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cursadas")
@CrossOrigin(origins = "*")  // Permitir acceso desde el frontend
public class CursadaController {

    @Autowired
    private CursadaService cursadaService;

    @GetMapping
    public List<Cursada> getAllCursadas() {
        return cursadaService.getAllCursadas();
    }

    @GetMapping("/{id}")
    public Optional<Cursada> getCursadaById(@PathVariable Integer id) {
        return cursadaService.getCursadaById(id);
    }

    @GetMapping("/getCursadaByLegajoId/{id}")
    public List<Cursada> getCursadaByLegajoId(@PathVariable String id) {
        return cursadaService.findByLegajoId(id);
    }

    @PostMapping
    public Cursada createCursada(@RequestBody Cursada cursada) {
        return cursadaService.saveCursada(cursada);
    }

    @PutMapping("/{id}")
    public Cursada updateCursada(@PathVariable Integer id, @RequestBody Cursada cursadaDetails) {
        return cursadaService.getCursadaById(id)
                .map(cursada -> {
                    cursada.setCursadaInscripto(cursadaDetails.getCursadaInscripto());
                    cursada.setStatus(cursadaDetails.getStatus());
                    cursada.setCursadaMateriaCarrera(cursadaDetails.getCursadaMateriaCarrera());
                    cursada.setCursadaNota(cursadaDetails.getCursadaNota());
                    return cursadaService.saveCursada(cursada);
                })
                .orElseGet(() -> {
                    cursadaDetails.setId(id);
                    return cursadaService.saveCursada(cursadaDetails);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteCursada(@PathVariable Integer id) {
        cursadaService.deleteCursada(id);
    }
}
