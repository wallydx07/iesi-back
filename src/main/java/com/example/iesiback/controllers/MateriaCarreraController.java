package com.example.iesiback.controllers;

import com.example.iesiback.dto.ActaCursadaDTO;
import com.example.iesiback.dto.CatedraDTO;
import com.example.iesiback.dto.MateriaCarreraDTO;
import com.example.iesiback.entities.MateriaCarrera;
import com.example.iesiback.entities.Turno;
import com.example.iesiback.services.MateriaCarreraService;
import com.example.iesiback.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/materiacarreras")
public class MateriaCarreraController {

    private final UserService userService;

    @Autowired
    private MateriaCarreraService materiaCarreraService;

    public MateriaCarreraController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public List<MateriaCarrera> obtenerMateriaCarreras() {
        return materiaCarreraService.obtenerMateriaCarreras();
    }


    @GetMapping("/{carreraId}/{materiaId}")
    public ResponseEntity<MateriaCarrera> obtenerMateriaCarrera(@PathVariable String carreraId,
                                                                @PathVariable String materiaId) {
        MateriaCarrera materiaCarrera = materiaCarreraService.obtenerMateriaCarrera(carreraId, materiaId);
        return ResponseEntity.ok(materiaCarrera);
    }


//    @GetMapping("/{id}")
//    public ResponseEntity<MateriaCarrera> obtenerMateriaCarreraPorId(@PathVariable Long id) {
//        Optional<MateriaCarrera> materiaCarrera = materiaCarreraService.obtenerMateriaCarreraPorId(id);
//        return materiaCarrera.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<MateriaCarrera> obtenerMateriaCarreraPorId(@PathVariable Long id) {
        Optional<MateriaCarrera> materiaCarreraOpt = materiaCarreraService.obtenerMateriaCarreraPorId(id);
        if (materiaCarreraOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        MateriaCarrera materiaCarrera = materiaCarreraOpt.get();
        String userRol = userService.getAuthenticatedUser().get().getRoles().get(0).getRoleNombre();
        Long userId = Long.valueOf(userService.getAuthenticatedUser().get().getUsername());
        // Solo los docentes deben ser validados
        if ("ROLE_DOCENTE".equalsIgnoreCase(userRol)) {
            // Si el docente NO es dueño de esta materia, prohibir el acceso
            if (!materiaCarrera.getFmcDocente().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.ok(materiaCarrera);
    }

    @GetMapping("/docente/{dni}/anio/{year}")
    public ResponseEntity<List<CatedraDTO>> obtenerCatedras(
            @PathVariable String dni,
            @PathVariable Integer year) {
        List<CatedraDTO> catedras = materiaCarreraService.obtenerCatedrasPorDocenteYAnio(dni, year);
        return ResponseEntity.ok(catedras);
    }

    @PostMapping
    public MateriaCarrera save(@RequestBody MateriaCarrera materiaCarrera) {
        return materiaCarreraService.save(materiaCarrera);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MateriaCarrera> update(@PathVariable Integer id, @RequestBody MateriaCarrera materiaCarrera) {
        return ResponseEntity.ok(materiaCarreraService.update(id, materiaCarrera));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        materiaCarreraService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cargarActaCursada")
    public ResponseEntity<List<ActaCursadaDTO>> obtenerActas() {
        List<ActaCursadaDTO> actas = materiaCarreraService.obtenerActas();
        return ResponseEntity.ok(actas);
    }

    @GetMapping("/cargarActaCursada/{anio}")
    public List<ActaCursadaDTO> obtenerActasPorAnio(@PathVariable int anio) {
        return materiaCarreraService.obtenerActasPorAnio(anio);
    }
}

