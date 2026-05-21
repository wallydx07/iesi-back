package com.example.iesiback.controllers;

import com.example.iesiback.dto.*;
import com.example.iesiback.entities.Cursada;
import com.example.iesiback.entities.Nota;
import com.example.iesiback.enums.EstadoCondicion;
import com.example.iesiback.services.CorrelativaService;
import com.example.iesiback.services.NotaService;
import com.example.iesiback.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.example.iesiback.services.LegajoServiceImpl.log;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/notas")
public class NotaController {

    private final UserService userService;
    private final CorrelativaService correlativaService;
    @Autowired
    private NotaService notaService;

    public NotaController(UserService userService, CorrelativaService correlativaService) {
        this.userService=userService;

        this.correlativaService = correlativaService;
    }

    @GetMapping
    public List<Nota> obtenerNotas() {
        return notaService.obtenerNotas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Nota> getNotaById(@PathVariable("id") Long notaId) {
        Nota nota = notaService.obtenerNotaPorId(notaId);
        if(nota == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(nota, HttpStatus.OK);
    }

    @GetMapping("/obtenerTodasNotasPorLegajo/{legajoId}")
    public ResponseEntity<List<NotaMateriaDTO>> obtenerTodasNotasPorLegajo(@PathVariable String legajoId) {
        return ResponseEntity.ok(notaService.obtenerTodasNotasPorLegajo(legajoId,EstadoCondicion.EXAMEN));
    }

//    @GetMapping("/obtenerTodasNotasPorMateria")
//    public ResponseEntity<List<NotaCursadaDTO>> obtenerTodasNotasPorMateria(
//            @RequestParam String carreraId,
//            @RequestParam String materiaId,
//            @RequestParam(required = false) Boolean cursadaInscripto
//    ) {
//        List<NotaCursadaDTO> notas = notaService.findNotasByCarreraAndMateria(carreraId, materiaId, cursadaInscripto);
//        return ResponseEntity.ok(notas);
//    }

    @GetMapping("/obtenerTodasNotasPorMateria")
    public ResponseEntity<?> obtenerTodasNotasPorMateria(
            @RequestParam String carreraId,
            @RequestParam String materiaId,
            @RequestParam String division,
            @RequestParam(required = false) Boolean cursadaInscripto) {
        try {
            List<NotaCursadaConEstadoDTO> notas = notaService.findNotasByCarreraAndMateria(
                    carreraId, materiaId, division, cursadaInscripto != null ? cursadaInscripto : true
            );
            return ResponseEntity.ok(notas);
        } catch (Exception e) {
            log.error("Error al buscar notas. carreraId={} materiaId={} division={}: {}",
                    carreraId, materiaId, division, e.getMessage(), e);
            return ResponseEntity.status(500).body("Ocurrió un error al buscar las notas: " + e.getMessage());
        }
    }



    @GetMapping("/obtenerNotasNoAprobadasPorLegajo")
    public ResponseEntity<List<NotaMateriaDTO>> obtenerNotasNoAprobadasPorLegajo(
            @RequestParam String legajoId
    ) {
        List<NotaMateriaDTO> examenes = notaService.obtenerNotasNoAprobadasCursadas(legajoId);
        return ResponseEntity.ok(examenes);
    }


    @GetMapping("/obtenerTodasNotasPorExamen")
    public ResponseEntity<List<NotaExamenDTO>> obtenerTodasNotasPorExamen(
            @RequestParam Long  cursadaExamenId,
            @RequestParam(required = false) Boolean examenInscripto
    ) {
        List<NotaExamenDTO> examenes = notaService.findExamenesByCursadaExamenIdMateriaCarrera(cursadaExamenId, examenInscripto);
        return ResponseEntity.ok(examenes);
    }

    @PostMapping("/crear")
    public ResponseEntity<Nota> crearNota(@RequestBody Nota nota) {
        Nota nuevaNotaAux = this.notaService.obtenerNotaPorId(nota.getNotaId());
//        String notaFecha = nota.getNotaFechaNota();
//        if (notaFecha != null && !notaFecha.isEmpty()) {
//            LocalDate fecha = LocalDate.parse(notaFecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//            String fechaFormateada = fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//            nota.setNotaFechaNota(fechaFormateada);
//        }
//        nota.setNotaUsuario(userService.getAuthenticatedUser().get().getUserApellido());

        if (userService.getAuthenticatedUser().get().getRoles().get(0).getRoleNombre().equals("ROLE_ADMIN")) {
            System.out.println("ES administrador");
            //     nota.setNotaUsuario(userService.getAuthenticatedUser().get().getRoles().get(0).equals("ROLE_ADMIN"));
        } else {
            System.out.println("no es administrador");
            nota.setNotaUsuario(userService.getAuthenticatedUser().get().getUserApellido());

        }


        nota.setCursada(nuevaNotaAux.getCursada());
        Nota nuevaNota = notaService.guardarNota(nota);
        return ResponseEntity.ok(nuevaNota);
    }

    @DeleteMapping("/eliminarNotaIndividual/{id}")
    public ResponseEntity<String> eliminarNotaIndividual(@PathVariable Long id) {
        try {
            // Llamamos al servicio que solo elimina la nota
            notaService.eliminarNotaIndividual(id);
            String msg = "Nota eliminada correctamente.";

            // Imprimir en consola
            System.out.println("Respuesta API: " + msg);

            return ResponseEntity.ok(msg);
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();

            // Imprimir en consola
            System.out.println("Error al eliminar nota: " + errorMsg);

            return ResponseEntity.badRequest().body(errorMsg);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarNota(@PathVariable Long id) {
        try {
            notaService.eliminarNota(id);
            return ResponseEntity.ok("Nota y Cursada eliminadas correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/obtenerNotasAnalitico/{legajoId}")
    public ResponseEntity<List<NotaMateriaDTO>> obtenerNotasAnalitico(
            @PathVariable String legajoId) {
        return ResponseEntity.ok(notaService.obtenerTodasNotasPorLegajoAnalitico(legajoId));
    }

    @GetMapping("/{notaId}/cursada")
    public ResponseEntity<Cursada> obtenerCursadaPorNota(@PathVariable Long notaId) {
        Cursada cursada = notaService.obtenerCursadaPorNotaId(notaId);
        if (cursada == null) {
            return ResponseEntity.notFound().build();
        }

        CorrelativaService.Veredicto veredicto = correlativaService.evaluar(
                cursada.getLegajo().getLegajoId(),
                cursada.getMateriaCarrera().getMateria().getMateriaOrden(),
                EstadoCondicion.CURSADA
        );

        cursada.setStatus(veredicto.toLegacy().getStatus());
        return ResponseEntity.ok(cursada);
    }

    @PostMapping("/evaluar/{legajoId}/{materiaOrden}/{condicion}")
    public ResponseEntity<EvaluacionCorrelativaResponse> evaluarMateriaIndividual(
            @PathVariable String legajoId,
            @PathVariable Integer materiaOrden,
            @PathVariable EstadoCondicion condicion) {

        EvaluacionCorrelativaResponse response = correlativaService
                .evaluar(legajoId, materiaOrden, condicion)
                .toLegacy();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/evaluarCorrelativaNotaId/{notaId}/{condicion}")
    public ResponseEntity<EvaluacionCorrelativaResponse> evaluarCorrelativaNotaId(
            @PathVariable Long notaId,
            @PathVariable EstadoCondicion condicion) {

        EvaluacionCorrelativaResponse response = correlativaService
                .evaluarPorNota(notaId, condicion)
                .toLegacy();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/permitir-edicion")
    public ResponseEntity<String> permitirEdicionMateria(
            @RequestParam String carreraId,
            @RequestParam String materiaId,
            @RequestParam boolean editable,
            @RequestParam String division

    ) {
        try {
            notaService.permitirEdicionMateria(carreraId, materiaId, editable, division);
            return ResponseEntity.ok("Permiso de edición actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el permiso de edición: " + e.getMessage());
        }
    }

    @GetMapping("/carrera/{carreraId}")
    public List<AlumnoCursadaMateriaNotaDTO> getAlumnosPorCarrera(@PathVariable String carreraId) {
        return notaService.getAlumnosPorCarrera(carreraId);
    }

//    @PostMapping("/notas/importar")
//    public void importarNotas(@RequestBody List<NotaImportDTO> notas) {
//        notaService.importarNotas(notas);
//    }


    @GetMapping("/reinscripciones")
    public List<ProcesadoReinscripcionMateriaDTO> obtenerReinscripciones(
            @RequestParam("cicloLectivo") Integer cicloLectivo,
            @RequestParam("legajoId") String legajoId,
            @RequestParam("division") String division
    ) {
        return notaService.obtenerReinscripciones(cicloLectivo,legajoId, division);
    }


}

