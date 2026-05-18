package com.example.iesiback.controllers;

import com.example.iesiback.services.CierreDiarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity; // 👈 FALTABA ESTO
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/cierre")
@CrossOrigin("*")
public class CierreDiarioController {

    private final CierreDiarioService cierreService;

    public CierreDiarioController(CierreDiarioService cierreService) {
        this.cierreService = cierreService;
    }


    @GetMapping("/estado-hoy")
    public ResponseEntity<Boolean> estaCerradoHoy() {
        return ResponseEntity.ok(cierreService.estaCerradoHoy());
    }


//    @PostMapping("/cerrar-dia")
//    public ResponseEntity<String> cerrarDia(@RequestParam String usuarioId) {
//        cierreService.cerrarDia(usuarioId);
//        return ResponseEntity.ok("Día cerrado correctamente");
//    }
//    @PostMapping("/auditar-dia")
//    public ResponseEntity<String> auditarDia(@RequestParam String usuarioId) {
//        cierreService.auditarDia(usuarioId);
//        return ResponseEntity.ok("Día auditado correctamente");
//    }



    // 🔵 CERRAR DÍA
    @PostMapping("/cerrar-dia")
    public ResponseEntity<String> cerrarDia(
            @RequestParam String usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        // Si no mandan fecha, tomamos la de hoy por defecto
        LocalDate fechaProceso = (fecha != null) ? fecha : LocalDate.now();

        cierreService.cerrarDia(usuarioId, fechaProceso);
        return ResponseEntity.ok("Día " + fechaProceso + " cerrado correctamente por el usuario " + usuarioId);
    }

    // 🧾 AUDITAR DÍA
    @PostMapping("/auditar-dia")
    public ResponseEntity<String> auditarDia(
            @RequestParam String usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        // Si no mandan fecha, tomamos la de hoy por defecto
        LocalDate fechaProceso = (fecha != null) ? fecha : LocalDate.now();

        cierreService.auditarDia(usuarioId, fechaProceso);
        return ResponseEntity.ok("Día " + fechaProceso + " auditado correctamente para el usuario " + usuarioId);
    }


    @GetMapping("/estado-completo")
    public ResponseEntity<String> estadoCompleto() {
        return ResponseEntity.ok(cierreService.estadoHoy());
    }
}