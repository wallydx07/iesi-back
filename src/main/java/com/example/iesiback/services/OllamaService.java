package com.example.iesiback.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String OLLAMA_URL = "http://147.93.35.109:11434/api/generate";

    public String mejorarTexto(String texto) throws JsonProcessingException {

        long start = System.currentTimeMillis(); // ⏱ Inicio de medición

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama3.1:latest");

        body.put("prompt",
                "corrige y redacta mejor el siguiente texto en tercera persona: " + texto
        );

        Map<String, Object> options = new HashMap<>();
        options.put("num_thread", 2);
        body.put("options", options);

        body.put("stream", false);

        // ---- 1 SOLA LLAMADA ----
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(OLLAMA_URL, body, String.class);

        String json = responseEntity.getBody();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        String respuesta = root.get("response").asText();

        long end = System.currentTimeMillis(); // ⏱ Fin de medición
        System.out.println("⏱ Tiempo total: " + (end - start) + " ms");

        return respuesta;
    }

}
