package com.example.iesiback.services;

import com.example.iesiback.entities.Documento;

import java.util.List;

public interface DocumentoService {

    Documento saveDocumento(Documento documento);

    Documento updateDocumento(Long id, Documento documento);

    List<Documento> getAllDocumentos();

    Documento getDocumentoById(Long id);

    void deleteDocumento(Long id);
}