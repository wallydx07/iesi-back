package com.example.iesiback.dto;
// src/main/java/com/example/iesiback/dto/GenerationConfig.java

public class GenerationConfig {
    private double temperature = 0.7;
    private int candidateCount = 1;
    private int maxOutputTokens = 500;

    // Getters
    public double getTemperature() {
        return temperature;
    }
    public int getCandidateCount() {
        return candidateCount;
    }
    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }
}