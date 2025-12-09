package com.example.iesiback.services;

import com.example.iesiback.entities.AsistenciaPersonal;
import com.example.iesiback.entities.PersonalHorario;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VerificadorAsistenciaAutomatica {

    private final AsistenciaPersonalService asistenciaPersonalService;
    private final PersonalHorariosService personalHorariosService;
    private final EmailService emailService;

    public VerificadorAsistenciaAutomatica(
            AsistenciaPersonalService asistenciaPersonalService,
            PersonalHorariosService personalHorariosService,
            EmailService emailService) {
        this.asistenciaPersonalService = asistenciaPersonalService;
        this.personalHorariosService = personalHorariosService;
        this.emailService = emailService;
    }


    @Transactional
    @Scheduled(cron = "0 0 5 * * *", zone = "America/Argentina/Buenos_Aires") // Todos los días a las 5 AM
    public void verificarAusencias() {
        verificarAusenciasPorFecha(LocalDate.now().minusDays(1));
    }

    public void verificarAusenciasPorFecha(LocalDate fecha) {
        String diaSemana = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es")).toUpperCase();

        diaSemana = Normalizer.normalize(diaSemana, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        int anioActual = fecha.getYear();
        System.out.println("📆 Verificando ausencias para: " + fecha + " (" + diaSemana + ")");

        List<PersonalHorario> horarios = personalHorariosService.findByDiaYAnio(diaSemana, anioActual);
        System.out.println("📊 Se encontraron " + horarios.size() + " horarios para " + diaSemana + " (" + anioActual + ")");

        // Agrupar horarios por DNI
        Map<Long, List<PersonalHorario>> horariosPorDni = horarios.stream()
                .collect(Collectors.groupingBy(h -> h.getDni().getId()));



        for (Map.Entry<Long, List<PersonalHorario>> entry : horariosPorDni.entrySet()) {
            Long dni = entry.getKey();
            List<PersonalHorario> horariosDni = entry.getValue().stream()
                    .sorted(Comparator.comparing(PersonalHorario::getEntrada))
                    .collect(Collectors.toList());

            // Obtener asistencias del día
            List<AsistenciaPersonal> asistenciasDia = asistenciaPersonalService.obtenerPorDniYFecha(dni, fecha);

            if (asistenciasDia.isEmpty()) {
                // 👉 Caso 1: No registró ninguna asistencia → marcar todos los horarios como ausente
                for (PersonalHorario horario : horariosDni) {
                    AsistenciaPersonal ausencia = new AsistenciaPersonal();
                    ausencia.setDni(dni);
                    ausencia.setHorarioId(horario.getId());
                    ausencia.setFecha(fecha);
                    ausencia.setHoraEntrada(LocalTime.MIDNIGHT);
                    ausencia.setHoraSalida(LocalTime.MIDNIGHT);
                    ausencia.setEstado(4); // Ausente
                    ausencia.setObservaciones("Marcado automáticamente: no asistió");
                    asistenciaPersonalService.guardar(ausencia);
                    System.out.println("🚫 Ausencia registrada para DNI " + dni + " horario " + horario.getId());
                }
                continue; // ya procesamos este DNI
            }
            for (AsistenciaPersonal asistencia : asistenciasDia) {
                System.out.println("   ➝ HorarioID: " + asistencia.getHorarioId() +
                        " | Entrada: " + asistencia.getHoraEntrada() +
                        " | Salida: " + asistencia.getHoraSalida() +
                        " | Estado: " + asistencia.getEstado());
            }
            boolean huboHorarioOlvidado = false;
            for (PersonalHorario horario : horariosDni) {
                Optional<AsistenciaPersonal> asistenciaOpt = asistenciasDia.stream()
                        .filter(a -> horario.getId().equals(a.getHorarioId()))
                        .findFirst();
                if (asistenciaOpt.isPresent()) {
                    AsistenciaPersonal asistencia = asistenciaOpt.get();
                    if (asistencia.getHoraEntrada() != null && asistencia.getHoraSalida() == null) {
                        asistencia.setHoraSalida(asistencia.getHoraEntrada()); // o LocalTime.now()
                        asistencia.setObservaciones("Marcado automáticamente: salida olvidada");
                        asistencia.setEstado(0); // estado personalizado
                        asistenciaPersonalService.guardar(asistencia);
                        System.out.println("✔️ Salida completada para DNI " + dni + " horario " + horario.getId());
                        huboHorarioOlvidado = true;
                    }
                } else if (huboHorarioOlvidado) {
                    // 👉 Caso 3: Horarios posteriores tras una salida olvidada
                    AsistenciaPersonal asistenciaFicticia = new AsistenciaPersonal();
                    asistenciaFicticia.setDni(dni);
                    asistenciaFicticia.setHorarioId(horario.getId());
                    asistenciaFicticia.setFecha(fecha);
                    asistenciaFicticia.setHoraEntrada(LocalTime.MIDNIGHT);
                    asistenciaFicticia.setHoraSalida(LocalTime.MIDNIGHT);
                    asistenciaFicticia.setEstado(0); // Ausente
                    asistenciaFicticia.setObservaciones("Marcado automáticamente: olvidó marcar salida en horario anterior");
                    asistenciaPersonalService.guardar(asistenciaFicticia);
                    System.out.println("🚫 Marcado horario posterior como 00:00 para DNI " + dni + " horario " + horario.getId());

                }
            }
        }
    }
}