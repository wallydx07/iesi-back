package com.example.iesiback.dto;

public class CorrelativasFaltantesEstadoDTO {
    private String materiaId;
    private String materiaNombre;
    private String materiaOrden;
    private String status; //libre, regular

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

    public String getMateriaOrden() {
        return materiaOrden;
    }

    public void setMateriaOrden(String materiaOrden) {
        this.materiaOrden = materiaOrden;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
