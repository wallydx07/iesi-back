package com.example.iesiback.controllers;


import com.example.iesiback.services.PermisoService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
@CrossOrigin(origins={"http://localhost:4200"})
@RestController
@RequestMapping("/api/permisos")
public class PermisoController {

    private final PermisoService permisoService;



    @Autowired
    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

    @PostMapping("/generar")
    public ResponseEntity<ByteArrayResource> generarPermiso(
            @RequestParam String libreta,
            @RequestParam String turno,
            @RequestParam String usuarioNombre
    ) {
        try {
            System.out.println("✅ Recibiendo solicitud para generar PDF: Libreta=" + libreta + ", Turno=" + turno + ", Usuario=" + usuarioNombre);

            // Generar el PDF en memoria
            PDDocument documento = permisoService.generaPermiso(libreta, turno, usuarioNombre);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            documento.save(outputStream);
            documento.close();

            byte[] pdfBytes = outputStream.toByteArray();
            System.out.println("✅ PDF generado correctamente, tamaño: " + pdfBytes.length + " bytes");

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=permiso.pdf")
                    .body(resource);

        } catch (Exception e) {
            System.out.println("❌ Error al generar el PDF:");
            e.printStackTrace(); // Imprime el error en la consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
