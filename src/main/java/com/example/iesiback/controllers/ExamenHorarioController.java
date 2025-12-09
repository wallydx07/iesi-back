package com.example.iesiback.controllers;


import com.example.iesiback.entities.ExamenHorario;
import com.example.iesiback.services.ExamenHorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/examen-horarios")
@CrossOrigin(origins = "*")
public class ExamenHorarioController {
    @Autowired
    private ExamenHorarioService examenHorarioService;

    @GetMapping
    public List<ExamenHorario> getAll() {
        return examenHorarioService.findAll();
    }

    @GetMapping("/{id}")
    public ExamenHorario getById(@PathVariable Integer id) {
        return examenHorarioService.findById(id);
    }

    @GetMapping("/turno/{turnoId}")
    public List<ExamenHorario> getByTurnoId(@PathVariable String turnoId) {
        return examenHorarioService.findByTurnoId(turnoId);
    }

    @PostMapping
    public ExamenHorario create(@RequestBody ExamenHorario examenHorario) {
        return examenHorarioService.save(examenHorario);
    }

    @PostMapping("/{turnoId}")
    public ExamenHorario createConturno(
            @PathVariable String turnoId,
            @RequestBody ExamenHorario examenHorario) {
        return examenHorarioService.saveConTurno(turnoId, examenHorario);
    }


    @PutMapping("/{turnoId}/{id}")
    public ExamenHorario update(
            @PathVariable String turnoId,
            @PathVariable Integer id,
            @RequestBody ExamenHorario examenHorario) {

        return examenHorarioService.update(id, examenHorario, turnoId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        examenHorarioService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file,
                                            @RequestParam("turnoId") String turnoId) {
        examenHorarioService.procesarCsv(file, turnoId);
        return ResponseEntity.ok("Archivo procesado correctamente.");
    }

    @PostMapping("/lote")
    public ResponseEntity<String> cargarLote(@RequestBody List<ExamenHorario> horarios) {
        horarios.forEach(examenHorarioService::save);
        return ResponseEntity.ok("Lote cargado correctamente.");
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> getHorarioPorMateriaYTurno(@RequestParam String materiaId, @RequestParam String turnoId) {
        return examenHorarioService.findByMateriaIdAndTurnoId(materiaId, turnoId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}