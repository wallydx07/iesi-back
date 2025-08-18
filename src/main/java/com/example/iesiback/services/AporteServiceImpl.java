package com.example.iesiback.services;

import com.example.iesiback.dto.AporteDTO;
import com.example.iesiback.entities.Aporte;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.repositories.AporteRepository;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import be.quodlibet.boxable.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class AporteServiceImpl implements AporteService {
    private final AporteRepository aporteRepository;
    private final LegajoService legajoService;

    public AporteServiceImpl(
            AporteRepository aporteRepository, LegajoService legajoService) {
        this.aporteRepository = aporteRepository;
        this.legajoService = legajoService;
    }
    
    
    
    private static final Color PRIMARY_COLOR = new Color(109, 40, 217); // Morado fuerte
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);  // Negro intenso
    private static final Color TEXT_SECONDARY = new Color(75, 85, 99);  // Gris oscuro
    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);  // Gris claro
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);  // Blanco
    private static final Color INFO_BACKGROUND = new Color(224, 231, 255);  // Azul claro

    public PDDocument generarReciboPDF(String libreta, String turno) {
        PDDocument document = new PDDocument();
        try {
            PDPage page = new PDPage(PDRectangle.A5);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            // Fondo del documento
            drawBackground(contentStream, page);
            // Encabezado
            drawHeader(contentStream, page);
            // Dibujar información en tarjetas
            drawDataTable(document, page);
            // Mensaje de confirmación
            drawFooter(contentStream, page);
            contentStream.close();  // ✅ Cerrar el contenido del PDF
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el recibo PDF", e);
        }
        return document;  // ✅ Retorna el documento abierto para que el Controller lo maneje
    }

    @Override
    public Aporte save(Aporte aporte) {
        return aporteRepository.save(aporte);
    }

    private void drawBackground(PDPageContentStream contentStream, PDPage page) throws IOException {
        contentStream.setNonStrokingColor(BACKGROUND_COLOR);
        contentStream.addRect(0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
        contentStream.fill();
    }

    private void drawHeader(PDPageContentStream contentStream, PDPage page) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.setNonStrokingColor(PRIMARY_COLOR);
        contentStream.newLineAtOffset(page.getMediaBox().getWidth() / 2 - 60, page.getMediaBox().getHeight() - 50);
        contentStream.showText("Recibo de Inscripción");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.setNonStrokingColor(TEXT_PRIMARY);
        contentStream.newLineAtOffset(page.getMediaBox().getWidth() / 2 - 80, page.getMediaBox().getHeight() - 70);
        contentStream.showText("Instituto de Educación Superior Intercultural");
        contentStream.endText();
    }

    private void drawDataTable(PDDocument document, PDPage page) throws IOException {
        float margin = 50;
        float yStart = page.getMediaBox().getHeight() - 120;
        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;

        BaseTable table = new BaseTable(yStart, yStart - 200, 50, tableWidth, margin, document, page, true, true);
        List<List<String>> datos = Arrays.asList(
                Arrays.asList("Nombre", "Juan Pérez"),
                Arrays.asList("Legajo", "12345"),
                Arrays.asList("Curso", "Desarrollo Web"),
                Arrays.asList("Fecha de pago", "2024-02-17"),
                Arrays.asList("Monto", "$5,000.00"),
                Arrays.asList("Usuario", "jperez"),
                Arrays.asList("Recibo", "REC-001"),
                Arrays.asList("Transacción", "TRX-001")
        );

        int index = 0;
        for (List<String> rowData : datos) {
            Row<PDPage> row = table.createRow(30); // Ajustar la altura de la fila

            // Crear celda de clave
            Cell<PDPage> keyCell = row.createCell(40, rowData.get(0));
            keyCell.setFont(PDType1Font.HELVETICA_BOLD);
            keyCell.setFontSize(10);
            keyCell.setFillColor(TEXT_SECONDARY);
            keyCell.setTextColor(Color.WHITE);
            keyCell.setBorderStyle(null); // Eliminar bordes

            // Crear celda de valor
            Cell<PDPage> valueCell = row.createCell(60, rowData.get(1));
            valueCell.setFont(PDType1Font.HELVETICA);
            valueCell.setFontSize(10);
            valueCell.setTextColor(TEXT_PRIMARY);
            valueCell.setBorderStyle(null); // Eliminar bordes

            // Alternar el color de fondo en filas impares
            if (index % 2 == 0) {
                keyCell.setFillColor(CARD_BACKGROUND);
                keyCell.setTextColor(TEXT_SECONDARY);
                valueCell.setFillColor(CARD_BACKGROUND);
            }

            index++;
        }

        table.draw();
    }


    private void drawFooter(PDPageContentStream contentStream, PDPage page) throws IOException {
        float yPosition = 80;

        // Mensaje de confirmación
        contentStream.setNonStrokingColor(INFO_BACKGROUND);
        contentStream.addRect(50, yPosition, page.getMediaBox().getWidth() - 100, 40);
        contentStream.fill();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.setNonStrokingColor(PRIMARY_COLOR);
        contentStream.newLineAtOffset(60, yPosition + 15);
        contentStream.showText("Este recibo confirma tu inscripción. Conserva este documento para futuras referencias.");
        contentStream.endText();

        // Línea divisoria del pie de página
        yPosition -= 20;
        contentStream.setStrokingColor(PRIMARY_COLOR);
        contentStream.setLineWidth(1.5f);
        contentStream.moveTo(50, yPosition);
        contentStream.lineTo(PDRectangle.A5.getWidth() - 50, yPosition);
        contentStream.stroke();

        // Texto del pie de página
        yPosition -= 15;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 8);
        contentStream.setNonStrokingColor(TEXT_SECONDARY);
        contentStream.newLineAtOffset(80, yPosition);
        contentStream.showText("Departamento de Sistemas y Soporte");
        contentStream.endText();

        yPosition -= 12;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 8);
        contentStream.setNonStrokingColor(TEXT_SECONDARY);
        contentStream.newLineAtOffset(80, yPosition);
        contentStream.showText("Instituto de Educación Superior Intercultural");
        contentStream.endText();
    }

    @Override
    public Aporte crearAporte(Aporte aporte) {
        // Si no se envía la fecha, se asigna la fecha actual
        if (aporte.getAporteFecha() == null) {
            aporte.setAporteFecha(LocalDate.now());
        }
        return aporteRepository.save(aporte);
    }


    @Override
    public Aporte findAporteById(Integer id) {
        return aporteRepository.findById(Integer.valueOf(id)).orElse(null);
    }


    @Override
    public List<AporteDTO> getAportesConDatos() {
        return aporteRepository.findAportesConDatos();
    }

    @Override
    public List<Aporte> obtenerAportesPorLegajoId(String legajoId) {
        Optional<Legajo> legajo = this.legajoService.findById(legajoId);
        return legajo.map(aporteRepository::findByAporteLegajo).orElseThrow(() -> new RuntimeException("Legajo no encontrado"));
    }

    @Override
    public List<Aporte> obtenerAportesDelAnioActualPorLegajo(String legajoId) {
        return aporteRepository.findAportesDelAnioActualPorLegajo(legajoId);
    }

    @Override
    public void deleteById(Integer id) {
        aporteRepository.deleteById(id);
    }

    @Override
    public List<Aporte> obtenerAportesYearFiltrado(String legajoId) {
        List<Aporte> aportes = aporteRepository.findAportesDelAnioActualPorLegajo(legajoId)
                .stream()
                .filter(distinctByKeys(a -> Arrays.asList(
//                        a.getAporteFecha(), a.getAporteTalonarioRecibo(), a.getAporteNroRecibo(), a.getAporteMonto()
                       a.getAporteFecha(),  a.getAporteMonto()
                )))
                .collect(Collectors.toList());

        return aportes;
    }

    public static <T> Predicate<T> distinctByKeys(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}