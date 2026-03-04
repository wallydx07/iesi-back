package com.example.iesiback.controllers;

import com.example.iesiback.dto.ImportResponseDTO;
import com.example.iesiback.dto.NotaImportDTO;
import com.example.iesiback.services.TrayectoriaAcademicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/trayectoria-academica")
@RequiredArgsConstructor
public class TrayectoriaAcademicaController {

    private final TrayectoriaAcademicaService trayectoriaAcademicaService;

    @PostMapping("/importar-notas")
    public ResponseEntity<ImportResponseDTO> importarNotas(
            @Valid @RequestBody List<NotaImportDTO> notas) {

        if (notas == null || notas.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ImportResponseDTO(
                            0,
                            "La lista de notas está vacía"));
        }

        trayectoriaAcademicaService.procesarNotas(notas);

        ImportResponseDTO response =
                new ImportResponseDTO(
                        notas.size(),
                        "Importación realizada correctamente");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}