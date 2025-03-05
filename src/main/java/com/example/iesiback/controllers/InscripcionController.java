package com.example.iesiback.controllers;

import com.example.iesiback.entities.Inscripcion;
import com.example.iesiback.services.InscripcionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins={"http://localhost:4200"})
@RestController
@RequestMapping("/api/inscripcion")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @GetMapping("/estado-estudiante")
    public ResponseEntity<List<String>> obtenerEstadoEstudiante(@RequestParam String legajoId) {
        List<String> opciones = inscripcionService.rellenarAnyo(legajoId);
        return ResponseEntity.ok(opciones);
    }
    
    @PostMapping
    public ResponseEntity<Inscripcion> crearInscripcion(@RequestBody Inscripcion inscripcion) {
        Inscripcion nuevaInscripcion = inscripcionService.crearInscripcion(inscripcion);
        return ResponseEntity.ok(nuevaInscripcion);
    }
    @GetMapping("/legajo/{legajoId}")
    public Inscripcion getInscripcionesByLegajoId(@PathVariable String legajoId) {
        return inscripcionService.findByLegajoId(legajoId);
    }
}