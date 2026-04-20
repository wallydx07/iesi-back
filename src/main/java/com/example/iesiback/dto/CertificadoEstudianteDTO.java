package com.example.iesiback.dto;

import com.example.iesiback.entities.CertificadoEstudiante;

import java.time.LocalDate;

public class CertificadoEstudianteDTO {
    private Integer id;
    private String tipo;
    private String autoridad;
    private LocalDate fecha;
    private String estado;
    private String observaciones;
    private String usuario;
    private Boolean validado;
    private Integer monto;
    private String legajoId;
    private Integer atencionId;

    // Constructor desde entidad
    public CertificadoEstudianteDTO(CertificadoEstudiante certificado) {
        this.id = certificado.getId();
        this.tipo = certificado.getTipo();
        this.autoridad = certificado.getAutoridad();
        this.fecha = certificado.getFecha();
        this.estado = certificado.getEstado();
        this.observaciones = certificado.getObservaciones();
        this.usuario = certificado.getUsuario();
        this.validado = certificado.getValidado();
        this.monto = certificado.getMonto();
//        this.legajoId = certificado.getAtencion().getLegajo() != null ? certificado.getAtencion().getLegajo().getLegajoId() : null;

        this.legajoId = certificado.getTramite().getLegajoId() != null ? certificado.getTramite().getLegajoId() : null;

        this.atencionId = certificado.getTramite() != null ? certificado.getTramite().getId() : null;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAutoridad() {
        return autoridad;
    }

    public void setAutoridad(String autoridad) {
        this.autoridad = autoridad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public Integer getMonto() {
        return monto;
    }

    public void setMonto(Integer monto) {
        this.monto = monto;
    }

    public String getLegajoId() {
        return legajoId;
    }

    public void setLegajoId(String legajoId) {
        this.legajoId = legajoId;
    }

    public Integer getAtencionId() {
        return atencionId;
    }

    public void setAtencionId(Integer atencionId) {
        this.atencionId = atencionId;
    }
}
