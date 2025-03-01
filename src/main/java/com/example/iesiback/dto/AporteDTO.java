package com.example.iesiback.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AporteDTO {

    private Integer aporteId;
    private Long alumnoDni;
    private String alumnoApellido;
    private String alumnoNombre;
    private String legajoId;
    private Integer aporteMonto;
    private Integer aporteNroRecibo;
    private Integer aporteTalonarioRecibo;
    private LocalDate aporteFecha;
    private String aporteObs;
    private String aporteUsuario;


    // Constructor que coincida con los tipos y el orden de la consulta JPQL
    public AporteDTO(Integer aporteId,
                     Long alumnoDni,
                     String alumnoApellido,
                     String alumnoNombre,
                     String legajoId,
                     Integer aporteMonto,
                     Integer aporteNroRecibo,
                     Integer aporteTalonarioRecibo,
                     LocalDate aporteFecha,
                     String aporteObs,
                     String usuario) {
        this.aporteId = aporteId;
        this.alumnoDni = alumnoDni;
        this.alumnoApellido = alumnoApellido;
        this.alumnoNombre = alumnoNombre;
        this.legajoId = legajoId;
        this.aporteMonto = aporteMonto;
        this.aporteNroRecibo = aporteNroRecibo;
        this.aporteTalonarioRecibo = aporteTalonarioRecibo;
        this.aporteFecha = aporteFecha;
        this.aporteObs = aporteObs;
        this.aporteUsuario = usuario;
    }

    public String getAlumnoApellido() {
        return alumnoApellido;
    }

    public void setAlumnoApellido(String alumnoApellido) {
        this.alumnoApellido = alumnoApellido;
    }

    public Long getAlumnoDni() {
        return alumnoDni;
    }

    public void setAlumnoDni(Long alumnoDni) {
        this.alumnoDni = alumnoDni;
    }

    public String getAlumnoNombre() {
        return alumnoNombre;
    }

    public void setAlumnoNombre(String alumnoNombre) {
        this.alumnoNombre = alumnoNombre;
    }

    public
    LocalDate getAporteFecha() {
        return aporteFecha;
    }

    public void setAporteFecha(
            LocalDate aporteFecha) {
        this.aporteFecha = aporteFecha;
    }

    public Integer getAporteId() {
        return aporteId;
    }

    public void setAporteId(Integer aporteId) {
        this.aporteId = aporteId;
    }

    public Integer getAporteMonto() {
        return aporteMonto;
    }

    public void setAporteMonto(Integer aporteMonto) {
        this.aporteMonto = aporteMonto;
    }

    public Integer getAporteNroRecibo() {
        return aporteNroRecibo;
    }

    public void setAporteNroRecibo(Integer aporteNroRecibo) {
        this.aporteNroRecibo = aporteNroRecibo;
    }

    public String getAporteObs() {
        return aporteObs;
    }

    public void setAporteObs(String aporteObs) {
        this.aporteObs = aporteObs;
    }

    public Integer getAporteTalonarioRecibo() {
        return aporteTalonarioRecibo;
    }

    public void setAporteTalonarioRecibo(Integer aporteTalonarioRecibo) {
        this.aporteTalonarioRecibo = aporteTalonarioRecibo;
    }

    public String getAporteUsuario() {
        return aporteUsuario;
    }

    public void setAporteUsuario(String aporteUsuario) {
        this.aporteUsuario = aporteUsuario;
    }

    public String getLegajoId() {
        return legajoId;
    }

    public void setLegajoId(String legajoId) {
        this.legajoId = legajoId;
    }
}