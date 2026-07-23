package com.example.iesiback.services;

import com.example.iesiback.entities.CierreDiario;

import java.time.LocalDate;

public interface CierreDiarioService {

    void cerrarDia(String usuarioId, LocalDate fecha);

    void validarDiaAbierto();

    boolean estaCerradoHoy();

    void auditarDia(String usuarioId, LocalDate fecha);

    String estadoHoy();

    // 🟡 CIERRE COMPLETO DEL DÍA (o de una fecha dada) DEL USUARIO LOGUEADO
    CierreDiario cierrePorFecha(String usuarioId, LocalDate fecha);
}