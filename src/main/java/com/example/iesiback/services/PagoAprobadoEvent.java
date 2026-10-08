package com.example.iesiback.services;

/**
 * Aviso de que un Pago quedó APROBADO por un canal interno
 * (caja presencial QR/Point, validación manual, cambio de estado, efectivo).
 *
 * Lo publica PagoServiceImpl y lo atiende PagoAprobadoListener.
 * El webhook de Checkout Pro no lo usa: llama directamente a AccionesPagoAprobadoService
 * para poder responder 500 y que Mercado Pago reintente si algo falla.
 */
public record PagoAprobadoEvent(Integer pagoId) {}