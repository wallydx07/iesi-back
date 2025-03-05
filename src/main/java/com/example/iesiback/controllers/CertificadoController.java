package com.example.iesiback.controllers;

import com.example.iesiback.services.CertificadoService;
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

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api/certificado")
public class CertificadoController {

    @Autowired
    private CertificadoService certificadoService;

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
}
