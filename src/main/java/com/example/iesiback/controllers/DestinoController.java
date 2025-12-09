package com.example.iesiback.controllers;

import com.example.iesiback.entities.Destino;
import com.example.iesiback.services.DestinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/destino")
@CrossOrigin(origins = "*")
public class DestinoController {

    @Autowired
    private DestinoService destinoService;

    @GetMapping
    public List<Destino> getAll() {
        return destinoService.findAll();
    }

    @GetMapping("/{id}")
    public Destino getById(@PathVariable Long id) {
        return destinoService.findById(id);
    }

    @PostMapping
    public Destino create(@RequestBody Destino destino) {
        return destinoService.save(destino);
    }

    @PutMapping("/{id}")
    public Destino update(@PathVariable Long id, @RequestBody Destino destino) {
        return destinoService.update(id, destino);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        destinoService.delete(id);
    }
}