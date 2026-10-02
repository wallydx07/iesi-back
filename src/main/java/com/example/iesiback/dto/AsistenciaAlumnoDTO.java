package com.example.iesiback.dto;

import com.example.iesiback.enums.EstadoAsistencia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaAlumnoDTO {
    private Integer asistenciaAlumnoId;
    private Integer informeId;
    private String legajoId;
    private EstadoAsistencia estado;
}