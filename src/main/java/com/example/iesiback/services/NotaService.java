package com.example.iesiback.services;

import com.example.iesiback.dto.*;
import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Examen;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.enums.EstadoCondicion;
import com.example.iesiback.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public interface NotaService {
    List<Nota> obtenerNotas();

    List<NotaMateriaDTO> obtenerTodasNotasPorLegajo(String legajoId, EstadoCondicion condicion);

    List<NotaMateriaDTO> obtenerTodasNotasPorLegajoSinCorrelativas(String legajoId);

    List<NotaMateriaDTO> obtenerNotasNoAprobadasCursadas(String legajoId);

    List<NotaMateriaDTO> obtenerNotasNoAprobadasPorLegajo(String legajoId); //

    List<NotaCursadaConEstadoDTO> findNotasByCarreraAndMateria(
            String carreraId, String materiaId, String division, boolean cursadaInscripto);


    List<NotaCursadaConEstadoDTO> findNotasByCarreraAndMateriaAll(String carreraId, String materaId, String division, boolean cursadaInscripto);

    List<NotaExamenDTO> findExamenesByCursadaExamenIdMateriaCarrera(Long  cursadaExamenId, Boolean soloInscritos);

    boolean isMateriaAprobada(String legajoId, String materiaId);

    Nota guardarNota(Nota nota);

    Nota obtenerNotaPorId(Long notaId);

    Nota actualizarNota(Long id, Nota nota) throws ResourceNotFoundException;

    List<NotaMateriaDTO> obtenerTodasNotasPorLegajoAnalitico(String legajoId);


    List<NotaMateriaDTO> obtenerTodasNotasPorLegajoCalificador(String legajoId);

    List<NotaExamenDTO> obtenerNotasPorCondicion(Long cursadaExamenId, boolean examenInscripto, EstadoCondicion notaCondicion);

    @Transactional
    void eliminarNota(Long id);

    @Transactional
    void eliminarNotaIndividual(Long id);

    NotaMateriaDTO obtenerUltimaNota(String legajoId);

    Cursada obtenerCursadaPorNotaId(Long notaId);

//    EvaluacionCorrelativaResponse evaluarCorrelativaIndividual(String legajoId, Integer materiaOrden,  EstadoCondicion condicion);
//    EvaluacionCorrelativaResponse evaluarCorrelativaNotaId(Long  notaId, EstadoCondicion condicion);

    void permitirEdicionMateria(String carreraId, String materiaId, boolean editable, String division);
    Nota saveNotaWithCursadsa(Nota nota, Integer cursadaId);
    NotaServiceImpl.ResultadoRegularidad evaluarRegularidad(List<Nota> notas);
    List<AlumnoCursadaMateriaNotaDTO> getAlumnosPorCarrera(String carreraId);
    @Transactional(readOnly = true)
    Nota findByCursadaId(Integer cursadaId);
    Nota obtenerExamenPorCursadaYPermiso(Integer cursadaId, Integer permisoId);
    @Transactional(readOnly = true)
    Nota findByCursadaIdMateriaCondicion(Integer cursadaId, Integer permisoId);
    Nota findExamenPorCursadaYTurnoId(Integer cursadaId, Integer turnoId);
    boolean evaluarSancion(Integer cursadaId, Integer turnoId);

    //Regularizado para cursar
    List<ProcesadoReinscripcionMateriaDTO> obtenerReinscripciones(Integer cicloLectivo, String legajoId, String division);

    ReporteAcademicoDTO generarReporteAcademico(String legajoId);

    List<Nota> updateFechaNotasByMateriaCarreraId(Integer materiaCarreraId, EstadoCondicion estadoCondicion, LocalDate fecha);
}
