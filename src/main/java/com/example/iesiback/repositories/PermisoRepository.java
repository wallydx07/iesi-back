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
            "INNER JOIN Cursada cu ON mc.id = cu.materiaCarrera.id " +
            "INNER JOIN Legajo l ON cu.legajo.legajoId = l.legajoId " +
            "WHERE l.legajoId = :libreta")
    String obtenerCarreraPorLibreta(@Param("libreta") String libreta);

    @Query("SELECT l.legajoAlumnoDni.alumnoDni FROM Legajo l WHERE l.legajoId = :libreta")
    int obtenerDniPorLibreta(@Param("libreta") String libreta);

    @Query("SELECT a.alumnoNombre FROM Alumno a WHERE a.alumnoDni = :dni")
    String obtenerNombrePorDni(@Param("dni") int dni);

    @Query("SELECT a.alumnoApellido FROM Alumno a WHERE a.alumnoDni = :dni")
    String obtenerApellidoPorDni(@Param("dni") int dni);

    @Query(value = "SELECT DISTINCT p.* " +
            "FROM permiso p " +
            "INNER JOIN examen e ON p.permiso_id = e.permiso_id " +
            "INNER JOIN cursada_examen c ON e.cursada_examen_id = c.cursada_examen_id " +
            "WHERE p.permiso_legajo_id = :legajoId " +
            "AND c.turno_id = :turnoId", nativeQuery = true)
    Optional<Permiso> findPermisoByLegajoAndTurnoOrdered(@Param("legajoId") String legajoId,
                                                         @Param("turnoId") String turnoId);

}
