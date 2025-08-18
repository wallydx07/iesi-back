package com.example.iesiback.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

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
}