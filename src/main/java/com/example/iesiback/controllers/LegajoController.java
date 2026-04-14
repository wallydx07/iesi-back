package com.example.iesiback.controllers;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.services.PersonaService;
import com.example.iesiback.services.LegajoService;
import com.example.iesiback.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/legajos")
public class LegajoController {

    private final LegajoService legajoService;


    public LegajoController(LegajoService legajoService) {
        this.legajoService = legajoService;
    }

    @GetMapping
    public List<Legajo> obtenerLegajos() {
        return legajoService.obtenerLegajos();
    }


    @PutMapping("/{id}")
    public ResponseEntity<Legajo> updateLegajo(@RequestBody Legajo legajo, @RequestParam String dni) {
        Legajo UpdateLegajo= legajoService.findLegajoById(legajo.getLegajoId());
        UpdateLegajo.setLegajoFotocopiaDni(legajo.getLegajoFotocopiaDni());
        UpdateLegajo.setLegajoCertificadoNacimiento(legajo.getLegajoCertificadoNacimiento());
        UpdateLegajo.setLegajoFotocopiaTitulo(legajo.getLegajoFotocopiaTitulo());
        UpdateLegajo.setLegajoCarnetSanitario(legajo.getLegajoCarnetSanitario());
        UpdateLegajo.setLegajoAval(legajo.getLegajoAval());
        UpdateLegajo.setLegajoEstado(legajo.getLegajoEstado());
        UpdateLegajo.setLegajoFoto(legajo.getLegajoFoto());
        UpdateLegajo.setNotasCorregidas(legajo.isNotasCorregidas());
        UpdateLegajo.setLegajoComision(legajo.getLegajoComision());
        //UpdateLegajo.setLegajoCarpetaColgante(legajo.getLegajoCarpetaColgante());
        Legajo updatedLegajo = legajoService.updateLegajo(UpdateLegajo);
        return new ResponseEntity<>(updatedLegajo, HttpStatus.OK);
    }



    @GetMapping("/{id}")
    public ResponseEntity<Legajo> getLegajoById(@PathVariable String id) {
        try {
            return legajoService.findById(id)
                    .map(legajo -> ResponseEntity.ok().body(legajo))
                    .orElseGet(() -> {
                        System.out.println("Legajo con ID " + id + " no encontrado.");
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            System.out.println("Error al obtener el legajo con ID " + id + ": " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace completo del error
            return ResponseEntity.internalServerError().build();
        }
    }


//    @PostMapping
//    public ResponseEntity<?> crearLegajo(@RequestBody Map<String, Object> request) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            // 🔹 Imprimir los datos recibidos
//            System.out.println("🔹 JSON recibido: " + request);
//
//            Legajo legajo = objectMapper.convertValue(request.get("legajo"), Legajo.class);
//            Carrera carrera = objectMapper.convertValue(request.get("carrera"), Carrera.class);
//            String alumnoDni = (String) request.get("alumnoDni");
//            LocalDate hoy = LocalDate.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//            String fechaFormateada = hoy.format(formatter);
//            legajo.setLegajoFecha(fechaFormateada);
//            System.out.println("📌 Alumno DNI: " + alumnoDni);
//            System.out.println("📌 Carrera: " + carrera);
//            System.out.println("📌 Legajo: " + legajo);
//
//            Optional<Persona> alumnoOpt = alumnoService.findById(alumnoDni);
//            if (alumnoOpt.isPresent()) {
//                legajo.setLegajoPersonaDni(alumnoOpt.get());
//                legajo.setUsuario(userService.getAuthenticatedUser().get().getUserApellido());
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Map.of("message", "Alumno con DNI " + alumnoDni + " no encontrado", "status", 404));
//            }
//
//            Legajo nuevoLegajo = LegajoService.guardarLegajo(legajo, carrera);
//            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLegajo);
//
//        } catch (Exception e) {
//            e.printStackTrace(); // 🔴 Muestra el error en la consola del backend
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("message", "Error interno al procesar la solicitud", "error", e.getMessage()));
//        }
//    }

    @PostMapping
    public ResponseEntity<?> crearLegajo(@RequestBody Map<String, Object> request) {

        try {
            Legajo nuevoLegajo = legajoService.crearLegajoDesdeRequest(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLegajo);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error interno", "error", e.getMessage()));
        }
    }


    @GetMapping("/dni/{dni}")
    public ResponseEntity<List<Legajo>> getLegajosByDni(@PathVariable String dni) {

        List<Legajo> legajos = legajoService.findLegajosByDNI(dni);

        if (legajos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(legajos);
    }




}
