package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cursada")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cursada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Usa auto incremento en PostgreSQL
    @Column(name = "cursada_id", nullable = false)
    private Integer id;

    @Column(name = "cursada_inscripto")
    private Boolean cursadaInscripto;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    // Relación con MateriaCarrera (Ya está bien)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cursada_materia_carrera_id")
    @JsonIgnore
    private MateriaCarrera cursadaMateriaCarrera;

    @JsonProperty("materia_carrera_id") // Expone solo el ID
    public Integer getMateriaCarreraId() {
        return cursadaMateriaCarrera != null ? cursadaMateriaCarrera.getId() : null;
    }

    @JsonProperty("materia_id") // Expone solo el ID
    public String getMateriaId() {
        return cursadaMateriaCarrera != null ? cursadaMateriaCarrera.getMateria().getMateriaId() : null;
    }

    @JsonProperty("carrera_id") // Expone solo el ID
    public String getCarreraId() {
        return cursadaMateriaCarrera != null ? cursadaMateriaCarrera.getCarrera().getCarreraId() : null;
    }

   @ManyToOne(fetch = FetchType.LAZY)  // Relación correcta con Legajo
    @JoinColumn(name = "cursada_legajo_id", nullable = false)  // Clave foránea en la BD
    @JsonIgnore
    private Legajo legajo;

    @OneToMany(mappedBy = "notaCursada", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Nota> notas = new LinkedHashSet<>();

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

    public MateriaCarrera getCursadaMateriaCarrera() {
        return cursadaMateriaCarrera;
    }

    public void setCursadaMateriaCarrera(MateriaCarrera cursadaMateriaCarrera) {
        this.cursadaMateriaCarrera = cursadaMateriaCarrera;
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
