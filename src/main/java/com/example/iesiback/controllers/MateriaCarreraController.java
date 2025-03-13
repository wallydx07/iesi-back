package com.example.iesiback.controllers;

import com.example.iesiback.dto.MateriaCarreraDTO;
import com.example.iesiback.entities.MateriaCarrera;
import com.example.iesiback.entities.Turno;
import com.example.iesiback.services.MateriaCarreraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/materiacarreras")
public class MateriaCarreraController {
    @Autowired
    private MateriaCarreraService materiaCarreraService;


    @GetMapping
    public List<MateriaCarrera> obtenerMateriaCarreras() {
        return materiaCarreraService.obtenerMateriaCarreras();
    }

  /*  @PatchMapping("/{id}")
    public ResponseEntity<String> actualizarMateriaCarrera(@PathVariable Long id,
                                                           @RequestBody MateriaCarreraDTO materiaCarreraDTO) {
        int filasActualizadas = materiaCarreraService.actualizarMateriaCarrera(id, materiaCarreraDTO);

        if (filasActualizadas > 0) {
            return ResponseEntity.ok("MateriaCarrera actualizada con éxito");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el registro con ID " + id);
        }
    }

*/
    @GetMapping("/{carreraId}/{materiaId}")
    public ResponseEntity<MateriaCarrera> obtenerMateriaCarrera(@PathVariable String carreraId,
                                                                @PathVariable String materiaId) {
        MateriaCarrera materiaCarrera = materiaCarreraService.obtenerMateriaCarrera(carreraId, materiaId);
        return ResponseEntity.ok(materiaCarrera);
    }


    @GetMapping("/{id}")
    public ResponseEntity<MateriaCarrera> obtenerMateriaCarreraPorId(@PathVariable Long id) {
        Optional<MateriaCarrera> materiaCarrera = materiaCarreraService.obtenerMateriaCarreraPorId(id);
        return materiaCarrera.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }



}

