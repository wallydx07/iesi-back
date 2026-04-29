package com.example.iesiback.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ActaCursadaDTO {
    private Integer id;
    private String materiaNombre;
    private Integer materiaOrden;
    private String folio;
    private String libro;
    private LocalDate fecha;
    private Boolean firma;
    private Long docenteDni;
    private String carreraId;
    private Integer carreraYear;
    private String apellidoDocente;
    private String nombreDocente;
    private String materiaModalidad;
    private String materiaRegimen;
    private String materiaId;
    private String division;

    public ActaCursadaDTO(Integer id, String materiaNombre, Integer materiaOrden,
                          String folio, String libro, LocalDate fecha, Boolean firma, Long docenteDni,
                          String carreraId, Integer carreraYear, String apellidoDocente, String nombreDocente,
                          String materiaModalidad, String materiaRegimen, String materiaId, String division) {


        this.id = id;
        this.materiaNombre = materiaNombre;
        this.materiaOrden = materiaOrden;
        this.folio = folio;
        this.libro = libro;
        this.fecha = fecha;
        this.firma = firma;
        this.docenteDni = docenteDni;
        this.carreraId = carreraId;
        this.carreraYear = carreraYear;
        this.apellidoDocente = apellidoDocente;
        this.nombreDocente = nombreDocente;
        this.materiaModalidad = materiaModalidad;
        this.materiaRegimen = materiaRegimen;
        this.materiaId=materiaId;
        this.division=division;


    }
}
