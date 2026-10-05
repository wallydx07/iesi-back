package com.example.iesiback.services.documentos;

import java.util.Set;

public final class EstadosTramite {
    private EstadosTramite() {}

    private static final Set<String> CON_DOCUMENTO = Set.of("RESUELTO", "ARCHIVADO");

    public static boolean permiteDocumento(String estado) {
        return estado != null && CON_DOCUMENTO.contains(estado.trim().toUpperCase());
    }
}