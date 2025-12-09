package com.example.iesiback.controllers;

import com.example.iesiback.services.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    @Autowired
    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/mejorar")
    public String mejorarRedaccion(@RequestBody String texto) {
        return geminiService.mejorarRedaccion(texto);
    }
}
