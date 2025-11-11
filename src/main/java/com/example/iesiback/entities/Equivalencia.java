package com.example.iesiback.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "equivalencia")
public class Equivalencia {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equivalencia_id", nullable = false)
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_id")
    private Nota nota;

    @Column(name = "legajo_id")
    private String legajoId;

    @Column(name = "materia_id")
    private String materiaId;

    @Size(max = 255)
    @Column(name = "institucion_origen")
    private String institucionOrigen;

    @Size(max = 255)
    @Column(name = "materia_origen")
    private String materiaOrigen;

    @Size(max = 255)
    @Column(name = "carrera_origen")
    private String carreraOrigen;

    @Size(max = 255)
    @Column(name = "resolucion")
    private String resolucion;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @Size(max = 100)
    @Column(name = "usuario", length = 100)
    private String usuario;

    public String getLegajoId() {
        return legajoId;
    }

    public void setLegajoId(String legajoId) {
        this.legajoId = legajoId;
    }

    public String getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(String materiaId) {
        this.materiaId = materiaId;
    }

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


    public String getInstitucionOrigen() {
        return institucionOrigen;
    }

    public void setInstitucionOrigen(String institucionOrigen) {
        this.institucionOrigen = institucionOrigen;
    }

    public String getMateriaOrigen() {
        return materiaOrigen;
    }

    public void setMateriaOrigen(String materiaOrigen) {
        this.materiaOrigen = materiaOrigen;
    }

    public String getCarreraOrigen() {
        return carreraOrigen;
    }

    public void setCarreraOrigen(String carreraOrigen) {
        this.carreraOrigen = carreraOrigen;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

}