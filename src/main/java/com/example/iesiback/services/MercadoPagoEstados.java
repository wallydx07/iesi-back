package com.example.iesiback.services;

import com.example.iesiback.enums.EstadoPago;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Traducción de estados de Mercado Pago a EstadoPago.
 * Es el mismo switch que hoy está como método privado en PagoServiceImpl,
 * movido acá para que lo usen el webhook de Checkout Pro y el presencial.
 * (En PagoServiceImpl reemplazar mapearEstadoMercadoPago(...) por MercadoPagoEstados.mapear(...).)
 */
public final class MercadoPagoEstados {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoEstados.class);

    private MercadoPagoEstados() {}

    public static EstadoPago mapear(String estadoMp, String statusDetail) {
        if (estadoMp == null) {
            return EstadoPago.PENDIENTE;
        }
        return switch (estadoMp.toLowerCase()) {
            // Checkout Pro / Payments API
            case "approved" -> EstadoPago.APROBADO;
            case "rejected" -> EstadoPago.RECHAZADO;

            // Orders API v2 (QR estático / Point)
            case "processed" -> "accredited".equalsIgnoreCase(statusDetail)
                    ? EstadoPago.APROBADO
                    : EstadoPago.PENDIENTE;
            case "expired" -> EstadoPago.CANCELADO;

            // comunes a ambas APIs
            case "cancelled", "canceled" -> EstadoPago.CANCELADO;
            case "refunded" -> EstadoPago.REEMBOLSADO;
            case "charged_back" -> EstadoPago.CONTRACARGO;
            case "pending", "in_process", "in_mediation", "authorized", "created",
                 "action_required", "processing" -> EstadoPago.PENDIENTE;

            default -> {
                log.warn("Estado MP desconocido: {} / {}", estadoMp, statusDetail);
                yield EstadoPago.PENDIENTE;
            }
        };
    }
}