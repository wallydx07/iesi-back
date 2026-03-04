package com.example.iesiback.repositories;

import com.example.iesiback.dto.MateriaDTO;
import com.example.iesiback.dto.ReinscripcionMateriaDTO;
import com.example.iesiback.entities.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
                    m.catedras
             FROM materia m
             INNER JOIN materia_carrera mc ON m.materia_id = mc.materia_id
             WHERE mc.carrera_id = :carreraId
             ORDER BY m.materia_orden ASC
            \s""", nativeQuery = true)
    List<MateriaDTO> findMateriasByCarrera(@Param("carreraId") String carreraId);

    @Query(value = """
            SELECT 
                mc.id As materiaCarreraId,
                m.materia_id       AS materiaId,
                m.materia_orden    AS materiaOrden,
                m.materia_nivel    AS materiaNivel,
                c.carrera_nombre   AS carreraNombre,
                m.materia_nombre   AS materiaNombre,
                c.carrera_year     AS carreraYear,
                m.materia_cursada  AS materiaCursada,
                m.materia_modalidad AS materiaModalidad,
                m.materia_regimen  AS materiaRegimen
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
            ORDER BY m.materia_nivel, m.materia_orden ASC
            """,
            nativeQuery = true)
    List<ReinscripcionMateriaDTO> findReinscripciones(
            @Param("cicloLectivo") Integer cicloLectivo,
            @Param("carreraNombre") String carreraNombre
    );


    @Query(value = "SELECT COUNT(*) FROM materia_carrera mc INNER JOIN materia m ON mc.materia_id = m.materia_id WHERE mc.carrera_id = :carreraId AND m.materia_nivel = :nivel", nativeQuery = true)
    int countMateriasPorNivel(@Param("carreraId") String carreraId, @Param("nivel") String nivel);

    Optional<Materia> findByMateriaOrden(Integer materiaOrden);


//    @Query(value = "SELECT * FROM materia m " +
//            "WHERE substring(m.materia_id from position('-' in m.materia_id) + 1) = :codigoCarrera " +
//            "AND unaccent(lower(m.materia_nombre)) ILIKE CONCAT('%', unaccent(lower(:nombre)), '%')",
//            nativeQuery = true)
//    List<Materia> buscarMateriaPorNombreYCodigoCarrera(
//            @Param("nombre") String nombre,
//            @Param("codigoCarrera") String codigoCarrera
//    );




}



