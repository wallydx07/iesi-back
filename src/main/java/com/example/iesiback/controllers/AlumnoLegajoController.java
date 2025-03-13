package com.example.iesiback.controllers;

import com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO;
import com.example.iesiback.services.AlumnoLegajoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api")
public class AlumnoLegajoController {

    @Autowired
    private AlumnoLegajoService alumnoLegajoService;

    @GetMapping("/alumnos-legajos")
    public List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosLegajos(
            @RequestParam String dato,
            @RequestParam String estado,
            @RequestParam String apellido) {
        return alumnoLegajoService.obtenerAlumnosLegajos(dato, estado, apellido);
    }
}
