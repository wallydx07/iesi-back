package com.example.iesiback.controllers;

import com.example.iesiback.entities.CierreDiario;
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



    @GetMapping("/cierre")
    public ResponseEntity<CierreDiario> cierrePorFecha(
            @RequestParam String usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        CierreDiario cierre = cierreService.cierrePorFecha(usuarioId, fecha);

        return cierre != null
                ? ResponseEntity.ok(cierre)
                : ResponseEntity.noContent().build();   // 204 si no hay cierre para esa fecha
    }


}