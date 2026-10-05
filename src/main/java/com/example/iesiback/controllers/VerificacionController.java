package com.example.iesiback.controllers;

import com.example.iesiback.dto.VerificacionDTO;
import com.example.iesiback.services.DocumentoEmitidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/public/verificar")
@RequiredArgsConstructor
public class VerificacionController {

    private final DocumentoEmitidoService documentoService;

    @GetMapping("/{token}")
    public VerificacionDTO verificar(@PathVariable UUID token) {
        return documentoService.verificar(token);
    }

    @GetMapping("/{token}/pdf")
    public ResponseEntity<byte[]> descargar(@PathVariable UUID token) {
        byte[] pdf = documentoService.obtenerPdfPublico(token);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"documento-" + token + ".pdf\"")
                .body(pdf);
    }

}