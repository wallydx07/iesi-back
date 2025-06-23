package com.example.iesiback.dto;

import java.time.LocalTime;

public class AsistenciaDetalleDTO {
    private Long asistenciaId;
    private Long horarioId;
    private Long carreraId;
    private String materiaNombre;
    private String dni;
    private String apellido;
    private String nombre;
    private String horaEntrada;
    private String horaSalida;
    private String observaciones;

    // Constructor
    public AsistenciaDetalleDTO(Long asistenciaId, Long horarioId, Long carreraId, String materiaNombre,
                                String dni, String apellido, String nombre,
                                String horaEntrada, String horaSalida, String observaciones) {
        this.asistenciaId = asistenciaId;
        this.horarioId = horarioId;
        this.carreraId = carreraId;
        this.materiaNombre = materiaNombre;
        this.dni = dni;
        this.apellido = apellido;
        this.nombre = nombre;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.observaciones = observaciones;
    }

    public Long getAsistenciaId() {
        return asistenciaId;
    }

    public void setAsistenciaId(Long asistenciaId) {
        this.asistenciaId = asistenciaId;
    }

    public Long getHorarioId() {
        return horarioId;
    }

    public void setHorarioId(Long horarioId) {
        this.horarioId = horarioId;
    }

    public Long getCarreraId() {
        return carreraId;
    }

    public void setCarreraId(Long carreraId) {
        this.carreraId = carreraId;
    }

    public String getMateriaNombre() {
        return materiaNombre;
    }

    public void setMateriaNombre(String materiaNombre) {
        this.materiaNombre = materiaNombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(String horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
// Getters y Setters (puedes generar con Lombok si usás)
}
