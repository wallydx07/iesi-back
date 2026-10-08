package com.example.iesiback.controllers;

import com.example.iesiback.dto.PreinscripcionDtos.*;
import com.example.iesiback.services.PreinscripcionPublicaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Endpoints PÚBLICOS (permitAll en SecurityConfig) de la preinscripción.
 * Sin @CrossOrigin: el front público se sirve desde el mismo dominio vía Apache.
 *
 * GET  /api/preinscripcion/publica/ofertas
 * POST /api/preinscripcion/publica
 * POST /api/preinscripcion/publica/{codigo}/pago
 * GET  /api/preinscripcion/publica/{codigo}/estado
 */
@RestController
@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RequestMapping("/api/preinscripcion/publica")
public class PreinscripcionPublicaController {

    private static final Logger log = LoggerFactory.getLogger(PreinscripcionPublicaController.class);
    private static final Pattern CODIGO = Pattern.compile("[A-Z2-9]{16}");

    private final PreinscripcionPublicaService service;

    public PreinscripcionPublicaController(PreinscripcionPublicaService service) {
        this.service = service;
    }

    @GetMapping("/ofertas")
    public List<OfertaPublicaDTO> ofertas() {
        return service.listarOfertas();
    }

    @PostMapping
    public ResponseEntity<PreinscripcionCreadaResponse> crear(@Valid @RequestBody PreinscripcionPublicaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    /** Sin body: el monto y el concepto los determina el backend a partir del trámite. */
    @PostMapping("/{codigo}/pago")
    public IniciarPagoResponse iniciarPago(@PathVariable String codigo) {
        validarCodigo(codigo);
        return service.iniciarPago(codigo);
    }

    @GetMapping("/{codigo}/estado")
    public PreinscripcionEstadoResponse estado(@PathVariable String codigo) {
        validarCodigo(codigo);
        return service.estado(codigo);
    }

    private static void validarCodigo(String codigo) {
        if (codigo == null || !CODIGO.matcher(codigo).matches()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trámite no encontrado.");
        }
    }

    // ---------------------------------------------------------------------
    // Errores: siempre { "mensaje": "..." }, sin stack traces ni detalles internos
    // ---------------------------------------------------------------------

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> manejarEstado(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode())
                .body(Map.of("mensaje", e.getReason() != null ? e.getReason() : "No se pudo procesar la solicitud."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> campos.putIfAbsent(f.getField(), f.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", "Revisá los datos ingresados.", "campos", campos));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarJsonInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of("mensaje", "Solicitud inválida."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarInesperado(Exception e) {
        log.error("Error en preinscripción pública", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "No pudimos procesar la solicitud. Intentá nuevamente en unos minutos."));
    }
}