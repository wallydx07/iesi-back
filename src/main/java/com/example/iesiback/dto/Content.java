package com.example.iesiback.dto;

// src/main/java/com/example/iesiback/dto/Content.java
import java.util.List;

public class Content {
    private List<Part> parts;

    public Content(String text) {
        this.parts = List.of(new Part(text)); // Lista que contiene una sola Part
    }
    // Getters
    public List<Part> getParts() {
        return parts;
    }
}