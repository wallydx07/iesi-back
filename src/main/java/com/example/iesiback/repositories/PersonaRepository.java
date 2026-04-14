package com.example.iesiback.repositories;
import com.example.iesiback.dto.PersonaDTO;
import com.example.iesiback.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, String> {

//
//    @Query("SELECT CONCAT(a.alumnoApellido,',',a.alumnoNombre,'-',l.legajoId) " +
//            "FROM Alumno a " +
//            "JOIN Legajo l ON a.alumnoDni = l.legajoAlumnoDni.alumnoDni " +
//            "WHERE LOWER(a.alumnoApellido) LIKE LOWER(CONCAT(:apellido, '%')) " +
//            "ORDER BY a.alumnoApellido ASC, a.alumnoNombre ASC")
//    List<String> buscarPorApellido(@Param("apellido") String apellido);
//

    @Query("SELECT CONCAT(a.personaApellido, ',', a.personaNombre, '-', l.legajoId) " +
            "FROM Persona a " +
            "JOIN Legajo l ON a.personaDni = l.legajoPersonaDni.personaDni " +
            "WHERE (:busqueda IS NULL OR " +
            "      LOWER(CAST(a.personaDni AS string)) LIKE LOWER(:busqueda) OR " +
            "      LOWER(a.personaApellido) LIKE LOWER(:busqueda) OR " +
            "      LOWER(a.personaNombre) LIKE LOWER(:busqueda) OR " +
            "      LOWER(CONCAT(a.personaApellido, ' ', a.personaNombre)) LIKE LOWER(:busqueda)) " +
            "ORDER BY a.personaApellido ASC, a.personaNombre ASC")
    List<String> buscarPorDniApellidoNombre(@Param("busqueda") String busqueda);


    @Query("SELECT CONCAT(a.personaApellido, ',', a.personaNombre, '-', a.personaDni) " +
            "FROM Persona a " +
            "JOIN Personal p ON p.id = a.personaDni " +
            "WHERE (:busqueda IS NULL OR " +
            "      LOWER(CAST(a.personaDni AS string)) LIKE LOWER(:busqueda) OR " +
            "      LOWER(a.personaApellido) LIKE LOWER(:busqueda) OR " +
            "      LOWER(a.personaNombre) LIKE LOWER(:busqueda) OR " +
            "      LOWER(CONCAT(a.personaApellido, ' ', a.personaNombre)) LIKE LOWER(:busqueda) OR " +
            "      LOWER(p.personalApellido) LIKE LOWER(:busqueda) OR " +
            "      LOWER(p.personalNombre) LIKE LOWER(:busqueda)) " +
            "ORDER BY a.personaApellido ASC, a.personaNombre ASC")
    List<String> buscarPersonalPorDniApellidoNombre(@Param("busqueda") String busqueda);



    @Query("SELECT CONCAT(a.personaApellido, ', ', a.personaNombre, '-', l.legajoId) " +
            "FROM Persona a " +
            "JOIN Legajo l ON a.personaDni = l.legajoPersonaDni.personaDni " +
            "JOIN Inscripcion i ON l.legajoId = i.legajo.legajoId " +
            "WHERE (:busqueda IS NULL OR " +
            "       LOWER(a.personaApellido) LIKE LOWER(:busqueda) OR " +
            "       LOWER(a.personaNombre) LIKE LOWER(:busqueda) OR " +
            "       LOWER(CONCAT(a.personaApellido, ' ', a.personaNombre)) LIKE LOWER(:busqueda) OR " +
            "       CAST(a.personaDni AS string) LIKE :busqueda) " +
            "AND (:carreraNombre IS NULL OR LOWER(i.carrera.carreraNombre) = LOWER(:carreraNombre)) " +
            "ORDER BY a.personaApellido ASC, a.personaNombre ASC")
    List<String> buscarPorApellidoYCarrera(@Param("busqueda") String busqueda,
                                           @Param("carreraNombre") String carreraNombre);


    @Query(value = "SELECT * FROM alumno WHERE CAST(alumno_dni AS text) LIKE CONCAT('%', :dni, '%')", nativeQuery = true)
    List<Persona> buscarPorDni(@Param("dni") String dni);

    @Query("SELECT a FROM Persona a " +
            "JOIN Legajo l ON l.legajoPersonaDni.personaDni = a.personaDni" +
            " WHERE l.legajoId = :legajoId")
    Persona findAlumnoByLegajoId(@Param("legajoId") String legajoId);



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

    @Query(value = """
    SELECT l.legajo_id
    FROM legajo l
    INNER JOIN inscripcion i ON l.legajo_id = i.legajo_id
    INNER JOIN carrera c ON i.carrera_id = c.carrera_id
    WHERE c.carrera_year = :year
      AND l.legajo_estado = 'Activo'
""", nativeQuery = true)
    List<String> obtenerLegajosPorCarreraYearNative(@Param("year") Integer year);


    @Query("""
    SELECT new com.example.iesiback.dto.PersonaDTO(
        p.personaDni,
        p.personaApellido,
        p.personaNombre,
        p.personaCorreo,
        p.personaDomicilioCelular
    )
    FROM Persona p
    WHERE p.personaDni = :personaDni
""")
    Optional<PersonaDTO> findPersonaDTObyDNI(@Param("personaDni") Long personaDni);

        @Modifying
        @Transactional
        @Query(value = "UPDATE persona SET persona_dni = :nuevo WHERE persona_dni = :actual", nativeQuery = true)
        int actualizarDni(@Param("actual") Long actual,
                          @Param("nuevo") Long nuevo);


}
