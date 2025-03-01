package com.example.iesiback.repositories;
import com.example.iesiback.entities.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlumnoRepository extends JpaRepository<Alumno, String> {


    @Query("SELECT CONCAT(a.alumnoApellido, ', ', a.alumnoNombre, '-', l.legajoId) " +
            "FROM Alumno a " +
            "JOIN Legajo l ON a.alumnoDni = l.legajoAlumnoDni.alumnoDni " +
            "WHERE LOWER(a.alumnoApellido) LIKE LOWER(CONCAT(:apellido, '%')) " +
            "ORDER BY a.alumnoApellido ASC, a.alumnoNombre ASC")
    List<String> buscarPorApellido(@Param("apellido") String apellido);

    @Query(value = "SELECT * FROM alumno WHERE CAST(alumno_dni AS text) LIKE CONCAT('%', :dni, '%')", nativeQuery = true)
    List<Alumno> buscarPorDni(@Param("dni") String dni);

}
