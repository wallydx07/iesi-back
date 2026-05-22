package com.example.iesiback.dto;

import lombok.Builder;
import lombok.Value;
import java.util.List;

@Value
@Builder
public class ReporteSituacionAcademicaDTO {
    String legajoId;
    long materiasAceptadas;
    long materiasProvisorias;
    long materiasRechazadas;
    String resumenGeneral; // El "análisis inteligente" global
    List<DetalleMateriaReporteDTO> detalles;
}