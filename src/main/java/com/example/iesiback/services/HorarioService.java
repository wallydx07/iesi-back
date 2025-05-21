package com.example.iesiback.services;

import com.example.iesiback.dto.HorarioDTO;
import com.example.iesiback.entities.PersonalHorario;

import java.util.List;
import java.util.Optional;

public interface HorarioService {
    List<HorarioDTO> obtenerPorDni(String dni);
    PersonalHorario guardar(PersonalHorario horario);
    PersonalHorario actualizar(Integer id, PersonalHorario horario);
    void eliminar(Integer id);

    Optional<PersonalHorario> findById(Integer id);
}