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


}