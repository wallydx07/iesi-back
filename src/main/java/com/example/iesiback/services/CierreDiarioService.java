package com.example.iesiback.services;

public interface CierreDiarioService {

    void cerrarDia(String usuarioId);

    void validarDiaAbierto();

    boolean estaCerradoHoy();

    void auditarDia(String usuarioId);

    String estadoHoy();
}