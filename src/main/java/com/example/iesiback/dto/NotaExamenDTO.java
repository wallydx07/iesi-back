package com.example.iesiback.dto;

public interface NotaExamenDTO {

    // Datos del alumno
    String getAlumnoDni();
    String getAlumnoNombre();
    String getAlumnoApellido();
    String getLegajoId();
    Long getNotaId();
    Integer getNotaCalificacionNotaNumero();
    String getNotaCalificacionNotaLetra();
    String getNotaCondicion();
    String getNotaEstado();
    String getNotaLibro();
    String getNotaFolio();
    String getNotaFecha();
    String getNotaObservaciones();
    String getNotaUsuario();
    Long getPermisoId();
    String getStatus();
}