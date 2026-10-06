package com.example.iesiback.controllers;

import com.example.iesiback.entities.Permiso;
import com.example.iesiback.services.PermisoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/permisos")
public class PermisoController {

    private final PermisoService permisoService;

    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

    @GetMapping("/{id}")
    public Permiso buscarPorId(@PathVariable Long id) {
        return permisoService.buscarPorId(id);
    }

    @GetMapping("/legajo/{legajoId}")
    public List<Permiso> buscarPorLegajo(@PathVariable String legajoId) {
        return permisoService.buscarPorLegajo(legajoId);
    }

    @GetMapping("/tramite/{tramiteId}")
    public Permiso buscarPorTramite(@PathVariable Integer tramiteId) {
        return permisoService.buscarPorTramite(tramiteId);
    }

    @PostMapping
    public ResponseEntity<Permiso> crear(@Valid @RequestBody Permiso req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permisoService.crear(req));
    }

    @PutMapping("/{id}")
    public Permiso actualizar(@PathVariable Long id, @Valid @RequestBody Permiso req) {
        return permisoService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        permisoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}