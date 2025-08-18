package com.example.iesiback.controllers;

import com.example.iesiback.entities.Turno;
import com.example.iesiback.services.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    @GetMapping
    public List<Turno> obtenerTurnos() {
        return turnoService.obtenerTurnos();
    }

    @PostMapping
    public ResponseEntity<Turno> createTurno(@RequestBody Turno turno) {
        return ResponseEntity.ok(turnoService.save(turno));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Turno> updateTurno(@PathVariable String id, @RequestBody Turno turno) {
        return turnoService.findById(id)
                .map(existingTurno -> {
                    turno.setTurnoId(id);
                    return ResponseEntity.ok(turnoService.save(turno));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

