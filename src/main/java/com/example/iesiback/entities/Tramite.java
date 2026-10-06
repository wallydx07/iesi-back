package com.example.iesiback.entities;

import com.example.iesiback.enums.PrioridadTramite;
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

    @ColumnDefault("'Pendiente'")
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

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tramite_prioridad",
            length = 20,
            columnDefinition = "varchar(20) default 'MEDIA'"
    )
    private PrioridadTramite tramitePrioridad = PrioridadTramite.MEDIA;

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

    @Size(max = 100)
    @Column(name = "tramite_area", length = 100)
    private String tramiteArea;

    @OneToMany(mappedBy = "tramite")
    private List<Pago> pagos;

    // Permiso generado por este trámite (lado inverso: no crea columna en tramite)
    @OneToOne(mappedBy = "tramite", fetch = FetchType.LAZY)
//    @JsonIgnore
    private Permiso permiso;

    @JsonIgnore
    public String getTramiteFechaFormateada() {
        if (tramiteFecha == null) return "";
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "Fecha: " + tramiteFecha.format(f) +
                "   Hora: " + tramiteFecha.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}