package com.example.iesiback.controllers;

import com.example.iesiback.entities.Atencion;
import com.example.iesiback.services.AtencionService;
import com.example.iesiback.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/atenciones")
public class AtencionController {

    private final AtencionService service;

    public AtencionController(AtencionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Atencion> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atencion> getById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Atencion create(@RequestBody Atencion atencion) {
        atencion.setAtencionFecha(LocalDate.now());
        return service.save(atencion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Atencion> update(@PathVariable Integer id, @RequestBody Atencion atencion) {
        return service.findById(id).map(existing -> {
            // Actualizar solo campos simples (no relaciones)
            existing.setAtencionResuelto(atencion.getAtencionResuelto());
            existing.setAtencionDni(atencion.getAtencionDni());
            existing.setAtencionApellidoNombre(atencion.getAtencionApellidoNombre());
            existing.setAtencionCorreo(atencion.getAtencionCorreo());
            existing.setAtencionCelular(atencion.getAtencionCelular());
            existing.setAtencionConsulta(atencion.getAtencionConsulta());
            existing.setAtencionProblema(atencion.getAtencionProblema());
            existing.setAtencionFecha(atencion.getAtencionFecha());
            existing.setAtencionRespuesta(atencion.getAtencionRespuesta());
            existing.setAtencionObservaciones(atencion.getAtencionObservaciones());
            existing.setAtencionDestino(atencion.getAtencionDestino());
            existing.setAtencionUsuario(atencion.getAtencionUsuario());
            existing.setCodigoSeguimiento(atencion.getCodigoSeguimiento());
            return ResponseEntity.ok(service.update(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return service.findById(id).map(a -> {
            service.deleteById(id);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // Búsquedas por campos
    @GetMapping("/dni/{dni}")
    public List<Atencion> getByDni(@PathVariable Long dni) {
        return service.findByDni(dni);
    }

    @GetMapping("/nombre/{nombre}")
    public List<Atencion> getByNombre(@PathVariable String nombre) {
        return service.findByApellidoNombre(nombre);
    }

    @GetMapping("/correo/{correo}")
    public List<Atencion> getByCorreo(@PathVariable String correo) {
        return service.findByCorreo(correo);
    }

    @GetMapping("/destino/{destino}")
    public List<Atencion> getByDestino(@PathVariable String destino) {
        return service.findByDestino(destino);
    }

    @GetMapping("/usuario/{usuario}")
    public List<Atencion> getByUsuario(@PathVariable String usuario) {
        return service.findByUsuario(usuario);
    }

    @GetMapping("/resuelto/{resuelto}")
    public List<Atencion> getByResuelto(@PathVariable Boolean resuelto) {
        return service.findByResuelto(resuelto);
    }

    @GetMapping("/fecha/{fecha}")
    public List<Atencion> getByFecha(@PathVariable String fecha) {
        return service.findByFecha(LocalDate.parse(fecha));
    }

    @GetMapping("/seguimiento/{codigo}")
    public ResponseEntity<Atencion> obtenerPorCodigoSeguimiento(@PathVariable String codigo) {
        return service.findByCodigoSeguimiento(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
