package com.example.iesiback.controllers;

import com.example.iesiback.dto.AlumnoAsistenciaDTO;
import com.example.iesiback.dto.AsistenciaAlumnoDTO;
import com.example.iesiback.dto.AsistenciaResumenDTO;
import com.example.iesiback.dto.InformeAsistenciaDTO;
import com.example.iesiback.entities.AsistenciaAlumno;
import com.example.iesiback.services.AsistenciaAlumnoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/asistencia-alumnos")
public class AsistenciaAlumnoController {

    private final AsistenciaAlumnoService asistenciaAlumnoService;

    public AsistenciaAlumnoController(AsistenciaAlumnoService asistenciaAlumnoService) {
        this.asistenciaAlumnoService = asistenciaAlumnoService;
    }

    @GetMapping
    public List<AsistenciaAlumno> findAll() {
        return asistenciaAlumnoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaAlumno> findById(@PathVariable Integer id) {
        return asistenciaAlumnoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AsistenciaAlumno save(@RequestBody AsistenciaAlumno asistenciaAlumno) {
        return asistenciaAlumnoService.save(asistenciaAlumno);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (asistenciaAlumnoService.findById(id).isPresent()) {
            asistenciaAlumnoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


    //AQUI SE OBTIENE LOS ALUMNOS DE ASISTENCIA///////
    @GetMapping("/detalle")
    public List<AlumnoAsistenciaDTO> getAsistencias(
            @RequestParam Integer idInforme
    ) {
        return asistenciaAlumnoService.obtenerAsistenciasConDetalle(idInforme);
    }

    @PostMapping("/lote")
    public ResponseEntity<Void> guardarLote(@RequestBody List<AsistenciaAlumno> asistencias) {
        asistenciaAlumnoService.guardarTodas(asistencias);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/asistencia")
    public ResponseEntity<List<AsistenciaAlumnoDTO>> getAsistenciaAlumnos(
            @RequestParam String materiaCarreraId) {
        List<AsistenciaAlumnoDTO> asistencias = asistenciaAlumnoService.obtenerAsistencias(materiaCarreraId);
        return ResponseEntity.ok(asistencias);
    }

    @GetMapping("/asistencia/fechas")
    public ResponseEntity<List<InformeAsistenciaDTO>> getFechasAsistencia(
            @RequestParam String materiaCarreraId) {
        return ResponseEntity.ok(asistenciaAlumnoService.obtenerFechasAsistencia(materiaCarreraId));
    }

    @GetMapping("/resumen/{legajoId}")
    public List<AsistenciaResumenDTO> obtenerResumen(@PathVariable String legajoId) {
        int anioActual = Year.now().getValue(); // java.time.Year
        return asistenciaAlumnoService.obtenerResumenAsistencia(legajoId, anioActual);
    }

    @GetMapping("/resumenMaterias/{legajoId}")
    public List<AsistenciaResumenDTO> obtenerResumenMaterias(@PathVariable String legajoId) {
        int anioActual = Year.now().getValue(); // java.time.Year
        return asistenciaAlumnoService.obtenerResumenAsistencia(legajoId, anioActual);
    }
}
