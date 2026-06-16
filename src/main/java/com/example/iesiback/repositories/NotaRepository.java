package com.example.iesiback.repositories;

import com.example.iesiback.dto.*;
import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Examen;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.enums.EstadoCondicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
 //es UNION ALL
//CAST(n.notaCalificacionNotaNumero AS string),

    @Query("""
SELECT new com.example.iesiback.dto.NotaMateriaDTO(
    n.notaId,
    m.materiaOrden,
    m.materiaNombre,
CAST(n.notaCalificacionNotaNumero AS string),
    n.notaCalificacionNotaLetra,
    n.notaCondicion,
    n.notaEstado,
    n.notaLibroNota,
    n.notaFolioNota,
    n.notaFechaNota,
    n.notaObservaciones,
    n.notaUsuario,
    mc.materia.materiaId,
    m.materiaNivel,
    c.id,
    mc.firma,
    mc.id
)
FROM Nota n
JOIN n.cursada c
JOIN c.materiaCarrera mc
JOIN mc.materia m
WHERE c.legajo.legajoId = :legajoId
ORDER BY m.materiaOrden ASC, n.notaFechaNota ASC
""")
    List<NotaMateriaDTO> findNotasPorLegajo(@Param("legajoId") String legajoId);

// @Query(value = """
//    SELECT
//        n.nota_id AS notaId,
//        m.materia_orden,
//        m.materia_nombre,
//        n.nota_calificacion_nota_numero,
//        n.nota_calificacion_nota_letra,
//        n.nota_condicion,
//        n.nota_estado,
//        n.nota_libro_nota,
//        n.nota_folio_nota,
//        n.nota_fecha_nota,
//        n.nota_observaciones,
//        n.nota_usuario,
//        n.nota_cursada_id,
//        mc.materia_id,
//        m.materia_nivel,
//        c.cursada_legajo_id,
//        mc.firma
//    FROM nota n
//    INNER JOIN cursada c ON n.nota_cursada_id = c.cursada_id
//    INNER JOIN materia_carrera mc ON c.cursada_materia_carrera_id = mc.id
//    INNER JOIN materia m ON mc.materia_id = m.materia_id
//    WHERE c.cursada_legajo_id = :legajoId
//    ORDER BY m.materia_orden ASC, n.nota_fecha_nota ASC
//""", nativeQuery = true)
// List<Object[]> findNotasPorLegajo(@Param("legajoId") String legajoId);


//    @Query(value = """
//    SELECT nota.nota_id , persona.alumno_dni,
//           persona.alumno_apellido , persona.alumno_nombre ,
//           nota.nota_fecha_nota ,
//           nota.nota_calificacion_nota_numero ,
//           nota.nota_calificacion_nota_letra ,
//           nota.nota_estado ,
//           nota.nota_libro_nota , nota.nota_folio_nota ,
//           cursada.status , nota.nota_observaciones ,
//           nota.nota_usuario
//    FROM persona
//    INNER JOIN legajo ON persona.alumno_dni = legajo.legajo_alumno_dni
//    INNER JOIN cursada ON legajo.legajo_id = cursada.cursada_legajo_id
//    INNER JOIN materia_carrera ON cursada.cursada_materia_carrera_id = materia_carrera.id
//    INNER JOIN materia ON materia_carrera.materia_id = materia.materia_id
//    INNER JOIN carrera ON materia_carrera.carrera_id = carrera.carrera_id
//    INNER JOIN nota ON cursada.cursada_id = nota.nota_cursada_id
//    WHERE carrera.carrera_id = :carreraId
//      AND materia.materia_id = :materiaId
//      AND (cursada.cursada_inscripto = TRUE OR (:cursadaInscripto = FALSE))
//      AND nota.nota_condicion = :notaCondicion
//    ORDER BY persona.alumno_apellido, persona.alumno_nombre ASC
//   """, nativeQuery = true)
//    List<NotaCursadaDTO> findNotasByCarreraAndMateria(
//            @Param("carreraId") String carreraId,
//            @Param("materiaId") String materiaId,
//            @Param("cursadaInscripto") boolean cursadaInscripto,
//            @Param("notaCondicion") String notaCondicion
//    );
//    //AQUI SE OBTIENE LOS ALUMNOS DE CURSADA///////
//



    @Query("""
SELECT
    n.notaId AS notaId,
    p.personaDni AS personaDni,
    l.legajoId AS personaLegajoId,
    p.personaApellido AS personaApellido,
    p.personaNombre AS personaNombre,
    n.notaFechaNota AS notaFechaNota,
    n.notaCalificacionNotaNumero AS notaCalificacionNotaNumero,
    n.notaCalificacionNotaLetra AS notaCalificacionNotaLetra,
    n.notaEstado AS notaEstado,
    n.notaLibroNota AS notaLibroNota,
    n.notaFolioNota AS notaFolioNota,
    c.status AS cursadaStatus,
    n.notaObservaciones AS notaObservaciones,
    n.notaUsuario AS notaUsuario,
    c.primerParcial AS primerParcial,
    c.recuperatorio1 AS recuperatorio1,
    c.segundoParcial AS segundoParcial,
    c.recuperatorio2 AS recuperatorio2,
    c.trabajosPracticos AS trabajosPracticos,
    c.asistencia AS asitencia,
    c.coloquio AS coloquio,
    c.trabajoInstitucional AS trabajoInstitucional
FROM Nota n
JOIN n.cursada c
JOIN c.legajo l
JOIN l.legajoPersonaDni p
JOIN c.materiaCarrera mc
JOIN mc.materia m
JOIN mc.carrera ca
WHERE ca.carreraId = :carreraId
  AND m.materiaId = :materiaId
  AND (:cursadaInscripto = FALSE OR c.cursadaInscripto = TRUE)
  AND n.notaCondicion = :notaCondicion
  AND mc.division = :division
ORDER BY p.personaApellido, p.personaNombre
""")
    List<NotaCursadaDTO> findNotasByCarreraAndMateria(
            @Param("carreraId") String carreraId,
            @Param("materiaId") String materiaId,
            @Param("cursadaInscripto") boolean cursadaInscripto,
            @Param("notaCondicion") EstadoCondicion notaCondicion,
            @Param("division") String division
    );

//
//    @Query(value = """
//    SELECT nota.nota_id AS notaId,
//           persona.persona_dni AS personaDni,
//           legajo.legajo_id AS personaLegajoId,
//           persona.persona_apellido AS personaApellido,
//           persona.persona_nombre AS personaNombre,
//           nota.nota_fecha_nota AS notaFechaNota,
//           nota.nota_calificacion_nota_numero AS notaCalificacionNotaNumero,
//           nota.nota_calificacion_nota_letra AS notaCalificacionNotaLetra,
//           nota.nota_estado AS notaEstado,
//           nota.nota_libro_nota AS notaLibroNota,
//           nota.nota_folio_nota AS notaFolioNota,
//           cursada.status AS cursadaStatus,
//           nota.nota_observaciones AS notaObservaciones,
//           nota.nota_usuario AS notaUsuario,
//           cursada.primer_parcial AS primerParcial,
//           cursada.recuperatorio1 AS recuperatorio1,
//           cursada.segundo_parcial AS segundoParcial,
//           cursada.recuperatorio2 AS recuperatorio2,
//           cursada.trabajos_practicos AS trabajosPracticos,
//           cursada.asistencia AS asistencia,
//           cursada.coloquio AS coloquio,
//           cursada.trabajo_institucional AS trabajoInstitucional
//    FROM persona
//    INNER JOIN legajo ON persona.persona_dni = legajo.legajo_alumno_dni
//    INNER JOIN cursada ON legajo.legajo_id = cursada.cursada_legajo_id
//    INNER JOIN materia_carrera ON cursada.cursada_materia_carrera_id = materia_carrera.id
//    INNER JOIN materia ON materia_carrera.materia_id = materia.materia_id
//    INNER JOIN carrera ON materia_carrera.carrera_id = carrera.carrera_id
//    INNER JOIN nota ON cursada.cursada_id = nota.nota_cursada_id
//    WHERE carrera.carrera_id = :carreraId
//      AND materia.materia_id = :materiaId
//      AND (cursada.cursada_inscripto = TRUE OR (:cursadaInscripto = FALSE))
//      AND nota.nota_condicion = :notaCondicion
//       AND materia_carrera.division= :division
//    ORDER BY persona.persona_apellido, persona.persona_nombre ASC
//   """, nativeQuery = true)
//    List<NotaCursadaDTO> findNotasByCarreraAndMateria(
//            @Param("carreraId") String carreraId,
//            @Param("materiaId") String materiaId,
//            @Param("cursadaInscripto") boolean cursadaInscripto,
//            @Param("notaCondicion") EstadoCondicion notaCondicion,
//            @Param("division") String division
//    );

    @Query(value = """
    SELECT nota.nota_id AS notaId,
           persona.persona_dni AS personaDni,
           legajo.legajo_id AS personaLegajoId,
           persona.persona_apellido AS personaApellido,
           persona.persona_nombre AS personaNombre,
           nota.nota_fecha_nota AS notaFechaNota,
           nota.nota_calificacion_nota_numero AS notaCalificacionNotaNumero,
           nota.nota_calificacion_nota_letra AS notaCalificacionNotaLetra,
           nota.nota_estado AS notaEstado,
           nota.nota_libro_nota AS notaLibroNota,
           nota.nota_folio_nota AS notaFolioNota,
           cursada.status AS cursadaStatus,
           nota.nota_observaciones AS notaObservaciones,
           nota.nota_usuario AS notaUsuario,
           cursada.primer_parcial AS primerParcial,
           cursada.recuperatorio1 AS recuperatorio1,
           cursada.segundo_parcial AS segundoParcial,
           cursada.recuperatorio2 AS recuperatorio2,
           cursada.trabajos_practicos AS trabajosPracticos,
           cursada.asistencia AS asistencia,
           cursada.coloquio AS coloquio,
           cursada.trabajo_institucional AS trabajoInstitucional
    FROM persona
    INNER JOIN legajo ON persona.persona_dni = legajo.legajo_alumno_dni
    INNER JOIN cursada ON legajo.legajo_id = cursada.cursada_legajo_id
    INNER JOIN materia_carrera ON cursada.cursada_materia_carrera_id = materia_carrera.id
    INNER JOIN materia ON materia_carrera.materia_id = materia.materia_id
    INNER JOIN carrera ON materia_carrera.carrera_id = carrera.carrera_id
    INNER JOIN nota ON cursada.cursada_id = nota.nota_cursada_id
    WHERE carrera.carrera_id = :carreraId
      AND materia.materia_id = :materiaId
      AND nota.nota_condicion = :notaCondicion
    ORDER BY persona.persona_apellido, persona.persona_nombre ASC
""", nativeQuery = true)
    List<NotaCursadaDTO> findNotasByCarreraAndMateriaNew(
            @Param("carreraId") String carreraId,
            @Param("materiaId") String materiaId,
            @Param("notaCondicion") EstadoCondicion notaCondicion
    );

    @Query(value = """
    SELECT nota.nota_id , persona.persona_dni,
           persona.persona_apellido , persona.persona_nombre ,
           legajo.legajo_id AS persona_legajo_id,
           nota.nota_fecha_nota ,
           nota.nota_calificacion_nota_numero ,
           nota.nota_calificacion_nota_letra ,
           nota.nota_estado ,
           nota.nota_libro_nota , nota.nota_folio_nota ,
           cursada.status , nota.nota_observaciones ,
           nota.nota_usuario,
           cursada.primer_parcial,
           cursada.recuperatorio1,
           cursada.segundo_parcial,
           cursada.recuperatorio2,
           cursada.trabajos_practicos,
           cursada.asistencia,
           cursada.coloquio,
           cursada.trabajo_institucional
    FROM persona
    INNER JOIN legajo ON persona.persona_dni = legajo.legajo_alumno_dni
    INNER JOIN cursada ON legajo.legajo_id = cursada.cursada_legajo_id
    INNER JOIN materia_carrera ON cursada.cursada_materia_carrera_id = materia_carrera.id
    INNER JOIN materia ON materia_carrera.materia_id = materia.materia_id
    INNER JOIN carrera ON materia_carrera.carrera_id = carrera.carrera_id
    INNER JOIN nota ON cursada.cursada_id = nota.nota_cursada_id
    WHERE carrera.carrera_id = :carreraId
      AND materia.materia_id = :materiaId
      AND nota.nota_condicion = :notaCondicion
             AND materia_carrera.division = :division
    ORDER BY persona.persona_apellido, persona.persona_nombre ASC
   """, nativeQuery = true)
    List<NotaCursadaDTO> findNotasByCarreraAndMateriaAll(
            @Param("carreraId") String carreraId,
            @Param("materiaId") String materiaId,
            @Param("notaCondicion") String notaCondicion,
            @Param("division") String division
    );


    @Query(value = """
    SELECT DISTINCT
        persona.persona_dni                  AS personaDni,
        persona.persona_nombre               AS personaNombre,
        legajo.legajo_id                     AS legajoId,
        persona.persona_apellido             AS personaApellido,
        nota.nota_id                         AS notaId,
        nota.nota_calificacion_nota_numero   AS notaCalificacionNotaNumero,
        nota.nota_calificacion_nota_letra    AS notaCalificacionNotaLetra,
        nota.nota_condicion                  AS notaCondicion,
        nota.nota_estado                     AS notaEstado,
        nota.nota_libro_nota                 AS notaLibro,
        nota.nota_folio_nota                 AS notaFolio,
        nota.nota_fecha_nota                 AS notaFechaNota,
        nota.nota_observaciones              AS notaObservaciones,
        nota.nota_usuario                    AS notaUsuario,
        permiso.permiso_id                   AS permisoId,
        examen.status                         AS status,
        materia.materia_orden        AS materiaOrden
    FROM nota
    INNER JOIN examen ON nota.nota_id = examen.nota_id
    INNER JOIN permiso ON permiso.permiso_id = examen.permiso_id
    INNER JOIN cursada_examen ON examen.cursada_examen_id = cursada_examen.cursada_examen_id
    INNER JOIN turno ON cursada_examen.turno_id = turno.turno_id
    INNER JOIN legajo ON permiso.permiso_legajo_id = legajo.legajo_id
    INNER JOIN materia ON cursada_examen.materia_id = materia.materia_id
    INNER JOIN materia_carrera ON materia.materia_id = materia_carrera.materia_id
    INNER JOIN carrera ON materia_carrera.carrera_id = carrera.carrera_id
    INNER JOIN persona ON legajo.legajo_alumno_dni = persona.persona_dni
    WHERE cursada_examen.cursada_examen_id = :cursadaExamenId
      AND examen.examen_inscripto = :examenInscripto
    ORDER BY persona.persona_apellido, persona.persona_nombre ASC
""", nativeQuery = true)
    List<Map<String, Object>> findExamenesByCursadaExamenIdMateriaCarrera(
            @Param("cursadaExamenId") Long cursadaExamenId,
            @Param("examenInscripto") boolean examenInscripto);

//    @Query(value = """
//        SELECT DISTINCT nota.nota_id,
//                        permiso.permiso_id,
//                        legajo.legajo_id,
//                        persona.persona_dni,
//                        persona.persona_apellido ,
//                        persona.persona_nombre,
//                        nota.nota_calificacion_nota_numero,
//                        nota.nota_calificacion_nota_letra,
//                        nota.nota_condicion,
//                        nota.nota_estado,
//                        nota.nota_libro_nota,
//                        nota.nota_folio_nota,
//                        nota.nota_fecha_nota,
//                        nota.nota_observaciones,
//                        examen.status,
//                        nota.nota_usuario
//        FROM nota
//        INNER JOIN examen ON nota.nota_id = examen.nota_id
//        INNER JOIN permiso ON permiso.permiso_id = examen.permiso_id
//        INNER JOIN cursada_examen ON examen.cursada_examen_id = cursada_examen.cursada_examen_id
//        INNER JOIN turno ON cursada_examen.turno_id = turno.turno_id
//        INNER JOIN legajo ON permiso.permiso_legajo_id = legajo.legajo_id
//        INNER JOIN materia ON cursada_examen.materia_id = materia.materia_id
//        INNER JOIN materia_carrera ON materia.materia_id = materia_carrera.materia_id
//        INNER JOIN carrera ON materia_carrera.carrera_id = carrera.carrera_id
//        INNER JOIN persona ON legajo.legajo_alumno_dni = persona.persona_dni
//        WHERE cursada_examen.cursada_examen_id = :cursadaExamenId
//        AND examen.examen_inscripto = :examenInscripto
//        ORDER BY persona.persona_apellido, persona.persona_nombre ASC
//        """, nativeQuery = true)
//    List<NotaExamenDTO> findExamenesByCursadaExamenIdMateriaCarrera(
//            @Param("cursadaExamenId") Long cursadaExamenId,
//            @Param("examenInscripto") boolean examenInscripto
//    );


    @Query(value = """
    SELECT DISTINCT 
        nota.nota_id,
        permiso.permiso_id,
        legajo.legajo_id,
        persona.persona_dni, 
        persona.persona_apellido,
        persona.persona_nombre,
        nota.nota_calificacion_nota_numero, 
        nota.nota_calificacion_nota_letra,
        nota.nota_condicion, 
        nota.nota_estado, 
        nota.nota_libro_nota, 
        nota.nota_folio_nota, 
        nota.nota_fecha_nota,
        nota.nota_observaciones, 
        examen.status,
        nota.nota_usuario,
        materia.materia_orden
    FROM nota
    INNER JOIN examen ON nota.nota_id = examen.nota_id
    INNER JOIN permiso ON permiso.permiso_id = examen.permiso_id
    INNER JOIN cursada_examen ON examen.cursada_examen_id = cursada_examen.cursada_examen_id
    INNER JOIN turno ON cursada_examen.turno_id = turno.turno_id
    INNER JOIN legajo ON permiso.permiso_legajo_id = legajo.legajo_id
    INNER JOIN materia ON cursada_examen.materia_id = materia.materia_id
    INNER JOIN materia_carrera ON materia.materia_id = materia_carrera.materia_id
    INNER JOIN carrera ON materia_carrera.carrera_id = carrera.carrera_id
    INNER JOIN persona ON legajo.legajo_alumno_dni = persona.persona_dni
    WHERE cursada_examen.cursada_examen_id = :cursadaExamenId
      AND examen.examen_inscripto = :examenInscripto  
      AND nota.nota_condicion = :notaCondicion    
    ORDER BY persona.persona_apellido, persona.persona_nombre ASC
""",
            nativeQuery = true)
    List<Object[]> findExamenesRaw(
            @Param("cursadaExamenId") Long cursadaExamenId,
            @Param("examenInscripto") boolean examenInscripto,
            @Param("notaCondicion") String notaCondicion
    );
    long countByCursadaId(Integer cursada_id);


    @Query("""
SELECT new com.example.iesiback.dto.EquivalenciaDTO(
    e.id,
    a.personaDni,
    a.personaApellido,
    a.personaNombre,
    e.materiaOrigen,
    e.institucionOrigen,
    e.resolucion,
    m.materiaNombre,
    n.notaCalificacionNotaNumero,
    e.status,
    n.notaFechaNota,
    n.notaCalificacionNotaLetra,
    n.notaLibroNota,
    n.notaFolioNota,
    n.notaUsuario
)
FROM Equivalencia e
JOIN e.nota n
JOIN n.cursada cu
JOIN cu.legajo l
JOIN l.legajoPersonaDni a
JOIN cu.materiaCarrera mc
JOIN mc.materia m
JOIN mc.carrera c
""")
    List<EquivalenciaDTO> listarEquivalenciasDetalladas();


   @Query("""
    SELECT new com.example.iesiback.dto.NotaMateriaDTO(
        n.notaId,
        m.materiaOrden,
        m.materiaNombre,
CAST(n.notaCalificacionNotaNumero AS string),
        n.notaCalificacionNotaLetra,
        n.notaCondicion,
        n.notaEstado,
        n.notaLibroNota,
        n.notaFolioNota,
        n.notaFechaNota,
        n.notaObservaciones,
        n.notaUsuario,
        mc.materia.materiaId,
        m.materiaNivel,
        c.materiaCarrera.id,
        mc.firma,
        mc.id
    )
    FROM Nota n
    JOIN n.cursada c
    JOIN c.materiaCarrera mc
    JOIN mc.materia m
    WHERE c.legajo.legajoId = :legajoId
    ORDER BY n.notaFechaNota DESC
""")
   List<NotaMateriaDTO> findUltimaNotaPorLegajo(@Param("legajoId") String legajoId);


    @Query("SELECT n.cursada FROM Nota n WHERE n.notaId = :notaId")
    Cursada findCursadaByNotaId(@Param("notaId") Long notaId);


//    @Query(value = "SELECT " +
//            "legajo.legajo_id AS legajoId, " +
//            "nota.nota_id AS notaId, " +
//            "nota.nota_fecha_nota AS notaFechaNota, " +
//            "materia.materia_id AS materiaId, " +
//            "materia.materia_nombre AS materiaNombre, " +
//            "materia.materia_orden AS materiaOrden, " +
//            "nota.nota_estado AS notaEstado, " +
//            "materia.materia_cursada AS materiaCursada, " +
//            "cursada.cursada_id AS cursadaId " +
//            "FROM nota " +
//            "INNER JOIN cursada ON nota.nota_cursada_id = cursada.cursada_id " +
//            "INNER JOIN materia_carrera ON cursada.cursada_materia_carrera_id = materia_carrera.id " +
//            "INNER JOIN materia ON materia_carrera.materia_id = materia.materia_id " +
//            "INNER JOIN legajo ON cursada.cursada_legajo_id = legajo.legajo_id " +
//            "WHERE nota.nota_estado = 'Cursando' " +
//            "AND legajo.legajo_id = :legajoId " +
//            "AND materia.materia_orden = :materiaOrden", nativeQuery = true)
//    List<NotaCursandoProjection> findNotaCursandoByLegajoAndMateria(@Param("legajoId") String legajoId,
//                                                                        @Param("materiaOrden") Integer materiaOrden);


    @Query(value = "SELECT " +
            "legajo.legajo_id AS legajoId, " +
            "nota.nota_id AS notaId, " +
            "nota.nota_fecha_nota AS notaFechaNota, " +
            "materia.materia_id AS materiaId, " +
            "materia.materia_nombre AS materiaNombre, " +
            "materia.materia_orden AS materiaOrden, " +
            "nota.nota_estado AS notaEstado, " +
            "materia.materia_cursada AS materiaCursada, " +
            "materia.materia_examen AS materiaExamen, " +
            "cursada.cursada_id AS cursadaId, " +
            "nota.nota_condicion AS notaCondicion " +
            "FROM nota " +
            "INNER JOIN cursada ON nota.nota_cursada_id = cursada.cursada_id " +
            "INNER JOIN materia_carrera ON cursada.cursada_materia_carrera_id = materia_carrera.id " +
            "INNER JOIN materia ON materia_carrera.materia_id = materia.materia_id " +
            "INNER JOIN legajo ON cursada.cursada_legajo_id = legajo.legajo_id " +
            "WHERE legajo.legajo_id = :legajoId " +
            "AND materia.materia_orden = :materiaOrden", nativeQuery = true)
    List<NotaCursandoProjection> findNotaCursandoByLegajoAndMateria(@Param("legajoId") String legajoId,
                                                                    @Param("materiaOrden") Integer materiaOrden);



    @Query(value = "SELECT " +
            "legajo.legajo_id AS legajoId, " +
            "nota.nota_id AS notaId, " +
            "nota.nota_fecha_nota AS notaFechaNota, " +
            "materia.materia_id AS materiaId, " +
            "materia.materia_nombre AS materiaNombre, " +
            "materia.materia_orden AS materiaOrden, " +
            "nota.nota_estado AS notaEstado, " +
            "materia.materia_cursada AS materiaCursada, " +
            "materia.materia_examen AS materiaExamen, " +
            "cursada.cursada_id AS cursadaId, " +
            "nota.nota_condicion AS notaCondicion " +
            "FROM nota " +
            "INNER JOIN cursada ON nota.nota_cursada_id = cursada.cursada_id " +
            "INNER JOIN materia_carrera ON cursada.cursada_materia_carrera_id = materia_carrera.id " +
            "INNER JOIN materia ON materia_carrera.materia_id = materia.materia_id " +
            "INNER JOIN legajo ON cursada.cursada_legajo_id = legajo.legajo_id " +
            "WHERE nota.nota_id = :notaId", nativeQuery = true)
    List<NotaCursandoProjection> findNotaNotaCursandoProjectionbyNotyaId(@Param("notaId") Long  notaId);


    @Query(value = "SELECT " +
            "legajo.legajo_id AS legajoId, " +
            "nota.nota_id AS notaId, " +
            "nota.nota_fecha_nota AS notaFechaNota, " +
            "materia.materia_id AS materiaId, " +
            "materia.materia_nombre AS materiaNombre, " +
            "materia.materia_orden AS materiaOrden, " +
            "nota.nota_estado AS notaEstado, " +
            "materia.materia_cursada AS materiaCursada, " +
            "materia.materia_examen AS materiaExamen, " +
            "cursada.cursada_id AS cursadaId, " +
            "nota.nota_condicion AS notaCondicion " +
            "FROM nota " +
            "INNER JOIN cursada ON nota.nota_cursada_id = cursada.cursada_id " +
            "INNER JOIN materia_carrera ON cursada.cursada_materia_carrera_id = materia_carrera.id " +
            "INNER JOIN materia ON materia_carrera.materia_id = materia.materia_id " +
            "INNER JOIN legajo ON cursada.cursada_legajo_id = legajo.legajo_id " +
            "WHERE legajo.legajo_id = :legajoId", nativeQuery = true)
    List<NotaCursandoProjection> findAllNotasByLegajo(@Param("legajoId") String legajoId);


    @Query(value = """
       SELECT new com.example.iesiback.dto.AlumnoCursadaMateriaNotaDTO(
           legajo.legajoId,
           persona.personaDni,
           persona.personaApellido,
           persona.personaNombre,
           legajo.legajoCambia,
           legajo.tituloEntregado,
           legajo.tituloSerie,
           legajo.tituloYear,
           legajo.tituloFecha,
           inscripcion.carrera.carreraId,
           legajo.legajoSede,
           legajo.legajoFotocopiaDni,
           legajo.legajoCertificadoNacimiento,
           legajo.legajoFotocopiaTitulo,
           legajo.folio,
           legajo.notasCorregidas
       )
       FROM Persona persona
       JOIN persona.legajos legajo
       JOIN legajo.inscripcion inscripcion
       WHERE inscripcion.carrera.carreraId = :carreraId
       ORDER BY persona.personaApellido, persona.personaNombre ASC
""")
    List<AlumnoCursadaMateriaNotaDTO> findAlumnosPorCarrera(@Param("carreraId") String carreraId);


    @Query("""
    SELECT new com.example.iesiback.dto.NotaMateriaDTO(
        n.notaId,
        m.materiaOrden,
        m.materiaNombre,
CAST(n.notaCalificacionNotaNumero AS string),
        n.notaCalificacionNotaLetra,
        n.notaCondicion,
        n.notaEstado,
        n.notaLibroNota,
        n.notaFolioNota,
        n.notaFechaNota,
        n.notaObservaciones,
        n.notaUsuario,
        mc.materia.materiaId,
        m.materiaNivel,
        mc.id,
        mc.firma,
        mc.id
    )
    FROM Nota n
    JOIN n.cursada c
    JOIN c.materiaCarrera mc
    JOIN mc.materia m
    WHERE c.legajo.legajoId = :legajoId AND n.notaEstado = 'Aprobado'
    ORDER BY n.notaFechaNota DESC
""")
    List<NotaMateriaDTO> findBylegajoId(@Param("legajoId") String legajoId);


    @Query("""
    SELECT n
    FROM Nota n
    WHERE n.cursada.id = :cursadaId
""")
    Optional<Nota> findByCursadaId(@Param("cursadaId") Integer cursadaId);

//
//    @Query(value = """
//        SELECT n.*
//        FROM nota n
//        INNER JOIN examen e ON e.nota_id = n.nota_id
//        WHERE n.nota_cursada_id = :cursadaId
//          AND e.permiso_id = :permisoId
//        LIMIT 1
//        """, nativeQuery = true)
//    Optional<Nota> findExamenPorCursadaYPermiso(
//            @Param("cursadaId") Integer cursadaId,
//            @Param("permisoId") Integer permisoId
//    );

    @Query("""
    SELECT n
    FROM Nota n
    JOIN n.examen e
    WHERE n.cursada.id = :cursadaId
      AND e.permiso.id = :permisoId
""")
    Optional<Nota> findExamenPorCursadaYPermiso(
            @Param("cursadaId") Integer cursadaId,
            @Param("permisoId") Integer permisoId
    );

    @Query("""
    SELECT n
    FROM Nota n
    JOIN n.examen e
    WHERE n.cursada.id = :cursadaId
      AND e.cursadaExamen.turno.turnoId = :turnoId
""")
    Optional<Nota> findExamenPorCursadaYTurnoId(
            @Param("cursadaId") Integer cursadaId,
            @Param("turnoId") Integer turnoId
    );




    Optional<Nota> findByCursadaIdAndNotaCondicion(Integer cursadaId, String notaCondicion);

    @Query("SELECT n FROM Nota n WHERE n.cursada.materiaCarrera.id = :materiaCarreraId AND n.notaCondicion = :estado")
    List<Nota> findByCursadaMateriaCarreraIdAndEstado(
            @Param("materiaCarreraId") Integer materiaCarreraId,
            @Param("estado") EstadoCondicion estado
    );



    @Query("""
    SELECT n FROM Nota n
    JOIN Examen e ON e.nota.notaId = n.notaId
    WHERE e.cursadaExamen.id = :cursadaExamenId
""")
    List<Nota> findByCursadaExamenId(
            @Param("cursadaExamenId") Integer cursadaExamenId
    );

}
