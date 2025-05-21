package com.example.iesiback.repositories;
import com.example.iesiback.entities.Equivalencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquivalenciaRepository extends JpaRepository<Equivalencia, Long> {
}