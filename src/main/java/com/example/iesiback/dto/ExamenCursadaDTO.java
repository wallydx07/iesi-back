package com.example.iesiback.dto;

public class ExamenCursadaDTO {
    private String cursadaExamenId;
    private String materiaId;
    private String materiaNombre;
    private String carreraNombre;

    public ExamenCursadaDTO(String cursadaExamenId, String materiaId, String materiaNombre, String carreraNombre) {
        this.cursadaExamenId = cursadaExamenId;
        this.materiaId = materiaId;
        this.materiaNombre = materiaNombre;
        this.carreraNombre = carreraNombre;
    }

    public String getCarreraNombre() {
        return carreraNombre;
    }

    public void setCarreraNombre(String carreraNombre) {
        this.carreraNombre = carreraNombre;
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
