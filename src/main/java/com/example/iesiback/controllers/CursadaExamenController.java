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
import java.util.Optional;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/cursada-examen")
public class CursadaExamenController {

    @Autowired
    private CursadaExamenService cursadaExamenService;

    @GetMapping("/findById")
    public ResponseEntity<Optional<CursadaExamen>> existePorTurnoYMateria(@RequestParam Integer cursadaExamenId) {
        Optional<CursadaExamen> existe = cursadaExamenService.findById(cursadaExamenId);
        return ResponseEntity.ok(existe);
    }

    @GetMapping("/existe")
    public ResponseEntity<Boolean> existePorTurnoYMateria(
            @RequestParam String turnoId,
            @RequestParam String materiaId) {

        boolean existe = cursadaExamenService.existePorTurnoYMateria(turnoId, materiaId);
        return ResponseEntity.ok(existe);
    }


//    @GetMapping("/findById")
//    public ResponseEntity<Boolean> findByTurnoYMateria(@RequestParam String turnoId, @RequestParam String materiaId) {
//        boolean existe = cursadaExamenService.existePorTurnoYMateria(turnoId, materiaId);
//        return ResponseEntity.ok(existe);
//    }

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
            @RequestParam String hora, // ahora como String directamente
            @RequestParam Long docenteDni, // ahora como String directamente
            @RequestParam Long vocal1Dni, // ahora como String directamente
            @RequestParam Long vocal2Dni // ahora como String directamente // ahora como String directamente
    ) {
        LocalDate fechaExamen = LocalDate.parse(fecha); // esto sigue siendo LocalDate
        CursadaExamen nuevaCursada = cursadaExamenService.crearCursadaExamen(
                turnoId, materiaId, fechaExamen, hora, docenteDni, vocal1Dni, vocal2Dni);
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

    @GetMapping("/ordenadasFecha")
    public ResponseEntity<List<ExamenCursadaDTO>> obtenerCursadasPorTurnoFecha(@RequestParam String turnoId) {
        List<ExamenCursadaDTO> cursadas = cursadaExamenService.obtenerCursadasPorTurnoFechaDesc(turnoId);
        return ResponseEntity.ok(cursadas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursadaExamen> actualizar(@PathVariable Long id, @RequestBody CursadaExamen dto) {
        return ResponseEntity.ok(cursadaExamenService.actualizar(id, dto));
    }



}

