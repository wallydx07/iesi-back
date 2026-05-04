package com.example.iesiback.services;


import com.example.iesiback.entities.Pago;
import com.example.iesiback.entities.PagoDetalle;
import com.example.iesiback.repositories.PagoDetalleRepository;
import com.example.iesiback.repositories.PagoRepository;
import com.example.iesiback.services.PagoDetalleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoDetalleServiceImpl implements PagoDetalleService {

    private final PagoRepository pagoRepository;
    private final PagoDetalleRepository detalleRepository;

    @Override
    @Transactional
    public Pago guardarPagoConDetalles(Pago pago, List<PagoDetalle> detalles) {

        // 🧠 Calcular total automáticamente
        BigDecimal total = detalles.stream()
                .map(d -> d.getMonto().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pago.setMontoTotal(total);

        // Guardar pago primero
        Pago pagoGuardado = pagoRepository.save(pago);

        // Asociar detalles
        detalles.forEach(d -> {
            d.setPago(pagoGuardado);

            if (d.getCantidad() == null || d.getCantidad() <= 0) {
                d.setCantidad(1);
            }
        });

        detalleRepository.saveAll(detalles);

        return pagoGuardado;
    }

    @Override
    public List<PagoDetalle> obtenerPorPago(Integer pagoId) {
        return detalleRepository.findByPagoId(pagoId);
    }
}