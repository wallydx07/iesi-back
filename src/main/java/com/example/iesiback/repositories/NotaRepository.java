package com.example.iesiback.repositories;

import com.example.iesiback.dto.NotaMateriaDTO;
import com.example.iesiback.entities.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {

    @Query("SELECT n FROM Nota n LEFT JOIN FETCH n.cursadas LEFT JOIN FETCH n.equivalencias LEFT JOIN FETCH n.examen")
    List<Nota> findAllWithRelations();

    @Query("SELECT n FROM Nota n LEFT JOIN FETCH n.cursadas LEFT JOIN FETCH n.equivalencias LEFT JOIN FETCH n.examen WHERE n.legajo.legajoId = :legajoId")
    List<Nota> findNotasByLegajoId(@Param("legajoId") String legajoId);

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
            mc.materia_id 
        FROM nota n
        INNER JOIN cursada c ON n.nota_id = c.cursada_nota_id
        INNER JOIN materia_carrera mc ON c.cursada_materia_carrera_id = mc.id
        INNER JOIN materia m ON mc.materia_id = m.materia_id
        WHERE n.legajo_id = :legajoId

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
            m.materia_id 
        FROM nota n
        INNER JOIN examen e ON e.nota_id = n.nota_id
        INNER JOIN permiso p ON e.permiso_id = p.permiso_id
        INNER JOIN cursada_examen ce ON e.cursada_examen_id = ce.cursada_examen_id
        INNER JOIN materia m ON ce.materia_id = m.materia_id
        WHERE n.legajo_id = :legajoId
        ORDER BY materia_orden ASC, nota_estado
        """, nativeQuery = true)
        List<Object[]> findTodasNotasByLegajo(@Param("legajoId") String legajoId);


    }
