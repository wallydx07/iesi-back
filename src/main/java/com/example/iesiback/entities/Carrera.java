package com.example.iesiback.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "carrera")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // Ignora los proxies de Hibernate

public class Carrera {

    @Id
    @Column(name = "carrera_id")  // Mapea el atributo
    private String carreraId;

    @NotBlank
    @Column(name = "carrera_resolucion")  // Mapea el atributo
    private String carreraResolucion;

    @NotBlank
    @Column(name = "carrera_Nombre")  // Mapea el atributo
    private String carreraNombre;

    @NotBlank
    @Column(name = "carrera_year")  // Mapea el atributo
    private String carreraYear;

    @OneToMany(mappedBy = "carrera")
    @JsonIgnore
    private Set<Inscripcion> inscripcions = new LinkedHashSet<>();

    @OneToMany
    @JsonIgnore
    private Set<com.example.iesiback.entities.MateriaCarrera> materiaCarreras = new LinkedHashSet<>();

    public Set<com.example.iesiback.entities.MateriaCarrera> getMateriaCarreras() {
        return materiaCarreras;
    }

    public void setMateriaCarreras(Set<com.example.iesiback.entities.MateriaCarrera> materiaCarreras) {
        this.materiaCarreras = materiaCarreras;
    }

    public Set<Inscripcion> getInscripcions() {
        return inscripcions;
    }

    public void setInscripcions(Set<Inscripcion> inscripcions) {
        this.inscripcions = inscripcions;
    }


    public String getCarreraId() {
        return carreraId;
    }

    public void setCarreraId(String carreraId) {
        this.carreraId = carreraId;
    }

    public String getCarreraResolucion() {
        return carreraResolucion;
    }

    public void setCarreraResolucion(String carreraResolucion) {
        this.carreraResolucion = carreraResolucion;
    }

    public String getCarreraNombre() {
        return carreraNombre;
    }

    public void setCarreraNombre(String carreraNombre) {
        this.carreraNombre = carreraNombre;
    }

    public String getCarreraYear() {
        return carreraYear;
    }

    public void setCarreraYear(String carreraYear) {
        this.carreraYear = carreraYear;
    }
}
