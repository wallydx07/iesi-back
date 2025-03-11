package com.example.iesiback.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

public class MateriaCarreraDTO {
    private Long id;
    private String libro;
    private String folio;
    private LocalDate fecha;
    private Boolean firma;
    private Long fmcDocente;
    private String division;
    private String turno;
    private String dia;
    private String inicio;
    private String fin;
    private String materiaId;
    private String carreraId;

    public MateriaCarreraDTO(
            Long fmcDocente,
            String libro,
            String folio,
            LocalDate fecha,
            Boolean firma,
            Long id,
            String division,
            String turno,
            String dia,
            String inicio,
            String fin,
            String materiaId,
            String carreraId
    ) {
        this.dia = dia;
        this.division = division;
        this.fecha = fecha;
        this.fin = fin;
        this.firma = firma;
        this.fmcDocente = fmcDocente;
        this.id = id;
        this.folio = folio;
        this.inicio = inicio;
        this.libro = libro;
        this.turno = turno;
        this.materiaId = materiaId;
        this.carreraId = carreraId;
    }



}
