package com.example.iesiback.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

@Service
public interface AporteService {
     PDDocument generarReciboPDF(String libreta,String turno);
}
