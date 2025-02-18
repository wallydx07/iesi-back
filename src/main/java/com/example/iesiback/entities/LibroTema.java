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
@Table(name = "libro_tema")
public class LibroTema {
    @Id
    @ColumnDefault("nextval('libro_tema_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_carrera_id")
    @JsonManagedReference
    private MateriaCarrera materiaCarrera;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "clase", length = Integer.MAX_VALUE)
    private String clase;

    @Column(name = "unidad", length = Integer.MAX_VALUE)
    private String unidad;

    @Size(max = 50)
    @Column(name = "caracter", length = 50)
    private String caracter;

    @Column(name = "tema", length = Integer.MAX_VALUE)
    private String tema;

    @Column(name = "actividades", length = Integer.MAX_VALUE)
    private String actividades;

    @Column(name = "observaciones", length = Integer.MAX_VALUE)
    private String observaciones;

}