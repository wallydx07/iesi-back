package com.example.iesiback.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documento_id_seq")
    @SequenceGenerator(name = "documento_id_seq", sequenceName = "documento_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "nombre", nullable = false, columnDefinition = "TEXT")
    private String nombre;

    @NotNull
    @Column(name = "tipo", nullable = false, columnDefinition = "TEXT")
    private String tipo; // MIME completo (sin límite)

    @NotNull
    @Column(name = "ruta", nullable = false, columnDefinition = "TEXT")
    private String ruta;

    @NotNull
    @Column(name = "tamanio", nullable = false)
    private Long tamanio;

    @NotNull
    @Column(name = "tipo_entidad", nullable = false, columnDefinition = "TEXT")
    private String tipoEntidad;

    @NotNull
    @Column(name = "entidad_id", nullable = false, columnDefinition = "TEXT")
    private String entidadId;

    @Column(name = "tipo_documento", columnDefinition = "TEXT")
    private String tipoDocumento;

}
