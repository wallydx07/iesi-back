package com.example.iesiback.dto;

import java.util.List;

public class ProcesadoReinscripcionMateriaDTO {

    private String materiaId;
    private Integer materiaOrden;
    private String materiaNivel;
    private String materiaRegimen;
    private String materiaModalidad;
    private String carreraNombre;
    private String materiaNombre;
    private Integer carreraYear;
    private String materiaCorrelativas;
    private Boolean cursadaInscripto;
    private String cursadaStatus;
    private String materiaCarreraId;
    private String division;
    private List<CorrelativasFaltantesEstadoDTO> correlativas;

    public List<CorrelativasFaltantesEstadoDTO> getCorrelativas() {
        return correlativas;
    }

    public void setCorrelativas(List<CorrelativasFaltantesEstadoDTO> correlativas) {
        this.correlativas = correlativas;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public Integer getMateriaOrden() {
        return materiaOrden;
    }

    public void setMateriaOrden(Integer materiaOrden) {
        this.materiaOrden = materiaOrden;
    }

    public String getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(String materiaId) {
        this.materiaId = materiaId;
    }

    public String getMateriaNivel() {
        return materiaNivel;
    }

    public void setMateriaNivel(String materiaNivel) {
        this.materiaNivel = materiaNivel;
    }

    public String getMateriaRegimen() {
        return materiaRegimen;
    }

    public void setMateriaRegimen(String materiaRegimen) {
        this.materiaRegimen = materiaRegimen;
    }

    public String getMateriaModalidad() {
        return materiaModalidad;
    }

    public void setMateriaModalidad(String materiaModalidad) {
        this.materiaModalidad = materiaModalidad;
    }

    public String getCarreraNombre() {
        return carreraNombre;
    }

    public void setCarreraNombre(String carreraNombre) {
        this.carreraNombre = carreraNombre;
    }

    public String getMateriaNombre() {
        return materiaNombre;
    }

    public void setMateriaNombre(String materiaNombre) {
        this.materiaNombre = materiaNombre;
    }

    public Integer getCarreraYear() {
        return carreraYear;
    }

    public void setCarreraYear(Integer carreraYear) {
        this.carreraYear = carreraYear;
    }

    public String getMateriaCorrelativas() {
        return materiaCorrelativas;
    }

    public void setMateriaCorrelativas(String materiaCorrelativas) {
        this.materiaCorrelativas = materiaCorrelativas;
    }

    public Boolean getCursadaInscripto() {
        return cursadaInscripto;
    }

    public void setCursadaInscripto(Boolean cursadaInscripto) {
        this.cursadaInscripto = cursadaInscripto;
    }

    public String getCursadaStatus() {
        return cursadaStatus;
    }

    public void setCursadaStatus(String cursadaStatus) {
        this.cursadaStatus = cursadaStatus;
    }

    public String getMateriaCarreraId() {
        return materiaCarreraId;
    }

    public void setMateriaCarreraId(String materiaCarreraId) {
        this.materiaCarreraId = materiaCarreraId;
    }
}