package com.example.iesiback.repositories;
import com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO;
import com.example.iesiback.entities.Legajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface AlumnoLegajoRepository extends JpaRepository<Legajo, Long> {

    @Query("SELECT new com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO(" +
            "l.legajoId, " +
            "a.alumnoDni, " +
            "a.alumnoApellido, " +
            "a.alumnoNombre, " +
            "c.carreraId, " +
            "l.legajoFotocopiaDni, " +
            "l.legajoCertificadoNacimiento, " +
            "l.legajoFotocopiaTitulo, " +
            "l.legajoPlanillaProntuarial, " +
            "l.legajoCarnetSanitario, " +
            "l.legajoFoto, " +
            "l.legajoAval, " +
            "l.legajoCarpetaColgante, " +
            "l.usuario," +
            "a.alumnoFechaNacimiento," +
            "a.domicilioAlumnoCorreo," +
            "a.domicilioAlumnoCelular" +
            ") " +
            "FROM Legajo l " +
            "JOIN l.legajoAlumnoDni a " +
            "JOIN Inscripcion i ON i.legajo = l " +
            "JOIN i.carrera c " +
            "WHERE (:carreraId IS NULL OR c.carreraId LIKE :carreraId) " +
            "AND (:estado IS NULL OR l.legajoEstado = :estado) " +
            "AND (:busqueda IS NULL OR a.alumnoApellido LIKE :busqueda OR a.alumnoNombre LIKE :busqueda OR CONCAT(a.alumnoApellido, ' ', a.alumnoNombre) LIKE :busqueda OR CAST(a.alumnoDni AS string) LIKE :busqueda) " +
            "ORDER BY a.alumnoApellido ASC, a.alumnoNombre ASC")
    List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosLegajos(
            @Param("carreraId") String carreraId,
            @Param("estado") String estado,
            @Param("busqueda") String busqueda);



    @Query("SELECT DISTINCT new com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO(" +
            "l.legajoId, " +
            "a.alumnoDni, " +
            "a.alumnoApellido, " +
            "a.alumnoNombre, " +
            "car.carreraId, " +
            "l.legajoFotocopiaDni, " +
            "l.legajoCertificadoNacimiento, " +
            "l.legajoFotocopiaTitulo, " +
            "l.legajoPlanillaProntuarial, " +
            "l.legajoCarnetSanitario, " +
            "l.legajoFoto, " +
            "l.legajoAval, " +
            "l.legajoCarpetaColgante, " +
            "l.usuario, " +
            "a.alumnoFechaNacimiento, " +
            "a.domicilioAlumnoCorreo, " +
            "a.domicilioAlumnoCelular" +
            ") " +
            "FROM Cursada cu " +
            "JOIN cu.legajo l " +
            "JOIN l.legajoAlumnoDni a " +
            "JOIN cu.materiaCarrera mc " +
            "JOIN mc.carrera car " +
            "WHERE (:dato IS NULL OR car.carreraId LIKE CONCAT(:dato, '%')) " +
            "AND (:estado IS NULL OR l.legajoEstado = :estado) " +
            "AND (:apellido IS NULL OR a.alumnoApellido LIKE CONCAT(:apellido, '%') OR CAST(a.alumnoDni AS string) LIKE CONCAT(:apellido, '%')) " +
            "ORDER BY a.alumnoApellido, a.alumnoNombre ASC")
    List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosConCursadas(
            @Param("dato") String dato,
            @Param("estado") String estado,
            @Param("apellido") String apellido);

    @Query("""
    SELECT new com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO(
        l.legajoId,
        a.alumnoDni,
        a.alumnoApellido,
        a.alumnoNombre,
        c.carreraId,
        l.legajoFotocopiaDni,
        l.legajoCertificadoNacimiento,
        l.legajoFotocopiaTitulo,
        l.legajoPlanillaProntuarial,
        l.legajoCarnetSanitario,
        l.legajoFoto,
        l.legajoAval,
        l.legajoCarpetaColgante,
        l.usuario,
        a.alumnoFechaNacimiento,
        a.domicilioAlumnoCorreo,
        a.domicilioAlumnoCelular
    )
    FROM Legajo l
    JOIN l.legajoAlumnoDni a
    JOIN Cursada cu ON cu.legajo = l
    JOIN MateriaCarrera mc ON cu.materiaCarrera.id = mc.id
    JOIN mc.carrera c
    WHERE (mc.id = :dato)
      AND (l.legajoEstado = :estado)
    ORDER BY a.alumnoApellido ASC, a.alumnoNombre ASC
""")
    List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosMateriaCursadaId(
            @Param("dato") Long dato,
            @Param("estado") String estado
    );



}