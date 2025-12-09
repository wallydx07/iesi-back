package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "persona")
public class Persona {
    @Id
    @Column(name = "persona_dni", nullable = false)
    private Long personaDni;

    @Size(max = 100)
    @Column(name = "persona_apellido", length = 100)
    private String personaApellido;

    @Size(max = 100)
    @Column(name = "persona_nombre", length = 100)
    private String personaNombre;

    @Size(max = 50)
    @Column(name = "persona_genero", length = 50)
    private String personaGenero;

    @Column(name = "persona_fecha_nacimiento")
    private LocalDate personaFechaNacimiento;

    @Size(max = 100)
    @Column(name = "persona_localidad_nacimiento", length = 100)
    private String personaLocalidadNacimiento;

    @Size(max = 50)
    @Column(name = "persona_pais_nacimiento", length = 50)
    private String personaPaisNacimiento;

    @Size(max = 50)
    @Column(name = "persona_provincia_nacimiento", length = 50)
    private String personaProvinciaNacimiento;

    @Size(max = 100)
    @Column(name = "persona_domicilio_calle", length = 100)
    private String personaDomicilioCalle;

    @Column(name = "persona_domicilio_nro")
    private Integer personaDomicilioNro;

    @Size(max = 100)
    @Column(name = "persona_domicilio_barrio", length = 100)
    private String personaDomicilioBarrio;

    @Size(max = 100)
    @Column(name = "persona_domicilio_localidad", length = 100)
    private String personaDomicilioLocalidad;

    @Size(max = 50)
    @Column(name = "persona_telefono", length = 50)
    private String personaDomicilioTelefono;

    @Size(max = 50)
    @Column(name = "persona_celular", length = 50)
    private String personaDomicilioCelular;

    @Size(max = 50)
    @Column(name = "persona_correo", length = 50)
    private String personaCorreo;

    @Size(max = 50)
    @Column(name = "persona_secundario_completo", length = 50)
    private String personaSecundarioCompleto;

    @Size(max = 150)
    @Column(name = "persona_escuela", length = 150)
    private String personaEscuela;

    @Size(max = 150)
    @Column(name = "persona_titulo", length = 150)
    private String personaTitulo;

    @Size(max = 50)
    @Column(name = "persona_secundario_fecha", length = 50)
    private String personaSecundarioFecha;

    @Size(max = 50)
    @Column(name = "persona_comunidad_originarios", length = 50)
    private String personaComunidadOriginarios;

    @Size(max = 150)
    @Column(name = "persona_comunidad_nombre", length = 150)
    private String personaComunidadNombre;

    @Size(max = 50)
    @Column(name = "persona_comunidad_pueblo", length = 50)
    private String personaComunidadPueblo;

    @Size(max = 100)
    @Column(name = "persona_comunidad_autoridad", length = 100)
    private String personaComunidadAutoridad;

    @Size(max = 50)
    @Column(name = "persona_comunidad_domicilio", length = 50)
    private String personaComunidadDomicilio;

    @Size(max = 50)
    @Column(name = "persona_comunidad_personeria", length = 50)
    private String personaComunidadPersoneria;

    @Size(max = 50)
    @Column(name = "persona_comunidad_departamento", length = 50)
    private String personaComunidadDepartamento;

    @Size(max = 50)
    @Column(name = "persona_comunidad_provincia", length = 50)
    private String personaComunidadProvincia;

    @Size(max = 50)
    @Column(name = "persona_departamento", length = 50)
    private String personaDepartamento;

    @Size(max = 50)
    @Column(name = "persona_provincia", length = 50)
    private String personaProvincia;

    @Size(max = 100)
    @Column(name = "persona_departamento_nacimiento", length = 100)
    private String personaDepartamentoNacimiento;

    @Size(max = 255)
    @Column(name = "persona_familiar_nombre")
    private String personaFamiliarNombre;

    @Size(max = 20)
    @Column(name = "persona_familiar_celular", length = 15)
    private String personaFamiliarCelular;

    @Size(max = 50)
    @Column(name = "persona_familiar_parentesco", length = 50)
    private String personaFamiliarParentesco;

    @Column(name = "persona_edad")
    private Integer personaEdad;

    @OneToMany(mappedBy = "legajoPersonaDni")
    @JsonManagedReference
    @JsonIgnore
    private Set<Legajo> legajos = new LinkedHashSet<>();

    @Size(max = 50)
    @Column(name = "persona_discapacidad", length = 50)
    private String personaDiscapacidad;

    @Size(max = 50)
    @Column(name = "persona_limitacion", length = 50)
    private String personaLimitacion;


    @Size(max = 50)
    @Column(name = "persona_cuil", length = 50)
    private String personaCuil;

    public Long getPersonaDni() {
        return personaDni;
    }

    public void setPersonaDni(Long personaDni) {
        this.personaDni = personaDni;
    }

    public String getPersonaApellido() {
        return personaApellido;
    }

    public void setPersonaApellido(String personaApellido) {
        this.personaApellido = personaApellido;
    }

    public String getPersonaNombre() {
        return personaNombre;
    }

    public void setPersonaNombre(String personaNombre) {
        this.personaNombre = personaNombre;
    }

    public String getPersonaGenero() {
        return personaGenero;
    }

    public void setPersonaGenero(String personaGenero) {
        this.personaGenero = personaGenero;
    }

    public LocalDate getPersonaFechaNacimiento() {
        return personaFechaNacimiento;
    }

    public void setPersonaFechaNacimiento(LocalDate personaFechaNacimiento) {
        this.personaFechaNacimiento = personaFechaNacimiento;
    }

    public String getPersonaLocalidadNacimiento() {
        return personaLocalidadNacimiento;
    }

    public void setPersonaLocalidadNacimiento(String personaLocalidadNacimiento) {
        this.personaLocalidadNacimiento = personaLocalidadNacimiento;
    }

    public String getPersonaPaisNacimiento() {
        return personaPaisNacimiento;
    }

    public void setPersonaPaisNacimiento(String personaPaisNacimiento) {
        this.personaPaisNacimiento = personaPaisNacimiento;
    }

    public String getPersonaProvinciaNacimiento() {
        return personaProvinciaNacimiento;
    }

    public void setPersonaProvinciaNacimiento(String personaProvinciaNacimiento) {
        this.personaProvinciaNacimiento = personaProvinciaNacimiento;
    }

    public String getPersonaDomicilioCalle() {
        return personaDomicilioCalle;
    }

    public void setPersonaDomicilioCalle(String personaDomicilioCalle) {
        this.personaDomicilioCalle = personaDomicilioCalle;
    }

    public Integer getPersonaDomicilioNro() {
        return personaDomicilioNro;
    }

    public void setPersonaDomicilioNro(Integer personaDomicilioNro) {
        this.personaDomicilioNro = personaDomicilioNro;
    }

    public String getPersonaDomicilioBarrio() {
        return personaDomicilioBarrio;
    }

    public void setPersonaDomicilioBarrio(String personaDomicilioBarrio) {
        this.personaDomicilioBarrio = personaDomicilioBarrio;
    }

    public String getPersonaDomicilioLocalidad() {
        return personaDomicilioLocalidad;
    }

    public void setPersonaDomicilioLocalidad(String personaDomicilioLocalidad) {
        this.personaDomicilioLocalidad = personaDomicilioLocalidad;
    }

    public String getPersonaDomicilioTelefono() {
        return personaDomicilioTelefono;
    }

    public void setPersonaDomicilioTelefono(String personaDomicilioTelefono) {
        this.personaDomicilioTelefono = personaDomicilioTelefono;
    }

    public String getPersonaDomicilioCelular() {
        return personaDomicilioCelular;
    }

    public void setPersonaDomicilioCelular(String personaDomicilioCelular) {
        this.personaDomicilioCelular = personaDomicilioCelular;
    }

    public String getPersonaCorreo() {
        return personaCorreo;
    }

    public void setPersonaCorreo(String personaCorreo) {
        this.personaCorreo = personaCorreo;
    }

    public String getPersonaSecundarioCompleto() {
        return personaSecundarioCompleto;
    }

    public void setPersonaSecundarioCompleto(String personaSecundarioCompleto) {
        this.personaSecundarioCompleto = personaSecundarioCompleto;
    }

    public String getPersonaEscuela() {
        return personaEscuela;
    }

    public void setPersonaEscuela(String personaEscuela) {
        this.personaEscuela = personaEscuela;
    }

    public String getPersonaTitulo() {
        return personaTitulo;
    }

    public void setPersonaTitulo(String personaTitulo) {
        this.personaTitulo = personaTitulo;
    }

    public String getPersonaSecundarioFecha() {
        return personaSecundarioFecha;
    }

    public void setPersonaSecundarioFecha(String personaSecundarioFecha) {
        this.personaSecundarioFecha = personaSecundarioFecha;
    }

    public String getPersonaComunidadOriginarios() {
        return personaComunidadOriginarios;
    }

    public void setPersonaComunidadOriginarios(String personaComunidadOriginarios) {
        this.personaComunidadOriginarios = personaComunidadOriginarios;
    }

    public String getPersonaComunidadNombre() {
        return personaComunidadNombre;
    }

    public void setPersonaComunidadNombre(String personaComunidadNombre) {
        this.personaComunidadNombre = personaComunidadNombre;
    }

    public String getPersonaComunidadPueblo() {
        return personaComunidadPueblo;
    }

    public void setPersonaComunidadPueblo(String personaComunidadPueblo) {
        this.personaComunidadPueblo = personaComunidadPueblo;
    }

    public String getPersonaComunidadAutoridad() {
        return personaComunidadAutoridad;
    }

    public void setPersonaComunidadAutoridad(String personaComunidadAutoridad) {
        this.personaComunidadAutoridad = personaComunidadAutoridad;
    }

    public String getPersonaComunidadDomicilio() {
        return personaComunidadDomicilio;
    }

    public void setPersonaComunidadDomicilio(String personaComunidadDomicilio) {
        this.personaComunidadDomicilio = personaComunidadDomicilio;
    }

    public String getPersonaComunidadPersoneria() {
        return personaComunidadPersoneria;
    }

    public void setPersonaComunidadPersoneria(String personaComunidadPersoneria) {
        this.personaComunidadPersoneria = personaComunidadPersoneria;
    }

    public String getPersonaComunidadDepartamento() {
        return personaComunidadDepartamento;
    }

    public void setPersonaComunidadDepartamento(String personaComunidadDepartamento) {
        this.personaComunidadDepartamento = personaComunidadDepartamento;
    }

    public String getPersonaComunidadProvincia() {
        return personaComunidadProvincia;
    }

    public void setPersonaComunidadProvincia(String personaComunidadProvincia) {
        this.personaComunidadProvincia = personaComunidadProvincia;
    }

    public String getPersonaDepartamento() {
        return personaDepartamento;
    }

    public void setPersonaDepartamento(String personaDepartamento) {
        this.personaDepartamento = personaDepartamento;
    }

    public String getPersonaProvincia() {
        return personaProvincia;
    }

    public void setPersonaProvincia(String personaProvincia) {
        this.personaProvincia = personaProvincia;
    }

    public String getPersonaDepartamentoNacimiento() {
        return personaDepartamentoNacimiento;
    }

    public void setPersonaDepartamentoNacimiento(String personaDepartamentoNacimiento) {
        this.personaDepartamentoNacimiento = personaDepartamentoNacimiento;
    }

    public String getPersonaFamiliarNombre() {
        return personaFamiliarNombre;
    }

    public void setPersonaFamiliarNombre(String personaFamiliarNombre) {
        this.personaFamiliarNombre = personaFamiliarNombre;
    }

    public String getPersonaFamiliarCelular() {
        return personaFamiliarCelular;
    }

    public void setPersonaFamiliarCelular(String personaFamiliarCelular) {
        this.personaFamiliarCelular = personaFamiliarCelular;
    }

    public String getPersonaFamiliarParentesco() {
        return personaFamiliarParentesco;
    }

    public void setPersonaFamiliarParentesco(String personaFamiliarParentesco) {
        this.personaFamiliarParentesco = personaFamiliarParentesco;
    }

    public Integer getPersonaEdad() {
        return personaEdad;
    }

    public void setPersonaEdad(Integer personaEdad) {
        this.personaEdad = personaEdad;
    }

    public Set<Legajo> getLegajos() {
        return legajos;
    }

    public void setLegajos(Set<Legajo> legajos) {
        this.legajos = legajos;
    }

    public String getPersonaDiscapacidad() {
        return personaDiscapacidad;
    }

    public void setPersonaDiscapacidad(String personaDiscapacidad) {
        this.personaDiscapacidad = personaDiscapacidad;
    }

    public String getPersonaLimitacion() {
        return personaLimitacion;
    }

    public void setPersonaLimitacion(String personaLimitacion) {
        this.personaLimitacion = personaLimitacion;
    }

    public String getPersonaCuil() {
        return personaCuil;
    }

    public void setPersonaCuil(String personaCuil) {
        this.personaCuil = personaCuil;
    }
}