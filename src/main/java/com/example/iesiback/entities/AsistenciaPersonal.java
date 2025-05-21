package com.example.iesiback.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "asistencia_personal")
public class AsistenciaPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asistencia_seq")
    @SequenceGenerator(name = "asistencia_seq", sequenceName = "asistencia_personal_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;


    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @Column(name = "dni")
    private Long dni;

    @Size(max = 200)
    @Column(name = "observaciones", length = 200)
    private String observaciones;

    @Column(name = "horario_id")
    private Integer horarioId;

    @ColumnDefault("0")
    @Column(name = "estado")
    private Integer estado;

}