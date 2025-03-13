package com.example.iesiback.controllers;

import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.services.CursadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/cursadas")
public class CursadaController {

    @Autowired
    private CursadaService cursadaService;

    @GetMapping
    public List<Cursada> getAllCursadas() {
        return cursadaService.getAllCursadas();
    }

    @PostMapping("/inscribir/{carreraId}")
    public ResponseEntity<Map<String, String>> agregarMateriasACursada(
            @PathVariable String carreraId,
            @RequestBody Legajo legajo) {
        try {
            cursadaService.agregarMateriasACursadaPorCarrera(carreraId, legajo);

            // ✅ Crear un JSON con un mensaje
            Map<String, String> response = new HashMap<>();
            response.put("message", "Materias agregadas correctamente a la cursada.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al agregar materias: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @GetMapping("/{id}")
    public Optional<Cursada> getCursadaById(@PathVariable Integer id) {
        return cursadaService.getCursadaById(id);
    }

    @GetMapping("/getCursadaByLegajoId/{id}")
    public List<Cursada> getCursadaByLegajoId(@PathVariable String id) {
        return cursadaService.findByLegajoId(id);
    }


    @PostMapping
    public Cursada createCursada(@RequestBody Cursada cursada) {
        return cursadaService.saveCursada(cursada);
    }

    @PutMapping("/{id}")
    public Cursada updateCursada(@PathVariable Integer id, @RequestBody Cursada cursadaDetails) {
        return cursadaService.getCursadaById(id)
                .map(cursada -> {
                    cursada.setCursadaInscripto(cursadaDetails.getCursadaInscripto());
                    cursada.setStatus(cursadaDetails.getStatus());
                    cursada.setMateriaCarrera(cursadaDetails.getMateriaCarrera());
                    cursada.setNotas(cursadaDetails.getNotas());
                    return cursadaService.saveCursada(cursada);
                })
                .orElseGet(() -> {
                    cursadaDetails.setId(id);
                    return cursadaService.saveCursada(cursadaDetails);
                });
    }


    @DeleteMapping("/{id}")
    public void deleteCursada(@PathVariable Integer id) {
        cursadaService.deleteCursada(id);
    }
}
