package com.example.iesiback.dto;

import java.time.LocalDate;

public class ExamenCursadaDTO {

    private String cursadaExamenId;
    private LocalDate fecha;
    private String hora;
    private String libro;
    private String folio;
    private boolean firma;
    private String docenteDni;
    private String materiaId;
    private String materiaNombre;
    private String materiaNivel;
    private String carreraNombre;

    public ExamenCursadaDTO(String cursada_examen_id, LocalDate fecha, String hora, String libro, String folio, boolean firma, String docente_dni, String materia_id, String materia_nombre, String materia_nivel, String carreraNombre) {
        this.cursadaExamenId = cursada_examen_id;
        this.fecha = fecha;
        this.hora = hora;
        this.libro = libro;
        this.folio = folio;
        this.firma = firma;
        this.docenteDni = docente_dni;
        this.materiaId = materia_id;
        this.materiaNombre = materia_nombre;
        this.materiaNivel = materia_nivel;
        this.carreraNombre=carreraNombre;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getLibro() {
        return libro;
    }

    public void setLibro(String libro) {
        this.libro = libro;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public boolean isFirma() {
        return firma;
    }

    public void setFirma(boolean firma) {
        this.firma = firma;
    }

    public String getDocenteDni() {
        return docenteDni;
    }

    public void setDocenteDni(String docenteDni) {
        this.docenteDni = docenteDni;
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

    public String getMateriaNivel() {
        return materiaNivel;
    }

    public void setMateriaNivel(String materiaNivel) {
        this.materiaNivel = materiaNivel;
    }
}
