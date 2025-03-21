package com.example.iesiback.dto;

public class AlumnoExamenDTO {

    private Long legajoId;
    private String alumnoDni;
    private String alumnoApellido;
    private String alumnoNombre;

    // Constructor
    public AlumnoExamenDTO(Long legajoId, String alumnoDni, String alumnoApellido, String alumnoNombre) {
        this.legajoId = legajoId;
        this.alumnoDni = alumnoDni;
        this.alumnoApellido = alumnoApellido;
        this.alumnoNombre = alumnoNombre;
    }

    // Getters y setters
    public Long getLegajoId() {
        return legajoId;
    }

    public void setLegajoId(Long legajoId) {
        this.legajoId = legajoId;
    }

    public String getAlumnoDni() {
        return alumnoDni;
    }

    public void setAlumnoDni(String alumnoDni) {
        this.alumnoDni = alumnoDni;
    }

    public String getAlumnoApellido() {
        return alumnoApellido;
    }

    public void setAlumnoApellido(String alumnoApellido) {
        this.alumnoApellido = alumnoApellido;
    }

    public String getAlumnoNombre() {
        return alumnoNombre;
    }

    public void setAlumnoNombre(String alumnoNombre) {
        this.alumnoNombre = alumnoNombre;
    }

    @Override
    public String toString() {
        return "AlumnoDTO{" +
                "legajoId=" + legajoId +
                ", alumnoDni='" + alumnoDni + '\'' +
                ", alumnoApellido='" + alumnoApellido + '\'' +
                ", alumnoNombre='" + alumnoNombre + '\'' +
                '}';
    }
}

