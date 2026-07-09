package com.example.iesiback.controllers;

import com.example.iesiback.entities.Carrera;
import com.example.iesiback.services.CarreraService;
import com.example.iesiback.services.MateriaCarreraService;
import com.example.iesiback.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    private final UserService userService;

    @Autowired
    private CarreraService carreraService;

    @Autowired
    private MateriaCarreraService materiaCarreraService;

    public CarreraController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<Carrera> obtenerCarreras() {
        return carreraService.obtenerCarreras();
    }

    @GetMapping("/ordenadas")
    public ResponseEntity<List<Carrera>> obtenerCarrerasOrdenadas() {
        List<Carrera> carreras = carreraService.obtenerCarrerasOrdenadas();
        return ResponseEntity.ok(carreras);
    }

    @GetMapping("/nombres")
    public ResponseEntity<List<String>> obtenerCarrerasNombres() {
        List<String> nombres = carreraService.obtenerCarrerasNombres();
        return ResponseEntity.ok(nombres);
    }

    @GetMapping("/legajo/{legajoId}")
    public Carrera obtenerCarreraPorLegajo(@PathVariable String legajoId) {
        return carreraService.obtenerCarreraPorLegajoId(legajoId);
    }

    @GetMapping("/ordenadas/por-usuario/")
    public ResponseEntity<List<Carrera>> obtenerCarrerasPorUsuario() {
        List<Carrera> carreras;
        String userRol= String.valueOf(userService.getAuthenticatedUser().get().getRoles().get(0).getRoleNombre());
        Long userId= Long.valueOf(userService.getAuthenticatedUser().get().getUsername());
        if ("ROLE_ADMIN".equalsIgnoreCase(userRol) || "ROLE_PERSONAL".equalsIgnoreCase(userRol) || "ROLE_TITULACION".equalsIgnoreCase(userRol) || "ROLE_DIRECTIVO".equalsIgnoreCase(userRol)) {
//            carreras = carreraService.obtenerCarrerasOrdenadas();
            carreras = carreraService.findVigentesOrderedByYearAndName();
        } else if ("ROLE_TUTOR".equalsIgnoreCase(userRol)) {
            carreras = carreraService.obtenerCarrerasPorTutor(userId);
        } else if("ROLE_DOCENTE".equalsIgnoreCase(userRol)) {
            carreras = materiaCarreraService.obtenerCarrerasPorDocente(userId);
        }else {
            System.out.println("NO AUTORIZADO!!!!!");
            return ResponseEntity.status(
                    HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(carreras);
    }

    @GetMapping("/inscripcion")
    public ResponseEntity<List<Carrera>> obtenerCarrerasInscripcion(@RequestParam String alumnoDni) {
        List<Carrera> carreras = carreraService.obtenerCarreraInstcripcion(alumnoDni);
        return ResponseEntity.ok(carreras);
    }

    @GetMapping("/anio-cursada")
    public ResponseEntity<String> getAnioCursada(@RequestParam String libretaEstudiantil) {
        try {
            String anioCursada = carreraService.obtenerAnioCursada(libretaEstudiantil);
            return ResponseEntity.ok(anioCursada);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/verificar")
    public ResponseEntity<Boolean> verificarInscripcion(
            @RequestParam String dniAlumno,
            @RequestParam String carreraId) {
        boolean existe = carreraService.estaInscripto(dniAlumno, carreraId);
        return ResponseEntity.ok(existe);
    }
}
