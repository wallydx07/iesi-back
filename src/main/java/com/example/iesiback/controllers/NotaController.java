package com.example.iesiback.controllers;

import com.example.iesiback.dto.NotaExamenDTO;
import com.example.iesiback.dto.NotaCursadaDTO;
import com.example.iesiback.dto.NotaMateriaDTO;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.exception.ResourceNotFoundException;
import com.example.iesiback.services.NotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api/notas")
public class NotaController {

    @Autowired
    private NotaService notaService;

    @GetMapping
    public List<Nota> obtenerNotas() {
        return notaService.obtenerNotas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Nota> getNotaById(@PathVariable("id") Long notaId) {
        Nota nota = notaService.obtenerNotaPorId(notaId);
        if(nota == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(nota, HttpStatus.OK);
    }

    @GetMapping("/obtenerTodasNotasPorLegajo/{legajoId}")
    public ResponseEntity<List<NotaMateriaDTO>> obtenerTodasNotasPorLegajo(@PathVariable String legajoId) {
        return ResponseEntity.ok(notaService.obtenerTodasNotasPorLegajo(legajoId));
    }

    @GetMapping("/obtenerTodasNotasPorMateria")
    public ResponseEntity<List<NotaCursadaDTO>> obtenerTodasNotasPorMateria(
            @RequestParam String carreraId,
            @RequestParam String materiaId,
            @RequestParam(required = false) Boolean cursadaInscripto
    ) {
        List<NotaCursadaDTO> notas = notaService.findNotasByCarreraAndMateria(carreraId, materiaId, cursadaInscripto);
        return ResponseEntity.ok(notas);
    }

    @GetMapping("/obtenerTodasNotasPorExamen")
    public ResponseEntity<List<NotaExamenDTO>> obtenerTodasNotasPorExamen(
            @RequestParam Long  cursadaExamenId,
            @RequestParam(required = false) Boolean examenInscripto
    ) {
        List<NotaExamenDTO> examenes = notaService.findExamenesByCursadaExamenIdMateriaCarrera(cursadaExamenId, examenInscripto);
        return ResponseEntity.ok(examenes);
    }
    // Endpoint para crear una nueva nota
    @PostMapping("/crear")
    public ResponseEntity<Nota> crearNota(@RequestBody Nota nota) {
        Nota nuevaNota = notaService.guardarNota(nota);
        return ResponseEntity.ok(nuevaNota);
    }




}

