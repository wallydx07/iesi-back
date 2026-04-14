package com.example.iesiback.repositories;

import com.example.iesiback.dto.AlumnoAsistenciaDTO;
import com.example.iesiback.dto.AsistenciaResumenDTO;
import com.example.iesiback.dto.InformeAsistenciaDTO;
import com.example.iesiback.entities.AsistenciaAlumno;
import com.example.iesiback.dto.AsistenciaAlumnoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsistenciaAlumnoRepository extends JpaRepository<AsistenciaAlumno, Integer> {

    @Query("""
        SELECT distinct new com.example.iesiback.dto.AlumnoAsistenciaDTO(
            a.id, a.idInforme.idInforme, a.legajoId, a.estado,
            al.personaDni, al.personaApellido, al.personaNombre
        )
        FROM AsistenciaAlumno a
        JOIN Legajo l ON a.legajoId = l.legajoId
        JOIN Persona al ON l.legajoPersonaDni.personaDni= al.personaDni
        JOIN Cursada c ON c.legajo.legajoId = l.legajoId
        WHERE (:idInforme IS NULL OR a.idInforme.idInforme = :idInforme)
            ORDER BY al.personaApellido asc, al.personaNombre asc
    """)
    List<AlumnoAsistenciaDTO> obtenerAsistenciasConDetalle(
            @Param("idInforme") Integer idInforme
    );


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


//    @Query("""
//    SELECT new com.example.iesiback.dto.AsistenciaResumenDTO(
//        m.materiaNombre,
//        COUNT(a.id),
//        SUM(CASE WHEN a.estado = true THEN 1 ELSE 0 END),
//        CAST(ROUND(SUM(CASE WHEN a.estado = true THEN 1 ELSE 0 END) * 100.0 / COUNT(a.id)) AS int)
//
//    )
//    FROM AsistenciaAlumno a
//    JOIN a.idInforme i
//    JOIN i.materiaCarrera mc
//    JOIN mc.materia m
//    WHERE a.legajoId = :legajoId
//    GROUP BY m.materiaNombre, m.materiaOrden
//    ORDER BY m.materiaOrden
//""")
//    List<AsistenciaResumenDTO> obtenerResumenAsistencia(@Param("legajoId") String legajoId);




    @Query("""
    SELECT new com.example.iesiback.dto.AsistenciaResumenDTO(
        m.materiaNombre,
        COUNT(a.id),
        SUM(CASE WHEN a.estado = true THEN 1 ELSE 0 END),
        CAST(ROUND(SUM(CASE WHEN a.estado = true THEN 1 ELSE 0 END) * 100.0 / COUNT(a.id)) AS int),m.materiaId
    )
    FROM AsistenciaAlumno a
    JOIN a.idInforme i
    JOIN i.materiaCarrera mc
    JOIN mc.materia m
    WHERE a.legajoId = :legajoId
      AND FUNCTION('date_part', 'year', i.fecha) = :anioActual
    GROUP BY m.materiaNombre, m.materiaOrden, m.materiaId
    ORDER BY m.materiaOrden
""")
    List<AsistenciaResumenDTO> obtenerResumenAsistencia(@Param("legajoId") String legajoId, @Param("anioActual") int anioActual);


}