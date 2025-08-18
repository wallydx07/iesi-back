package com.example.iesiback.services;

import com.example.iesiback.entities.ConstanciaPrecio;
import com.example.iesiback.repositories.ConstanciaPrecioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConstanciaPrecioServiceImpl implements ConstanciaPrecioService {

    @Autowired
    private ConstanciaPrecioRepository repository;

    @Override
    public Optional<ConstanciaPrecio> findByTipoConstancia(String tipoConstancia) {
        return repository.findByTipoConstancia(tipoConstancia);
    }

    @Override
    public List<ConstanciaPrecio> obtenerTodas() {
        return repository.findAll();
    }

}