package com.example.iesiback.dto;

import java.util.List;

public class MesaExamenDTO {
    private Long id;
    private String fecha;
    private String carrera;
    private String materia;
    private String anioMateria;
    private String llamado;
    private PersonaDTO presidente;
    private PersonaDTO vocal1;
    private PersonaDTO vocal2;
    private String encargadoMesa;
    private String turno;
    private String libro;
    private String folio;
    private List<NotaExamenDTO> alumnos; // lista de alumnos con su info académica

}
