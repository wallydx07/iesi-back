package com.example.iesiback.controllers;

import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.entities.Materia;
import com.example.iesiback.services.*;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/certificado")
public class CertificadoController {

    @Autowired
    private CertificadoService certificadoService;
    private final MateriaService materiaService;
    private final CarreraService carreraService;
    private final CursadaExamenService cursadaExamenService;



    public CertificadoController(MateriaService materiaService,
                                 CarreraService carreraService,
                                 CursadaExamenService cursadaExamenService) {
        this.materiaService = materiaService;
        this.carreraService = carreraService;
        this.cursadaExamenService = cursadaExamenService;
    }

    @GetMapping("/estudianteregular")
    public ResponseEntity<ByteArrayResource> generarFicha(
            @RequestParam String alumnoId,
            @RequestParam String legajoId,
            @RequestParam String autoridades,
            @RequestParam String curso) {

        try {
            System.out.println("Solictando certificado");
            PDDocument document = certificadoService.generaRegular(alumnoId, legajoId, autoridades, curso);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            document.close();
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificado_regular.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/analitico")
    public ResponseEntity<ByteArrayResource> generaAnalitico(
            @RequestParam String legajoId,
            @RequestParam String accion,
            @RequestParam String autoridades) {

        try {
            // Se obtiene el PDDocument desde el servicio
            PDDocument document = certificadoService.generaAnalitico(legajoId, accion, autoridades);
            // Convertir PDDocument a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
           // document.close();
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificado_regular.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/generarActa")
    public ResponseEntity<ByteArrayResource> generarActaExamen(
            @RequestParam String materiaId,
            @RequestParam String carrera,
            @RequestParam Integer cursadaExamenId,
            @RequestParam String modalidad) {

        try {
            Materia materia=this.materiaService.findMateriaById(materiaId);
           // Carrera carrera=this.carreraService.findCarreraById(carreraId);
            CursadaExamen cursadaExamen=this.cursadaExamenService.obtenerPorId(cursadaExamenId).get();
            PDDocument document = certificadoService.generaExamen(materia, carrera, cursadaExamen, modalidad);
            // Convertir PDDocument a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            // document.close();
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificado_regular.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @GetMapping("/titulotramite")
    public ResponseEntity<ByteArrayResource> generatituloTramite(
            @RequestParam String carreraId,
            @RequestParam String alumnoDni,
            @RequestParam String autoridades) {

        try {
            PDDocument document = certificadoService.generaTramite(carreraId, alumnoDni, autoridades);
            // Convertir PDDocument a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            // document.close();
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filenametituloTramite.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/asistenciaclases")
    public ResponseEntity<ByteArrayResource> generAsistenciaClases(
            @RequestParam String alumnoDNI,
            @RequestParam String legajoId,
            @RequestParam String autoridades,
            @RequestParam String curso,
            @RequestParam String entrada,
            @RequestParam String salida,
            @RequestParam String fecT,
            @RequestParam String accion

    ) {
        try {
            PDDocument document = certificadoService.generaCertificadoAsistencia(alumnoDNI,legajoId,autoridades,curso,entrada,salida,fecT,accion);
            // Convertir PDDocument a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            // document.close();
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; generaCertificadoAsistencia.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/generaFichaActualizacion")
    public ResponseEntity<ByteArrayResource> generaFichaActualizacion(
            @RequestParam String legajoId) {

        try {
            PDDocument document = certificadoService.generaFichaActualizacion(legajoId);
            // Convertir PDDocument a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            // document.close();
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; generaFichaActualizacion.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/generaPlanillaTutores")
    public ResponseEntity<ByteArrayResource> generaPlanillaTutores(
            @RequestParam String carreraId,
            @RequestParam String estado,
            @RequestParam String apellido) {

        try {
          PDDocument document = certificadoService.generaPlanillaTutores(carreraId,estado,apellido);
            // Convertir PDDocument a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            // document.close();
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=planillaTutores-certificado_regular.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/generaPlanillaAsitencia")
    public ResponseEntity<ByteArrayResource> generaPlanillaAsitencia(
            @RequestParam Long id) {
        try {
            PDDocument document = certificadoService.generaPlanillaAsistencia(id);
            // Convertir PDDocument a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            // document.close();
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=planillaAsitencia.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/planillaSeguimiento")
    public ResponseEntity<ByteArrayResource> planillaSeguimiento(
            @RequestParam String materiaId,
            @RequestParam String carreraId,
            @RequestParam Boolean cursadaInscripto) {
        try {
            System.out.println("Materia ID: " + materiaId);
            System.out.println("carreraId: " + carreraId);
            System.out.println("cursadaInscripto: " + cursadaInscripto);

            // 🔴 POSIBLE ERROR 1: certificadoService.generaPlanilla() puede retornar null o lanzar una excepción
            PDDocument document = certificadoService.generaPlanilla(carreraId, materiaId, cursadaInscripto);
            if (document == null) {
                // Buen control defensivo
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // 🔴 POSIBLE ERROR 2: IOException al guardar el documento
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);  // <-- Esto puede lanzar IOException

            // 🔴 POSIBLE ERROR 3: falta cerrar el documento (fuga de recursos)
            document.close();

            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=planillaAsitencia.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace(); // ✅ Recomendado para ver el error real en consola/log
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            e.printStackTrace(); // ✅ Agregado para capturar otros errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/credencialEstudiantil")
    public ResponseEntity<byte[]> generarCredencial(@RequestBody String legajoId) {
        System.out.println("LEG: " + legajoId); // Debug básico

        try {
            byte[] pdf = certificadoService.generarCredencialEstudiantil(legajoId);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=credencial.pdf")
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(("Error generando credencial: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/planilla-excel")
    public ResponseEntity<byte[]> exportarPlanillaExcel(
            @RequestParam String carreraId,
            @RequestParam String materiaId,
            @RequestParam(defaultValue = "true") Boolean inscripto) {

        ByteArrayInputStream in = certificadoService.generaPlanillaExcel(carreraId, materiaId, inscripto);
        byte[] contenido;
        contenido = in.readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "planilla.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return new ResponseEntity<>(contenido, headers, HttpStatus.OK);
    }

    //=============================================0

    @GetMapping("/generaAsistenciaParcial")
    public ResponseEntity<ByteArrayResource> generaAsistenciaParcial(
            @RequestParam String legajoId,
            @RequestParam String autoridades,
            @RequestParam String curso,
            @RequestParam String entrada,
            @RequestParam String salida,
            @RequestParam String fecha,
            @RequestParam String accion,
            @RequestParam String materia

    ) {
        try {
            PDDocument document = certificadoService.generaAsistenciaParcial(legajoId,autoridades,curso,fecha,accion,entrada,salida,materia);
            // Convertir PDDocument a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            // document.close();
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; generaCertificadoAsistencia.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    @GetMapping("/generaAsistenciaSalidaCampo")
    public ResponseEntity<ByteArrayResource> generaAsistenciaSalidaCampo(
            @RequestParam String legajoId,
            @RequestParam String autoridades,
            @RequestParam String curso,
            @RequestParam String fecha,
            @RequestParam String accion,
            @RequestParam String lugar
    ) {
        try {
            PDDocument document = certificadoService.generaAsistenciaSalidaCampo(legajoId,autoridades,fecha,curso,accion,lugar);
            // Convertir PDDocument a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            // document.close();
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; generaCertificadoAsistencia.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    @GetMapping("/generaAsistenciaExamenFinal")
    public ResponseEntity<ByteArrayResource> generaAsistenciaExamenFinal(
            @RequestParam String legajoId,
            @RequestParam String autoridades,
            @RequestParam String curso,
            @RequestParam String entrada,
            @RequestParam String salida,
            @RequestParam String fecha,
            @RequestParam String accion,
            @RequestParam String materia
    ) {
        try {
            PDDocument document = certificadoService.generaAsistenciaExamenFinal(legajoId,autoridades,fecha,curso,entrada, salida,accion,materia);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; generaCertificadoAsistencia.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    @GetMapping("/reporte-mensual")
    public ResponseEntity<ByteArrayResource> descargarReportePorMes(@RequestParam String mes) {
        try (PDDocument document = certificadoService.crearPDFPorMes(mes);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            document.save(baos);
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=generaCertificadoAsistencia.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/reporte-rango")
    public ResponseEntity<ByteArrayResource> descargarReportePorFechas(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {

        try (PDDocument document = certificadoService.crearPDFPorFecha(fechaDesde, fechaHasta);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            document.save(baos);
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=generaCertificadoAsistencia.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/generaPlanillaSeguimiento")
    public ResponseEntity<ByteArrayResource> generaPlanillaSeguimiento(
            @RequestParam String materiaId,
            @RequestParam String carreraId,
            @RequestParam Boolean cursadaInscripto) {
        try {
            System.out.println("Materia ID: " + materiaId);
            System.out.println("carreraId: " + carreraId);
            System.out.println("cursadaInscripto: " + cursadaInscripto);

            // 🔴 POSIBLE ERROR 1: certificadoService.generaPlanilla() puede retornar null o lanzar una excepción
            PDDocument document = certificadoService.generaPlanillaSeguimiento(carreraId, materiaId, cursadaInscripto);
            if (document == null) {
                // Buen control defensivo
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // 🔴 POSIBLE ERROR 2: IOException al guardar el documento
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);  // <-- Esto puede lanzar IOException

            // 🔴 POSIBLE ERROR 3: falta cerrar el documento (fuga de recursos)
            document.close();

            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=planillaAsitencia.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace(); // ✅ Recomendado para ver el error real en consola/log
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            e.printStackTrace(); // ✅ Agregado para capturar otros errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
