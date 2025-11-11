package com.example.iesiback.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Actuaciones")
public class Actuacione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private Instant fecha;

    @NotNull
    @Column(name = "fechaRecepcion", nullable = false)
    private Instant fechaRecepcion;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "dirigidoA", nullable = false, length = 50)
    private String dirigidoA;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "firmadoPor", nullable = false, length = 50)
    private String firmadoPor;

    @Size(max = 500)
    @NotNull
    @Nationalized
    @Column(name = "asunto", nullable = false, length = 500)
    private String asunto;

    @Column(name = "numero")
    private Integer numero;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Size(max = 50)
    @Nationalized
    @Column(name = "numeroExterno", length = 50)
    private String numeroExterno;

    @Column(name = "fojas")
    private Integer fojas;

    @Size(max = 150)
    @Nationalized
    @Column(name = "contacto", length = 150)
    private String contacto;

    @Column(name = "idAreaActual")
    private Integer idAreaActual;

}