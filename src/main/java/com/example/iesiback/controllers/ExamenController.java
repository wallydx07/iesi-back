package com.example.iesiback.controllers;

import com.example.iesiback.dto.ExamenRequestDTO;
import com.example.iesiback.dto.InscripcionExamenDTO;
import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.entities.Examen;
import com.example.iesiback.services.ExamenService;
import com.example.iesiback.services.MateriaService;
import com.example.iesiback.services.TurnoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/examenes")
public class ExamenController {

    @Autowired
    private ExamenService examenService;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private TurnoService turnoService;

    @GetMapping
    public ResponseEntity<List<Examen>> getAllExamenes() {
        return ResponseEntity.ok(examenService.getAllExamenes());
    }

    @GetMapping("/verificar-permiso")
    public ResponseEntity<Boolean> verificarPermiso(
            @RequestParam String permisoLegajoId,
            @RequestParam String turnoId) {

        boolean tienePermiso = examenService.verificarPermisoParaTurno(permisoLegajoId, turnoId);

        if (tienePermiso) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Examen> getExamenById(@PathVariable Long id) {
        Examen examen = examenService.getExamenById(id);
        return (examen != null) ? ResponseEntity.ok(examen) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Examen> saveExamen(@RequestBody Examen examen) {
        return ResponseEntity.ok(examenService.saveExamen(examen));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamen(@PathVariable Long id) {
        examenService.deleteExamen(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Examen>> findByPermisoLegajoIdAndTurnoId(
            @RequestParam String permisoLegajoId,
            @RequestParam String turnoId) {
        return ResponseEntity.ok(examenService.findByPermisoLegajoIdAndTurnoId(permisoLegajoId, turnoId));
    }

    @GetMapping("/cursadas")
    public ResponseEntity<List<InscripcionExamenDTO>> completarCursadas(
            @RequestParam String legajoId,
            @RequestParam String turno) {
        List<InscripcionExamenDTO> examenes = examenService.completarCursadas(legajoId, turno);
        System.out.println("Datos devueltos por completarCursadas: " + examenes);
        return ResponseEntity.ok(examenes);
    }


    @PostMapping("/cursadas/registrar")
    public ResponseEntity<Examen> registrarExamen(@RequestBody ExamenRequestDTO request) {

        Examen nuevoExamen = examenService.registrarExamen(
           //     request.getExamen(),
                request.getLegajoId(),
                turnoService.obtenerTurnoPorId(request.getTurnoId()),
                materiaService.findMateriaById( request.getMateriaId()),
                request.getExamenCondicion(),
                request.getCursadaId()
        );
        return ResponseEntity.ok(nuevoExamen);
    }


    @PutMapping("/baja/{examenId}")
    public ResponseEntity<String> darDeBajaExamen(@PathVariable Long examenId) {
        examenService.darDeBajaExamen(examenId);
        return ResponseEntity.ok("✅ Examen dado de baja correctamente.");
    }

    @PutMapping("/activar/{examenId}")
    public ResponseEntity<String> activarExamen(@PathVariable Long examenId) {
        examenService.activarExamen(examenId);
        return ResponseEntity.ok("✅ Examen activado correctamente.");
    }


}
