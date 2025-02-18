package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cursada_examen")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CursadaExamen {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cursada_examen_id_gen")
    @SequenceGenerator(name = "cursada_examen_id_gen", sequenceName = "cursada_examen_cursada_examen_id_seq", allocationSize = 1)
    @Column(name = "cursada_examen_id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "vocal1_dni", length = 50)
    private String vocal1Dni;

    @Size(max = 50)
    @Column(name = "vocal2_dni", length = 50)
    private String vocal2Dni;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Size(max = 10)
    @Column(name = "hora", length = 10)
    private String hora;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id", referencedColumnName = "turno_id") // Referencia correcta a Turno
    @JsonBackReference
    private Turno turno;

    @Size(max = 50)
    @Column(name = "materia_id", length = 50)
    private String materiaId;

    @Size(max = 10)
    @Column(name = "libro", length = 10)
    private String libro;

    @Size(max = 10)
    @Column(name = "folio", length = 10)
    private String folio;

    @Column(name = "firma")
    private Boolean firma;

    @Column(name = "docente_dni")
    private Long docenteDni;

    @OneToMany(mappedBy = "cursadaExamen")
    @JsonIgnore // ✅ Evita que Jackson serialice esta relación
    private Set<Examen> examen = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVocal1Dni() {
        return vocal1Dni;
    }

    public void setVocal1Dni(String vocal1Dni) {
        this.vocal1Dni = vocal1Dni;
    }

    public String getVocal2Dni() {
        return vocal2Dni;
    }

    public void setVocal2Dni(String vocal2Dni) {
        this.vocal2Dni = vocal2Dni;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public String getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(String materiaId) {
        this.materiaId = materiaId;
    }

    public String getLibro() {
        return libro;
    }

    public void setLibro(String libro) {
        this.libro = libro;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Boolean getFirma() {
        return firma;
    }

    public void setFirma(Boolean firma) {
        this.firma = firma;
    }

    public Long getDocenteDni() {
        return docenteDni;
    }

    public void setDocenteDni(Long docenteDni) {
        this.docenteDni = docenteDni;
    }

    public Set<Examen> getExamen() {
        return examen;
    }

    public void setExamen(Set<Examen> examen) {
        this.examen = examen;
    }

}