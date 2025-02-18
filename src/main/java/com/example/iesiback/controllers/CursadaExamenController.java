package com.example.iesiback.controllers;

import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.services.CursadaExamenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursada-examen")
public class CursadaExamenController {

    @Autowired
    private CursadaExamenService cursadaExamenService;

    @GetMapping("/existe")
    public ResponseEntity<Boolean> existePorTurnoYMateria(@RequestParam String turnoId, @RequestParam String materiaId) {
        boolean existe = cursadaExamenService.existePorTurnoYMateria(turnoId, materiaId);
        return ResponseEntity.ok(existe);
    }

    @GetMapping("/fecha")
    public ResponseEntity<String> obtenerFecha(
            @RequestParam String materiaId,
            @RequestParam String turnoId) {

        String fecha = cursadaExamenService.obtenerFechaPorMateriaYTurno(materiaId, turnoId);
        return ResponseEntity.ok(fecha);
    }


    @GetMapping("/todas")
    public ResponseEntity<List<CursadaExamen>> obtenerTodasLasCursadas() {
        List<CursadaExamen> cursadas = cursadaExamenService.obtenerTodasLasCursadas();
        return ResponseEntity.ok(cursadas);
    }
}

