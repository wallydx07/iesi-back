package com.example.iesiback.services;

import com.example.iesiback.dto.ProductoDTO;
import com.example.iesiback.dto.ResumenOperadorDTO;
import com.example.iesiback.dto.ResumenRecaudacionDTO;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.entities.User;
import com.example.iesiback.enums.EstadoPago;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
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

    Map<String, String> crearPreferencia(ProductoDTO producto,
                                         Integer pagoId,
                                         String urlRetorno,
                                         OffsetDateTime vence,
                                         String emailPagador);

    @SuppressWarnings("unchecked")
    void procesarWebhookPresencial(Map<String, Object> payload) throws Exception;

    Optional<ResumenRecaudacionDTO> ResumenRecaudacionDTO(
            LocalDate fechaPago
    );

    List<ResumenOperadorDTO> obtenerResumenPorOperador(
            LocalDate desde, LocalDate hasta, User user
    );

    void actualizarEstadoValidacion(Long id);

    void actualizarEstadoValidacionPorTramite(Integer tramiteId);

    @Transactional
    void cambiarEstadoPago(Integer pagoId, EstadoPago nuevoEstado);

    List<Pago> findByFechaPagoBetween(Instant desde, Instant hasta);
}
