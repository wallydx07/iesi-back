package com.example.iesiback.services;

import com.example.iesiback.dto.PersonalHorarioDTO;
import com.example.iesiback.entities.PersonalHorario;
import com.example.iesiback.repositories.PersonalHorarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class PersonalHorariosServiceImpl implements PersonalHorariosService {

    private final PersonalHorarioRepository repository;

    public PersonalHorariosServiceImpl(PersonalHorarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PersonalHorario> obtenerPorDiaYHora(String dia, LocalTime hora) {
        return repository.findByDiaAndEntrada(dia, hora);
    }

    @Override
    public List<PersonalHorario> findByDiaAndHoraEntrada(String dia, LocalTime horaEntrada) {
        return repository.findByDiaAndEntrada(dia, horaEntrada);
    }

    @Override
    public List<PersonalHorario> findByDiaHoraEntradaEnRangoYAnio(String dia, LocalTime horaInicio, LocalTime horaFin, Integer anio) {
        return repository.findByDiaHoraEntradaEnRangoYAnio(dia, horaInicio, horaFin, anio);
    }

    @Override
    public List<PersonalHorario> findByDiaYAnio(String dia, Integer anio) {
        return repository.findByDiaYAnio(dia, anio);
    }

    @Override
    public List<PersonalHorarioDTO> getPersonalHorarios(Integer year) {
        return repository.findPersonalHorariosDelAnio(year);
    }
}