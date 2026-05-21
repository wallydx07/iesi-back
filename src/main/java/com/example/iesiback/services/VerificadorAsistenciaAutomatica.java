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
        // Verifica el día anterior completo
        verificarAusenciasPorFecha(LocalDate.now().minusDays(1));
    }

    public void verificarAusenciasPorFecha(LocalDate fecha) {
        // 1. Obtener el día de la semana formateado (ej: "LUNES")
        String diaSemana = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es")).toUpperCase();
        diaSemana = Normalizer.normalize(diaSemana, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        int anioActual = fecha.getYear();

        System.out.println("📆 Verificando asistencias cronológicas para: " + fecha + " (" + diaSemana + ")");

        // 2. Traer los horarios planificados para saber quiénes DEBÍAN asistir ese día
        List<PersonalHorario> horariosDelDia = personalHorariosService.findByDiaYAnio(diaSemana, anioActual);

        // Agrupamos por DNI para obtener la lista única de empleados que tenían que ir a trabajar
        Set<Long> empleadosQueDebianAsistir = horariosDelDia.stream()
                .map(h -> h.getDni().getId())
                .collect(Collectors.toSet());

        for (Long dni : empleadosQueDebianAsistir) {
            // Obtener todas las marcas reales cronológicas que tuvo este DNI en el día
            List<AsistenciaPersonal> asistenciasDia = asistenciaPersonalService.obtenerPorDniYFecha(dni, fecha);

            // 👉 CASO 1: AUSENCIA TOTAL (No tiene ninguna fichada en todo el día)
            if (asistenciasDia.isEmpty()) {
                AsistenciaPersonal ausencia = new AsistenciaPersonal();
                ausencia.setDni(dni);
                ausencia.setFecha(fecha);
                ausencia.setHoraEntrada(LocalTime.MIDNIGHT);
                ausencia.setHoraSalida(LocalTime.MIDNIGHT);
                ausencia.setHorarioId(null); // Ya no se asocia a un tramo fijo
                ausencia.setEstado(4); // 4 = Falta Injustificada / Ausente
                ausencia.setObservaciones("Ausencia automática: No registró entrada ni salida en todo el día.");

                asistenciaPersonalService.guardar(ausencia);
                System.out.println("🚫 Ausencia absoluta registrada para DNI: " + dni);
                continue;
            }

            // 👉 CASO 2: REVISAR JORNADAS ABIERTAS (Fichó entrada pero se olvidó la salida)
            for (AsistenciaPersonal asistencia : asistenciasDia) {
                if (asistencia.getHoraEntrada() != null && asistencia.getHoraSalida() == null) {
                    // Forzamos el cierre de la jornada para que no quede rota
                    asistencia.setHoraSalida(asistencia.getHoraEntrada()); // O podés usar el fin del día (LocalTime.MAX)
                    asistencia.setEstado(5); // 5 = Requiere Información / Registro Incompleto
                    asistencia.setObservaciones("Cierre automático: El empleado olvidó marcar la salida.");

                    asistenciaPersonalService.guardar(asistencia);
                    System.out.println("⚠️ Salida olvidada solucionada de forma automática para DNI: " + dni);
                }
            }
        }
    }
}