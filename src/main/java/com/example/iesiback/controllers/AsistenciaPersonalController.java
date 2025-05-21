package com.example.iesiback.controllers;

import com.example.iesiback.dto.AsistenciaEventoDTO;
import com.example.iesiback.entities.AsistenciaPersonal;
import com.example.iesiback.services.AsistenciaPersonalService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaPersonalController {

    private final AsistenciaPersonalService service;

    public AsistenciaPersonalController(AsistenciaPersonalService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AsistenciaPersonal>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaPersonal> buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AsistenciaPersonal> crear(@RequestBody AsistenciaPersonal asistencia) {
        return ResponseEntity.ok(service.guardar(asistencia));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AsistenciaPersonal> actualizar(@PathVariable Integer id, @RequestBody AsistenciaPersonal asistencia) {
        return service.buscarPorId(id).map(existing -> {
            asistencia.setId(id); // aseguramos que sea actualización
            return ResponseEntity.ok(service.guardar(asistencia));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/existe")
    public ResponseEntity<Boolean> verificarAsistencia(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("dni") Long dni,
            @RequestParam("horarioId") Integer horarioId) {
        boolean existe = service.verificarSiYaEstaRegistrado(fecha, dni, horarioId);
        return ResponseEntity.ok(existe);
    }


    @PostMapping("/registrar")
    public ResponseEntity<String> registrarAsistenciaAutomatica(
            @RequestParam("dni") Long dni,
            @RequestParam("horarioId") Integer horarioId) {

        LocalDate fechaHoy = LocalDate.now();
        boolean existe = service.verificarSiYaEstaRegistrado(fechaHoy, dni, horarioId);

        if (existe) {
            boolean actualizado = service.marcarHoraSalida(fechaHoy, dni, horarioId);
            if (actualizado) {
                return ResponseEntity.ok("⏺ Salida registrada correctamente.");
            } else {
                return ResponseEntity.badRequest().body("❌ No se pudo registrar la salida.");
            }
        } else {
            service.registrarEntrada(fechaHoy, dni, horarioId);
            return ResponseEntity.ok("✅ Entrada registrada correctamente.");
        }
    }


    @PostMapping("/desde-dispositivo")
    public ResponseEntity<String> registrarDesdeDispositivo(@RequestParam("dni") String dni) {
        LocalDate fechaHoy = LocalDate.now();
        // 🚧 Buscar el horario asignado automáticamente con el DNI
        Boolean asistencia = service.AsistenciaDahua(dni);
        if (asistencia) {
            return ResponseEntity.ok("✅ Asistencia registrada correctamente.");
        } else {
            return ResponseEntity.badRequest().body("❌ No se pudo registrar la salida.");
        }
    }
}