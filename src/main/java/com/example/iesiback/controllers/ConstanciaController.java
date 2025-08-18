package com.example.iesiback.controllers;

import com.example.iesiback.dto.AporteDTO;
import com.example.iesiback.dto.CertificadoEstudianteDTO;
import com.example.iesiback.entities.Atencion;
import com.example.iesiback.entities.CertificadoEstudiante;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.services.AtencionService;
import com.example.iesiback.services.CertificadoEstudianteService;
import com.example.iesiback.services.LegajoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/constancias")
@CrossOrigin(origins = "*")
public class ConstanciaController {

    @Autowired
    private CertificadoEstudianteService certificadoEstudianteService;

    @Autowired
    private LegajoService legajoService;

    @Autowired
    private AtencionService atencionService;


    @GetMapping
    public List<CertificadoEstudiante> findAll() {
        return certificadoEstudianteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificadoEstudianteDTO> findById(@PathVariable Integer id) {
        CertificadoEstudiante certificado = certificadoEstudianteService.findById(id);
        if (certificado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new CertificadoEstudianteDTO(certificado));
    }


//    @PostMapping
//    public CertificadoEstudiante save(@RequestBody CertificadoEstudiante constancia) {
//        return service.save(constancia);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        certificadoEstudianteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Búsquedas

    @GetMapping("/tipo/{tipo}")
    public List<CertificadoEstudiante> findByTipo(@PathVariable String tipo) {
        return certificadoEstudianteService.findByTipo(tipo);
    }

    @GetMapping("/autoridad/{autoridad}")
    public List<CertificadoEstudiante> findByAutoridad(@PathVariable String autoridad) {
        return certificadoEstudianteService.findByAutoridad(autoridad);
    }

    @GetMapping("/estado/{estado}")
    public List<CertificadoEstudiante> findByEstado(@PathVariable String estado) {
        return certificadoEstudianteService.findByEstado(estado);
    }

    @GetMapping("/usuario/{usuario}")
    public List<CertificadoEstudiante> findByUsuario(@PathVariable String usuario) {
        return certificadoEstudianteService.findByUsuario(usuario);
    }

    @GetMapping("/validado/{validado}")
    public List<CertificadoEstudiante> findByValidado(@PathVariable Boolean validado) {
        return certificadoEstudianteService.findByValidado(validado);
    }

    @GetMapping("/monto/{monto}")
    public List<CertificadoEstudiante> findByMonto(@PathVariable Integer monto) {
        return certificadoEstudianteService.findByMonto(monto);
    }

    @GetMapping("/atencion/{atencionId}")
    public List<CertificadoEstudiante> findByAtencion(@PathVariable Integer atencionId) {
        return certificadoEstudianteService.findByAtencionId(atencionId);
    }

    @GetMapping("/legajo/{legajoId}")
    public List<CertificadoEstudiante> findByLegajoId(@PathVariable String legajoId) {
        return certificadoEstudianteService.findByLegajoId(legajoId);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<CertificadoEstudiante>> guardarVarios(@RequestBody List<CertificadoEstudiante> certificados,
                                                                     @RequestParam(required = false) String legajoId) {
        List<CertificadoEstudiante> guardados = certificadoEstudianteService.saveAll(certificados);
        return ResponseEntity.ok(guardados);
    }
//
//    @PostMapping("/batch")
//    public ResponseEntity<List<CertificadoEstudiante>> guardarVarios(
//            @RequestBody List<CertificadoEstudiante> certificados,
//            @RequestParam(required = false) String legajoId) {
//        for (CertificadoEstudiante certificado : certificados) {
//            System.out.println("Certificado ID: " + certificado.getId());
//            System.out.println("Tipo: " + certificado.getTipo());
//            System.out.println("Autoridad: " + certificado.getAutoridad());
//            System.out.println("Fecha: " + certificado.getFecha());
//            System.out.println("Estado: " + certificado.getEstado());
//            System.out.println("Observaciones: " + certificado.getObservaciones());
//            System.out.println("Usuario: " + certificado.getUsuario());
//            System.out.println("Validado: " + certificado.getValidado());
//            System.out.println("Monto: " + certificado.getMonto());
//            System.out.println("Legajo ID: " + certificado.getLegajo().getLegajoId());
//            System.out.println("Atencion ID: " + certificado.getAtencion().getId());
//            System.out.println("--------------------------------------");
//        }
//        if (legajoId != null) {
//            Legajo legajo = legajoService.findById(legajoId).get();
//            if (legajo != null) {
//                for (CertificadoEstudiante certificado : certificados) {
//                    certificado.setLegajo(legajo);
//                }
//            } else {
//                return ResponseEntity.badRequest().body(null); // legajo no encontrado
//            }
//        }
//
//        List<CertificadoEstudiante> guardados = certificadoEstudianteService.saveAll(certificados);
//        return ResponseEntity.ok(guardados);
//    }

    @PostMapping("/certificado")
    public ResponseEntity<CertificadoEstudiante> saveOrUpdate(
            @RequestBody CertificadoEstudiante certificado) {
        // Traemos el certificado actual desde la base
        CertificadoEstudiante updateCertificado = this.certificadoEstudianteService.findById(certificado.getId());
        // Actualizamos los campos del certificado existente
        updateCertificado.setTipo(certificado.getTipo());
        updateCertificado.setAutoridad(certificado.getAutoridad());
        updateCertificado.setFecha(certificado.getFecha());
        updateCertificado.setEstado(certificado.getEstado());
        updateCertificado.setObservaciones(certificado.getObservaciones());
        updateCertificado.setUsuario(certificado.getUsuario());
        updateCertificado.setValidado(certificado.getValidado());
        updateCertificado.setMonto(certificado.getMonto());
        // Logs de verificación
        System.out.println("Certificado ID: " + updateCertificado.getId());
        System.out.println("Tipo: " + updateCertificado.getTipo());
        System.out.println("Autoridad: " + updateCertificado.getAutoridad());
        System.out.println("Fecha: " + updateCertificado.getFecha());
        System.out.println("Estado: " + updateCertificado.getEstado());
        System.out.println("Observaciones: " + updateCertificado.getObservaciones());
        System.out.println("Usuario: " + updateCertificado.getUsuario());
        System.out.println("Validado: " + updateCertificado.getValidado());
        System.out.println("Monto: " + updateCertificado.getMonto());
        System.out.println("Legajo ID: " + updateCertificado.getAtencion().getLegajo().getLegajoId());
        System.out.println("Atencion ID: " + updateCertificado.getAtencion().getId());
        System.out.println("--------------------------------------");

        // Guardamos el certificado actualizado
        CertificadoEstudiante saved = certificadoEstudianteService.save(updateCertificado);
        return ResponseEntity.ok(saved);
    }


    @GetMapping("/aportados")
    public ResponseEntity<List<AporteDTO>> getCertificadosComoAportes() {
        List<AporteDTO> certificados = certificadoEstudianteService.obtenerCertificadosComoAportes();
        if (certificados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(certificados);
    }


}
