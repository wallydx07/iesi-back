package com.example.iesiback.repositories;

import com.example.iesiback.entities.ExamenHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamenHorarioRepository extends JpaRepository<ExamenHorario, Integer> {
    List<ExamenHorario> findByTurno_TurnoId(String turnoId);
    Optional<ExamenHorario> findByMateriaIdAndTurno_TurnoId(String materiaId, String turnoId);


}