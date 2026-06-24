package com.example.iesiback.services;

import com.example.iesiback.dto.PaymentDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MercadoPagoService {

    @Value("${mercadopago.access-token}")
    private String accessToken;
    private static final String PAYMENT_URL = "https://api.mercadopago.com/v1/payments/";

    private final RestTemplate restTemplate;

    public MercadoPagoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PaymentDTO consultarPagoPorId(Long paymentId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<PaymentDTO> response = restTemplate.exchange(
                PAYMENT_URL + paymentId,
                HttpMethod.GET,
                entity,
                PaymentDTO.class
        );
        return response.getBody();
    }
}