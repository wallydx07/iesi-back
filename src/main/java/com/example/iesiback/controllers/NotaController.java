package com.example.iesiback.controllers;

import com.example.iesiback.dto.NotaMateriaDTO;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.services.NotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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

   // @GetMapping("/{legajoId}")
   // public ResponseEntity<List<Nota>> obtenerNotasPorLegajo(@PathVariable String legajoId) {
   //     List<Nota> notas = notaService.obtenerNotasPorLegajo(legajoId);
   //     return ResponseEntity.ok(notas);
  //  }

    @GetMapping("/obtenerTodasNotasPorLegajo/{legajoId}")
    public ResponseEntity<List<NotaMateriaDTO>> obtenerTodasNotasPorLegajo(@PathVariable String legajoId) {
        return ResponseEntity.ok(notaService.obtenerTodasNotasPorLegajo(legajoId));
    }




}

