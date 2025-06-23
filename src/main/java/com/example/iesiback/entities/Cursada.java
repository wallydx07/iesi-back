package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cursada")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cursada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cursada_id", nullable = false)
    private Integer id;

    @Column(name = "cursada_inscripto")
    private Boolean cursadaInscripto;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cursada_materia_carrera_id")
    @JsonProperty(value = "materiaCarrera", access = JsonProperty.Access.WRITE_ONLY)
    private MateriaCarrera materiaCarrera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cursada_legajo_id", nullable = false)
    @JsonProperty(value = "legajo", access = JsonProperty.Access.WRITE_ONLY)
    private Legajo legajo;

    @OneToMany(mappedBy = "cursada", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Nota> notas = new LinkedHashSet<>();

    @Column(name = "primer_parcial", precision = 5, scale = 2)
    private BigDecimal primerParcial;

    @Column(name = "recuperatorio1", precision = 5, scale = 2)
    private BigDecimal recuperatorio1;

    @Column(name = "segundo_parcial", precision = 5, scale = 2)
    private BigDecimal segundoParcial;

    @Column(name = "recuperatorio2", precision = 5, scale = 2)
    private BigDecimal recuperatorio2;

    @Column(name = "trabajos_practicos", precision = 5, scale = 2)
    private BigDecimal trabajosPracticos;

    @Column(name = "asistencia", precision = 5, scale = 2)
    private BigDecimal asistencia;

    @Column(name = "coloquio", precision = 5, scale = 2)
    private BigDecimal coloquio;

    @Column(name = "trabajo_institucional", precision = 5, scale = 2)
    private BigDecimal trabajoInstitucional;

    public BigDecimal getTrabajoInstitucional() {
        return trabajoInstitucional;
    }

    public void setTrabajoInstitucional(BigDecimal trabajoInstitucional) {
        this.trabajoInstitucional = trabajoInstitucional;
    }

    public BigDecimal getColoquio() {
        return coloquio;
    }

    public void setColoquio(BigDecimal coloquio) {
        this.coloquio = coloquio;
    }

    public BigDecimal getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(BigDecimal asistencia) {
        this.asistencia = asistencia;
    }

    public BigDecimal getTrabajosPracticos() {
        return trabajosPracticos;
    }

    public void setTrabajosPracticos(BigDecimal trabajosPracticos) {
        this.trabajosPracticos = trabajosPracticos;
    }

    public BigDecimal getRecuperatorio2() {
        return recuperatorio2;
    }

    public void setRecuperatorio2(BigDecimal recuperatorio2) {
        this.recuperatorio2 = recuperatorio2;
    }

    public BigDecimal getSegundoParcial() {
        return segundoParcial;
    }

    public void setSegundoParcial(BigDecimal segundoParcial) {
        this.segundoParcial = segundoParcial;
    }

    public BigDecimal getRecuperatorio1() {
        return recuperatorio1;
    }

    public void setRecuperatorio1(BigDecimal recuperatorio1) {
        this.recuperatorio1 = recuperatorio1;
    }

    public BigDecimal getPrimerParcial() {
        return primerParcial;
    }

    public void setPrimerParcial(BigDecimal primerParcial) {
        this.primerParcial = primerParcial;
    }

    // ✅ Métodos seguros con null-check para evitar el error de serialización

    @JsonProperty("materia_carrera_id")
    public Integer getMateriaCarreraId() {
        return (materiaCarrera != null) ? materiaCarrera.getId() : null;
    }

    @JsonProperty("materia_id")
    public String getMateriaId() {
        return (materiaCarrera != null && materiaCarrera.getMateria() != null)
                ? materiaCarrera.getMateria().getMateriaId()
                : null;
    }

    @JsonProperty("carrera_id")
    public String getCarreraId() {
        return (materiaCarrera != null && materiaCarrera.getCarrera() != null)
                ? materiaCarrera.getCarrera().getCarreraId()
                : null;
    }

    // Getters y Setters

    public Set<Nota> getNotas() {
        return notas;
    }

    public void setNotas(Set<Nota> notas) {
        this.notas = notas;
    }

    public Legajo getLegajo() {
        return legajo;
    }

    public void setLegajo(Legajo legajo) {
        this.legajo = legajo;
    }

    public MateriaCarrera getMateriaCarrera() {
        return materiaCarrera;
    }

    public void setMateriaCarrera(MateriaCarrera materiaCarrera) {
        this.materiaCarrera = materiaCarrera;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getCursadaInscripto() {
        return cursadaInscripto;
    }

    public void setCursadaInscripto(Boolean cursadaInscripto) {
        this.cursadaInscripto = cursadaInscripto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
