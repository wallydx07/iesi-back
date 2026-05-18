package com.example.iesiback.dto;

import com.example.iesiback.enums.EstadoNota;

public class CorrelativasFaltantesEstadoDTO {
    private String materiaId;
    private String materiaNombre;
    private String materiaOrden;
    private EstadoNota notaEstado; //libre, regular


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

    public EstadoNota getNotaEstado() {
        return notaEstado;
    }
    public void setNotaEstado(EstadoNota notaEstado) {
        this.notaEstado = notaEstado;
    }
}
