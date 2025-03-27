package com.example.iesiback.services;

import com.example.iesiback.entities.Documento;
import com.example.iesiback.repositories.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentoServiceImpl implements DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    @Override
    public Documento saveDocumento(Documento documento) {
        return documentoRepository.save(documento);
    }

    @Override
    public Documento updateDocumento(Long id, Documento documento) {
        Optional<Documento> existingDocumento = documentoRepository.findById(id);
        if (existingDocumento.isPresent()) {
            Documento updatedDocumento = existingDocumento.get();
            updatedDocumento.setNombre(documento.getNombre());
            updatedDocumento.setTipo(documento.getTipo());
            updatedDocumento.setRuta(documento.getRuta());
            updatedDocumento.setTamanio(documento.getTamanio());
            updatedDocumento.setTipoEntidad(documento.getTipoEntidad());
            updatedDocumento.setEntidadId(documento.getEntidadId());
            return documentoRepository.save(updatedDocumento);
        } else {
            throw new RuntimeException("Documento no encontrado con id: " + id);
        }
    }

    @Override
    public List<Documento> getAllDocumentos() {
        return documentoRepository.findAll();
    }

    @Override
    public Documento getDocumentoById(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + id));
    }

    @Override
    public void deleteDocumento(Long id) {
        documentoRepository.deleteById(id);
    }
}