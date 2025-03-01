package com.example.iesiback.controllers;

import com.example.iesiback.entities.Observacione;
import com.example.iesiback.services.ObservacionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins={"http://localhost:4200"})
@RestController
@RequestMapping("/api/observaciones")
public class ObservacioneController {

    @Autowired
    private ObservacionesService observacionesService;

    @GetMapping("/{id}")
    public ResponseEntity<List<Observacione>> findByLegajoId(@PathVariable String id) {
        List<Observacione> observaciones = observacionesService.findByLegajoId(id);
        if (observaciones.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(observaciones);
        }
    }

}
