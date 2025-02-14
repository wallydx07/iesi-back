package com.example.iesiback.dto;

import com.example.iesiback.entities.Alumno;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Date;

public class AlumnoLegajoInscripcionCarreraDTO {

    String legajoId;
    Long alumnoDni;
    String alumnoApellido;
    String alumnoNombre;
    String carreraId;
    String legajoFotocopiaDni;
    String legajoCertificadoNacimiento;
    String legajoFotocopiaTitulo;
    String legajoPlanillaProntuarial;
    String legajoCarnetSanitario;
    String legajoFoto;
    String legajoAval;
    String legajoCarpetaColgante;
    String usuario;
    LocalDate alumnoFechaNacimiento;
    String domicilioAlumnoCorreo;
    String domicilioAlumnoCelular;

    public AlumnoLegajoInscripcionCarreraDTO(
            String legajoId,
            Long alumnoDni,
            String alumnoApellido,
            String alumnoNombre,
            String carreraId,
            String legajoFotocopiaDni,
            String legajoCertificadoNacimiento,
            String legajoFotocopiaTitulo,
            String legajoPlanillaProntuarial,
            String legajoCarnetSanitario,
            String legajoFoto,
            String legajoAval,
            String legajoCarpetaColgante,
            String usuario,
            LocalDate  alumnoFechaNacimiento,
            String domicilioAlumnoCorreo,
            String domicilioAlumnoCelular
    ) {
        this.legajoId = legajoId;
        this.alumnoDni = alumnoDni;
        this.alumnoApellido = alumnoApellido;
        this.alumnoNombre = alumnoNombre;
        this.carreraId = carreraId;
        this.legajoFotocopiaDni = legajoFotocopiaDni;
        this.legajoCertificadoNacimiento = legajoCertificadoNacimiento;
        this.legajoFotocopiaTitulo = legajoFotocopiaTitulo;
        this.legajoPlanillaProntuarial = legajoPlanillaProntuarial;
        this.legajoCarnetSanitario = legajoCarnetSanitario;
        this.legajoFoto = legajoFoto;
        this.legajoAval = legajoAval;
        this.legajoCarpetaColgante = legajoCarpetaColgante;
        this.usuario = usuario;
        this.alumnoFechaNacimiento = alumnoFechaNacimiento;
        this.domicilioAlumnoCorreo = domicilioAlumnoCorreo;
        this.domicilioAlumnoCelular = domicilioAlumnoCelular;

    }

    public LocalDate  getAlumnoFechaNacimiento() {
        return alumnoFechaNacimiento;
    }

    public void setAlumnoFechaNacimiento(LocalDate  alumnoFechaNacimiento) {
        this.alumnoFechaNacimiento = alumnoFechaNacimiento;
    }

    public String getDomicilioAlumnoCelular() {
        return domicilioAlumnoCelular;
    }

    public void setDomicilioAlumnoCelular(String domicilioAlumnoCelular) {
        this.domicilioAlumnoCelular = domicilioAlumnoCelular;
    }

    public String getDomicilioAlumnoCorreo() {
        return domicilioAlumnoCorreo;
    }

    public void setDomicilioAlumnoCorreo(String domicilioAlumnoCorreo) {
        this.domicilioAlumnoCorreo = domicilioAlumnoCorreo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getAlumnoApellido() {
        return alumnoApellido;
    }

    public void setAlumnoApellido(String alumnoApellido) {
        this.alumnoApellido = alumnoApellido;
    }

    public Long getAlumnoDni() {
        return alumnoDni;
    }

    public void setAlumnoDni(Long alumnoDni) {
        this.alumnoDni = alumnoDni;
    }

    public String getCarreraId() {
        return carreraId;
    }

    public void setCarreraId(String carreraId) {
        this.carreraId = carreraId;
    }

    public String getAlumnoNombre() {
        return alumnoNombre;
    }

    public void setAlumnoNombre(String alumnoNombre) {
        this.alumnoNombre = alumnoNombre;
    }

    public String getLegajoAval() {
        return legajoAval;
    }

    public void setLegajoAval(String legajoAval) {
        this.legajoAval = legajoAval;
    }

    public String getLegajoCarnetSanitario() {
        return legajoCarnetSanitario;
    }

    public void setLegajoCarnetSanitario(String legajoCarnetSanitario) {
        this.legajoCarnetSanitario = legajoCarnetSanitario;
    }

    public String getLegajoCarpetaColgante() {
        return legajoCarpetaColgante;
    }

    public void setLegajoCarpetaColgante(String legajoCarpetaColgante) {
        this.legajoCarpetaColgante = legajoCarpetaColgante;
    }

    public String getLegajoCertificadoNacimiento() {
        return legajoCertificadoNacimiento;
    }

    public void setLegajoCertificadoNacimiento(String legajoCertificadoNacimiento) {
        this.legajoCertificadoNacimiento = legajoCertificadoNacimiento;
    }

    public String getLegajoFoto() {
        return legajoFoto;
    }

    public void setLegajoFoto(String legajoFoto) {
        this.legajoFoto = legajoFoto;
    }

    public String getLegajoFotocopiaDni() {
        return legajoFotocopiaDni;
    }

    public void setLegajoFotocopiaDni(String legajoFotocopiaDni) {
        this.legajoFotocopiaDni = legajoFotocopiaDni;
    }

    public String getLegajoId() {
        return legajoId;
    }

    public void setLegajoId(String legajoId) {
        this.legajoId = legajoId;
    }

    public String getLegajoFotocopiaTitulo() {
        return legajoFotocopiaTitulo;
    }

    public void setLegajoFotocopiaTitulo(String legajoFotocopiaTitulo) {
        this.legajoFotocopiaTitulo = legajoFotocopiaTitulo;
    }

    public String getLegajoPlanillaProntuarial() {
        return legajoPlanillaProntuarial;
    }

    public void setLegajoPlanillaProntuarial(String legajoPlanillaProntuarial) {
        this.legajoPlanillaProntuarial = legajoPlanillaProntuarial;
    }
}