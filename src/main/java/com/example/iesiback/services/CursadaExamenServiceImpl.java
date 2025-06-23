package com.example.iesiback.services;

import com.example.iesiback.dto.ExamenCursadaDTO;
import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.entities.Turno;
import com.example.iesiback.repositories.CursadaExamenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursadaExamenServiceImpl implements CursadaExamenService {


    private final TurnoService turnoService;
    @Autowired
    private CursadaExamenRepository cursadaExamenRepository;

    @Autowired
    public CursadaExamenServiceImpl(TurnoService turnoService) {
        this.turnoService = turnoService;
    }


    @Override
    public boolean existePorTurnoYMateria(String turnoId, String materiaId) {
        return cursadaExamenRepository.existsByTurno_TurnoIdAndMateriaId(turnoId, materiaId);
    }

    @Override
    public String obtenerFechaPorMateriaYTurno(String materiaId, String turnoId) {
        Optional<LocalDate> fechaOpt = cursadaExamenRepository.findFechaByMateriaIdAndTurnoId(materiaId, turnoId);
        return fechaOpt.map(LocalDate::toString).orElse("No asignado");
    }

    @Override
    public String obtenerHoraPorMateriaYTurno(String materiaId, String turnoId) {
        Optional<String> horaOpt = cursadaExamenRepository.findHoraByMateriaIdAndTurnoId(materiaId, turnoId);
        return horaOpt.orElse("No asignada");
    }

    public List<CursadaExamen> obtenerTodasLasCursadas() {
        return cursadaExamenRepository.findAll();
    }

    @Override
    public List<ExamenCursadaDTO> obtenerCursadasPorTurno(String turnoId) {
        List<Object[]> resultados = cursadaExamenRepository.findByTurno(turnoId);
        return resultados.stream().map(obj -> new ExamenCursadaDTO(
                Objects.toString(obj[0], null),                               // cursadaExamenId
                obj[1] != null ? LocalDate.parse(obj[1].toString()) : null,   // fecha
                Objects.toString(obj[2], null),                               // hora
                Objects.toString(obj[3], null),                               // libro
                Objects.toString(obj[4], null),                               // folio
                obj[5] != null && Boolean.parseBoolean(obj[5].toString()),    // firma (booleano)
                Objects.toString(obj[6], null),                               // docente_dni
                Objects.toString(obj[7], null),                               // materiaId
                Objects.toString(obj[8], null),                               // materiaNombre
                Objects.toString(obj[9], null),                              // carreraNombre
                Objects.toString(obj[10], null)                                // carreraNombre
        )).collect(Collectors.toList());
    }

    @Override
    public Optional<CursadaExamen> obtenerPorId(Integer id) {
        return cursadaExamenRepository.findById(id);
    }

    @Override
    public CursadaExamen crearCursadaExamen(String turnoId, String materiaId, LocalDate fecha, String hora) {
        Turno turno = this.turnoService.obtenerTurnoPorId(turnoId);
        Optional<CursadaExamen> existente = cursadaExamenRepository.findByTurnoAndMateriaId(turno, materiaId);

        if (existente.isPresent()) {
            throw new RuntimeException("Ya existe un CursadaExamen para el turno " + turnoId + " y materia " + materiaId);
        }

        CursadaExamen cursadaExamen = new CursadaExamen();
        cursadaExamen.setTurno(turno);
        cursadaExamen.setMateriaId(materiaId);
        cursadaExamen.setFecha(fecha);
        cursadaExamen.setHora(hora); // ✅ acá seteás la hora como String

        return cursadaExamenRepository.save(cursadaExamen);
    }


}
