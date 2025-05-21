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

    @GetMapping("/buscar/nombre")
    public List<Personal> buscarPorNombre(@RequestParam String nombre) {
        return personalService.findByPersonalNombre(nombre);
    }

    @GetMapping("/buscar/apellido")
    public List<Personal> buscarPorApellido(@RequestParam String apellido) {
        return personalService.findByPersonalApellido(apellido);
    }

    @GetMapping("/buscar/correo")
    public List<Personal> buscarPorCorreo(@RequestParam String correo) {
        return personalService.findByPersonalCorreo(correo);
    }

    @GetMapping("/buscar/celular")
    public List<Personal> buscarPorCelular(@RequestParam String celular) {
        return personalService.findByPersonalCelular(celular);
    }

    @GetMapping("/buscar/tipo")
    public List<Personal> buscarPorTipo(@RequestParam String tipo) {
        return personalService.findByPersonalTipo(tipo);
    }

    @GetMapping("/buscar/rdni")
    public List<Personal> buscarPorRdni(@RequestParam Boolean rdni) {
        return personalService.findByPersonalRdni(rdni);
    }

    @GetMapping("/buscar/rresidencia")
    public List<Personal> buscarPorRresidencia(@RequestParam Boolean rresidencia) {
        return personalService.findByPersonalRresidencia(rresidencia);
    }

    @GetMapping("/buscar/rplanilla")
    public List<Personal> buscarPorRplanilla(@RequestParam Boolean rplanilla) {
        return personalService.findByPersonalRplanilla(rplanilla);
    }

    @GetMapping("/buscar/rsanitario")
    public List<Personal> buscarPorRsanitario(@RequestParam Boolean rsanitario) {
        return personalService.findByPersonalRsanitario(rsanitario);
    }

    @GetMapping("/buscar/rnacimiento")
    public List<Personal> buscarPorRnacimiento(@RequestParam Boolean rnacimiento) {
        return personalService.findByPersonalRnacimiento(rnacimiento);
    }

    @GetMapping("/buscar/declaracion")
    public List<Personal> buscarPorDeclaracion(@RequestParam Boolean declaracion) {
        return personalService.findByDeclaracion(declaracion);
    }

    @GetMapping("/buscar/cargo")
    public List<Personal> buscarPorCargo(@RequestParam Boolean cargo) {
        return personalService.findByCargo(cargo);
    }

    @GetMapping("/buscar/cuil")
    public List<Personal> buscarPorCuil(@RequestParam Boolean cuil) {
        return personalService.findByCuil(cuil);
    }

    @GetMapping("/buscar/titulo")
    public List<Personal> buscarPorTitulo(@RequestParam Boolean titulo) {
        return personalService.findByTitulo(titulo);
    }

    @GetMapping("/buscar/curriculum")
    public List<Personal> buscarPorCurriculum(@RequestParam Boolean curriculum) {
        return personalService.findByCurriculum(curriculum);
    }

}