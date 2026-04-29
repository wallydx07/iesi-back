package com.example.iesiback.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReciboDTO {

    private Long aporteId;
    private String aporteNroRecibo;

    private String alumnoApellido;
    private String alumnoNombre;
    private String alumnoDni;

    private String concepto;
    private BigDecimal aporteMonto;

    private Boolean validado;
}