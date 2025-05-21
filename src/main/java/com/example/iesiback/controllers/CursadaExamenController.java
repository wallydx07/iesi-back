package com.example.iesiback.controllers;

import com.example.iesiback.dto.ExamenCursadaDTO;
import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.services.CursadaExamenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
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

    @PostMapping("/crear")
    public ResponseEntity<CursadaExamen> crearCursadaExamen(
            @RequestParam String turnoId,
            @RequestParam String materiaId,
            @RequestParam String fecha,
            @RequestParam String hora // ahora como String directamente
    ) {
        LocalDate fechaExamen = LocalDate.parse(fecha); // esto sigue siendo LocalDate
        CursadaExamen nuevaCursada = cursadaExamenService.crearCursadaExamen(turnoId, materiaId, fechaExamen, hora);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCursada);
    }


    @GetMapping("/todas")
    public ResponseEntity<List<CursadaExamen>> obtenerTodasLasCursadas() {
        List<CursadaExamen> cursadas = cursadaExamenService.obtenerTodasLasCursadas();
        return ResponseEntity.ok(cursadas);
    }

    @GetMapping("/ordenadas")
    public ResponseEntity<List<ExamenCursadaDTO>> obtenerCursadasPorTurno(@RequestParam String turnoId) {
        List<ExamenCursadaDTO> cursadas = cursadaExamenService.obtenerCursadasPorTurno(turnoId);
        return ResponseEntity.ok(cursadas);
    }
}

