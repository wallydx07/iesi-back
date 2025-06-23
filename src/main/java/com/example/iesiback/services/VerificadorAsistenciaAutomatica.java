package com.example.iesiback.services;

import com.example.iesiback.entities.AsistenciaPersonal;
import com.example.iesiback.entities.PersonalHorario;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        int anioActual = fecha.getYear();
        System.out.println("📆 Verificando ausencias para: " + fecha + " (" + diaSemana + ")");

        List<PersonalHorario> horarios = personalHorariosService.findByDiaYAnio(diaSemana, anioActual);

        for (PersonalHorario horario : horarios) {
            boolean existe = asistenciaPersonalService.existeAsistenciaParaHoy(Long.valueOf(horario.getId()), fecha);
            if (!existe) {
                AsistenciaPersonal falta = new AsistenciaPersonal();
                falta.setDni(horario.getDni().getId());
                falta.setHorarioId(horario.getId());
                falta.setFecha(fecha);
                falta.setHoraEntrada(null);
                falta.setHoraSalida(null);
                falta.setEstado(4);
                falta.setObservaciones("Registrado automáticamente por ausencia");
                asistenciaPersonalService.guardar(falta);

                System.out.println("🚫 Ausencia registrada para DNI " + horario.getDni());

                try {
                    Map<String, Object> variables = Map.of(
                            "Materia", horario.getMateriaCarrera().getMateria().getMateriaNombre(),
                            "Carrera", horario.getMateriaCarrera().getCarrera().getCarreraNombre(),
                            "dni", horario.getDni().getId(),
                            "fecha", fecha.toString(),
                            "hora", horario.getEntrada().toString()
                    );

                    String destinatario = "walterxd00@gmail.com";
                    emailService.enviarCorreoConPlantilla(
                            destinatario,
                            "Notificación de Ausencia",
                            "notificacion-ausencia",
                            variables
                    );
                    System.out.println("📧 Correo enviado a " + destinatario);
                } catch (Exception e) {
                    System.err.println("⚠️ Error al enviar el correo de ausencia: " + e.getMessage());
                }
            }
        }
    }

}