package com.example.iesiback.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ConciliacionResumenDTO {
    private int conciliados;
    private int discrepancias;        // ESTADO_DISTINTO + MONTO_DISTINTO
    private int reembolsados;
    private int soloMp;
    private int soloLocal;
    private int efectivoNoAplica;     // pagos locales sin mpPaymentId (manuales)
    private BigDecimal montoConciliado;
    private BigDecimal montoDiscrepante;
}