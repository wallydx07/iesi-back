package com.example.iesiback.services;

import java.util.Set;

/**
 * Lo que debe ocurrir cuando se aprueba el pago de un tipo de trámite.
 *
 * Para agregar un comportamiento nuevo (por ejemplo, constancias), alcanza con crear
 * un @Service que implemente esta interfaz: Spring lo detecta solo.
 *
 * ejecutar() DEBE ser idempotente: el mismo pago puede notificarse varias veces
 * (reintentos de Mercado Pago, validación manual repetida, reintento administrativo).
 */
public interface AccionPagoAprobado {

    /** Valores de Tramite.tramiteTipo que atiende esta acción. */
    Set<String> tiposDeTramite();

    void ejecutar(Integer pagoId);
}