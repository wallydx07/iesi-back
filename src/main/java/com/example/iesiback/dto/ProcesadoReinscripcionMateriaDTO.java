package com.example.iesiback.dto;

public class ProcesadoReinscripcionMateriaDTO {

    private String materiaId;
    private Integer materiaOrden;
    private String materiaNivel;
    private String carreraNombre;
    private String materiaNombre;
    private Integer carreraYear;
    private String materiaCorrelativas;
    private Boolean cursadaInscripto;
    private String cursadaStatus;
    private String materiaCarreraId;


    public String getMateriaCarreraId() {
        return materiaCarreraId;
    }

    public void setMateriaCarreraId(String materiaCarreraId) {
        this.materiaCarreraId = materiaCarreraId;
    }

    // Constructor por defecto (sin argumentos)
    public ProcesadoReinscripcionMateriaDTO() {
    }

    public Boolean getCursadaInscripto() {
        return cursadaInscripto;
    }

    public void setCursadaInscripto(Boolean cursadaInscripto) {
        this.cursadaInscripto = cursadaInscripto;
    }

    public String getCarreraNombre() {
        return carreraNombre;
    }

    public void setCarreraNombre(String carreraNombre) {
        this.carreraNombre = carreraNombre;
    }

    public Integer getCarreraYear() {
        return carreraYear;
    }

    public void setCarreraYear(Integer carreraYear) {
        this.carreraYear = carreraYear;
    }

    public String getCursadaStatus() {
        return cursadaStatus;
    }

    public void setCursadaStatus(String cursadaStatus) {
        this.cursadaStatus = cursadaStatus;
    }

    public String getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(String materiaId) {
        this.materiaId = materiaId;
    }

    public String getMateriaCorrelativas() {
        return materiaCorrelativas;
    }

    public void setMateriaCorrelativas(String materiaCorrelativas) {
        this.materiaCorrelativas = materiaCorrelativas;
    }

    public String getMateriaNivel() {
        return materiaNivel;
    }

    public void setMateriaNivel(String materiaNivel) {
        this.materiaNivel = materiaNivel;
    }

    public String getMateriaNombre() {
        return materiaNombre;
    }

    public void setMateriaNombre(String materiaNombre) {
        this.materiaNombre = materiaNombre;
    }

    public Integer getMateriaOrden() {
        return materiaOrden;
    }

    public void setMateriaOrden(Integer materiaOrden) {
        this.materiaOrden = materiaOrden;
    }
}