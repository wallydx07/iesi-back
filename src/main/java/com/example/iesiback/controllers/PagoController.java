package com.example.iesiback.controllers;

import com.example.iesiback.dto.PagoRequestDTO;
import com.example.iesiback.dto.ProductoDTO;
import com.example.iesiback.dto.ResumenOperadorDTO;
import com.example.iesiback.dto.ResumenRecaudacionDTO;
import com.example.iesiback.entities.User;
import com.example.iesiback.enums.EstadoPago;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.entities.PagoDetalle;
import com.example.iesiback.entities.Tramite;
import com.example.iesiback.services.PagoDetalleService;
import com.example.iesiback.services.PagoService;
import com.example.iesiback.services.TramiteService;
import com.example.iesiback.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final TramiteService tramiteService;
    private final PagoDetalleService pagoDetalleService;
    private final UserService userService;

    public PagoController(
            PagoService pagoService,
            TramiteService tramiteService,
            PagoDetalleService pagoDetalleService, UserService userService
    ) {
        this.pagoService = pagoService;
        this.tramiteService = tramiteService;
        this.pagoDetalleService = pagoDetalleService;
        this.userService = userService;
    }

    // =====================================================
    // RESUMEN OPERADORES
    // =====================================================

    @GetMapping("/ResumenOperadorDTO/{fecha}")
    public ResponseEntity<List<ResumenOperadorDTO>> getResumen(
            @PathVariable LocalDate fecha
    ) {
        User user= userService.getAuthenticatedUser().get();
        return ResponseEntity.ok(
                pagoService.obtenerResumenPorOperador(fecha,user)
        );
    }

    // =====================================================
    //______________VALIDAR PAGO INDIVIDUAL________________
    // =====================================================

    @PatchMapping("/{id}/validar")
    public ResponseEntity<?> validarPago(
            @PathVariable Long id
    ) {

        pagoService.actualizarEstadoValidacion(id);

        return ResponseEntity.ok(
                "Pago validado correctamente"
        );
    }

    // =====================================================
    // VALIDAR PAGOS POR TRÁMITE
    // =====================================================

    @PatchMapping("/tramite/{tramiteId}/validar")
    public ResponseEntity<?> validarPagosPorTramite(
            @PathVariable Integer tramiteId
    ) {
        pagoService.actualizarEstadoValidacionPorTramite(
                tramiteId
        );
        return ResponseEntity.ok(
                "Pagos validados correctamente"
        );
    }

    // =====================================================
    // CAMBIAR ESTADO MANUAL
    // =====================================================

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPago estado
    ) {

        pagoService.cambiarEstadoPago(
                id.intValue(),
                estado
        );

        return ResponseEntity.ok(
                "Estado actualizado"
        );
    }

    // =====================================================
    // CREAR PREFERENCIA MP
    // =====================================================

    @PostMapping("/crear-preferencia")
    public ResponseEntity<?> crearPreferencia(
            @RequestBody ProductoDTO producto
    ) {

        try {

            Map<String, String> datos =
                    pagoService.crearPreferencia(producto);

            return ResponseEntity.ok(datos);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body("Error: " + e.getMessage());
        }
    }

    // =====================================================
    // GUARDAR PAGO ASOCIADO A TRÁMITE
    // =====================================================

    @PostMapping("/atencion/{atencionId}")
    public ResponseEntity<Pago> guardarPagoAT(
            @RequestBody Pago pago,
            @PathVariable Integer atencionId
    ) {

        Tramite atencion =
                tramiteService.findById(atencionId).get();

        pago.setTramite(atencion);

        pago.setResponsable(
                atencion.getTramiteUsuario()
        );

        Pago pagoGuardado =
                pagoService.guardar(pago);

        return ResponseEntity.ok(pagoGuardado);
    }

    // =====================================================
    // GUARDAR PAGO
    // =====================================================

    @PostMapping
    public ResponseEntity<Pago> guardar(
            @RequestBody Pago pago
    ) {

        Pago pagoGuardado =
                pagoService.guardar(pago);

        return ResponseEntity.ok(pagoGuardado);
    }

    // =====================================================
    // BUSCAR POR ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<Pago> buscarPorId(
            @PathVariable Integer id
    ) {

        Optional<Pago> pagoOpt =
                pagoService.buscarPorId(id);

        return pagoOpt.map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // =====================================================
    // BUSCAR POR ATENCIÓN
    // =====================================================

    @GetMapping("/atencion/{id}")
    public ResponseEntity<List<Pago>> buscarPorAtencionId(
            @PathVariable Integer id
    ) {

        List<Pago> pagos =
                pagoService.findByAtencionId(id);

        return ResponseEntity.ok(pagos);
    }

    // =====================================================
    // LISTAR TODOS
    // =====================================================

    @GetMapping
    public ResponseEntity<List<Pago>> listarTodos() {

        List<Pago> pagos =
                pagoService.listarTodos();

        return ResponseEntity.ok(pagos);
    }

    // =====================================================
    // ELIMINAR
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer id
    ) {

        Optional<Pago> pagoOpt =
                pagoService.buscarPorId(id);

        if (pagoOpt.isPresent()) {

            pagoService.eliminar(id);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // =====================================================
    // WEBHOOK MERCADO PAGO
    // =====================================================

    @PostMapping("/webhook")
    public ResponseEntity<?> recibirNotificacion(
            @RequestBody Map<String, Object> payload
    ) {
        try {
            pagoService.procesarWebhook(payload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR                    )
                    .body("Error al procesar webhook");
        }
    }
    // =====================================================
    // CREAR PAGO INICIAL
    // =====================================================
    @PostMapping("/inicial")
    public ResponseEntity<Pago> crearPagoInicial(
            @RequestBody Pago pago
    ) {

        if (
                pago.getTramite() == null
                        || pago.getTramite().getId() == null
        ) {

            return ResponseEntity.badRequest().build();
        }

        pago.setEstado(EstadoPago.PENDIENTE);

        pago.setCreadoEn(Instant.now());

        Pago guardado =
                pagoService.guardar(pago);

        return ResponseEntity.ok(guardado);
    }

    // =====================================================
    // RESUMEN RECAUDACIÓN
    // =====================================================

    @GetMapping("/ResumenRecaudacionDTO/{fechaPago}")
    public ResponseEntity<ResumenRecaudacionDTO>
    ResumenRecaudacionDTO(
            @PathVariable LocalDate fechaPago
    ) {

        Optional<ResumenRecaudacionDTO> pagoOpt =
                pagoService.ResumenRecaudacionDTO(
                        fechaPago
                );

        return ResponseEntity.ok(
                pagoOpt.orElseGet(
                        ResumenRecaudacionDTO::new
                )
        );
    }

    // =====================================================
    // GUARDAR CON DETALLES
    // =====================================================

    @PostMapping("/con-detalles")
    public ResponseEntity<Pago> guardarConDetalles(
            @RequestBody PagoRequestDTO request
    ) {

        Pago pago = request.getPago();

        List<PagoDetalle> detalles =
                request.getDetalles();

        Pago guardado =
                pagoDetalleService
                        .guardarPagoConDetalles(
                                pago,
                                detalles
                        );

        return ResponseEntity.ok(guardado);
    }

    // =====================================================
    // OBTENER DETALLES
    // =====================================================

    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<PagoDetalle>>
    obtenerDetalles(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(
                pagoDetalleService.obtenerPorPago(id)
        );
    }
}