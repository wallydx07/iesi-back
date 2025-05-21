package com.example.iesiback.repositories;

import com.example.iesiback.dto.HorarioDTO;
import com.example.iesiback.entities.PersonalHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalHorarioRepository extends JpaRepository<PersonalHorario, Integer> {

    @Query("""
    SELECT new com.example.iesiback.dto.HorarioDTO(
        ph.id, ph.dia, ph.entrada, ph.salida,
        m.materiaNombre, mc.carrera.carreraId, mc.id
    )
    FROM PersonalHorario ph
    JOIN ph.materiaCarrera mc
    JOIN mc.materia m
    WHERE ph.dni.id = :dni
      AND ph.year = :year
""")
    List<HorarioDTO> obtenerHorariosPorDniYAnio(@Param("dni") String dni, @Param("year") Integer year);

}