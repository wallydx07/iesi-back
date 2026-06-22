package com.example.iesiback.entities;

import com.example.iesiback.enums.EstadoAsistencia;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "asistencia_alumno")
public class AsistenciaAlumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia_alumnos", nullable = false)
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_informe")
    private InformeAsistenciaAlumno idInforme;

    @Size(max = 50)
    @Column(name = "legajo_id", length = 50)
    private String legajoId;

    @Column(name = "estado")
    private Boolean estado;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "estado", length = 20)
//    private EstadoAsistencia estado;

}