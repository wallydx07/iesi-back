package com.example.iesiback.services;

import com.example.iesiback.dto.HorarioDTO;
import com.example.iesiback.entities.PersonalHorario;
import com.example.iesiback.repositories.PersonalHorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HorarioServiceImpl implements HorarioService {

    @Autowired
    private PersonalHorarioRepository repository;

//    @Override
//    public List<HorarioDTO> obtenerPorDni(String dni) {
//            int anioActual = LocalDate.now().getYear();
//            LocalDate date = LocalDate.now();
//            return repository.obtenerHorariosPorDniYAnio(dni, anioActual);
//
//    }


@Override
    public List<HorarioDTO> obtenerPorDni(String dni) {
        LocalDate hoy = LocalDate.now();
        int anioActual = hoy.getYear();

        // Obtenemos todos los horarios
        List<HorarioDTO> horarios = repository.obtenerHorariosPorDniYAnio(dni, anioActual);

        int mes = hoy.getMonthValue();
        String periodoActual;

        if (mes >= Month.MARCH.getValue() && mes <= Month.JULY.getValue()) {
            periodoActual = "1ER CUATRIMESTRE";
        } else if (mes >= Month.AUGUST.getValue() && mes <= Month.NOVEMBER.getValue()) {
            periodoActual = "2DO CUATRIMESTRE";
        } else {
            periodoActual = "ANUAL"; // o "FUERA DE PERÍODO LECTIVO"
        }
        return horarios.stream()
                .filter(h -> h.getMateriaRegimen().equalsIgnoreCase(periodoActual)
                        || h.getMateriaRegimen().equalsIgnoreCase("ANUAL")
                        || h.getMateriaRegimen().equalsIgnoreCase("FULL"))
                .collect(Collectors.toList());
    }


    @Override
    public PersonalHorario guardar(PersonalHorario horario) {
        horario.setYear(LocalDate.now().getYear()); // Año actual
        return repository.save(horario);
    }

    @Override
    public PersonalHorario actualizar(Integer id, PersonalHorario horario) {
        horario.setId(id);
        horario.setYear(LocalDate.now().getYear()); // Año actual
        return repository.save(horario);
    }

    @Override
    public void eliminar(Integer id) {
        repository.deleteById(id);
    }
    @Override
    public Optional<PersonalHorario> findById(Integer id) {
        return repository.findById(id);
    }
}
