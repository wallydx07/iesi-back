package com.example.iesiback.repositories;

import com.example.iesiback.dto.ActaCursadaDTO;
import com.example.iesiback.dto.CatedraDTO;
import com.example.iesiback.dto.MateriaCarreraDTO;
import com.example.iesiback.dto.MateriaDTO;
import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.Materia;
import com.example.iesiback.entities.MateriaCarrera;
import com.example.iesiback.projection.MateriaNivelPorLegajoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaCarreraRepository extends JpaRepository<MateriaCarrera, Long> {
    List<MateriaCarrera> findByCarrera_CarreraId(String carreraId);


//    Optional<MateriaCarrera> findByCarrera_CarreraIdAndMateria_MateriaId(String carreraId, String materiaId);


    List<MateriaCarrera> findByCarrera_CarreraIdAndMateria_MateriaId(String carreraId, String materiaId);
    List<MateriaCarrera> findByCarrera_CarreraIdAndMateria_MateriaIdAndDivision(String carreraId, String materiaId, String division);



/*
    @Query("SELECT new com.example.iesiback.dto.MateriaCarreraDTO(" +
            "m.fmcDocente, " +
            "m.libro, " +
            "m.folio," +
            "m.fecha," +
            "m.firma, " +
            "m.id," +
            "m.division," +
            " m.turno, " +
            "m.dia, " +
            "m.inicio, " +
            "m.fin," +
            "m.materia.materiaId," +
            "m.carrera.carreraId) " +
            "FROM MateriaCarrera m " +
            "WHERE m.materia.materiaId = :materiaId AND m.carrera.carreraId = :carreraId")
    MateriaCarreraDTO findMateriaCarreraByMateriaAndCarrera(String materiaId, String carreraId);
*/
    @Query(value = "SELECT COUNT(*) FROM materia_carrera mc INNER JOIN materia m ON mc.materia_id = m.materia_id WHERE mc.carrera_id = :carreraId AND m.materia_nivel = :nivel", nativeQuery = true)
    int countMateriasPorNivel(@Param("carreraId") String carreraId, @Param("nivel") String nivel);



    @Modifying
    @Transactional
    @Query("UPDATE MateriaCarrera m SET " +
            "m.libro = COALESCE(:libro, m.libro), " +
            "m.folio = COALESCE(:folio, m.folio), " +
            "m.fecha = COALESCE(:fecha, m.fecha), " +
            "m.firma = COALESCE(:firma, m.firma), " +
            "m.fmcDocente = COALESCE(:fmcDocente, m.fmcDocente), " +
            "m.division = COALESCE(:division, m.division), " +
            "m.turno = COALESCE(:turno, m.turno), " +
            "m.dia = COALESCE(:dia, m.dia), " +
            "m.inicio = COALESCE(:inicio, m.inicio), " +
            "m.fin = COALESCE(:fin, m.fin) " +
            "WHERE m.id = :id")
    int actualizarMateriaCarrera(@Param("id") Long id,
                                 @Param("libro") String libro,
                                 @Param("folio") String folio,
                                 @Param("fecha") LocalDate fecha,
                                 @Param("firma") boolean firma,
                                 @Param("fmcDocente") Integer fmcDocente,
                                 @Param("division") String division,
                                 @Param("turno") String turno,
                                 @Param("dia") String dia,
                                 @Param("inicio") String inicio,
                                 @Param("fin") String fin);

//    @Query("SELECT new com.example.iesiback.dto.CatedraDTO(mc.id, m.materiaId, m.materiaNombre, c.carreraId, mc.division, mc.turno, mc.dia, mc.inicio, mc.fin, mc.materia.catedras, c.carreraNombre) " +
//            "FROM MateriaCarrera mc " +
//            "JOIN mc.materia m " +
//            "JOIN mc.carrera c " +
//            "WHERE mc.fmcDocente = :dni AND c.carreraYear = :year " +
//            "ORDER BY m.materiaId ASC")
//    List<CatedraDTO> findCatedrasByDocenteAndYear(@Param("dni") String dni, @Param("year") String year);


//    @Query("SELECT new com.example.iesiback.dto.CatedraDTO(mc.id, m.materiaId, m.materiaNombre, c.carreraId, mc.division, mc.turno, mc.dia, mc.inicio, mc.fin, mc.materia.catedras, c.carreraNombre, c.carreraYear) " +
//            "FROM MateriaCarrera mc " +
//            "JOIN mc.materia m " +
//            "JOIN mc.carrera c " +
//            "WHERE mc.fmcDocente = :dni AND :year BETWEEN c.carreraYear AND (c.carreraYear + 2) " +
//            "ORDER BY m.materiaId ASC")
//    List<CatedraDTO> findCatedrasByDocenteAndYear(@Param("dni") String dni, @Param("year") Integer year);


    @Query("SELECT new com.example.iesiback.dto.CatedraDTO(mc.id, m.materiaId, m.materiaNombre, c.carreraId, mc.division, mc.turno, mc.dia, mc.inicio, mc.fin, mc.materia.catedras, c.carreraNombre, c.carreraYear) " +
            "FROM MateriaCarrera mc " +
            "JOIN mc.materia m " +
            "JOIN mc.carrera c " +
            "WHERE mc.fmcDocente = :dni " +
            "AND (" +
            "     (m.materiaNivel = '1ro' AND c.carreraYear = :year) " +
            "  OR (m.materiaNivel = '2do' AND c.carreraYear + 1 = :year) " +
            "  OR (m.materiaNivel = '3ro' AND c.carreraYear + 2 = :year)" +
            ") " +
            "ORDER BY m.materiaId ASC")
    List<CatedraDTO> findCatedrasByDocenteAndYear(@Param("dni") String dni, @Param("year") Integer year);



//    @Query("SELECT DISTINCT mc.carrera FROM MateriaCarrera mc WHERE mc.fmcDocente = :fmcDocente")
//    List<Carrera> findCarrerasByFmcDocente(@Param("fmcDocente") Long fmcDocente);


    @Query("""
    SELECT DISTINCT mc.carrera
    FROM MateriaCarrera mc
    JOIN mc.materia m
    WHERE mc.fmcDocente = :fmcDocente
      AND :anioActual = (
        CASE 
          WHEN m.materiaNivel = '1ro' THEN mc.carrera.carreraYear
          WHEN m.materiaNivel = '2do' THEN mc.carrera.carreraYear + 1
          WHEN m.materiaNivel = '3ro' THEN mc.carrera.carreraYear + 2
          ELSE -1
        END
      )
""")
    List<Carrera> findCarrerasDictadasEsteAnio(@Param("fmcDocente") Long fmcDocente, @Param("anioActual") Integer anioActual);

//
//
//    @Query("SELECT mc.materia FROM MateriaCarrera mc WHERE mc.fmcDocente = :fmcDocente AND mc.carrera.carreraId = :carreraId")
//    List<MateriaDTO> findMateriasByCarreraAndFmcDocente(@Param("fmcDocente") Long fmcDocente, @Param("carreraId") String carreraId);
//
//

//    @Query("""
//    SELECT m.materiaId AS materiaId,
//           m.materiaNombre AS materiaNombre,
//           CAST(m.materiaOrden AS string) AS materiaOrden,
//           m.materiaNivel AS materiaNivel,
//           m.materiaRegimen AS materiaRegimen,
//           m.materiaModalidad AS materiaModalidad,
//           CAST(m.materiaCursada AS string) AS materiaCursada,
//           CAST(m.materiaExamen AS string) AS materiaExamen,
//           '' AS catedras
//    FROM MateriaCarrera mc
//    JOIN mc.materia m
//    WHERE mc.fmcDocente = :fmcDocente AND mc.carrera.carreraId = :carreraId
//""")
//    List<MateriaDTO> findMateriasByCarreraAndFmcDocente(@Param("fmcDocente") Long fmcDocente, @Param("carreraId") String carreraId);
//


    @Query(value = """
    SELECT 
        m.materia_id,
        m.materia_nombre,
        m.materia_orden,
        m.materia_nivel,
        m.materia_regimen,
        m.materia_cursada,
        m.materia_modalidad,
        m.materia_examen,
        m.catedras,
        mc.division
    FROM materia_carrera mc
    INNER JOIN materia m ON mc.materia_id = m.materia_id
    INNER JOIN carrera c ON mc.carrera_id = c.carrera_id
    WHERE mc.fmc_docente = :fmcDocente
      AND c.carrera_id = :carreraId
      AND :anioActual = (
        CASE
          WHEN m.materia_nivel = '1ro' THEN c.carrera_year
          WHEN m.materia_nivel = '2do' THEN c.carrera_year + 1
          WHEN m.materia_nivel = '3ro' THEN c.carrera_year + 2
          ELSE -1
        END
      )
""", nativeQuery = true)
    List<MateriaDTO> findMateriasDictadasEsteAnio(
            @Param("fmcDocente") Long fmcDocente,
            @Param("carreraId") String carreraId,
            @Param("anioActual") Integer anioActual
    );




//    @Query(value = """
//             SELECT m.materiaId,
//                    m.materiaNombre,
//                    m.materiaOrden,
//                    m.materiaNivel,
//                    m.materiaRegimen,  \s
//                    m.materiaCursada,
//                    m.materiaModalidad,
//                    m.materiaExamen,
//                    m.catedras,
//                    mc.division
//    FROM MateriaCarrera mc
//    JOIN mc.materia m
//    WHERE mc.fmcDocente = :fmcDocente
//      AND mc.carrera.carreraId = :carreraId
//      AND :anioActual = (
//        CASE
//          WHEN m.materiaNivel = '1ro' THEN mc.carrera.carreraYear
//          WHEN m.materiaNivel = '2do' THEN mc.carrera.carreraYear + 1
//          WHEN m.materiaNivel = '3ro' THEN mc.carrera.carreraYear + 2
//          ELSE -1
//        END
//      )
//""")
//    List<MateriaDTO> findMateriasDictadasEsteAnio(
//            @Param("fmcDocente") Long fmcDocente,
//            @Param("carreraId") String carreraId,
//            @Param("anioActual") Integer anioActual
//    );
//




    @Query(value = "SELECT COUNT(*) - 1 FROM materia_carrera mc INNER JOIN materia m ON mc.materia_id = m.materia_id WHERE mc.carrera_id = :carreraId AND m.materia_nivel = :nivel", nativeQuery = true)
    int contarMateriasPorNivel(@Param("carreraId") String carreraId, @Param("nivel") String nivel);



    @Query("SELECT new com.example.iesiback.dto.ActaCursadaDTO(" +
            "mc.id, m.materiaNombre, m.materiaOrden, mc.folio, mc.libro, mc.fecha, mc.firma, " +
            "mc.fmcDocente, c.carreraId, c.carreraYear, p.personalApellido, p.personalNombre, " +
            "m.materiaModalidad, m.materiaRegimen, m.materiaId, mc.division) " +
            "FROM MateriaCarrera mc " +
            "JOIN mc.materia m " +
            "JOIN mc.carrera c " +
            "LEFT JOIN Personal p ON mc.fmcDocente = p.id " +
            "ORDER BY mc.fecha DESC, c.carreraId, m.materiaOrden")
    List<ActaCursadaDTO> obtenerActas();

    @Query("SELECT new com.example.iesiback.dto.ActaCursadaDTO(" +
            "mc.id, m.materiaNombre, m.materiaOrden, mc.folio, mc.libro, mc.fecha, mc.firma, " +
            "mc.fmcDocente, c.carreraId, c.carreraYear, p.personalApellido, p.personalNombre, " +
            "m.materiaModalidad, m.materiaRegimen, m.materiaId, mc.division) " +
            "FROM MateriaCarrera mc " +
            "JOIN mc.materia m " +
            "JOIN mc.carrera c " +
            "LEFT JOIN Personal p ON mc.fmcDocente = p.id " +
            "WHERE :anio BETWEEN c.carreraYear AND c.carreraYear + 2 " +
            "AND (" +
            "  (m.materiaNivel = '1ro' AND c.carreraYear = :anio) " +
            "  OR (m.materiaNivel = '2do' AND c.carreraYear + 1 = :anio) " +
            "  OR (m.materiaNivel = '3ro' AND c.carreraYear + 2 = :anio)" +
            ")")
    List<ActaCursadaDTO> obtenerActasPorAnio(@Param("anio") int anio);

    @Query("""
    SELECT mc
    FROM MateriaCarrera mc
    JOIN mc.materia m
    WHERE m.materiaOrden = :orden
      AND mc.carrera.carreraId = :carrera
      AND mc.fecha >= :fechaDesde
      AND mc.fecha < :fechaHasta
    ORDER BY mc.fecha DESC
""")
    MateriaCarrera buscarPorOrdenCarreraYAño(
            @Param("orden") Integer orden,
            @Param("carrera") String carrera,
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta
    );

    @Query("""
    SELECT mc
    FROM MateriaCarrera mc
    JOIN mc.materia m
    JOIN mc.carrera c
    WHERE m.materiaOrden = :orden
      AND c.carreraNombre = (
            SELECT c2.carreraNombre
            FROM Carrera c2
            WHERE c2.carreraId = :carreraId
      )
      AND mc.fechaInicio <= :fecha
    ORDER BY mc.fechaInicio DESC
""")
    List<MateriaCarrera> buscarMateriaCarreraAnterior(
            @Param("orden") Integer orden,
            @Param("carreraId") String carreraId,
            @Param("fecha") LocalDate fecha
    );

    @Query(value = """
     SELECT mc.fecha
     FROM materia m
     INNER JOIN materia_carrera mc ON m.materia_id = mc.materia_id
     WHERE mc.carrera_id = :carreraId 
       AND m.materia_orden = :orden
     ORDER BY m.materia_orden ASC
""", nativeQuery = true)
    LocalDate findFechaByCarreraIdAndOrden(
            @Param("carreraId") String carreraId,
            @Param("orden") Integer orden
    );


//
//    @Query("""
//    SELECT mc
//    FROM MateriaCarrera mc
//    JOIN mc.materia m
//    JOIN mc.carrera c
//    WHERE m.materiaOrden = :orden
//      AND c.carreraId= :orden
//    ORDER BY mc.fechaInicio DESC
//""")
//    List<MateriaCarrera> buscarMateriaCarreraOrden(
//            @Param("orden") Integer orden,
//            @Param("carreraId") String carreraId
//    );


    @Query("SELECT mc FROM MateriaCarrera mc " +
            "JOIN mc.cursadas c " +
            "WHERE c.legajo.legajoId = :legajoId " +
            "AND YEAR(mc.fechaInicio) = :anioActual")
    List<MateriaCarrera> findMateriasPorLegajoYAnio(
            @Param("legajoId") String legajoId,
            @Param("anioActual") int anioActual
    );


    @Query("SELECT c.legajo.legajoId AS legajoId, mc.materia.materiaNivel AS materiaNivel " +
            "FROM MateriaCarrera mc " +
            "JOIN mc.cursadas c " +
            "WHERE c.legajo.legajoId IN :legajoIds " +
            "AND YEAR(mc.fechaInicio) = :anioActual")
    List<MateriaNivelPorLegajoProjection> findNivelesPorLegajosYAnio(
            @Param("legajoIds") List<String> legajoIds,
            @Param("anioActual") int anioActual
    );
}


