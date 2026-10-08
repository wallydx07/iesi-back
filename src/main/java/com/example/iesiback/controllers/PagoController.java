package com.example.iesiback.controllers;

import com.example.iesiback.dto.PagoRequestDTO;
import com.example.iesiback.dto.ProductoDTO;
import com.example.iesiback.dto.ResumenOperadorDTO;
import com.example.iesiback.dto.ResumenRecaudacionDTO;
import com.example.iesiback.entities.ConstanciaPrecio;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.entities.PagoDetalle;
import com.example.iesiback.entities.Tramite;
import com.example.iesiback.entities.User;
import com.example.iesiback.enums.EstadoPago;
import com.example.iesiback.exception.BusinessException;
import com.example.iesiback.repositories.ConstanciaPrecioRepository;
import com.example.iesiback.services.MercadoPagoWebhookService;
import com.example.iesiback.services.PagoDetalleService;
import com.example.iesiback.services.PagoService;
import com.example.iesiback.services.TramiteService;
import com.example.iesiback.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/*
 * CAMBIOS RESPECTO DE LA VERSIÓN ANTERIOR
 *
 * 1. /webhook: antes llamaba a procesarWebhookPresencial (que espera notificaciones "order.*"
 *    de QR/Point) con notificaciones "payment" de Checkout Pro, así que nunca actualizaba nada.
 *    Ahora usa MercadoPagoWebhookService, acepta el formato webhook y el IPN (?topic=&id=),
 *    y devuelve 500 si falla para que Mercado Pago reintente.
 *    El webhook presencial sigue en PagoPresencialController (/api/pagos/presencial/webhook).
 *
 * 2. /iniciar: el monto ya no viene del frontend. Se recibe conceptoId y el importe sale de
 *    constancia_precios.precio_mp. El frontend que llame a este endpoint debe enviar
 *    { tramiteId, conceptoId } en lugar de { tramiteId, concepto, monto }.
 *
 * 3. Logger con la clase correcta (antes usaba PagoService.class).
 *
 * El resto quedó igual. La protección por roles de estos endpoints se define en SecurityConfig.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    public static final Logger logger = LoggerFactory.getLogger(PagoController.class);

    private final PagoService pagoService;
    private final TramiteService tramiteService;
    private final PagoDetalleService pagoDetalleService;
    private final UserService userService;
    private final MercadoPagoWebhookService mercadoPagoWebhookService;
    private final ConstanciaPrecioRepository constanciaPrecioRepository;

    public PagoController(
            PagoService pagoService,
            TramiteService tramiteService,
            PagoDetalleService pagoDetalleService,
            UserService userService,
            MercadoPagoWebhookService mercadoPagoWebhookService,
            ConstanciaPrecioRepository constanciaPrecioRepository
    ) {
        this.pagoService = pagoService;
        this.tramiteService = tramiteService;
        this.pagoDetalleService = pagoDetalleService;
        this.userService = userService;
        this.mercadoPagoWebhookService = mercadoPagoWebhookService;
        this.constanciaPrecioRepository = constanciaPrecioRepository;
    }

    // =====================================================
    // INICIAR PAGO CHECKOUT PRO (usuarios autenticados)
    // El importe lo decide el backend a partir del concepto.
    // La preinscripción pública usa /api/preinscripcion/publica/{codigo}/pago.
    // =====================================================

    public record IniciarPagoRequest(Integer tramiteId, Integer conceptoId) {}

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarPago(@RequestBody IniciarPagoRequest request) {
        if (request == null || request.tramiteId() == null || request.conceptoId() == null) {
            return ResponseEntity.badRequest().body("Faltan tramiteId o conceptoId");
        }

        Tramite tramite = tramiteService.findById(request.tramiteId())
                .orElseThrow(() -> new BusinessException("Trámite no encontrado: " + request.tramiteId()));

        ConstanciaPrecio concepto = constanciaPrecioRepository.findById(Long.valueOf(request.conceptoId()))
                .orElseThrow(() -> new BusinessException("Concepto no encontrado: " + request.conceptoId()));

        if (concepto.getPrecioMp() == null || concepto.getPrecioMp().signum() <= 0) {
            return ResponseEntity.unprocessableEntity().body("El concepto no tiene precio para Mercado Pago");
        }

        boolean yaPagado = pagoService.findByAtencionId(tramite.getId()).stream()
                .anyMatch(p -> p.getEstado() == EstadoPago.APROBADO
                        && concepto.getNombre().equals(p.getTipoPago()));
        if (yaPagado) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ese concepto ya está pagado en el trámite");
        }

        Pago pago = new Pago();
        pago.setTramite(tramite);
        pago.setMontoTotal(concepto.getPrecioMp());
        pago.setTipoPago(concepto.getNombre());
        pago.setEstado(EstadoPago.PENDIENTE);
        pago.setResponsable("Alumno");
        pago.setMetodoPago("checkout_pro");
        Pago pagoGuardado = pagoService.guardar(pago);

        ProductoDTO producto = new ProductoDTO();
        producto.setNombre(concepto.getNombre());
        producto.setDescripcion("Trámite N° " + tramite.getId());
        producto.setPrecio(concepto.getPrecioMp());

        try {
            Map<String, String> datos = pagoService.crearPreferencia(producto, pagoGuardado.getId(), tramite.getId());
            logger.info("Pago {} iniciado por {} (trámite {})",
                    pagoGuardado.getId(), pagoGuardado.getMontoTotal(), tramite.getId());
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            logger.error("Error al iniciar el pago del trámite {}", tramite.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al iniciar el pago");
        }
    }

    // =====================================================
    // WEBHOOK MERCADO PAGO (Checkout Pro)
    // Público: Mercado Pago lo llama sin token (permitAll en SecurityConfig).
    // =====================================================

    @PostMapping("/webhook")
    public ResponseEntity<Void> recibirNotificacion(
            @RequestBody(required = false) Map<String, Object> payload,
            @RequestParam Map<String, String> params) {

        String tipo = primeroNoVacio(
                texto(payload, "type"),     // webhook: {"type":"payment", ...}
                params.get("type"),         // webhook: ?type=payment
                params.get("topic"),        // IPN:     ?topic=payment
                texto(payload, "topic"));   // IPN:     {"topic":"payment", ...}

        // merchant_order y otros tópicos no se procesan: el estado real llega por "payment"
        if (!"payment".equals(tipo)) {
            return ResponseEntity.ok().build();
        }

        String idTexto = primeroNoVacio(
                idDeData(payload),           // webhook: {"data":{"id":"123"}}
                params.get("data.id"),       // webhook: ?data.id=123
                params.get("id"),            // IPN:     ?id=123
                texto(payload, "resource")); // IPN:     {"resource":"123"}

        if (idTexto == null || !idTexto.matches("\\d{1,19}")) {
            logger.warn("Webhook MP sin paymentId válido. params={} payload={}", params, payload);
            return ResponseEntity.ok().build();   // no tiene sentido que MP lo reintente
        }

        try {
            mercadoPagoWebhookService.procesarPayment(Long.valueOf(idTexto));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error procesando webhook MP (paymentId={}): {}", idTexto, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();   // MP reintenta
        }
    }

    private static String texto(Map<String, Object> mapa, String clave) {
        if (mapa == null || mapa.get(clave) == null) return null;
        return mapa.get(clave).toString();
    }

    private static String idDeData(Map<String, Object> payload) {
        if (payload != null && payload.get("data") instanceof Map<?, ?> data && data.get("id") != null) {
            return data.get("id").toString();
        }
        return null;
    }

    private static String primeroNoVacio(String... valores) {
        for (String v : valores) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    // =====================================================
    // RESÚMENES
    // =====================================================

    @GetMapping("/resumen-operador")
    public ResponseEntity<List<ResumenOperadorDTO>> getResumenOperador(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new BusinessException("Usuario no autenticado"));
        if (desde.isAfter(hasta)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(pagoService.obtenerResumenPorOperador(desde, hasta, user));
    }

    @GetMapping("/ResumenRecaudacionDTO/{fechaPago}")
    public ResponseEntity<ResumenRecaudacionDTO> resumenRecaudacionDTO(@PathVariable LocalDate fechaPago) {
        return ResponseEntity.ok(
                pagoService.ResumenRecaudacionDTO(fechaPago).orElseGet(ResumenRecaudacionDTO::new)
        );
    }

    // =====================================================
    // VALIDACIÓN Y ESTADO MANUAL (administrativos)
    // =====================================================

    @PatchMapping("/{id}/validar")
    public ResponseEntity<?> validarPago(@PathVariable Integer id) {
        pagoService.actualizarEstadoValidacion(id.longValue());
        return ResponseEntity.ok("Pago validado correctamente");
    }

    @PatchMapping("/tramite/{tramiteId}/validar")
    public ResponseEntity<?> validarPagosPorTramite(@PathVariable Integer tramiteId) {
        pagoService.actualizarEstadoValidacionPorTramite(tramiteId);
        return ResponseEntity.ok("Pagos validados correctamente");
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @RequestParam EstadoPago estado) {
        pagoService.cambiarEstadoPago(id, estado);
        return ResponseEntity.ok("Estado actualizado");
    }

    // =====================================================
    // CONSULTAS
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<Pago> buscarPorId(@PathVariable Integer id) {
        return pagoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/atencion/{id}")
    public ResponseEntity<List<Pago>> buscarPorAtencionId(@PathVariable Integer id) {
        return ResponseEntity.ok(pagoService.findByAtencionId(id));
    }

    @GetMapping
    public ResponseEntity<List<Pago>> listarTodos() {
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<PagoDetalle>> obtenerDetalles(@PathVariable Integer id) {
        return ResponseEntity.ok(pagoDetalleService.obtenerPorPago(id));
    }

    // =====================================================
    // ALTA Y BAJA (administrativos)
    // =====================================================

    @PostMapping("/con-detalles")
    public ResponseEntity<Pago> guardarConDetalles(@RequestBody PagoRequestDTO request) {
        Pago guardado = pagoDetalleService.guardarPagoConDetalles(request.getPago(), request.getDetalles());
        return ResponseEntity.ok(guardado);
    }

    @PostMapping
    public ResponseEntity<Pago> guardar(@RequestBody Pago pago) {
        Pago pagoGuardado = pagoService.guardar(pago);
        return ResponseEntity.ok(pagoGuardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (pagoService.buscarPorId(id).isPresent()) {
            pagoService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}