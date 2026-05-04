package com.example.iesiback.controllers;

import com.example.iesiback.entities.CursadaExamen;
import com.example.iesiback.entities.Materia;
import com.example.iesiback.services.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
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

@CrossOrigin(origins = "*")  // Permite solicitudes desde cualquier origen
@RestController
@RequestMapping("/api/certificado")
public class CertificadoController {

    private final CertificadoService certificadoService;
    private final MateriaService materiaService;
    private final CarreraService carreraService;
    private final CursadaExamenService cursadaExamenService;



    public CertificadoController(CertificadoService certificadoService, MateriaService materiaService,
                                 CarreraService carreraService,
                                 CursadaExamenService cursadaExamenService) {
        this.certificadoService = certificadoService;
        this.materiaService = materiaService;
        this.carreraService = carreraService;
        this.cursadaExamenService = cursadaExamenService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<String> enviarPermisoPorEmail(
            @RequestParam String libreta,
            @RequestParam String turno,
            @RequestParam String usuarioNombre,
            @RequestParam String destinatario
    ) {
        try {
            certificadoService.enviarPermisoPorEmail(libreta, turno, usuarioNombre, destinatario);
            return ResponseEntity.ok("Permiso enviado por correo"); // ✅ esto es correcto
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar permiso: " + e.getMessage()); // ✅ manejo de error
        }
    }



    @PostMapping("/generar")
    public ResponseEntity<ByteArrayResource> generarPermiso(
            @RequestParam String libreta,
            @RequestParam String turno,
            @RequestParam String usuarioNombre
    ) {
        try {
            PDDocument documento = certificadoService.generaPermiso(libreta, turno, usuarioNombre);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            documento.save(outputStream);
            documento.close();

            byte[] pdfBytes = outputStream.toByteArray();
            System.out.println("✅ PDF generado correctamente, tamaño: " + pdfBytes.length + " bytes");

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=permiso.pdf")
                    .body(resource);

        } catch (Exception e) {
            System.out.println("❌ Error al generar el PDF:");
            e.printStackTrace(); // Imprime el error en la consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/finalizacion")
    public ResponseEntity<ByteArrayResource> generarfinalizacion(
            @RequestParam String alumnoId,
            @RequestParam String legajoId,
            @RequestParam String autoridades) {

        try {
            System.out.println("Solictando certificado");
            PDDocument document = certificadoService.generaFinalizacionEstudios(legajoId, alumnoId, autoridades);
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
            System.out.println("la fecha es: "+cursadaExamen.getFecha());
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
            @RequestParam String legajoId,
            @RequestParam String autoridades) {

        try {
            PDDocument document = certificadoService.generaTramite(carreraId, legajoId, autoridades);
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
            @RequestParam String apellido,
            @RequestParam String comision) {

        try {
          PDDocument document = certificadoService.generaPlanillaTutores(carreraId,estado,apellido, comision);
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
//            @RequestParam String materiaId,
//            @RequestParam String carreraId,
//            @RequestParam Boolean cursadaInscripto
            @RequestParam Long materiaCarreraId
    ) {
        try {
            // 🔴 POSIBLE ERROR 1: certificadoService.generaPlanilla() puede retornar null o lanzar una excepción
//            PDDocument document = certificadoService.generaPlanilla(carreraId, materiaId, cursadaInscripto);
//
            PDDocument document = certificadoService.generaPlanilla(materiaCarreraId);


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
            @RequestParam String division,
            @RequestParam(defaultValue = "true") Boolean inscripto) {

        ByteArrayInputStream in = certificadoService.generaPlanillaExcel(carreraId, materiaId, inscripto, division);
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
            @RequestParam String fecha,
            @RequestParam String accion,
            @RequestParam String materia
    ) {
        try {
            PDDocument document = certificadoService.generaAsistenciaExamenFinal(legajoId,autoridades,curso,fecha, accion,materia);
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

    @GetMapping("/generaAsistenciaExamenFinalPersonal")
    public ResponseEntity<ByteArrayResource> generaAsistenciaExamenFinalPersonal(
            @RequestParam String dni,
            @RequestParam String autoridades,
            @RequestParam String carrera,
            @RequestParam String fecha,
            @RequestParam String accion,
            @RequestParam String materia
    ) {
        try {
            PDDocument document = certificadoService.generaAsistenciaExamenFinalDocente(dni,autoridades,carrera,fecha, accion,materia);
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
    public ResponseEntity<ByteArrayResource> descargarReportePorMes(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {

        try (PDDocument document = certificadoService.crearPDFPorMes(fechaDesde, fechaHasta);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            document.save(baos);
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=generaCertificadoAsistencia.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (Exception e) {
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

    @GetMapping("/reporte-rango-dni")
    public ResponseEntity<ByteArrayResource> descargarReportePorFechasDNI(
            @RequestParam("dni") String dni,
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {

        try (PDDocument document = certificadoService.crearPDFPorUsuario(dni, fechaDesde, fechaHasta);
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
            @RequestParam Long materiaCarreraId,
            @RequestParam Boolean cursadaInscripto) {
        try {
            // 🔴 POSIBLE ERROR 1: certificadoService.generaPlanilla() puede retornar null o lanzar una excepción
            PDDocument document = certificadoService.generaPlanillaSeguimiento(materiaCarreraId, cursadaInscripto);
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

    @GetMapping("/generaUltimaMateria")
    public ResponseEntity<ByteArrayResource> generaUltimaMateria(
            @RequestParam String legajoId,
            @RequestParam String autoridades
    ) {
        try {
            PDDocument document = certificadoService.generaUltimaMateria(legajoId,autoridades);
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

    @GetMapping("/generaTroquelTramite")
    public ResponseEntity<ByteArrayResource> generaTroquelTramite(
            @RequestParam String legajoId,
            @RequestParam Integer atencionId
    ) {
        try {
            PDDocument document = certificadoService.generaTroquelTramite(legajoId, atencionId);
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

    @GetMapping("/generarCalificador")
    public ResponseEntity<ByteArrayResource> generarCalificador(
            @RequestParam String legajoId,
            @RequestParam boolean enBlanco
    ) {
        System.out.println("enBlanco = " + enBlanco);
        try {
            PDDocument document = certificadoService.generaCalificador(legajoId,enBlanco);
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

    @GetMapping("/generaTroquelIngresoNota")
    public ResponseEntity<ByteArrayResource> generaTroquelIngresoNota(
            @RequestParam Integer atencionId
    ) {
        try {
            PDDocument document = certificadoService.generaTroquelNotaIngresante(atencionId);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; generaTroquelIngresoNota.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/generaTroquelPase")
    public ResponseEntity<ByteArrayResource> generaTroquelPase(
            @RequestParam Integer paseId
    ) {
        try {
            PDDocument document = certificadoService.generaTroquelPase(paseId);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; generaTroquelIngresoNota.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
