package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "documento_emitido_pdf")
public class DocumentoEmitidoPdf {

    @Id
    @Column(name = "documento_id")
    private Long id;   // mismo valor que el ID de DocumentoEmitido

    @JsonIgnore
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "documento_id")
    private DocumentoEmitido documento;

    @JsonIgnore
    @Column(name = "contenido", nullable = false, columnDefinition = "bytea")
    private byte[] contenido;
}