package com.example.iesiback.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "constancia_precios")
public class ConstanciaPrecio {
    @Id
    @ColumnDefault("nextval('constancia_precios_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotNull
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull
    @Column(name = "precio_mp", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMp;

//    ALTER TABLE constancia_precios
//    ADD COLUMN precio_mp NUMERIC(10,2) DEFAULT 0 NOT NULL;

    @Column(name = "descuento")
    private Double descuento;

    @Column(name = "icono", length = 100)
    private String icono;

    @Column(name = "tipo", length = 30)
    private String tipo;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getPrecioMp() {
        return precioMp;
    }

    public void setPrecioMp(BigDecimal precioMp) {
        this.precioMp = precioMp;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }
}