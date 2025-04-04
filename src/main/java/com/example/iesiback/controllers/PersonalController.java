package com.example.iesiback.controllers;

import com.example.iesiback.entities.Personal;
import com.example.iesiback.services.PersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/personal")
public class PersonalController {

    private final PersonalService personalService;

    @Autowired
    public PersonalController(PersonalService personalService) {
        this.personalService = personalService;
    }

    @GetMapping
    public List<Personal> getAll() {
        return personalService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Personal> getById(@PathVariable String id) {
        return personalService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Personal create(@RequestBody Personal personal) {
        return personalService.save(personal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Personal> update(@PathVariable String id, @RequestBody Personal personalDetails) {
        return personalService.findById(id).map(personal -> {
            personal.setPersonalNombre(personalDetails.getPersonalNombre());
            personal.setPersonalApellido(personalDetails.getPersonalApellido());
            personal.setPersonalCorreo(personalDetails.getPersonalCorreo());
            personal.setPersonalCelular(personalDetails.getPersonalCelular());
            personal.setPersonalTipo(personalDetails.getPersonalTipo());
            personal.setPersonalRdni(personalDetails.getPersonalRdni());
            personal.setPersonalRresidencia(personalDetails.getPersonalRresidencia());
            personal.setPersonalRplanilla(personalDetails.getPersonalRplanilla());
            personal.setPersonalRsanitario(personalDetails.getPersonalRsanitario());
            personal.setPersonalRnacimiento(personalDetails.getPersonalRnacimiento());
            personal.setDeclaracion(personalDetails.getDeclaracion());
            personal.setCargo(personalDetails.getCargo());
            personal.setCuil(personalDetails.getCuil());
            personal.setTitulo(personalDetails.getTitulo());
            personal.setCurriculum(personalDetails.getCurriculum());
            return ResponseEntity.ok(personalService.save(personal));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (personalService.findById(id).isPresent()) {
            personalService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/{dni}")
    public ResponseEntity<Boolean> existsByDni(@PathVariable String dni) {
        boolean exists = personalService.existsByDni(dni);
        return ResponseEntity.ok(exists);
    }

}