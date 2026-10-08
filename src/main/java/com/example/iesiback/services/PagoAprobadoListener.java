package com.example.iesiback.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Atiende los pagos aprobados por canales internos (caja, validación manual, efectivo).
 *
 * - AFTER_COMMIT: si el pago se aprobó dentro de una transacción, la acción corre recién
 *   cuando el APROBADO quedó guardado. Si esa transacción se revierte, no se hace nada.
 * - fallbackExecution = true: si el pago se aprobó fuera de una transacción
 *   (la mayoría de los métodos de PagoServiceImpl), la acción corre en el momento.
 *
 * Si la acción falla, NO se revierte el pago ni falla la operación de la caja:
 * el cobro ya ocurrió. Se registra el error y se puede reintentar con
 * POST /api/pagos/{id}/finalizar.
 */
@Component
public class PagoAprobadoListener {

    private static final Logger log = LoggerFactory.getLogger(PagoAprobadoListener.class);

    private final AccionesPagoAprobadoService accionesPagoAprobadoService;

    public PagoAprobadoListener(AccionesPagoAprobadoService accionesPagoAprobadoService) {
        this.accionesPagoAprobadoService = accionesPagoAprobadoService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void alAprobarsePago(PagoAprobadoEvent evento) {
        try {
            accionesPagoAprobadoService.ejecutar(evento.pagoId());
        } catch (Exception e) {
            log.error("Pago {} aprobado pero falló la acción posterior. Reintentar con POST /api/pagos/{}/finalizar",
                    evento.pagoId(), evento.pagoId(), e);
        }
    }
}