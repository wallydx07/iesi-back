package com.example.iesiback.services;

import com.example.iesiback.dto.ProductoDTO;
import com.example.iesiback.dto.ResumenOperadorDTO;
import com.example.iesiback.dto.ResumenRecaudacionDTO;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.entities.User;
import com.example.iesiback.enums.EstadoPago;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PagoService {
    Pago guardar(Pago pago);

    Optional<Pago> buscarPorId(Integer id);

    List<Pago> listarTodos();

    void eliminar(Integer id);

    List<Pago> findByAtencionId(Integer id);

    Optional<Pago> findByOrderId(String id);

    Map<String, String> crearPreferencia(ProductoDTO producto, Integer pagoId, Integer tramiteId);

    void procesarWebhook(Map<String, Object> payload) throws Exception;

    void procesarWebhookPresencial(Map<String, Object> payload) throws Exception;

    Optional<ResumenRecaudacionDTO> ResumenRecaudacionDTO(
            LocalDate fechaPago
    );

    List<ResumenOperadorDTO> obtenerResumenPorOperador(
  LocalDate desde, LocalDate hasta, User user
    );


    /**
     * ✅ Validar un pago individual
     */
    void actualizarEstadoValidacion(Long id);

    /**
     * ✅ Validar todos los pagos de un trámite
     */
    void actualizarEstadoValidacionPorTramite(
            Integer tramiteId
    );

    void cambiarEstadoPago(
            Integer pagoId,
            EstadoPago nuevoEstado
    );

    List<Pago> findByFechaPagoBetween(
            Instant desde, Instant hasta
    );
}
