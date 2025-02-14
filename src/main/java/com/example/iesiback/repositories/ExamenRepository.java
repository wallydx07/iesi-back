package com.example.iesiback.repositories;

import com.example.iesiback.entities.Examen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamenRepository extends JpaRepository<Examen, Long> {

    @Query("SELECT e FROM Examen e WHERE e.permiso.permisoLegajoId = :legajoId AND e.cursadaExamen.turno = :turno")
    List<Examen> findByLegajoIdAndTurno(@Param("legajoId") String legajoId, @Param("turno") String turno);

    @Query("SELECT COUNT(e) > 0 FROM Examen e " +
            "JOIN e.permiso p " +
            "JOIN e.cursadaExamen ce " +
            "WHERE ce.turno = :turno " +
            "AND ce.materiaId = :materiaId " +
            "AND p.permisoLegajoId = :legajoId")
    boolean existsInscripcion(@Param("turno") String turno,
                              @Param("materiaId") String materiaId,
                              @Param("legajoId") String legajoId);

    @Query("SELECT e.examenInscripto FROM Examen e " +
            "JOIN e.permiso p " +
            "JOIN e.cursadaExamen ce " +
            "WHERE ce.turno = :turno " +
            "AND ce.materiaId = :materiaId " +
            "AND p.permisoLegajoId = :legajoId")
    boolean getEstadoExamen(@Param("turno") String turno,
                            @Param("materiaId") String materiaId,
                            @Param("legajoId") String legajoId);

    @Modifying
    @Query("UPDATE Examen e SET e.examenInscripto = true WHERE e.id = :examenId")
    void marcarPermisoExamen(@Param("examenId") Long examenId);
}
