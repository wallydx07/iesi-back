package com.example.iesiback.repositories;
import com.example.iesiback.entities.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    // Método para encontrar documentos por nombre
    Documento findByNombre(String nombre);
    List<Documento> findByEntidadId(String legajoId);
}