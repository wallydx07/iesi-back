package com.example.iesiback.repositories;
import com.example.iesiback.entities.Pases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PasesRepository extends JpaRepository<Pases, Long> {

    // Obtener todos los pases de un trámite
    List<Pases> findByTramiteId(Long tramiteId);

    // Obtener los pases enviados por un usuario
    List<Pases> findByDeUsuarioId(Long deUsuarioId);

    List<Pases> findByParaDestinoId(Long paraDestino_id);

    // Obtener los pases recibidos por un usuario
    List<Pases> findByParaUsuarioId(Long id);

}