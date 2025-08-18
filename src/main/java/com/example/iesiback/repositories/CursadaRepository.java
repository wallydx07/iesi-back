package com.example.iesiback.repositories;

import com.example.iesiback.entities.Cursada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursadaRepository extends JpaRepository<Cursada, Integer> {

    @Query("SELECT c FROM Cursada c " +
            "JOIN c.materiaCarrera mc " +
            "JOIN mc.materia m " +
            "WHERE m.materiaOrden = :materiaOrden AND c.legajo.legajoId = :legajoId")
    List<Cursada> findByMateriaOrdenAndLegajoId(@Param("materiaOrden") String materiaOrden,
                                                    @Param("legajoId") String legajoId);

    @Query("SELECT c FROM Cursada c LEFT JOIN FETCH c.notas WHERE c.legajo.legajoId = :legajoId")
    List<Cursada> findByLegajoId(@Param("legajoId") String legajoId);

    @Query(value = """
        SELECT c.*
        FROM cursada c
        INNER JOIN materia_carrera mc ON c.cursada_materia_carrera_id = mc.id
        WHERE c.cursada_legajo_id = :legajoId
          AND mc.materia_id = :materiaId
        """,
            nativeQuery = true)
    List<Cursada> findByLegajoAndMateria(
            @Param("legajoId") String legajoId,
            @Param("materiaId") String materiaId);


    @Query(value = """
    SELECT cu.cursada_inscripto
    FROM cursada cu
    INNER JOIN materia_carrera mc ON cu.cursada_materia_carrera_id = mc.id
    INNER JOIN carrera ca ON mc.carrera_id = ca.carrera_id
    WHERE cu.cursada_legajo_id = :legajoId
      AND mc.materia_id = :materiaId
     AND ca.carrera_year = :materiaYear
    """, nativeQuery = true)
    Optional<Boolean> findEstadoByLegajoAndMateria(@Param("legajoId") String legajoId,
                                                   @Param("materiaId") String materiaId,
                                                   @Param("materiaYear") Integer materiaYear);


    Optional<Cursada> findByLegajo_LegajoIdAndMateriaCarrera_Id(String legajoId, Integer materiaCarreraId);

};









