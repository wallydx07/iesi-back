package com.example.iesiback.controllers;

import com.example.iesiback.repositories.HtmlService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/aportes")
public class AporteController {

    private final HtmlService htmlService;

    public AporteController(HtmlService htmlService) {
        this.htmlService = htmlService;
    }

    @PostMapping("/recibo")
    public ResponseEntity<byte[]> generarRecibo(@RequestBody Map<String, String> datos) {
        try {
            // Validación de datos
            if (!datos.containsKey("legajoId") || !datos.containsKey("tipoPago")) {
                return ResponseEntity.badRequest().body("Faltan parámetros obligatorios".getBytes());
            }

            // Crear datos dinámicos
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("nombre", "Geronimo Walter");
            templateData.put("curso", "Spring Angular");
            templateData.put("legajo", datos.get("legajoId"));
            templateData.put("fecha_pago", "2024-02-17");
            templateData.put("monto", "$5,000.00");
            templateData.put("usuario", "jperez");
            templateData.put("id_pago", "REC-001");
            templateData.put("id_transaccion", "TRX-001");
            // Generar HTML
            String html = htmlService.procesarHtml("recibo", templateData);
            System.out.println("HTML Generado: \n" + html); // Debug para revisar el HTML

            // Convertir HTML a PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            renderer.finishPDF();


            byte[] pdfBytes = outputStream.toByteArray();
            outputStream.close();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=recibo.pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace(); // Imprimir error en consola para depuración
            return ResponseEntity.internalServerError().body(("Error en la generación del PDF: " + e.getMessage()).getBytes());
        }
    }
}