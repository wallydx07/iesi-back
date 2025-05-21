package com.example.iesiback.repositories;

import com.example.iesiback.dto.ExamenCursadaDTO;
import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.entities.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface CursadaExamenRepository extends JpaRepository<CursadaExamen, Integer> {


    @Query("SELECT c.fecha FROM CursadaExamen c WHERE c.materiaId = :materiaId AND c.turno.turnoId = :turnoId")
    Optional<LocalDate> findFechaByMateriaIdAndTurnoId(@Param("materiaId") String materiaId, @Param("turnoId") String turnoId);

    @Query("SELECT c.hora FROM CursadaExamen c WHERE c.materiaId = :materiaId AND c.turno.turnoId = :turnoId")
    Optional<String> findHoraByMateriaIdAndTurnoId(@Param("materiaId") String materiaId, @Param("turnoId") String turnoId);

    boolean existsByTurno_TurnoIdAndMateriaId(String turnoId, String materiaId);

    Optional<CursadaExamen> findByMateriaIdAndTurno_TurnoId(String materiaId, String turnoId);

    /* @Query(value = """
         SELECT ce.cursada_examen_id, m.materia_id, m.materia_nombre
         FROM cursada_examen ce
         INNER JOIN materia m ON ce.materia_id = m.materia_id
         WHERE ce.turno_id = :turnoId
         ORDER BY m.materia_nombre ASC
     """, nativeQuery = true)*/
    @Query(value = """
    SELECT 
        ce.cursada_examen_id, 
        m.materia_id, 
        m.materia_nombre,
        (SELECT c.carrera_nombre 
         FROM carrera c 
         JOIN materia_carrera mc ON c.carrera_id = mc.carrera_id 
         WHERE mc.materia_id = ce.materia_id 
         LIMIT 1) AS carrera_nombre
    FROM cursada_examen ce
    INNER JOIN materia m ON ce.materia_id = m.materia_id
    WHERE ce.turno_id = :turnoId
    GROUP BY ce.cursada_examen_id, m.materia_id, m.materia_nombre
    ORDER BY m.materia_nombre ASC
""", nativeQuery = true)
    List<Object[]> findByTurno(@Param("turnoId") String turnoId);

    Optional<CursadaExamen> findByTurnoAndMateriaId(Turno turno, String materiaId);


}