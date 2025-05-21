package com.example.iesiback.dto;

public class AlumnoAsistenciaDTO {
    private Integer idAsistencia;
    private Integer idInforme;
    private String legajoId;
    private Boolean estado;
    private Long alumnoDni;
    private String alumnoApellido;
    private String alumnoNombre;

    public AlumnoAsistenciaDTO(Integer idAsistencia, Integer idInforme, String legajoId, Boolean estado,Long alumnoDni,String alumnoApellido,String alumnoNombre) {
        this.idAsistencia = idAsistencia;
        this.idInforme = idInforme;
        this.legajoId = legajoId;
        this.estado = estado;
        this.alumnoDni = alumnoDni;
        this.alumnoApellido = alumnoApellido;
        this.alumnoNombre = alumnoNombre;

    }

    public Integer getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(Integer idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public Integer getIdInforme() {
        return idInforme;
    }

    public void setIdInforme(Integer idInforme) {
        this.idInforme = idInforme;
    }

    public String getLegajoId() {
        return legajoId;
    }

    public void setLegajoId(String legajoId) {
        this.legajoId = legajoId;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Long getAlumnoDni() {
        return alumnoDni;
    }

    public void setAlumnoDni(Long alumnoDni) {
        this.alumnoDni = alumnoDni;
    }

    public String getAlumnoNombre() {
        return alumnoNombre;
    }

    public void setAlumnoNombre(String alumnoNombre) {
        this.alumnoNombre = alumnoNombre;
    }

    public String getAlumnoApellido() {
        return alumnoApellido;
    }

    public void setAlumnoApellido(String alumnoApellido) {
        this.alumnoApellido = alumnoApellido;
    }
}
