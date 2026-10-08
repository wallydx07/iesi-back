package com.example.iesiback.services;

import com.example.iesiback.repositories.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Punto único que decide qué hacer cuando se aprueba un pago, según el tipo de trámite.
 * Lo usan el webhook de Checkout Pro, el listener de pagos internos y el reintento manual.
 *
 * No depende de PagoService: así no se forman dependencias circulares.
 */
@Service
public class AccionesPagoAprobadoService {

    private static final Logger log = LoggerFactory.getLogger(AccionesPagoAprobadoService.class);

    private final PagoRepository pagoRepository;
    private final Map<String, AccionPagoAprobado> accionesPorTipo = new HashMap<>();

    public AccionesPagoAprobadoService(PagoRepository pagoRepository, List<AccionPagoAprobado> acciones) {
        this.pagoRepository = pagoRepository;
        for (AccionPagoAprobado accion : acciones) {
            for (String tipo : accion.tiposDeTramite()) {
                if (accionesPorTipo.putIfAbsent(tipo, accion) != null) {
                    throw new IllegalStateException("Hay dos acciones para el tipo de trámite: " + tipo);
                }
            }
        }
        log.info("Acciones por pago aprobado registradas para: {}", accionesPorTipo.keySet());
    }

    /**
     * Ejecuta la acción del tipo de trámite del pago, si existe.
     * Lanza la excepción si la acción falla: quien llama decide si reintentar o registrar el error.
     */
    public void ejecutar(Integer pagoId) {
        String tipo = pagoRepository.findTipoTramiteByPagoId(pagoId).orElse(null);
        AccionPagoAprobado accion = tipo != null ? accionesPorTipo.get(tipo) : null;

        if (accion == null) {
            log.debug("Pago {} aprobado (trámite '{}'): sin acción posterior", pagoId, tipo);
            return;
        }
        accion.ejecutar(pagoId);
    }
}