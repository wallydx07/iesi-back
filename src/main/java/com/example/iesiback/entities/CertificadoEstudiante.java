package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "certificado_estudiante")
public class CertificadoEstudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // usa la secuencia del SERIAL
    @Column(name = "constancia_id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "tipo", length = 50)
    private String tipo;

    @Size(max = 100)
    @Column(name = "autoridad", length = 100)
    private String autoridad;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Size(max = 20)
    @Column(name = "estado", length = 20)
    private String estado;

    @Size(max = 200)
    @Column(name = "observaciones", length = 200)
    private String observaciones;

    @Size(max = 50)
    @Column(name = "usuario", length = 50)
    private String usuario;

    @Column(name = "validado")
    private Boolean validado;

    @Column(name = "monto")
    private Integer monto;

    @Column(name = "curso")
    private String curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atencion_id")
    @JsonBackReference("atencion-certificados")
    private Atencion atencion;

//    @Size(max = 50)
//    @Column(name = "legajo_id", length = 50)
//    private String legajoId;

    @Column(name = "accion", length = Integer.MAX_VALUE)
    private String accion;

    @Column(name = "entrada", length = Integer.MAX_VALUE)
    private String entrada;

    @Column(name = "salida", length = Integer.MAX_VALUE)
    private String salida;

    @Column(name = "fechas", length = Integer.MAX_VALUE)
    private String fechas;

    @Column(name = "razon", length = Integer.MAX_VALUE)
    private String razon;

}