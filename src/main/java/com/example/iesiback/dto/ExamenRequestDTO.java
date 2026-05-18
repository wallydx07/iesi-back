package com.example.iesiback.dto;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.enums.NotaCondicion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // ✅ Evita errores con propiedades desconocidas
public class ExamenRequestDTO {
   // private Examen examen;
    private String legajoId;
    private String turnoId;
    private String materiaId;
    private NotaCondicion examenCondicion;
    private Nota nota;
    private Integer cursadaId;

    public Integer getCursadaId() {
        return cursadaId;
    }

    public void setCursadaId(Integer cursadaId) {
        this.cursadaId = cursadaId;
    }

    public Nota getNota() {
        return nota;
    }

    public void setNota(Nota nota) {
        this.nota = nota;
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

    public NotaCondicion getExamenCondicion() {
        return examenCondicion;
    }

    public void setExamenCondicion(NotaCondicion examenCondicion) {
        this.examenCondicion = examenCondicion;
    }
}

