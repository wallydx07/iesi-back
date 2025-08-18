package com.example.iesiback.repositories;

import com.example.iesiback.entities.ConstanciaPrecio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConstanciaPrecioRepository extends JpaRepository<ConstanciaPrecio, Long> {
    Optional<ConstanciaPrecio> findByTipoConstancia(String tipoConstancia);
}