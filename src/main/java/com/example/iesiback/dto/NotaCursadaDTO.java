package com.example.iesiback.dto;

import com.example.iesiback.enums.EstadoNota;

import java.time.LocalDate;

public interface NotaCursadaDTO {
    Long getNotaId();
    String getPersonaDni();
    String getPersonaLegajoId();
    String getPersonaApellido();
    String getPersonaNombre();
    LocalDate getNotaFechaNota();
    String getNotaCalificacionNotaNumero();
    String getNotaCalificacionNotaLetra();
    EstadoNota getNotaEstado();
    String getNotaLibroNota();
    String getNotaFolioNota();
    String getCursadaStatus();
    String getNotaObservaciones();
    String getNotaUsuario();

    // Nuevas columnas
    Double getPrimerParcial();
    Double getRecuperatorio1();
    Double getSegundoParcial();
    Double getRecuperatorio2();
    Double getTrabajosPracticos();
    Double getAsistencia();
    Double getColoquio();
    Double getTrabajoInstitucional();
}



