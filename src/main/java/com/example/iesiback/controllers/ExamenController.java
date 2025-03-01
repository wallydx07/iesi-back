package com.example.iesiback.controllers;

import com.example.iesiback.dto.ExamenRequestDTO;
import com.example.iesiback.dto.InscripcionExamenDTO;
import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Examen;
import com.example.iesiback.services.ExamenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api/examenes")
public class ExamenController {

    @Autowired
    private ExamenService examenService;

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
            @RequestParam String turno) {  // Ahora el turno es un mes numérico

        List<InscripcionExamenDTO> examenes = examenService.completarCursadas(legajoId, turno);
        return ResponseEntity.ok(examenes);
    }

    @PostMapping("/cursadas/registrar")
    public ResponseEntity<Examen> registrarExamen(@RequestBody ExamenRequestDTO request) {

        Logger logger = LoggerFactory.getLogger(ExamenController.class);
        logger.info("📌 Recibiendo solicitud de inscripción:");
       // logger.info("   🔹 Examen: {}", request.getExamen());
        logger.info("   🔹 Legajo ID: {}", request.getLegajoId());
        logger.info("   🔹 Turno ID: {}", request.getTurnoId());
        logger.info("   🔹 Materia ID: {}", request.getMateriaId());
        logger.info("   🔹 Nota ID: {}", request.getNota());
        logger.info("   🔹 Nota ID: {}", request.getExamenCondicion());

        Examen nuevoExamen = examenService.registrarExamen(
           //     request.getExamen(),
                request.getLegajoId(),
                request.getTurnoId(),
                request.getMateriaId(),
                request.getExamenCondicion(),
                request.getCursada()
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
