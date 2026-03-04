package com.example.iesiback.services;

import com.example.iesiback.entities.Documento;
import com.example.iesiback.repositories.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentoServiceImpl implements DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    // -----------------------------------------------------
    // GUARDAR
    // -----------------------------------------------------
    @Override
    public Documento saveDocumento(Documento documento) {
        return documentoRepository.save(documento);
    }

    // -----------------------------------------------------
    // ACTUALIZAR
    // -----------------------------------------------------
    @Override
    public Documento updateDocumento(Long id, Documento documento) {
        Documento existente = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + id));

        existente.setNombre(documento.getNombre());
        existente.setTipo(documento.getTipo());
        existente.setRuta(documento.getRuta());
        existente.setTamanio(documento.getTamanio());
        existente.setTipoEntidad(documento.getTipoEntidad());
        existente.setEntidadId(documento.getEntidadId());

        return documentoRepository.save(existente);
    }

    // -----------------------------------------------------
    // LISTAR TODOS
    // -----------------------------------------------------
    @Override
    public List<Documento> getAllDocumentos() {
        return documentoRepository.findAll();
    }

    // -----------------------------------------------------
    // OBTENER POR ID
    // -----------------------------------------------------
    @Override
    public Documento getDocumentoById(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + id));
    }

    // -----------------------------------------------------
    // ELIMINAR (DB + ARCHIVO FÍSICO)
    // -----------------------------------------------------
    @Override
    public void deleteDocumento(Long id) {
        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + id));

        // Eliminar archivo físico
        String ruta = doc.getRuta();
        if (ruta != null) {
            try {
                Path path = Paths.get(ruta);
                Files.deleteIfExists(path);
                System.out.println("Archivo eliminado: " + ruta);
            } catch (IOException e) {
                System.err.println("No se pudo eliminar el archivo: " + ruta);
                e.printStackTrace();
            }
        }

        // Eliminar de BD
        documentoRepository.deleteById(id);
    }



    // =====================================================
    // NUEVOS MÉTODOS
    // =====================================================

    // Buscar por nombre
    @Override
    public Documento findByNombre(String nombre) {
        return documentoRepository.findByNombre(nombre);
    }

    // Buscar lista por entidadId
    @Override
    public List<Documento> findByEntidadId(String entidadId) {
        return documentoRepository.findByEntidadId(entidadId);
    }

    // Buscar por entidadId + tipoDocumento (único)
    @Override
    public Optional<Documento> findByEntidadIdAndTipoDocumento(String entidadId, String tipoDocumento) {
        return documentoRepository.findByEntidadIdAndTipoDocumento(entidadId, tipoDocumento);
    }
}
