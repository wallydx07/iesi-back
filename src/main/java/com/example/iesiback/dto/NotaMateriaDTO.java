package com.example.iesiback.dto;

import java.time.LocalDate;

public class NotaMateriaDTO {
    private Integer notaId;
    private Integer materiaOrden;
    private String materiaNombre;
    private String notaCalificacionNumero;
    private String notaCalificacionLetra;
    private String notaCondicion;
    private String notaEstado;
    private String notaLibro;
    private String notaFolio;
    private LocalDate notaFecha;
    private String notaObservaciones;
    private String notaUsuario;
    private String materiaId;


    public NotaMateriaDTO(Integer notaId, Integer materiaOrden, String materiaNombre, String notaCalificacionNumero,
                          String notaCalificacionLetra, String notaCondicion, String notaEstado, String notaLibro,
                          String notaFolio, LocalDate notaFecha, String notaObservaciones, String notaUsuario,
                          String materiaId) {
        this.notaId = notaId;
        this.materiaOrden = materiaOrden;
        this.materiaNombre = materiaNombre;
        this.notaCalificacionNumero = notaCalificacionNumero;
        this.notaCalificacionLetra = notaCalificacionLetra;
        this.notaCondicion = notaCondicion;
        this.notaEstado = notaEstado;
        this.notaLibro = notaLibro;
        this.notaFolio = notaFolio;
        this.notaFecha = notaFecha;
        this.notaObservaciones = notaObservaciones;
        this.notaUsuario = notaUsuario;
        this.materiaId = materiaId;
    }

    // Getters y Setters
    public Integer getNotaId() { return notaId; }
    public void setNotaId(Integer notaId) { this.notaId = notaId; }
    public Integer getMateriaOrden() { return materiaOrden; }
    public void setMateriaOrden(Integer materiaOrden) { this.materiaOrden = materiaOrden; }
    public String getMateriaNombre() { return materiaNombre; }
    public void setMateriaNombre(String materiaNombre) { this.materiaNombre = materiaNombre; }
    public String getNotaCalificacionNumero() { return notaCalificacionNumero; }
    public void setNotaCalificacionNumero(String notaCalificacionNumero) { this.notaCalificacionNumero = notaCalificacionNumero; }
    public String getNotaCalificacionLetra() { return notaCalificacionLetra; }
    public void setNotaCalificacionLetra(String notaCalificacionLetra) { this.notaCalificacionLetra = notaCalificacionLetra; }
    public String getNotaCondicion() { return notaCondicion; }
    public void setNotaCondicion(String notaCondicion) { this.notaCondicion = notaCondicion; }
    public String getNotaEstado() { return notaEstado; }
    public void setNotaEstado(String notaEstado) { this.notaEstado = notaEstado; }
    public String getNotaLibro() { return notaLibro; }
    public void setNotaLibro(String notaLibro) { this.notaLibro = notaLibro; }
    public String getNotaFolio() { return notaFolio; }
    public void setNotaFolio(String notaFolio) { this.notaFolio = notaFolio; }
    public LocalDate getNotaFecha() { return notaFecha; }
    public void setNotaFecha(LocalDate notaFecha) { this.notaFecha = notaFecha; }
    public String getNotaObservaciones() { return notaObservaciones; }
    public void setNotaObservaciones(String notaObservaciones) { this.notaObservaciones = notaObservaciones; }
    public String getNotaUsuario() { return notaUsuario; }
    public void setNotaUsuario(String notaUsuario) { this.notaUsuario = notaUsuario; }
    public String getMateriaId() { return materiaId; }
    public void setMateriaId(String materiaId) { this.materiaId = materiaId; }

}
