package com.example.iesiback.repositories;

import com.example.iesiback.entities.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    Optional<Permiso> findByPermisoLegajoIdAndPermisoFecha(String permisoLegajoId, java.time.LocalDate permisoFecha);
    @Query("SELECT DISTINCT c.carreraNombre FROM Carrera c " +
            "INNER JOIN MateriaCarrera mc ON c.carreraId= mc.carrera.carreraId " +
            "INNER JOIN Cursada cu ON mc.id = cu.cursadaMateriaCarrera.id " +
            "INNER JOIN Legajo l ON cu.legajo.legajoId = l.legajoId " +
            "WHERE l.legajoId = :libreta")
    String obtenerCarreraPorLibreta(@Param("libreta") String libreta);

    @Query("SELECT p.id FROM Permiso p " +
            "INNER JOIN Examen e ON p.id = e.permiso.id " +
            "INNER JOIN CursadaExamen ce ON e.cursadaExamen.id = ce.id " +
            "WHERE ce.turno.turnoId = :turno " +
            "AND p.permisoLegajoId = :libreta")
    String obtenerPermisoId(@Param("libreta") String libreta, @Param("turno") String turno);

    @Query("SELECT l.legajoAlumnoDni.alumnoDni FROM Legajo l WHERE l.legajoId = :libreta")
    int obtenerDniPorLibreta(@Param("libreta") String libreta);

    @Query("SELECT a.alumnoNombre FROM Alumno a WHERE a.alumnoDni = :dni")
    String obtenerNombrePorDni(@Param("dni") int dni);

    @Query("SELECT a.alumnoApellido FROM Alumno a WHERE a.alumnoDni = :dni")
    String obtenerApellidoPorDni(@Param("dni") int dni);

    @Query("SELECT DISTINCT p FROM Permiso p " +
            "INNER JOIN Examen e ON p.permisoLegajoId = e.permiso.permisoLegajoId " +
            "INNER JOIN CursadaExamen c ON e.cursadaExamen.id = c.id " +
            "WHERE p.permisoLegajoId = :legajoId AND c.turno.turnoId = :turnoId ")
    Optional<Permiso> findPermisoByLegajoAndTurnoOrdered(@Param("legajoId") String legajoId,
                                                     @Param("turnoId") String turnoId);

}
