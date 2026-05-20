package com.example.iesiback.dto;

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
    private Double notaCalificacionNotaNumero;
    private String notaCalificacionNotaLetra;
    private String notaEstado;
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
}