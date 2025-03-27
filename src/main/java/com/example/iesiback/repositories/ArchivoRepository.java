package com.example.iesiback.repositories;

import com.example.iesiback.entities.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ArchivoRepository extends JpaRepository<Archivo, Integer> {
    Optional<Archivo> findByEtiqueta(String etiqueta);
}