package com.example.iesiback.services;

import com.example.iesiback.dto.ExamenCursadaDTO;
import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.entities.Turno;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public interface CursadaExamenService {

    String obtenerFechaPorMateriaYTurno(String materiaId, String turnoId);
    boolean existePorTurnoYMateria(String turnoId, String materiaId);
    String obtenerHoraPorMateriaYTurno(String materiaId, String turnoId);
    List<CursadaExamen> obtenerTodasLasCursadas();
    List<ExamenCursadaDTO> obtenerCursadasPorTurno(String turnoId);

    List<ExamenCursadaDTO> obtenerCursadasPorTurnoFechaDesc(String turnoId);

    Optional<CursadaExamen> obtenerPorId(Integer id);
    CursadaExamen crearCursadaExamen(String turnoId, String materiaId, LocalDate fecha, String hora, Long docenteDni,Long vocal1Dni, Long vocal2Dni);
    CursadaExamen actualizar(Long id, CursadaExamen dto);
    Optional<CursadaExamen> findById(Integer cursadaExamenId);

    Optional<CursadaExamen> findByTurnoAndMateriaId(Turno t, String materiaId);


    Optional<CursadaExamen> findByTurnoIdAndMateriaId(String t, String materiaId);

    @Transactional
    CursadaExamen obtenerOCrearCursadaExamen(
            String turnoId,
            String materiaId);
}
