package com.example.iesiback.dto;

import java.time.LocalDate;
import java.util.Date;

public interface NotaCursadaDTO {
    Long getNotaId();
    String getAlumnoDni();
    String getAlumnoLegajoId();
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
    Double getPrimerParcial();
    Double getRecuperatorio1();
    Double getSegundoParcial();
    Double getRecuperatorio2();
    Double getTrabajosPracticos();
    Double getAsistencia();
    Double getColoquio();
    Double getTrabajoInstitucional();
}



