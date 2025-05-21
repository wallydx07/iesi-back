package com.example.iesiback.dto;

public class HorarioDTO {
    private Integer id;
    private String dia;
    private String entrada;
    private String salida;
    private String materiaNombre;
    private String carreraId;
    private Integer razonId; // ID de materia_carrera

    // Constructor
    public HorarioDTO(Integer id, String dia, String entrada, String salida,
                      String materiaNombre, String carreraId, Integer razonId) {
        this.id = id;
        this.dia = dia;
        this.entrada = entrada;
        this.salida = salida;
        this.materiaNombre = materiaNombre;
        this.carreraId = carreraId;
        this.razonId = razonId;
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

    public String getEntrada() {
        return entrada;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
    }

    public String getSalida() {
        return salida;
    }

    public void setSalida(String salida) {
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
