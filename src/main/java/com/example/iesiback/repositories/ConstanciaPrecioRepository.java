package com.example.iesiback.repositories;

import com.example.iesiback.entities.ConstanciaPrecio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConstanciaPrecioRepository extends JpaRepository<ConstanciaPrecio, Long> {

    Optional<ConstanciaPrecio> findByNombre(String tipoConstancia);

    List<ConstanciaPrecio> findByIdIn(List<Long> ids); // 🔥 clave

    List<ConstanciaPrecio> findByTipo(String tipo);
}