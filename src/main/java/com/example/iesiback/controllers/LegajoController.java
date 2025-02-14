package com.example.iesiback.controllers;
import com.example.iesiback.entities.Alumno;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.services.LegajoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins={"http://localhost:4200"})
@RestController
@RequestMapping("/api/legajos")
public class LegajoController {

    @Autowired
    private LegajoService LegajoService;

    @GetMapping
    public List<Legajo> obtenerLegajos() {
        return LegajoService.obtenerLegajos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Legajo> getLegajoById(@PathVariable String id) {
        return LegajoService.findById(id)
                .map(legajo -> ResponseEntity.ok().body(legajo))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
