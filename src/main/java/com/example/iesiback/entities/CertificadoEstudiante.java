package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Entity
@Table(name = "certificado_estudiante")
public class CertificadoEstudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Hibernate maneja la secuencia automáticamente
    @Column(name = "constancia_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legajo_id")
    @JsonBackReference // Evita la recursión infinita con Legajo
    private Legajo legajo;

    @Size(max = 50)
    @Column(name = "tipo", length = 50)
    private String tipo;

    @Size(max = 100)
    @Column(name = "autoridad", length = 100)
    private String autoridad;

    @Column(name = "fecha", updatable = false)
    private Instant fecha = Instant.now(); // Asigna la fecha actual por defecto en Java

    @Size(max = 20)
    @Column(name = "estado", length = 20)
    private String estado;

    @Size(max = 200)
    @Column(name = "observaciones", length = 200)
    private String observaciones;

    @Size(max = 50)
    @Column(name = "usuario", length = 50)
    private String usuario;

    @Column(name = "validado")
    private Boolean validado;

    @Column(name = "monto")
    private Integer monto;

    @Column(name = "atencion_id", columnDefinition = "INTEGER DEFAULT 0") // Manejo correcto del valor por defecto
    private Integer atencionId;

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Legajo getLegajo() {
        return legajo;
    }

    public void setLegajo(Legajo legajo) {
        this.legajo = legajo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAutoridad() {
        return autoridad;
    }

    public void setAutoridad(String autoridad) {
        this.autoridad = autoridad;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public Integer getMonto() {
        return monto;
    }

    public void setMonto(Integer monto) {
        this.monto = monto;
    }

    public Integer getAtencionId() {
        return atencionId;
    }

    public void setAtencionId(Integer atencionId) {
        this.atencionId = atencionId;
    }
}
