package com.example.iesiback.controllers;


import com.example.iesiback.services.ConciliacionMpService;
import com.mercadopago.exceptions.MPApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/conciliacion")
@RequiredArgsConstructor
public class ConciliacionController {

    private final ConciliacionMpService conciliacionMpService;

    @GetMapping
    public ResponseEntity<?> conciliar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        try {
            if (desde.isAfter(hasta)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Rango de fechas inválido"));
            }
            return ResponseEntity.ok(conciliacionMpService.conciliar(desde, hasta));
        } catch (MPApiException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("message", "Error de Mercado Pago: " + e.getApiResponse().getContent()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Error al conciliar: " + e.getMessage()));
        }
    }

    @PatchMapping("/pagos/{id}/sincronizar")
    public ResponseEntity<?> sincronizar(@PathVariable Integer id) {
        try {
            conciliacionMpService.sincronizarPago(id);
            return ResponseEntity.ok(Map.of("message", "Pago sincronizado con Mercado Pago"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}