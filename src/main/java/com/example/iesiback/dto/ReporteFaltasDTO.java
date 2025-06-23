package com.example.iesiback.dto;

public class ReporteFaltasDTO {
    private String nombreCompleto;
    private String materiaNombre;
    private String dni;
    private Integer estado0;
    private Integer estado1;
    private Integer estado2;
    private Integer estado3;
    private Integer totalCount;

    public ReporteFaltasDTO(String nombreCompleto, String materiaNombre, String dni,
                            Integer estado0, Integer estado1, Integer estado2, Integer estado3, Integer totalCount) {
        this.nombreCompleto = nombreCompleto;
        this.materiaNombre = materiaNombre;
        this.dni = dni;
        this.estado0 = estado0;
        this.estado1 = estado1;
        this.estado2 = estado2;
        this.estado3 = estado3;
        this.totalCount = totalCount;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
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

    public Integer getEstado0() {
        return estado0;
    }

    public void setEstado0(Integer estado0) {
        this.estado0 = estado0;
    }

    public Integer getEstado1() {
        return estado1;
    }

    public void setEstado1(Integer estado1) {
        this.estado1 = estado1;
    }

    public Integer getEstado2() {
        return estado2;
    }

    public void setEstado2(Integer estado2) {
        this.estado2 = estado2;
    }

    public Integer getEstado3() {
        return estado3;
    }

    public void setEstado3(Integer estado3) {
        this.estado3 = estado3;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}