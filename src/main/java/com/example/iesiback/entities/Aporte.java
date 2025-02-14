package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "aporte")
public class Aporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Hibernate maneja la secuencia automáticamente
    @Column(name = "aporte_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aporte_legajo_id")
    @JsonBackReference // Evita recursión infinita con Legajo
    private Legajo aporteLegajo;

    @Column(name = "aporte_nro_recibo")
    private Integer aporteNroRecibo;

    @Column(name = "aporte_talonario_recibo")
    private Integer aporteTalonarioRecibo;

    @Column(name = "aporte_monto")
    private Integer aporteMonto;

    @Column(name = "aporte_fecha")
    private LocalDate aporteFecha;

    @Size(max = 200)
    @Column(name = "aporte_obs", length = 200)
    private String aporteObs;

    @Column(name = "validado")
    private Boolean validado;

    @Size(max = 20)
    @Column(name = "usuario", length = 20)
    private String usuario;

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Legajo getAporteLegajo() {
        return aporteLegajo;
    }

    public void setAporteLegajo(Legajo aporteLegajo) {
        this.aporteLegajo = aporteLegajo;
    }

    public Integer getAporteNroRecibo() {
        return aporteNroRecibo;
    }

    public void setAporteNroRecibo(Integer aporteNroRecibo) {
        this.aporteNroRecibo = aporteNroRecibo;
    }

    public Integer getAporteTalonarioRecibo() {
        return aporteTalonarioRecibo;
    }

    public void setAporteTalonarioRecibo(Integer aporteTalonarioRecibo) {
        this.aporteTalonarioRecibo = aporteTalonarioRecibo;
    }

    public Integer getAporteMonto() {
        return aporteMonto;
    }

    public void setAporteMonto(Integer aporteMonto) {
        this.aporteMonto = aporteMonto;
    }

    public LocalDate getAporteFecha() {
        return aporteFecha;
    }

    public void setAporteFecha(LocalDate aporteFecha) {
        this.aporteFecha = aporteFecha;
    }

    public String getAporteObs() {
        return aporteObs;
    }

    public void setAporteObs(String aporteObs) {
        this.aporteObs = aporteObs;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {

    }
}