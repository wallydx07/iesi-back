package com.example.iesiback.repositories;

import com.example.iesiback.dto.EquivalenciaDetalleDTO;
import com.example.iesiback.entities.Equivalencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquivalenciaRepository extends JpaRepository<Equivalencia, Long> {

    List<Equivalencia> findByLegajoId(String legajoId);

    List<Equivalencia> findByMateriaId(String materiaId);

    List<Equivalencia> findByStatus(String status);


    @Query("SELECT new com.example.iesiback.dto.EquivalenciaDetalleDTO(" +
            "n.notaId, a.alumnoDni, a.alumnoApellido, a.alumnoNombre, n.notaCalificacionNotaNumero, n.notaCalificacionNotaLetra," +
            "n.notaLibroNota, n.notaFolioNota, e.materiaOrigen, e.institucionOrigen, m.materiaNombre, c.carreraNombre, n.notaUsuario, n.notaFechaNota, e.status, e.resolucion) " +
            "FROM Equivalencia e " +
            "JOIN e.nota n " +
            "JOIN n.cursada cu " +
            "JOIN cu.legajo l " +
            "JOIN l.legajoAlumnoDni a " +
            "JOIN cu.materiaCarrera mc " +
            "JOIN mc.carrera c " +
            "JOIN mc.materia m")
    List<EquivalenciaDetalleDTO> obtenerEquivalenciasConDetalle();

}
