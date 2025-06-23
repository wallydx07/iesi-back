package com.example.iesiback.controllers;

import com.example.iesiback.dto.AsistenciaEventoDTO;
import com.example.iesiback.dto.DetalleAsistenciaPersonalDTO;
import com.example.iesiback.dto.RegistroAsistenciaDTO;
import com.example.iesiback.dto.RespuestaAsistenciaDTO;
import com.example.iesiback.entities.AsistenciaPersonal;
import com.example.iesiback.services.AsistenciaPersonalService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//
//    @PutMapping("/{id}")
//    public ResponseEntity<AsistenciaPersonal> actualizar(@PathVariable Integer id, @RequestBody AsistenciaPersonal asistencia) {
//        return service.buscarPorId(id).map(existing -> {
//            asistencia.setId(id); // aseguramos que sea actualización
//            return ResponseEntity.ok(service.guardar(asistencia));
//        }).orElse(ResponseEntity.notFound().build());
//    }
//
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

//
//    @PostMapping("/registrar")
//    public ResponseEntity<String> registrarAsistenciaAutomatica(
//            @RequestParam("dni") Long dni,
//            @RequestParam("horarioId") Integer horarioId) {
//
//        LocalDate fechaHoy = LocalDate.now();
//        boolean existe = service.verificarSiYaEstaRegistrado(fechaHoy, dni, horarioId);
//
//        if (existe) {
//            boolean actualizado = service.marcarHoraSalida(fechaHoy, dni, horarioId);
//            if (actualizado) {
//                return ResponseEntity.ok("⏺ Salida registrada correctamente.");
//            } else {
//                return ResponseEntity.badRequest().body("❌ No se pudo registrar la salida.");
//            }
//        } else {
//            service.registrarEntrada(fechaHoy, dni, horarioId);
//            return ResponseEntity.ok("✅ Entrada registrada correctamente.");
//        }
//    }

//    @PostMapping("/desde-dispositivo")
//    public ResponseEntity<String> registrarDesdeDispositivo(@RequestParam("dni") String dni) {
//        LocalDate fechaHoy = LocalDate.now();
//        Boolean asistencia = service.AsistenciaDahua(dni);
//        if (asistencia) {
//            return ResponseEntity.ok("✅ Asistencia registrada correctamente.");
//        } else {
//            return ResponseEntity.badRequest().body("❌ No se pudo registrar la salida.");
//        }
//    }

    @PostMapping("/desde-dispositivo")
    public ResponseEntity<?> recibirDesdeDispositivo(@RequestBody RegistroAsistenciaDTO dto) {
        System.out.println("DNI recibido: " + dto.getDni());
        System.out.println("FechaHora: " + dto.getFechaHora());
        System.out.println("Dispositivo: " + dto.getDispositivo());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime fechaHoraCompleta = LocalDateTime.parse(dto.getFechaHora(), formatter);
        LocalDate fecha = fechaHoraCompleta.toLocalDate();
        LocalTime hora = fechaHoraCompleta.toLocalTime();
        System.out.println("Fecha: " + fecha);
        System.out.println("Hora: " + hora);

        RespuestaAsistenciaDTO asistencia = service.AsistenciaDahua(dto);

        // Imprimir mensaje antes de devolverlo
        System.out.println("Respuesta enviada: " + asistencia.getMensaje());

        if (asistencia.isExito()) {
            return ResponseEntity.ok(asistencia.getMensaje());
        } else {
            return ResponseEntity.badRequest().body(asistencia.getMensaje());
        }
    }


    @GetMapping("/detalle")
    public ResponseEntity<List<DetalleAsistenciaPersonalDTO>> obtenerDetallePorFecha(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<DetalleAsistenciaPersonalDTO> detalle = service.obtenerDetallePorFecha(fecha);
        return ResponseEntity.ok(detalle);
    }

    @GetMapping("/detalleYear")
    public ResponseEntity<List<DetalleAsistenciaPersonalDTO>> obtenerDetallePorYear(
            @RequestParam("year")  Integer year) {
        List<DetalleAsistenciaPersonalDTO> detalle = service.obtenerDetallePorYear(year);
        return ResponseEntity.ok(detalle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAsistencia(
            @PathVariable Integer id,
            @RequestBody AsistenciaPersonal update) {
        try {
            service.actualizarAsistencia(id, update);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Asistencia actualizada correctamente.");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Asistencia no encontrada.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar la asistencia.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<List<DetalleAsistenciaPersonalDTO>> obtenerPorDni(
            @PathVariable("dni") Long dni) {
        List<DetalleAsistenciaPersonalDTO> detalle = service.obtenerDetallePorDNI(dni);
        return ResponseEntity.ok(detalle);
    }
}