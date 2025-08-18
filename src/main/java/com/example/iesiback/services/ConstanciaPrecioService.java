package com.example.iesiback.services;

import com.example.iesiback.entities.ConstanciaPrecio;

import java.util.List;
import java.util.Optional;

public interface ConstanciaPrecioService {
    Optional<ConstanciaPrecio> findByTipoConstancia(String tipoConstancia);

    List<ConstanciaPrecio> obtenerTodas();
}
