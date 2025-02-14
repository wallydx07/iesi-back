package com.example.iesiback.controllers;

import com.example.iesiback.dto.InscripcionExamenDTO;
import com.example.iesiback.services.ExamenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/examenes")
public class ExamenController {

    @Autowired
    private ExamenService examenService;

    @GetMapping("/cursadas")
    public ResponseEntity<List<InscripcionExamenDTO>> completarCursadas(
            @RequestParam String legajoId,
            @RequestParam String turno) {  // Ahora el turno es un mes numérico

        List<InscripcionExamenDTO> examenes = examenService.completarCursadas(legajoId, turno);
        return ResponseEntity.ok(examenes);
    }
}
