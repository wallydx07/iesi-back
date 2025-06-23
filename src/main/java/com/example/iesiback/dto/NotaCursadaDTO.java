package com.example.iesiback.dto;

import java.time.LocalDate;
import java.util.Date;

public interface NotaCursadaDTO {
    Long getNotaId();
    String getAlumnoDni();
    String getAlumnoApellido();
    String getAlumnoNombre();
    LocalDate getNotaFechaNota();
    Double getNotaCalificacionNotaNumero();
    String getNotaCalificacionNotaLetra();
    String getNotaEstado();
    String getNotaLibroNota();
    String getNotaFolioNota();
    String getCursadaStatus();
    String getNotaObservaciones();
    String getNotaUsuario();

    // Nuevas columnas
    Double getCursadaPrimerParcial();
    Double getCursadaRecuperatorio1();
    Double getCursadaSegundoParcial();
    Double getCursadaRecuperatorio2();
    Double getCursadaTrabajosPracticos();
    Double getCursadaAsistencia();
    Double getCursadaColoquio();
    Double getCursadaTrabajoInstitucional();
}



