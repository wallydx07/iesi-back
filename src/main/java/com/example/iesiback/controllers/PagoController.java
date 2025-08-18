package com.example.iesiback.controllers;

import com.example.iesiback.dto.ProductoDTO;
import com.example.iesiback.entities.Atencion;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.services.AtencionService;
import com.example.iesiback.services.PagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final AtencionService atencionService;

    public PagoController(PagoService pagoService, AtencionService atencionService) {
        this.pagoService = pagoService;
        this.atencionService = atencionService;
    }

    @PostMapping("/crear-preferencia")
    public ResponseEntity<?> crearPreferencia(@RequestBody ProductoDTO producto) {
        try {
            Map<String, String> datos = pagoService.crearPreferencia(producto);
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }


    // Guardar o actualizar un Pago
    @PostMapping("/atencion/{atencionId}")
    public ResponseEntity<Pago> guardarPAgoAT(@RequestBody Pago pago, @PathVariable Integer atencionId) {
        Atencion atencion= atencionService.findById(atencionId).get();
        pago.setAtencion(atencion);
        pago.setResponsable(atencion.getAtencionUsuario());
        Pago pagoGuardado = pagoService.guardar(pago);
        return ResponseEntity.ok(pagoGuardado);
    }

    // Guardar o actualizar un Pago
    @PostMapping
    public ResponseEntity<Pago> guardar(@RequestBody Pago pago, @RequestBody String atencionId) {

        Pago pagoGuardado = pagoService.guardar(pago);
        return ResponseEntity.ok(pagoGuardado);
    }

    // Buscar un Pago por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pago> buscarPorId(@PathVariable Integer id) {
        Optional<Pago> pagoOpt = pagoService.buscarPorId(id);
        return pagoOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Buscar un Pago por ID
    @GetMapping("/atencion/{id}")
    public ResponseEntity<Pago> buscarPorAtencionId(@PathVariable Integer id) {
//        Optional<Pago> pagoOpt = pagoService.buscarPorId(id);

        Optional<Pago> pagoOpt = pagoService.findByAtencionId(id);

        return pagoOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Listar todos los Pagos
    @GetMapping
    public ResponseEntity<List<Pago>> listarTodos() {
        List<Pago> pagos = pagoService.listarTodos();
        return ResponseEntity.ok(pagos);
    }

    // Eliminar un Pago por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        Optional<Pago> pagoOpt = pagoService.buscarPorId(id);
        if (pagoOpt.isPresent()) {
            pagoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Webhook Mercado Pago
    @PostMapping("/webhook")
    public ResponseEntity<?> recibirNotificacion(@RequestBody Map<String, Object> payload) {
        try {
            pagoService.procesarWebhook(payload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar webhook");
        }
    }

    @PostMapping("/inicial")
    public ResponseEntity<Pago> crearPagoInicial(@RequestBody Pago pago) {
        if (pago.getAtencion() == null || pago.getAtencion().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        pago.setEstado("pendiente");
        pago.setCreadoEn(Instant.now());

        Pago guardado = pagoService.guardar(pago);
        return ResponseEntity.ok(guardado);
    }


//    // Consulta estado pago por atención (polling)
//    @GetMapping("/estado/atencion/{atencionId}")
//    public ResponseEntity<String> obtenerEstadoPago(@PathVariable Long atencionId) {
//        return pagoService.obtenerEstadoPorAtencion(atencionId)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body("No se encontró pago para la atención"));
//    }
}
