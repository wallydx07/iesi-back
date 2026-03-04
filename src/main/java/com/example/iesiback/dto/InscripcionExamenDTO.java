package com.example.iesiback.dto;

import java.util.List;

public class InscripcionExamenDTO {

    private int cursadaId;
    private int materiaCarreraId;
    private int materiaOrden;
    private String materiaId;
    private String materiaNombre;
    private String condicion;
    private String fecha;
    private Boolean inscripto;
    private String curso;
    private String fechaHoraMesa;
    private List<String> correlativas;
    private String turnoId;
    private String hora;
    private String justificacion; // ← agregar esto

    public InscripcionExamenDTO() {

    }

    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public List<String> getCorrelativas() {
        return correlativas;
    }

    public void setCorrelativas(List<String> correlativas) {
        this.correlativas = correlativas;
    }

    public int getMateriaCarreraId() {
        return materiaCarreraId;
    }

    public void setMateriaCarreraId(int materiaCarreraId) {
        this.materiaCarreraId = materiaCarreraId;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }


    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public int getCursadaId() {
        return cursadaId;
    }

    public void setCursadaId(int cursadaId) {
        this.cursadaId = cursadaId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFechaHoraMesa() {
        return fechaHoraMesa;
    }

    public void setFechaHoraMesa(String fechaHoraMesa) {
        this.fechaHoraMesa = fechaHoraMesa;
    }

    public Boolean getInscripto() {
        return inscripto;
    }

    public void setInscripto(Boolean inscripto) {
        this.inscripto = inscripto;
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

    public int getMateriaOrden() {
        return materiaOrden;
    }

    public void setMateriaOrden(int materiaOrden) {
        this.materiaOrden = materiaOrden;
    }

    public String getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(String turnoId) {
        this.turnoId = turnoId;
    }
}