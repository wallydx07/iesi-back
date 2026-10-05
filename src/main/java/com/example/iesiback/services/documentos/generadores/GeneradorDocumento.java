package com.example.iesiback.services.documentos.generadores;

import com.example.iesiback.entities.DocumentoEmitido;
import com.example.iesiback.entities.Tramite;

public interface GeneradorDocumento {
    boolean soporta(Tramite tramite);
    Integer diasVigencia();                       // null = no vence

    Integer diasVigencia(Tramite t, DocumentoEmitido doc);

    void completarSnapshot(Tramite tramite, DocumentoEmitido doc);
    byte[] generarPdf(Tramite tramite, DocumentoEmitido doc);
}