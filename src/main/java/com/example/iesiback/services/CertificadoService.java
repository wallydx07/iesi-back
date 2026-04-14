package com.example.iesiback.services;

import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.entities.Materia;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

@Service
public interface CertificadoService {


    PDDocument generaRegular(String dniId, String carreraid, String autoridades, String curso);
    PDDocument generaAnalitico(String legajoId, String accion, String autoridades);
    PDDocument generaExamen(Materia materia, String carrera, CursadaExamen cursadaExamen, String modalidad);
    PDDocument generaTramite(String carreraId, String legajoId, String Autoridades);
    PDDocument generaCertificadoAsistencia(String alumnoDNI, String legajoId, String autoridades, String curso, String entrada, String salida, String fecT, String accion);
    PDDocument generaFichaActualizacion(String legajoId) throws IOException;
    PDDocument generaPlanillaTutores(String carreraId, String estado, String ape, String comision);
    PDDocument generaPlanillaAsistencia(Long id);

    PDDocument generaPlanilla(Long materiaCarreraId);

    //    PDDocument generaPlanilla(String carreraId, String materiaId, Boolean inscripto);
    byte[] generarCredencialEstudiantil(String legajoId);


    PDDocument generaFinalizacionEstudios(String legajoId, String alumnoDNI, String autoridades);

    PDDocument generaAsistenciaSalidaCampo(String legajoId, String autoridades, String fechaSeleccionada, String curso, String accion, String lugar);

    PDDocument generaAsistenciaParcial(String legajoId, String autoridades, String curso, String fechaSeleccionada,
                                       String accion, String entrada, String salida, String materia);

    PDDocument generaUltimaMateria(String legajoId, String autoridades);

    ByteArrayInputStream generaPlanillaExcel(String carreraId, String materiaId, Boolean inscripto, String division);

    PDDocument generaAsistenciaExamenFinal(String legajoId, String autoridades, String curso,
                                           String fechaSeleccionada, String accion, String materia);

    PDDocument crearPDFPorMes(LocalDate fechaInicio, LocalDate fechaFin);

    PDDocument crearPDFPorFecha(LocalDate fechaInicio, LocalDate fechaFin);

    PDDocument generaPlanillaSeguimiento(String carreraId, String materiaId, Boolean inscripto);

    PDDocument generaCalificador(String legajoId, boolean enBlanco);

    PDDocument generaTroquelTramite(String legajoId, Integer atencionId);

    PDDocument generaTroquelNotaIngresante(Integer atencionId) throws IOException;

    PDDocument generaTroquelPase(Integer paseId);

    PDDocument crearPDFPorUsuario(String dni, LocalDate fechaInicio, LocalDate fechaFin);


    PDDocument generaAsistenciaExamenFinalDocente(String dni, String autoridades, String carrera,
                                                  String fechaSeleccionada, String accion, String materia);

    PDDocument generaPermiso(String libreta, String turno, String usuarioNombre);

    void enviarPermisoPorEmail(String libreta, String turno, String usuarioNombre, String destinatario);
}
