package com.example.iesiback.controllers;

import com.example.iesiback.dto.PreinscripcionDtos.PreinscripcionPresencialRequest;
import com.example.iesiback.entities.Tramite;
import com.example.iesiback.services.PreinscripcionPublicaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Preinscripción cargada en ventanilla. REQUIERE usuario autenticado:
 * no debe figurar entre las rutas permitAll de SecurityConfig.
 *
 * POST /api/preinscripcion/presencial → crea el Tramite y lo devuelve para cobrarlo en la caja.
 */
@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/preinscripcion/presencial")
public class PreinscripcionPresencialController {

    private final PreinscripcionPublicaService service;

    public PreinscripcionPresencialController(PreinscripcionPublicaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Tramite> crear(@Valid @RequestBody PreinscripcionPresencialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearPresencial(request));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> manejarEstado(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode())
                .body(Map.of("mensaje", e.getReason() != null ? e.getReason() : "No se pudo procesar la solicitud."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> campos.putIfAbsent(f.getField(), f.getDefaultMessage()));
        String primero = campos.values().stream().findFirst().orElse("Revisá los datos ingresados.");
        return ResponseEntity.badRequest().body(Map.of("mensaje", primero, "campos", campos));
    }
}