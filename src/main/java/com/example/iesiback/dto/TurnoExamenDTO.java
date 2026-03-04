package com.example.iesiback.dto;
import java.time.LocalDate;


public class TurnoExamenDTO {
    private String turnoId;
    private String turnoMes;
    private String turnoAnio;
    private LocalDate turnoLimite;
    private String llamado;

    public TurnoExamenDTO(String turnoId, String turnoMes, String turnoAnio, LocalDate turnoLimite, String llamado) {
        this.turnoId = turnoId;
        this.turnoMes = turnoMes;
        this.turnoAnio = turnoAnio;
        this.turnoLimite = turnoLimite;
        this.llamado = llamado;
    }

    public String getLlamado() {
        return llamado;
    }

    public void setLlamado(String llamado) {
        this.llamado = llamado;
    }

    public LocalDate getTurnoLimite() {
        return turnoLimite;
    }

    public void setTurnoLimite(LocalDate turnoLimite) {
        this.turnoLimite = turnoLimite;
    }

    public String getTurnoAnio() {
        return turnoAnio;
    }

    public void setTurnoAnio(String turnoAnio) {
        this.turnoAnio = turnoAnio;
    }

    public String getTurnoMes() {
        return turnoMes;
    }

    public void setTurnoMes(String turnoMes) {
        this.turnoMes = turnoMes;
    }

    public String getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(String turnoId) {
        this.turnoId = turnoId;
    }
}




