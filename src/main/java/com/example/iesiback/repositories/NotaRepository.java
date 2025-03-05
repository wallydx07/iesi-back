package com.example.iesiback.repositories;

import com.example.iesiback.dto.NotaExamenDTO;
import com.example.iesiback.dto.NotaCursadaDTO;
import com.example.iesiback.entities.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {

        @Query(value = """
        SELECT 
            n.nota_id,
            m.materia_orden,
            m.materia_nombre,
            n.nota_calificacion_nota_numero,
            n.nota_calificacion_nota_letra,
            n.nota_condicion,
            n.nota_estado,
            n.nota_libro_nota,
            n.nota_folio_nota,
            n.nota_fecha_nota,
            n.nota_observaciones,
            n.nota_usuario,
            n.nota_cursada_id,
            mc.materia_id,
            m.materia_nivel
        FROM nota n
        INNER JOIN cursada c ON n.nota_cursada_id = c.cursada_id
        INNER JOIN materia_carrera mc ON c.cursada_materia_carrera_id = mc.id
        INNER JOIN materia m ON mc.materia_id = m.materia_id
        WHERE c.cursada_legajo_id = :legajoId
        UNION ALL
        SELECT 
            n.nota_id,
            m.materia_orden,
            m.materia_nombre,
            n.nota_calificacion_nota_numero,
            n.nota_calificacion_nota_letra,
            n.nota_condicion,
            n.nota_estado,
            n.nota_libro_nota,
            n.nota_folio_nota,
            n.nota_fecha_nota,
            n.nota_observaciones,
            n.nota_usuario,
            n.nota_cursada_id,
            m.materia_id,
            m.materia_nivel
        FROM nota n
        INNER JOIN examen e ON e.nota_id = n.nota_id
        INNER JOIN permiso p ON e.permiso_id = p.permiso_id
        INNER JOIN cursada_examen ce ON e.cursada_examen_id = ce.cursada_examen_id
        INNER JOIN materia m ON ce.materia_id = m.materia_id
        WHERE p.permiso_legajo_id = :legajoId
        ORDER BY materia_orden ASC, nota_estado
        """, nativeQuery = true)
        List<Object[]> findTodasNotasByLegajo(@Param("legajoId") String legajoId);



    @Query(value = """
    SELECT nota.nota_id , alumno.alumno_dni,\s
           alumno.alumno_apellido , alumno.alumno_nombre ,\s
           nota.nota_fecha_nota ,\s
           nota.nota_calificacion_nota_numero ,\s
           nota.nota_calificacion_nota_letra ,\s
           nota.nota_estado ,\s
           nota.nota_libro_nota , nota.nota_folio_nota ,\s
           cursada.status , nota.nota_observaciones ,\s
           nota.nota_usuario\s
    FROM alumno\s
    INNER JOIN legajo ON alumno.alumno_dni = legajo.legajo_alumno_dni\s
    INNER JOIN cursada ON legajo.legajo_id = cursada.cursada_legajo_id\s
    INNER JOIN materia_carrera ON cursada.cursada_materia_carrera_id = materia_carrera.id\s
    INNER JOIN materia ON materia_carrera.materia_id = materia.materia_id\s
    INNER JOIN carrera ON materia_carrera.carrera_id = carrera.carrera_id\s
    INNER JOIN nota ON cursada.cursada_id = nota.nota_cursada_id\s
    WHERE carrera.carrera_id = :carreraId\s
      AND materia.materia_id = :materiaId
      AND cursada_inscripto = :cursadaInscripto\s
       AND nota.nota_condicion= :notaCondicion\s
    ORDER BY alumno.alumno_apellido, alumno.alumno_nombre ASC
   \s""", nativeQuery = true)
    List<NotaCursadaDTO> findNotasByCarreraAndMateria(
            @Param("carreraId") String carreraId,
            @Param("materiaId") String materiaId,
            @Param("cursadaInscripto") boolean cursadaInscripto,
            @Param("notaCondicion") String notaCondicion
    );


    @Query(value = """
        SELECT DISTINCT nota.nota_id,
                        permiso.permiso_id,
                        legajo.legajo_id,
                        alumno.alumno_dni, 
                        alumno.alumno_apellido ,
                        alumno.alumno_nombre,
                        nota.nota_calificacion_nota_numero, 
                        nota.nota_calificacion_nota_letra,
                        nota.nota_condicion, 
                        nota.nota_estado, 
                        nota.nota_libro_nota, 
                        nota.nota_folio_nota, 
                        nota.nota_fecha_nota,
                        nota.nota_observaciones, 
                        examen.status,
                        nota.nota_usuario
        FROM nota
        INNER JOIN examen ON nota.nota_id = examen.nota_id
        INNER JOIN permiso ON permiso.permiso_id = examen.permiso_id
        INNER JOIN cursada_examen ON examen.cursada_examen_id = cursada_examen.cursada_examen_id
        INNER JOIN turno ON cursada_examen.turno_id = turno.turno_id
        INNER JOIN legajo ON permiso.permiso_legajo_id = legajo.legajo_id
        INNER JOIN materia ON cursada_examen.materia_id = materia.materia_id
        INNER JOIN materia_carrera ON materia.materia_id = materia_carrera.materia_id
        INNER JOIN carrera ON materia_carrera.carrera_id = carrera.carrera_id
        INNER JOIN alumno ON legajo.legajo_alumno_dni = alumno.alumno_dni
        WHERE cursada_examen.cursada_examen_id = :cursadaExamenId
        AND examen.examen_inscripto = :examenInscripto      
        ORDER BY alumno.alumno_apellido, alumno.alumno_nombre ASC
        """, nativeQuery = true)
    List<NotaExamenDTO> findExamenesByCursadaExamenIdMateriaCarrera(
            @Param("cursadaExamenId") Long cursadaExamenId,
            @Param("examenInscripto") boolean examenInscripto
    );




    }
