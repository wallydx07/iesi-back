package com.example.iesiback.dto;

public class ExamenCursadaDTO {
    private String cursadaExamenId;
    private String materiaId;
    private String materiaNombre;

    public ExamenCursadaDTO(String cursadaExamenId, String materiaId, String materiaNombre) {
        this.cursadaExamenId = cursadaExamenId;
        this.materiaId = materiaId;
        this.materiaNombre = materiaNombre;
    }

    public String getCursadaExamenId() {
        return cursadaExamenId;
    }

    public void setCursadaExamenId(String cursadaExamenId) {
        this.cursadaExamenId = cursadaExamenId;
    }

    public String getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(String materiaId) {
        this.materiaId = materiaId;
    }

    public String getMateriaNombre() {
        return materiaNombre;
    }

    public void setMateriaNombre(String materiaNombre) {
        this.materiaNombre = materiaNombre;
    }
}
