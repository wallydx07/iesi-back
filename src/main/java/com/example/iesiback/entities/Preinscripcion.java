package com.example.iesiback.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
public class Preinscripcion {
    @Id
    @ColumnDefault("nextval('preinscripcion_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 15)
    @NotNull
    @Column(name = "dni", nullable = false, length = 15)
    private String dni;

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 100)
    @NotNull
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Size(max = 100)
    @NotNull
    @Column(name = "localidad", nullable = false, length = 100)
    private String localidad;

    @Size(max = 20)
    @NotNull
    @Column(name = "celular", nullable = false, length = 20)
    private String celular;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Size(max = 100)
    @Column(name = "estudios_alcanzados", length = 100)
    private String estudiosAlcanzados;

    @Size(max = 100)
    @Column(name = "departamento", length = 100)
    private String departamento;

    @Size(max = 100)
    @Column(name = "carrera_solicitada", length = 100)
    private String carreraSolicitada;

    @Size(max = 50)
    @Column(name = "estado", length = 50)
    private String estado;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha")
    private Instant fecha;

    @Size(max = 500)
    @Column(name = "obs", length = 500)
    private String obs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEstudiosAlcanzados() {
        return estudiosAlcanzados;
    }

    public void setEstudiosAlcanzados(String estudiosAlcanzados) {
        this.estudiosAlcanzados = estudiosAlcanzados;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCarreraSolicitada() {
        return carreraSolicitada;
    }

    public void setCarreraSolicitada(String carreraSolicitada) {
        this.carreraSolicitada = carreraSolicitada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

}