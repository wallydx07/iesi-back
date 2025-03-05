package com.example.iesiback.controllers;

import com.example.iesiback.dto.MateriaDTO;
import com.example.iesiback.dto.ProcesadoReinscripcionMateriaDTO;
import com.example.iesiback.entities.Materia;
import com.example.iesiback.services.MateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/materias")
@CrossOrigin(origins = "http://localhost:4200") // Permitir conexión desde Angular
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    @GetMapping
    public List<Materia> obtenerTodasMaterias() {
        return materiaService.obtenerTodasMaterias();
    }



    @GetMapping("/{id}")
    public ResponseEntity<Materia> obtenerMateriaPorId(@PathVariable String id) {
        Optional<Materia> materia = materiaService.obtenerMateriaPorId(id);
        return materia.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Materia> crearMateria(@RequestBody Materia materia) {
        Materia nuevaMateria = materiaService.guardarMateria(materia);
        return ResponseEntity.ok(nuevaMateria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Materia> actualizarMateria(@PathVariable String id, @RequestBody Materia materia) {
        Materia materiaActualizada = materiaService.actualizarMateria(id, materia);
        if (materiaActualizada != null) {
            return ResponseEntity.ok(materiaActualizada);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMateria(@PathVariable String id) {
        materiaService.eliminarMateria(id);
        return ResponseEntity.noContent().build();
    }

        @GetMapping("/carrera/{carreraId}")
        public List<MateriaDTO> obtenerMateriasPorCarrera(@PathVariable String carreraId) {
            return materiaService.obtenerMateriasPorCarrera(carreraId);
        }
    @GetMapping("/reinscripciones")
    public List<ProcesadoReinscripcionMateriaDTO> obtenerReinscripciones(
            @RequestParam("cicloLectivo") Integer cicloLectivo,
            //@RequestParam("carreraNombre") String carreraNombre,
            @RequestParam("legajoId") String legajoId
    ) {
        //return materiaService.obtenerReinscripciones(cicloLectivo, carreraNombre,legajoId);
        return materiaService.obtenerReinscripciones(cicloLectivo,legajoId);
    }
}
