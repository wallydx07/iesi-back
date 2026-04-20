package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pases")
public class Pases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tramite_id", nullable = false)
    @JsonBackReference
    private Tramite tramite;

    // Usuario que generó el pase
    @ManyToOne
    @JoinColumn(name = "de_usuario_id")
    private Personal deUsuario;

    @Column(name = "de_texto", columnDefinition = "text")
    private String deTexto;

    // Destino por USUARIO
    @ManyToOne
    @JoinColumn(name = "para_usuario_id")
    private Personal paraUsuario;

    // Destino por ÁREA
    @ManyToOne
    @JoinColumn(name = "para_destino_id")
    private Destino paraDestino;

    @Column(name = "para_texto", columnDefinition = "text")
    private String paraTexto;

    private LocalDateTime fecha = LocalDateTime.now();
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "text")
    private String observaciones;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tramite getTramite() {
        return tramite;
    }

    public void setTramite(Tramite tramite) {
        this.tramite = tramite;
    }

    public Personal getDeUsuario() {
        return deUsuario;
    }

    public void setDeUsuario(Personal deUsuario) {
        this.deUsuario = deUsuario;
    }

    public Personal getParaUsuario() {
        return paraUsuario;
    }

    public void setParaUsuario(Personal paraUsuario) {
        this.paraUsuario = paraUsuario;
    }

    public String getDeTexto() {
        return deTexto;
    }

    public void setDeTexto(String deTexto) {
        this.deTexto = deTexto;
    }

    public Destino getParaDestino() {
        return paraDestino;
    }

    public void setParaDestino(Destino paraDestino) {
        this.paraDestino = paraDestino;
    }

    public String getParaTexto() {
        return paraTexto;
    }

    public void setParaTexto(String paraTexto) {
        this.paraTexto = paraTexto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
