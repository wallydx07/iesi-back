package com.example.iesiback.dto;

import com.example.iesiback.entities.Examen;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // ✅ Evita errores con propiedades desconocidas
public class ExamenRequestDTO {
    private Examen examen;
    private String legajoId;
    private String turnoId;
    private String materiaId;

    // ✅ Getters y Setters
    public Examen getExamen() {
        return examen;
    }

    public void setExamen(Examen examen) {
        this.examen = examen;
    }

    public String getLegajoId() {
        return legajoId;
    }

    public void setLegajoId(String legajoId) {
        this.legajoId = legajoId;
    }

    public String getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(String turnoId) {
        this.turnoId = turnoId;
    }

    public String getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(String materiaId) {
        this.materiaId = materiaId;
    }
}

