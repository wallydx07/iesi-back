package com.example.iesiback.controllers;

import com.example.iesiback.dto.MateriaDTO;
import com.example.iesiback.dto.ProcesadoReinscripcionMateriaDTO;
import com.example.iesiback.entities.Materia;
import com.example.iesiback.services.MateriaCarreraService;
import com.example.iesiback.services.MateriaService;
import com.example.iesiback.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/materias")
@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
public class MateriaController {

    private final UserService userService;

    @Autowired
    private MateriaCarreraService materiaCarreraService;

    @Autowired
    private MateriaService materiaService;

    public MateriaController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<Materia> obtenerTodasMaterias() {
        return materiaService.obtenerTodasMaterias();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Materia> obtenerMateriaPorId(@PathVariable String id) {
        Optional<Materia> materia = materiaService.obtenerMateriaPorId(id);
        return materia.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Materia> crearMateria(@RequestBody Materia materia) {
        Materia nuevaMateria = materiaService.guardarMateria(materia);
        return ResponseEntity.ok(nuevaMateria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Materia> actualizarMateria(@PathVariable String id, @RequestBody Materia materia) {
        Materia materiaActualizada = materiaService.actualizarMateria(id, materia);
        if (materiaActualizada != null) {
            return ResponseEntity.ok(materiaActualizada);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMateria(@PathVariable String id) {
        materiaService.eliminarMateria(id);
        return ResponseEntity.noContent().build();
    }

     @GetMapping("/carrera/{carreraId}")
        public List<MateriaDTO> obtenerMateriasPorCarrera(@PathVariable String carreraId) {
            return materiaService.obtenerMateriasPorCarrera(carreraId);
        }

    @GetMapping("/reinscripciones")
    public List<ProcesadoReinscripcionMateriaDTO> obtenerReinscripciones(
            @RequestParam("cicloLectivo") Integer cicloLectivo,
            //@RequestParam("carreraNombre") String carreraNombre,
            @RequestParam("legajoId") String legajoId
    ) {
        //return materiaService.obtenerReinscripciones(cicloLectivo, carreraNombre,legajoId);
        return materiaService.obtenerReinscripciones(cicloLectivo,legajoId);
    }

@GetMapping("/carrera/user/{carreraId}")
public ResponseEntity<List<MateriaDTO>> getMateriasPorCarrerasyUsuario(@PathVariable String carreraId) {
    System.out.println("Carrera recibida: " + carreraId);
    List<MateriaDTO> materias;
    String userRol = String.valueOf(userService.getAuthenticatedUser().get().getRoles().get(0).getRoleNombre());
    Long userId = Long.valueOf(userService.getAuthenticatedUser().get().getUsername());
    System.out.println("Usuario autenticado: ID=" + userId + ", Rol=" + userRol);
    if ("ROLE_ADMIN".equalsIgnoreCase(userRol) || "ROLE_PERSONAL".equalsIgnoreCase(userRol)) {
        materias = materiaService.obtenerMateriasPorCarrera(carreraId);
    } else if ("ROLE_TUTOR".equalsIgnoreCase(userRol)) {
        materias = materiaService.obtenerMateriasPorCarrera(carreraId);
    } else if ("ROLE_DOCENTE".equalsIgnoreCase(userRol)) {
        materias = materiaCarreraService.obtenerMateriasPorCarreraYDocente(userId, carreraId);
        if (materias.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    System.out.println("Materias obtenidas: " + materias.size());
    return ResponseEntity.ok(materias);
}

}
