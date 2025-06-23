package com.example.iesiback.services;

import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.entities.Materia;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

@Service
public interface CertificadoService {


    PDDocument generaRegular(String dniId, String carreraid, String autoridades, String curso);
    PDDocument generaAnalitico(String legajoId, String accion, String autoridades);
    PDDocument generaExamen(Materia materia, String carrera, CursadaExamen cursadaExamen, String modalidad);
    PDDocument generaTramite(String carreraId, String alumnoDNI, String Autoridades);
    PDDocument generaCertificadoAsistencia(String alumnoDNI, String legajoId, String autoridades, String curso, String entrada, String salida, String fecT, String accion);
    PDDocument generaFichaActualizacion(String legajoId) throws IOException;
    PDDocument generaPlanillaTutores(String carreraId, String estado, String ape);
    PDDocument generaPlanillaAsistencia(Long id);
    PDDocument generaPlanilla(String carreraId, String materiaId, Boolean inscripto);
    byte[] generarCredencialEstudiantil(String legajoId);


    PDDocument generaAsistenciaSalidaCampo(String legajoId, String autoridades, String fechaSeleccionada, String curso, String accion, String lugar);

    PDDocument generaAsistenciaParcial(String legajoId, String autoridades, String curso, String fechaSeleccionada,
                                       String accion, String entrada, String salida, String materia);

    ByteArrayInputStream generaPlanillaExcel(String carreraId, String materiaId, Boolean inscripto);

    PDDocument generaAsistenciaExamenFinal(String legajoId, String autoridades, String curso, String entrada, String salida,
                                           String fechaSeleccionada, String accion, String materia);

    PDDocument crearPDFPorMes(String mes);

    PDDocument crearPDFPorFecha(LocalDate fechaInicio, LocalDate fechaFin);

    PDDocument generaPlanillaSeguimiento(String carreraId, String materiaId, Boolean inscripto);
}
