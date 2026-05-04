package com.example.iesiback.services;

import com.example.iesiback.entities.Pago;
import com.example.iesiback.entities.PagoDetalle;

import java.util.List;

public interface PagoDetalleService {

    Pago guardarPagoConDetalles(Pago pago, List<PagoDetalle> detalles);

    List<PagoDetalle> obtenerPorPago(Integer pagoId);

}