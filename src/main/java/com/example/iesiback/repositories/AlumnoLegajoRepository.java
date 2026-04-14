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
            "a.personaDni, " +
            "a.personaApellido, " +
            "a.personaNombre, " +
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
            "a.personaFechaNacimiento," +
            "a.personaCorreo," +
            "a.personaDomicilioCelular," +
            "l.legajoComision " +
            ") " +
            "FROM Legajo l " +
            "JOIN l.legajoPersonaDni a " +
            "JOIN Inscripcion i ON i.legajo = l " +
            "JOIN i.carrera c " +
            "WHERE (:carreraId IS NULL OR c.carreraId LIKE :carreraId) " +
            "AND (:estado IS NULL OR l.legajoEstado = :estado) " +
            "AND (:busqueda IS NULL OR a.personaApellido LIKE :busqueda OR a.personaNombre LIKE :busqueda OR CONCAT(a.personaApellido, ' ', a.personaNombre) LIKE :busqueda OR CAST(a.personaDni AS string) LIKE :busqueda) " +
            "ORDER BY a.personaApellido ASC, a.personaNombre ASC")
    List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosLegajos(
            @Param("carreraId") String carreraId,
            @Param("estado") String estado,
            @Param("busqueda") String busqueda);

//modificar

    @Query("SELECT DISTINCT new com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO(" +
            "l.legajoId, " +
            "a.personaDni, " +
            "a.personaApellido, " +
            "a.personaNombre, " +
            "l.inscripcion.carrera.carreraId, " +
            "l.legajoFotocopiaDni, " +
            "l.legajoCertificadoNacimiento, " +
            "l.legajoFotocopiaTitulo, " +
            "l.legajoPlanillaProntuarial, " +
            "l.legajoCarnetSanitario, " +
            "l.legajoFoto, " +
            "l.legajoAval, " +
            "l.legajoCarpetaColgante, " +
            "l.usuario, " +
            "a.personaFechaNacimiento, " +
            "a.personaCorreo, " +
            "a.personaDomicilioCelular," +
            "l.legajoComision " +
            ") " +
            "FROM Cursada cu " +
            "JOIN cu.legajo l " +
            "JOIN l.legajoPersonaDni a " +
            "JOIN cu.materiaCarrera mc " +
            "JOIN mc.carrera car " +
            "WHERE (:dato IS NULL OR car.carreraId LIKE CONCAT(:dato, '%')) " +
            "AND (:estado IS NULL OR l.legajoEstado = :estado) " +
            "AND (:apellido IS NULL OR a.personaApellido LIKE CONCAT(:apellido, '%') OR CAST(a.personaDni AS string) LIKE CONCAT(:apellido, '%')) " +

            "AND (:comision IS NULL OR l.legajoComision LIKE CONCAT(:comision, '%')) " +

            "ORDER BY a.personaApellido, a.personaNombre ASC")

    List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosConCursadas(
            @Param("dato") String dato,
            @Param("estado") String estado,
            @Param("apellido") String apellido,
            @Param("comision") String comision

    );



    @Query("SELECT DISTINCT new com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO(" +
            "l.legajoId, " +
            "a.personaDni, " +
            "a.personaApellido, " +
            "a.personaNombre, " +
            "l.inscripcion.carrera.carreraId, " +
            "l.legajoFotocopiaDni, " +
            "l.legajoCertificadoNacimiento, " +
            "l.legajoFotocopiaTitulo, " +
            "l.legajoPlanillaProntuarial, " +
            "l.legajoCarnetSanitario, " +
            "l.legajoFoto, " +
            "l.legajoAval, " +
            "l.legajoCarpetaColgante, " +
            "l.usuario, " +
            "a.personaFechaNacimiento, " +
            "a.personaCorreo, " +
            "a.personaDomicilioCelular, " +
            "l.legajoComision " +
            ") " +
            "FROM Legajo l " +
            "JOIN l.legajoPersonaDni a " +
            "LEFT JOIN l.cursadas cu " +              // ← LEFT JOIN
            "LEFT JOIN cu.materiaCarrera mc " +       // ← LEFT JOIN
            "LEFT JOIN mc.carrera car " +             // ← LEFT JOIN
            "WHERE (:dato IS NULL OR car.carreraId LIKE CONCAT(:dato, '%') " +
            "       OR (:dato IS NULL AND car IS NULL)) " +  // ← si no tiene cursada, igual lo incluye
            "AND (:estado IS NULL OR l.legajoEstado = :estado) " +
            "AND (:apellido IS NULL OR " +
            "     a.personaApellido LIKE CONCAT(:apellido, '%') OR " +
            "     STR(a.personaDni) LIKE CONCAT(:apellido, '%')) " +
            "AND (:comision IS NULL OR l.legajoComision LIKE CONCAT(:comision, '%')) " +
            "ORDER BY a.personaApellido ASC, a.personaNombre ASC")
    List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosConOCinCursadas(
            @Param("dato") String dato,
            @Param("estado") String estado,
            @Param("apellido") String apellido,
            @Param("comision") String comision
    );






    @Query("""
    SELECT new com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO(
        l.legajoId,
        a.personaDni,
        a.personaApellido,
        a.personaNombre,
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
        a.personaFechaNacimiento,
        a.personaCorreo,
        a.personaDomicilioCelular,
        l.legajoComision
    )
    FROM Legajo l
    JOIN l.legajoPersonaDni a
    JOIN Cursada cu ON cu.legajo = l
    JOIN MateriaCarrera mc ON cu.materiaCarrera.id = mc.id
    JOIN mc.carrera c
    WHERE (mc.id = :dato)
      AND (l.legajoEstado = :estado)
    ORDER BY a.personaApellido ASC, a.personaNombre ASC
""")

    List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosMateriaCursadaId(
            @Param("dato") Long dato,
            @Param("estado") String estado
    );



    @Query("SELECT DISTINCT new com.example.iesiback.dto.AlumnoLegajoInscripcionCarreraDTO(" +
            "l.legajoId, " +
            "a.personaDni, " +
            "a.personaApellido, " +
            "a.personaNombre, " +
            "l.inscripcion.carrera.carreraId, " +
            "l.legajoFotocopiaDni, " +
            "l.legajoCertificadoNacimiento, " +
            "l.legajoFotocopiaTitulo, " +
            "l.legajoPlanillaProntuarial, " +
            "l.legajoCarnetSanitario, " +
            "l.legajoFoto, " +
            "l.legajoAval, " +
            "l.legajoCarpetaColgante, " +
            "l.usuario, " +
            "a.personaFechaNacimiento, " +
            "a.personaCorreo, " +
            "a.personaDomicilioCelular, " +
            "l.legajoComision " +
            ") " +
            "FROM Legajo l " +
            "JOIN l.legajoPersonaDni a " +
            "LEFT JOIN l.cursadas cu " +
            "LEFT JOIN cu.materiaCarrera mc " +
            "LEFT JOIN mc.carrera car " +
            "WHERE ( " +
            "    (:dato IS NULL OR :dato = '') " +
            "    OR (car IS NOT NULL AND car.carreraId LIKE CONCAT(:dato, '%')) " +
            ") " +
            "AND (:estado IS NULL OR :estado = '' OR l.legajoEstado = :estado) " +
            "AND (:apellido IS NULL OR :apellido = '' OR LOWER(a.personaApellido) LIKE CONCAT(LOWER(:apellido), '%')) " +
            "AND (:nombre IS NULL OR :nombre = '' OR LOWER(a.personaNombre) LIKE CONCAT(LOWER(:nombre), '%')) " +
            "AND (:comision IS NULL OR :comision = '' OR l.legajoComision LIKE CONCAT(:comision, '%')) " +
            "ORDER BY a.personaApellido ASC, a.personaNombre ASC")
    List<AlumnoLegajoInscripcionCarreraDTO> obtenerAlumnosConCursadasNombre(
            @Param("dato")     String dato,
            @Param("estado")   String estado,
            @Param("apellido") String apellido,
            @Param("nombre")   String nombre,       // ← nuevo parámetro
            @Param("comision") String comision
    );


}