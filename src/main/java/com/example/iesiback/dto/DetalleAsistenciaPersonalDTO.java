package com.example.iesiback.dto;


import java.time.LocalDate;

public class DetalleAsistenciaPersonalDTO {

    private Long asistenciaId;
    private Long horarioId;
    private String carreraId;
    private String materiaNombre;
    private Long dni;
    private String apellido;
    private String nombre;
    private String horaEntrada;
    private String horaSalida;
    private String observaciones;
    private Integer estado;
    private LocalDate fecha;
    public DetalleAsistenciaPersonalDTO(Long asistenciaId, Long horarioId, String carreraId, String materiaNombre,
                                        Long dni, String apellido, String nombre,
                                        String horaEntrada, String horaSalida, String observaciones, Integer estado, LocalDate fecha) {
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
        this.estado = estado;
        this.fecha=fecha;
    }


    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
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

    public String getCarreraId() {
        return carreraId;
    }

    public void setCarreraId(String carreraId) {
        this.carreraId = carreraId;
    }

    public String getMateriaNombre() {
        return materiaNombre;
    }

    public void setMateriaNombre(String materiaNombre) {
        this.materiaNombre = materiaNombre;
    }

    public Long getDni() {
        return dni;
    }

    public void setDni(Long dni) {
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
}
