package com.example.iesiback.services;

import com.example.iesiback.entities.ExamenHorario;
import com.example.iesiback.entities.Turno;
import com.example.iesiback.repositories.ExamenHorarioRepository;
import com.example.iesiback.repositories.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExamenHorarioServiceImpl implements ExamenHorarioService {

    @Autowired
    private ExamenHorarioRepository examenHorarioRepository;

    @Autowired
    private TurnoRepository turnoRepository;

    @Override
    public List<ExamenHorario> findAll() {
        return examenHorarioRepository.findAll();
    }

    @Override
    public ExamenHorario findById(Integer id) {
        return examenHorarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examen horario no encontrado"));
    }

    @Override
    public ExamenHorario save(ExamenHorario examenHorario) {
        return examenHorarioRepository.save(examenHorario);
    }

    @Override
    public ExamenHorario update(Integer id, ExamenHorario examenHorario) {
        ExamenHorario existente = findById(id);
        existente.setFecha(examenHorario.getFecha());
        existente.setHora(examenHorario.getHora());
        existente.setMateriaId(examenHorario.getMateriaId());
        existente.setTurno(examenHorario.getTurno());
        return examenHorarioRepository.save(existente);
    }

    @Override
    public void delete(Integer id) {
        examenHorarioRepository.deleteById(id);
    }

    @Override
    public List<ExamenHorario> findByTurnoId(String turnoId) {
        return examenHorarioRepository.findByTurno_TurnoId(turnoId);
    }

    @Override
    public void procesarCsv(MultipartFile file, String turnoId) {
        try {
            Turno turno = turnoRepository.findById(turnoId)
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

            List<ExamenHorario> horarios = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // Saltar encabezado si viene

                String[] columns = line.split(",");
                if (columns.length >= 3) {
                    ExamenHorario horario = new ExamenHorario();
                    horario.setTurno(turno);
                    horario.setFecha(LocalDate.parse(columns[0].trim())); // yyyy-MM-dd
                    horario.setHora(LocalTime.parse(columns[1].trim()));  // HH:mm
                    horario.setMateriaId(columns[2].trim());

                    horarios.add(horario);
                }
            }

            examenHorarioRepository.saveAll(horarios);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el archivo CSV.", e);
        }
    }


    @Override
    public Optional<ExamenHorario> findByMateriaIdAndTurnoId(String materiaId, String turnoId) {
        return examenHorarioRepository.findByMateriaIdAndTurno_TurnoId(materiaId, turnoId);
    }
}