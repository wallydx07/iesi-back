package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.BatchSize;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "nota")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Nota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Hibernate maneja la secuencia automáticamente
    @Column(name = "nota_id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "nota_calificacion_nota_numero", length = 50)
    private String notaCalificacionNotaNumero;

    @Size(max = 50)
    @Column(name = "nota_calificacion_nota_letra", length = 50)
    private String notaCalificacionNotaLetra;

    @Size(max = 50)
    @Column(name = "nota_fecha_nota", length = 50)
    private String notaFechaNota;

    @Size(max = 50)
    @Column(name = "nota_condicion", length = 50)
    private String notaCondicion;

    @Size(max = 50)
    @Column(name = "nota_estado", length = 50)
    private String notaEstado;

    @Size(max = 50)
    @Column(name = "nota_libro_nota", length = 50)
    private String notaLibroNota;

    @Size(max = 50)
    @Column(name = "nota_folio_nota", length = 50)
    private String notaFolioNota;

    @Size(max = 50)
    @Column(name = "nota_observaciones", length = 50)
    private String notaObservaciones;

    @Size(max = 50)
    @Column(name = "nota_usuario", length = 50)
    private String notaUsuario;

    @OneToMany(mappedBy = "cursadaNota", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    @JsonIgnore
    private Set<Cursada> cursadas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    @JsonIgnore
    private Set<Equivalencia> equivalencias = new LinkedHashSet<>();

    @OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    @JsonIgnore
    private Set<Examen> examen = new LinkedHashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "legajo_id")
  @JsonIgnore // Evita que se serialice la entidad completa
   private Legajo legajo;



    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNotaCalificacionNotaNumero() {
        return notaCalificacionNotaNumero;
    }

    public void setNotaCalificacionNotaNumero(String notaCalificacionNotaNumero) {
        this.notaCalificacionNotaNumero = notaCalificacionNotaNumero;
    }

    public String getNotaCalificacionNotaLetra() {
        return notaCalificacionNotaLetra;
    }

    public void setNotaCalificacionNotaLetra(String notaCalificacionNotaLetra) {
        this.notaCalificacionNotaLetra = notaCalificacionNotaLetra;
    }

    public String getNotaFechaNota() {
        return notaFechaNota;
    }

    public void setNotaFechaNota(String notaFechaNota) {
        this.notaFechaNota = notaFechaNota;
    }

    public String getNotaCondicion() {
        return notaCondicion;
    }

    public void setNotaCondicion(String notaCondicion) {
        this.notaCondicion = notaCondicion;
    }

    public String getNotaEstado() {
        return notaEstado;
    }

    public void setNotaEstado(String notaEstado) {
        this.notaEstado = notaEstado;
    }

    public String getNotaLibroNota() {
        return notaLibroNota;
    }

    public void setNotaLibroNota(String notaLibroNota) {
        this.notaLibroNota = notaLibroNota;
    }

    public String getNotaFolioNota() {
        return notaFolioNota;
    }

    public void setNotaFolioNota(String notaFolioNota) {
        this.notaFolioNota = notaFolioNota;
    }

    public String getNotaObservaciones() {
        return notaObservaciones;
    }

    public void setNotaObservaciones(String notaObservaciones) {
        this.notaObservaciones = notaObservaciones;
    }

    public String getNotaUsuario() {
        return notaUsuario;
    }

    public void setNotaUsuario(String notaUsuario) {
        this.notaUsuario = notaUsuario;
    }

    public Set<Cursada> getCursadas() {
        return cursadas;
    }

    public void setCursadas(Set<Cursada> cursadas) {
        this.cursadas = cursadas;
    }

    public Set<Equivalencia> getEquivalencias() {
        return equivalencias;
    }

    public void setEquivalencias(Set<Equivalencia> equivalencias) {
        this.equivalencias = equivalencias;
    }

    public Set<Examen> getExamen() {
        return examen;
    }

    public void setExamen(Set<Examen> examen) {
        this.examen = examen;
    }

    @JsonProperty("legajo_id") // Agrega solo el ID de Legajo en el JSON
    public String getLegajoId() {
        return legajo != null ? legajo.getLegajoId() : null;
    }




    public Legajo getLegajo() {
        return legajo;
    }

    public void setLegajo(Legajo legajo) {
        this.legajo = legajo;
    }
}
