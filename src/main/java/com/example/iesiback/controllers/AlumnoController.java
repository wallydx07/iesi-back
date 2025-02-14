package com.example.iesiback.controllers;
import com.example.iesiback.entities.Alumno;
import com.example.iesiback.entities.Preinscripcion;
import com.example.iesiback.services.AlumnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api/alumnos")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoService;

    @GetMapping
    public List<Alumno> obtenerAlumno() {
        return alumnoService.obtenerAlumnos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alumno> getAlumnoById(@PathVariable String id) {
        return alumnoService.findById(id)
                .map(alumno -> ResponseEntity.ok().body(alumno))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<Alumno> actualizarAlumno(@PathVariable String id, @RequestBody Alumno alumno) {
        Optional<Alumno> alumnoExistente = alumnoService.findById(id);
        if (alumnoExistente.isPresent()) {
            alumno.setAlumnoDni(Long.valueOf(id)); // Asegura que se actualiza el registro correcto
            Alumno alumnoActualizado = alumnoService.save(alumno);
            return ResponseEntity.ok(alumnoActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAlumno(@PathVariable String id) {
        boolean eliminado = alumnoService.delete(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}


