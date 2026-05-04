package com.example.iesiback.repositories;

import com.example.iesiback.entities.PagoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoDetalleRepository extends JpaRepository<PagoDetalle, Integer> {

    List<PagoDetalle> findByPagoId(Integer pagoId);

}