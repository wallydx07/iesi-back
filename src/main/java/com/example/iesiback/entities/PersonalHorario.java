package com.example.iesiback.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "personal_horarios")
public class PersonalHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ph_seq")
    @SequenceGenerator(name = "ph_seq", sequenceName = "personal_horarios_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;


    @Size(max = 20)
    @Column(name = "dia", length = 20)
    private String dia;

    @Column(name = "entrada")
    private LocalTime entrada;


    @Column(name = "salida")
    private LocalTime salida;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "razon")
    private MateriaCarrera materiaCarrera;  // ✅ Nombre corregido para que coincida con el mappedBy

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dni")
    private Personal dni;

    @Column(name = "year")
    private Integer year;

}
