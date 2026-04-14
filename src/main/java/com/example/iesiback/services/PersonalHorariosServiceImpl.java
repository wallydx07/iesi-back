package com.example.iesiback.services;

import com.example.iesiback.dto.PersonalHorarioDTO;
import com.example.iesiback.entities.PersonalHorario;
import com.example.iesiback.repositories.PersonalHorarioRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

//    @Override
//    public List<PersonalHorario> findByDiaYAnio(String dia, Integer anio) {
//        return repository.findByDiaYAnio(dia, anio);
//
//    }


    @Override
    public List<PersonalHorario> findByDiaYAnio(String dia, Integer anio) {
        LocalDate hoy = LocalDate.now();
        int mes = hoy.getMonthValue();

        // Obtenemos todos los horarios según el repositorio
        List<PersonalHorario> horarios = repository.findByDiaYAnio(dia, anio);


        // Determinar período actual
        String periodoActual;
        if (mes >= Month.MARCH.getValue() && mes <= Month.JULY.getValue()) {
            periodoActual = "1ER CUATRIMESTRE";
        } else if (mes >= Month.AUGUST.getValue() && mes <= Month.NOVEMBER.getValue()) {
            periodoActual = "2DO CUATRIMESTRE";
        } else {
            periodoActual = "ANUAL"; // o "FUERA DE PERÍODO LECTIVO"
        }


        return horarios.stream()
                .filter(h -> Boolean.TRUE.equals(h.getActivo()))  // debe estar activo
                .filter(h -> {
                    String regimen = h.getMateriaCarrera()
                            .getMateria()
                            .getMateriaRegimen();
                    return regimen != null && (
                            regimen.equalsIgnoreCase(periodoActual) ||
                                    regimen.equalsIgnoreCase("ANUAL") ||
                                    regimen.equalsIgnoreCase("FULL")
                    );
                })
                .collect(Collectors.toList());



//        // Filtrar por régimen
//        return horarios.stream()
//                .filter(h -> {
//                    String regimen = h.getMateriaCarrera()
//                            .getMateria()
//                            .getMateriaRegimen();
//                    return regimen != null && (
//                            regimen.equalsIgnoreCase(periodoActual) ||
//                                    regimen.equalsIgnoreCase("ANUAL") ||
//                                    regimen.equalsIgnoreCase("FULL")
//                    );
//                })
//                .collect(Collectors.toList());
    }


//    @Override
//    public List<PersonalHorarioDTO> getPersonalHorarios(Integer year) {
//        return repository.findPersonalHorariosDelAnio(year);
//    }

    @Override
    public List<PersonalHorarioDTO> getPersonalHorarios(Integer year) {
        LocalDate hoy = LocalDate.now();
        int anioActual = hoy.getYear();

        // Obtenemos todos los horarios
        List<PersonalHorarioDTO> horarios = repository.findPersonalHorariosDelAnio(year);

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
public List<LocalDateTime> obtenerClasesDelMes(long razon) {
        List<PersonalHorario> horarios = repository.findByMateriaCarreraId(razon);
        List<LocalDateTime> clases = new ArrayList<>();

        YearMonth yearMonth = YearMonth.now();

        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate fecha = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), day);
            DayOfWeek diaSemana = fecha.getDayOfWeek();

            for (PersonalHorario h : horarios) {
                if (coincideDia(h.getDia(), diaSemana)) {
                    clases.add(LocalDateTime.of(fecha, h.getEntrada()));
                }
            }
        }

        return clases;
    }
    private boolean coincideDia(String diaBD, DayOfWeek diaJava) {
        return switch (diaBD.toUpperCase()) {
            case "LUNES" -> diaJava == DayOfWeek.MONDAY;
            case "MARTES" -> diaJava == DayOfWeek.TUESDAY;
            case "MIERCOLES" -> diaJava == DayOfWeek.WEDNESDAY;
            case "JUEVES" -> diaJava == DayOfWeek.THURSDAY;
            case "VIERNES" -> diaJava == DayOfWeek.FRIDAY;
            case "SABADO" -> diaJava == DayOfWeek.SATURDAY;
            case "DOMINGO" -> diaJava == DayOfWeek.SUNDAY;
            default -> false;
        };
    }



}