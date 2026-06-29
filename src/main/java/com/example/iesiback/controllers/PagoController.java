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
import com.example.iesiback.exception.BusinessException;
import com.example.iesiback.services.PagoDetalleService;
import com.example.iesiback.services.PagoService;
import com.example.iesiback.services.TramiteService;
import com.example.iesiback.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(PagoService.class);
    public PagoController(
            PagoService pagoService,
            TramiteService tramiteService,
            PagoDetalleService pagoDetalleService,
            UserService userService
    ) {
        this.pagoService = pagoService;
        this.tramiteService = tramiteService;
        this.pagoDetalleService = pagoDetalleService;
        this.userService = userService;
    }

    // =====================================================
    // INICIAR PAGO (guarda Pago PENDIENTE + crea preferencia MP)
    // ÚNICO punto de entrada para arrancar un pago desde el front
    // =====================================================

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarPago(@RequestBody IniciarPagoRequest request) {
        Tramite tramite = tramiteService.findById(request.tramiteId())
                .orElseThrow(() -> new BusinessException("Trámite no encontrado: " + request.tramiteId()));
        Pago pago = new Pago();
        pago.setTramite(tramite);
        pago.setMontoTotal(request.monto());
        pago.setTipoPago(request.concepto());
        pago.setEstado(EstadoPago.PENDIENTE);
        pago.setResponsable(tramite.getTramiteUsuario());
        Pago pagoGuardado = pagoService.guardar(pago);
        ProductoDTO producto = new ProductoDTO();
        producto.setNombre(request.concepto());
        producto.setDescripcion("Trámite N° " + request.tramiteId());
        producto.setPrecio(request.monto());
        try {
            Map<String, String> datos = pagoService.crearPreferencia(producto, pagoGuardado.getId());
            logger.info("Pago creado con éxito. ID: {}, Monto: {}, Alumno: {}",
                    pago.getId(),
                    pago.getMontoTotal());
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al iniciar el pago: " + e.getMessage());
        }
    }

    public record IniciarPagoRequest(Integer tramiteId, String concepto, java.math.BigDecimal monto) {}

    // =====================================================
    // RESUMEN OPERADORES
    // =====================================================

    @GetMapping("/ResumenOperadorDTO/{fecha}")
    public ResponseEntity<List<ResumenOperadorDTO>> getResumen(@PathVariable LocalDate fecha) {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new BusinessException("Usuario no autenticado"));
        return ResponseEntity.ok(pagoService.obtenerResumenPorOperador(fecha, user));
    }

    // =====================================================
    // VALIDAR PAGO INDIVIDUAL
    // =====================================================

    @PatchMapping("/{id}/validar")
    public ResponseEntity<?> validarPago(@PathVariable Integer id) {
        pagoService.actualizarEstadoValidacion(id.longValue());
        return ResponseEntity.ok("Pago validado correctamente");
    }

    // =====================================================
    // VALIDAR PAGOS POR TRÁMITE
    // =====================================================

    @PatchMapping("/tramite/{tramiteId}/validar")
    public ResponseEntity<?> validarPagosPorTramite(@PathVariable Integer tramiteId) {
        pagoService.actualizarEstadoValidacionPorTramite(tramiteId);
        return ResponseEntity.ok("Pagos validados correctamente");
    }

    // =====================================================
    // CAMBIAR ESTADO MANUAL
    // =====================================================

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @RequestParam EstadoPago estado) {
        pagoService.cambiarEstadoPago(id, estado);
        return ResponseEntity.ok("Estado actualizado");
    }

    // =====================================================
    // BUSCAR POR ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<Pago> buscarPorId(@PathVariable Integer id) {
        return pagoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // =====================================================
    // BUSCAR POR ATENCIÓN
    // =====================================================

    @GetMapping("/atencion/{id}")
    public ResponseEntity<List<Pago>> buscarPorAtencionId(@PathVariable Integer id) {
        return ResponseEntity.ok(pagoService.findByAtencionId(id));
    }

    // =====================================================
    // LISTAR TODOS
    // =====================================================

    @GetMapping
    public ResponseEntity<List<Pago>> listarTodos() {
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    // =====================================================
    // ELIMINAR
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (pagoService.buscarPorId(id).isPresent()) {
            pagoService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // =====================================================
    // WEBHOOK MERCADO PAGO
    // =====================================================

    @PostMapping("/webhook")
    public ResponseEntity<Void> recibirNotificacion(@RequestBody Map<String, Object> payload) {
        try {
            String type = (String) payload.get("type");
            if ("payment".equals(type)) {
                pagoService.procesarWebhook(payload);
            }
        } catch (Exception e) {
            System.err.println("❌ Error procesando webhook MP: " + e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }
    // =====================================================
    // RESUMEN RECAUDACIÓN
    // =====================================================

    @GetMapping("/ResumenRecaudacionDTO/{fechaPago}")
    public ResponseEntity<ResumenRecaudacionDTO> resumenRecaudacionDTO(@PathVariable LocalDate fechaPago) {
        return ResponseEntity.ok(
                pagoService.ResumenRecaudacionDTO(fechaPago).orElseGet(ResumenRecaudacionDTO::new)
        );
    }

    // =====================================================
    // GUARDAR CON DETALLES
    // =====================================================

    @PostMapping("/con-detalles")
    public ResponseEntity<Pago> guardarConDetalles(@RequestBody PagoRequestDTO request) {
        Pago guardado = pagoDetalleService.guardarPagoConDetalles(request.getPago(), request.getDetalles());
        return ResponseEntity.ok(guardado);
    }

    // =====================================================
    // OBTENER DETALLES
    // =====================================================

    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<PagoDetalle>> obtenerDetalles(@PathVariable Integer id) {
        return ResponseEntity.ok(pagoDetalleService.obtenerPorPago(id));
    }

    @PostMapping
    public ResponseEntity<Pago> guardar(
            @RequestBody Pago pago
    ) {

        Pago pagoGuardado =
                pagoService.guardar(pago);

        return ResponseEntity.ok(pagoGuardado);
    }
}