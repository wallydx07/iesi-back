package com.example.iesiback.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

// ReporteAcademicoDTO.java
@Data
@Builder
public class ReporteAcademicoDTO {

    private String legajoId;
    private int totalMaterias;
    private int puedeCursar;
    private int puedeRendir;
    private int conDeuda;

    private List<MateriaEstadoDTO> materias;

    @Data
    @Builder
    public static class MateriaEstadoDTO {

        private Integer materiaOrden;
        private String materiaNombre;
        private String notaEstado;        // APROBADO, REGULAR, LIBRE, etc.
        private String calificacion;      // "7", "R", "L", "D", "P", "-"

        // ── Cursada ──────────────────────────────────────
        private boolean puedeCursar;
        private String estadoCursada;     // "Aceptada" | "Provisoria" | "-"
        private List<String> correlativasCursadaPendientes;
        private List<String> observacionesCursada;

        // ── Examen ───────────────────────────────────────
        private boolean puedeRendir;
        private String estadoExamen;      // "Aceptada" | "Provisoria" | "-"
        private List<String> correlativasExamenPendientes;
        private List<String> observacionesExamen;

        // ── Explicación legible ──────────────────────────
        private String explicacionCursada;
        private String explicacionExamen;
    }
}