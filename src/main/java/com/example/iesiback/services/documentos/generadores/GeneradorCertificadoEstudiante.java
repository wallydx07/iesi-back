package com.example.iesiback.services.documentos.generadores;

import com.example.iesiback.entities.CertificadoEstudiante;
import com.example.iesiback.entities.DocumentoEmitido;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.entities.Tramite;
import com.example.iesiback.services.CertificadoService;
import com.example.iesiback.services.LegajoService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

import static com.example.iesiback.services.LegajoServiceImpl.log;

@Component
@RequiredArgsConstructor
public class GeneradorCertificadoEstudiante implements GeneradorDocumento {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CertificadoService certificadoService;

    private final LegajoService LegajoService;
    private final LegajoService legajoService;

    @Value("${app.verificacion.url-base}")
    private String urlBaseVerificacion;

    // ================= GeneradorDocumento =================

    @Override
    public boolean soporta(Tramite t) {
        return t.getCertificados() != null && !t.getCertificados().isEmpty();
    }

    @Override
    public Integer diasVigencia() {
        return 0;
    }

    @Override
    public Integer diasVigencia(Tramite t, DocumentoEmitido doc) {
        return switch (tipoDe(doc)) {
            case "ESTUDIANTE REGULAR" -> 30;
            default                   -> null;   // no vence
        };
    }

    @Override
    public void completarSnapshot(Tramite t, DocumentoEmitido doc) {
        CertificadoEstudiante c = doc.getCertificado();
        doc.setTitulo("Certificado de " + texto(c.getTipo()));
        doc.setDetalle("Curso: " + texto(c.getCurso()) + " — Presentar ante: " + texto(c.getAutoridad()));
    }

    @Override
    public byte[] generarPdf(Tramite t, DocumentoEmitido doc) {
        CertificadoEstudiante c = doc.getCertificado();

        if (t.getLegajoId() == null) {
            throw new IllegalStateException("El trámite no tiene legajo asociado");
        }
        Legajo l = legajoService.findLegajoById(t.getLegajoId());

        // ---- datos del legajo ----
        String legajoId  = t.getLegajoId();
        String dni       = String.valueOf(l.getLegajoPersonaDni().getPersonaDni());

        String carreraId = String.valueOf(l.getInscripcion().getCarrera().getCarreraId());
        // ---- datos del certificado ----
        String autoridades = texto(c.getAutoridad());
        String curso       = texto(c.getCurso());
        String accion      = texto(c.getAccion());
        String entrada     = texto(c.getEntrada());
        String salida      = texto(c.getSalida());
        String fechas      = texto(c.getFechas());
        String razon       = texto(c.getRazon());

        log.info("generaRegular -> dni={}, carreraId={}, autoridades={}, curso={}",
                dni, carreraId, autoridades, curso);

        PDDocument original = switch (tipoDe(doc)) {

            case "ESTUDIANTE REGULAR" ->
                    certificadoService.generaRegular(dni, legajoId, autoridades, curso);

            case "ANALITICO" ->
                    certificadoService.generaAnalitico(legajoId, accion, autoridades);

            case "TITULO EN TRAMITE" ->
                    certificadoService.generaTramite(carreraId, legajoId, autoridades);

            case "FINALIZACION DE ESTUDIOS" ->
                    certificadoService.generaFinalizacionEstudios(legajoId, dni, autoridades);

            case "ULTIMA MATERIA" ->
                    certificadoService.generaUltimaMateria(legajoId, autoridades);

            case "ASISTENCIA CLASES" ->
                    certificadoService.generaCertificadoAsistencia(
                            dni, legajoId, autoridades, curso, entrada, salida, fechas, accion);

            case "ASISTENCIA PARCIAL" ->
                    certificadoService.generaAsistenciaParcial(
                            legajoId, autoridades, curso, fechas, accion, entrada, salida,
                            razon);   // TODO: materia → ¿de qué campo sale?

            case "ASISTENCIA EXAMEN FINAL" ->
                    certificadoService.generaAsistenciaExamenFinal(
                            legajoId, autoridades, curso, fechas, accion,
                            razon);   // TODO: materia → ¿de qué campo sale?

            case "SALIDA A CAMPO" ->
                    certificadoService.generaAsistenciaSalidaCampo(
                            legajoId, autoridades, fechas, curso, accion,
                            razon);   // TODO: lugar → ¿de qué campo sale?

            case "JORNADA INSTITUCIONAL" ->
                    certificadoService.generaAsistenciaJornadaInstitucional(
                            dni, autoridades, carreraId, fechas, accion,
                            razon);   // TODO: materia → ¿de qué campo sale?

            default -> throw new IllegalStateException("Tipo de certificado sin PDF: " + c.getTipo());
        };

        return estamparQr(original, doc);
    }

    // ================= estampado del QR =================

    private byte[] estamparQr(PDDocument pdf, DocumentoEmitido doc) {
        try (pdf; ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage ultima = pdf.getPage(pdf.getNumberOfPages() - 1);
            PDImageXObject qr = generarQr(pdf, urlBaseVerificacion + doc.getToken());

            try (PDPageContentStream cs = new PDPageContentStream(
                    pdf, ultima, PDPageContentStream.AppendMode.APPEND, true, true)) {

                float x = 40, y = 40, tam = 80;
                cs.drawImage(qr, x, y, tam, tam);

                String vence = doc.getFechaVencimiento() != null
                        ? "Válido hasta: " + doc.getFechaVencimiento().format(FECHA)
                        : "Sin vencimiento";

                float xt = x + tam + 8;
                float yt = y + tam - 10;
                yt = linea(cs, PDType1Font.HELVETICA_BOLD, 7, "Verificá este documento en:", xt, yt);
                yt = linea(cs, PDType1Font.HELVETICA, 7, urlBaseVerificacion, xt, yt);
                yt = linea(cs, PDType1Font.HELVETICA, 7, "Código: " + doc.getToken(), xt, yt);
                linea(cs, PDType1Font.HELVETICA_BOLD, 7, vence, xt, yt);
            }

            pdf.save(out);
            return out.toByteArray();

        } catch (IOException | WriterException e) {
            throw new IllegalStateException("No se pudo estampar el QR", e);
        }
    }
    // ================= helpers =================

    private String tipoDe(DocumentoEmitido doc) {
        return texto(doc.getCertificado().getTipo()).toUpperCase();
    }

    private PDImageXObject generarQr(PDDocument pdf, String contenido) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = Map.of(
                EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M,
                EncodeHintType.MARGIN, 1);
        BitMatrix matriz = new QRCodeWriter().encode(contenido, BarcodeFormat.QR_CODE, 300, 300, hints);
        BufferedImage imagen = MatrixToImageWriter.toBufferedImage(matriz);
        return LosslessFactory.createFromImage(pdf, imagen);
    }

    private float linea(PDPageContentStream cs, PDFont font, float size,
                        String texto, float x, float y) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(texto);
        cs.endText();
        return y - size - 3;
    }

    private String texto(String valor) {
        return Objects.toString(valor, "").trim();
    }
}