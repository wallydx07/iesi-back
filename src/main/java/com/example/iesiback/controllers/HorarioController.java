package com.example.iesiback.controllers;

import com.example.iesiback.dto.HorarioDTO;
import com.example.iesiback.entities.PersonalHorario;
import com.example.iesiback.services.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    @GetMapping("/{dni}")
    public ResponseEntity<List<HorarioDTO>> obtenerPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(horarioService.obtenerPorDni(dni));
    }

    @PostMapping
    public ResponseEntity<PersonalHorario> guardar(@RequestBody PersonalHorario horario) {
        return ResponseEntity.ok(horarioService.guardar(horario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonalHorario> actualizar(@PathVariable Integer id, @RequestBody PersonalHorario horario) {
        return ResponseEntity.ok(horarioService.actualizar(id, horario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        horarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/obtenerPorId/{id}")
    public ResponseEntity<PersonalHorario> obtenerPorId(@PathVariable Integer id) {
        Optional<PersonalHorario> optional = horarioService.findById(id);
        return optional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
