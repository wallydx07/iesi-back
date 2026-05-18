package com.example.iesiback.services;

import java.time.LocalDate;

public interface CierreDiarioService {

    void cerrarDia(String usuarioId, LocalDate fecha);

    void validarDiaAbierto();

    boolean estaCerradoHoy();

    void auditarDia(String usuarioId, LocalDate fecha);

    String estadoHoy();
}