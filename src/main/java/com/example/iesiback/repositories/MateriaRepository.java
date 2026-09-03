package com.example.iesiback.repositories;

import com.example.iesiback.dto.MateriaDTO;
import com.example.iesiback.dto.ReinscripcionMateriaDTO;
import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, String> {
    @Query(value = """
             SELECT m.materia_id,
                    m.materia_nombre,
                    m.materia_orden,
                    m.materia_nivel,
                    m.materia_regimen,  \s
                    m.materia_cursada,
                    m.materia_modalidad,
                    m.materia_examen,
                    m.catedras,
                    mc.division
             FROM materia m
             INNER JOIN materia_carrera mc ON m.materia_id = mc.materia_id
             WHERE mc.carrera_id = :carreraId
             ORDER BY m.materia_orden ASC
            \s""", nativeQuery = true)
    List<MateriaDTO> findMateriasByCarrera(@Param("carreraId") String carreraId);

    @Query(value = """
        SELECT 
            mc.id               As materiaCarreraId,
            m.materia_id        AS materiaId,
            m.materia_orden     AS materiaOrden,
            m.materia_nivel     AS materiaNivel,
            c.carrera_nombre    AS carreraNombre,
            m.materia_nombre    AS materiaNombre,
            c.carrera_year      AS carreraYear,
            m.materia_cursada   AS materiaCursada,
            m.materia_modalidad AS materiaModalidad,
            m.materia_regimen   AS materiaRegimen,
            mc.division         AS division,
            c.carrera_id        AS carreraId
        FROM materia m
        INNER JOIN materia_carrera mc ON m.materia_id = mc.materia_id
        INNER JOIN carrera c ON mc.carrera_id = c.carrera_id
        WHERE :cicloLectivo <= (CAST(c.carrera_year AS INTEGER) + 2)
          AND (
                (m.materia_nivel = '1ro' AND CAST(c.carrera_year AS INTEGER) = :cicloLectivo)
             OR (m.materia_nivel = '2do' AND (CAST(c.carrera_year AS INTEGER) + 1) = :cicloLectivo)
             OR (m.materia_nivel = '3ro' AND (CAST(c.carrera_year AS INTEGER) + 2) = :cicloLectivo)
          )
          AND c.carrera_nombre = :carreraNombre
          AND (:division IS NULL OR :division = '' OR mc.division = :division)
        ORDER BY m.materia_nivel, m.materia_orden ASC
        """,
            nativeQuery = true)
    List<ReinscripcionMateriaDTO> findReinscripciones(
            @Param("cicloLectivo") Integer cicloLectivo,
            @Param("carreraNombre") String carreraNombre,
            @Param("division") String division
    );

    @Query(value = "SELECT COUNT(*) FROM materia_carrera mc INNER JOIN materia m ON mc.materia_id = m.materia_id WHERE mc.carrera_id = :carreraId AND m.materia_nivel = :nivel", nativeQuery = true)
    int countMateriasPorNivel(@Param("carreraId") String carreraId, @Param("nivel") String nivel);

    Optional<Materia> findByMateriaOrden(Integer materiaOrden);


    @Query(value = """
SELECT m.*
FROM materia m
JOIN materia_carrera mc 
    ON mc.materia_id = m.materia_id
JOIN carrera c 
    ON c.carrera_id = mc.carrera_id
JOIN inscripcion i
    ON SPLIT_PART(i.carrera_id, '-', 1) =
       SPLIT_PART(c.carrera_id, '-', 1)
JOIN legajo l 
    ON i.legajo_id = l.legajo_id
WHERE m.materia_orden = :materiaOrden
AND l.legajo_id = :legajoId
LIMIT 1
""", nativeQuery = true)
    Optional<Materia> findByMateriaOrdenAndLegajoId(
            @Param("materiaOrden") Integer materiaOrden,
            @Param("legajoId") String legajoId);


    List<Materia> findAllByMateriaIdIn(Set<String> ids);


    // Opción 2: Usando SQL Nativo (Tal cual tu consulta)
    @Query(value = "SELECT m.* FROM materia m " +
            "INNER JOIN materia_carrera mc ON m.materia_id = mc.materia_id " +
            "INNER JOIN cursada c          ON mc.id = c.cursada_materia_carrera_id " +
            "INNER JOIN nota n             ON c.cursada_id = n.nota_cursada_id " +
            "WHERE n.nota_id = :notaId", nativeQuery = true)
    Optional<Materia> findMateriaByNotaIdNativo(@Param("notaId") Long notaId);

}



