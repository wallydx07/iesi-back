package com.example.iesiback.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "pago_detalle")
public class PagoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 🔗 Relación con pago
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id", nullable = false)
    @JsonBackReference
    private Pago pago;

    @NotNull
    @Column(name = "concepto")
    private String concepto;

    @NotNull
    @Column(name = "monto", precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "cantidad")
    private Integer cantidad = 1;
}