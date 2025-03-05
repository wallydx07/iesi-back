package com.example.iesiback.dto;

import java.util.Date;

public interface NotaCursadaDTO {
    Long getNotaId();
    String getAlumnoDni();
    String getAlumnoApellido();
    String getAlumnoNombre();
    String getNotaFechaNota();
    Double getNotaCalificacionNotaNumero();
    String getNotaCalificacionNotaLetra();
    String getNotaEstado();
    String getNotaLibroNota();
    String getNotaFolioNota();
    String getCursadaStatus();
    String getNotaObservaciones();
    String getNotaUsuario();

}



