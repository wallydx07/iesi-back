package com.example.iesiback.dto;

import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Examen;
import com.example.iesiback.entities.Nota;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // ✅ Evita errores con propiedades desconocidas
public class ExamenRequestDTO {
   // private Examen examen;
    private String legajoId;
    private String turnoId;
    private String materiaId;
    private String examenCondicion;
    private Nota nota;
    private Cursada cursada;



    // ✅ Getters y Setters


    public Nota getNota() {
        return nota;
    }

    public void setNota(Nota nota) {
        this.nota = nota;
    }

  //  public Examen getExamen() {
  //      return examen;
  //  }

   // public void setExamen(Examen examen) {
   //     this.examen = examen;
   // }

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

    public String getExamenCondicion() {
        return examenCondicion;
    }

    public void setExamenCondicion(String examenCondicion) {
        this.examenCondicion = examenCondicion;
    }

    public Cursada getCursada() {
        return cursada;
    }

    public void setCursada(Cursada cursada) {
        this.cursada = cursada;
    }
}

