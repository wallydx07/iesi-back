package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "cursada")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cursada {

    @Id
    @ColumnDefault("nextval('cursada_cursada_id_seq')")
    @Column(name = "cursada_id", nullable = false)
    private Integer id;



    @Column(name = "cursada_inscripto")
    private Boolean cursadaInscripto;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cursada_materia_carrera_id")
    @JsonBackReference
    private MateriaCarrera cursadaMateriaCarrera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cursada_nota_id")
    @JsonManagedReference
    private Nota cursadaNota;

    @Size(max = 50)
    @Column(name = "cursada_legajo_id", length = 50)
    private String cursadaLegajoId;

    public String getCursadaLegajoId() {
        return cursadaLegajoId;
    }

    public void setCursadaLegajoId(String cursadaLegajoId) {
        this.cursadaLegajoId = cursadaLegajoId;
    }

    // Getters y Setters
    public Nota getCursadaNota() {
        return cursadaNota;
    }

    public void setCursadaNota(Nota cursadaNota) {
        this.cursadaNota = cursadaNota;
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
