package com.example.iesiback.controllers;

import com.example.iesiback.entities.Documento;
import com.example.iesiback.repositories.DocumentoRepository;
import com.example.iesiback.services.ArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/documento")
public class DocumentoController {

    private final ArchivoService archivoService;
    @Autowired
    private DocumentoRepository documentoRepository;


    // Ruta donde se almacenarán los archivos (en el disco D)
    private static final String UPLOAD_DIR = "D:/files/";  // Directorio donde se almacenan los archivos
    //private static final String UPLOAD_DIR = "/var/www/app/files/";  // Linux
    public DocumentoController(ArchivoService archivoService) {
        this.archivoService = archivoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("tipo") String tipo,
                                             @RequestParam("tipoEntidad") String tipoEntidad,
                                             @RequestParam("entidadId") String entidadId,
                                             @RequestParam("tipoDocumento") String tipoDocumento) {
        try {
            // Verifica si el directorio existe, si no lo crea
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Generar un nombre único para evitar sobrescribir el archivo
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String ruta = UPLOAD_DIR + uniqueFileName;
            File dest = new File(ruta);

            // Si el archivo ya existe, eliminarlo para sobrescribirlo
            if (dest.exists()) {
                dest.delete();  // Elimina el archivo existente
            }

            // Guardar el nuevo archivo
            file.transferTo(dest);

            // Crear o actualizar el objeto Documento en la base de datos
            Documento documento = documentoRepository.findByNombre(file.getOriginalFilename());
            if (documento == null) {
                // Si no existe un documento con ese nombre, se crea uno nuevo
                documento = new Documento();
            }

            // Establecer los valores del archivo
            documento.setNombre(file.getOriginalFilename());
            documento.setTipo(file.getContentType());
            documento.setRuta(ruta);
            documento.setTamanio(file.getSize());
            documento.setTipoEntidad(tipoEntidad);
            documento.setEntidadId(entidadId);
            documento.setTipoDocumento(tipoDocumento);

            // Guardar o actualizar en la base de datos
            documentoRepository.save(documento);

            return ResponseEntity.ok("Archivo cargado y guardado correctamente");
        } catch (IOException e) {
            // Imprime el error en la consola para depuración
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cargar el archivo: " + e.getMessage());
        }
    }

    @GetMapping("/archivos/{legajoId}")
    public ResponseEntity<List<Documento>> getArchivosByLegajo(@PathVariable("legajoId") String legajoId) {
        List<Documento> archivos = documentoRepository.findByEntidadId(legajoId);  // Lógica para obtener los archivos
        return ResponseEntity.ok(archivos);
    }

    @GetMapping("/descargar")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) {
        try {
            // Decodificar la ruta en caso de que tenga caracteres especiales
            filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);

            // Normalizar la ruta para evitar problemas de seguridad
            Path path = Paths.get(filePath).normalize();

            // Crear el recurso a partir del archivo
            Resource resource = new UrlResource(path.toUri());

            // Verificar si el archivo existe y es legible
            if (!resource.exists() || !resource.isReadable()) {
                System.err.println("Error: El archivo no existe o no es accesible: " + path.toString());
                return ResponseEntity.notFound().build();
            }

            // Obtener el tipo de contenido
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // Devolver el archivo como respuesta
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName() + "\"")
                    .body(resource);

        } catch (IOException e) {
            System.err.println("Error al procesar la descarga del archivo: " + filePath);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/descargarPorEtiqueta/{etiqueta}")
    public ResponseEntity<InputStreamResource> descargarArchivosPorEtiqueta(@PathVariable String etiqueta) {
        byte[] zipBytes = archivoService.descargarArchivosPorEtiqueta(etiqueta); // Llamamos al servicio

        if (zipBytes == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Si no hay archivos, respondemos con 404
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(zipBytes);
        InputStreamResource resource = new InputStreamResource(byteArrayInputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/zip");
        headers.add("Content-Disposition", "attachment; filename=archivos_" + etiqueta + ".zip");

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }


}