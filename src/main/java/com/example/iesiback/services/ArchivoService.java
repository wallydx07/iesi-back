package com.example.iesiback.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public interface ArchivoService {
    boolean existePorEtiqueta(String etiqueta);

    @Transactional
    byte[] descargarArchivosPorEtiqueta(String etiqueta);
}
