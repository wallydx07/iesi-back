package com.example.iesiback.controllers;

import com.example.iesiback.entities.Alumno;
import com.example.iesiback.entities.Aporte;
import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.services.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/ficha")
public class FichaInscripcionController {

    private final FichaInscripcionService fichaInscripcionService;
    private final AlumnoService alumnoService;
    private final LegajoService legajoService;
    private final AporteService aporteService;
    private final CarreraService carreraService;

    public FichaInscripcionController(FichaInscripcionService fichaInscripcionService,
                                      AlumnoService alumnoService,
                                      LegajoService legajoService,
                                      AporteService aporteService,
                                      CarreraService carreraService) {
        this.fichaInscripcionService = fichaInscripcionService;
        this.alumnoService = alumnoService;
        this.legajoService = legajoService;
        this.aporteService = aporteService;
        this.carreraService = carreraService;
    }

    @GetMapping("/generar")
    public ResponseEntity<ByteArrayResource> generarFicha(
            @RequestParam String alumnoId,
            @RequestParam String legajoId,
            @RequestParam Integer aporteId,
            @RequestParam String carreraId) {
        try {
            Alumno alumno=this.alumnoService.findAlumnoById(alumnoId);
            Legajo legajo=this.legajoService.findLegajoById(legajoId);
            Aporte aporte=this.aporteService.findAporteById(aporteId);
            Carrera carrera=this.carreraService.findCarreraById(carreraId);



            byte[] pdfBytes = fichaInscripcionService.generarFichaInscripcion(alumno, legajo,aporte,carrera);

            // Crear recurso a partir del byte[]
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Ficha_Inscripcion.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
