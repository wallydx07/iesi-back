package com.example.iesiback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConciliacionResultadoDTO {
    private ConciliacionResumenDTO resumen;
    private List<ConciliacionItemDTO> items;
}