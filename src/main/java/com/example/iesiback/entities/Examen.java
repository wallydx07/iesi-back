package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "examen")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Examen {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "examen_id_gen")
    @SequenceGenerator(name = "examen_id_gen", sequenceName = "examen_examen_id_seq", allocationSize = 1)
    @Column(name = "examen_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_id")
    @JsonIgnoreProperties("examen") // ✅ Evita referencia cíclica en CursadaExamen
    private Nota nota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cursada_examen_id")
//    @JsonIgnoreProperties("examen") // ✅ Evita referencia cíclica en CursadaExamen
    @JsonBackReference
    private CursadaExamen cursadaExamen;

    @Column(name = "examen_inscripto")
    private Boolean examenInscripto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permiso_id")
    @JsonIgnoreProperties("examen") // ✅ Evita referencia cíclica
    private Permiso permiso;

//    @ManyToOne
//    @JsonBackReference
//    private Permiso permiso;

    @Size(max = 50)
    @Column(name = "examen_modalidad", length = 50)
    private String examenModalidad;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Nota getNota() {
        return nota;
    }

    public void setNota(Nota nota) {
        this.nota = nota;
    }

    public CursadaExamen getCursadaExamen() {
        return cursadaExamen;
    }

    public void setCursadaExamen(CursadaExamen cursadaExamen) {
        this.cursadaExamen = cursadaExamen;
    }

    public Boolean getExamenInscripto() {
        return examenInscripto;
    }

    public void setExamenInscripto(Boolean examenInscripto) {
        this.examenInscripto = examenInscripto;
    }

    public Permiso getPermiso() {
        return permiso;
    }

    public void setPermiso(Permiso permiso) {
        this.permiso = permiso;
    }

    public String getExamenModalidad() {
        return examenModalidad;
    }

    public void setExamenModalidad(String examenModalidad) {
        this.examenModalidad = examenModalidad;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}