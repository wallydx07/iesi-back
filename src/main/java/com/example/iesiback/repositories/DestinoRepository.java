package com.example.iesiback.repositories;

import com.example.iesiback.entities.Destino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinoRepository extends JpaRepository<Destino, Long> {

    // Buscar por nombre exacto
    Destino findByNombre(String nombre);

    // Listar solo los activos
    List<Destino> findByActivoTrue();
}
