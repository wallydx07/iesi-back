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
    public ResponseEntity<List<ConstanciaPrecio>> obtenerPorTipoConstancia(
            @PathVariable String tipo) {

        System.out.println("🔍 [constancia-precios/tipo] tipo recibido = '" + tipo + "'"
                + " | length=" + (tipo != null ? tipo.length() : "null"));

        List<ConstanciaPrecio> servicios = service.findByTipoConstancia(tipo);

        System.out.println("🔍 [constancia-precios/tipo] resultados = " + servicios.size());
        servicios.forEach(s -> System.out.println("   → " + s.getId() + " | " + s.getNombre()));

        if (servicios.isEmpty()) {
            System.out.println("⚠️ [constancia-precios/tipo] lista vacía → devolviendo 404");
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(servicios);
    }
}