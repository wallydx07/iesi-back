package com.example.iesiback.dto;

public class PromedioEgresadoDTO {
    private String legajoId;
    private Double promedio;
    private String personaNombre;
    private String personaApellido;
    private String personaDni;
    private String carreraNombre;

    public PromedioEgresadoDTO(String legajoId, Double promedio, String personaNombre, String personaApellido, String personaDni, String carreraNombre) {
        this.legajoId = legajoId;
        this.promedio = promedio;
        this.personaNombre = personaNombre;
        this.personaApellido = personaApellido;
        this.personaDni = personaDni;
        this.carreraNombre = carreraNombre;
    }

    public String getLegajoId() {
        return legajoId;
    }

    public Double getPromedio() {
        return promedio;
    }

    public void setLegajoId(String legajoId) {
        this.legajoId = legajoId;
    }

    public void setPromedio(Double promedio) {
        this.promedio = promedio;
    }

    public String getPersonaNombre() {
        return personaNombre;
    }

    public void setPersonaNombre(String personaNombre) {
        this.personaNombre = personaNombre;
    }

    public String getPersonaApellido() {
        return personaApellido;
    }

    public void setPersonaApellido(String personaApellido) {
        this.personaApellido = personaApellido;
    }

    public String getPersonaDni() {
        return personaDni;
    }

    public void setPersonaDni(String personaDni) {
        this.personaDni = personaDni;
    }

    public String getCarreraNombre() {
        return carreraNombre;
    }

    public void setCarreraNombre(String carreraNombre) {
        this.carreraNombre = carreraNombre;
    }
}
