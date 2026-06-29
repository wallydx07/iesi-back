package com.example.iesiback.controllers;

import com.example.iesiback.enums.TipoCobro;
import com.example.iesiback.services.PagoPresencialService;
import com.example.iesiback.services.PagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pagos/presencial")
public class PagoPresencialController {

    private final PagoPresencialService pagoPresencialService;
    private final PagoService pagoService;

    public PagoPresencialController(PagoPresencialService pagoPresencialService, PagoService pagoService) {
        this.pagoPresencialService = pagoPresencialService;
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody CrearOrderRequest req) {
        Map<String, Object> order = pagoPresencialService.crearOrder(
                req.tipo(), req.externalReference(), req.description(), req.totalAmount()
        );
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}/estado")
    public ResponseEntity<Map<String, Object>> estado(@PathVariable String orderId) {
        return ResponseEntity.ok(pagoPresencialService.consultarOrder(orderId));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> cancelar(@PathVariable String orderId) {
        return ResponseEntity.ok(pagoPresencialService.cancelarOrder(orderId));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload) {
        try {
            String type = (String) payload.get("type");
            if ("order".equals(type)) {
                pagoService.procesarWebhookPresencial(payload);
            }
        } catch (Exception e) {
            System.err.println("❌ Error procesando webhook presencial MP: " + e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    public record CrearOrderRequest(TipoCobro tipo, String externalReference, String description, BigDecimal totalAmount) {}
}