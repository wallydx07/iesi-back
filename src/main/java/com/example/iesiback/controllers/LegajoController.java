package com.example.iesiback.controllers;
import com.example.iesiback.entities.Alumno;
import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.services.AlumnoService;
import com.example.iesiback.services.LegajoService;
import com.example.iesiback.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/legajos")
public class LegajoController {

    private final UserService userService;
    @Autowired
    private LegajoService LegajoService;

    private final AlumnoService alumnoService;
    @Autowired
    public LegajoController(AlumnoService alumnoService, UserService userService) {
        this.userService = userService;
        this.alumnoService = alumnoService;
    }

    @GetMapping
    public List<Legajo> obtenerLegajos() {
        return LegajoService.obtenerLegajos();
    }


    @PutMapping("/{id}")
    public ResponseEntity<Legajo> updateLegajo(@RequestBody Legajo legajo, @RequestParam String dni) {
        Legajo UpdateLegajo=LegajoService.findLegajoById(legajo.getLegajoId());
        UpdateLegajo.setLegajoFotocopiaDni(legajo.getLegajoFotocopiaDni());
        UpdateLegajo.setLegajoCertificadoNacimiento(legajo.getLegajoCertificadoNacimiento());
        UpdateLegajo.setLegajoFotocopiaTitulo(legajo.getLegajoFotocopiaTitulo());
        UpdateLegajo.setLegajoCarnetSanitario(legajo.getLegajoCarnetSanitario());
        UpdateLegajo.setLegajoAval(legajo.getLegajoAval());
        UpdateLegajo.setLegajoEstado(legajo.getLegajoEstado());
        //UpdateLegajo.setLegajoFoto(legajo.getLegajoFoto());
        //UpdateLegajo.setLegajoCarpetaColgante(legajo.getLegajoCarpetaColgante());
        Legajo updatedLegajo = LegajoService.updateLegajo(UpdateLegajo);
        return new ResponseEntity<>(updatedLegajo, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Legajo> getLegajoById(@PathVariable String id) {
        return LegajoService.findById(id)
                .map(legajo -> ResponseEntity.ok().body(legajo))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }




    @PostMapping
    public ResponseEntity<?> crearLegajo(@RequestBody Map<String, Object> request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // 🔹 Imprimir los datos recibidos
            System.out.println("🔹 JSON recibido: " + request);

            Legajo legajo = objectMapper.convertValue(request.get("legajo"), Legajo.class);
            Carrera carrera = objectMapper.convertValue(request.get("carrera"), Carrera.class);
            String alumnoDni = (String) request.get("alumnoDni");

            System.out.println("📌 Alumno DNI: " + alumnoDni);
            System.out.println("📌 Carrera: " + carrera);
            System.out.println("📌 Legajo: " + legajo);

            Optional<Alumno> alumnoOpt = alumnoService.findById(alumnoDni);
            if (alumnoOpt.isPresent()) {
                legajo.setLegajoAlumnoDni(alumnoOpt.get());
                legajo.setUsuario(userService.getAuthenticatedUser().get().getUserApellido());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Alumno con DNI " + alumnoDni + " no encontrado", "status", 404));
            }

            Legajo nuevoLegajo = LegajoService.guardarLegajo(legajo, carrera);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLegajo);

        } catch (Exception e) {
            e.printStackTrace(); // 🔴 Muestra el error en la consola del backend
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error interno al procesar la solicitud", "error", e.getMessage()));
        }
    }

}
