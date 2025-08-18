package com.example.iesiback.dto;

import java.time.LocalDate;

public interface NotaCursandoProjection {
    String getLegajoId();
    Long getNotaId();
    String getMateriaId();
    String getMateriaNombre();
    String getMateriaOrden();
    String getNotaEstado();
    String getMateriaCursada();
    String getMateriaExamen();
    Integer getCursadaId();
    LocalDate getNotaFechaNota();
}
