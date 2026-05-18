package com.example.iesiback.dto;

import com.example.iesiback.entities.PagoDetalle;
import com.example.iesiback.enums.EstadoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReciboDTO {
    private Long aporteId;
    private String aporteNroRecibo;

    // Datos del Alumno
    private String alumnoApellido;
    private String alumnoNombre;
    private String alumnoDni;

    // Datos del Movimiento
    private String concepto;
    private BigDecimal aporteMonto;
    private String metodo; // "Efectivo", "Transferencia", etc.
    private LocalDate aporteFecha;
    private String hora;

    private List<PagoDetalle> pagoDetalles = new ArrayList<>();

    // Auditoría y Estado
    private String usuario; // Campo vital para el agrupamiento por operador
    private EstadoPago estado;


    public Long getAporteId() {
        return aporteId;
    }

    public void setAporteId(Long aporteId) {
        this.aporteId = aporteId;
    }

    public String getAporteNroRecibo() {
        return aporteNroRecibo;
    }

    public void setAporteNroRecibo(String aporteNroRecibo) {
        this.aporteNroRecibo = aporteNroRecibo;
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

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getAlumnoDni() {
        return alumnoDni;
    }

    public void setAlumnoDni(String alumnoDni) {
        this.alumnoDni = alumnoDni;
    }

    public BigDecimal getAporteMonto() {
        return aporteMonto;
    }

    public void setAporteMonto(BigDecimal aporteMonto) {
        this.aporteMonto = aporteMonto;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public LocalDate getAporteFecha() {
        return aporteFecha;
    }

    public void setAporteFecha(LocalDate aporteFecha) {
        this.aporteFecha = aporteFecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public List<PagoDetalle> getPagoDetalles() {
        return pagoDetalles;
    }

    public void setPagoDetalles(List<PagoDetalle> pagoDetalles) {
        this.pagoDetalles = pagoDetalles;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }
}