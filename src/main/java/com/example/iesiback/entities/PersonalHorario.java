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

    @Column(name = "activo")
    private Boolean activo;

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalTime getEntrada() {
        return entrada;
    }

    public void setEntrada(LocalTime entrada) {
        this.entrada = entrada;
    }

    public LocalTime getSalida() {
        return salida;
    }

    public void setSalida(LocalTime salida) {
        this.salida = salida;
    }

    public MateriaCarrera getMateriaCarrera() {
        return materiaCarrera;
    }

    public void setMateriaCarrera(MateriaCarrera materiaCarrera) {
        this.materiaCarrera = materiaCarrera;
    }

    public Personal getDni() {
        return dni;
    }

    public void setDni(Personal dni) {
        this.dni = dni;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
