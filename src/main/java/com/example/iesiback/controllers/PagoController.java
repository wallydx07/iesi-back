package com.example.iesiback.controllers;

import com.example.iesiback.dto.PagoRequestDTO;
import com.example.iesiback.dto.ProductoDTO;
import com.example.iesiback.dto.ResumenOperadorDTO;
import com.example.iesiback.dto.ResumenRecaudacionDTO;
import com.example.iesiback.entities.PagoDetalle;
import com.example.iesiback.entities.Tramite;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.services.PagoDetalleService;
import com.example.iesiback.services.TramiteService;
import com.example.iesiback.services.PagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final TramiteService tramiteService;
    private final PagoDetalleService pagoDetalleService;

    public PagoController(PagoService pagoService, TramiteService tramiteService, PagoDetalleService pagoDetalleService) {
        this.pagoService = pagoService;
        this.tramiteService = tramiteService;
        this.pagoDetalleService = pagoDetalleService;
    }


//    @GetMapping("/ResumenRecaudacionDTO/{fechaPago}")
//    public ResponseEntity<ResumenRecaudacionDTO> ResumenRecaudacionDTO(
//            @PathVariable LocalDate fechaPago) {
//        Optional<ResumenRecaudacionDTO> pagoOpt = pagoService.ResumenRecaudacionDTO(fechaPago);
//        return pagoOpt.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }


    @GetMapping("/ResumenOperadorDTO/{fecha}")
    public ResponseEntity<List<ResumenOperadorDTO>> getResumen(
            @PathVariable LocalDate fecha) {

        return ResponseEntity.ok(
                pagoService.obtenerResumenPorOperador(fecha)
        );
    }

    @PatchMapping("/{id}/validar")
    public ResponseEntity<Void> toggleValidar(@PathVariable Long id) {
        pagoService.actualizarEstadoValidacion(id);
        return ResponseEntity.noContent().build();
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
        Tramite atencion= tramiteService.findById(atencionId).get();
        pago.setTramite(atencion);
        pago.setResponsable(atencion.getTramiteUsuario());
        Pago pagoGuardado = pagoService.guardar(pago);
        return ResponseEntity.ok(pagoGuardado);
    }

        // Guardar o actualizar un Pago
        @PostMapping
        public ResponseEntity<Pago> guardar(@RequestBody Pago pago) {
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
        if (pago.getTramite() == null || pago.getTramite().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        pago.setEstado("pendiente");
        pago.setCreadoEn(Instant.now());
        Pago guardado = pagoService.guardar(pago);
        return ResponseEntity.ok(guardado);
    }


    @GetMapping("/ResumenRecaudacionDTO/{fechaPago}")
    public ResponseEntity<ResumenRecaudacionDTO> ResumenRecaudacionDTO(
            @PathVariable LocalDate fechaPago) {

        Optional<ResumenRecaudacionDTO> pagoOpt =
                pagoService.ResumenRecaudacionDTO(fechaPago);

        return ResponseEntity.ok(
                pagoOpt.orElseGet(ResumenRecaudacionDTO::new)
        );
    }




    @PostMapping("/con-detalles")
    public ResponseEntity<Pago> guardarConDetalles(@RequestBody PagoRequestDTO request) {

        Pago pago = request.getPago();
        List<PagoDetalle> detalles = request.getDetalles();

        Pago guardado = pagoDetalleService.guardarPagoConDetalles(pago, detalles);

        return ResponseEntity.ok(guardado);
    }


    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<PagoDetalle>> obtenerDetalles(@PathVariable Integer id) {
        return ResponseEntity.ok(pagoDetalleService.obtenerPorPago(id));
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
