package com.example.iesiback.services.notificaciones;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.springframework.web.util.HtmlUtils.htmlEscape;

/**
 * Envía el correo de confirmación cuando una inscripción queda finalizada.
 *
 * - AFTER_COMMIT: solo se envía si la inscripción quedó realmente guardada.
 * - @Async: corre en otro hilo; no demora el webhook ni la validación en caja.
 * - Si el envío falla, se registra en el log: la inscripción ya está hecha y no se revierte.
 */
@Component
public class NotificacionInscripcionListener {

    private static final Logger log = LoggerFactory.getLogger(NotificacionInscripcionListener.class);
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "AR"));

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    @Value("${app.mail.nombre-remitente:IESI Campinta Guazú Gloria Pérez}")
    private String nombreRemitente;

    public NotificacionInscripcionListener(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void enviarConfirmacion(InscripcionFinalizadaEvent e) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(remitente, nombreRemitente);
            helper.setTo(e.correo());
            helper.setSubject("Inscripción confirmada: " + e.curso());
            helper.setText(textoPlano(e), html(e));
            mailSender.send(mensaje);
            log.info("Correo de inscripción enviado a {} (trámite {})", e.correo(), e.codigoSeguimiento());
        } catch (Exception ex) {
            log.error("No se pudo enviar el correo de inscripción a {} (trámite {}): {}",
                    e.correo(), e.codigoSeguimiento(), ex.getMessage(), ex);
        }
    }

    private String fecha(InscripcionFinalizadaEvent e) {
        return e.fechaInicio() != null ? FECHA.format(e.fechaInicio()) : null;
    }

    private String textoPlano(InscripcionFinalizadaEvent e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hola ").append(e.nombre()).append(",\n\n");
        sb.append("Tu inscripción al curso ").append(e.curso()).append(" quedó confirmada.\n\n");
        if (fecha(e) != null) sb.append("Inicio: ").append(fecha(e)).append("\n");
        if (e.horario() != null) sb.append("Horario: ").append(e.horario()).append("\n");
        sb.append("Legajo: ").append(e.legajoId()).append("\n");
        sb.append("Código de trámite: ").append(e.codigoSeguimiento()).append("\n\n");
        sb.append("Guardá este correo: el código de trámite es el que te van a pedir en la institucion.\n\n");
        sb.append(nombreRemitente);
        return sb.toString();
    }

    private String html(InscripcionFinalizadaEvent e) {
        String filas = fila("Curso", e.curso())
                + (fecha(e) != null ? fila("Inicio", fecha(e)) : "")
                + (e.horario() != null ? fila("Horario", e.horario()) : "")
                + fila("Legajo", e.legajoId())
                + filaHtml("Código de trámite",
                "<span style=\"font-family:monospace;letter-spacing:1px\">"
                        + htmlEscape(e.codigoSeguimiento()) + "</span>");

        return """
                <div style="font-family:Arial,Helvetica,sans-serif;max-width:560px;margin:0 auto;color:#1b2a44">
                  <p style="font-size:15px;margin:0 0 4px;color:#4b5770">%s</p>
                  <h1 style="font-size:22px;margin:0 0 16px">Inscripción confirmada</h1>
                  <p style="font-size:15px;line-height:1.5">Hola %s, tu inscripción quedó registrada.</p>
                  <table style="width:100%%;border-collapse:collapse;font-size:15px;margin:16px 0">%s</table>
                  <p style="font-size:14px;line-height:1.5;color:#4b5770">
                    Guardá este correo: el código de trámite es el que te van a pedir en ventanilla para cualquier consulta.
                  </p>
                </div>
                """.formatted(htmlEscape(nombreRemitente), htmlEscape(e.nombre()), filas);
    }

    /** Fila con un valor de texto (se escapa). */
    private String fila(String rotulo, String valor) {
        return filaHtml(rotulo, htmlEscape(valor));
    }

    /** Fila con un valor que ya es HTML seguro. */
    private String filaHtml(String rotulo, String valorHtml) {
        return "<tr>"
                + "<td style=\"padding:8px 0;border-top:1px solid #d3d8e1;color:#4b5770;width:40%\">" + rotulo + "</td>"
                + "<td style=\"padding:8px 0;border-top:1px solid #d3d8e1;font-weight:600\">" + valorHtml + "</td>"
                + "</tr>";
    }
}