package com.example.iesiback.controllers;

import com.example.iesiback.entities.Carrera;
import com.example.iesiback.services.CarreraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    @Autowired
    private CarreraService carreraService;

    @GetMapping
    public List<Carrera> obtenerCarreras() {
        return carreraService.obtenerCarreras();
    }

    @GetMapping("/ordenadas")
    public ResponseEntity<List<Carrera>> obtenerCarrerasOrdenadas() {
        List<Carrera> carreras = carreraService.obtenerCarrerasOrdenadas();
        return ResponseEntity.ok(carreras);
    }


    @GetMapping("/inscripcion")
    public ResponseEntity<List<Carrera>> obtenerCarrerasInscripcion(@RequestParam String alumnoDni) {
        List<Carrera> carreras = carreraService.obtenerCarreraInstcripcion(alumnoDni);
        return ResponseEntity.ok(carreras);
    }

    @GetMapping("/anio-cursada")
    public ResponseEntity<String> getAnioCursada(@RequestParam String libretaEstudiantil) {
        try {
            String anioCursada = carreraService.obtenerAnioCursada(libretaEstudiantil);
            return ResponseEntity.ok(anioCursada);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
