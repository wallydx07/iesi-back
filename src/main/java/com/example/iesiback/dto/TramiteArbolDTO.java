package com.example.iesiback.dto;
public record TramiteArbolDTO(
        Long id,
        Long tramiteReferenciaId,
        String tramiteAsunto,
        String tramiteEstado,
        String tramiteApellidoNombre
) {}