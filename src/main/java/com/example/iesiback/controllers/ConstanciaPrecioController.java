package com.example.iesiback.controllers;

import com.example.iesiback.entities.ConstanciaPrecio;
import com.example.iesiback.services.ConstanciaPrecioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/constancia-precios")
@CrossOrigin(origins = "*")
public class ConstanciaPrecioController {

    @Autowired
    private ConstanciaPrecioService service;

    @GetMapping
    public List<ConstanciaPrecio> obtenerTodasLasConstancias(
            @RequestParam(required = false) List<Long> ids
    ) {
        if (ids != null && !ids.isEmpty()) {
            return service.findByIdIn(ids);
        }
        return service.obtenerTodas();
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<ConstanciaPrecio> obtenerPorTipoConstancia(
            @PathVariable String tipo
    ) {
        return service.findByTipoConstancia(tipo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}