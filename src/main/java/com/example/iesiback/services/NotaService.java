package com.example.iesiback.services;

import com.example.iesiback.dto.*;
import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Examen;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface NotaService {
    List<Nota> obtenerNotas();
    List<NotaMateriaDTO> obtenerTodasNotasPorLegajo(String legajoId, String condicion);
    List<NotaMateriaDTO> obtenerTodasNotasPorLegajoSinCorrelativas(String legajoId);
    List<NotaMateriaDTO> obtenerNotasNoAprobadasCursadas(String legajoId);
    List<NotaMateriaDTO> obtenerNotasNoAprobadasPorLegajo(String legajoId); //
    List<NotaCursadaDTO> findNotasByCarreraAndMateria(String carreraId, String materiaId, String division, boolean cursadaInscripto);
//    List<NotaCursadaDTO> findNotasByCarreraAndMateriaNew(String carreraId, String materaId, boolean cursadaInscripto);
    List<NotaCursadaDTO> findNotasByCarreraAndMateriaAll(String carreraId, String materaId, String division, boolean cursadaInscripto);
    List<NotaExamenDTO> findExamenesByCursadaExamenIdMateriaCarrera(Long  cursadaExamenId, Boolean soloInscritos);
    boolean isMateriaAprobada(String legajoId, String materiaId);
    Nota guardarNota(Nota nota);
    Nota obtenerNotaPorId(Long notaId);
    Nota actualizarNota(Long id, Nota nota) throws ResourceNotFoundException;
    List<NotaMateriaDTO> obtenerTodasNotasPorLegajoAnalitico(String legajoId);

    List<NotaMateriaDTO> obtenerTodasNotasPorLegajoAnaliticoSINREF(String legajoId);
    List<NotaExamenDTO> obtenerNotasPorCondicion(Long cursadaExamenId, boolean examenInscripto, String notaCondicion);
    @Transactional
    void eliminarNota(Long id);
    @Transactional
    void eliminarNotaIndividual(Long id);
    NotaMateriaDTO obtenerUltimaNota(String legajoId);
    Cursada obtenerCursadaPorNotaId(Long notaId);
    EvaluacionCorrelativaResponse evaluarCorrelativaIndividual(String legajoId, Integer materiaOrden,  String condicion);
    EvaluacionCorrelativaResponse evaluarCorrelativaNotaId(Long  notaId, String condicion);
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
}
