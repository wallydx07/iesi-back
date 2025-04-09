package com.example.iesiback.controllers;

import com.example.iesiback.entities.CertificadoEstudiante;
import com.example.iesiback.services.CertificadoEstudianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/constancias")
@CrossOrigin(origins = "*")
public class ConstanciaController {

    @Autowired
    private CertificadoEstudianteService service;

    @GetMapping
    public List<CertificadoEstudiante> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificadoEstudiante> findById(@PathVariable Integer id) {
        CertificadoEstudiante c = service.findById(id);
        return c != null ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public CertificadoEstudiante save(@RequestBody CertificadoEstudiante constancia) {
        return service.save(constancia);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Búsquedas

    @GetMapping("/tipo/{tipo}")
    public List<CertificadoEstudiante> findByTipo(@PathVariable String tipo) {
        return service.findByTipo(tipo);
    }

    @GetMapping("/autoridad/{autoridad}")
    public List<CertificadoEstudiante> findByAutoridad(@PathVariable String autoridad) {
        return service.findByAutoridad(autoridad);
    }

    @GetMapping("/estado/{estado}")
    public List<CertificadoEstudiante> findByEstado(@PathVariable String estado) {
        return service.findByEstado(estado);
    }

    @GetMapping("/usuario/{usuario}")
    public List<CertificadoEstudiante> findByUsuario(@PathVariable String usuario) {
        return service.findByUsuario(usuario);
    }

    @GetMapping("/validado/{validado}")
    public List<CertificadoEstudiante> findByValidado(@PathVariable Boolean validado) {
        return service.findByValidado(validado);
    }

    @GetMapping("/monto/{monto}")
    public List<CertificadoEstudiante> findByMonto(@PathVariable Integer monto) {
        return service.findByMonto(monto);
    }

    @GetMapping("/atencion/{atencionId}")
    public List<CertificadoEstudiante> findByAtencion(@PathVariable Integer atencionId) {
        return service.findByAtencionId(atencionId);
    }

    @GetMapping("/legajo/{legajoId}")
    public List<CertificadoEstudiante> findByLegajoId(@PathVariable String legajoId) {
        return service.findByLegajoId(legajoId);
    }
}
