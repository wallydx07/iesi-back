package com.example.iesiback.services;

import com.example.iesiback.enums.TipoCobro;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Value("${app.back-url}") // o como se llame la property que usás en PagoService
    private String backUrl;

    public Map<String, Object> crearOrder(TipoCobro tipo, String externalReference, String description, BigDecimal totalAmount) {
        Map<String, Object> body = new HashMap<>(Map.of(
                "external_reference", externalReference,
                "description", description,
                "total_amount", totalAmount.toPlainString(),
                "notification_url", backUrl + "/api/pagos/presencial/webhook",
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

        return restClient.post()
                .uri("https://api.mercadopago.com/v1/orders")
                .header("Authorization", "Bearer " + accessToken)
                .header("X-Idempotency-Key", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
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
                .retrieve()
                .body(Map.class);
    }
}