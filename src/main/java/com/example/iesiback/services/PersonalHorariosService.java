package com.example.iesiback.services;

import com.example.iesiback.entities.PersonalHorario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface PersonalHorariosService {
    List<PersonalHorario> obtenerPorDiaYHora(String dia, LocalTime hora);
    List<PersonalHorario> findByDiaAndHoraEntrada(String diaSemana, LocalTime horaActual);
    List<PersonalHorario> findByDiaHoraEntradaEnRangoYAnio(String dia, LocalTime horaInicio, LocalTime horaFin, Integer anio);
    List<PersonalHorario> findByDiaYAnio(String dia, Integer anio);
}
