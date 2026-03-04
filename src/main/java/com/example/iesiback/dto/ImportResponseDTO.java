package com.example.iesiback.dto;
public class ImportResponseDTO {

    private int registrosProcesados;
    private String mensaje;

    public ImportResponseDTO(int registrosProcesados, String mensaje) {
        this.registrosProcesados = registrosProcesados;
        this.mensaje = mensaje;
    }

    public int getRegistrosProcesados() {
        return registrosProcesados;
    }

    public String getMensaje() {
        return mensaje;
    }
}