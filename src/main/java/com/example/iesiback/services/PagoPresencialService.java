package com.example.iesiback.services;

import com.example.iesiback.enums.TipoCobro;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PagoPresencialService {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.qr.external-pos-id}")
    private String externalPosId;

    @Value("${mercadopago.point.terminal-id}")
    private String terminalId;

    private final RestClient restClient;

    @Value("${app.back-url}")
    private String backUrl;

    private final Map<String, String> ultimaOrderPorPos = new ConcurrentHashMap<>();

    public Map<String, Object> crearOrder(TipoCobro tipo, String externalReference, String description, BigDecimal totalAmount) {
        try {
            return intentarCrearOrder(tipo, externalReference, description, totalAmount);
        } catch (MpOrderException e) {
            // 409 = ya hay una orden abierta en la terminal/POS → cancelar y reintentar una vez
            if (e.status() == 409) {
                String key = (tipo == TipoCobro.POINT) ? terminalId : externalPosId;
                String pendiente = ultimaOrderPorPos.get(key);
                if (pendiente != null) {
                    System.err.println("⚠️ Orden pendiente " + pendiente + " en " + key + ", cancelando y reintentando...");
                    try {
                        cancelarOrder(pendiente);
                    } catch (Exception ex) {
                        System.err.println("⚠️ No se pudo cancelar la orden pendiente: " + ex.getMessage());
                    }
                    return intentarCrearOrder(tipo, externalReference, description, totalAmount);
                }
            }
            throw e;
        }
    }

    private Map<String, Object> intentarCrearOrder(TipoCobro tipo, String externalReference, String description, BigDecimal totalAmount) {
        Map<String, Object> body = new HashMap<>(Map.of(
                "external_reference", externalReference,
                "description", description,
                "total_amount", totalAmount.toPlainString(),
                "expiration_time", "PT5M",
                "transactions", Map.of(
                        "payments", List.of(Map.of("amount", totalAmount.toPlainString()))
                ),
                "items", List.of(Map.of(
                        "title", description,
                        "unit_price", totalAmount.toPlainString(),
                        "quantity", 1,
                        "unit_measure", "unit"
                ))
        ));

        switch (tipo) {
            case POINT -> {
                body.put("type", "point");
                body.put("config", Map.of("point", Map.of("terminal_id", terminalId)));
            }
            case QR_ESTATICO -> {
                body.put("type", "qr");
                body.put("config", Map.of("qr", Map.of(
                        "external_pos_id", externalPosId,
                        "mode", "static"
                )));
            }
        }

        Map<String, Object> order = restClient.post()
                .uri("https://api.mercadopago.com/v1/orders")
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Idempotency-Key", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    System.err.println("❌ MP Orders " + res.getStatusCode().value()
                            + " | tipo=" + tipo
                            + " | extRef=" + externalReference
                            + " | body=" + errorBody);
                    throw new MpOrderException(res.getStatusCode().value(), errorBody);
                })
                .body(Map.class);

        // Recordar la orden activa por terminal/POS para poder cancelarla si queda colgada
        if (order != null && order.get("id") != null) {
            String key = (tipo == TipoCobro.POINT) ? terminalId : externalPosId;
            ultimaOrderPorPos.put(key, order.get("id").toString());
        }
        return order;
    }

    public static class MpOrderException extends RuntimeException {
        private final int status;
        public MpOrderException(int status, String body) {
            super("MP Orders error " + status + ": " + body);
            this.status = status;
        }
        public int status() { return status; }
    }

    public Map<String, Object> consultarOrder(String orderId) {
        return restClient.get()
                .uri("https://api.mercadopago.com/v1/orders/" + orderId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> cancelarOrder(String orderId) {
        return restClient.post()
                .uri("https://api.mercadopago.com/v1/orders/" + orderId + "/cancel")
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Idempotency-Key", UUID.randomUUID().toString())
                .retrieve()
                .body(Map.class);
    }
}