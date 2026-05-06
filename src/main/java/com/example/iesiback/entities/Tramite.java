package com.example.iesiback.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tramite")
public class Tramite {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tramite_id_gen")
    @SequenceGenerator(name = "tramite_id_gen", sequenceName = "tramite_id_seq", allocationSize = 1)
    @Column(name = "tramite_id", nullable = false)
    private Integer id;


    @ColumnDefault("Pendiente")
    @Column(name = "tramite_estado")
    private String tramiteEstado;


    @Column(name = "tramite_dni")
    private Long tramiteDni;

    @Size(max = 100)
    @Column(name = "tramite_apellido_nombre", length = 100)
    private String tramiteApellidoNombre;

    @Size(max = 100)
    @Column(name = "tramite_correo", length = 100)
    private String tramiteCorreo;

    @Column(name = "tramite_celular")
    private Long tramiteCelular;

    @Column(name = "tramite_tipo", length = Integer.MAX_VALUE)
    private String tramiteTipo;



    @Column(name = "tramite_problema", length = Integer.MAX_VALUE)
    private String tramiteProblema;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "tramite_fecha")
    private LocalDateTime tramiteFecha;


    @Column(name = "tramite_respuesta", length = Integer.MAX_VALUE)
    private String tramiteRespuesta;

    @Column(name = "tramite_observaciones", length = Integer.MAX_VALUE)
    private String tramiteObservaciones;

    @Size(max = 50)
    @Column(name = "tramite_destino", length = 50)
    private String tramiteDestino;

    @Size(max = 50)
    @Column(name = "tramite_usuario", length = 50)
    private String tramiteUsuario;

    @OneToMany(mappedBy = "tramite")
    @JsonManagedReference
    private List<Pases> pases;

    @OneToMany(mappedBy = "tramite", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("tramite-certificados")
    private List<CertificadoEstudiante> certificados;

    @Size(max = 20)
    @Column(name = "codigo_seguimiento", length = 20)
    private String codigoSeguimiento;

    @Column(name = "numero_tipo")
    private Long numeroTipo;

    @Size(max = 20)
    @Column(name = "tramite_prioridad", length = 20)
    private String tramitePrioridad;

    @Column(name = "tramite_folios")
    private Long tramiteFolios;

    @Size(max = 50)
    @Column(name = "legajo_id", length = 20)
    private String legajoId;

    @Column(name = "gestor_dni")
    private Long gestorDni;

    // Para valores cortos (ej: "Presencial", "WhatsApp")
    @Size(max = 50)
    @Column(name = "tramite_canal", length = 50)
    private String tramiteCanal;

    // Para títulos o tipos (ej: "Información Académica")
    @Size(max = 100)
    @Column(name = "tramite_subtipo", length = 100)
    private String tramiteSubTipo;


    @Column(name = "tramite_asunto", columnDefinition = "TEXT")
    private String tramiteAsunto;

    @Column(name = "tramite_Referencia")
    private Integer tramiteReferencia;

    @OneToMany(mappedBy = "tramite")
    private List<Pago> pagos;




    @JsonIgnore
    public String getTramiteFechaFormateada() {
        if (tramiteFecha == null) return "";
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "Fecha: " + tramiteFecha.format(f) +
                "   Hora: " + tramiteFecha.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getTramiteDni() {
        return tramiteDni;
    }

    public void setTramiteDni(Long tramiteDni) {
        this.tramiteDni = tramiteDni;
    }

    public String getTramiteEstado() {
        return tramiteEstado;
    }

    public void setTramiteEstado(String tramiteEstado) {
        this.tramiteEstado = tramiteEstado;
    }

    public String getTramiteApellidoNombre() {
        return tramiteApellidoNombre;
    }

    public void setTramiteApellidoNombre(String tramiteApellidoNombre) {
        this.tramiteApellidoNombre = tramiteApellidoNombre;
    }

    public String getTramiteCorreo() {
        return tramiteCorreo;
    }

    public void setTramiteCorreo(String tramiteCorreo) {
        this.tramiteCorreo = tramiteCorreo;
    }

    public Long getTramiteCelular() {
        return tramiteCelular;
    }

    public void setTramiteCelular(Long tramiteCelular) {
        this.tramiteCelular = tramiteCelular;
    }

    public String getTramiteTipo() {
        return tramiteTipo;
    }

    public void setTramiteTipo(String tramiteTipo) {
        this.tramiteTipo = tramiteTipo;
    }

    public String getTramiteProblema() {
        return tramiteProblema;
    }

    public void setTramiteProblema(String tramiteProblema) {
        this.tramiteProblema = tramiteProblema;
    }

    public LocalDateTime getTramiteFecha() {
        return tramiteFecha;
    }

    public void setTramiteFecha(LocalDateTime tramiteFecha) {
        this.tramiteFecha = tramiteFecha;
    }

    public String getTramiteRespuesta() {
        return tramiteRespuesta;
    }

    public void setTramiteRespuesta(String tramiteRespuesta) {
        this.tramiteRespuesta = tramiteRespuesta;
    }

    public String getTramiteObservaciones() {
        return tramiteObservaciones;
    }

    public void setTramiteObservaciones(String tramiteObservaciones) {
        this.tramiteObservaciones = tramiteObservaciones;
    }

    public String getTramiteDestino() {
        return tramiteDestino;
    }

    public void setTramiteDestino(String tramiteDestino) {
        this.tramiteDestino = tramiteDestino;
    }

    public String getTramiteUsuario() {
        return tramiteUsuario;
    }

    public void setTramiteUsuario(String tramiteUsuario) {
        this.tramiteUsuario = tramiteUsuario;
    }

    public List<Pases> getPases() {
        return pases;
    }

    public void setPases(List<Pases> pases) {
        this.pases = pases;
    }

    public List<CertificadoEstudiante> getCertificados() {
        return certificados;
    }

    public void setCertificados(List<CertificadoEstudiante> certificados) {
        this.certificados = certificados;
    }

    public String getCodigoSeguimiento() {
        return codigoSeguimiento;
    }

    public void setCodigoSeguimiento(String codigoSeguimiento) {
        this.codigoSeguimiento = codigoSeguimiento;
    }

    public Long getNumeroTipo() {
        return numeroTipo;
    }

    public void setNumeroTipo(Long numeroTipo) {
        this.numeroTipo = numeroTipo;
    }

    public String getTramitePrioridad() {
        return tramitePrioridad;
    }

    public void setTramitePrioridad(String tramitePrioridad) {
        this.tramitePrioridad = tramitePrioridad;
    }

    public String getLegajoId() {
        return legajoId;
    }

    public void setLegajoId(String legajoId) {
        this.legajoId = legajoId;
    }

    public Long getTramiteFolios() {
        return tramiteFolios;
    }

    public void setTramiteFolios(Long tramiteFolios) {
        this.tramiteFolios = tramiteFolios;
    }

    public Long getGestorDni() {
        return gestorDni;
    }

    public void setGestorDni(Long gestorDni) {
        this.gestorDni = gestorDni;
    }

    public String getTramiteCanal() {
        return tramiteCanal;
    }

    public void setTramiteCanal(String tramiteCanal) {
        this.tramiteCanal = tramiteCanal;
    }

    public String getTramiteSubTipo() {
        return tramiteSubTipo;
    }

    public void setTramiteSubTipo(String tramiteSubTipo) {
        this.tramiteSubTipo = tramiteSubTipo;
    }

    public String getTramiteAsunto() {
        return tramiteAsunto;
    }

    public void setTramiteAsunto(String tramiteAsunto) {
        this.tramiteAsunto = tramiteAsunto;
    }

    public Integer getTramiteReferencia() {
        return tramiteReferencia;
    }

    public void setTramiteReferencia(Integer tramiteReferencia) {
        this.tramiteReferencia = tramiteReferencia;
    }


    //    @Transient
//    public String getLegajoId() {
//        return legajo != null ? legajo.getLegajoId() : null;
//    }

}