package com.example.iesiback.controllers;

import com.example.iesiback.entities.DocumentoEmitido;
import com.example.iesiback.services.DocumentoEmitidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tramites/{tramiteId}/certificados")
@RequiredArgsConstructor
public class DocumentoEmitidoController {

    private final DocumentoEmitidoService documentoService;

    @GetMapping("/{certificadoId}/documento")
    public ResponseEntity<byte[]> descargar(@PathVariable Integer tramiteId,
                                            @PathVariable Integer certificadoId) {
        DocumentoEmitido doc = documentoService.obtenerOEmitir(tramiteId, certificadoId);
        byte[] pdf = documentoService.obtenerPdf(doc);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"certificado-" + certificadoId + ".pdf\"")
                .body(pdf);
    }
}