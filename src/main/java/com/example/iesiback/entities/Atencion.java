package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "atencion")
public class Atencion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "atencion_id_gen")
    @SequenceGenerator(name = "atencion_id_gen", sequenceName = "atencion_atencion_id_seq", allocationSize = 1)
    @Column(name = "atencion_id", nullable = false)
    private Integer id;

    @ColumnDefault("false")
    @Column(name = "atencion_resuelto")
    private Boolean atencionResuelto;

    @Column(name = "atencion_dni")
    private Long atencionDni;

    @Size(max = 100)
    @Column(name = "atencion_apellido_nombre", length = 100)
    private String atencionApellidoNombre;

    @Size(max = 100)
    @Column(name = "atencion_correo", length = 100)
    private String atencionCorreo;

    @Column(name = "atencion_celular")
    private Long atencionCelular;

    @Column(name = "atencion_consulta", length = Integer.MAX_VALUE)
    private String atencionConsulta;

    @Column(name = "atencion_problema", length = Integer.MAX_VALUE)
    private String atencionProblema;

    @ColumnDefault("CURRENT_DATE")
    @Column(name = "atencion_fecha")
    private LocalDate atencionFecha;

    @Column(name = "atencion_respuesta", length = Integer.MAX_VALUE)
    private String atencionRespuesta;

    @Column(name = "atencion_observaciones", length = Integer.MAX_VALUE)
    private String atencionObservaciones;

    @Size(max = 50)
    @Column(name = "atencion_destino", length = 50)
    private String atencionDestino;

    @Size(max = 50)
    @Column(name = "atencion_usuario", length = 50)
    private String atencionUsuario;

    @OneToMany(mappedBy = "atencion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("atencion-certificados")
    private List<CertificadoEstudiante> certificados;




}