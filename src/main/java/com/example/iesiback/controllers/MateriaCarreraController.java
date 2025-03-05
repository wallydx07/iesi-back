package com.example.iesiback.controllers;

import com.example.iesiback.entities.MateriaCarrera;
import com.example.iesiback.entities.Turno;
import com.example.iesiback.services.MateriaCarreraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api/materiacarreras")
public class MateriaCarreraController {
    @Autowired
    private MateriaCarreraService materiaCarreraService;


    @GetMapping
    public List<MateriaCarrera> obtenerMateriaCarreras() {
        return materiaCarreraService.obtenerMateriaCarreras();
    }

    @GetMapping("/{carreraId}/{materiaId}")
    public ResponseEntity<MateriaCarrera> obtenerMateriaCarrera(@PathVariable String carreraId,
                                                                @PathVariable String materiaId) {
        MateriaCarrera materiaCarrera = materiaCarreraService.obtenerMateriaCarrera(carreraId, materiaId);
        return ResponseEntity.ok(materiaCarrera);
    }


    // 🔹 Nuevo método para obtener MateriaCarrera por ID
    @GetMapping("/{id}")
    public ResponseEntity<MateriaCarrera> obtenerMateriaCarreraPorId(@PathVariable Long id) {
        Optional<MateriaCarrera> materiaCarrera = materiaCarreraService.obtenerMateriaCarreraPorId(id);
        return materiaCarrera.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

