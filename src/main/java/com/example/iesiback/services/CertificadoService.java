package com.example.iesiback.services;

import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.entities.Materia;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

@Service
public interface CertificadoService {


    PDDocument generaRegular(String dniId, String carreraid, String autoridades, String curso);
    PDDocument generaAnalitico(String legajoId, String accion, String autoridades);
    PDDocument generaExamen(Materia materia, String carrera, CursadaExamen cursadaExamen, String modalidad);
}
