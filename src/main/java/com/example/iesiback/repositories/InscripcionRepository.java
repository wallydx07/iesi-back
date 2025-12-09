package com.example.iesiback.repositories;

import com.example.iesiback.entities.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    // Se asume que la entidad Legajo tiene una propiedad 'legajoId' (puede ser String o Integer según corresponda)
    Inscripcion findByLegajo_LegajoId(String legajoId);
    //===================================================================
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END " +
                  "FROM Inscripcion i " +
                  "JOIN i.legajo l " +
                  "JOIN l.legajoPersonaDni a " +
                  "JOIN i.carrera c " +
                  "WHERE a.personaDni = :alumnoDni " +
                  "AND c.carreraNombre = :carreraNombre")
    boolean existsByAlumnoDniAndCarreraNombre(@Param("alumnoDni") String alumnoDni,
                                              @Param("carreraNombre") String carreraNombre);

    @Query("SELECT i FROM Inscripcion i WHERE i.legajo.legajoId = :legajoId")
    Inscripcion findInscripcionByLegajoId(@Param("legajoId") String legajoId);


//    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END " +
//            "FROM Inscripcion i " +
//            "WHERE i.legajo.legajoAlumnoDni = :dniAlumno " +
//            "AND i.carrera.carreraId = :carreraId")
//    boolean existsByAlumnoDniAndCarreraId(@Param("dniAlumno") String dniAlumno,
//                                          @Param("carreraId") String carreraId);
//
}
