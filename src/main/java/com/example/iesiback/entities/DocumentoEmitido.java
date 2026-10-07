package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "documento_emitido", schema = "public")
@Getter @Setter
public class DocumentoEmitido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID token;

    // en DocumentoEmitido: borrá el campo tramite y dejá este
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "certificado_id", nullable = false)
    private CertificadoEstudiante certificado;

    @Column(nullable = false, length = 100)
    private String titularNombre;

    @Column(nullable = false, length = 20)
    private String titularDni;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(length = 500)
    private String detalle;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    private LocalDate fechaVencimiento;

    @Column(nullable = false, length = 64, updatable = false)
    private String hashSha256;

    @Column(nullable = false)
    private boolean anulada = false;
    private LocalDateTime fechaAnulacion;
    @Column(length = 500)
    private String motivoAnulacion;
}