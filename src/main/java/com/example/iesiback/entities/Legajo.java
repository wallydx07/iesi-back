package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "legajo")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Legajo {
    @Id
    @Size(max = 50)
    @Column(name = "legajo_id", nullable = false, length = 50)
    private String legajoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legajo_alumno_dni", nullable = false)
    @JsonProperty(value = "persona", access = JsonProperty.Access.WRITE_ONLY)
    private Persona legajoPersonaDni;

    @Size(max = 50)
    @Column(name = "legajo_sede", length = 50)
    private String legajoSede;

    @Size(max = 50)
    @Column(name = "legajo_aval", length = 50)
    private String legajoAval;

    @Size(max = 50)
    @Column(name = "legajo_foto", length = 50)
    private String legajoFoto;

    @Size(max = 50)
    @Column(name = "legajo_fotocopia_dni", length = 50)
    private String legajoFotocopiaDni;

    @Size(max = 50)
    @Column(name = "legajo_certificado_nacimiento", length = 50)
    private String legajoCertificadoNacimiento;

    @Size(max = 50)
    @Column(name = "legajo_fotocopia_titulo", length = 50)
    private String legajoFotocopiaTitulo;

    @Size(max = 50)
    @Column(name = "legajo_planilla_prontuarial", length = 50)
    private String legajoPlanillaProntuarial;

    @Size(max = 50)
    @Column(name = "legajo_carnet_sanitario", length = 50)
    private String legajoCarnetSanitario;

    @Size(max = 50)
    @Column(name = "legajo_carpeta_colgante", length = 50)
    private String legajoCarpetaColgante;

    @Size(max = 50)
    @Column(name = "legajo_estado", length = 50)
    private String legajoEstado;

    @Size(max = 50)
    @Column(name = "legajo_fecha", length = 50)
    private String legajoFecha;

    @Size(max = 50)
    @Column(name = "usuario", length = 50)
    private String usuario;

    @Size(max = 10)
    @Column(name = "libreta", length = 10)
    private String libreta;

    @Size(max = 10)
    @Column(name = "folio", length = 10)
    private String folio;

    @Column(name = "notas_corregidas", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean notasCorregidas;


    // Relaciones corregidas con @JsonManagedReference y mappedBy
    @OneToMany(mappedBy = "aporteLegajo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Aporte> aportes = new LinkedHashSet<>();

//    @OneToMany(mappedBy = "legajo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonManagedReference
//    private Set<Atencion> atenciones = new LinkedHashSet<>();

    @OneToOne(mappedBy = "legajo", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JsonManagedReference
    private Inscripcion inscripcion;


    @OneToMany(mappedBy = "legajo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Observacione> observaciones = new LinkedHashSet<>();

    @OneToMany(mappedBy = "legajo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Cursada> cursadas = new LinkedHashSet<>();


    public String getLegajoId() {
        return legajoId;
    }

    public void setLegajoId(String legajoId) {
        this.legajoId = legajoId;
    }

    public Persona getLegajoPersonaDni() {
        return legajoPersonaDni;
    }

    public void setLegajoPersonaDni(Persona legajoPersonaDni) {
        this.legajoPersonaDni = legajoPersonaDni;
    }

    public String getLegajoSede() {
        return legajoSede;
    }

    public void setLegajoSede(String legajoSede) {
        this.legajoSede = legajoSede;
    }

    public String getLegajoAval() {
        return legajoAval;
    }

    public void setLegajoAval(String legajoAval) {
        this.legajoAval = legajoAval;
    }

    public String getLegajoFoto() {
        return legajoFoto;
    }

    public void setLegajoFoto(String legajoFoto) {
        this.legajoFoto = legajoFoto;
    }

    public String getLegajoFotocopiaDni() {
        return legajoFotocopiaDni;
    }

    public void setLegajoFotocopiaDni(String legajoFotocopiaDni) {
        this.legajoFotocopiaDni = legajoFotocopiaDni;
    }

    public String getLegajoCertificadoNacimiento() {
        return legajoCertificadoNacimiento;
    }

    public void setLegajoCertificadoNacimiento(String legajoCertificadoNacimiento) {
        this.legajoCertificadoNacimiento = legajoCertificadoNacimiento;
    }

    public String getLegajoFotocopiaTitulo() {
        return legajoFotocopiaTitulo;
    }

    public void setLegajoFotocopiaTitulo(String legajoFotocopiaTitulo) {
        this.legajoFotocopiaTitulo = legajoFotocopiaTitulo;
    }

    public String getLegajoPlanillaProntuarial() {
        return legajoPlanillaProntuarial;
    }

    public void setLegajoPlanillaProntuarial(String legajoPlanillaProntuarial) {
        this.legajoPlanillaProntuarial = legajoPlanillaProntuarial;
    }

    public String getLegajoCarnetSanitario() {
        return legajoCarnetSanitario;
    }

    public void setLegajoCarnetSanitario(String legajoCarnetSanitario) {
        this.legajoCarnetSanitario = legajoCarnetSanitario;
    }

    public String getLegajoCarpetaColgante() {
        return legajoCarpetaColgante;
    }

    public void setLegajoCarpetaColgante(String legajoCarpetaColgante) {
        this.legajoCarpetaColgante = legajoCarpetaColgante;
    }

    public String getLegajoEstado() {
        return legajoEstado;
    }

    public void setLegajoEstado(String legajoEstado) {
        this.legajoEstado = legajoEstado;
    }

    public String getLegajoFecha() {
        return legajoFecha;
    }

    public void setLegajoFecha(String legajoFecha) {
        this.legajoFecha = legajoFecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getLibreta() {
        return libreta;
    }

    public void setLibreta(String libreta) {
        this.libreta = libreta;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public boolean isNotasCorregidas() {
        return notasCorregidas;
    }

    public void setNotasCorregidas(boolean notasCorregidas) {
        this.notasCorregidas = notasCorregidas;
    }

    public Set<Aporte> getAportes() {
        return aportes;
    }

    public void setAportes(Set<Aporte> aportes) {
        this.aportes = aportes;
    }

//    public Set<Atencion> getAtenciones() {
//        return atenciones;
//    }
//
//    public void setAtenciones(Set<Atencion> atenciones) {
//        this.atenciones = atenciones;
//    }

    public Inscripcion getInscripcion() {
        return inscripcion;
    }

    public void setInscripcion(Inscripcion inscripcion) {
        this.inscripcion = inscripcion;
    }

    public Set<Observacione> getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(Set<Observacione> observaciones) {
        this.observaciones = observaciones;
    }

    public Set<Cursada> getCursadas() {
        return cursadas;
    }

    public void setCursadas(Set<Cursada> cursadas) {
        this.cursadas = cursadas;
    }
}