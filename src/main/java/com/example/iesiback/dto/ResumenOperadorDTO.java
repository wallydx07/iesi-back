package com.example.iesiback.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ResumenOperadorDTO {
    private String usuario;
    private BigDecimal total;
    private long validados;
    private long pendientes;

    private List<ReciboDTO> items;

    public ResumenOperadorDTO(String usuario, List<ReciboDTO> items) {
        this.usuario = usuario;
        this.items = items;
        this.total = items.stream()
                .map(ReciboDTO::getAporteMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.validados = items.stream().filter(ReciboDTO::getValidado).count();
        this.pendientes = items.size() - validados;
    }
}