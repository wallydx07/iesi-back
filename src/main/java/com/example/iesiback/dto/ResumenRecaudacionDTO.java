package com.example.iesiback.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ResumenRecaudacionDTO {
    private BigDecimal totalRecaudado;
    private Integer totalOperaciones;
    private Integer totalValidados;
    private Integer totalPendientes;

    private List<ReciboDTO> recibos;
    private List<ConceptoDTO> porConcepto;

    public ResumenRecaudacionDTO(BigDecimal totalRecaudado, Integer totalOperaciones, Integer totalValidados, Integer totalPendientes, List<ReciboDTO> recibos, List<ConceptoDTO> porConcepto) {
        this.totalRecaudado = totalRecaudado;
        this.totalOperaciones = totalOperaciones;
        this.totalValidados = totalValidados;
        this.totalPendientes = totalPendientes;
        this.recibos = recibos;
        this.porConcepto = porConcepto;
    }

    public BigDecimal getTotalRecaudado() {
        return totalRecaudado;
    }

    public void setTotalRecaudado(BigDecimal totalRecaudado) {
        this.totalRecaudado = totalRecaudado;
    }

    public Integer getTotalOperaciones() {
        return totalOperaciones;
    }

    public void setTotalOperaciones(Integer totalOperaciones) {
        this.totalOperaciones = totalOperaciones;
    }

    public Integer getTotalPendientes() {
        return totalPendientes;
    }

    public void setTotalPendientes(Integer totalPendientes) {
        this.totalPendientes = totalPendientes;
    }

    public Integer getTotalValidados() {
        return totalValidados;
    }

    public void setTotalValidados(Integer totalValidados) {
        this.totalValidados = totalValidados;
    }

    public List<ReciboDTO> getRecibos() {
        return recibos;
    }

    public void setRecibos(List<ReciboDTO> recibos) {
        this.recibos = recibos;
    }

    public List<ConceptoDTO> getPorConcepto() {
        return porConcepto;
    }

    public void setPorConcepto(List<ConceptoDTO> porConcepto) {
        this.porConcepto = porConcepto;
    }
}