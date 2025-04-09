package com.example.iesiback.controllers;

import com.example.iesiback.dto.NotaExamenDTO;
import com.example.iesiback.dto.NotaCursadaDTO;
import com.example.iesiback.dto.NotaMateriaDTO;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.exception.ResourceNotFoundException;
import com.example.iesiback.services.CursadaService;
import com.example.iesiback.services.NotaService;
import com.example.iesiback.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/notas")
public class NotaController {

    private final UserService userService;
    private final CursadaService cursadaService;
    @Autowired
    private NotaService notaService;

    public NotaController( UserService userService,
    CursadaService cursadaService) {
        this.userService=userService;
        this.cursadaService=cursadaService;
    }

    @GetMapping
    public List<Nota> obtenerNotas() {
        return notaService.obtenerNotas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Nota> getNotaById(@PathVariable("id") Long notaId) {
        Nota nota = notaService.obtenerNotaPorId(notaId);
        if(nota == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(nota, HttpStatus.OK);
    }

    @GetMapping("/obtenerTodasNotasPorLegajo/{legajoId}")
    public ResponseEntity<List<NotaMateriaDTO>> obtenerTodasNotasPorLegajo(@PathVariable String legajoId) {
        return ResponseEntity.ok(notaService.obtenerTodasNotasPorLegajo(legajoId));
    }

//    @GetMapping("/obtenerTodasNotasPorMateria")
//    public ResponseEntity<List<NotaCursadaDTO>> obtenerTodasNotasPorMateria(
//            @RequestParam String carreraId,
//            @RequestParam String materiaId,
//            @RequestParam(required = false) Boolean cursadaInscripto
//    ) {
//        List<NotaCursadaDTO> notas = notaService.findNotasByCarreraAndMateria(carreraId, materiaId, cursadaInscripto);
//        return ResponseEntity.ok(notas);
//    }


    @GetMapping("/obtenerTodasNotasPorMateria")
    public ResponseEntity<?> obtenerTodasNotasPorMateria(
            @RequestParam String carreraId,
            @RequestParam String materiaId,
            @RequestParam(required = false) Boolean cursadaInscripto
    ) {
        try {
            System.out.println("🔍 Recibiendo parámetros:");
            System.out.println("  carreraId: " + carreraId);
            System.out.println("  materiaId: " + materiaId);
            System.out.println("  cursadaInscripto: " + cursadaInscripto);

            List<NotaCursadaDTO> notas = notaService.findNotasByCarreraAndMateria(
                    carreraId, materiaId, cursadaInscripto != null ? cursadaInscripto : false
            );

            return ResponseEntity.ok(notas);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener notas por materia:");
            e.printStackTrace(); // Muestra la traza completa del error en consola

            return ResponseEntity.status(500).body("Ocurrió un error al buscar las notas: " + e.getMessage());
        }
    }

    @GetMapping("/obtenerTodasNotasPorExamen")
    public ResponseEntity<List<NotaExamenDTO>> obtenerTodasNotasPorExamen(
            @RequestParam Long  cursadaExamenId,
            @RequestParam(required = false) Boolean examenInscripto
    ) {
        List<NotaExamenDTO> examenes = notaService.findExamenesByCursadaExamenIdMateriaCarrera(cursadaExamenId, examenInscripto);
        return ResponseEntity.ok(examenes);
    }

    @PostMapping("/crear")
    public ResponseEntity<Nota> crearNota(@RequestBody Nota nota) {
        Nota nuevaNotaAux = this.notaService.obtenerNotaPorId(nota.getNotaId());
        String notaFecha = nota.getNotaFechaNota();
        if (notaFecha != null && !notaFecha.isEmpty()) {
            LocalDate fecha = LocalDate.parse(notaFecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String fechaFormateada = fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            nota.setNotaFechaNota(fechaFormateada);
        }
        nota.setNotaUsuario(userService.getAuthenticatedUser().get().getUserApellido());
        nota.setCursada(nuevaNotaAux.getCursada());
        Nota nuevaNota = notaService.guardarNota(nota);
        return ResponseEntity.ok(nuevaNota);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarNota(@PathVariable Long id) {
        try {
            notaService.eliminarNota(id);
            return ResponseEntity.ok("Nota y Cursada eliminadas correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/obtenerNotasAnalitico/{legajoId}")
    public ResponseEntity<List<NotaMateriaDTO>> obtenerNotasAnalitico(
            @PathVariable String legajoId) {
        return ResponseEntity.ok(notaService.obtenerTodasNotasPorLegajoAnalitico(legajoId));
    }
}

