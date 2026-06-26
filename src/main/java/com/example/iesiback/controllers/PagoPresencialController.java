package com.example.iesiback.controllers;

import com.example.iesiback.enums.TipoCobro;
import com.example.iesiback.services.PagoPresencialService;
import com.example.iesiback.services.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos/presencial")
@RequiredArgsConstructor
public class PagoPresencialController {

    private final PagoPresencialService pagoPresencialService;
    private final PagoService pagoService;

    @PostMapping
    public Map<String, Object> crear(@RequestBody CrearOrderRequest req) {
        return pagoPresencialService.crearOrder(req.tipo(), req.externalReference(), req.description(), req.totalAmount());
    }

    @GetMapping("/{orderId}/estado")
    public Map<String, Object> estado(@PathVariable String orderId) {
        return pagoPresencialService.consultarOrder(orderId);
    }

    @DeleteMapping("/{orderId}")
    public Map<String, Object> cancelar(@PathVariable String orderId) {
        return pagoPresencialService.cancelarOrder(orderId);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> recibirNotificacion(@RequestBody Map<String, Object> payload) {
        try {
            String type = (String) payload.get("type");
            if ("payment".equals(type)) {
                pagoService.procesarWebhook(payload);
            } else if ("order".equals(type)) {
                pagoService.procesarWebhookPresencial(payload);
            }
        } catch (Exception e) {
            System.err.println("❌ Error procesando webhook MP: " + e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    public record CrearOrderRequest(TipoCobro tipo, String externalReference, String description, BigDecimal totalAmount) {}
}