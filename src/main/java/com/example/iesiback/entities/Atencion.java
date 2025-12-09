package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "atencion")
public class Atencion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "atencion_id_gen")
    @SequenceGenerator(name = "atencion_id_gen", sequenceName = "atencion_atencion_id_seq", allocationSize = 1)
    @Column(name = "atencion_id", nullable = false)
    private Integer id;


    @ColumnDefault("Pendiente")
    @Column(name = "atencion_resuelto")
    private String atencionResuelto;


    @Column(name = "atencion_dni")
    private Long atencionDni;

    @Size(max = 100)
    @Column(name = "atencion_apellido_nombre", length = 100)
    private String atencionApellidoNombre;

    @Size(max = 100)
    @Column(name = "atencion_correo", length = 100)
    private String atencionCorreo;

    @Column(name = "atencion_celular")
    private Long atencionCelular;

    @Column(name = "atencion_tipo", length = Integer.MAX_VALUE)
    private String atencionTipo;

    @Column(name = "atencion_problema", length = Integer.MAX_VALUE)
    private String atencionProblema;

    @ColumnDefault("CURRENT_DATE")
    @Column(name = "atencion_fecha")
    private LocalDate atencionFecha;

    @Column(name = "atencion_respuesta", length = Integer.MAX_VALUE)
    private String atencionRespuesta;

    @Column(name = "atencion_observaciones", length = Integer.MAX_VALUE)
    private String atencionObservaciones;

    @Size(max = 50)
    @Column(name = "atencion_destino", length = 50)
    private String atencionDestino;

    @Size(max = 50)
    @Column(name = "atencion_usuario", length = 50)
    private String atencionUsuario;

    @OneToMany(mappedBy = "tramite")
    @JsonManagedReference
    private List<Pases> pases;

    @OneToMany(mappedBy = "atencion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("atencion-certificados")
    private List<CertificadoEstudiante> certificados;

    @Size(max = 20)
    @Column(name = "codigo_seguimiento", length = 20)
    private String codigoSeguimiento;

    @Column(name = "numero_tipo")
    private Long numeroTipo;

    @Size(max = 20)
    @Column(name = "atencion_prioridad", length = 20)
    private String atencionPrioridad;

    @Column(name = "atencion_folios")
    private Long atencionFolios;

    @Size(max = 50)
    @Column(name = "legajo_id", length = 20)
    private String legajoId;

    @Column(name = "gestor_dni")
    private Long gestorDni;

    // Para valores cortos (ej: "Presencial", "WhatsApp")
    @Size(max = 50)
    @Column(name = "atencion_canal", length = 50)
    private String atencionCanal;

    // Para títulos o tipos (ej: "Información Académica")
    @Size(max = 100)
    @Column(name = "atencion_subtipo", length = 100)
    private String atencionSubTipo;


    @Column(name = "atencion_asunto", columnDefinition = "TEXT")
    private String atencionAsunto;

    @Column(name = "atencion_Referencia")
    private Integer atencionReferencia;


    public String getAtencionCanal() {
        return atencionCanal;
    }

    public void setAtencionCanal(String atencionCanal) {
        this.atencionCanal = atencionCanal;
    }

    public String getAtencionSubTipo() {
        return atencionSubTipo;
    }

    public void setAtencionSubTipo(String atencionSubTipo) {
        this.atencionSubTipo = atencionSubTipo;
    }

    public String getAtencionAsunto() {
        return atencionAsunto;
    }

    public void setAtencionAsunto(String atencionAsunto) {
        this.atencionAsunto = atencionAsunto;
    }

    public List<Pases> getPases() {
        return pases;
    }

    public void setPases(List<Pases> pases) {
        this.pases = pases;
    }

    public Long getGestorDni() {
        return gestorDni;
    }

    public void setGestorDni(Long gestorDni) {
        this.gestorDni = gestorDni;
    }

    public String getLegajoId() {
        return legajoId;
    }

    public void setLegajoId(String legajoId) {
        this.legajoId = legajoId;
    }

    public Long getAtencionFolios() {
        return atencionFolios;
    }

    public void setAtencionFolios(Long atencionFolios) {
        this.atencionFolios = atencionFolios;
    }

    public String getAtencionPrioridad() {
        return atencionPrioridad;
    }

    public void setAtencionPrioridad(String atencionPrioridad) {
        this.atencionPrioridad = atencionPrioridad;
    }

    public Long getNumeroTipo() {
        return numeroTipo;
    }

    public void setNumeroTipo(Long numeroTipo) {
        this.numeroTipo = numeroTipo;
    }

    public String getCodigoSeguimiento() {
        return codigoSeguimiento;
    }

    public void setCodigoSeguimiento(String codigoSeguimiento) {
        this.codigoSeguimiento = codigoSeguimiento;
    }

    public String getAtencionUsuario() {
        return atencionUsuario;
    }

    public void setAtencionUsuario(String atencionUsuario) {
        this.atencionUsuario = atencionUsuario;
    }

    public List<CertificadoEstudiante> getCertificados() {
        return certificados;
    }

    public void setCertificados(List<CertificadoEstudiante> certificados) {
        this.certificados = certificados;
    }

    public String getAtencionDestino() {
        return atencionDestino;
    }

    public void setAtencionDestino(String atencionDestino) {
        this.atencionDestino = atencionDestino;
    }

    public String getAtencionObservaciones() {
        return atencionObservaciones;
    }

    public void setAtencionObservaciones(String atencionObservaciones) {
        this.atencionObservaciones = atencionObservaciones;
    }

    public LocalDate getAtencionFecha() {
        return atencionFecha;
    }

    public void setAtencionFecha(LocalDate atencionFecha) {
        this.atencionFecha = atencionFecha;
    }

    public String getAtencionRespuesta() {
        return atencionRespuesta;
    }

    public void setAtencionRespuesta(String atencionRespuesta) {
        this.atencionRespuesta = atencionRespuesta;
    }

    public String getAtencionProblema() {
        return atencionProblema;
    }

    public void setAtencionProblema(String atencionProblema) {
        this.atencionProblema = atencionProblema;
    }

    public String getAtencionTipo() {
        return atencionTipo;
    }

    public void setAtencionTipo(String atencionTipo) {
        this.atencionTipo = atencionTipo;
    }

    public Long getAtencionCelular() {
        return atencionCelular;
    }

    public void setAtencionCelular(Long atencionCelular) {
        this.atencionCelular = atencionCelular;
    }

    public String getAtencionCorreo() {
        return atencionCorreo;
    }

    public void setAtencionCorreo(String atencionCorreo) {
        this.atencionCorreo = atencionCorreo;
    }

    public String getAtencionApellidoNombre() {
        return atencionApellidoNombre;
    }

    public void setAtencionApellidoNombre(String atencionApellidoNombre) {
        this.atencionApellidoNombre = atencionApellidoNombre;
    }

    public Long getAtencionDni() {
        return atencionDni;
    }

    public void setAtencionDni(Long atencionDni) {
        this.atencionDni = atencionDni;
    }

    public String getAtencionResuelto() {
        return atencionResuelto;
    }

    public void setAtencionResuelto(String atencionResuelto) {
        this.atencionResuelto = atencionResuelto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    //    @Transient
//    public String getLegajoId() {
//        return legajo != null ? legajo.getLegajoId() : null;
//    }

}