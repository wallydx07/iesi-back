package com.example.iesiback.controllers;

import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.entities.Materia;
import com.example.iesiback.services.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/certificado")
public class CertificadoController {

    @Autowired
    private CertificadoService certificadoService;
    private final MateriaService materiaService;
    private final CarreraService carreraService;
    private final CursadaExamenService cursadaExamenService;



    public CertificadoController(MateriaService materiaService,
                                 CarreraService carreraService,
                                 CursadaExamenService cursadaExamenService) {
        this.materiaService = materiaService;
        this.carreraService = carreraService;
        this.cursadaExamenService = cursadaExamenService;
    }

    @GetMapping("/estudianteregular")
    public ResponseEntity<ByteArrayResource> generarFicha(
            @RequestParam String alumnoId,
            @RequestParam String carreraId,
            @RequestParam String autoridades,
            @RequestParam String curso) {

        try {
            // Se obtiene el PDDocument desde el servicio
            PDDocument document = certificadoService.generaRegular(alumnoId, carreraId, autoridades, curso);
            // Convertir PDDocument a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            document.close();
            byte[] pdfBytes = baos.toByteArray();

            // Crear recurso a partir del byte[]
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificado_regular.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/analitico")
    public ResponseEntity<ByteArrayResource> generaAnalitico(
            @RequestParam String legajoId,
            @RequestParam String accion,
            @RequestParam String autoridades) {

        try {
            // Se obtiene el PDDocument desde el servicio
            PDDocument document = certificadoService.generaAnalitico(legajoId, accion, autoridades);
            // Convertir PDDocument a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
           // document.close();
            byte[] pdfBytes = baos.toByteArray();

            // Crear recurso a partir del byte[]
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificado_regular.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @GetMapping("/generarActa")
    public ResponseEntity<byte[]> generarActaExamen(
            @RequestParam String materiaId,
            @RequestParam String carrera,
            @RequestParam Integer cursadaExamenId,
            @RequestParam String modalidad) {

        try {
            Materia materia=this.materiaService.findMateriaById(materiaId);
           // Carrera carrera=this.carreraService.findCarreraById(carreraId);
            CursadaExamen cursadaExamen=this.cursadaExamenService.obtenerPorId(cursadaExamenId).get();
            PDDocument pdf = certificadoService.generaExamen(materia, carrera, cursadaExamen, modalidad);
            // Convertir el PDF a bytes
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            pdf.save(out);
            pdf.close();
            // Configurar la respuesta HTTP con el PDF
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=Acta_Examen.pdf");
            return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
