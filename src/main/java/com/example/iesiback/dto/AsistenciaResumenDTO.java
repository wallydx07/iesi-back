package com.example.iesiback.dto;

public class AsistenciaResumenDTO {

    private String materiaId;
    private String materia;
    private Long totalSesiones;
    private Long presentes;
    private Integer porcentaje;

    public AsistenciaResumenDTO(String materia, Long totalSesiones, Long presentes, Integer porcentaje, String materiaId) {
        this.materia = materia;
        this.totalSesiones = totalSesiones;
        this.presentes = presentes;
        this.porcentaje = porcentaje;
        this.materiaId = materiaId;
    }

    public String getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(String materiaId) {
        this.materiaId = materiaId;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public Long getTotalSesiones() {
        return totalSesiones;
    }

    public void setTotalSesiones(Long totalSesiones) {
        this.totalSesiones = totalSesiones;
    }

    public Long getPresentes() {
        return presentes;
    }

    public void setPresentes(Long presentes) {
        this.presentes = presentes;
    }

    public Integer getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Integer porcentaje) {
        this.porcentaje = porcentaje;
    }
// Getters y setters
}
