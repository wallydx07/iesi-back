package com.example.iesiback.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

@Service
public interface CertificadoService {


    PDDocument generaRegular(String dniId, String carreraid, String autoridades, String curso);
    PDDocument generaAnalitico(String legajoId, String accion, String autoridades);
}
