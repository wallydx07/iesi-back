package com.example.iesiback.services;

import com.example.iesiback.dto.TurnoExamenDTO;
import com.example.iesiback.entities.Turno;
import com.example.iesiback.repositories.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class TurnoServiceImpl implements TurnoService {

    private UserService userService;


    @Autowired
    private TurnoRepository turnoRepository;

    public TurnoServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<Turno> obtenerTurnos() {
        return turnoRepository.findAllByOrderByTurnoLimiteDesc();
    }

    @Override
    public Turno obtenerTurnoPorId(String turnoId) {
        return turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + turnoId));
    }

    @Override
    public Optional<Turno> findById(String turnoId) {
        return turnoRepository.findById(turnoId);
    }

    @Override
    public Turno save(Turno turno) {
        return turnoRepository.save(turno);
    }

//    @Override
//    public List<TurnoExamenDTO> getTurnoExamenDTO() {
//        List<TurnoExamenDTO> turnos = turnoRepository.findAllTurnoExamenDTO();
//        return turnos;
//    }

//    @Override
//    public List<TurnoExamenDTO> getTurnoExamenDTO() {
//
//        List<TurnoExamenDTO> turnos = turnoRepository.findAllTurnoExamenDTO();
//
//        String userRol = userService.getAuthenticatedUser()
//                .get()
//                .getRoles()
//                .get(0)
//                .getRoleNombre();
//
//        // 🔐 Si es docente → devolver solo el más reciente
//        if ("ROLE_DOCENTE".equalsIgnoreCase(userRol) && !turnos.isEmpty()) {
//            return Collections.singletonList(turnos.get(0));
//        }
//
//        // 👥 Otros roles → devolver todos
//        return turnos;
//    }


    @Override
    public List<TurnoExamenDTO> getTurnoExamenDTO() {

        List<TurnoExamenDTO> turnos = turnoRepository.findAllTurnoExamenDTO();

        if (turnos.isEmpty()) {
            return turnos;
        }

        String userRol = userService.getAuthenticatedUser()
                .get()
                .getRoles()
                .get(0)
                .getRoleNombre();

        // 🔐 Si es docente → filtrar por último mes y año, devolver todos los llamados de ese mes
//        if ("ROLE_DOCENTE".equalsIgnoreCase(userRol)) {
//
//            TurnoExamenDTO ultimo = turnos.stream()
//                    .max(Comparator.comparing(TurnoExamenDTO::getTurnoAnio)
//                            .thenComparing(TurnoExamenDTO::getTurnoMes))
//                    .orElse(turnos.get(0));
//
//            // Filtrar todos los turnos del mismo mes y año que el último
//            List<TurnoExamenDTO> turnosDelUltimoMes = turnos.stream()
//                    .filter(t -> t.getTurnoMes().equalsIgnoreCase(ultimo.getTurnoMes())
//                            && t.getTurnoAnio().equalsIgnoreCase(ultimo.getTurnoAnio()))
//                    .sorted(Comparator.comparing(TurnoExamenDTO::getTurnoLimite)) // ordenar por fecha límite
//                    .toList();
//
//            return turnosDelUltimoMes;
//        }

        // 🔐 Si es docente → filtrar por último mes y año, devolver todos los llamados de ese mes
        if ("ROLE_DOCENTE".equalsIgnoreCase(userRol)) {

            // Tomar el turno con la fecha límite más reciente
            TurnoExamenDTO ultimo = turnos.stream()
                    .max(Comparator.comparing(TurnoExamenDTO::getTurnoLimite))
                    .orElse(turnos.get(0));

            // Filtrar todos los turnos del mismo mes y año que el último
            List<TurnoExamenDTO> turnosDelUltimoMes = turnos.stream()
                    .filter(t -> t.getTurnoMes().equalsIgnoreCase(ultimo.getTurnoMes())
                            && t.getTurnoAnio().equalsIgnoreCase(ultimo.getTurnoAnio()))
                    .sorted(Comparator.comparing(TurnoExamenDTO::getTurnoLimite).reversed())
                    .toList();

            return turnosDelUltimoMes;
        }

        // 👥 Otros roles → devolver todos los turnos sin filtrar
        return turnos;
    }

    @Override
    public TurnoExamenDTO obtenerTurnoMasCercano(LocalDate fecha) {

        List<Object[]> results = turnoRepository.findTurnoMasCercanoRaw(fecha);

        if (results == null || results.isEmpty()) {
            return null;
        }

        Object[] row = results.get(0);

        LocalDate turnoLimite;

        if (row[3] instanceof java.sql.Date sqlDate) {
            turnoLimite = sqlDate.toLocalDate();
        } else if (row[3] instanceof LocalDate localDate) {
            turnoLimite = localDate;
        } else {
            throw new IllegalStateException(
                    "Tipo inesperado para turno_limite: " + row[3].getClass());
        }

        return new TurnoExamenDTO(
                (String) row[0],
                (String) row[1],
                (String) row[2],
                turnoLimite,
                (String) row[4]
        );
    }

    @Override
    public TurnoExamenDTO obtenerTurnoActualbyNotaId(Long notaId) {
        return turnoRepository.findTurnobyNotaId(notaId);
    }






}
