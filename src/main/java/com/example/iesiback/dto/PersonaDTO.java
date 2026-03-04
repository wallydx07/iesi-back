package com.example.iesiback.dto;

public class PersonaDTO {
    private Long personaDni;
    private String personaApellido;
    private String personaNombre;
    private String personaCorreo;
    private String personaDomicilioCelular;


    public PersonaDTO(Long personaDni, String personaApellido, String personaNombre,
                      String personaCorreo, String personaDomicilioCelular) {
        this.personaDni = personaDni;
        this.personaApellido = personaApellido;
        this.personaNombre = personaNombre;
        this.personaCorreo = personaCorreo;
        this.personaDomicilioCelular = personaDomicilioCelular;
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

    public Long getPersonaDni() {
        return personaDni;
    }

    public void setPersonaDni(Long personaDni) {
        this.personaDni = personaDni;
    }

    public String getPersonaCorreo() {
        return personaCorreo;
    }

    public void setPersonaCorreo(String personaCorreo) {
        this.personaCorreo = personaCorreo;
    }

    public String getPersonaDomicilioCelular() {
        return personaDomicilioCelular;
    }

    public void setPersonaDomicilioCelular(String personaDomicilioCelular) {
        this.personaDomicilioCelular = personaDomicilioCelular;
    }

    @Override
    public String toString() {
        return "PersonaDTO{" +
                "personaDni=" + personaDni +
                ", personaApellido='" + personaApellido + '\'' +
                ", personaNombre='" + personaNombre + '\'' +
                ", personaCorreo='" + personaCorreo + '\'' +
                ", personaDomicilioCelular='" + personaDomicilioCelular + '\'' +
                '}';
    }

}
