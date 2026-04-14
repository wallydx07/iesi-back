package com.example.iesiback.repositories;

import com.example.iesiback.dto.HorarioDTO;
import com.example.iesiback.dto.PersonalHorarioDTO;
import com.example.iesiback.entities.PersonalHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface PersonalHorarioRepository extends JpaRepository<PersonalHorario, Integer> {

    @Query("""
    SELECT new com.example.iesiback.dto.HorarioDTO(
        ph.id, ph.dia, ph.entrada, ph.salida,
        m.materiaNombre, mc.carrera.carreraId, mc.id, m.materiaRegimen
    )
    FROM PersonalHorario ph
    JOIN ph.materiaCarrera mc
    JOIN mc.materia m
    WHERE ph.dni.id = :dni
      AND ph.year = :year
""")
    List<HorarioDTO> obtenerHorariosPorDniYAnio(@Param("dni") String dni, @Param("year") Integer year);

    List<PersonalHorario> findByDiaAndEntrada(String dia, LocalTime entrada);

    @Query("SELECT ph FROM PersonalHorario ph " +
            "WHERE ph.dia = :dia " +
            "AND ph.entrada BETWEEN :horaInicio AND :horaFin " +
            "AND ph.year = :anio")
    List<PersonalHorario> findByDiaHoraEntradaEnRangoYAnio(
            @Param("dia") String dia,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("anio") Integer anio);


    @Query("SELECT ph FROM PersonalHorario ph " +
            "WHERE ph.dia = :dia " +
            "AND ph.year = :anio")
    List<PersonalHorario> findByDiaYAnio(
            @Param("dia") String dia,
            @Param("anio") Integer anio);

    @Query("SELECT new com.example.iesiback.dto.PersonalHorarioDTO(" +
            "ph.id, " +
            "p.id, " +
            "p.personalApellido, " +
            "p.personalNombre, " +
            "mc.id, " +
            "mc.carrera.carreraId, " +
            "m.materiaNombre, " +
            "ph.dia, " +
            "ph.entrada, " +
            "ph.salida, m.materiaRegimen) " +
            "FROM PersonalHorario ph " +
            "JOIN ph.dni p " +
            "JOIN ph.materiaCarrera mc " +
            "JOIN mc.materia m " +
            "WHERE ph.year = :year " +
            "ORDER BY p.personalApellido, p.personalNombre ASC")
    List<PersonalHorarioDTO> findPersonalHorariosDelAnio(@Param("year") Integer year);


    List<PersonalHorario> findByMateriaCarreraId(long materiaId);

}