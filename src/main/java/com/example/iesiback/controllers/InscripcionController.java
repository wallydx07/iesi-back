package com.example.iesiback.controllers;

import com.example.iesiback.entities.Inscripcion;
import com.example.iesiback.entities.Observacione;
import com.example.iesiback.services.InscripcionService;
import com.example.iesiback.services.ObservacionesService;
import com.example.iesiback.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
@CrossOrigin(origins={"http://localhost:4200"})
@RestController
@RequestMapping("/api/inscripcion")
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final ObservacionesService observacionesService;

    private final UserService userService;

    public InscripcionController(InscripcionService inscripcionService,
                                 ObservacionesService observacionesService, UserService userService) {
        this.observacionesService=observacionesService;
        this.inscripcionService = inscripcionService;
        this.userService = userService;
    }

    @GetMapping("/estado-estudiante")
    public ResponseEntity<List<String>> obtenerEstadoEstudiante(@RequestParam String legajoId) {
        List<String> opciones = inscripcionService.rellenarAnyo(legajoId);
        return ResponseEntity.ok(opciones);
    }
    
    @PostMapping
    public ResponseEntity<Inscripcion> crearInscripcion(@RequestBody Inscripcion inscripcion) {
        Inscripcion nuevaInscripcion = inscripcionService.crearInscripcion(inscripcion);
        Observacione observacione = new Observacione();
        observacione.setLegajo(nuevaInscripcion.getLegajo());
        observacione.setUsuario(userService.getAuthenticatedUser().get().getUserApellido());
        observacione.setObservaciones("Inscripcion Carrera");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        observacione.setFecha(LocalDate.parse(LocalDate.now().format(formatter)));
        this.observacionesService.guardarObservacion(observacione);
        return ResponseEntity.ok(nuevaInscripcion);
    }
    @GetMapping("/legajo/{legajoId}")
    public Inscripcion getInscripcionesByLegajoId(@PathVariable String legajoId) {
        return inscripcionService.findByLegajoId(legajoId);
    }
}