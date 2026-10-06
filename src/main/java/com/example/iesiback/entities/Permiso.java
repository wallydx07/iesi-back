package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "permiso")
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Hibernate delega la generación del ID a PostgreSQL
    @Column(name = "permiso_id", nullable = false)
    private Integer id;

    @Column(name = "permiso_fecha")
    private LocalDate permisoFecha;

    @Size(max = 50)
    @Column(name = "permiso_obs", length = 50)
    private String permisoObs;

    @Size(max = 50)
    @Column(name = "permiso_legajo_id", length = 50)
    private String permisoLegajoId;

    // Trámite que originó este permiso (lado dueño de la relación: tiene la FK tramite_id)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tramite_id", unique = true)
    @JsonIgnoreProperties({"permiso", "pases", "certificados", "pagos",
            "hibernateLazyInitializer", "handler"})
    private Tramite tramite;

    // Quitá el @JsonIgnore si necesitás los exámenes en el JSON del permiso
    // y Examen ya evita el ciclo (por ejemplo con @JsonIgnore/@JsonBackReference en su campo permiso)
    @OneToMany(mappedBy = "permiso")
    @JsonIgnore
    private Set<Examen> examen = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getPermisoFecha() {
        return permisoFecha;
    }

    public void setPermisoFecha(LocalDate permisoFecha) {
        this.permisoFecha = permisoFecha;
    }

    public String getPermisoObs() {
        return permisoObs;
    }

    public void setPermisoObs(String permisoObs) {
        this.permisoObs = permisoObs;
    }

    public String getPermisoLegajoId() {
        return permisoLegajoId;
    }

    public void setPermisoLegajoId(String permisoLegajoId) {
        this.permisoLegajoId = permisoLegajoId;
    }

    public Tramite getTramite() {
        return tramite;
    }

    public void setTramite(Tramite tramite) {
        this.tramite = tramite;
    }

    public Set<Examen> getExamen() {
        return examen;
    }

    public void setExamen(Set<Examen> examen) {
        this.examen = examen;
    }
}