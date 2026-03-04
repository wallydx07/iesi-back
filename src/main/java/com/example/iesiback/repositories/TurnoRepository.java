package com.example.iesiback.repositories;

import com.example.iesiback.dto.TurnoExamenDTO;
import com.example.iesiback.entities.Turno; // Asegúrate de importar la entidad Turno
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, String> {
    List<Turno> findAllByOrderByTurnoLimiteDesc();

    @Query("SELECT new com.example.iesiback.dto.TurnoExamenDTO(t.turnoId, t.turnoMes, t.turnoAnio, t.turnoLimite, t.llamado) " +
            "FROM Turno t ORDER BY t.turnoLimite desc")
    List<TurnoExamenDTO> findAllTurnoExamenDTO();

    @Query(value = """
    SELECT 
        t.turno_id,
        t.turno_mes,
        t.turno_anio,
        t.turno_limite,
        t.llamado
    FROM turno t
    ORDER BY 
        ABS(t.turno_limite - :fechaReferencia),
        CASE 
            WHEN t.llamado = '1ER LLAMADO' THEN 1
            WHEN t.llamado = 'UNICO' THEN 2
            WHEN t.llamado = '2DO LLAMADO' THEN 3
        END
    LIMIT 1
""", nativeQuery = true)
    List<Object[]> findTurnoMasCercanoRaw(
            @Param("fechaReferencia") LocalDate fechaReferencia
    );

    @Query(value = """
    SELECT 
        t.turno_id,
        t.turno_mes,
        t.turno_anio,
        t.turno_limite,
        t.llamado
    FROM turno t
    INNER JOIN cursada_examen ce
        ON t.turno_id = ce.turno_id
    INNER JOIN examen e
        ON ce.cursada_examen_id = e.cursada_examen_id
    INNER JOIN nota n
        ON e.nota_id = n.nota_id
    WHERE n.nota_id = :notaId
""", nativeQuery = true)
    TurnoExamenDTO findTurnobyNotaId(@Param("notaId") Long notaId);










}

