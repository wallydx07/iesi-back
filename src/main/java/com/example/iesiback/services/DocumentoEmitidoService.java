package com.example.iesiback.services;

import com.example.iesiback.dto.VerificacionDTO;
import com.example.iesiback.entities.CertificadoEstudiante;
import com.example.iesiback.entities.DocumentoEmitido;
import com.example.iesiback.entities.DocumentoEmitidoPdf;
import com.example.iesiback.entities.Tramite;
import com.example.iesiback.enums.EstadoVerificacion;
import com.example.iesiback.repositories.DocumentoEmitidoPdfRepository;
import com.example.iesiback.repositories.DocumentoEmitidoRepository;
import com.example.iesiback.repositories.TramiteRepository;
import com.example.iesiback.services.documentos.EstadosTramite;
import com.example.iesiback.services.documentos.generadores.GeneradorDocumento;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentoEmitidoService {

    private final List<GeneradorDocumento> generadores;
    private final DocumentoEmitidoRepository docRepo;
    private final DocumentoEmitidoPdfRepository pdfRepo;
    private final TramiteRepository tramiteRepo;

    // ================= obtener o emitir =================

    @Transactional
    public DocumentoEmitido obtenerOEmitir(Integer tramiteId, Integer certificadoId) {
        Tramite t = tramiteRepo.findById(tramiteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trámite no encontrado"));

        if (!EstadosTramite.permiteDocumento(t.getTramiteEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El trámite no está en estado Listo o Archivado");
        }

        CertificadoEstudiante c = t.getCertificados().stream()
                .filter(cert -> cert.getId().equals(certificadoId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El certificado no pertenece a este trámite"));

        return docRepo.findByCertificadoIdAndAnuladaFalse(certificadoId)
                .orElseGet(() -> emitir(t, c));
    }

    @Transactional
    public List<DocumentoEmitido> obtenerOEmitirTodos(Integer tramiteId) {
        Tramite t = tramiteRepo.findById(tramiteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trámite no encontrado"));

        return t.getCertificados().stream()
                .map(c -> obtenerOEmitir(tramiteId, c.getId()))
                .toList();
    }

    // ================= PDF =================

    @Transactional(readOnly = true)
    public byte[] obtenerPdf(DocumentoEmitido doc) {
        if (doc.getFechaVencimiento() != null && LocalDate.now().isAfter(doc.getFechaVencimiento())) {
            throw new ResponseStatusException(HttpStatus.GONE,
                    "El documento venció el " + doc.getFechaVencimiento()
                            + ". Debe iniciar un nuevo trámite.");
        }

        return pdfRepo.findById(doc.getId())
                .map(DocumentoEmitidoPdf::getContenido)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.GONE,
                        "El archivo de este documento ya no está disponible"));
    }

    // ================= emisión =================

    private DocumentoEmitido emitir(Tramite t, CertificadoEstudiante c) {
        GeneradorDocumento gen = generadores.stream()
                .filter(g -> g.soporta(t))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Este tipo de trámite no emite documento"));

        DocumentoEmitido doc = new DocumentoEmitido();
        doc.setToken(UUID.randomUUID());
        doc.setCertificado(c);
        doc.setTitularNombre(t.getTramiteApellidoNombre());
        doc.setTitularDni(String.valueOf(t.getTramiteDni()));
        doc.setFechaEmision(LocalDateTime.now());

        Integer dias = gen.diasVigencia(t, doc);
        if (dias != null) {
            doc.setFechaVencimiento(LocalDate.now().plusDays(dias));
        }

        gen.completarSnapshot(t, doc);
        byte[] pdf = gen.generarPdf(t, doc);
        doc.setHashSha256(sha256(pdf));
        docRepo.save(doc);

        DocumentoEmitidoPdf archivo = new DocumentoEmitidoPdf();
        archivo.setDocumento(doc);
        archivo.setContenido(pdf);
        pdfRepo.save(archivo);

        return doc;
    }

    private String sha256(byte[] datos) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(datos));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    @Transactional(readOnly = true)
    public VerificacionDTO verificar(UUID token) {
        DocumentoEmitido doc = docRepo.findByTokenConTramite(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Documento no encontrado"));

        String estadoTramite = doc.getCertificado().getTramite().getTramiteEstado();

        EstadoVerificacion estado;
        if (doc.isAnulada() || !EstadosTramite.permiteDocumento(estadoTramite)) {
            estado = EstadoVerificacion.ANULADA;
        } else if (doc.getFechaVencimiento() != null && LocalDate.now().isAfter(doc.getFechaVencimiento())) {
            estado = EstadoVerificacion.VENCIDA;
        } else {
            estado = EstadoVerificacion.VIGENTE;
        }

        return new VerificacionDTO(
                estado,
                doc.getTitularNombre(),
                enmascararDni(doc.getTitularDni()),
                doc.getTitulo(),
                doc.getDetalle(),
                doc.getFechaEmision().toLocalDate(),
                doc.getFechaVencimiento()
        );
    }

    private String enmascararDni(String dni) {
        if (dni == null || dni.length() < 3) return "***";
        return "**.***." + dni.substring(dni.length() - 3);
    }

    @Transactional(readOnly = true)
    public byte[] obtenerPdfPublico(UUID token) {
        DocumentoEmitido doc = docRepo.findByTokenConTramite(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Documento no encontrado"));

        String estadoTramite = doc.getCertificado().getTramite().getTramiteEstado();
        if (doc.isAnulada() || !EstadosTramite.permiteDocumento(estadoTramite)) {
            throw new ResponseStatusException(HttpStatus.GONE, "El documento no tiene validez");
        }

        return obtenerPdf(doc);   // ya controla vencimiento y si el archivo fue depurado
    }

}