package com.example.iesiback.dto;

import java.time.LocalTime;

public class PersonalHorarioDTO {

    private Integer id;
    private Long personalDni; // 🔸 Era Long, no String
    private String personalApellido;
    private String personalNombre;
    private Integer mcId;
    private String carreraId;
    private String materiaNombre;
    private String dia;
    private LocalTime entrada; // 🔸 LocalTime
    private LocalTime salida;  // 🔸 LocalTime
    private String materiaRegimen;

    public PersonalHorarioDTO(Integer id, Long personalDni, String personalApellido, String personalNombre,
                              Integer mcId, String carreraId, String materiaNombre, String dia,
                              LocalTime entrada, LocalTime salida, String materiaRegimen) {
        this.id = id;
        this.personalDni = personalDni;
        this.personalApellido = personalApellido;
        this.personalNombre = personalNombre;
        this.mcId = mcId;
        this.carreraId = carreraId;
        this.materiaNombre = materiaNombre;
        this.dia = dia;
        this.entrada = entrada;
        this.salida = salida;
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

    public Long getPersonalDni() {
        return personalDni;
    }

    public void setPersonalDni(Long personalDni) {
        this.personalDni = personalDni;
    }

    public String getPersonalApellido() {
        return personalApellido;
    }

    public void setPersonalApellido(String personalApellido) {
        this.personalApellido = personalApellido;
    }

    public String getPersonalNombre() {
        return personalNombre;
    }

    public void setPersonalNombre(String personalNombre) {
        this.personalNombre = personalNombre;
    }

    public Integer getMcId() {
        return mcId;
    }

    public void setMcId(Integer mcId) {
        this.mcId = mcId;
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
}
