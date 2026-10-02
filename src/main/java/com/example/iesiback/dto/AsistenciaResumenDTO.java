package com.example.iesiback.dto;

public class AsistenciaResumenDTO {

    private String materiaId;
    private String materia;
    private Long totalSesiones;
    private Double presentes;
    private Integer porcentaje;

    private Long cantPresentes;
    private Long cantAusentes;
    private Long cantTardanzas;
    private Long cantRetiros;
    private Long cantJustificados;

    public AsistenciaResumenDTO(String materia, Long totalSesiones, Double presentes,
                                Integer porcentaje, String materiaId,
                                Long cantPresentes, Long cantAusentes, Long cantTardanzas,
                                Long cantRetiros, Long cantJustificados) {
        this.materia = materia;
        this.totalSesiones = totalSesiones;
        this.presentes = presentes;
        this.porcentaje = porcentaje;
        this.materiaId = materiaId;
        this.cantPresentes = cantPresentes;
        this.cantAusentes = cantAusentes;
        this.cantTardanzas = cantTardanzas;
        this.cantRetiros = cantRetiros;
        this.cantJustificados = cantJustificados;
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

    public Double getPresentes() {
        return presentes;
    }

    public void setPresentes(Double presentes) {
        this.presentes = presentes;
    }

    public Integer getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Integer porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Long getCantPresentes() {
        return cantPresentes;
    }

    public void setCantPresentes(Long cantPresentes) {
        this.cantPresentes = cantPresentes;
    }

    public Long getCantAusentes() {
        return cantAusentes;
    }

    public void setCantAusentes(Long cantAusentes) {
        this.cantAusentes = cantAusentes;
    }

    public Long getCantTardanzas() {
        return cantTardanzas;
    }

    public void setCantTardanzas(Long cantTardanzas) {
        this.cantTardanzas = cantTardanzas;
    }

    public Long getCantRetiros() {
        return cantRetiros;
    }

    public void setCantRetiros(Long cantRetiros) {
        this.cantRetiros = cantRetiros;
    }

    public Long getCantJustificados() {
        return cantJustificados;
    }

    public void setCantJustificados(Long cantJustificados) {
        this.cantJustificados = cantJustificados;
    }
}