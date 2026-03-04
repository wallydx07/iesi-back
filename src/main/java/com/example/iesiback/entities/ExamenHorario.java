package com.example.iesiback.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "examen_horarios")
public class ExamenHorario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "examen_horarios_id_gen")
    @SequenceGenerator(name = "examen_horarios_id_gen", sequenceName = "examen_horarios_examen_horario_id_seq", allocationSize = 1)
    @Column(name = "examen_horario_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull
    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @Size(max = 50)
    @NotNull
    @Column(name = "materia_id", nullable = false, length = 50)
    private String materiaId;

    @Column(name = "titular_dni", nullable = false)
    private Long TitularDni;

    @Column(name = "vocal1_dni", nullable = false)
    private Long vocal1Dni;

    @Column(name = "vocal2_dni", nullable = false)
    private Long vocal2Dni;

}