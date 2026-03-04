package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "materia_carrera")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // Ignora los proxies
public class MateriaCarrera {
    @Id
    @ColumnDefault("nextval('materia_carrera_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 10)
    @Column(name = "libro", length = 10)
    private String libro;

    @Size(max = 10)
    @Column(name = "folio", length = 10)
    private String folio;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "firma")
    private Boolean firma;

    @Column(name = "fmc_docente")
    private Long fmcDocente;

    @Size(max = 100)
    @Column(name = "division", length = 100)
    private String division;

    @Size(max = 100)
    @Column(name = "turno", length = 100)
    private String turno;

    @Size(max = 20)
    @Column(name = "dia", length = 20)
    private String dia;

    @Size(max = 10)
    @Column(name = "inicio", length = 10)
    private String inicio;

    @Size(max = 10)
    @Column(name = "fin", length = 10)
    private String fin;


    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    @JsonIgnore
    @OneToMany(mappedBy = "materiaCarrera")
    private Set<Cursada> cursadas = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id")
    private Materia materia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;


    @OneToMany(mappedBy = "materiaCarrera")
    @JsonIgnore
    private Set<InformeAsistenciaAlumno> informeAsistenciaAlumnos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "materiaCarrera")
    @JsonIgnore
    private Set<com.example.iesiback.entities.LibroTema> libroTemas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "materiaCarrera")
    @JsonIgnore
    private Set<com.example.iesiback.entities.PersonalHorario> personalHorarios = new LinkedHashSet<>();

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Boolean getFirma() {
        return firma;
    }

    public void setFirma(Boolean firma) {
        this.firma = firma;
    }

    public Long getFmcDocente() {
        return fmcDocente;
    }

    public void setFmcDocente(Long fmcDocente) {
        this.fmcDocente = fmcDocente;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public Set<Cursada> getCursadas() {
        return cursadas;
    }

    public void setCursadas(Set<Cursada> cursadas) {
        this.cursadas = cursadas;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public Set<InformeAsistenciaAlumno> getInformeAsistenciaAlumnos() {
        return informeAsistenciaAlumnos;
    }

    public void setInformeAsistenciaAlumnos(Set<InformeAsistenciaAlumno> informeAsistenciaAlumnos) {
        this.informeAsistenciaAlumnos = informeAsistenciaAlumnos;
    }

    public Set<LibroTema> getLibroTemas() {
        return libroTemas;
    }

    public void setLibroTemas(Set<LibroTema> libroTemas) {
        this.libroTemas = libroTemas;
    }

    public Set<PersonalHorario> getPersonalHorarios() {
        return personalHorarios;
    }

    public void setPersonalHorarios(Set<PersonalHorario> personalHorarios) {
        this.personalHorarios = personalHorarios;
    }
}