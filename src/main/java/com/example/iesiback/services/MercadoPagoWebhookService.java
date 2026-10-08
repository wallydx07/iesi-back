package com.example.iesiback.services;

import com.example.iesiback.dto.PaymentDTO;
import com.example.iesiback.enums.EstadoPago;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Orquesta el webhook de Checkout Pro (sin transacción propia):
 * 1. consulta el payment a la API de Mercado Pago,
 * 2. actualiza el Pago (RegistroPagoMpService),
 * 3. si quedó APROBADO, ejecuta la acción del tipo de trámite (AccionesPagoAprobadoService).
 *
 * A diferencia del listener, acá los errores se propagan: el controller responde 500
 * y Mercado Pago vuelve a notificar.
 */
@Service
public class MercadoPagoWebhookService {

    private final MercadoPagoService mercadoPagoService;
    private final RegistroPagoMpService registroPagoMpService;
    private final AccionesPagoAprobadoService accionesPagoAprobadoService;

    public MercadoPagoWebhookService(MercadoPagoService mercadoPagoService,
                                     RegistroPagoMpService registroPagoMpService,
                                     AccionesPagoAprobadoService accionesPagoAprobadoService) {
        this.mercadoPagoService = mercadoPagoService;
        this.registroPagoMpService = registroPagoMpService;
        this.accionesPagoAprobadoService = accionesPagoAprobadoService;
    }

    /** Lanza excepción ante cualquier fallo para que el controller responda 500 y MP reintente. */
    public void procesarPayment(Long paymentId) throws Exception {
        PaymentDTO payment = mercadoPagoService.consultarPagoPorId(paymentId);
        if (payment == null) {
            throw new IllegalStateException("Mercado Pago no devolvió el payment " + paymentId);
        }

        Optional<RegistroPagoMpService.Resultado> registro = registroPagoMpService.aplicar(paymentId, payment);

        if (registro.isPresent() && registro.get().estado() == EstadoPago.APROBADO) {
            accionesPagoAprobadoService.ejecutar(registro.get().pagoId());
        }
    }
}