package com.example.iesiback.repositories;

import com.example.iesiback.entities.Turno; // Asegúrate de importar la entidad Turno
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {
    List<Turno> findAllByOrderByTurnoLimiteDesc();

}
