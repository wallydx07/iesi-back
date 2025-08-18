package com.example.iesiback.repositories;

import com.example.iesiback.entities.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, String> {

    @Query("SELECT c FROM Carrera c JOIN c.inscripcions i WHERE i.legajo.legajoId = :legajoId")
    Carrera findCarreraByLegajoId(@Param("legajoId") String legajoId);

    // Consulta para obtener todas las carreras ordenadas por carrera_year y carrera_nombre DESC
    @Query("SELECT c FROM Carrera c ORDER BY c.carreraYear DESC, c.carreraNombre DESC")
    List<Carrera> findAllOrderedByYearAndName();


    @Query(value = "SELECT c.* FROM carrera c " +
            "INNER JOIN inscripcion i ON c.carrera_id = i.carrera_id " +
            "INNER JOIN legajo l ON i.legajo_id = l.legajo_id " +
            "WHERE l.legajo_alumno_dni = :alumnoDni", nativeQuery = true)
    List<Carrera> findCarreraIdByAlumnoDni(@Param("alumnoDni") String alumnoDni);

    List<Carrera> findAllByOrderByCarreraYearDesc();

    @Query(value = "SELECT c.carrera_year FROM carrera c " +
            "INNER JOIN inscripcion i ON c.carrera_id = i.carrera_id " +
            "INNER JOIN legajo l ON i.legajo_id = l.legajo_id " +
            "WHERE l.legajo_id = :libretaEstudiantil", nativeQuery = true)
    Integer findCarreraYearByLibreta(@Param("libretaEstudiantil") String libretaEstudiantil);


    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END " +
            "FROM Inscripcion i " +
            "WHERE i.legajo.legajoAlumnoDni.alumnoDni = :dniAlumno " +
            "AND i.carrera.carreraNombre = :carreraNombre")
    boolean existsByAlumnoDniAndCarreraId(@Param("dniAlumno") String dniAlumno,
                                          @Param("carreraNombre") String carreraNombre);

}

