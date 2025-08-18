package com.example.iesiback.services;

import com.example.iesiback.entities.ExamenHorario;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ExamenHorarioService {
    List<ExamenHorario> findAll();

    ExamenHorario findById(Integer id);

    ExamenHorario save(ExamenHorario examenHorario);

    ExamenHorario update(Integer id, ExamenHorario examenHorario);

    void delete(Integer id);

    List<ExamenHorario> findByTurnoId(String turnoId);

    void procesarCsv(MultipartFile file, String turnoId);

    Optional<ExamenHorario> findByMateriaIdAndTurnoId(String materiaId, String turnoId);
}
