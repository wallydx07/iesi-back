package com.example.iesiback.services;

import com.example.iesiback.dto.NotaImportDTO;
import java.util.List;

public interface TrayectoriaAcademicaService {

    void procesarNotas(List<NotaImportDTO> notas);
}