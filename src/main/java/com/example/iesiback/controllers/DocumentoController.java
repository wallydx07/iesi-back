package com.example.iesiback.controllers;

import com.example.iesiback.entities.Documento;
import com.example.iesiback.repositories.DocumentoRepository;
import com.example.iesiback.services.ArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/documento")
public class DocumentoController {
    private final ArchivoService archivoService;
    @Autowired
    private DocumentoRepository documentoRepository;
    // Ruta donde se almacenarán los archivos (en el disco D)
    //private static final String uploadDir = "D:/files/";  // Directorio donde se almacenan los archivos
//    private static final String uploadDir = "/var/www/app/files/";  // Linux

    @Value("${app.upload.dir}")
    private String uploadDir;

    public DocumentoController(ArchivoService archivoService) {
        this.archivoService = archivoService;
    }

//
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
//                                             @RequestParam("tipo") String tipo,
//                                             @RequestParam("tipoEntidad") String tipoEntidad,
//                                             @RequestParam("entidadId") String entidadId,
//                                             @RequestParam("tipoDocumento") String tipoDocumento) {
//        try {
//            File dir = new File(UPLOAD_DIR);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
//            String ruta = UPLOAD_DIR + uniqueFileName;
//            File dest = new File(ruta);
//            if (dest.exists()) {
//                dest.delete();
//            }
//            file.transferTo(dest);//Guardar imagen en carpeta del servidor
//            Documento documento = documentoRepository.findByNombre(file.getOriginalFilename());
//            if (documento == null) {
//                documento = new Documento();
//            }
//            //Documento contiene los datos de la iamgen para buscarla en la carpte del servidor
//            documento.setNombre(file.getOriginalFilename());
//            documento.setTipo(file.getContentType());
//            documento.setRuta(ruta);
//            documento.setTamanio(file.getSize());
//            documento.setTipoEntidad(tipoEntidad);
//            documento.setEntidadId(entidadId);
//            documento.setTipoDocumento(tipoDocumento);
//            documentoRepository.save(documento);
//            return ResponseEntity.ok("Archivo cargado y guardado correctamente");
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error al cargar el archivo: " + e.getMessage());
//        }
//    }


@PostMapping("/upload")
public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("tipo") String tipo,
                                         @RequestParam("tipoEntidad") String tipoEntidad,
                                         @RequestParam("entidadId") String entidadId,
                                         @RequestParam("tipoDocumento") String tipoDocumento) {
    String ruta = null;
    try {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        ruta = uploadDir + uniqueFileName;
        File dest = new File(ruta);

        if ("fotoId".equals(tipoDocumento)) {
            byte[] bytes = file.getBytes();
            Mat image = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
            if (image.empty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La imagen no es válida.");
            }

            Mat croppedImage = recortarImagen(image);
            boolean success = Imgcodecs.imwrite(ruta, croppedImage);
            if (!success) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo guardar la imagen recortada.");
            }
        } else {
            file.transferTo(dest);
        }

        // Guardar en la base de datos solo si el archivo fue guardado correctamente
        guardarDocumentoEnBaseDeDatos(file, ruta, tipoEntidad, entidadId, tipoDocumento);
        return ResponseEntity.ok("Archivo cargado y guardado correctamente");
    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al cargar el archivo: " + e.getMessage());
    }
}


//    // Método para recortar la imagen usando OpenCV
//    private Mat recortarImagen(Mat image) {
//        // Convertir la imagen a escala de grises
//        Mat gray = new Mat();
//        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
//        // Aplicar un umbral para mejorar la detección de bordes
//        Mat thresholdImage = new Mat();
//        Imgproc.threshold(gray, thresholdImage, 200, 255, Imgproc.THRESH_BINARY_INV);
//
//        // Buscar los contornos en la imagen umbralizada
//        java.util.List<MatOfPoint> contours = new java.util.ArrayList<>();
//        Mat hierarchy = new Mat();
//       Imgproc.findContours(thresholdImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        // Buscar el contorno más grande (probablemente la foto de 4x4)
//        double maxArea = 0;
//        Rect boundingRect = null;
//        for (MatOfPoint contour : contours) {
//            double area = Imgproc.contourArea(contour);
//            if (area > maxArea) {
//                maxArea = area;
//                boundingRect = Imgproc.boundingRect(contour);
//            }
//        }
//
//        if (boundingRect != null) {
//            System.out.println("SE ha enocntrado el contorno");
//            // Recortar la imagen usando la caja delimitadora del contorno encontrado
//            return new Mat(image, boundingRect);
//        } else {
//            // Si no se encuentra un contorno válido, se devuelve la imagen original
//
//            System.out.println("imposible encontrar contorno");
//            return image;
//        }
//    }


    private Mat recortarImagen(Mat image) {
        // Convertimos la imagen a espacio de color HSV
        Mat hsv = new Mat();
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);

        // Rango ajustado de fondo celeste (mejor exclusión de blanco/gris)
        Scalar lowerBlue = new Scalar(85, 30, 120);  // (H, S, V)
        Scalar upperBlue = new Scalar(115, 255, 255);

        // Crear una máscara del fondo celeste
        Mat mask = new Mat();
        Core.inRange(hsv, lowerBlue, upperBlue, mask);

        // Buscar contornos en la máscara
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Buscar el contorno más grande (mayor a un área mínima)
        double maxArea = 0;
        Rect boundingRect = null;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > 5000 && area > maxArea) {  // evitar contornos chicos
                maxArea = area;
                boundingRect = Imgproc.boundingRect(contour);
            }
        }

        if (boundingRect != null) {
            System.out.println("✔ Recorte cuadrado limpio sin márgenes");

            int margin = 5; // margen interno para evitar bordes del documento

            int x = Math.max(boundingRect.x + margin, 0);
            int y = Math.max(boundingRect.y + margin, 0);
            int width = boundingRect.width - 2 * margin;
            int height = boundingRect.height - 2 * margin;

            int side = Math.max(width, height);

            // Asegurar que no se pase de los límites
            x = Math.min(x, image.cols() - side);
            y = Math.min(y, image.rows() - side);
            x = Math.max(0, x);
            y = Math.max(0, y);

            Rect squareRect = new Rect(x, y, side, side);
            return new Mat(image, squareRect);
        } else {
            System.out.println("⚠ No se detectó fondo celeste. Devolviendo imagen original.");
            return image;
        }
    }



    private void guardarDocumentoEnBaseDeDatos(MultipartFile file, String ruta, String tipoEntidad, String entidadId, String tipoDocumento) {
        Documento documento = documentoRepository.findByNombre(file.getOriginalFilename());
        if (documento == null) {
            documento = new Documento();
        }
        documento.setNombre(file.getOriginalFilename());
        documento.setTipo(file.getContentType());
        documento.setRuta(ruta);
        documento.setTamanio(file.getSize());
        documento.setTipoEntidad(tipoEntidad);
        documento.setEntidadId(entidadId);
        documento.setTipoDocumento(tipoDocumento);
        documentoRepository.save(documento);
    }



    @GetMapping("/archivos/{legajoId}")
    public ResponseEntity<List<Documento>> getArchivosByLegajo(@PathVariable("legajoId") String legajoId) {
        List<Documento> archivos = documentoRepository.findByEntidadId(legajoId);  // Lógica para obtener los archivos
        return ResponseEntity.ok(archivos);
    }

    @GetMapping("/descargar")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) {
        try {
            filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);
            Path path = Paths.get(filePath).normalize();
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                System.err.println("Error: El archivo no existe o no es accesible: " + path.toString());
                return ResponseEntity.notFound().build();
            }
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
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



    @GetMapping("/getDocumentoPorEntidadYTipo")
    public ResponseEntity<Documento> getDocumentoPorEntidadYTipo(
            @RequestParam String entidadId,
            @RequestParam String tipoDocumento) {

        return documentoRepository.findByEntidadIdAndTipoDocumento(entidadId, tipoDocumento)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }




}