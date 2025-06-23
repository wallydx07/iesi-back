package com.example.iesiback.controllers;

import com.example.iesiback.services.VerificadorAsistenciaAutomatica;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin(origins={"*"})
@RestController
@RequestMapping("/api/verificacion")
public class VerificacionAsistenciaController {

    private final VerificadorAsistenciaAutomatica verificador;

    public VerificacionAsistenciaController(VerificadorAsistenciaAutomatica verificador) {
        this.verificador = verificador;
    }

    @PostMapping("/ausencias")
    public ResponseEntity<String> verificarAusenciasPorFecha(@RequestParam("fecha") String fechaStr) {
        try {
            LocalDate fecha = LocalDate.parse(fechaStr); // formato: yyyy-MM-dd
            verificador.verificarAusenciasPorFecha(fecha);
            return ResponseEntity.ok("✔️ Verificación ejecutada para la fecha: " + fecha);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Error: " + e.getMessage());
        }
    }
}
