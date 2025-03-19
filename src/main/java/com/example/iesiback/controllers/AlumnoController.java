package com.example.iesiback.controllers;
import com.example.iesiback.entities.Alumno;
import com.example.iesiback.services.AlumnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/alumnos")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoService;

    @GetMapping
    public List<Alumno> obtenerAlumno() {
        return alumnoService.obtenerAlumnos();
    }

    @PostMapping
    public ResponseEntity<Alumno> createAlumno(@RequestBody Alumno alumno) {
        Alumno nuevoAlumno = alumnoService.createAlumno(alumno);
        return ResponseEntity.ok(nuevoAlumno);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAlumnoById(@PathVariable String id) {
        var alumnoOpt = alumnoService.findById(id);

        if (alumnoOpt.isPresent()) {
            return ResponseEntity.ok().body(alumnoOpt.get());
        } else {
            // ✅ Crear JSON de respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("status", 404);
            response.put("message", "Alumno no encontrado");

            // ✅ Configurar las cabeceras correctamente
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(headers) // ✅ Agregar las cabeceras manualmente
                    .body(response);
        }
    }

    @GetMapping("/buscar")
    public List<String> buscarAlumnos(@RequestParam String apellido) {
        return alumnoService.buscarAlumnosPorApellido(apellido);
    }

    @GetMapping("/buscar/dni")
    public List<Alumno> buscarAlumnosDni(@RequestParam String dni) {
        return alumnoService.buscarPorDni(dni);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alumno> actualizarAlumno(@PathVariable String id, @RequestBody Alumno alumno) {
        Optional<Alumno> alumnoExistente = alumnoService.findById(id);
        if (alumnoExistente.isPresent()) {
            alumno.setAlumnoDni(Long.valueOf(id)); // Asegura que se actualiza el registro correcto
            Alumno alumnoActualizado = alumnoService.save(alumno);
            return ResponseEntity.ok(alumnoActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAlumno(@PathVariable String id) {
        boolean eliminado = alumnoService.delete(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/legajo/{legajoId}")
    public ResponseEntity<Alumno> obtenerAlumnoPorLegajoId(@PathVariable String legajoId) {
        Alumno alumno = alumnoService.obtenerAlumnoPorLegajoId(legajoId);
        if (alumno != null) {
            return ResponseEntity.ok(alumno);  // Devuelve el alumno encontrado
        } else {
            return ResponseEntity.notFound().build();  // Devuelve un 404 si no se encuentra el alumno
        }
    }
}