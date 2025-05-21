package com.example.iesiback.repositories;

import com.example.iesiback.entities.AsistenciaPersonal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface  AsistenciaPersonalRepository extends JpaRepository<AsistenciaPersonal, Integer> {
//    @Query("SELECT COUNT(a) > 0 FROM AsistenciaPersonal a WHERE a.fecha = :fecha AND a.dni = :dni AND a.horarioId = :horarioId")
//    boolean existeAsistencia(@Param("fecha") LocalDate fecha, @Param("dni") Long dni, @Param("horarioId") Integer horarioId);

    boolean existsByFechaAndDniAndHorarioId(LocalDate fecha, Long dni, Integer horarioId);

    Optional<AsistenciaPersonal> findByFechaAndDniAndHorarioId(LocalDate fecha, Long dni, Integer horarioId);

}
