package com.example.iesiback.dto;

import java.time.LocalDate;

public class NotaExamenDTO {

    private String personaDni;
    private String personaNombre;
    private String personaApellido;
    private String legajoId;
    private Long notaId;
    private Integer notaCalificacionNotaNumero;
    private String notaCalificacionNotaLetra;
    private String notaCondicion;
    private String notaEstado;
    private String notaLibro;
    private String notaFolio;
    private LocalDate notaFechaNota;
    private String notaObservaciones;
    private String notaUsuario;
    private Long permisoId;
    private String status;
    private Integer materiaOrden;

    public NotaExamenDTO(String personaDni, String personaNombre, String legajoId, String personaApellido, Long notaId, Integer notaCalificacionNotaNumero, String notaCalificacionNotaLetra, String notaCondicion, String notaEstado, String notaLibro, String notaFolio, LocalDate notaFechaNota, String notaObservaciones, String notaUsuario, Long permisoId, String status, Integer materiaOrden) {
        this.personaDni = personaDni;
        this.personaNombre = personaNombre;
        this.legajoId = legajoId;
        this.personaApellido = personaApellido;
        this.notaId = notaId;
        this.notaCalificacionNotaNumero = notaCalificacionNotaNumero;
        this.notaCalificacionNotaLetra = notaCalificacionNotaLetra;
        this.notaCondicion = notaCondicion;
        this.notaEstado = notaEstado;
        this.notaLibro = notaLibro;
        this.notaFolio = notaFolio;
        this.notaFechaNota = notaFechaNota;
        this.notaObservaciones = notaObservaciones;
        this.notaUsuario = notaUsuario;
        this.permisoId = permisoId;
        this.status = status;
        this.materiaOrden = materiaOrden;
    }

    public void setPersonaDni(String personaDni) {
        this.personaDni = personaDni;
    }

    public void setPersonaNombre(String personaNombre) {
        this.personaNombre = personaNombre;
    }

    public void setPersonaApellido(String personaApellido) {
        this.personaApellido = personaApellido;
    }

    public void setLegajoId(String legajoId) {
        this.legajoId = legajoId;
    }

    public void setNotaId(Long notaId) {
        this.notaId = notaId;
    }

    public void setNotaCalificacionNotaNumero(Integer notaCalificacionNotaNumero) {
        this.notaCalificacionNotaNumero = notaCalificacionNotaNumero;
    }

    public void setNotaCalificacionNotaLetra(String notaCalificacionNotaLetra) {
        this.notaCalificacionNotaLetra = notaCalificacionNotaLetra;
    }

    public void setNotaCondicion(String notaCondicion) {
        this.notaCondicion = notaCondicion;
    }

    public void setNotaEstado(String notaEstado) {
        this.notaEstado = notaEstado;
    }

    public void setNotaLibro(String notaLibro) {
        this.notaLibro = notaLibro;
    }

    public void setNotaFolio(String notaFolio) {
        this.notaFolio = notaFolio;
    }

    public void setNotaFechaNota(LocalDate notaFechaNota) {
        this.notaFechaNota = notaFechaNota;
    }

    public void setNotaObservaciones(String notaObservaciones) {
        this.notaObservaciones = notaObservaciones;
    }

    public void setNotaUsuario(String notaUsuario) {
        this.notaUsuario = notaUsuario;
    }

    public void setPermisoId(Long permisoId) {
        this.permisoId = permisoId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMateriaOrden(Integer materiaOrden) {
        this.materiaOrden = materiaOrden;
    }

    public String getPersonaNombre() {
        return personaNombre;
    }

    public String getPersonaDni() {
        return personaDni;
    }

    public String getPersonaApellido() {
        return personaApellido;
    }

    public String getLegajoId() {
        return legajoId;
    }

    public Long getNotaId() {
        return notaId;
    }

    public Integer getNotaCalificacionNotaNumero() {
        return notaCalificacionNotaNumero;
    }

    public String getNotaCalificacionNotaLetra() {
        return notaCalificacionNotaLetra;
    }

    public String getNotaCondicion() {
        return notaCondicion;
    }

    public String getNotaEstado() {
        return notaEstado;
    }

    public String getNotaLibro() {
        return notaLibro;
    }

    public String getNotaFolio() {
        return notaFolio;
    }

    public LocalDate getNotaFechaNota() {
        return notaFechaNota;
    }

    public String getNotaObservaciones() {
        return notaObservaciones;
    }

    public String getNotaUsuario() {
        return notaUsuario;
    }

    public Long getPermisoId() {
        return permisoId;
    }

    public String getStatus() {
        return status;
    }

    public Integer getMateriaOrden() {
        return materiaOrden;
    }
}