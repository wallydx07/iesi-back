package com.example.iesiback.controllers;

import com.example.iesiback.dto.MejorarTextoRequest;
import com.example.iesiback.services.OllamaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/ai")
public class OllamaController {

    private final OllamaService ollamaService;

    public OllamaController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/mejorar")
    public ResponseEntity<String> mejorarTexto(@RequestBody MejorarTextoRequest request) throws JsonProcessingException {
        String textoMejorado = ollamaService.mejorarTexto(request.getTexto());
        return ResponseEntity.ok(textoMejorado);
    }
}
