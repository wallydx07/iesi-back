package com.example.iesiback.controllers;

import com.example.iesiback.entities.Legajo;
import com.example.iesiback.entities.Observacione;
import com.example.iesiback.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/observaciones")
public class ObservacioneController {

    private UserService userService;
    private final LegajoService legajoService;
    @Autowired
    private ObservacionesService observacionesService;
    public ObservacioneController(LegajoService legajoService, UserService userService) {
        this.legajoService = legajoService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Observacione> crearObservacion(@RequestBody Observacione observacion) {
        if (observacion == null || observacion.getLegajo() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Verificar que el legajo existe
        Optional<Legajo> legajoOpt = legajoService.findById(observacion.getLegajo().getLegajoId());
        if (!legajoOpt.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        observacion.setLegajo(legajoOpt.get());  // Asociar la observación al legajo
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fechaFormateada = LocalDate.now().format(formatter);
        observacion.setFecha(LocalDate.parse(fechaFormateada, formatter));
        observacion.setUsuario(userService.getAuthenticatedUser().get().getUserApellido());
        Observacione nuevaObservacion = observacionesService.guardarObservacion(observacion);
        return new ResponseEntity<>(nuevaObservacion, HttpStatus.CREATED);
    }



    @GetMapping("/{id}")
    public ResponseEntity<List<Observacione>> findByLegajoId(@PathVariable String id) {
        List<Observacione> observaciones = observacionesService.findByLegajoId(id);
        if (observaciones.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(observaciones);
        }
    }

}
