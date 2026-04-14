package com.example.iesiback.dto;

import java.util.List;

public class ImportResponseDTO {

    private int registrosProcesados;
    private String mensaje;

    private List<String> errores;

    public ImportResponseDTO(int registrosProcesados, String mensaje) {
        this.registrosProcesados = registrosProcesados;
        this.mensaje = mensaje;
    }

    public int getRegistrosProcesados() {
        return registrosProcesados;
    }

    public void setRegistrosProcesados(int registrosProcesados) {
        this.registrosProcesados = registrosProcesados;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<String> getErrores() {
        return errores;
    }

    public void setErrores(List<String> errores) {
        this.errores = errores;
    }
}