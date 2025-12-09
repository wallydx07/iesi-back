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


    @Override
    public CursadaExamen actualizar(Long id, CursadaExamen dto) {

        CursadaExamen e = cursadaExamenRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("No encontrado id: " + id));

        // Actualizar campos simples
        e.setVocal1Dni(dto.getVocal1Dni());
        e.setVocal2Dni(dto.getVocal2Dni());
        e.setLibro(dto.getLibro());
        e.setFolio(dto.getFolio());
        e.setFirma(dto.getFirma());
        e.setDocenteDni(dto.getDocenteDni());
        e.setMateriaId(dto.getMateriaId());

        // Fecha
        if (dto.getFecha() != null) {
            e.setFecha(LocalDate.parse(dto.getFecha().toString()));
        }

        // Hora
        if (dto.getHora() != null) {
            e.setHora(dto.getHora());
        }

        // Turno (ManyToOne)
        if (dto.getTurno() != null && dto.getTurno().getTurnoId() != null) {
            Turno turno = turnoService.findById(dto.getTurno().getTurnoId())
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
            e.setTurno(turno);
        }

        return cursadaExamenRepository.save(e);
    }

    @Override
    public Optional<CursadaExamen> findById(Integer cursadaExamenId) {
        return cursadaExamenRepository.findById(cursadaExamenId);
    }
}