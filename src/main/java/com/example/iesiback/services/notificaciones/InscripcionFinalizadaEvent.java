package com.example.iesiback.services.notificaciones;
import java.time.LocalDate;

/**
 * Aviso de que una inscripción quedó finalizada por primera vez.
 * Lo publica FinalizacionInscripcionService (solo con resultado FINALIZADA, nunca con YA_FINALIZADA)
 * y lleva todo lo necesario para el correo, así el envío no vuelve a consultar la base.
 */
public record InscripcionFinalizadaEvent(
        String correo,
        String nombre,
        String curso,
        LocalDate fechaInicio,
        String horario,
        String legajoId,
        String codigoSeguimiento
) {}