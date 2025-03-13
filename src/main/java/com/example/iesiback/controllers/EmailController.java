package com.example.iesiback.controllers;

import com.example.iesiback.dto.EmailRequest;
import com.example.iesiback.services.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/enviar-email")
    public ResponseEntity<Map<String, String>> enviarEmail(@RequestBody EmailRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            emailService.enviarCorreoConPlantilla(
                    request.getTo(),
                    request.getSubject(),
                    request.getTemplate(),
                    request.getVariables()
            );
            response.put("mensaje", "Email enviado correctamente.");
            return ResponseEntity.ok(response);
        } catch (MessagingException e) {
            response.put("mensaje", "Error al enviar el email.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
