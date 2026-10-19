package com.example.iesiback.services;

import com.example.iesiback.entities.ConstanciaPrecio;
import com.example.iesiback.repositories.PersonaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Precio final de un curso según quién se inscribe.
 * Regla: si el DNI ya está en la tabla persona, se aplica el % de constancia_precios.descuento.
 */
@Service
public class PrecioPreinscripcionService {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    public record PrecioFinal(BigDecimal base, BigDecimal descuentoPct, BigDecimal total) {
        public boolean conDescuento() { return descuentoPct.signum() > 0; }
    }

    private final PersonaRepository personaRepository;

    public PrecioPreinscripcionService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    /** Precio para el canal web (precio_mp). */
    public PrecioFinal calcularOnline(ConstanciaPrecio concepto, Long dni) {
        return calcular(concepto.getPrecioMp(), concepto, dni);
    }

    /** Precio para la caja (precio). */
    public PrecioFinal calcularPresencial(ConstanciaPrecio concepto, Long dni) {
        return calcular(concepto.getPrecio(), concepto, dni);
    }

    private PrecioFinal calcular(BigDecimal base, ConstanciaPrecio concepto, Long dni) {
        BigDecimal pct = porcentaje(concepto);
        if (pct.signum() == 0 || dni == null || personaRepository.findByPersonaDni(dni).isEmpty()) {
            return new PrecioFinal(base, BigDecimal.ZERO, base);
        }
        BigDecimal total = base.multiply(CIEN.subtract(pct)).divide(CIEN, 2, RoundingMode.HALF_UP);
        return new PrecioFinal(base, pct, total);
    }

    /** descuento como porcentaje entre 0 y 100; cualquier otro valor se ignora. */
    private static BigDecimal porcentaje(ConstanciaPrecio concepto) {
        Double d = concepto.getDescuento();
        if (d == null || d <= 0 || d > 100) return BigDecimal.ZERO;
        return BigDecimal.valueOf(d);
    }
}