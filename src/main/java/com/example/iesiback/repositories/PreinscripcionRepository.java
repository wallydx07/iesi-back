package com.example.iesiback.repositories;

import com.example.iesiback.entities.Preinscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreinscripcionRepository extends JpaRepository<Preinscripcion, Integer> {
}
