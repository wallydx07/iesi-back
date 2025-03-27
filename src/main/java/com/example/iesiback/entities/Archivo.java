package com.example.iesiback.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "archivo")
public class Archivo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "archivo_id_gen")
    @SequenceGenerator(name = "archivo_id_gen", sequenceName = "archivo_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "nombre")
    private String nombre;

    @Size(max = 50)
    @Column(name = "etiqueta", length = 50)
    private String etiqueta;

    @Column(name = "dato")
    private byte[] dato;

}