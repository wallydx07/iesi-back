package com.example.iesiback.repositories;

import com.example.iesiback.dto.*;
import com.example.iesiback.entities.AsistenciaAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsistenciaAlumnoRepository extends JpaRepository<AsistenciaAlumno, Integer> {



    @Query("""
    SELECT new com.example.iesiback.dto.AlumnoAsistenciaDTO(
        a.id, :idInforme, l.legajoId, a.estado,
        al.personaDni, al.personaApellido, al.personaNombre
    )
    FROM Cursada c
    JOIN c.legajo l
    JOIN Persona al ON l.legajoPersonaDni.personaDni = al.personaDni
    LEFT JOIN AsistenciaAlumno a ON a.legajoId = l.legajoId
        AND a.idInforme.idInforme = :idInforme
    WHERE c.materiaCarrera.id = (
        SELECT i.materiaCarrera.id FROM InformeAsistenciaAlumno i
        WHERE i.idInforme = :idInforme
    )
    ORDER BY al.personaApellido asc, al.personaNombre asc
""")
    List<AlumnoAsistenciaDTO> obtenerAsistenciasConDetalle(@Param("idInforme") Integer idInforme);


    //AQUI SE OBTIENE LOS ALUMNOS DE CURSADA///////


    @Query("""
    SELECT new com.example.iesiback.dto.AsistenciaAlumnoDTO(
        asistencia.id,
        informe.idInforme,
        asistencia.legajoId,
        asistencia.estado
    )
    FROM AsistenciaAlumno asistencia
    JOIN asistencia.idInforme informe
    JOIN informe.materiaCarrera materiaCarrera
    JOIN materiaCarrera.materia materia
    WHERE materiaCarrera.id = :materiaCarreraId
""")
    List<AsistenciaAlumnoDTO> obtenerAsistenciaPorMateriaCarrera(
            @Param("materiaCarreraId") String materiaCarreraId
    );

    @Query("""
    SELECT new com.example.iesiback.dto.InformeAsistenciaDTO(
        informe.idInforme,
        informe.fecha
    )
    FROM InformeAsistenciaAlumno informe
    JOIN informe.materiaCarrera mc
    JOIN mc.materia m
    JOIN mc.carrera c
    WHERE mc.id = :materiaCarreraId
    ORDER BY informe.fecha ASC
""")
    List<InformeAsistenciaDTO> obtenerFechasAsistencia(
            @Param("materiaCarreraId") String materiaCarreraId
    );

//
//    @Query("""
//    SELECT new com.example.iesiback.dto.AsistenciaResumenDTO(
//        m.materiaNombre,
//        COUNT(a.id),
//        SUM(CASE WHEN a.estado = true THEN 1 ELSE 0 END),
//        CAST(ROUND(SUM(CASE WHEN a.estado = true THEN 1 ELSE 0 END) * 100.0 / COUNT(a.id), 0) AS Integer),
//        m.materiaId
//    )
//    FROM AsistenciaAlumno a
//    JOIN a.idInforme i
//    JOIN i.materiaCarrera mc
//    JOIN mc.materia m
//    WHERE a.legajoId = :legajoId
//      AND YEAR(i.fecha) = :anioActual
//      AND EXISTS (
//          SELECT 1 FROM Cursada c
//          WHERE c.materiaCarrera = mc
//            AND c.legajo.legajoId = a.legajoId
//      )
//    GROUP BY m.materiaNombre, m.materiaOrden, m.materiaId
//    ORDER BY m.materiaOrden
//""")
//    List<AsistenciaResumenDTO> obtenerResumenAsistencia(@Param("legajoId") String legajoId,
//                                                        @Param("anioActual") int anioActual);

    @Query("""
    SELECT new com.example.iesiback.dto.AsistenciaResumenDTO(
        m.materiaNombre,
        COUNT(a.id) - SUM(CASE WHEN a.estado = com.example.iesiback.enums.EstadoAsistencia.JUSTIFICADO THEN 1 ELSE 0 END),
        CAST(SUM(CASE
            WHEN a.estado = com.example.iesiback.enums.EstadoAsistencia.PRESENTE THEN 1.0
            WHEN a.estado IN (
                com.example.iesiback.enums.EstadoAsistencia.TARDANZA,
                com.example.iesiback.enums.EstadoAsistencia.RETIRO_TEMPRANO
            ) THEN 0.5
            ELSE 0.0
        END) AS Double),
        CAST(ROUND(SUM(CASE
            WHEN a.estado = com.example.iesiback.enums.EstadoAsistencia.PRESENTE THEN 1.0
            WHEN a.estado IN (
                com.example.iesiback.enums.EstadoAsistencia.TARDANZA,
                com.example.iesiback.enums.EstadoAsistencia.RETIRO_TEMPRANO
            ) THEN 0.5
            ELSE 0.0
        END) * 100.0 / NULLIF(
            COUNT(a.id) - SUM(CASE WHEN a.estado = com.example.iesiback.enums.EstadoAsistencia.JUSTIFICADO THEN 1 ELSE 0 END)
        , 0), 0) AS Integer),
        m.materiaId,
        SUM(CASE WHEN a.estado = com.example.iesiback.enums.EstadoAsistencia.PRESENTE        THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.estado = com.example.iesiback.enums.EstadoAsistencia.AUSENTE         THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.estado = com.example.iesiback.enums.EstadoAsistencia.TARDANZA        THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.estado = com.example.iesiback.enums.EstadoAsistencia.RETIRO_TEMPRANO THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.estado = com.example.iesiback.enums.EstadoAsistencia.JUSTIFICADO     THEN 1 ELSE 0 END)
    )
    FROM AsistenciaAlumno a
    JOIN a.idInforme i
    JOIN i.materiaCarrera mc
    JOIN mc.materia m
    WHERE a.legajoId = :legajoId
      AND YEAR(i.fecha) = :anioActual
      AND EXISTS (
          SELECT 1 FROM Cursada c
          WHERE c.materiaCarrera = mc
            AND c.legajo.legajoId = a.legajoId
      )
    GROUP BY m.materiaNombre, m.materiaOrden, m.materiaId
    ORDER BY m.materiaOrden
""")
    List<AsistenciaResumenDTO> obtenerResumenAsistencia(@Param("legajoId") String legajoId,
                                                        @Param("anioActual") int anioActual);
    @Query("""
    SELECT new com.example.iesiback.dto.AsistenciaAlumnoDetalleDTO(
        m.materiaId,
        m.materiaNombre,
        i.fecha,
        a.estado
    )
    FROM AsistenciaAlumno a
    JOIN a.idInforme i
    JOIN i.materiaCarrera mc
    JOIN mc.materia m
    WHERE a.legajoId = :legajoId
      AND YEAR(i.fecha) = :anio
      AND EXISTS (
          SELECT 1 FROM Cursada c
          WHERE c.materiaCarrera = mc
            AND c.legajo.legajoId = a.legajoId
      )
    ORDER BY m.materiaOrden, i.fecha
""")
    List<AsistenciaAlumnoDetalleDTO> obtenerDetalleAsistencia(@Param("legajoId") String legajoId,
                                                        @Param("anio") int anio);
}