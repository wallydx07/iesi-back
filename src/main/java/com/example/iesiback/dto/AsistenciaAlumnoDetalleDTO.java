package com.example.iesiback.dto;

import com.example.iesiback.enums.EstadoAsistencia;

import java.time.LocalDate;

public record AsistenciaAlumnoDetalleDTO(
        String materiaId,
        String materiaNombre,
        LocalDate fecha,
        EstadoAsistencia estado
) {}