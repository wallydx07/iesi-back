package com.example.iesiback.dto;

import com.example.iesiback.enums.EstadoNota;
import com.example.iesiback.enums.EstadoCondicion;

import java.time.LocalDate;

public class NotaImportDTO {

    private Long dni;
    private String nombre;
    private String apellido;
    private String carreraId;
    private String materiaOrden;
    private LocalDate fecha;
    private String libro;
    private String folio;
    private EstadoCondicion condicion; // PROMOCION, FINAL, EQUIVALENCIA
    private EstadoNota notaEstado; //libre, regular
    private Double nota;

    public Long getDni() {
        return dni;
    }

    public void setDni(Long dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCarreraId() {
        return carreraId;
    }

    public void setCarreraId(String carreraId) {
        this.carreraId = carreraId;
    }

    public String getMateriaOrden() {
        return materiaOrden;
    }

    public void setMateriaOrden(String materiaOrden) {
        this.materiaOrden = materiaOrden;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getLibro() {
        return libro;
    }

    public void setLibro(String libro) {
        this.libro = libro;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public EstadoNota getNotaEstado() {
        return notaEstado;
    }

    public void setNotaEstado(EstadoNota notaEstado) {
        this.notaEstado = notaEstado;
    }

    public EstadoCondicion getCondicion() {
        return condicion;
    }

    public void setCondicion(EstadoCondicion condicion) {
        this.condicion = condicion;
    }
}