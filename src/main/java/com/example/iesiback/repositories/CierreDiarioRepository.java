package com.example.iesiback.repositories;
import com.example.iesiback.entities.CierreDiario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CierreDiarioRepository extends JpaRepository<CierreDiario, Long> {

    Optional<CierreDiario> findByFecha(LocalDate fecha);

    boolean existsByFecha(LocalDate fecha);
}