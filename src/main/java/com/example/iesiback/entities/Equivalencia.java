package com.example.iesiback.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "equivalencia")
public class Equivalencia {
    @Id
    @ColumnDefault("nextval('equivalencia_equivalencia_id_seq')")
    @Column(name = "equivalencia_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_id")
    private Nota nota;

    @Column(name = "legajo_id")
    private Integer legajoId;

    @Column(name = "materia_id")
    private Integer materiaId;

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

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha")
    private Instant fecha;

    @Size(max = 100)
    @Column(name = "usuario", length = 100)
    private String usuario;

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

    public Integer getLegajoId() {
        return legajoId;
    }

    public void setLegajoId(Integer legajoId) {
        this.legajoId = legajoId;
    }

    public Integer getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(Integer materiaId) {
        this.materiaId = materiaId;
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

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

}