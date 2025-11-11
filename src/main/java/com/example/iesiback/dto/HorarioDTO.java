package com.example.iesiback.dto;

import java.time.LocalTime;

public class HorarioDTO {
    private Integer id;
    private String dia;
    private LocalTime entrada;
    private LocalTime salida;
    private String materiaNombre;
    private String carreraId;
    private Integer razonId; // ID de materia_carrera
    private String materiaRegimen; // ID de materia_carrera


    // Constructor
    public HorarioDTO(Integer id, String dia, LocalTime entrada, LocalTime salida,
                      String materiaNombre, String carreraId, Integer razonId, String materiaRegimen) {
        this.id = id;
        this.dia = dia;
        this.entrada = entrada;
        this.salida = salida;
        this.materiaNombre = materiaNombre;
        this.carreraId = carreraId;
        this.razonId = razonId;
        this.materiaRegimen = materiaRegimen;
    }

    public String getMateriaRegimen() {
        return materiaRegimen;
    }

    public void setMateriaRegimen(String materiaRegimen) {
        this.materiaRegimen = materiaRegimen;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public LocalTime getEntrada() {
        return entrada;
    }

    public void setEntrada(LocalTime entrada) {
        this.entrada = entrada;
    }

    public LocalTime getSalida() {
        return salida;
    }

    public void setSalida(LocalTime salida) {
        this.salida = salida;
    }

    public String getMateriaNombre() {
        return materiaNombre;
    }

    public void setMateriaNombre(String materiaNombre) {
        this.materiaNombre = materiaNombre;
    }

    public String getCarreraId() {
        return carreraId;
    }

    public void setCarreraId(String carreraId) {
        this.carreraId = carreraId;
    }

    public Integer getRazonId() {
        return razonId;
    }

    public void setRazonId(Integer razonId) {
        this.razonId = razonId;
    }
// Getters y Setters
}
