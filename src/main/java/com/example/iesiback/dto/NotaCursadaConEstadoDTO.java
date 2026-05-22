package com.example.iesiback.dto;

import com.example.iesiback.enums.EstadoNota;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class NotaCursadaConEstadoDTO {
    private Long notaId;
    private String personaDni;
    private String personaLegajoId;
    private String personaApellido;
    private String personaNombre;
    private LocalDate notaFechaNota;
    private String notaCalificacionNotaNumero;
    private String notaCalificacionNotaLetra;
    private EstadoNota notaEstado;
    private String notaLibroNota;
    private String notaFolioNota;
    private String cursadaStatus;
    private String notaObservaciones;
    private String notaUsuario;
    private Double primerParcial;
    private Double recuperatorio1;
    private Double segundoParcial;
    private Double recuperatorio2;
    private Double trabajosPracticos;
    private Double asistencia;
    private Double coloquio;
    private Double trabajoInstitucional;

    // Constructor que mapea desde la projection + estado
    public NotaCursadaConEstadoDTO(NotaCursadaDTO dto, String estadoCorrelativa) {
        this.notaId = dto.getNotaId();
        this.personaDni = dto.getPersonaDni();
        this.personaLegajoId = dto.getPersonaLegajoId();
        this.personaApellido = dto.getPersonaApellido();
        this.personaNombre = dto.getPersonaNombre();
        this.notaFechaNota = dto.getNotaFechaNota();
        this.notaCalificacionNotaNumero = dto.getNotaCalificacionNotaNumero();
        this.notaCalificacionNotaLetra = dto.getNotaCalificacionNotaLetra();
        this.notaEstado = dto.getNotaEstado();
        this.notaLibroNota = dto.getNotaLibroNota();
        this.notaFolioNota = dto.getNotaFolioNota();
        this.cursadaStatus = estadoCorrelativa; // ← el estado calculado
        this.notaObservaciones = dto.getNotaObservaciones();
        this.notaUsuario = dto.getNotaUsuario();
        this.primerParcial = dto.getPrimerParcial();
        this.recuperatorio1 = dto.getRecuperatorio1();
        this.segundoParcial = dto.getSegundoParcial();
        this.recuperatorio2 = dto.getRecuperatorio2();
        this.trabajosPracticos = dto.getTrabajosPracticos();
        this.asistencia = dto.getAsistencia();
        this.coloquio = dto.getColoquio();
        this.trabajoInstitucional = dto.getTrabajoInstitucional();
    }

    public Long getNotaId() {
        return notaId;
    }

    public void setNotaId(Long notaId) {
        this.notaId = notaId;
    }

    public String getPersonaDni() {
        return personaDni;
    }

    public void setPersonaDni(String personaDni) {
        this.personaDni = personaDni;
    }

    public String getPersonaLegajoId() {
        return personaLegajoId;
    }

    public void setPersonaLegajoId(String personaLegajoId) {
        this.personaLegajoId = personaLegajoId;
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

    public LocalDate getNotaFechaNota() {
        return notaFechaNota;
    }

    public void setNotaFechaNota(LocalDate notaFechaNota) {
        this.notaFechaNota = notaFechaNota;
    }

    public String getNotaCalificacionNotaNumero() {
        return notaCalificacionNotaNumero;
    }

    public void setNotaCalificacionNotaNumero(String notaCalificacionNotaNumero) {
        this.notaCalificacionNotaNumero = notaCalificacionNotaNumero;
    }

    public String getNotaCalificacionNotaLetra() {
        return notaCalificacionNotaLetra;
    }

    public void setNotaCalificacionNotaLetra(String notaCalificacionNotaLetra) {
        this.notaCalificacionNotaLetra = notaCalificacionNotaLetra;
    }

    public EstadoNota getNotaEstado() {
        return notaEstado;
    }

    public void setNotaEstado(EstadoNota notaEstado) {
        this.notaEstado = notaEstado;
    }

    public String getNotaLibroNota() {
        return notaLibroNota;
    }

    public void setNotaLibroNota(String notaLibroNota) {
        this.notaLibroNota = notaLibroNota;
    }

    public String getNotaFolioNota() {
        return notaFolioNota;
    }

    public void setNotaFolioNota(String notaFolioNota) {
        this.notaFolioNota = notaFolioNota;
    }

    public String getCursadaStatus() {
        return cursadaStatus;
    }

    public void setCursadaStatus(String cursadaStatus) {
        this.cursadaStatus = cursadaStatus;
    }

    public String getNotaObservaciones() {
        return notaObservaciones;
    }

    public void setNotaObservaciones(String notaObservaciones) {
        this.notaObservaciones = notaObservaciones;
    }

    public String getNotaUsuario() {
        return notaUsuario;
    }

    public void setNotaUsuario(String notaUsuario) {
        this.notaUsuario = notaUsuario;
    }

    public Double getPrimerParcial() {
        return primerParcial;
    }

    public void setPrimerParcial(Double primerParcial) {
        this.primerParcial = primerParcial;
    }

    public Double getRecuperatorio1() {
        return recuperatorio1;
    }

    public void setRecuperatorio1(Double recuperatorio1) {
        this.recuperatorio1 = recuperatorio1;
    }

    public Double getSegundoParcial() {
        return segundoParcial;
    }

    public void setSegundoParcial(Double segundoParcial) {
        this.segundoParcial = segundoParcial;
    }

    public Double getRecuperatorio2() {
        return recuperatorio2;
    }

    public void setRecuperatorio2(Double recuperatorio2) {
        this.recuperatorio2 = recuperatorio2;
    }

    public Double getTrabajosPracticos() {
        return trabajosPracticos;
    }

    public void setTrabajosPracticos(Double trabajosPracticos) {
        this.trabajosPracticos = trabajosPracticos;
    }

    public Double getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(Double asistencia) {
        this.asistencia = asistencia;
    }

    public Double getColoquio() {
        return coloquio;
    }

    public void setColoquio(Double coloquio) {
        this.coloquio = coloquio;
    }

    public Double getTrabajoInstitucional() {
        return trabajoInstitucional;
    }

    public void setTrabajoInstitucional(Double trabajoInstitucional) {
        this.trabajoInstitucional = trabajoInstitucional;
    }
}