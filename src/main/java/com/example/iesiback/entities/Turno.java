package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "turno")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Turno {
    @Id
    @Size(max = 50)
    @Column(name = "turno_id", nullable = false, length = 50)
    private String turnoId;

    @Size(max = 50)
    @Column(name = "turno_mes", length = 50)
    private String turnoMes;

    @Size(max = 50)
    @Column(name = "turno_anio", length = 50)
    private String turnoAnio;

    @Column(name = "turno_limite")
    private LocalDate turnoLimite;

    @OneToMany(mappedBy = "turno")
    @JsonIgnore
    private Set<CursadaExamen> cursadaExamen = new LinkedHashSet<>();

    @Size(max = 50)
    @Column(name = "llamado", length = 50)
    private String llamado;

    public String getLlamado() {
        return llamado;
    }

    public void setLlamado(String llamado) {
        this.llamado = llamado;
    }

    public Set<CursadaExamen> getCursadaExamen() {
        return cursadaExamen;
    }

    public void setCursadaExamen(Set<CursadaExamen> cursadaExamen) {
        this.cursadaExamen = cursadaExamen;
    }

    public String getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(String turnoId) {
        this.turnoId = turnoId;
    }

    public String getTurnoMes() {
        return turnoMes;
    }

    public void setTurnoMes(String turnoMes) {
        this.turnoMes = turnoMes;
    }

    public String getTurnoAnio() {
        return turnoAnio;
    }

    public void setTurnoAnio(String turnoAnio) {
        this.turnoAnio = turnoAnio;
    }

    public LocalDate getTurnoLimite() {
        return turnoLimite;
    }

    public void setTurnoLimite(LocalDate turnoLimite) {
        this.turnoLimite = turnoLimite;
    }
}
