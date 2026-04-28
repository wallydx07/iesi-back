package com.example.iesiback.controllers;

import com.example.iesiback.entities.Tramite;
import com.example.iesiback.services.TramiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tramite")
public class TramiteController {


    private final TramiteService service;


    public TramiteController(TramiteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Tramite> getAll() {
        return service.findAll();
    }

    @GetMapping("/findAllByOrderByAtencionFechaDesc")
    public List<Tramite> findAllOrderByFecha() {
        return service.findAllByOrderByAtencionFechaDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tramite> getById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Tramite create(@RequestBody Tramite atencion) {
        atencion.setTramiteFecha(LocalDateTime.now());
        return service.save(atencion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tramite> update(@PathVariable Integer id, @RequestBody Tramite atencion) {
        return service.findById(id).map(existing -> {
            // Actualizar solo campos simples (no relaciones)
            existing.setTramiteEstado(atencion.getTramiteEstado());
            existing.setTramiteDni(atencion.getTramiteDni());
            existing.setTramiteApellidoNombre(atencion.getTramiteApellidoNombre());
            existing.setTramiteCorreo(atencion.getTramiteCorreo());
            existing.setTramiteCelular(atencion.getTramiteCelular());
            existing.setTramiteTipo(atencion.getTramiteTipo());
            existing.setTramiteProblema(atencion.getTramiteProblema());
            existing.setTramiteFecha(atencion.getTramiteFecha());
            existing.setTramiteRespuesta(atencion.getTramiteRespuesta());
            existing.setTramiteObservaciones(atencion.getTramiteObservaciones());
            existing.setTramiteDestino(atencion.getTramiteDestino());
            existing.setTramiteUsuario(atencion.getTramiteUsuario());
            existing.setCodigoSeguimiento(atencion.getCodigoSeguimiento());
            existing.setGestorDni(atencion.getGestorDni());
            existing.setTramiteCanal(atencion.getTramiteCanal());
            existing.setTramiteAsunto(atencion.getTramiteAsunto());
            existing.setTramiteSubTipo(atencion.getTramiteSubTipo());

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
    public List<Tramite> getByDni(@PathVariable Long dni) {
        return service.findByDni(dni);
    }

    @GetMapping("/nombre/{nombre}")
    public List<Tramite> getByNombre(@PathVariable String nombre) {
        return service.findByApellidoNombre(nombre);
    }

    @GetMapping("/correo/{correo}")
    public List<Tramite> getByCorreo(@PathVariable String correo) {
        return service.findByCorreo(correo);
    }

    @GetMapping("/destino/{destino}")
    public List<Tramite> getByDestino(@PathVariable String destino) {
        return service.findByDestino(destino);
    }

    @GetMapping("/usuario/{usuario}")
    public List<Tramite> getByUsuario(@PathVariable String usuario) {
        return service.findByUsuario(usuario);
    }

    @GetMapping("/resuelto/{resuelto}")
    public List<Tramite> getByResuelto(@PathVariable String resuelto) {
        return service.findByResuelto(resuelto);
    }

    @GetMapping("/fecha/{fecha}")
    public List<Tramite> getByFecha(@PathVariable String fecha) {
        return service.findByFecha(LocalDate.parse(fecha));
    }

    @GetMapping("/seguimiento/{codigo}")
    public ResponseEntity<Tramite> obtenerPorCodigoSeguimiento(@PathVariable String codigo) {
        return service.findByCodigoSeguimiento(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/gestor/{dni}")
    public ResponseEntity<List<Tramite>> obtenerPorGestor(@PathVariable Long dni) {
        List<Tramite> lista = service.obtenerPorGestor(dni);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/referencia/{id}")
    public ResponseEntity<List<Tramite>> obtenerPorReferencia(@PathVariable Integer id) {
        List<Tramite> lista = service.findByAtencionReferencia(id);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/findByLegajoId/{legajoId}")
    public List<Tramite> findByLegajoId(@PathVariable String legajoId) {
        return service.findByAtencionLegajoId(legajoId);
    }
}