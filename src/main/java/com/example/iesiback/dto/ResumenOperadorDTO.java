package com.example.iesiback.dto;

import com.example.iesiback.enums.EstadoPago;
import lombok.Data;
import lombok.NoArgsConstructor; // 1. Importamos
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor // 2. Obligatorio para que Spring/Jackson puedan serializar la lista
public class ResumenOperadorDTO {
    private String usuario;
    private BigDecimal total;
    private long validados;
    private long pendientes;
    private List<ReciboDTO> items;

    // Tu constructor manual se mantiene impecable para cuando lo usas en el Service
    public ResumenOperadorDTO(String usuario, List<ReciboDTO> items) {
        this.usuario = usuario;
        this.items = items;
        this.total = items != null ? items.stream()
                .map(ReciboDTO::getAporteMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;

        this.validados = items != null ? items.stream()
                .filter(r -> r.getEstado() == EstadoPago.APROBADO)
                .count() : 0;

        this.pendientes = items != null ? items.size() - this.validados : 0;
    }
}