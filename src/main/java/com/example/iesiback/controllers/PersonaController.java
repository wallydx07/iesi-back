package com.example.iesiback.controllers;
import com.example.iesiback.dto.PromedioEgresadoDTO;
import com.example.iesiback.entities.Persona;
import com.example.iesiback.services.PersonaService;
import com.example.iesiback.services.PersonalService;
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
public class PersonaController {

    private final PersonaService alumnoService;
    private final PersonalService personalService;

    @Autowired
    public PersonaController(PersonaService alumnoService, PersonalService personalService) {
        this.alumnoService = alumnoService;
        this.personalService = personalService;
    }
    @GetMapping
    public List<Persona> obtenerAlumno() {
        return alumnoService.obtenerAlumnos();
    }

    @PostMapping
    public ResponseEntity<Persona> createAlumno(@RequestBody Persona persona) {
        Persona nuevoPersona = alumnoService.createAlumno(persona);
        return ResponseEntity.ok(nuevoPersona);
    }


    @GetMapping("/buscar")
    public List<String> buscarAlumnos(@RequestParam String apellido) {
        return alumnoService.buscarPorDniApellidoNombre(apellido);
    }


    @GetMapping("/buscarPersonal")
    public List<String> buscarPersonalPorDniApellidoNombre(@RequestParam String apellido) {
        return alumnoService.buscarPersonalPorDniApellidoNombre(apellido);
    }



    @GetMapping("/buscarPorApellidoYCarrera")
    public List<String> buscarPorApellidoYCarrera(
            @RequestParam String busqueda,
            @RequestParam String carreraNombre) {
        return alumnoService.buscarPorApellidoYCarrera(busqueda, carreraNombre);
    }

    @GetMapping("/buscar/dni")
    public List<Persona> buscarAlumnosDni(@RequestParam String dni) {
        return alumnoService.buscarPorDni(dni);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Persona> actualizarAlumno(@PathVariable String id, @RequestBody Persona persona) {
        Optional<Persona> alumnoExistente = alumnoService.findById(id);
        if (alumnoExistente.isPresent()) {
            persona.setPersonaDni(Long.valueOf(id)); // Asegura que se actualiza el registro correcto
            Persona personaActualizado = alumnoService.save(persona);
            return ResponseEntity.ok(personaActualizado);
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
    public ResponseEntity<Persona> obtenerAlumnoPorLegajoId(@PathVariable String legajoId) {
        Persona persona = alumnoService.obtenerAlumnoPorLegajoId(legajoId);
        if (persona != null) {
            return ResponseEntity.ok(persona);  // Devuelve el alumno encontrado
        } else {
            return ResponseEntity.notFound().build();  // Devuelve un 404 si no se encuentra el alumno
        }
    }

    @GetMapping("personalForbbiden/{id}")
    public ResponseEntity<?> getAlumnoByIdForbbiden(@PathVariable String id) {
        System.out.println("➡️ Buscando alumno con DNI: " + id);

        if (this.personalService.existsByDni(id)) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 403);
            response.put("message", "No se permite al personal inscribirse a las carreras");

            System.out.println("⛔ Personal detectado, devolviendo 403: " + response);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .headers(headers)
                    .body(response);
        }

        var alumnoOpt = alumnoService.findById(id);

        if (alumnoOpt.isPresent()) {
            System.out.println("✅ Alumno encontrado: " + alumnoOpt.get());
            return ResponseEntity.ok().body(alumnoOpt.get());
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 404);
            response.put("message", "Alumno no encontrado");

            System.out.println("❌ Alumno no encontrado, devolviendo 404: " + response);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(headers)
                    .body(response);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getAlumnoById(@PathVariable String id) {
        var alumnoOpt = alumnoService.findById(id);
        if (alumnoOpt.isPresent()) {
            return ResponseEntity.ok().body(alumnoOpt.get());
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 404);
            response.put("message", "Alumno no encontrado");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(headers)
                    .body(response);
        }
    }




    @PutMapping("/cambio-dni")
    public ResponseEntity<String> cambioDni(
            @RequestParam String dniActual,
            @RequestParam Long dniNuevo) {

        alumnoService.cambioDNI(dniActual, dniNuevo);
        return ResponseEntity.ok("DNI actualizado correctamente");
    }

    @GetMapping("/promedios/{year}")
    public ResponseEntity<List<PromedioEgresadoDTO>> obtenerEgresados(
            @PathVariable Integer year
    ) {
        List<PromedioEgresadoDTO> egresados =
                alumnoService.obtenerEgresados(year);

        return ResponseEntity.ok(egresados);
    }

//    @GetMapping("/api/AlumnoExamenDTO")
//    public List<AlumnoExamenDTO> AlumnoExamenDTO(@RequestParam String apellido, @RequestParam String carreraNombre) {
//        return alumnoService.AlumnoExamenDTO(apellido, carreraNombre);
//    }
}