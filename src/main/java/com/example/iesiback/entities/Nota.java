package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "nota")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Nota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Hibernate maneja la secuencia automáticamente
    @Column(name = "nota_id", nullable = false)
    private Long notaId;


    @Column(name = "nota_calificacion_nota_numero")
    private Double notaCalificacionNotaNumero;


    @Size(max = 50)
    @Column(name = "nota_calificacion_nota_letra", length = 50)
    private String notaCalificacionNotaLetra;

//    @Size(max = 50)
//    @Column(name = "nota_fecha_nota", length = 50)
//    private String notaFechaNota;

    @Column(name = "nota_fecha_nota")
    private LocalDate notaFechaNota;

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

    @OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    @JsonIgnore
    private Set<Equivalencia> equivalencias = new LinkedHashSet<>();

    @OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    @JsonIgnore // ✅ Evita que Jackson serialice esta relación
    private Set<Examen> examen = new LinkedHashSet<>();

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "nota_cursada_id", nullable = false)
//    @JsonProperty(value = "cursada", access = JsonProperty.Access.WRITE_ONLY)
//    private Cursada cursada;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_cursada_id", nullable = false)
    @JsonBackReference // ✅ Este lado no se serializa, se ignora para evitar recursión
    private Cursada cursada;

    @ColumnDefault("true")
    @Column(name = "editable")
    private Boolean editable;

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }


    public Cursada getCursada() {
        return cursada;
    }

    public void setCursada(Cursada notaCursada) {
        this.cursada = notaCursada;
    }


    // Getters y Setters
    public Long getNotaId() {
        return notaId;
    }

    public void setNotaId(Long id) {
        this.notaId = id;
    }


    public String getNotaCalificacionNotaLetra() {
        return notaCalificacionNotaLetra;
    }

    public void setNotaCalificacionNotaLetra(String notaCalificacionNotaLetra) {
        this.notaCalificacionNotaLetra = notaCalificacionNotaLetra;
    }

    public LocalDate getNotaFechaNota() {
        return notaFechaNota;
    }

    public void setNotaFechaNota(LocalDate notaFechaNota) {
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

    public Double getNotaCalificacionNotaNumero() {
        return notaCalificacionNotaNumero;
    }

    public void setNotaCalificacionNotaNumero(Double notaCalificacionNotaNumero) {
        this.notaCalificacionNotaNumero = notaCalificacionNotaNumero;
    }
}
