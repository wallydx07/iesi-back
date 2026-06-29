package com.example.iesiback.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PaymentDTO {
    private Long id;
    private String status;
    private String status_detail;
    private String payment_method_id;
    private String payment_type_id;
    private BigDecimal transaction_amount;
    private String currency_id;
    private String external_reference;
    private String preference_id;
    private Instant date_approved;
    private Payer payer;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Payer {
        private String id;
        private String email;
        private String first_name;
        private String last_name;
        private Identification identification;

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        public static class Identification {
            private String type;
            private String number;
        }
    }
}