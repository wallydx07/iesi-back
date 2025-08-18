package com.example.iesiback.dto;

import java.util.List;

public class EvaluacionCorrelativaResponse {
    private String status;
    private List<String> correlativasDesaprobadas;
    private List<String> correlativasFechaInvalida;

    // Constructor
    public EvaluacionCorrelativaResponse(String status, List<String> correlativasDesaprobadas, List<String> correlativasFechaInvalida) {
        this.status = status;
        this.correlativasDesaprobadas = correlativasDesaprobadas;
        this.correlativasFechaInvalida = correlativasFechaInvalida;
    }

    // Getters y Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getCorrelativasDesaprobadas() { return correlativasDesaprobadas; }
    public void setCorrelativasDesaprobadas(List<String> correlativasDesaprobadas) { this.correlativasDesaprobadas = correlativasDesaprobadas; }

    public List<String> getCorrelativasFechaInvalida() { return correlativasFechaInvalida; }
    public void setCorrelativasFechaInvalida(List<String> correlativasFechaInvalida) { this.correlativasFechaInvalida = correlativasFechaInvalida; }
}
