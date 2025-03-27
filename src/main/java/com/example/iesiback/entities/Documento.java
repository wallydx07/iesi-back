package com.example.iesiback.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documento_id_seq")
    @SequenceGenerator(name = "documento_id_seq", sequenceName = "documento_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Size(max = 50)
    @NotNull
    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @NotNull
    @Column(name = "ruta", nullable = false, length = Integer.MAX_VALUE)
    private String ruta;

    @NotNull
    @Column(name = "tamanio", nullable = false)
    private Long tamanio;

    @Size(max = 50)
    @NotNull
    @Column(name = "tipo_entidad", nullable = false, length = 50)
    private String tipoEntidad;

    @Size(max = 50)
    @NotNull
    @Column(name = "entidad_id", nullable = false, length = 50)
    private String entidadId;


    @Size(max = 100)
    @Column(name = "tipo_documento", length = 100)
    private String tipoDocumento;

}