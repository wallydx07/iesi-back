package com.example.iesiback.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario al que va dirigida la notificación
    @Column(nullable = false)
    private Long usuarioId;

    // Mensaje de la notificación
    @Column(nullable = false, length = 500)
    private String mensaje;

    // Fecha y hora en que se generó la notificación
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    // Indica si el usuario ya leyó la notificación
    @Column(nullable = false)
    private boolean leido = false;

    // Constructores
    public Notificacion() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Notificacion(Long usuarioId, String mensaje) {
        this.usuarioId = usuarioId;
        this.mensaje = mensaje;
        this.leido = false;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
}
