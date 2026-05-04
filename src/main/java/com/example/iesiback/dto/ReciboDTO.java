package com.example.iesiback.dto;

import com.example.iesiback.entities.PagoDetalle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReciboDTO {
    private Long aporteId;
    private String aporteNroRecibo;

    // Datos del Alumno
    private String alumnoApellido;
    private String alumnoNombre;
    private String alumnoDni;

    // Datos del Movimiento
    private String concepto;
    private BigDecimal aporteMonto;
    private String metodo; // "Efectivo", "Transferencia", etc.
    private LocalDate aporteFecha;
    private String hora;

    private List<PagoDetalle> pagoDetalles = new ArrayList<>();

    // Auditoría y Estado
    private String usuario; // Campo vital para el agrupamiento por operador
    private Boolean validado;
}