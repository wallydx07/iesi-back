package com.example.iesiback.repositories;

import com.example.iesiback.entities.Legajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegajoRepository extends JpaRepository<Legajo, String> {
}
