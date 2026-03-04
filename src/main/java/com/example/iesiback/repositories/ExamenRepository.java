package com.example.iesiback.repositories;

import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.entities.Examen;
import com.example.iesiback.entities.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamenRepository extends JpaRepository<Examen, Long> {

    @Query("SELECT COUNT(e) > 0 FROM Examen e WHERE e.permiso.permisoLegajoId = :legajoId AND e.cursadaExamen.turno.turnoId = :turnoId")
    boolean existsByPermisoLegajoIdAndTurnoId(@Param("legajoId") String legajoId, @Param("turnoId") String turnoId);

    Optional<Examen> findByPermisoAndCursadaExamen(Permiso permiso, CursadaExamen cursadaExamen);

    List<Examen> findByPermiso_PermisoLegajoIdAndCursadaExamen_Turno_TurnoId(String permisoLegajoId, String turnoId);

    @Query("SELECT e FROM Examen e WHERE e.permiso.permisoLegajoId = :legajoId AND e.cursadaExamen.turno.turnoId = :turnoId")
    List<Examen> findByLegajoIdAndTurno(@Param("legajoId") String legajoId, @Param("turnoId") String turnoId);

    @Query("SELECT COUNT(e) > 0 FROM Examen e " +
            "JOIN e.permiso p " +
            "JOIN e.cursadaExamen ce " +
            "WHERE ce.turno.turnoId = :turnoId " +  // ✅ Cambio aquí
            "AND ce.materiaId = :materiaId " +
            "AND p.permisoLegajoId = :legajoId")
    boolean existsInscripcion(@Param("turnoId") String turnoId,
                              @Param("materiaId") String materiaId,
                              @Param("legajoId") String legajoId);



    @Query("SELECT e.examenInscripto FROM Examen e " +
            "JOIN e.permiso p " +
            "JOIN e.cursadaExamen ce " +
            "WHERE ce.turno.turnoId = :turnoId " +
            "AND ce.materiaId = :materiaId " +
            "AND p.permisoLegajoId = :legajoId")
    Boolean getEstadoExamen(@Param("turnoId") String turno,
                            @Param("materiaId") String materiaId,
                            @Param("legajoId") String legajoId);

    @Modifying
    @Query("UPDATE Examen e SET e.examenInscripto = true WHERE e.id = :examenId")
    void marcarPermisoExamen(@Param("examenId") Long examenId);

    void deleteByNota_NotaId(Long notaId);
}
