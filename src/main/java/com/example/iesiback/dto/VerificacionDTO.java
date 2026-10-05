package com.example.iesiback.dto;

import com.example.iesiback.enums.EstadoVerificacion;

import java.time.LocalDate;

public record VerificacionDTO(
        EstadoVerificacion estado,
        String titularNombre,
        String titularDni,          // enmascarado
        String titulo,
        String detalle,
        LocalDate fechaEmision,
        LocalDate fechaVencimiento
) {}