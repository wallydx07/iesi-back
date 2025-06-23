package com.example.iesiback.repositories;
import com.example.iesiback.dto.AlumnoAsistenciaDTO;
import com.example.iesiback.entities.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, String> {

//
//    @Query("SELECT CONCAT(a.alumnoApellido,',',a.alumnoNombre,'-',l.legajoId) " +
//            "FROM Alumno a " +
//            "JOIN Legajo l ON a.alumnoDni = l.legajoAlumnoDni.alumnoDni " +
//            "WHERE LOWER(a.alumnoApellido) LIKE LOWER(CONCAT(:apellido, '%')) " +
//            "ORDER BY a.alumnoApellido ASC, a.alumnoNombre ASC")
//    List<String> buscarPorApellido(@Param("apellido") String apellido);
//

    @Query("SELECT CONCAT(a.alumnoApellido, ',', a.alumnoNombre, '-', l.legajoId) " +
            "FROM Alumno a " +
            "JOIN Legajo l ON a.alumnoDni = l.legajoAlumnoDni.alumnoDni " +
            "WHERE (:busqueda IS NULL OR " +
            "      LOWER(CAST(a.alumnoDni AS string)) LIKE LOWER(:busqueda) OR " +
            "      LOWER(a.alumnoApellido) LIKE LOWER(:busqueda) OR " +
            "      LOWER(a.alumnoNombre) LIKE LOWER(:busqueda) OR " +
            "      LOWER(CONCAT(a.alumnoApellido, ' ', a.alumnoNombre)) LIKE LOWER(:busqueda)) " +
            "ORDER BY a.alumnoApellido ASC, a.alumnoNombre ASC")
    List<String> buscarPorDniApellidoNombre(@Param("busqueda") String busqueda);



    @Query("SELECT CONCAT(a.alumnoApellido, ', ', a.alumnoNombre, '-', l.legajoId) " +
            "FROM Alumno a " +
            "JOIN Legajo l ON a.alumnoDni = l.legajoAlumnoDni.alumnoDni " +
            "JOIN Inscripcion i ON l.legajoId = i.legajo.legajoId " +
            "WHERE (:busqueda IS NULL OR " +
            "       LOWER(a.alumnoApellido) LIKE LOWER(:busqueda) OR " +
            "       LOWER(a.alumnoNombre) LIKE LOWER(:busqueda) OR " +
            "       LOWER(CONCAT(a.alumnoApellido, ' ', a.alumnoNombre)) LIKE LOWER(:busqueda) OR " +
            "       CAST(a.alumnoDni AS string) LIKE :busqueda) " +
            "AND (:carreraNombre IS NULL OR LOWER(i.carrera.carreraNombre) = LOWER(:carreraNombre)) " +
            "ORDER BY a.alumnoApellido ASC, a.alumnoNombre ASC")
    List<String> buscarPorApellidoYCarrera(@Param("busqueda") String busqueda,
                                           @Param("carreraNombre") String carreraNombre);


    @Query(value = "SELECT * FROM alumno WHERE CAST(alumno_dni AS text) LIKE CONCAT('%', :dni, '%')", nativeQuery = true)
    List<Alumno> buscarPorDni(@Param("dni") String dni);

    @Query("SELECT a FROM Alumno a " +
            "JOIN Legajo l ON l.legajoAlumnoDni.alumnoDni = a.alumnoDni" +
            " WHERE l.legajoId = :legajoId")
    Alumno findAlumnoByLegajoId(@Param("legajoId") String legajoId);
//
//    @Query("""
//        SELECT new com.example.iesiback.dto.AlumnoAsistenciaDTO(
//            a.alumnoDni, a.alumnoApellido, a.alumnoNombre, l.legajoId
//        )
//        FROM Alumno a
//        JOIN Legajo l ON a.alumnoDni = l.legajoAlumnoDni.alumnoDni
//        JOIN Cursada c ON l.legajoId = c.legajo.legajoId
//        JOIN MateriaCarrera mc ON c.materiaCarrera.id = mc.id
//        JOIN Materia m ON mc.materia.materiaId = m.materiaId
//        JOIN Carrera ca ON mc.carrera.carreraId = ca.carreraId
//        WHERE ca.carreraId = :carreraId
//          AND m.materiaNombre = :materiaNombre
//        ORDER BY a.alumnoApellido, a.alumnoNombre
//    """)
//    List<AlumnoAsistenciaDTO> obtenerAlumnosParaAsistencia(
//            @Param("carreraId") String carreraId,
//            @Param("materiaNombre") String materiaNombre
//    );





}
