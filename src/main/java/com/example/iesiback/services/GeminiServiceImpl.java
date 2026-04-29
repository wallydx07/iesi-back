package com.example.iesiback.services;

import com.example.iesiback.dto.GeminiRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class GeminiServiceImpl implements GeminiService {

    private final WebClient webClient;

    public GeminiServiceImpl(@Value("${gemini.api.url}") String apiUrl,
                             @Value("${gemini.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("x-goog-api-key", apiKey) // 👈 header correcto
                .build();
    }

    @Override
    public String mejorarRedaccion(String texto) {
        return "";
    }
//    // Código conceptual para GeminiServiceImpl.mejorarRedaccion
//    @Override
//    public String mejorarRedaccion(String texto) {
//        int maxRetries = 5;
//        long delayMs = 2000; // 1 segundo
//
//        for (int attempt = 0; attempt < maxRetries; attempt++) {
//            try {
//                // ... (Tu código de WebClient.post().uri()...)
//                GeminiRequest request = new GeminiRequest(texto);
//                return webClient.post()
//                        .uri("/v1beta/models/gemini-2.5-flash:generateContent")
//                        .bodyValue(request)
//                        .retrieve()
//                        .bodyToMono(GeminiResponse.class)
//                        .block()
//                        .getImprovedText();
//
//            } catch (WebClientResponseException.TooManyRequests e) {
//                if (attempt == maxRetries - 1) {
//                    // Último intento fallido, lanza la excepción
//                    throw e;
//                }
//                // Espera con retroceso exponencial
//                try {
//                    System.out.println("Rate Limit excedido. Esperando " + delayMs + " ms...");
//                    Thread.sleep(delayMs);
//                    delayMs *= 2; // Duplica el tiempo de espera (1s, 2s, 4s, 8s...)
//                } catch (InterruptedException ie) {
//                    Thread.currentThread().interrupt();
//                    throw new RuntimeException("Reintento interrumpido", ie);
//                }
//            }
//        }
//        return null;
//    }
}
