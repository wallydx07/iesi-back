package com.example.iesiback.dto;

import java.util.List;

public class AlumnoCursadaMateriaNotaDTO {
    private String legajoId;
    private Long personaDni;
    private String personaApellido;
    private String personaNombre;
    private String legajoCambia;
    private String tituloEntregado;
    private String tituloSerie;
    private String tituloYear;
    private String tituloFecha;
    private String carreraId;
    private String legajoSede;
    private String legajoFotocopiaDni;
    private String legajoCertificadoNacimiento;
    private String legajoFotocopiaTitulo;
    private String folio;
    private boolean notasCorregidas;
    private List<NotaMateriaDTO> notaMateriaDTO;

    // Constructor usado por JPA
    public AlumnoCursadaMateriaNotaDTO(String legajoId, Long personaDni, String personaApellido,
                                       String personaNombre, String legajoCambia, String tituloEntregado,
                                       String tituloSerie, String tituloYear, String tituloFecha,
                                       String carreraId, String legajoSede, String legajoFotocopiaDni,
                                       String legajoCertificadoNacimiento, String legajoFotocopiaTitulo,
                                       String folio, boolean notasCorregidas) {
        this.legajoId = legajoId;
        this.personaDni = personaDni;
        this.personaApellido = personaApellido;
        this.personaNombre = personaNombre;
        this.legajoCambia = legajoCambia;
        this.tituloEntregado = tituloEntregado;
        this.tituloSerie = tituloSerie;
        this.tituloYear = tituloYear;
        this.tituloFecha = tituloFecha;
        this.carreraId = carreraId;
        this.legajoSede = legajoSede;
        this.legajoFotocopiaDni = legajoFotocopiaDni;
        this.legajoCertificadoNacimiento = legajoCertificadoNacimiento;
        this.legajoFotocopiaTitulo = legajoFotocopiaTitulo;
        this.folio = folio;
        this.notasCorregidas = notasCorregidas;
    }

    public String getLegajoId() {
        return legajoId;
    }

    public void setLegajoId(String legajoId) {
        this.legajoId = legajoId;
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

    public String getLegajoCambia() {
        return legajoCambia;
    }

    public void setLegajoCambia(String legajoCambia) {
        this.legajoCambia = legajoCambia;
    }

    public String getTituloEntregado() {
        return tituloEntregado;
    }

    public void setTituloEntregado(String tituloEntregado) {
        this.tituloEntregado = tituloEntregado;
    }

    public String getTituloSerie() {
        return tituloSerie;
    }

    public void setTituloSerie(String tituloSerie) {
        this.tituloSerie = tituloSerie;
    }

    public String getTituloFecha() {
        return tituloFecha;
    }

    public void setTituloFecha(String tituloFecha) {
        this.tituloFecha = tituloFecha;
    }

    public String getTituloYear() {
        return tituloYear;
    }

    public void setTituloYear(String tituloYear) {
        this.tituloYear = tituloYear;
    }

    public String getLegajoSede() {
        return legajoSede;
    }

    public void setLegajoSede(String legajoSede) {
        this.legajoSede = legajoSede;
    }

    public String getCarreraId() {
        return carreraId;
    }

    public void setCarreraId(String carreraId) {
        this.carreraId = carreraId;
    }

    public String getLegajoFotocopiaDni() {
        return legajoFotocopiaDni;
    }

    public void setLegajoFotocopiaDni(String legajoFotocopiaDni) {
        this.legajoFotocopiaDni = legajoFotocopiaDni;
    }

    public String getLegajoCertificadoNacimiento() {
        return legajoCertificadoNacimiento;
    }

    public void setLegajoCertificadoNacimiento(String legajoCertificadoNacimiento) {
        this.legajoCertificadoNacimiento = legajoCertificadoNacimiento;
    }

    public String getLegajoFotocopiaTitulo() {
        return legajoFotocopiaTitulo;
    }

    public void setLegajoFotocopiaTitulo(String legajoFotocopiaTitulo) {
        this.legajoFotocopiaTitulo = legajoFotocopiaTitulo;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public boolean isNotasCorregidas() {
        return notasCorregidas;
    }

    public void setNotasCorregidas(boolean notasCorregidas) {
        this.notasCorregidas = notasCorregidas;
    }

    public List<NotaMateriaDTO> getNotaMateriaDTO() {
        return notaMateriaDTO;
    }

    public void setNotaMateriaDTO(List<NotaMateriaDTO> notaMateriaDTO) {
        this.notaMateriaDTO = notaMateriaDTO;
    }
}
