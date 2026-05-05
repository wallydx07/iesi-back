package com.example.iesiback.services;

public interface CierreDiarioService {

    void cerrarDia(Long usuarioId);

    void validarDiaAbierto();

    boolean estaCerradoHoy();

    void auditarDia(Long usuarioId);
}