package com.example.iesiback.entities;

import com.beust.jcommander.internal.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "pago")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pago_id_gen")
    @SequenceGenerator(name = "pago_id_gen", sequenceName = "pagos_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "atencion_id", nullable = false)
    private Tramite atencion;

    @Nullable
    @Column(name = "mp_payment_id", nullable = false)
    private Long mpPaymentId;

    @Size(max = 100)
    @Column(name = "preference_id", length = 100)
    private String preferenceId;

    @Size(max = 100)
    @Column(name = "external_reference", length = 100)
    private String externalReference;

    @Size(max = 50)
    @NotNull
    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

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
    @ColumnDefault("'ARS'")
    @Column(name = "moneda", length = 10)
    private String moneda;

    @Column(name = "fecha_pago")
    private Instant fechaPago;

    @Column(name = "raw_response")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> rawResponse;

    @ColumnDefault("now()")
    @Column(name = "creado_en")
    private Instant creadoEn;

    @Size(max = 50)
    @Column(name = "responsable", length = 50)
    private String responsable;

}