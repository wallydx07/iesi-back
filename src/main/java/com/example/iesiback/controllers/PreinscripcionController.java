package com.example.iesiback.controllers;

import com.example.iesiback.entities.Preinscripcion;
import com.example.iesiback.services.EmailService;
import com.example.iesiback.services.PreinscripcionService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api/preinscripcion")
public class PreinscripcionController {

    @Autowired
    private PreinscripcionService preinscripcionService;

    @Autowired
    private EmailService emailService;


    // Obtener todas las preinscripciones
    @GetMapping
    public List<Preinscripcion> obtenerPreinscripciones() {
        return preinscripcionService.obtenerPreinscripcion();
    }

    // Obtener una preinscripción por ID
    @GetMapping("/{id}")
    public ResponseEntity<Preinscripcion> obtenerPreinscripcionPorId(@PathVariable int id) {
        Optional<Preinscripcion> preinscripcion = preinscripcionService.obtenerPreinscripcionPorId(id);
        return preinscripcion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear una nueva preinscripción
    @PostMapping
    public ResponseEntity<Preinscripcion> crearPreinscripcion(@RequestBody Preinscripcion preinscripcion) {
        Preinscripcion nuevaPreinscripcion = preinscripcionService.guardarPreinscripcion(preinscripcion);
        return ResponseEntity.ok(nuevaPreinscripcion);
    }

    // Actualizar una preinscripción existente
    @PutMapping("/{id}")
    public ResponseEntity<Preinscripcion> actualizarPreinscripcion(@PathVariable int id, @RequestBody Preinscripcion preinscripcion) {
        Optional<Preinscripcion> preinscripcionExistente = preinscripcionService.obtenerPreinscripcionPorId(id);
        if (preinscripcionExistente.isPresent()) {
            preinscripcion.setId(id);  // Asegura que se actualiza el registro correcto
            Preinscripcion preinscripcionActualizada = preinscripcionService.guardarPreinscripcion(preinscripcion);
            return ResponseEntity.ok(preinscripcionActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar una preinscripción por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPreinscripcion(@PathVariable int id) {
        if (preinscripcionService.eliminarPreinscripcion(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
