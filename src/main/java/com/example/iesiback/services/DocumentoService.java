package com.example.iesiback.services;

import com.example.iesiback.entities.Documento;

import java.util.List;
import java.util.Optional;

public interface DocumentoService {

    Documento saveDocumento(Documento documento);
    Documento updateDocumento(Long id, Documento documento);
    List<Documento> getAllDocumentos();
    Documento getDocumentoById(Long id);
    void deleteDocumento(Long id);


    // Buscar por nombre
    Documento findByNombre(String nombre);

    // Buscar lista por entidadId
    List<Documento> findByEntidadId(String entidadId);

    // Buscar por entidadId + tipoDocumento (único)
    Optional<Documento> findByEntidadIdAndTipoDocumento(String entidadId, String tipoDocumento);
}