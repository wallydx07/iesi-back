package com.example.iesiback.services;

import com.example.iesiback.dto.PaymentDTO;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.enums.EstadoPago;
import com.example.iesiback.repositories.PagoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * Transacción 1 del webhook: aplica al Pago interno el resultado REAL del payment
 * (consultado a la API de Mercado Pago, nunca tomado del JSON del webhook).
 * Bloquea la fila del Pago para que dos notificaciones simultáneas se procesen de a una.
 */
@Service
public class RegistroPagoMpService {

    private static final Logger log = LoggerFactory.getLogger(RegistroPagoMpService.class);

    public record Resultado(Integer pagoId, EstadoPago estado) {}

    private final PagoRepository pagoRepository;
    private final ObjectMapper objectMapper;

    public RegistroPagoMpService(PagoRepository pagoRepository, ObjectMapper objectMapper) {
        this.pagoRepository = pagoRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Optional<Resultado> aplicar(Long paymentId, PaymentDTO payment) {

        Integer pagoId = resolverPagoId(payment);
        if (pagoId == null) {
            log.warn("Webhook MP sin Pago asociado. paymentId={} externalRef={} preferenceId={}",
                    paymentId, payment.getExternal_reference(), payment.getPreference_id());
            return Optional.empty();
        }

        Pago pago = pagoRepository.lockById(pagoId).orElse(null);
        if (pago == null) {
            log.warn("Webhook MP apunta a un Pago inexistente. pagoId={} paymentId={}", pagoId, paymentId);
            return Optional.empty();
        }

        EstadoPago nuevo = MercadoPagoEstados.mapear(payment.getStatus(), payment.getStatus_detail());

        if (!aceptaTransicion(pago, nuevo, paymentId)) {
            log.info("Webhook MP ignorado: pago {} está en {} y no pasa a {} (paymentId {}, actual {})",
                    pago.getId(), pago.getEstado(), nuevo, paymentId, pago.getMpPaymentId());
            return Optional.of(new Resultado(pago.getId(), pago.getEstado()));
        }

        pago.setMpPaymentId(paymentId);
        pago.setPreferenceId(payment.getPreference_id());
        pago.setExternalReference(payment.getExternal_reference());
        pago.setEstado(nuevo);
        pago.setStatusDetail(payment.getStatus_detail());
        pago.setMetodoPago(payment.getPayment_method_id());
        // tipoPago NO se pisa: conserva el concepto cobrado
        pago.setMontoTotal(payment.getTransaction_amount());
        pago.setMoneda(payment.getCurrency_id());
        pago.setFechaPago(payment.getDate_approved());
        pago.setRawResponse(objectMapper.convertValue(payment, Map.class));

        if (payment.getPayer() != null) {
            PaymentDTO.Payer payer = payment.getPayer();
            String nombre = ((payer.getFirst_name() != null ? payer.getFirst_name() : "") + " "
                    + (payer.getLast_name() != null ? payer.getLast_name() : "")).trim();
            if (!nombre.isEmpty()) {
                pago.setNombrePagador(nombre);
            }
            if (payer.getIdentification() != null && payer.getIdentification().getNumber() != null) {
                pago.setDniPagador(payer.getIdentification().getNumber());
            }
        }

        log.info("Pago {} actualizado a {} (paymentId {})", pago.getId(), nuevo, paymentId);
        return Optional.of(new Resultado(pago.getId(), pago.getEstado()));
    }

    private Integer resolverPagoId(PaymentDTO payment) {
        String ext = payment.getExternal_reference();
        if (ext != null && ext.matches("\\d{1,9}")) {
            return Integer.valueOf(ext);
        }
        if (payment.getPreference_id() != null) {
            return pagoRepository.findByPreferenceId(payment.getPreference_id())
                    .map(Pago::getId)
                    .orElse(null);
        }
        return null;
    }

    /**
     * - Si ya hubo dinero (APROBADO/REEMBOLSADO/CONTRACARGO), solo el MISMO payment lo mueve,
     *   y solo hacia reembolso o contracargo.
     * - Un PENDIENTE con payment en curso no lo pisa el rechazo tardío de otro intento.
     * - Desde RECHAZADO o CANCELADO se acepta todo (reintento con otra tarjeta).
     */
    static boolean aceptaTransicion(Pago pago, EstadoPago nuevo, Long paymentId) {
        boolean mismoPayment = paymentId.equals(pago.getMpPaymentId());
        return switch (pago.getEstado()) {
            case APROBADO -> mismoPayment
                    && (nuevo == EstadoPago.APROBADO || nuevo == EstadoPago.REEMBOLSADO || nuevo == EstadoPago.CONTRACARGO);
            case REEMBOLSADO -> mismoPayment
                    && (nuevo == EstadoPago.REEMBOLSADO || nuevo == EstadoPago.CONTRACARGO);
            case CONTRACARGO -> mismoPayment && nuevo == EstadoPago.CONTRACARGO;
            case PENDIENTE -> pago.getMpPaymentId() == null || mismoPayment || nuevo == EstadoPago.APROBADO;
            case RECHAZADO, CANCELADO -> true;
        };
    }
}