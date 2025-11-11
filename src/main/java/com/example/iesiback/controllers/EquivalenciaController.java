package com.example.iesiback.controllers;

import com.example.iesiback.dto.EquivalenciaDetalleDTO;
import com.example.iesiback.entities.Equivalencia;
import com.example.iesiback.services.EquivalenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/equivalencias")
@CrossOrigin(origins = "*") // O ajustá el origen según tu frontend
public class EquivalenciaController {

    private final EquivalenciaService equivalenciaService;

    @Autowired
    public EquivalenciaController(EquivalenciaService equivalenciaService) {
        this.equivalenciaService = equivalenciaService;
    }

    @GetMapping
    public List<Equivalencia> getAll() {
        return equivalenciaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equivalencia> getById(@PathVariable Long id) {
        return equivalenciaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Equivalencia create(@RequestBody Equivalencia equivalencia) {
        return equivalenciaService.save(equivalencia);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equivalencia> update(@PathVariable Long id, @RequestBody Equivalencia equivalencia) {
        if (!equivalenciaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        equivalencia.setId(id.intValue());
        return ResponseEntity.ok(equivalenciaService.save(equivalencia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!equivalenciaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        equivalenciaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/legajo/{legajoId}")
    public List<Equivalencia> getByLegajo(@PathVariable String legajoId) {
        return equivalenciaService.findByLegajoId(legajoId);
    }

    @GetMapping("/materia/{materiaId}")
    public List<Equivalencia> getByMateria(@PathVariable String materiaId) {
        return equivalenciaService.findByMateriaId(materiaId);
    }

    @PostMapping("/crear-con-nota")
    public ResponseEntity<Equivalencia> crearEquivalencia(
            @RequestBody Equivalencia equivalencia,
            @RequestParam Integer cursadaId) {

        Equivalencia creada = equivalenciaService.crearEquivalenciaConNota(equivalencia, cursadaId);
        return ResponseEntity.ok(creada);
    }
    @GetMapping("/detalle")
    public ResponseEntity<List<EquivalenciaDetalleDTO>> obtenerEquivalenciasConDetalle() {
        List<EquivalenciaDetalleDTO> lista = equivalenciaService.obtenerEquivalenciasConDetalle();
        return ResponseEntity.ok(lista);
    }
}
