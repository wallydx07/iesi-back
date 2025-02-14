package com.example.iesiback.dto;

import java.time.LocalDate;

public class InscripcionExamenDTO {
    private Long cursadaId;
    private Long materiaId;
    public  String materia_orden;
    private String materiaNombre;
    private String condicion;
    private String fecha;
    private boolean inscripto;
    private String correlativas;
    private String curso;
    private String fechaHoraMesa;
    private String turno_id;

    public InscripcionExamenDTO(Integer integer, Integer integer1, String s, String s1, String s2, String s3, String s4, String s5, String s6, LocalDate fechaNota, String s7, String s8, String s9) {
    }
}
