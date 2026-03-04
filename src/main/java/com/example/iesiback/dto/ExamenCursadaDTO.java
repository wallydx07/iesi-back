package com.example.iesiback.dto;

import com.example.iesiback.entities.Persona;
import com.google.inject.spi.PrivateElements;

import java.time.LocalDate;
import java.util.List;

public class ExamenCursadaDTO {

    private String cursadaExamenId;
    private LocalDate fecha;
    private String hora;
    private String libro;
    private String folio;
    private boolean firma;
    private PersonaDTO titular;
    private String materiaId;
    private String materiaNombre;
    private String materiaNivel;
    private String carreraNombre;
    private List<NotaExamenDTO> notaExamenDTO;
    private PersonaDTO vocal1;
    private PersonaDTO vocal2;



    public ExamenCursadaDTO(
            String cursada_examen_id, LocalDate fecha, String hora, String libro, String folio, boolean firma,
            PersonaDTO titular, String materia_id, String materia_nombre, String materia_nivel, String carreraNombre, PersonaDTO vocal1, PersonaDTO vocal2

            ) {
        this.cursadaExamenId = cursada_examen_id;
        this.fecha = fecha;
        this.hora = hora;
        this.libro = libro;
        this.folio = folio;
        this.firma = firma;
        this.titular = titular;
        this.materiaId = materia_id;
        this.materiaNombre = materia_nombre;
        this.materiaNivel = materia_nivel;
        this.carreraNombre=carreraNombre;
        this.vocal1 = vocal1;
        this.vocal2 = vocal2;
    }

    public ExamenCursadaDTO() {
    }

    public PersonaDTO getVocal1() {
        return vocal1;
    }

    public void setVocal1(PersonaDTO vocal1) {
        this.vocal1 = vocal1;
    }

    public PersonaDTO getVocal2() {
        return vocal2;
    }

    public void setVocal2(PersonaDTO vocal2) {
        this.vocal2 = vocal2;
    }

    public PersonaDTO getTitular() {
        return titular;
    }

    public void setTitular(PersonaDTO titular) {
        this.titular = titular;
    }

    public String getCarreraNombre() {
        return carreraNombre;
    }

    public void setCarreraNombre(String carreraNombre) {
        this.carreraNombre = carreraNombre;
    }

    public String getCursadaExamenId() {
        return cursadaExamenId;
    }

    public void setCursadaExamenId(String cursadaExamenId) {
        this.cursadaExamenId = cursadaExamenId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getLibro() {
        return libro;
    }

    public void setLibro(String libro) {
        this.libro = libro;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public boolean isFirma() {
        return firma;
    }

    public void setFirma(boolean firma) {
        this.firma = firma;
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

    public String getMateriaNivel() {
        return materiaNivel;
    }

    public void setMateriaNivel(String materiaNivel) {
        this.materiaNivel = materiaNivel;
    }

    public List<NotaExamenDTO> getNotaExamenDTO() {
        return notaExamenDTO;
    }

    public void setNotaExamenDTO(List<NotaExamenDTO> notaExamenDTO) {
        this.notaExamenDTO = notaExamenDTO;
    }
}
