package com.example.iesiback.dto;

public class CatedraDTO {

    private Integer id;
    private String materiaId;
    private String materiaNombre;
    private String carreraNombre;
    private String carreraId;
    private String division;
    private String turno;
    private String dia;
    private String inicio;
    private String fin;
    private Integer catedras;

    // Constructor
    public CatedraDTO(Integer id, String materiaId, String materiaNombre, String carreraId, String division,
                      String turno, String dia, String inicio, String fin, Integer catedras, String carreraNombre) {
        this.id = id;
        this.materiaId = materiaId;
        this.materiaNombre = materiaNombre;
        this.carreraId = carreraId;
        this.division = division;
        this.turno = turno;
        this.dia = dia;
        this.inicio = inicio;
        this.fin = fin;
        this.catedras = catedras;
        this.carreraNombre = carreraNombre;
    }

    public String getCarreraNombre() {
        return carreraNombre;
    }

    public void setCarreraNombre(String carreraNombre) {
        this.carreraNombre = carreraNombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(String materiaId) {
        this.materiaId = materiaId;
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

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public Integer getCatedras() {
        return catedras;
    }

    public void setCatedras(Integer catedras) {
        this.catedras = catedras;
    }
}
