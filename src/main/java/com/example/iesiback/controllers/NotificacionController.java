package com.example.iesiback.controllers;


import com.example.iesiback.entities.Notificacion;
import com.example.iesiback.services.NotificacionDBService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionDBService notificacionService;

    public NotificacionController(NotificacionDBService notificacionService) {
        this.notificacionService = notificacionService;
    }

    // 🔹 Obtener notificaciones pendientes
    @GetMapping("/pendientes/{usuarioId}")
    public ResponseEntity<List<Notificacion>> obtenerPendientes(@PathVariable Long usuarioId) {
        List<Notificacion> pendientes = notificacionService.pendientes(usuarioId);
        return ResponseEntity.ok(pendientes);
    }

    // 🔹 Marcar todas como leídas
    @PostMapping("/marcar-leidas/{usuarioId}")
    public ResponseEntity<Void> marcarComoLeidas(@PathVariable Long usuarioId) {
        notificacionService.marcarComoLeido(usuarioId);
        return ResponseEntity.ok().build();
    }
}
