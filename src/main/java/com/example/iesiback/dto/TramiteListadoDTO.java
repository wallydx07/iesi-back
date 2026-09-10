package com.example.iesiback.dto;

import com.example.iesiback.enums.PrioridadTramite;

import java.time.LocalDateTime;

public record TramiteListadoDTO(
        Integer id,
        String tramiteEstado,
        Long tramiteDni,
        String tramiteApellidoNombre,
        String tramiteTipo,
        String tramiteAsunto,
        String tramiteProblema,
        LocalDateTime tramiteFecha,
        String tramiteDestino,
        String codigoSeguimiento,
        Long numeroTipo,
        PrioridadTramite tramitePrioridad,
        Long gestorDni,
        String tramiteArea,
        String paseMaxTexto,
        LocalDateTime ultimoPaseFecha
) {}