package com.example.iesiback.controllers;

import com.example.iesiback.services.ArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    private final ArchivoService archivoService;

    @Autowired
    public ArchivoController(ArchivoService archivoService) {
        this.archivoService = archivoService;
    }

    @GetMapping("/existe")
    public boolean existePorEtiqueta(@RequestParam String etiqueta) {
        return archivoService.existePorEtiqueta(etiqueta);
    }

    @GetMapping("/descargarPorEtiqueta/{etiqueta}")
    public ResponseEntity<InputStreamResource> descargarArchivosPorEtiqueta(@PathVariable String etiqueta) {
        byte[] pdfBytes = archivoService.descargarArchivosPorEtiqueta(etiqueta); // Obtener el archivo PDF

        if (pdfBytes == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Si no hay archivos, respondemos con 404
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pdfBytes);
        InputStreamResource resource = new InputStreamResource(byteArrayInputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/pdf"); // Establecer el tipo de contenido como PDF
        headers.add("Content-Disposition", "attachment; filename=archivo_" + etiqueta + ".pdf"); // Nombre del archivo

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

}