package com.example.iesiback.dto;

public interface MateriaDTO {
    String getMateriaId();
    String getMateriaNombre();
    String getMateriaOrden();  // Cambiado a String
    String getMateriaNivel();
    String getMateriaRegimen();
    String getMateriaModalidad();
    String getMateriaCursada();
    String getMateriaExamen();
    String getCatedras();
    String getDivision();
    String getCarreraId();   // 👈 nuevo
}
