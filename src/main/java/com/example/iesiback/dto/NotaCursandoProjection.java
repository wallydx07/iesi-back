package com.example.iesiback.dto;

import com.example.iesiback.enums.EstadoCondicion;
import com.example.iesiback.enums.EstadoNota;

import java.time.LocalDate;

public interface NotaCursandoProjection {
    String getLegajoId();
    Long getNotaId();
    String getMateriaId();
    String getMateriaNombre();
    String getMateriaOrden();
    EstadoNota getNotaEstado();
    String getMateriaCursada();
    String getMateriaExamen();
    Integer getCursadaId();
    LocalDate getNotaFechaNota();
    EstadoCondicion getNotaCondicion();
}
