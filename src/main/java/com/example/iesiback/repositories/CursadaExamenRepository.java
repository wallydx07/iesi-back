package com.example.iesiback.repositories;

import com.example.iesiback.entities.CursadaExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface CursadaExamenRepository extends JpaRepository<CursadaExamen, Integer> {


    @Query("SELECT c.fecha FROM CursadaExamen c WHERE c.materiaId = :materiaId AND c.turno.turnoId = :turnoId")
    Optional<LocalDate> findFechaByMateriaIdAndTurnoId(@Param("materiaId") String materiaId, @Param("turnoId") String turnoId);

    boolean existsByTurno_TurnoIdAndMateriaId(String turnoId, String materiaId);

    Optional<CursadaExamen> findByMateriaIdAndTurno_TurnoId(String materiaId, String turnoId);
}
