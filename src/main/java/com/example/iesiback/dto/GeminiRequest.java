package com.example.iesiback.dto;


// src/main/java/com/example/iesiback/dto/GeminiRequest.java
import java.util.List;

public class GeminiRequest {

    private List<Content> contents; // Clave principal y obligatoria
    private GenerationConfig generationConfig = new GenerationConfig(); // Opcional, pero recomendado

    public GeminiRequest(String userPrompt) {
        // Inicializa 'contents' con el texto del usuario
        this.contents = List.of(new Content(userPrompt));
    }

    // Getters
    public List<Content> getContents() {
        return contents;
    }
    public GenerationConfig getGenerationConfig() {
        return generationConfig;
    }
}