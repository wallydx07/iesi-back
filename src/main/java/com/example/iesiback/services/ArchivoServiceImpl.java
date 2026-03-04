package com.example.iesiback.services;

import com.example.iesiback.entities.Archivo;
import com.example.iesiback.repositories.ArchivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ArchivoServiceImpl implements ArchivoService {

    private final ArchivoRepository archivoRepository;

    @Autowired
    public ArchivoServiceImpl(ArchivoRepository archivoRepository) {
        this.archivoRepository = archivoRepository;
    }

    @Override
    public boolean existePorEtiqueta(String etiqueta) {
        return archivoRepository.findByEtiqueta(etiqueta).isPresent();
    }



    @Override
    @Transactional
    public byte[] descargarArchivosPorEtiqueta(String etiqueta) {
        Optional<Archivo> archivoOptional = archivoRepository.findByEtiqueta(etiqueta); // Usamos el repositorio para obtener el archivo por etiqueta
        if (archivoOptional.isEmpty()) {
            return null;
        }
        Archivo archivo = archivoOptional.get();
        return archivo.getDato(); // Retornar los bytes del archivo PDF directamente
    }

    @Transactional
    @Override
    public byte[] descargarArchivosPorId(int id) {
        Optional<Archivo> archivoOptional = archivoRepository.findById(id); // Usamos el repositorio para obtener el archivo por etiqueta
        if (archivoOptional.isEmpty()) {
            return null;
        }
        Archivo archivo = archivoOptional.get();
        return archivo.getDato(); // Retornar los bytes del archivo PDF directamente
    }

}