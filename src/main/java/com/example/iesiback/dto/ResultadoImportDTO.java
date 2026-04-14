package com.example.iesiback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoImportDTO {

    private int filasExitosas;
    private List<String> errores;

    public int getTotalErrores() {
        return errores != null ? errores.size() : 0;
    }

    public boolean tieneErrores() {
        return getTotalErrores() > 0;
    }
}