package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "alumno")
public class Alumno {
    @Id
    @Column(name = "alumno_dni", nullable = false)
    private Long alumnoDni;

    @Size(max = 100)
    @Column(name = "alumno_apellido", length = 100)
    private String alumnoApellido;

    @Size(max = 100)
    @Column(name = "alumno_nombre", length = 100)
    private String alumnoNombre;

    @Size(max = 50)
    @Column(name = "alumno_genero", length = 50)
    private String alumnoGenero;

    @Column(name = "alumno_fecha_nacimiento")
    private LocalDate alumnoFechaNacimiento;

    @Size(max = 100)
    @Column(name = "alumno_localidad_nacimiento", length = 100)
    private String alumnoLocalidadNacimiento;

    @Size(max = 50)
    @Column(name = "alumno_pais_nacimiento", length = 50)
    private String alumnoPaisNacimiento;

    @Size(max = 50)
    @Column(name = "alumno_provincia_nacimiento", length = 50)
    private String alumnoProvinciaNacimiento;

    @Size(max = 100)
    @Column(name = "domicilio_alumno_calle", length = 100)
    private String domicilioAlumnoCalle;

    @Column(name = "domicilio_alumno_nro")
    private Integer domicilioAlumnoNro;

    @Size(max = 100)
    @Column(name = "domicilio_alumno_barrio", length = 100)
    private String domicilioAlumnoBarrio;

    @Size(max = 100)
    @Column(name = "domicilio_alumno_localidad", length = 100)
    private String domicilioAlumnoLocalidad;

    @Size(max = 50)
    @Column(name = "domicilio_alumno_telefono", length = 50)
    private String domicilioAlumnoTelefono;

    @Size(max = 50)
    @Column(name = "domicilio_alumno_celular", length = 50)
    private String domicilioAlumnoCelular;

    @Size(max = 50)
    @Column(name = "domicilio_alumno_correo", length = 50)
    private String domicilioAlumnoCorreo;

    @Size(max = 50)
    @Column(name = "secundario_alumno_completo", length = 50)
    private String secundarioAlumnoCompleto;

    @Size(max = 150)
    @Column(name = "secundario_alumno_escuela", length = 150)
    private String secundarioAlumnoEscuela;

    @Size(max = 150)
    @Column(name = "secundario_alumno_titulo", length = 150)
    private String secundarioAlumnoTitulo;

    @Size(max = 50)
    @Column(name = "secundario_alumno_fecha", length = 50)
    private String secundarioAlumnoFecha;

    @Size(max = 50)
    @Column(name = "comunidad_alumno_originarios", length = 50)
    private String comunidadAlumnoOriginarios;

    @Size(max = 150)
    @Column(name = "comunidad_alumno_nombre", length = 150)
    private String comunidadAlumnoNombre;

    @Size(max = 50)
    @Column(name = "comunidad_alumno_pueblo", length = 50)
    private String comunidadAlumnoPueblo;

    @Size(max = 100)
    @Column(name = "comunidad_alumno_autoridad", length = 100)
    private String comunidadAlumnoAutoridad;

    @Size(max = 50)
    @Column(name = "comunidad_alumno_domicilio", length = 50)
    private String comunidadAlumnoDomicilio;

    @Size(max = 50)
    @Column(name = "comunidad_alumno_personeria", length = 50)
    private String comunidadAlumnoPersoneria;

    @Size(max = 50)
    @Column(name = "comunidad_alumno_departamente", length = 50)
    private String comunidadAlumnoDepartamente;

    @Size(max = 50)
    @Column(name = "comunidad_alumno_provincia", length = 50)
    private String comunidadAlumnoProvincia;

    @Size(max = 50)
    @Column(name = "alumno_departamento", length = 50)
    private String alumnoDepartamento;

    @Size(max = 50)
    @Column(name = "alumno_provincia", length = 50)
    private String alumnoProvincia;

    @Size(max = 100)
    @Column(name = "alumno_departamento_nacimiento", length = 100)
    private String alumnoDepartamentoNacimiento;

    @Size(max = 255)
    @Column(name = "familiar_nombre")
    private String familiarNombre;

    @Size(max = 15)
    @Column(name = "familiar_celular", length = 15)
    private String familiarCelular;

    @Size(max = 50)
    @Column(name = "familiar_parentesco", length = 50)
    private String familiarParentesco;

    @Column(name = "alumno_edad")
    private Integer alumnoEdad;

    @OneToMany(mappedBy = "legajoAlumnoDni")
    @JsonManagedReference
    @JsonIgnore
    private Set<Legajo> legajos = new LinkedHashSet<>();

    @Size(max = 50)
    @Column(name = "discapacidad_nombre", length = 50)
    private String discapacidadNombre;

    @Size(max = 50)
    @Column(name = "limitacion_nombre", length = 50)
    private String limitacionNombre;

    public String getLimitacionNombre() {
        return limitacionNombre;
    }

    public void setLimitacionNombre(String limitacionNombre) {
        this.limitacionNombre = limitacionNombre;
    }

    public String getDiscapacidadNombre() {
        return discapacidadNombre;
    }

    public void setDiscapacidadNombre(String discapacidadNombre) {
        this.discapacidadNombre = discapacidadNombre;
    }


    public Set<Legajo> getLegajos() {
        return legajos;
    }

    public void setLegajos(Set<Legajo> legajos) {
        this.legajos = legajos;
    }

    public Long getAlumnoDni() {
        return alumnoDni;
    }

    public void setAlumnoDni(Long id) {
        this.alumnoDni = id;
    }

    public String getAlumnoApellido() {
        return alumnoApellido;
    }

    public void setAlumnoApellido(String alumnoApellido) {
        this.alumnoApellido = alumnoApellido;
    }

    public String getAlumnoNombre() {
        return alumnoNombre;
    }

    public void setAlumnoNombre(String alumnoNombre) {
        this.alumnoNombre = alumnoNombre;
    }

    public String getAlumnoGenero() {
        return alumnoGenero;
    }

    public void setAlumnoGenero(String alumnoGenero) {
        this.alumnoGenero = alumnoGenero;
    }

    public LocalDate getAlumnoFechaNacimiento() {
        return alumnoFechaNacimiento;
    }

    public void setAlumnoFechaNacimiento(LocalDate alumnoFechaNacimiento) {
        this.alumnoFechaNacimiento = alumnoFechaNacimiento;
    }

    public String getAlumnoLocalidadNacimiento() {
        return alumnoLocalidadNacimiento;
    }

    public void setAlumnoLocalidadNacimiento(String alumnoLocalidadNacimiento) {
        this.alumnoLocalidadNacimiento = alumnoLocalidadNacimiento;
    }

    public String getAlumnoPaisNacimiento() {
        return alumnoPaisNacimiento;
    }

    public void setAlumnoPaisNacimiento(String alumnoPaisNacimiento) {
        this.alumnoPaisNacimiento = alumnoPaisNacimiento;
    }

    public String getAlumnoProvinciaNacimiento() {
        return alumnoProvinciaNacimiento;
    }

    public void setAlumnoProvinciaNacimiento(String alumnoProvinciaNacimiento) {
        this.alumnoProvinciaNacimiento = alumnoProvinciaNacimiento;
    }

    public String getDomicilioAlumnoCalle() {
        return domicilioAlumnoCalle;
    }

    public void setDomicilioAlumnoCalle(String domicilioAlumnoCalle) {
        this.domicilioAlumnoCalle = domicilioAlumnoCalle;
    }

    public Integer getDomicilioAlumnoNro() {
        return domicilioAlumnoNro;
    }

    public void setDomicilioAlumnoNro(Integer domicilioAlumnoNro) {
        this.domicilioAlumnoNro = domicilioAlumnoNro;
    }

    public String getDomicilioAlumnoBarrio() {
        return domicilioAlumnoBarrio;
    }

    public void setDomicilioAlumnoBarrio(String domicilioAlumnoBarrio) {
        this.domicilioAlumnoBarrio = domicilioAlumnoBarrio;
    }

    public String getDomicilioAlumnoLocalidad() {
        return domicilioAlumnoLocalidad;
    }

    public void setDomicilioAlumnoLocalidad(String domicilioAlumnoLocalidad) {
        this.domicilioAlumnoLocalidad = domicilioAlumnoLocalidad;
    }

    public String getDomicilioAlumnoTelefono() {
        return domicilioAlumnoTelefono;
    }

    public void setDomicilioAlumnoTelefono(String domicilioAlumnoTelefono) {
        this.domicilioAlumnoTelefono = domicilioAlumnoTelefono;
    }

    public String getDomicilioAlumnoCelular() {
        return domicilioAlumnoCelular;
    }

    public void setDomicilioAlumnoCelular(String domicilioAlumnoCelular) {
        this.domicilioAlumnoCelular = domicilioAlumnoCelular;
    }

    public String getDomicilioAlumnoCorreo() {
        return domicilioAlumnoCorreo;
    }

    public void setDomicilioAlumnoCorreo(String domicilioAlumnoCorreo) {
        this.domicilioAlumnoCorreo = domicilioAlumnoCorreo;
    }

    public String getSecundarioAlumnoCompleto() {
        return secundarioAlumnoCompleto;
    }

    public void setSecundarioAlumnoCompleto(String secundarioAlumnoCompleto) {
        this.secundarioAlumnoCompleto = secundarioAlumnoCompleto;
    }

    public String getSecundarioAlumnoEscuela() {
        return secundarioAlumnoEscuela;
    }

    public void setSecundarioAlumnoEscuela(String secundarioAlumnoEscuela) {
        this.secundarioAlumnoEscuela = secundarioAlumnoEscuela;
    }

    public String getSecundarioAlumnoTitulo() {
        return secundarioAlumnoTitulo;
    }

    public void setSecundarioAlumnoTitulo(String secundarioAlumnoTitulo) {
        this.secundarioAlumnoTitulo = secundarioAlumnoTitulo;
    }

    public String getSecundarioAlumnoFecha() {
        return secundarioAlumnoFecha;
    }

    public void setSecundarioAlumnoFecha(String secundarioAlumnoFecha) {
        this.secundarioAlumnoFecha = secundarioAlumnoFecha;
    }

    public String getComunidadAlumnoOriginarios() {
        return comunidadAlumnoOriginarios;
    }

    public void setComunidadAlumnoOriginarios(String comunidadAlumnoOriginarios) {
        this.comunidadAlumnoOriginarios = comunidadAlumnoOriginarios;
    }

    public String getComunidadAlumnoNombre() {
        return comunidadAlumnoNombre;
    }

    public void setComunidadAlumnoNombre(String comunidadAlumnoNombre) {
        this.comunidadAlumnoNombre = comunidadAlumnoNombre;
    }

    public String getComunidadAlumnoPueblo() {
        return comunidadAlumnoPueblo;
    }

    public void setComunidadAlumnoPueblo(String comunidadAlumnoPueblo) {
        this.comunidadAlumnoPueblo = comunidadAlumnoPueblo;
    }

    public String getComunidadAlumnoAutoridad() {
        return comunidadAlumnoAutoridad;
    }

    public void setComunidadAlumnoAutoridad(String comunidadAlumnoAutoridad) {
        this.comunidadAlumnoAutoridad = comunidadAlumnoAutoridad;
    }

    public String getComunidadAlumnoDomicilio() {
        return comunidadAlumnoDomicilio;
    }

    public void setComunidadAlumnoDomicilio(String comunidadAlumnoDomicilio) {
        this.comunidadAlumnoDomicilio = comunidadAlumnoDomicilio;
    }

    public String getComunidadAlumnoPersoneria() {
        return comunidadAlumnoPersoneria;
    }

    public void setComunidadAlumnoPersoneria(String comunidadAlumnoPersoneria) {
        this.comunidadAlumnoPersoneria = comunidadAlumnoPersoneria;
    }

    public String getComunidadAlumnoDepartamente() {
        return comunidadAlumnoDepartamente;
    }

    public void setComunidadAlumnoDepartamente(String comunidadAlumnoDepartamente) {
        this.comunidadAlumnoDepartamente = comunidadAlumnoDepartamente;
    }

    public String getComunidadAlumnoProvincia() {
        return comunidadAlumnoProvincia;
    }

    public void setComunidadAlumnoProvincia(String comunidadAlumnoProvincia) {
        this.comunidadAlumnoProvincia = comunidadAlumnoProvincia;
    }

    public String getAlumnoDepartamento() {
        return alumnoDepartamento;
    }

    public void setAlumnoDepartamento(String alumnoDepartamento) {
        this.alumnoDepartamento = alumnoDepartamento;
    }

    public String getAlumnoProvincia() {
        return alumnoProvincia;
    }

    public void setAlumnoProvincia(String alumnoProvincia) {
        this.alumnoProvincia = alumnoProvincia;
    }

    public String getAlumnoDepartamentoNacimiento() {
        return alumnoDepartamentoNacimiento;
    }

    public void setAlumnoDepartamentoNacimiento(String alumnoDepartamentoNacimiento) {
        this.alumnoDepartamentoNacimiento = alumnoDepartamentoNacimiento;
    }

    public String getFamiliarNombre() {
        return familiarNombre;
    }

    public void setFamiliarNombre(String familiarNombre) {
        this.familiarNombre = familiarNombre;
    }

    public String getFamiliarCelular() {
        return familiarCelular;
    }

    public void setFamiliarCelular(String familiarCelular) {
        this.familiarCelular = familiarCelular;
    }

    public String getFamiliarParentesco() {
        return familiarParentesco;
    }

    public void setFamiliarParentesco(String familiarParentesco) {
        this.familiarParentesco = familiarParentesco;
    }

    public Integer getAlumnoEdad() {
        return alumnoEdad;
    }

    public void setAlumnoEdad(Integer alumnoEdad) {
        this.alumnoEdad = alumnoEdad;
    }

}