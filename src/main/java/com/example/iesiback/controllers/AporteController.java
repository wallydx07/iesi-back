package com.example.iesiback.controllers;

import com.example.iesiback.dto.AporteDTO;
import com.example.iesiback.entities.Aporte;
import com.example.iesiback.repositories.AporteRepository;
import com.example.iesiback.repositories.HtmlService;
import com.example.iesiback.services.AporteService;
import com.example.iesiback.services.UserService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/aportes")
public class AporteController {

    private final HtmlService htmlService;
    private final AporteService aporteService;
    private final UserService userService;

    public AporteController(HtmlService htmlService, AporteService aporteService,UserService userService) {
        this.htmlService = htmlService;
        this.aporteService = aporteService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<AporteDTO>> getAportes() {
        System.out.println("Llamada a getAportes() iniciada");

        List<AporteDTO> aportes = aporteService.getAportesConDatos();

        System.out.println("Cantidad de aportes obtenidos: " + aportes.size());

        return ResponseEntity.ok(aportes);
    }


    @PostMapping("/recibo")
    public ResponseEntity<byte[]> generarRecibo(@RequestBody Map<String, String> datos) {
        try {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("nombre", datos.get("nombre"));
            templateData.put("curso", datos.get("carrera"));
            templateData.put("legajoId", datos.get("legajoId"));
            templateData.put("fecha_pago", datos.get("fecha"));
            templateData.put("monto", datos.get("monto"));
            templateData.put("usuario", userService.getAuthenticatedUser().get().getUserApellido());
            templateData.put("id_pago", datos.get("idAporte"));


            // Generar HTML
            String html = htmlService.procesarHtml("recibo", templateData);
            System.out.println("HTML Generado: \n" + html); // Debug para revisar el HTML

            // Convertir HTML a PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ConverterProperties properties = new ConverterProperties();
            HtmlConverter.convertToPdf(html, outputStream, properties);

            byte[] pdfBytes = outputStream.toByteArray();
            outputStream.close();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=recibo.pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace(); // Imprimir error en consola para depuración
            return ResponseEntity.internalServerError().body(("Error en la generación del PDF: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping
    public ResponseEntity<Aporte> crearAporte(@RequestBody Aporte aporte) {
        if (aporte.getAporteFecha() == null) {
            aporte.setAporteFecha(LocalDate.now());
        }
        aporte.setUsuario(userService.getAuthenticatedUser().get().getUserApellido());
        System.out.println(aporte.getUsuario()+"Usario agregado");
        Aporte nuevoAporte = aporteService.save(aporte);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAporte);
    }

    @GetMapping("/legajo/{legajoId}")
    public List<Aporte> obtenerAportesPorLegajo(@PathVariable String legajoId) {
        return aporteService.obtenerAportesPorLegajoId(legajoId);
    }

    @GetMapping("/{id}")
    public Aporte findAporteById(@PathVariable("id") Integer aporteId) {
        return aporteService.findAporteById(aporteId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aporte> actualizarAporte(@PathVariable Integer id, @RequestBody Aporte aporte) {
        Aporte aporteExistenteOpt = aporteService.findAporteById(id);
        // Actualizá solo los campos que pueden cambiar
        aporteExistenteOpt.setAporteNroRecibo(aporte.getAporteNroRecibo());
        aporteExistenteOpt.setAporteTalonarioRecibo(aporte.getAporteTalonarioRecibo());
        aporteExistenteOpt.setAporteMonto(aporte.getAporteMonto());
        aporteExistenteOpt.setAporteObs(aporte.getAporteObs());
        aporteExistenteOpt.setValidado(aporte.getValidado());
        aporteExistenteOpt.setAporteFecha(aporte.getAporteFecha());
        aporteExistenteOpt.setUsuario(userService.getAuthenticatedUser().get().getUserApellido());
        Aporte actualizado = aporteService.save(aporteExistenteOpt);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAporte(@PathVariable Integer id) {
        Aporte aporteExistente = aporteService.findAporteById(id);

        if (aporteExistente == null) {
            return ResponseEntity.notFound().build();
        }

        aporteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/anio-actual/{legajoId}")
    public ResponseEntity<List<Aporte>> obtenerAportesDelAnioActual(@PathVariable String legajoId) {
        List<Aporte> aportes = aporteService.obtenerAportesYearFiltrado(legajoId);
        return ResponseEntity.ok(aportes);
    }





}
