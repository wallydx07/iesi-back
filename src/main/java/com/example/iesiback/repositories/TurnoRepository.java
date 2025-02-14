package com.example.iesiback.repositories;

import com.example.iesiback.entities.Turno; // Asegúrate de importar la entidad Turno
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {
}
