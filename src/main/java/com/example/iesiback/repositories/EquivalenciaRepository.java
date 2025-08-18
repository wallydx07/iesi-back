package com.example.iesiback.repositories;

import com.example.iesiback.entities.Equivalencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquivalenciaRepository extends JpaRepository<Equivalencia, Long> {

    List<Equivalencia> findByLegajoId(Integer legajoId);

    List<Equivalencia> findByMateriaId(Integer materiaId);

    List<Equivalencia> findByStatus(String status);

}
