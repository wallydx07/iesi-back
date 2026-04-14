package com.example.iesiback.controllers;

import com.example.iesiback.dto.ImportResponseDTO;
import com.example.iesiback.dto.NotaImportDTO;
import com.example.iesiback.dto.ResultadoImportDTO;
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
                    .body(new ImportResponseDTO(0, "La lista de notas está vacía"));
        }

        ResultadoImportDTO resultado = trayectoriaAcademicaService.procesarNotas(notas);

        String mensaje = resultado.tieneErrores()
                ? "Importación finalizada con " + resultado.getTotalErrores() + " error/es"
                : "Importación realizada correctamente";

        ImportResponseDTO response = new ImportResponseDTO(
                resultado.getFilasExitosas(), mensaje);

        response.setErrores(resultado.getErrores());

        HttpStatus status = resultado.tieneErrores()
                ? HttpStatus.MULTI_STATUS
                : HttpStatus.OK;

        return ResponseEntity.status(status).body(response);
    }



}