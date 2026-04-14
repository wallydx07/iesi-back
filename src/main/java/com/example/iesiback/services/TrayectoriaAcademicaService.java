package com.example.iesiback.services;

import com.example.iesiback.dto.NotaImportDTO;
import com.example.iesiback.dto.ResultadoImportDTO;
import java.util.List;

public interface TrayectoriaAcademicaService {

    ResultadoImportDTO procesarNotas(List<NotaImportDTO> notas);
}