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
            @RequestParam(required = false) String carreraId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String busqueda) {

        String carreraIdParam = (carreraId == null || carreraId.trim().isEmpty()) ? null : carreraId.trim() + "%";

        String busquedaParam = (busqueda == null || busqueda.trim().isEmpty()) ? null : "%" + busqueda.trim() + "%";

        return alumnoLegajoService.obtenerAlumnosLegajos(carreraIdParam, estado, busquedaParam);
    }





    @GetMapping("/alumnos-cursadas")
    public List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosConCursadas(
            @RequestParam String dato,
            @RequestParam String estado,
            @RequestParam String apellido) {
        return alumnoLegajoService.obtenerAlumnosConCursadas(dato, estado, apellido);
    }

    @GetMapping("/legajos-por-materia")
    public List<AlumnoLegajoInscripcionCarreraDTO> obtenerLegajosPorMateria(
            @RequestParam("materiaCarreraId") Long materiaCarreraId,
            @RequestParam("estado") String estado
    ) {
        return alumnoLegajoService.obtenerAlumnosMateriaCursadaId(materiaCarreraId, estado);
    }
}
