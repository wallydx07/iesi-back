package com.example.iesiback.controllers;

import com.example.iesiback.entities.Documento;
import com.example.iesiback.services.ArchivoService;
import com.example.iesiback.services.DocumentoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/documento")
public class DocumentoController {

    private final ArchivoService archivoService;

    @Autowired
    private DocumentoService documentoService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public DocumentoController(ArchivoService archivoService) {
        this.archivoService = archivoService;
    }

    // =====================================================
    //  SUBIR ARCHIVO
    // =====================================================
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("tipo") String tipo,
                                             @RequestParam("tipoEntidad") String tipoEntidad,
                                             @RequestParam("entidadId") String entidadId,
                                             @RequestParam("tipoDocumento") String tipoDocumento) {

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            String uniqueFileName = UUID.randomUUID().toString() + extension;
            String ruta = uploadDir + uniqueFileName;

            File dest = new File(ruta);
            file.transferTo(dest);

            // guardar en BD
            Documento doc = new Documento();
            doc.setNombre(originalFilename);
            doc.setTipo(file.getContentType());
            doc.setRuta(ruta);
            doc.setTamanio(file.getSize());
            doc.setTipoEntidad(tipoEntidad);
            doc.setEntidadId(entidadId);
            doc.setTipoDocumento(tipoDocumento);

            documentoService.saveDocumento(doc);

            return ResponseEntity.ok("Archivo cargado correctamente");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar archivo: " + e.getMessage());
        }
    }

    // =====================================================
    //  BUSCAR ARCHIVOS POR LEGAJO
    // =====================================================
    @GetMapping("/archivos/{entidadId}")
    public ResponseEntity<List<Documento>> getArchivosByLegajo(@PathVariable String entidadId) {
        List<Documento> archivos = documentoService.findByEntidadId(entidadId);
        return ResponseEntity.ok(archivos);
    }

    // =====================================================
    //  DESCARGAR ARCHIVO POR RUTA COMPLETA
    // =====================================================
    @GetMapping("/descargar")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) {
        try {
            filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);
            Path path = Paths.get(filePath).normalize();
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(path);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + path.getFileName() + "\"")
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // =====================================================
    //  DESCARGAR ZIP POR ETIQUETA
    // =====================================================
    @GetMapping("/descargarPorEtiqueta/{etiqueta}")
    public ResponseEntity<InputStreamResource> descargarArchivosPorEtiqueta(@PathVariable String etiqueta) {

        byte[] zipBytes = archivoService.descargarArchivosPorEtiqueta(etiqueta);

        if (zipBytes == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(zipBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/zip");
        headers.add("Content-Disposition", "attachment; filename=archivos_" + etiqueta + ".zip");

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    // =====================================================
    //  NUEVO: OBTENER DOCUMENTO POR ENTIDAD Y TIPO
    // =====================================================
    @GetMapping("/getDocumentoPorEntidadYTipo")
    public ResponseEntity<Documento> getDocumentoPorEntidadYTipo(
            @RequestParam String entidadId,
            @RequestParam String tipoDocumento) {

        Optional<Documento> doc = documentoService.findByEntidadIdAndTipoDocumento(entidadId, tipoDocumento);

        return doc.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // =====================================================
    // CRUD BÁSICO
    // =====================================================

    @PostMapping
    public ResponseEntity<Documento> crearDocumento(@RequestBody Documento documento) {
        return ResponseEntity.ok(documentoService.saveDocumento(documento));
    }

    @GetMapping
    public ResponseEntity<List<Documento>> obtenerTodos() {
        return ResponseEntity.ok(documentoService.getAllDocumentos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Documento> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.getDocumentoById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Documento> actualizarDocumento(
            @PathVariable Long id,
            @RequestBody Documento documento) {

        return ResponseEntity.ok(documentoService.updateDocumento(id, documento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDocumento(@PathVariable Long id) {
        documentoService.deleteDocumento(id);
        return ResponseEntity.noContent().build();
    }
}
