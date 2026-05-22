package com.example.iesiback.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DetalleMateriaReporteDTO {
    Integer materiaOrden;
    String materiaNombre;
    String estadoFinal; // "Aceptada", "Provisoria", "Rechazado"
    boolean puedeCursar;
    boolean puedeAprobarExamen;
    String explicacionCursada;  // Por qué sí o por qué no (Materia Cursada)
    String explicacionExamen;   // Por qué sí o por qué no (Materia Examen)
}