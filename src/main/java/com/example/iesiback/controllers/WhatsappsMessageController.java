package com.example.iesiback.controllers;

import com.example.iesiback.dto.WhatsappsMessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api")
public class WhatsappsMessageController {

    private final RestTemplate restTemplate;

    @Autowired
    public WhatsappsMessageController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/enviar-mensaje-protegido")
    public ResponseEntity<?> enviarMensajeProtegido(@RequestBody WhatsappsMessageRequest request) {
        // Validar que se envíen los parámetros requeridos
        if (request.getNumero() == null || request.getNumero().isEmpty() ||
                request.getMessage() == null || request.getMessage().isEmpty()) {
            return ResponseEntity.badRequest().body("Faltan parámetros 'numero' o 'message'.");
        }

        // URL de la API1 (ejecutándose en la misma VPS en el puerto 4000)
        String api1Url = "http://localhost:4000/enviar-mensaje";

        // Configurar los headers y el body de la petición a la API1
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = new HashMap<>();
        payload.put("numero", request.getNumero());
        payload.put("message", request.getMessage());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(api1Url, entity, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Puedes registrar el error para depuración
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al llamar a la API interna: " + e.getMessage());
        }
    }
}