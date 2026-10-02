package com.example.iesiback.dto;

import com.example.iesiback.enums.EstadoAsistencia;

public class AlumnoAsistenciaDTO {
    private Integer idAsistencia;
    private Integer idInforme;
    private String legajoId;
    private EstadoAsistencia estado;
    private Long personaDni;
    private String personaApellido;
    private String personaNombre;

    public AlumnoAsistenciaDTO(Integer idAsistencia, Integer idInforme, String legajoId, EstadoAsistencia estado,Long personaDni,String personaApellido,String personaNombre) {
        this.idAsistencia = idAsistencia;
        this.idInforme = idInforme;
        this.legajoId = legajoId;
        this.estado = estado;
        this.personaDni = personaDni;
        this.personaApellido = personaApellido;
        this.personaNombre = personaNombre;

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

    public EstadoAsistencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsistencia estado) {
        this.estado = estado;
    }

    public Long getPersonaDni() {
        return personaDni;
    }

    public void setPersonaDni(Long personaDni) {
        this.personaDni = personaDni;
    }

    public String getPersonaApellido() {
        return personaApellido;
    }

    public void setPersonaApellido(String personaApellido) {
        this.personaApellido = personaApellido;
    }

    public String getPersonaNombre() {
        return personaNombre;
    }

    public void setPersonaNombre(String personaNombre) {
        this.personaNombre = personaNombre;
    }
}
