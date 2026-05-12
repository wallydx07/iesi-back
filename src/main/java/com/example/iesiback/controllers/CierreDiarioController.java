package com.example.iesiback.controllers;

import com.example.iesiback.services.CierreDiarioService;
import org.springframework.http.ResponseEntity; // 👈 FALTABA ESTO
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cierre")
@CrossOrigin("*")
public class CierreDiarioController {

    private final CierreDiarioService cierreService;

    public CierreDiarioController(CierreDiarioService cierreService) {
        this.cierreService = cierreService;
    }

    @PostMapping("/cerrar-dia")
    public ResponseEntity<String> cerrarDia(@RequestParam String usuarioId) {
        cierreService.cerrarDia(usuarioId);
        return ResponseEntity.ok("Día cerrado correctamente");
    }

    /**
     * 🔎 Saber si hoy está cerrado
     */
    @GetMapping("/estado-hoy")
    public ResponseEntity<Boolean> estaCerradoHoy() {
        return ResponseEntity.ok(cierreService.estaCerradoHoy());
    }

    /**
     * 🧾 Auditar día
     */
    @PostMapping("/auditar-dia")
    public ResponseEntity<String> auditarDia(@RequestParam String usuarioId) {
        cierreService.auditarDia(usuarioId);
        return ResponseEntity.ok("Día auditado correctamente");
    }

    @GetMapping("/estado-completo")
    public ResponseEntity<String> estadoCompleto() {
        return ResponseEntity.ok(cierreService.estadoHoy());
    }
}