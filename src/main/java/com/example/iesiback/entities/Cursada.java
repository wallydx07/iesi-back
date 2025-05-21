package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

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
