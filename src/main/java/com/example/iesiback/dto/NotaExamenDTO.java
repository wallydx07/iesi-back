package com.example.iesiback.dto;

import java.time.LocalDate;

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
    LocalDate getNotaFechaNota(); // ✅ coincide con nota_fecha_nota
    String getNotaObservaciones();
    String getNotaUsuario();
    Long getPermisoId();
    String getStatus();
}