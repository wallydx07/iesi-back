package com.example.iesiback.services;

import com.example.iesiback.dto.ProductoDTO;
import com.example.iesiback.entities.Pago;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PagoService {

    Pago guardar(Pago pago);

    Optional<Pago> buscarPorId(Integer id);

    List<Pago> listarTodos();

    void eliminar(Integer id);


    Optional<Pago> findByAtencionId(Integer id);

    Map<String, String> crearPreferencia(ProductoDTO producto) throws Exception;
    void procesarWebhook(Map<String, Object> payload) throws Exception;

}
