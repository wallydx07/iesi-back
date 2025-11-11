package com.example.iesiback.dto;

import java.time.LocalDate;

public class EquivalenciaDetalleDTO {
    private Long notaId;
    private Long alumnoDni;
    private String alumnoApellido;
    private String alumnoNombre;
    private Double notaCalificacionNumero;
    private String notaCalificacionLetra;
    private String notaLibro;
    private String notaFolio;
    private String materiaOrigen;
    private String institucionOrigen;
    private String materiaNombre;
    private String carreraNombre;
    private String notaUsuario;
    private String status;
    private LocalDate fecha;
    private String resolucion;


    public EquivalenciaDetalleDTO(Long notaId, Long alumnoDni, String alumnoApellido, String alumnoNombre, Double notaCalificacionNumero, String notaCalificacionLetra, String notaFolio, String notaLibro, String materiaOrigen, String institucionOrigen, String materiaNombre, String carreraNombre, String notaUsuario, LocalDate fecha, String status, String resolucion) {
        this.notaId = notaId;
        this.alumnoDni = alumnoDni;
        this.alumnoApellido = alumnoApellido;
        this.alumnoNombre = alumnoNombre;
        this.notaCalificacionNumero = notaCalificacionNumero;
        this.notaCalificacionLetra = notaCalificacionLetra;
        this.notaFolio = notaFolio;
        this.notaLibro = notaLibro;
        this.materiaOrigen = materiaOrigen;
        this.institucionOrigen = institucionOrigen;
        this.materiaNombre = materiaNombre;
        this.carreraNombre = carreraNombre;
        this.notaUsuario = notaUsuario;
        this.fecha=fecha;
        this.status = status;
        this.resolucion = resolucion;


    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Long getNotaId() {
        return notaId;
    }

    public void setNotaId(Long notaId) {
        this.notaId = notaId;
    }

    public Long getAlumnoDni() {
        return alumnoDni;
    }

    public void setAlumnoDni(Long alumnoDni) {
        this.alumnoDni = alumnoDni;
    }

    public String getAlumnoApellido() {
        return alumnoApellido;
    }

    public void setAlumnoApellido(String alumnoApellido) {
        this.alumnoApellido = alumnoApellido;
    }

    public String getAlumnoNombre() {
        return alumnoNombre;
    }

    public void setAlumnoNombre(String alumnoNombre) {
        this.alumnoNombre = alumnoNombre;
    }

    public Double getNotaCalificacionNumero() {
        return notaCalificacionNumero;
    }

    public void setNotaCalificacionNumero(Double notaCalificacionNumero) {
        this.notaCalificacionNumero = notaCalificacionNumero;
    }

    public String getNotaCalificacionLetra() {
        return notaCalificacionLetra;
    }

    public void setNotaCalificacionLetra(String notaCalificacionLetra) {
        this.notaCalificacionLetra = notaCalificacionLetra;
    }

    public String getNotaLibro() {
        return notaLibro;
    }

    public void setNotaLibro(String notaLibro) {
        this.notaLibro = notaLibro;
    }

    public String getNotaFolio() {
        return notaFolio;
    }

    public void setNotaFolio(String notaFolio) {
        this.notaFolio = notaFolio;
    }

    public String getMateriaOrigen() {
        return materiaOrigen;
    }

    public void setMateriaOrigen(String materiaOrigen) {
        this.materiaOrigen = materiaOrigen;
    }

    public String getInstitucionOrigen() {
        return institucionOrigen;
    }

    public void setInstitucionOrigen(String institucionOrigen) {
        this.institucionOrigen = institucionOrigen;
    }

    public String getMateriaNombre() {
        return materiaNombre;
    }

    public void setMateriaNombre(String materiaNombre) {
        this.materiaNombre = materiaNombre;
    }

    public String getCarreraNombre() {
        return carreraNombre;
    }

    public void setCarreraNombre(String carreraNombre) {
        this.carreraNombre = carreraNombre;
    }

    public String getNotaUsuario() {
        return notaUsuario;
    }

    public void setNotaUsuario(String notaUsuario) {
        this.notaUsuario = notaUsuario;
    }
}
