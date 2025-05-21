package com.example.iesiback.controllers;
import com.example.iesiback.entities.InformeAsistenciaAlumno;
import com.example.iesiback.services.InformeAsistenciaAlumnoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // ← agrega esto
@RestController
@RequestMapping("/api/informes-asistencia")
public class InformeAsistenciaAlumnoController {

    private final InformeAsistenciaAlumnoService service;

    public InformeAsistenciaAlumnoController(InformeAsistenciaAlumnoService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<InformeAsistenciaAlumno> crear(@RequestBody InformeAsistenciaAlumno informe) {
        return ResponseEntity.ok(service.guardar(informe));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InformeAsistenciaAlumno> actualizar(@PathVariable Integer id, @RequestBody InformeAsistenciaAlumno informe) {
        return ResponseEntity.ok(service.actualizar(id, informe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InformeAsistenciaAlumno> obtenerPorId(@PathVariable Integer id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<InformeAsistenciaAlumno>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/materia/{id}")
    public ResponseEntity<List<InformeAsistenciaAlumno>> listarPorMateria(@PathVariable Integer id) {
        InformeAsistenciaAlumno dummy = new InformeAsistenciaAlumno();
        dummy.setIdInforme(id);
        return ResponseEntity.ok(service.buscarPorMateriaCarreraId(id));
    }

}
