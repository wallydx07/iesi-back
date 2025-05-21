package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "informe_asistencia_alumnos")
public class InformeAsistenciaAlumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_informe")
    private Integer idInforme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_carrera_id")
    private MateriaCarrera materiaCarrera;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Size(max = 50)
    @Column(name = "horario", length = 50)
    private String horario;

    @Size(max = 50)
    @Column(name = "modalidad", length = 50)
    private String modalidad;

    @Column(name = "tema", length = Integer.MAX_VALUE)
    private String tema;

    @Size(max = 50)
    @Column(name = "puntual", length = 50)
    private String puntual;

    @Column(name = "reclamos", length = Integer.MAX_VALUE)
    private String reclamos;

    @Column(name = "particular", length = Integer.MAX_VALUE)
    private String particular;

    @Column(name = "observaciones", length = Integer.MAX_VALUE)
    private String observaciones;

}