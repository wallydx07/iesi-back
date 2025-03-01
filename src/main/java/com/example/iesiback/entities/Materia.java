package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "materia")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Materia {
    @Id
    @Size(max = 100)
    @Column(name = "materia_id", nullable = false, length = 100)
    private String materiaId;

    @Column(name = "materia_orden")
    private Integer materiaOrden;

    @Size(max = 100)
    @Column(name = "materia_nombre", length = 100)
    private String materiaNombre;

    @Size(max = 50)
    @Column(name = "materia_nivel", length = 50)
    private String materiaNivel;

    @Size(max = 50)
    @Column(name = "materia_modalidad", length = 50)
    private String materiaModalidad;

    @Size(max = 50)
    @Column(name = "materia_regimen", length = 50)
    private String materiaRegimen;

    @Size(max = 50)
    @Column(name = "materia_cursada", length = 50)
    private String materiaCursada;

    @Size(max = 50)
    @Column(name = "materia_examen", length = 50)
    private String materiaExamen;

    @Column(name = "catedras")
    private Integer catedras;

    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<MateriaCarrera> materiaCarreras = new LinkedHashSet<>();

    public Integer getCatedras() {
        return catedras;
    }

    public void setCatedras(Integer catedras) {
        this.catedras = catedras;
    }

    public Set<MateriaCarrera> getMateriaCarreras() {
        return materiaCarreras;
    }

    public void setMateriaCarreras(Set<MateriaCarrera> materiaCarreras) {
        this.materiaCarreras = materiaCarreras;
    }

    public String getMateriaCursada() {
        return materiaCursada;
    }

    public void setMateriaCursada(String materiaCursada) {
        this.materiaCursada = materiaCursada;
    }

    public String getMateriaExamen() {
        return materiaExamen;
    }

    public void setMateriaExamen(String materiaExamen) {
        this.materiaExamen = materiaExamen;
    }

    public String getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(String materiaId) {
        this.materiaId = materiaId;
    }

    public String getMateriaModalidad() {
        return materiaModalidad;
    }

    public void setMateriaModalidad(String materiaModalidad) {
        this.materiaModalidad = materiaModalidad;
    }

    public String getMateriaNivel() {
        return materiaNivel;
    }

    public void setMateriaNivel(String materiaNivel) {
        this.materiaNivel = materiaNivel;
    }

    public String getMateriaNombre() {
        return materiaNombre;
    }

    public void setMateriaNombre(String materiaNombre) {
        this.materiaNombre = materiaNombre;
    }

    public Integer getMateriaOrden() {
        return materiaOrden;
    }

    public void setMateriaOrden(Integer materiaOrden) {
        this.materiaOrden = materiaOrden;
    }

    public String getMateriaRegimen() {
        return materiaRegimen;
    }

    public void setMateriaRegimen(String materiaRegimen) {
        this.materiaRegimen = materiaRegimen;
    }
}