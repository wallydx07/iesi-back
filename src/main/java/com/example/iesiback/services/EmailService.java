package com.example.iesiback.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void enviarCorreoConPlantilla(String to, String subject, String templateName,Map<String, Object> variables) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        // Cargar la plantilla y reemplazar variables
        Context context = new Context();
        context.setVariables(variables);
        System.out.println(variables);
        String htmlContent = templateEngine.process(templateName, context);
        System.out.println(htmlContent);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom("alumnos@iesijujuy.edu.ar");
        mailSender.send(message);
    }

    public void enviarCorreoConAdjunto(
            String to,
            String subject,
            String templateName,
            Map<String, Object> variables,
            byte[] adjunto,
            String nombreArchivo
    ) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        // 1. Procesar plantilla HTML
        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = templateEngine.process(templateName, context);

        // 2. Armar correo
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom("alumnos@iesijujuy.edu.ar");

        // 3. Agregar el PDF como adjunto
        helper.addAttachment(nombreArchivo, new org.springframework.core.io.ByteArrayResource(adjunto));

        // 4. Enviar
        mailSender.send(message);
    }

}
