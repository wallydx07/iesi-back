package com.example.iesiback.dto;

import com.example.iesiback.enums.ResultadoConciliacion;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ConciliacionItemDTO {
    private Long pagoId;              // null si es SOLO_MP
    private String mpPaymentId;
    private String externalReference;
    private String alumno;            // "Apellido, Nombre" si hay pago local
    private String estadoLocal;       // EstadoPago local
    private String estadoMp;          // status crudo de MP: approved, refunded...
    private String estadoMpMapeado;   // traducido a EstadoPago
    private BigDecimal montoLocal;
    private BigDecimal montoMp;
    private String fechaMp;
    private String metodoMp;          // payment_method_id de MP
    private ResultadoConciliacion resultado;
    private String detalle;           // explicación legible
}