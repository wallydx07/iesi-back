package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "informe_asistencia_alumnos")
public class InformeAsistenciaAlumno {
    @Id
    @ColumnDefault("nextval('informe_asistencia_alumnos_id_informe_seq')")
    @Column(name = "id_informe", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_carrera_id")
    @JsonManagedReference
    private MateriaCarrera materiaCarrera;

    @Size(max = 50)
    @Column(name = "fecha", length = 50)
    private String fecha;

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