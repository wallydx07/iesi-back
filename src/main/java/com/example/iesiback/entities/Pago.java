package com.example.iesiback.entities;

import com.example.iesiback.enums.EstadoPago;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "pago", indexes = {
        @Index(name = "idx_pago_tramite", columnList = "tramite_id"),
        @Index(name = "idx_pago_estado", columnList = "estado"),
        @Index(name = "idx_pago_fecha", columnList = "fecha_pago")
})
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pago_id_gen")
    @SequenceGenerator(name = "pago_id_gen", sequenceName = "pagos_id_seq", allocationSize = 1)
    private Integer id;

    // 🔗 OPCIONAL: muchos pagos → 1 trámite
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "tramite_id", nullable = true)
    @JsonIgnore
    private Tramite tramite;

    @Column(name = "mp_payment_id")
    private Long mpPaymentId;

    @Size(max = 100)
    @Column(name = "preference_id", length = 100)
    private String preferenceId;

    @Size(max = 100)
    @Column(name = "external_reference", length = 100)
    private String externalReference;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 50)
    private EstadoPago estado;

    @Size(max = 100)
    @Column(name = "status_detail", length = 100)
    private String statusDetail;

    @Size(max = 100)
    @Column(name = "metodo_pago", length = 100)
    private String metodoPago;

    @Size(max = 100)
    @Column(name = "tipo_pago", length = 100)
    private String tipoPago;

    @NotNull
    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Size(max = 10)
    @Column(name = "moneda", length = 10)
    private String moneda = "ARS";

    @Column(name = "fecha_pago")
    private Instant fechaPago;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_response")
    private Map<String, Object> rawResponse;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Size(max = 50)//
    @Column(name = "responsable", length = 50)
    private String responsable;

    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PagoDetalle> detalles;

    @PrePersist
    public void prePersist() {
        this.creadoEn = Instant.now();
    }

    @Size(max = 50)
    @Column(name = "order_id", length = 50)
    private String orderId;

    @Size(max = 50)
    @Column(name = "mp_payment_id_str", length = 50)
    private String mpPaymentIdStr;
    //CREATE INDEX idx_pago_order_id ON pago (order_id);
}