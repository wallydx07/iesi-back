package com.example.iesiback.services;

import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.entities.Materia;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface CertificadoService {


    PDDocument generaRegular(String dniId, String carreraid, String autoridades, String curso);
    PDDocument generaAnalitico(String legajoId, String accion, String autoridades);
    PDDocument generaExamen(Materia materia, String carrera, CursadaExamen cursadaExamen, String modalidad);
    PDDocument generaTramite(String carreraId, String alumnoDNI, String Autoridades);
    PDDocument generaCertificadoAsistencia(String alumnoDNI, String legajoId, String autoridades, String curso, String entrada, String salida, String fecT, String accion);

    PDDocument generaFichaActualizacion(String legajoId) throws IOException;

    PDDocument generaPlanillaTutores(String carreraId, String estado, String ape);
}
