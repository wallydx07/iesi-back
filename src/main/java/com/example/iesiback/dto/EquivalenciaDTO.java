package com.example.iesiback.dto;

import java.time.LocalDate;

public class EquivalenciaDTO {

    private Integer id;
    private Long alumnoDni;
    private String alumnoApellido;
    private String alumnoNombre;
    private String materiaOrigen;
    private String institucionOrigen;
    private String resolucion;
    private String materiaActualNombre;
    private Double nota;
    private String status;
    private LocalDate fecha;
    private Long cursadaId;
    private String notaLetra;
    private String libro;
    private String folio;
    private String usuario;

    public EquivalenciaDTO(Integer id, Long alumnoDni, String alumnoApellido, String alumnoNombre,
                           String materiaOrigen, String institucionOrigen, String resolucion,
                           String materiaActualNombre, Double nota, String status, LocalDate fecha,
                           String notaLetra, String libro, String folio, String usuario) {
        this.id = id;
        this.alumnoDni = alumnoDni;
        this.alumnoApellido = alumnoApellido;
        this.alumnoNombre = alumnoNombre;
        this.materiaOrigen = materiaOrigen;
        this.institucionOrigen = institucionOrigen;
        this.resolucion = resolucion;
        this.materiaActualNombre = materiaActualNombre;
        this.nota = nota;
        this.status = status;
        this.fecha = fecha;
        this.notaLetra = notaLetra;
        this.libro = libro;
        this.folio = folio;
        this.usuario = usuario;
    }

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer  id) { this.id = id; }

    public Long getAlumnoDni() { return alumnoDni; }
    public void setAlumnoDni(Long alumnoDni) { this.alumnoDni = alumnoDni; }

    public String getAlumnoApellido() { return alumnoApellido; }
    public void setAlumnoApellido(String alumnoApellido) { this.alumnoApellido = alumnoApellido; }

    public String getAlumnoNombre() { return alumnoNombre; }
    public void setAlumnoNombre(String alumnoNombre) { this.alumnoNombre = alumnoNombre; }

    public String getMateriaOrigen() { return materiaOrigen; }
    public void setMateriaOrigen(String materiaOrigen) { this.materiaOrigen = materiaOrigen; }

    public String getInstitucionOrigen() { return institucionOrigen; }
    public void setInstitucionOrigen(String institucionOrigen) { this.institucionOrigen = institucionOrigen; }

    public String getResolucion() { return resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }

    public String getMateriaActualNombre() { return materiaActualNombre; }
    public void setMateriaActualNombre(String materiaActualNombre) { this.materiaActualNombre = materiaActualNombre; }

    public Double getNota() { return nota; }
    public void setNota(Double nota) { this.nota = nota; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Long getCursadaId() { return cursadaId; }
    public void setCursadaId(Long cursadaId) { this.cursadaId = cursadaId; }

    public String getNotaLetra() { return notaLetra; }
    public void setNotaLetra(String notaLetra) { this.notaLetra = notaLetra; }

    public String getLibro() { return libro; }
    public void setLibro(String libro) { this.libro = libro; }

    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}
