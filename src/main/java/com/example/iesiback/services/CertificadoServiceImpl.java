package com.example.iesiback.services;

import be.quodlibet.boxable.*;
import be.quodlibet.boxable.line.LineStyle;
import com.example.iesiback.dto.*;
import com.example.iesiback.entities.*;
import com.example.iesiback.repositories.HtmlService;
import com.example.iesiback.repositories.MateriaCarreraRepository;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.pdf.BaseFont;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.format.TextStyle;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

@Service
public class CertificadoServiceImpl implements CertificadoService {
    private final PersonaService alumnoService;
    private final CarreraService carreraService;
    private final NotaService notaService;
    private final MateriaCarreraRepository materiaCarreraRepository;
    private final DocumentoService documentoService;
    private final LegajoService legajoService;
    private final AlumnoLegajoService alumnoLegajoService;
    private final PersonalService personalService;
    private final AsistenciaPersonalService asistenciaPersonalService;
    private final HtmlService htmlService;
    private final AporteService aporteService;
    private final UserService userService;
    private final ObservacionesService observacionesService;
    private final AtencionService atencionService;
    private final PagoService pagoService;
    private final PasesService paseService;
    private final TurnoService turnoService;
    private final PermisoService permisoService;
    private final ExamenService examenService;

    @Autowired
    public CertificadoServiceImpl(@Lazy PersonaService alumnoService,
                                  CarreraService carreraService,
                                  NotaService notaService,
                                  MateriaCarreraRepository materiaCarreraRepository, DocumentoService documentoService,
                                  LegajoService legajoService,
                                  AlumnoLegajoService alumnoLegajoService,
                                  PersonalService personalService, AsistenciaPersonalService asistenciaPersonalService, HtmlService htmlService, AporteService aporteService, UserService userService, ObservacionesService observacionesService, AtencionService atencionService, PagoService pagoService, PasesService paseService, TurnoService turnoService, PermisoService permisoService, ExamenService examenService) {
        this.alumnoService = alumnoService;
        this.carreraService = carreraService;
        this.notaService = notaService;
        this.materiaCarreraRepository = materiaCarreraRepository;
        this.documentoService = documentoService;
        this.legajoService = legajoService;
        this.alumnoLegajoService = alumnoLegajoService;
        this.personalService = personalService;
        this.asistenciaPersonalService = asistenciaPersonalService;
        this.htmlService = htmlService;
        this.aporteService = aporteService;
        this.userService = userService;
        this.observacionesService = observacionesService;
        this.atencionService = atencionService;
        this.pagoService = pagoService;
        this.paseService = paseService;
        this.turnoService = turnoService;
        this.permisoService = permisoService;
        this.examenService = examenService;
    }

    @Override
    public PDDocument generaRegular(String dniId, String legajoId, String autoridades, String curso) {
        Persona persona = this.alumnoService.findAlumnoById(dniId);//agregar el id
        Carrera carrera = this.carreraService.obtenerCarreraPorLegajoId(legajoId);//agregar el id
        PDImageXObject Iesc2;
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc2I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc2I == null) {
                System.out.println("readFilesInBytes: File does not exist");
            }
            byte[] be = IOUtils.toByteArray(iesc2I);
            Iesc2 = PDImageXObject.createFromByteArray(Documento, be, "static/imagenes/esc2.png");
            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);
            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);
// Altura de la página
            float pageHeight = PDRectangle.A4.getHeight();
// Ancho de la página
            float pageWidth = PDRectangle.A4.getWidth();
// Texto para cada línea
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };
// Distancia entre líneas
            int n = -10;
// Comienza a escribir el texto
            contenido.beginText();
            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);
            for (String line : lines) {
                // Calcula el ancho de cada línea
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;

                // Calcula la posición x para centrar el texto
                float xStart = (pageWidth - textWidth) / 2;

                // Mueve la posición x
                contenido.newLineAtOffset(xStart, 0);

                // Escribe la línea
                contenido.showText(line);

                // Mueve a la siguiente línea
                contenido.newLineAtOffset(-xStart, n);
            }

            contenido.endText();
            contenido.close();

            //imagen del encavezado izquierda
            PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc2.moveTo(200, 100);
            PDesc2.drawImage(Iesc2, 15, 780, 50, 50);
            PDesc2.close();
            //imagen derecha del envavezado
            //  PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //   PDesc2.moveTo(200, 100);//image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            //  PDesc2.drawImage(Iesc2, 510, 770, 60, 60);//Draw an image at the x,y coordinates, with the given size.
            //    PDesc2.close();

            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);

            n = -10;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 10);
            regular.newLineAtOffset(200, 720);//titulo
            regular.showText("CONSTANCIA DE ESTUDIANTE REGULAR");
            regular.newLineAtOffset(0, 0);
            regular.showText("_____________________________________");
            regular.endText();
            ///=============================================================
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            String hola = " __________________";
            String texto = Dividir(hola, width, letra);
            lineas = procesar(texto, letra);
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
            //consulta
            String genero = persona.getPersonaGenero();
            String genero2 = "";
            String genero1 = "";
            if (genero.equals("Masculino")) {
                genero1 = "el SR";
                genero2 = "el interesado";
                System.out.println("Es Hombre");
            } else {
                genero1 = "la Sra";
                genero2 = "la interesada";
                System.out.println("Es Mujer");
            }
            regular.beginText();
            regular.setFont(normal, letra);
            regular.newLineAtOffset(50, 680);
            String t1 = ("-----Por la presente, la Rectora del ");
            String t2 = ("Instituto de Educación Superior Intercultural CAMPINTA ");
            //regular.setCharacterSpacing(charspacing(longitud,t1+t2,letra));//espacio entre caracteres
            regular.showText(t1);
            regular.newLineAtOffset(tamaño(t1, letra, normal), 0);
            regular.setFont(negrita, letra);
            //String t2=("Instituto de Educación Superior Intercultural CAMPINTA");
            regular.showText(t2);
            float longitud = tamaño(t1 + t2, letra, PDType1Font.HELVETICA) + 20;//longitud permitida para justificar
            regular.newLineAtOffset(-tamaño(t1, letra, normal), -20);//nueva linea abajo justo al inicio
            String t3 = ("GUAZU GLORIA PEREZ ");
            String t4 = ("Prof. Cristina Noemi Martínez, deja ");//CONSTANCIA que ");
            String t41 = ("CONSTANCIA ");
            String t5 = ("que " + genero1);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41));//espacio entre caracteres
            regular.showText(t3);
            regular.setFont(PDType1Font.HELVETICA, letra);
            regular.newLineAtOffset(tamaño(t3, letra, negrita) + t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            //String t4=(" Prof. Cristina Noemi Martínez, deja CONSTANCIA que el Sr");
            regular.showText(t4);
            regular.newLineAtOffset(tamaño(t4, letra, normal) + t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t41);
            regular.newLineAtOffset(tamaño(t41, letra, normal) + t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t5);
            regular.newLineAtOffset(-tamaño(t3, letra, negrita) - tamaño(t41, letra, negrita) - tamaño(t4, letra, normal) - t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), -20);////charspacing(longitud, tamaño(t3, letra, normal),t3+t4)-tamaño(t4,letra, PDType1Font.HELVETICA)-t4.length()*charspacing(longitud, tamaño(t3, letra, normal),t3+t4+t5),-20 );
            String nombre = persona.getPersonaNombre();
            String apellido = persona.getPersonaApellido();
            Long dni = persona.getPersonaDni();
            String t6 = (apellido + " " + nombre + " D.N.I: " + dni + " ");
            String t7 = (", es estudiante regular de la carrera:");
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, +tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7));//espacio entre caracteres
            regular.showText(t6);
            regular.newLineAtOffset(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t7);
            regular.newLineAtOffset(-(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7)), -20);//linea nueav
            String carreraCompl = "Tecnicatura Superior en " + carrera.getCarreraNombre();
            String t10 = (" y actualmente se");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(carreraCompl, letra, negrita) + tamaño(t10, letra, normal), carrera + t10));//espacio entre caracteres
            regular.setFont(negrita, letra);
            regular.showText(carreraCompl);
            regular.newLineAtOffset(tamaño(carreraCompl, letra, negrita) + carreraCompl.length() * charspacing(longitud, tamaño(carreraCompl, letra, negrita) + tamaño(t10, letra, normal), carreraCompl + t10), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t10);
            regular.newLineAtOffset(-(tamaño(carreraCompl, letra, negrita) + (carreraCompl.length()) * charspacing(longitud, tamaño(carreraCompl, letra, negrita) + tamaño(t10, letra, normal), carreraCompl + t10)), -20);//linea nueav
            String t11 = ("encuentra cursando el ");
            String t12 = curso;
            String t13 = (" de este Instituto");
            t13 = rellenar(t11 + t12 + t13, t13, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13));//espacio entre caracteres
            regular.showText(t11);
            regular.newLineAtOffset(tamaño(t11, letra, normal) + t11.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t12);
            System.out.println(t13 + "//////////////////");
//String t13=("Año de este Instituto");
            regular.newLineAtOffset(tamaño(t12, letra, normal) + t12.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t13);
            String t14 = ("-----Se expide la presente CONSTANCIA a solicitud de " + genero2 + "  y al solo efecto de ser");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal), t14));//espacio entre caracteres
            regular.newLineAtOffset(-(tamaño(t11, letra, normal) + (t11.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)) - (tamaño(t12, letra, PDType1Font.HELVETICA) + (t12.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)), -20);//linea nueav

            regular.showText(t14);
            String t15 = ("presentada ante las Autoridades del ");
            String t16 = autoridades;
            System.out.println("autoridades " + t16);
            if (t16.equals("que lo requieran")) {
                t15 = "presentada ante las Autoridades";
            }
            t16 = rellenar(t15 + t16, t16, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, negrita), t15 + t16));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);
            regular.setFont(normal, letra);
            regular.showText(t15);
            // String t16=("B.E.G.U.P");
            regular.newLineAtOffset(tamaño(t15, letra, normal) + t15.length() * charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, normal), t15 + t16), 0);
            regular.setFont(normal, letra);
            regular.showText(t16);
            // String t17=("------------------SAN SALVADOR DE JUJUY, "+fecha());
            String t17 = ("-----SAN SALVADOR DE JUJUY, " + fecha() + "-------------");
            regular.newLineAtOffset(-(tamaño(t15, letra, normal) + (t15.length()) * charspacing(longitud, tamaño(t16, letra, normal) + tamaño(t15, letra, normal), t15 + t16)), -20);//linea nueav
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t17, letra, normal), t17));//espacio entre caracteres
            regular.showText(t17);
            regular.newLineAtOffset(0, -20);
            margin = 30;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (4 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = yStartNewPage + 30;
            float bottomMargin = 70;
            float yPosition = 550;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, Documento, Pagina, true, drawContent);
            Row<PDPage> headerRow = table.createRow(300);
            Cell<PDPage> cell = headerRow.createCell(100, " ");
            //cell.setText("hoal a atodsssss");
            table.draw();
            regular.endText();
            regular.close();
            System.out.println("se divujo la imagen");
            //   Documento.save(dir + ".pdf");
            //================

            //Documento.close();
        } catch (IOException e) {
        }
        return Documento;
    }


    private static String rellenar(String Ta, String t13, int letra, float width) throws IOException {
        String todo = "";
        float size = letra * PDType1Font.HELVETICA.getStringWidth(Ta) / 1000;
        float free = width - size;
        while (free > 100) {
            t13 = t13 + "-";
            Ta = Ta + "-";
            size = letra * PDType1Font.HELVETICA.getStringWidth(Ta) / 1000;
            free = width - size;
        }
        todo = todo + t13;
        return todo;
    }

    private static float charspacing(float width, float size, String par) throws IOException {
        float espacio = 0;
        float free = width - size;
        espacio = free / (par.length() - 1);
        return espacio;
    }

    private static float tamaño(String t1, int letra, PDType1Font fuente) throws IOException {
        float size = 0;
        size = letra * fuente.getStringWidth(t1) / 1000;
        return size;
    }

    private static String fecha() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM 'del año' yyyy", new Locale("es", "ES"));
        LocalDate fechaActual = LocalDate.now();
        return fechaActual.format(formatter);
    }

    private static String fechaAnalitico() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'días del mes de' MMMM 'del año' yyyy", new Locale("es", "ES"));
        LocalDate fechaActual = LocalDate.now();
        return fechaActual.format(formatter);
    }


    private static String fechaFija(LocalDate fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'San Salvador de jujuy ' EEEE dd 'de' MMMM 'del año' yyyy", new Locale("es", "ES"));
        return fecha.format(formatter);
    }


    public static String Dividir(String s, float width, int letra) throws IOException {
        float free = 1;
        int contador = 1, pos;
        String todo = "";
        float charSpacing = 0;
        float size = 0;
        s = s.trim(); //eliminar los posibles espacios en blanco al principio y al final
        pos = s.indexOf(" "); //se busca el primer espacio en blanco
        System.out.println();
        String aux = "";
        String var = "";
        while (pos != -1) {   //mientras que se encuentre un espacio en blanco
            String SubCadena = s.substring(var.length() + todo.length(), pos);
            System.out.println(free);
            if (free > 0) {
                var = var + SubCadena;
            } else {
                todo = todo + var;
                todo = todo + SubCadena + "@";
                contador = 1;
                var = "";
            }
            size = letra + 15 * PDType1Font.HELVETICA.getStringWidth(var) / 1000;
            free = width - size;
            pos = s.indexOf(" ", pos + 1); //se busca el siguiente espacio en blanco
        }
        String SubCadena = s.substring(todo.length(), s.length());
        var = SubCadena;
        size = letra + 15 * PDType1Font.HELVETICA.getStringWidth(var) / 1000;
        free = width - size;
        System.out.println(free);
        while (free > 0) {
            var = var + "-";
            size = letra + 15 * PDType1Font.HELVETICA.getStringWidth(var) / 1000;
            free = width - size;
        }
        todo = todo + var;
        return todo;
    }

    public static java.util.List<String> procesar(String texto, int letra) {
        java.util.List<String> lines = new ArrayList<String>();
        try {

            PDPage P = new PDPage(PDRectangle.A4);
            PDRectangle mediabox = P.getMediaBox();
            float margin = 72;
            float width = mediabox.getWidth() - 2 * margin;
            float startX = mediabox.getLowerLeftX() + margin;
            float startY = mediabox.getUpperRightY() - margin;
            String textNL = texto;//"I am trying to create a PDF file with a lot of text contents in the document. I am using PDFBox.\nFurthermore, I have added some newline characters to the string at which lines also shall be broken.It should work alright like this...";
            for (String text : textNL.split("@")) {
                int lastSpace = -1;
                while (text.length() > 0) {
                    int spaceIndex = text.indexOf(' ', lastSpace + 1);
                    if (spaceIndex < 0) {
                        spaceIndex = text.length();
                    }
                    String subString = text.substring(0, spaceIndex);
                    float size = letra * PDType1Font.HELVETICA.getStringWidth(subString) / 1000;
                    //System.out.printf("'%s' - %f of %f\n", subString, size, width);
                    if (size > width) {
                        if (lastSpace < 0) {
                            lastSpace = spaceIndex;
                        }
                        subString = text.substring(0, lastSpace);
                        lines.add(subString);
                        text = text.substring(lastSpace).trim();
                        //  System.out.printf("'%s' is line\n", subString);
                        lastSpace = -1;
                    } else if (spaceIndex == text.length()) {
                        lines.add(text);
                        //System.out.printf("'%s' is line\n", text);
                        text = "";
                    } else {
                        lastSpace = spaceIndex;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("No se ha podido procesar el texto");
        }
        System.out.println(lines);
        return lines;
    }

//    @Override
//    public PDDocument generaAnalitico(String legajoId, String accion, String autoridades) {
//        Persona persona = this.alumnoService.obtenerAlumnoPorLegajoId(legajoId);
//        Carrera carrera = this.carreraService.obtenerCarreraPorLegajoId(legajoId);
//        double nuevoProm = 0;
//        int contProm = 0;
//        List<NotaMateriaDTO> listaMaterias = new ArrayList<NotaMateriaDTO>();
//        listaMaterias = this.notaService.obtenerTodasNotasPorLegajoAnalitico(legajoId);
////            System.out.println("Lista de Materias:");
////            for (NotaMateriaDTO notaMateria : listaMaterias) {
////                System.out.println("Nota ID: " + notaMateria.getNotaId());
////                System.out.println("Materia Orden: " + notaMateria.getMateriaOrden());
////                System.out.println("Materia Nombre: " + notaMateria.getMateriaNombre());
////                System.out.println("Nota Calificación Número: " + notaMateria.getNotaCalificacionNumero());
////                System.out.println("Nota Calificación Letra: " + notaMateria.getNotaCalificacionLetra());
////                System.out.println("Nota Condición: " + notaMateria.getNotaCondicion());
////                System.out.println("Nota Estado: " + notaMateria.getNotaEstado());
////                System.out.println("Nota Libro: " + notaMateria.getNotaLibro());
////                System.out.println("Nota Folio: " + notaMateria.getNotaFolio());
////                System.out.println("Nota Fecha: " + notaMateria.getNotaFecha());
////                System.out.println("Nota Observaciones: " + notaMateria.getNotaObservaciones());
////                System.out.println("Nota Usuario: " + notaMateria.getNotaUsuario());
////                System.out.println("Nota Status: " + notaMateria.getNotaStatus());
////                System.out.println("Nota Final: " + notaMateria.getNotaFinal());
////                System.out.println("Correlativas: " + notaMateria.getCorrelativas());
////                System.out.println("Materia ID: " + notaMateria.getMateriaId());
////                System.out.println("Materia Nivel: " + notaMateria.getMateriaNivel());
////                System.out.println("Cursada ID: " + notaMateria.getCursadaId());
////                System.out.println("-----------------------------");
////            }
//        System.out.println(listaMaterias.size() + "Tamaño 1");
//        PDImageXObject Iesc1, Iesc2, casilla0, casilla1;
//        PDDocument Documento = new PDDocument();
//        try {
//            String carrera_id = carrera.getCarreraId();
//            String carreraNombre = carrera.getCarreraNombre();
//            int nMaterias = 0;//cantidad de matirias
//            int materiasPrimero = this.materiaCarreraRepository.countMateriasPorNivel(carrera_id, "1ro");
//            int materiasSegundo = this.materiaCarreraRepository.countMateriasPorNivel(carrera_id, "2do");
//            int materiasTercero = this.materiaCarreraRepository.countMateriasPorNivel(carrera_id, "3ro");
//            int n = -10;//distancia entre lineas
//            int letra = 11;//Tamaño de letras
//            Long dni = persona.getPersonaDni();
//            String nombre = persona.getPersonaNombre();
//            String apellido = persona.getPersonaApellido();
//            String resolucion = carrera.getCarreraResolucion();
//            PDType1Font normal = PDType1Font.HELVETICA;
//            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
//            //Creando documento nuevo
//            PDPage Pagina = new PDPage(PDRectangle.A4);
//            float margin = 25; //40
//
//            //===============================================================================
//            //  InputStream iesc1I = CRegular.class.getClassLoader().getResourceAsStream("Imagenes/logocoaj.png");
//            //    if (iesc1I == null) {
//            //        System.out.println("readFilesInBytes: File " + "file" + " does not exist");
//            //     }
//            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
//
//            //   InputStream iesc1I = CertificadoService.class.getClassLoader().getResourceAsStream("Imagenes/esc2.png");//iesc2I
//            if (iesc1I == null) {
//                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
//            }
//            byte[] ba = IOUtils.toByteArray(iesc1I);
//            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");//divujar desde el path
//            // byte[] be = IOUtils.toByteArray(iesc2I);
//            //     Iesc2 = PDImageXObject.createFromByteArray(Documento, be, "esc2.png");//divujar desde el path
//
//            Documento.addPage(Pagina);
//            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);
//
//            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
//            int fontSize = 8; // Tamaño de la fuente
//            contenido.setFont(font, fontSize);
//            // Altura de la página
//            float pageHeight = PDRectangle.A4.getHeight();
//
//            // Ancho de la página
//            float pageWidth = PDRectangle.A4.getWidth();
//
//            // Texto para cada línea
//            String[] lines = {
//                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
//                    "“CAMPINTA GUAZU GLORIA PEREZ”",
//                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
//                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
//                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
//                    "________________________________________________________________________________________________________________________"
//            };
//
//            // Comienza a escribir el texto
//            contenido.beginText();
//
//            float iStart = 820;
//            contenido.newLineAtOffset(0, iStart);
//
//            for (String line : lines) {
//                // Calcula el ancho de cada línea
//                float textWidth = font.getStringWidth(line) / 1000 * fontSize;
//
//                // Calcula la posición x para centrar el texto
//                float xStart = (pageWidth - textWidth) / 2;
//
//                // Mueve la posición x
//                contenido.newLineAtOffset(xStart, 0);
//
//                // Escribe la línea
//                contenido.showText(line);
//
//                // Mueve a la siguiente línea
//                contenido.newLineAtOffset(-xStart, n);
//            }
//
//            contenido.endText();
//            contenido.close();
//
//            //imagen del encavezado izquierda
//            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
//            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
//            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
//            PDesc1.close();
//            //imagen derecha del envavezado
//            //  PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
//            //   PDesc2.moveTo(200, 100);//image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
//            //  PDesc2.drawImage(Iesc2, 510, 770, 60, 60);//Draw an image at the x,y coordinates, with the given size.
//            //    PDesc2.close();
//
//            //================================================
//            PDPageContentStream titulo = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
//            //texto de constancia
//            n = -15;//distancia entre lineas
//            titulo.beginText();
//            titulo.setFont(PDType1Font.HELVETICA_BOLD, 10);
//            titulo.newLineAtOffset(240, 750);//titulo/(250,745)
//            titulo.showText("CONSTANCIA ANALITICO");
//            titulo.newLineAtOffset(0, 0);
//            titulo.showText("______________________");
//            titulo.endText();
//            titulo.close();
//            PDPageContentStream pTexto = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
//            //===================================================================
//            //Justificar texto
//            String genero1 = "";
//            String genero = persona.getPersonaGenero();
//            if (genero.equals("Masculino")) {
//                genero1 = "el SR";
//            } else {
//                genero1 = "la Sra";
//            }
//            float longitud = 500;//longitud permitida para justificar
//            pTexto.beginText();
//            pTexto.setFont(normal, letra);
//            pTexto.newLineAtOffset(55, 725);
//            String t1 = ("-----Por la presente, se deja constancia que " + genero1 + " ");
//            //String nombre=(String)objcrud.consultaString("select alumno.alumno_nombre from alumno where alumno.alumno_dni='"+alumno_dni+"';", "alumno_nombre");
//            //String apellido=(String)objcrud.consultaString("select alumno.alumno_apellido from alumno where alumno.alumno_dni='"+alumno_dni+"';", "alumno_apellido");
//            String t2 = (apellido + " " + nombre + ", DNI: " + dni);
//            pTexto.setCharacterSpacing(charspacing(longitud, tamaño(t1, letra, normal) + tamaño(t2, letra, negrita), t1 + t2));//espacio entre caracteres
//            pTexto.showText(t1);
//            // pTexto.newLineAtOffset(tamaño(t1,letra, normal), 0);
//            pTexto.newLineAtOffset(tamaño(t1, letra, normal) + t1.length() * charspacing(longitud, tamaño(t1, letra, normal) + tamaño(t2, letra, negrita), t1 + t2), 0);
//            pTexto.setFont(negrita, letra);
//            pTexto.showText(t2);
//            //  float longitud=tamaño(t1+t2,letra, PDType1Font.HELVETICA)+10;//longitud permitida para justificar
//            // float longitud=tamaño(t1+t2,letra, normal)+20;//longitud permitida para justificar
//
//            pTexto.newLineAtOffset(-tamaño(t1, letra, normal) - t1.length() * charspacing(longitud, tamaño(t1, letra, normal) + tamaño(t2, letra, negrita), t1 + t2), n);//nueva linea abajo justo al inicio
//            String t3 = accion;
//            //String t3="ha cursado la carrera: ";
//            //   if(!año.equals("")){
//            //        t3= ("Es estudiante del "+año+" de la: ");
//            //    }
//            String t4 = " Tecnicatura Superior en " + carreraNombre;//(String)objcrud.consultaString("select carrera.carrera_nombre from carrera where Carrera.carrera_id='"+alumno_carrera+"';" , "carrera_nombre");
//            pTexto.setCharacterSpacing(charspacing(longitud, tamaño(t4, letra, negrita) + tamaño(t3, letra, normal), t3 + t4));//espacio entre caracteres
//            pTexto.setFont(normal, letra);
//            pTexto.showText(t3);
//            pTexto.newLineAtOffset(tamaño(t3, letra, normal) + t3.length() * charspacing(longitud, tamaño(t4, letra, negrita) + tamaño(t3, letra, normal), t3 + t4), 0);
//            pTexto.setFont(negrita, letra);
//            pTexto.showText(t4);
//            pTexto.setFont(normal, letra);
//            pTexto.newLineAtOffset(tamaño(t4, letra, negrita) + t4.length() * charspacing(longitud, tamaño(t4, letra, negrita) + tamaño(t3, letra, normal), t3 + t4), 0);
//            pTexto.newLineAtOffset(-tamaño(t3, letra, normal) - tamaño(t4, letra, negrita) - t3.length() * charspacing(longitud, tamaño(t4, letra, negrita) + tamaño(t3, letra, normal), t3 + t4) - t4.length() * charspacing(longitud, tamaño(t4, letra, negrita) + tamaño(t3, letra, normal), t3 + t4), n);////charspacing(longitud, tamaño(t3, letra, normal),t3+t4)-tamaño(t4,letra, PDType1Font.HELVETICA)-t4.length()*charspacing(longitud, tamaño(t3, letra, normal),t3+t4+t5),-20 );
//            String t6 = ("aprobada mediante Resolución Ministerial Nº " + resolucion + " con sede académica en el:");
//            pTexto.setFont(normal, letra);
//            pTexto.setCharacterSpacing(charspacing(longitud, tamaño(t6, letra, normal), t6));//espacio entre caracteres
//            pTexto.showText(t6);
//
//            pTexto.newLineAtOffset(0, n);//linea nueav
//            //nueva line=tamfrase
//            //String carrera=(String)objcrud.consultaString("select carrera.carrera_nombre from carrera where Carrera.carrera_id='"+alumno_carrera+"';" , "carrera_nombre");
//            String t8 = ("Instituto de Educación Superior Intercultural Campinta Guazú Gloria Pérez ");
//            //String t9=("DESARROLLO INDIGENA ");
//            pTexto.setCharacterSpacing(charspacing(longitud, tamaño(t8, letra, negrita), t8));//espacio entre caracteres
//            pTexto.setFont(negrita, letra);
//            pTexto.showText(t8);
//            pTexto.newLineAtOffset(0, n);
//            String t10 = ("----A la fecha aprobo los siguientes espacios curriculares:");
//            pTexto.setFont(normal, letra);
//            pTexto.showText(t10);
//            pTexto.endText();
//            pTexto.close();
//            PDPageContentStream cuadro = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
//            PDRectangle mediabox = Pagina.getMediaBox();
//            float width = mediabox.getWidth() - 4 * margin;
//            float X = mediabox.getLowerLeftX() + margin;
//            float Y = mediabox.getUpperRightY() - margin;
//            List<String> lineas = new ArrayList<String>();
//            margin = 60;
//            // starting y position is whole page height subtracted by top and bottom margin
//            float yStartNewPage = Pagina.getMediaBox().getHeight() - (2 * margin);
//            // we want table across whole page width (subtracted by left and right margin ofcourse)
//            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
//            boolean drawContent = true;
//            float yStart = 650;//yStartNewPage;
//            float bottomMargin = 70;
//            float auxmargin = 40;
//            // y position is your coordinate of top left corner of the table
//            float yPosition = 300;
//            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
//            int espaciado = 0;
//            cuadro.beginText();
//            cuadro.newLineAtOffset(0, 600);//X=40
//            Row<PDPage> headerRow = table.createRow(20);
//            int a = 5;
//            Cell<PDPage> cell = headerRow.createCell(5, "CURSO");
//            cell.setAlign(HorizontalAlignment.CENTER);
//            cell.setValign(VerticalAlignment.MIDDLE);
//            cell.setTextRotated(true);
//            System.out.println(cell.getHeight());
//            float h = cell.getInnerWidth();
//            cell = headerRow.createCell(5, "ORDEN");
//            cell.setAlign(HorizontalAlignment.CENTER);
//            cell.setValign(VerticalAlignment.MIDDLE);
//            cell.setTextRotated(true);
//            float b = cell.getInnerWidth();
//            cuadro.setCharacterSpacing(espaciado);
//            cell = headerRow.createCell(50, "ESPACIO CURRICULAR");
//            cell.setAlign(HorizontalAlignment.CENTER);
//            cell.setValign(VerticalAlignment.MIDDLE);
//            float c = cell.getExtraWidth();
//
//
//            cell = headerRow.createCell(15, "CONDICION");
//            cell.setAlign(HorizontalAlignment.CENTER);
//            cell.setValign(VerticalAlignment.MIDDLE);
//
//
//            cell = headerRow.createCell(25, "NOTA FINAL");
//            cell.setAlign(HorizontalAlignment.CENTER);
//            cell.setValign(VerticalAlignment.MIDDLE);
//            float r = cell.getInnerWidth();
//
//            cell = headerRow.createCell(10, "AÑO");
//            cell.setAlign(HorizontalAlignment.CENTER);
//            cell.setValign(VerticalAlignment.MIDDLE);
//
//            float d = cell.getExtraWidth();
//            cell.setFont(PDType1Font.HELVETICA);
//
//            //Row<PDPage> row = table.createRow(12);
//            int año = 0;
//
//            // int maux=objcrud.consultaInt("SELECT YEAR(CURDATE()) - YEAR(STR_TO_DATE(legajo_fecha, '%d/%m/%Y')) AS anio_cursando FROM legajo WHERE legajo_id = '"+libretaEstudiantil+"';","anio_cursando",true);
//            int maux = this.carreraService.obtenerDuracionCarrera(legajoId);
//            if (maux >= 3) { //si ya el años es mayor al q dura la ceerrea se considera que ya curoso l mayoria
//                año = 3;
//            } else {
//                año = maux;
//            }
//            table.draw();
//            BaseTable Cursoaño = new BaseTable(yStart - headerRow.getHeight() + 1, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
//            BaseTable Materiasaño = new BaseTable(yStart - headerRow.getHeight() + 1, yStartNewPage + 1, bottomMargin, tableWidth, 22.9f + auxmargin, Documento, Pagina, true, drawContent);
//            //LinkedList<materiaAnalitico> listaMaterias = new LinkedList<materiaAnalitico>();
//            //listaMaterias=objcrud.generarAnailitico(libretaEstudiantil);
//            float H = 0;
//            for (int i = 0; i < año; i++) {// primer for, este genera los años, es decir las materias que llevan cada año
////                System.out.println("*-*-*-*-*-*-**--*-" + i);
//                List<NotaMateriaDTO> listaMateriasyear = materiasyear(listaMaterias, i);
////                System.out.println("Lista de Materias=======================================:"+i);
////                for (NotaMateriaDTO notaMateria : listaMateriasyear) {
////                    System.out.println("Nota ID: " + notaMateria.getNotaId());
////                    System.out.println("Materia Orden: " + notaMateria.getMateriaOrden());
////                    System.out.println("Materia Nombre: " + notaMateria.getMateriaNombre());
////                    System.out.println("Nota Calificación Número: " + notaMateria.getNotaCalificacionNumero());
////                    System.out.println("Nota Calificación Letra: " + notaMateria.getNotaCalificacionLetra());
////                    System.out.println("Nota Condición: " + notaMateria.getNotaCondicion());
////                    System.out.println("Nota Estado: " + notaMateria.getNotaEstado());
////                    System.out.println("Nota Libro: " + notaMateria.getNotaLibro());
////                    System.out.println("Nota Folio: " + notaMateria.getNotaFolio());
////                    System.out.println("Nota Fecha: " + notaMateria.getNotaFecha());
////                    System.out.println("Nota Observaciones: " + notaMateria.getNotaObservaciones());
////                    System.out.println("Nota Usuario: " + notaMateria.getNotaUsuario());
////                    System.out.println("Nota Status: " + notaMateria.getNotaStatus());
////                    System.out.println("Nota Final: " + notaMateria.getNotaFinal());
////                    System.out.println("Correlativas: " + notaMateria.getCorrelativas());
////                    System.out.println("Materia ID: " + notaMateria.getMateriaId());
////                    System.out.println("Materia Nivel: " + notaMateria.getMateriaNivel());
////                    System.out.println("Cursada ID: " + notaMateria.getCursadaId());
////                    System.out.println("-----------------------------");
////                }
////
////                System.out.println(listaMateriasyear.size());
////                System.out.println(listaMateriasyear);
//                int materias = listaMateriasyear.size();
//                System.out.println("*-*-*-*-*se va a cargar el año-*-**--*-");
//                // Recorre la lista y muestra los elementos por pantalla
//                String x = "1ro";
//                Row<PDPage> raw = Cursoaño.createRow(materias * 19);
//                if (i == 1) {
//                    x = "2do";
//                } else if (i == 2) {
//                    x = "3ro";
//                }
//                float altura = 0;
//                //cell = raw.createCell(5, "x");//año
//                cell = raw.createCell(5, x);//año
//                cell.setAlign(HorizontalAlignment.CENTER);
//                cell.setValign(VerticalAlignment.MIDDLE);
//                cell.setFont(PDType1Font.HELVETICA);
//                cell.setTextRotated(true);
//                for (int j = 0; j < listaMateriasyear.size(); j++) {
//                    // Suponiendo que tienes una variable llamada notaFinal que contiene la nota final
//                    System.out.println("*-*-*-*-*se va a recorrer las materia*-**--*-" + j);
//                    int nk = 6;
//                    NotaMateriaDTO mat = listaMateriasyear.get(j);
//                    Row<PDPage> rew = Materiasaño.createRow(5);//19
//                    // Celda para la columna "Orden"
//                    System.out.println("Materia Orden" + mat.getMateriaOrden().toString());
//                    Cell<PDPage> cellOrden = rew.createCell(5.2f, mat.getMateriaOrden().toString());
//                    cellOrden.setAlign(HorizontalAlignment.CENTER);
//                    cellOrden.setValign(VerticalAlignment.MIDDLE);
//                    cellOrden.setFont(PDType1Font.HELVETICA);
//                    cellOrden.setFontSize(nk);
//                    // Celda para la columna "Nombre Materia"
//                    System.out.println("Materia Nombre" + mat.getMateriaNombre());
//                    Cell<PDPage> cellNombreMateria = rew.createCell(50, mat.getMateriaNombre());
//                    cellNombreMateria.setFontSize(nk);
//                    cellNombreMateria.setValign(VerticalAlignment.MIDDLE);
//                    cellNombreMateria.setFont(PDType1Font.HELVETICA);
//                    String condicion="";
//                    switch (mat.getNotaCondicion()) {
//                        case "Cursada":
//                      condicion="Promocion";
//                            System.out.println("Condición: Cursada");
//                            break;
//
//                        case "Examen Regular":
//                            condicion="Examen";
//                            System.out.println("Condición: Examen");
//                            break;
//
//                        case "Examen Libre":
//                            condicion="Examen";
//                            System.out.println("Condición: Examen");
//                            break;
//
//                        case "Equivalencia":
//                            condicion="Equivalencia";
//                            System.out.println("Condición: Equivalencia");
//                            break;
//
//                        default:
//                            System.out.println("Condición desconocida: " + mat.getNotaCondicion());
//                            break;
//                    }
//
//
//
//                    if(mat.getNotaFinal().equals("Desaprobado")){
//                        condicion="Desaprobado";
//                        mat.setNotaFinal("(-)");
//
//                    }
//
//                    if(mat.getNotaFinal().equals("(-)")){
//                        condicion=mat.getNotaFinal();
//                    }
//
//                    // Celda para la columna "Nombre Materia"
//                    Cell<PDPage> cellCondicionMateria = rew.createCell(15,condicion);
//                    cellCondicionMateria.setFontSize(nk);
//                    cellCondicionMateria.setValign(VerticalAlignment.MIDDLE);
//                    cellCondicionMateria.setAlign(HorizontalAlignment.CENTER);
//                    cellCondicionMateria.setFont(PDType1Font.HELVETICA);
//
//
//                    // Celda para la columna "Nota Final"
//                    Cell<PDPage> cellNotaFinal = rew.createCell(25, mat.getNotaFinal());
//
//                    cellNotaFinal.setFontSize(nk);
//                    cellNotaFinal.setAlign(HorizontalAlignment.CENTER);
//                    cellNotaFinal.setValign(VerticalAlignment.MIDDLE);
//                    cellNotaFinal.setFont(PDType1Font.HELVETICA);
//
//                    System.out.println("Aca esta el error de siempre" + mat.getNotaFecha());
//                    LocalDate fecha = mat.getNotaFecha();  // Asumiendo que getNotaFecha devuelve LocalDate
//                    int year = fecha.getYear();
//// Ahora puedes usar `year` como el año extraído de la fecha
//                    System.out.println("Materia año" + String.valueOf(year));
//                    String miYear=String.valueOf(year);
//
//                    if(mat.getNotaFinal().equals("(-)")){
//                        miYear=mat.getNotaFinal();
//                    }
//
//
//                    Cell<PDPage> cellyear = rew.createCell(10, miYear);
//                    //    Cell<PDPage> cellyear = rew.createCell(10, mat.getNotaFecha();
//                    cellyear.setFontSize(nk);
//                    cellyear.setAlign(HorizontalAlignment.CENTER);
//                    cellyear.setValign(VerticalAlignment.MIDDLE);
//                    cellyear.setFont(PDType1Font.HELVETICA);
//                    float filaHeight = rew.getHeight();
//                    altura = altura + filaHeight;
//                    if (!mat.getNotaFinal().equals("Desaprobado") && !mat.getNotaFinal().equals("Cursando") && !mat.getNotaFinal().equals("(-)")) {
//                        try {
//                            double nota = mat.getNotaCalificacionNumero();
//                            nuevoProm += nota;
//                            contProm++;
//                        } catch (NumberFormatException e) {
//                            // Manejar la excepción, por ejemplo, mostrar un mensaje de error o registrar el problema
//                            // System.err.println("Error al convertir la nota a entero: " + e.getMessage() + "MATERIA: " + mat.getNombre() + "Nota: " + mat.getNotaNumero());
//                        }
//                    }
//                }
//                H = H + altura;
//                raw.setHeight(altura);
//            }
//            Cursoaño.draw();
//            Materiasaño.draw();
//            cuadro.endText();
//            cuadro.close();
//            PDPageContentStream fin = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
//            fin.beginText();
//            fin.setFont(normal, letra);
//            float tam = 842 - H - 250;//Ajusta la altura
//            fin.newLineAtOffset(55, tam);//80,100
//            fin.setCharacterSpacing(0);
//            double resultado = nuevoProm / contProm;
//            String resultadoFormateado = String.format("%.2f", resultado);
//            String pr = "---------------------------------------------------Promedio: " + resultadoFormateado + "---";
//            fin.setCharacterSpacing(charspacing(longitud, tamaño(pr, letra, normal), pr));//espacio entre caracteres
//            fin.showText(pr);
//            fin.newLineAtOffset(0, -15);//linea nueav
//            String t14 = ("----Se extiende la presente constancia en la ciudad de ");
//            String ciudad = "San Salvador de Jujuy";
//            fin.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal) + tamaño(ciudad, letra, negrita), t14 + ciudad));//espacio entre caracteres
//            fin.showText(t14);
//            fin.setFont(negrita, letra);
//            fin.showText(ciudad);
//            fin.setFont(normal, letra);
//            fin.newLineAtOffset(0, -15);//linea nueav
//            String t15 = ("a los " + fechaAnalitico()) + " " + "para ser presentado";
//            //longitud=tamaño(t14,letra, PDType1Font.HELVETICA)+10+tamaño(ciudad,letra, negrita);
//            fin.setCharacterSpacing(charspacing(longitud, tamaño(t15, letra, normal), t15));//espacio entre caracteres
//            fin.showText(t15);
//            fin.newLineAtOffset(0, -15);
//            String t16 = "ante las autoridades del: ";
//            String t17 = autoridades;
//            if (t17.equals("que lo requieran ")) {
//                t16 = "ante las autoridades ";
//            }
//            String t1y = rellenar(t16 + t17, t17, letra, longitud);//Tada la frase, la palabra, longitud
//            fin.setCharacterSpacing(charspacing(longitud, tamaño(t16, letra, negrita) + tamaño(t1y, letra, normal), t1y + t16));//espacio entre caracteres
//            fin.setFont(normal, letra);
//            fin.showText(t16);
//            fin.setFont(normal, letra);
//            fin.showText(t1y);
//            fin.endText();
//            fin.close();
//            SimpleDateFormat form = new SimpleDateFormat("dd '-' MMMM '-' yyyy", new Locale("ES"));
//            Date fechaDatee = new Date();
//            String fec = form.format(fechaDatee);
//            // Documento.save(dir + ".pdf");
//            // Documento.close();
//        } catch (Exception e) {
//
//        }
//        return Documento;
//    }



    @Override
    public PDDocument generaAnalitico(String legajoId, String accion, String autoridades) {
        Persona persona = this.alumnoService.obtenerAlumnoPorLegajoId(legajoId);
        Carrera carrera = this.carreraService.obtenerCarreraPorLegajoId(legajoId);
        double nuevoProm = 0;
        int contProm = 0;
        List<NotaMateriaDTO> listaMaterias = new ArrayList<NotaMateriaDTO>();
        listaMaterias = this.notaService.obtenerTodasNotasPorLegajoAnalitico(legajoId);
//            System.out.println("Lista de Materias:");
//            for (NotaMateriaDTO notaMateria : listaMaterias) {
//                System.out.println("Nota ID: " + notaMateria.getNotaId());
//                System.out.println("Materia Orden: " + notaMateria.getMateriaOrden());
//                System.out.println("Materia Nombre: " + notaMateria.getMateriaNombre());
//                System.out.println("Nota Calificación Número: " + notaMateria.getNotaCalificacionNumero());
//                System.out.println("Nota Calificación Letra: " + notaMateria.getNotaCalificacionLetra());
//                System.out.println("Nota Condición: " + notaMateria.getNotaCondicion());
//                System.out.println("Nota Estado: " + notaMateria.getNotaEstado());
//                System.out.println("Nota Libro: " + notaMateria.getNotaLibro());
//                System.out.println("Nota Folio: " + notaMateria.getNotaFolio());
//                System.out.println("Nota Fecha: " + notaMateria.getNotaFecha());
//                System.out.println("Nota Observaciones: " + notaMateria.getNotaObservaciones());
//                System.out.println("Nota Usuario: " + notaMateria.getNotaUsuario());
//                System.out.println("Nota Status: " + notaMateria.getNotaStatus());
//                System.out.println("Nota Final: " + notaMateria.getNotaFinal());
//                System.out.println("Correlativas: " + notaMateria.getCorrelativas());
//                System.out.println("Materia ID: " + notaMateria.getMateriaId());
//                System.out.println("Materia Nivel: " + notaMateria.getMateriaNivel());
//                System.out.println("Cursada ID: " + notaMateria.getCursadaId());
//                System.out.println("-----------------------------");
//            }
        System.out.println(listaMaterias.size() + "Tamaño 1");
        PDImageXObject Iesc1, Iesc2, casilla0, casilla1;
        PDDocument Documento = new PDDocument();
        try {
            String carrera_id = carrera.getCarreraId();
            String carreraNombre = carrera.getCarreraNombre();
            int nMaterias = 0;//cantidad de matirias
            int materiasPrimero = this.materiaCarreraRepository.countMateriasPorNivel(carrera_id, "1ro");
            int materiasSegundo = this.materiaCarreraRepository.countMateriasPorNivel(carrera_id, "2do");
            int materiasTercero = this.materiaCarreraRepository.countMateriasPorNivel(carrera_id, "3ro");
            int n = -10;//distancia entre lineas
            int letra = 11;//Tamaño de letras
            Long dni = persona.getPersonaDni();
            String nombre = persona.getPersonaNombre();
            String apellido = persona.getPersonaApellido();
            String resolucion = carrera.getCarreraResolucion();
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
            //Creando documento nuevo
            PDPage Pagina = new PDPage(PDRectangle.A4);
            float margin = 25; //40

            //===============================================================================
            //  InputStream iesc1I = CRegular.class.getClassLoader().getResourceAsStream("Imagenes/logocoaj.png");
            //    if (iesc1I == null) {
            //        System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            //     }
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");

            //   InputStream iesc1I = CertificadoService.class.getClassLoader().getResourceAsStream("Imagenes/esc2.png");//iesc2I
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");//divujar desde el path
            // byte[] be = IOUtils.toByteArray(iesc2I);
            //     Iesc2 = PDImageXObject.createFromByteArray(Documento, be, "esc2.png");//divujar desde el path

            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);

            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);
            // Altura de la página
            float pageHeight = PDRectangle.A4.getHeight();

            // Ancho de la página
            float pageWidth = PDRectangle.A4.getWidth();

            // Texto para cada línea
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };

            // Comienza a escribir el texto
            contenido.beginText();

            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);

            for (String line : lines) {
                // Calcula el ancho de cada línea
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;

                // Calcula la posición x para centrar el texto
                float xStart = (pageWidth - textWidth) / 2;

                // Mueve la posición x
                contenido.newLineAtOffset(xStart, 0);

                // Escribe la línea
                contenido.showText(line);

                // Mueve a la siguiente línea
                contenido.newLineAtOffset(-xStart, n);
            }

            contenido.endText();
            contenido.close();

            //imagen del encavezado izquierda
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();
            //imagen derecha del envavezado
            //  PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //   PDesc2.moveTo(200, 100);//image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            //  PDesc2.drawImage(Iesc2, 510, 770, 60, 60);//Draw an image at the x,y coordinates, with the given size.
            //    PDesc2.close();

            //================================================
            PDPageContentStream titulo = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //texto de constancia
            n = -15;//distancia entre lineas
            titulo.beginText();
            titulo.setFont(PDType1Font.HELVETICA_BOLD, 10);
            titulo.newLineAtOffset(240, 750);//titulo/(250,745)
            titulo.showText("CONSTANCIA ANALITICO");
            titulo.newLineAtOffset(0, 0);
            titulo.showText("______________________");
            titulo.endText();
            titulo.close();
            PDPageContentStream pTexto = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //===================================================================
            //Justificar texto
            String genero1 = "";
            String genero = persona.getPersonaGenero();
            if (genero.equals("Masculino")) {
                genero1 = "el SR";
            } else {
                genero1 = "la Sra";
            }
            float longitud = 500;//longitud permitida para justificar
            pTexto.beginText();
            pTexto.setFont(normal, letra);
            pTexto.newLineAtOffset(55, 725);
            String t1 = ("-----Por la presente, se deja constancia que " + genero1 + " ");
            //String nombre=(String)objcrud.consultaString("select alumno.alumno_nombre from alumno where alumno.alumno_dni='"+alumno_dni+"';", "alumno_nombre");
            //String apellido=(String)objcrud.consultaString("select alumno.alumno_apellido from alumno where alumno.alumno_dni='"+alumno_dni+"';", "alumno_apellido");
            String t2 = (apellido + " " + nombre + ", DNI: " + dni);
            pTexto.setCharacterSpacing(charspacing(longitud, tamaño(t1, letra, normal) + tamaño(t2, letra, negrita), t1 + t2));//espacio entre caracteres
            pTexto.showText(t1);
            // pTexto.newLineAtOffset(tamaño(t1,letra, normal), 0);
            pTexto.newLineAtOffset(tamaño(t1, letra, normal) + t1.length() * charspacing(longitud, tamaño(t1, letra, normal) + tamaño(t2, letra, negrita), t1 + t2), 0);
            pTexto.setFont(negrita, letra);
            pTexto.showText(t2);
            //  float longitud=tamaño(t1+t2,letra, PDType1Font.HELVETICA)+10;//longitud permitida para justificar
            // float longitud=tamaño(t1+t2,letra, normal)+20;//longitud permitida para justificar

            pTexto.newLineAtOffset(-tamaño(t1, letra, normal) - t1.length() * charspacing(longitud, tamaño(t1, letra, normal) + tamaño(t2, letra, negrita), t1 + t2), n);//nueva linea abajo justo al inicio
            String t3 = accion;
            //String t3="ha cursado la carrera: ";
            //   if(!año.equals("")){
            //        t3= ("Es estudiante del "+año+" de la: ");
            //    }
            String t4 = " Tecnicatura Superior en " + carreraNombre;//(String)objcrud.consultaString("select carrera.carrera_nombre from carrera where Carrera.carrera_id='"+alumno_carrera+"';" , "carrera_nombre");
            pTexto.setCharacterSpacing(charspacing(longitud, tamaño(t4, letra, negrita) + tamaño(t3, letra, normal), t3 + t4));//espacio entre caracteres
            pTexto.setFont(normal, letra);
            pTexto.showText(t3);
            pTexto.newLineAtOffset(tamaño(t3, letra, normal) + t3.length() * charspacing(longitud, tamaño(t4, letra, negrita) + tamaño(t3, letra, normal), t3 + t4), 0);
            pTexto.setFont(negrita, letra);
            pTexto.showText(t4);
            pTexto.setFont(normal, letra);
            pTexto.newLineAtOffset(tamaño(t4, letra, negrita) + t4.length() * charspacing(longitud, tamaño(t4, letra, negrita) + tamaño(t3, letra, normal), t3 + t4), 0);
            pTexto.newLineAtOffset(-tamaño(t3, letra, normal) - tamaño(t4, letra, negrita) - t3.length() * charspacing(longitud, tamaño(t4, letra, negrita) + tamaño(t3, letra, normal), t3 + t4) - t4.length() * charspacing(longitud, tamaño(t4, letra, negrita) + tamaño(t3, letra, normal), t3 + t4), n);////charspacing(longitud, tamaño(t3, letra, normal),t3+t4)-tamaño(t4,letra, PDType1Font.HELVETICA)-t4.length()*charspacing(longitud, tamaño(t3, letra, normal),t3+t4+t5),-20 );
            String t6 = ("aprobada mediante Resolución Ministerial Nº " + resolucion + " con sede académica en el:");
            pTexto.setFont(normal, letra);
            pTexto.setCharacterSpacing(charspacing(longitud, tamaño(t6, letra, normal), t6));//espacio entre caracteres
            pTexto.showText(t6);

            pTexto.newLineAtOffset(0, n);//linea nueav
            //nueva line=tamfrase
            //String carrera=(String)objcrud.consultaString("select carrera.carrera_nombre from carrera where Carrera.carrera_id='"+alumno_carrera+"';" , "carrera_nombre");
            String t8 = ("Instituto de Educación Superior Intercultural Campinta Guazú Gloria Pérez ");
            //String t9=("DESARROLLO INDIGENA ");
            pTexto.setCharacterSpacing(charspacing(longitud, tamaño(t8, letra, negrita), t8));//espacio entre caracteres
            pTexto.setFont(negrita, letra);
            pTexto.showText(t8);
            pTexto.newLineAtOffset(0, n);
            String t10 = ("----A la fecha aprobo los siguientes espacios curriculares:");
            pTexto.setFont(normal, letra);
            pTexto.showText(t10);
            pTexto.endText();
            pTexto.close();
            PDPageContentStream cuadro = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDRectangle mediabox = Pagina.getMediaBox();
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            margin = 60;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (2 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 650;//yStartNewPage;
            float bottomMargin = 70;
            float auxmargin = 55;
            // y position is your coordinate of top left corner of the table
            float yPosition = 300;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            int espaciado = 0;
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 600);//X=40
            Row<PDPage> headerRow = table.createRow(20);
            int a = 5;
            Cell<PDPage> cell = headerRow.createCell(5, "CURSO");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setTextRotated(true);
            System.out.println(cell.getHeight());
            float h = cell.getInnerWidth();
            cell = headerRow.createCell(5, "ORDEN");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setTextRotated(true);
            float b = cell.getInnerWidth();
            cuadro.setCharacterSpacing(espaciado);
            cell = headerRow.createCell(60, "ESPACIO CURRICULAR");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float c = cell.getExtraWidth();
            cell = headerRow.createCell(25, "NOTA FINAL");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float r = cell.getInnerWidth();
            cell = headerRow.createCell(10, "AÑO");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);

            float d = cell.getExtraWidth();
            cell.setFont(PDType1Font.HELVETICA);

            //Row<PDPage> row = table.createRow(12);
            int año = 0;

            // int maux=objcrud.consultaInt("SELECT YEAR(CURDATE()) - YEAR(STR_TO_DATE(legajo_fecha, '%d/%m/%Y')) AS anio_cursando FROM legajo WHERE legajo_id = '"+libretaEstudiantil+"';","anio_cursando",true);
            int maux = this.carreraService.obtenerDuracionCarrera(legajoId);
            if (maux >= 3) { //si ya el años es mayor al q dura la ceerrea se considera que ya curoso l mayoria
                año = 3;
            } else {
                año = maux;
            }
            table.draw();
            BaseTable Cursoaño = new BaseTable(yStart - headerRow.getHeight() + 1, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            BaseTable Materiasaño = new BaseTable(yStart - headerRow.getHeight() + 1, yStartNewPage + 1, bottomMargin, tableWidth, 22.9f + auxmargin, Documento, Pagina, true, drawContent);
            //LinkedList<materiaAnalitico> listaMaterias = new LinkedList<materiaAnalitico>();
            //listaMaterias=objcrud.generarAnailitico(libretaEstudiantil);
            float H = 0;
            for (int i = 0; i < año; i++) {// primer for, este genera los años, es decir las materias que llevan cada año
//                System.out.println("*-*-*-*-*-*-**--*-" + i);
                List<NotaMateriaDTO> listaMateriasyear = materiasyear(listaMaterias, i);
//                System.out.println("Lista de Materias=======================================:"+i);
//                for (NotaMateriaDTO notaMateria : listaMateriasyear) {
//                    System.out.println("Nota ID: " + notaMateria.getNotaId());
//                    System.out.println("Materia Orden: " + notaMateria.getMateriaOrden());
//                    System.out.println("Materia Nombre: " + notaMateria.getMateriaNombre());
//                    System.out.println("Nota Calificación Número: " + notaMateria.getNotaCalificacionNumero());
//                    System.out.println("Nota Calificación Letra: " + notaMateria.getNotaCalificacionLetra());
//                    System.out.println("Nota Condición: " + notaMateria.getNotaCondicion());
//                    System.out.println("Nota Estado: " + notaMateria.getNotaEstado());
//                    System.out.println("Nota Libro: " + notaMateria.getNotaLibro());
//                    System.out.println("Nota Folio: " + notaMateria.getNotaFolio());
//                    System.out.println("Nota Fecha: " + notaMateria.getNotaFecha());
//                    System.out.println("Nota Observaciones: " + notaMateria.getNotaObservaciones());
//                    System.out.println("Nota Usuario: " + notaMateria.getNotaUsuario());
//                    System.out.println("Nota Status: " + notaMateria.getNotaStatus());
//                    System.out.println("Nota Final: " + notaMateria.getNotaFinal());
//                    System.out.println("Correlativas: " + notaMateria.getCorrelativas());
//                    System.out.println("Materia ID: " + notaMateria.getMateriaId());
//                    System.out.println("Materia Nivel: " + notaMateria.getMateriaNivel());
//                    System.out.println("Cursada ID: " + notaMateria.getCursadaId());
//                    System.out.println("-----------------------------");
//                }
//
//                System.out.println(listaMateriasyear.size());
//                System.out.println(listaMateriasyear);
                int materias = listaMateriasyear.size();
                System.out.println("*-*-*-*-*se va a cargar el año-*-**--*-");
                // Recorre la lista y muestra los elementos por pantalla
                String x = "1ro";
                Row<PDPage> raw = Cursoaño.createRow(materias * 19);
                if (i == 1) {
                    x = "2do";
                } else if (i == 2) {
                    x = "3ro";
                }
                float altura = 0;
                //cell = raw.createCell(5, "x");//año
                cell = raw.createCell(5, x);//año
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFont(PDType1Font.HELVETICA);
                cell.setTextRotated(true);
                for (int j = 0; j < listaMateriasyear.size(); j++) {
                    // Suponiendo que tienes una variable llamada notaFinal que contiene la nota final
                    System.out.println("*-*-*-*-*se va a recorrer las materia*-**--*-" + j);
                    int nk = 6;
                    NotaMateriaDTO mat = listaMateriasyear.get(j);
                    Row<PDPage> rew = Materiasaño.createRow(5);//19
                    // Celda para la columna "Orden"
                    System.out.println("Materia Orden" + mat.getMateriaOrden().toString());
                    Cell<PDPage> cellOrden = rew.createCell(5.2f, mat.getMateriaOrden().toString());
                    cellOrden.setAlign(HorizontalAlignment.CENTER);
                    cellOrden.setValign(VerticalAlignment.MIDDLE);
                    cellOrden.setFont(PDType1Font.HELVETICA);
                    cellOrden.setFontSize(nk);
                    // Celda para la columna "Nombre Materia"
                    System.out.println("Materia Nombre" + mat.getMateriaNombre());
                    Cell<PDPage> cellNombreMateria = rew.createCell(60, mat.getMateriaNombre());
                    cellNombreMateria.setFontSize(nk);
                    cellNombreMateria.setValign(VerticalAlignment.MIDDLE);
                    cellNombreMateria.setFont(PDType1Font.HELVETICA);
                    // Celda para la columna "Nota Final"
                    System.out.println("Materia Nota final" + mat.getNotaFinal() + ", " + mat.getNotaId());
                    Cell<PDPage> cellNotaFinal = rew.createCell(25, mat.getNotaFinal());

                    cellNotaFinal.setFontSize(nk);
                    cellNotaFinal.setAlign(HorizontalAlignment.CENTER);
                    cellNotaFinal.setValign(VerticalAlignment.MIDDLE);
                    cellNotaFinal.setFont(PDType1Font.HELVETICA);
                    System.out.println("Aca esta el error de siempre" + mat.getNotaFecha());
                    LocalDate fecha = mat.getNotaFecha();  // Asumiendo que getNotaFecha devuelve LocalDate
                    int year = fecha.getYear();
// Ahora puedes usar `year` como el año extraído de la fecha
                    System.out.println("Materia año" + String.valueOf(year));
                    Cell<PDPage> cellyear = rew.createCell(10, String.valueOf(year));
                    //    Cell<PDPage> cellyear = rew.createCell(10, mat.getNotaFecha();
                    cellyear.setFontSize(nk);
                    cellyear.setAlign(HorizontalAlignment.CENTER);
                    cellyear.setValign(VerticalAlignment.MIDDLE);
                    cellyear.setFont(PDType1Font.HELVETICA);
                    float filaHeight = rew.getHeight();
                    altura = altura + filaHeight;
                    if (!mat.getNotaFinal().equals("Desaprobado") && !mat.getNotaFinal().equals("Cursando") && !mat.getNotaFinal().equals("(-)")) {
                        try {
                            double nota = mat.getNotaCalificacionNumero();
                            nuevoProm += nota;
                            contProm++;
                        } catch (NumberFormatException e) {
                            // Manejar la excepción, por ejemplo, mostrar un mensaje de error o registrar el problema
                            // System.err.println("Error al convertir la nota a entero: " + e.getMessage() + "MATERIA: " + mat.getNombre() + "Nota: " + mat.getNotaNumero());
                        }
                    }
                }
                H = H + altura;
                raw.setHeight(altura);
            }
            Cursoaño.draw();
            Materiasaño.draw();
            cuadro.endText();
            cuadro.close();
            PDPageContentStream fin = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            fin.beginText();
            fin.setFont(normal, letra);
            float tam = 842 - H - 250;//Ajusta la altura
            fin.newLineAtOffset(55, tam);//80,100
            fin.setCharacterSpacing(0);
            double resultado = nuevoProm / contProm;
            String resultadoFormateado = String.format("%.2f", resultado);
            String pr = "---------------------------------------------------Promedio: " + resultadoFormateado + "---";
            fin.setCharacterSpacing(charspacing(longitud, tamaño(pr, letra, normal), pr));//espacio entre caracteres
            fin.showText(pr);
            fin.newLineAtOffset(0, -15);//linea nueav
            String t14 = ("----Se extiende la presente constancia en la ciudad de ");
            String ciudad = "San Salvador de Jujuy";
            fin.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal) + tamaño(ciudad, letra, negrita), t14 + ciudad));//espacio entre caracteres
            fin.showText(t14);
            fin.setFont(negrita, letra);
            fin.showText(ciudad);
            fin.setFont(normal, letra);
            fin.newLineAtOffset(0, -15);//linea nueav
            String t15 = ("a los " + fechaAnalitico()) + " " + "para ser presentado";
            //longitud=tamaño(t14,letra, PDType1Font.HELVETICA)+10+tamaño(ciudad,letra, negrita);
            fin.setCharacterSpacing(charspacing(longitud, tamaño(t15, letra, normal), t15));//espacio entre caracteres
            fin.showText(t15);
            fin.newLineAtOffset(0, -15);
            String t16 = "ante las autoridades del: ";
            String t17 = autoridades;
            if (t17.equals("que lo requieran ")) {
                t16 = "ante las autoridades ";
            }
            String t1y = rellenar(t16 + t17, t17, letra, longitud);//Tada la frase, la palabra, longitud
            fin.setCharacterSpacing(charspacing(longitud, tamaño(t16, letra, negrita) + tamaño(t1y, letra, normal), t1y + t16));//espacio entre caracteres
            fin.setFont(normal, letra);
            fin.showText(t16);
            fin.setFont(normal, letra);
            fin.showText(t1y);
            fin.endText();
            fin.close();
            SimpleDateFormat form = new SimpleDateFormat("dd '-' MMMM '-' yyyy", new Locale("ES"));
            Date fechaDatee = new Date();
            String fec = form.format(fechaDatee);
            // Documento.save(dir + ".pdf");
            // Documento.close();
        } catch (Exception e) {

        }
        return Documento;
    }

    private List<NotaMateriaDTO> materiasyear(List<NotaMateriaDTO> listaMaterias, int i) {
        List<NotaMateriaDTO> materiasFiltradas = new ArrayList<>();
        String H = "1ro";  // Asumimos por defecto 1ro
        switch (i) {
            case 1:
                H = "2do";
                break;
            case 2:
                H = "3ro";
                break;
            default:
                // Puedes agregar un mensaje de advertencia o manejar el caso de i no esperado
                System.out.println("Valor inesperado de i: " + i);
                break;
        }
        for (NotaMateriaDTO materia : listaMaterias) {
            System.out.println(materia.getMateriaNivel() + "-" + H);
            if (materia.getMateriaNivel().equals(H)) {
                System.out.println("agregado");
                materiasFiltradas.add(materia);
            }
        }
        return materiasFiltradas;
    }

    @Override
    public PDDocument generaExamen(Materia materia, String carrera, CursadaExamen cursadaExamen, String modalidad) {
        List<NotaExamenDTO> listaResultados = this.notaService.obtenerNotasPorCondicion(Long.valueOf(cursadaExamen.getId()), true, modalidad);
        PDImageXObject Iesc1, Iesc2;
        String division = "  ";
        String turno = "Vespertino";
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            //   InputStream iesc1I = notaCursada.class.getClassLoader().getResourceAsStream("Imagenes/logocoaj.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            //        InputStream iesc2I = notaCursada.class.getClassLoader().getResourceAsStream("Imagenes/esc2.png");
            //        if (iesc2I == null) {
            //            System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            //        }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");//divujar desde el path
            //  byte[] be = IOUtils.toByteArray(iesc2I);
            //   Iesc2 = PDImageXObject.createFromByteArray(Documento, be, "esc2.png");//divujar desde el path
            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);
            int n = -10;//distancia entre lineas
            contenido.beginText();
            contenido.setFont(PDType1Font.HELVETICA, 8);
            contenido.newLineAtOffset(200, 820);
            contenido.showText("INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL");
            contenido.newLineAtOffset(40, n);
            contenido.showText("“CAMPINTA GUAZU GLORIA PEREZ”");
            contenido.newLineAtOffset(-25, n);
            contenido.showText("Del Consejo de Organizaciones Aborígenes de Jujuy");
            contenido.newLineAtOffset(-6, n);
            contenido.showText("Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15");
            contenido.newLineAtOffset(-15, n);
            contenido.showText("Bahia Blanca Nº 235 Bº .Kennedy – Tel. Fax. N° (0388)-4237323");
            contenido.newLineAtOffset(-55, n);
            contenido.showText("(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy – Kollasuyu- República Argentina");
            contenido.newLineAtOffset(-108, n);
            contenido.showText("________________________________________________________________________________________________________________________");
            contenido.endText();
            contenido.close();
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();
            //      PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //       PDesc2.moveTo(200, 100);//image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            //       PDesc2.drawImage(Iesc2, 510, 770, 60, 60);//Draw an image at the x,y coordinates, with the given size.
            //       PDesc2.close();
            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            n = -18;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 15);
            regular.newLineAtOffset(40, 740);//titulo
            regular.showText("ACTA VOLANTE");
            regular.newLineAtOffset(0, n);
            regular.setFont(PDType1Font.HELVETICA, 10);
            regular.showText("INSTITUTO DE EDUCACION INTERCULTURAL CAMPINTA GUAZU GLORIA PEREZ");
            regular.newLineAtOffset(0, n);
            regular.showText("CARRERA: " + carrera);
            regular.newLineAtOffset(0, n);

            regular.showText("ASIGNATURA: " + materia.getMateriaNombre());
            regular.newLineAtOffset(0, n);
            regular.showText("EXAMEN DE ALUMNO: " + modalidad + "   AÑO: " + materia.getMateriaNivel() + "  DIVISON: " + division + "   TURNO: " + turno);

            regular.endText();
            regular.close();
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
            PDPageContentStream cuadro = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            margin = 60;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (margin);//- ( 2*margin)
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 650;//650yStartNewPage;
            float bottomMargin = 70;
            float auxmargin = 40;//55
            float yPosition = 300;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            int espaciado = 0;
            cuadro.beginText();
            cuadro.newLineAtOffset(-10, 600);//X=40
            Row<PDPage> headerRow = table.createRow(50);
            int a = 5;
            Cell<PDPage> cell = headerRow.createCell(10, "Numero de Orden");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFontSize(8);
            //  cell.setTextRotated(true);
            System.out.println(cell.getHeight());
            float h = cell.getInnerWidth();
            cell = headerRow.createCell(10, "Numero de Permiso");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFontSize(8);
            //cell.setTextRotated(true);
            float b = cell.getInnerWidth();
            cuadro.setCharacterSpacing(espaciado);
            cell = headerRow.createCell(15, "DNI");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFontSize(8);
            float c = cell.getExtraWidth();
            cell = headerRow.createCell(40, "APELLIDO Y NOMBRE");//30
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float r = cell.getInnerWidth();
            cell.setFontSize(8);
            /*
          fecha///////////////

             */

            BaseTable filafecha = new BaseTable(yStart + 64, yStartNewPage, bottomMargin, tableWidth, 457 + auxmargin, Documento, Pagina, true, drawContent);
            Row<PDPage> cabfilafecha = filafecha.createRow(15);
            Cell<PDPage> cabfilaabajofecha = cabfilafecha.createCell(7, "Libro");
            cabfilaabajofecha.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajofecha.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajofecha.setFontSize(8);
            cabfilaabajofecha = cabfilafecha.createCell(7, "Folio");
            cabfilaabajofecha.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajofecha.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajofecha.setFontSize(8);
            // Crear una nueva fila debajo de cabfilafecha
            Row<PDPage> nuevaFilaFecha = filafecha.createRow(20);
            Cell<PDPage> celdaDia = nuevaFilaFecha.createCell(7, "");
            celdaDia.setAlign(HorizontalAlignment.CENTER);
            celdaDia.setValign(VerticalAlignment.MIDDLE);
            celdaDia.setFontSize(5);
            Cell<PDPage> celdaMes = nuevaFilaFecha.createCell(7, "");
            celdaMes.setAlign(HorizontalAlignment.CENTER);
            celdaMes.setValign(VerticalAlignment.MIDDLE);
            celdaMes.setFontSize(8);
            filafecha.draw();
            //=======CALIFICACIONES Y BOLILLA*/
            // Calcular la posición yStart para la nueva tabla basado en la altura de la tabla anterior y un margen
            BaseTable calificaciones_bolillas = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, 355.6f + auxmargin, Documento, Pagina, true, drawContent);
            Row<PDPage> cabecalfbol = calificaciones_bolillas.createRow(8);
            Cell<PDPage> cab = cabecalfbol.createCell(21.2f, "CALIFICACIONES");
            cab.setAlign(HorizontalAlignment.CENTER);
            cab.setValign(VerticalAlignment.MIDDLE);
            cab.setFontSize(8);
            cab = cabecalfbol.createCell(14, "Nº de las Bolillas");
            cab.setAlign(HorizontalAlignment.CENTER);
            cab.setValign(VerticalAlignment.MIDDLE);
            cab.setFontSize(8);

            BaseTable fila = new BaseTable(yStart - 26, yStartNewPage, bottomMargin, tableWidth, 355.6f + auxmargin, Documento, Pagina, true, drawContent);
            Row<PDPage> cabfila = fila.createRow(24);//25
            Cell<PDPage> cabfilaabajo = cabfila.createCell(7.2f, "Escrito");
            cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajo.setFontSize(6);
            cabfilaabajo = cabfila.createCell(5, "Oral");
            cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajo.setFontSize(6);
            cabfilaabajo = cabfila.createCell(9, "Promedio");
            cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajo.setFontSize(6);
            cabfilaabajo = cabfila.createCell(7, "Escrito");
            cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajo.setFontSize(6);
            cabfilaabajo = cabfila.createCell(7, "Oral");
            cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajo.setFontSize(6);
            fila.draw();
            table.draw();
            calificaciones_bolillas.draw();
            BaseTable Cursoaño = new BaseTable(yStart - headerRow.getHeight() + 1, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            float H = 0;
            int al = listaResultados.size();
            for (int i = 0; i < 24; i++) {
                String dni = "-";
                String permiso_id = "-";
                String apellido = "-";
                String nota_c = "-";
                if (i < al) {
                    NotaExamenDTO fResultado = listaResultados.get(i);
                    permiso_id = fResultado.getPermisoId().toString(); // Ajusta al método correcto
                    dni = fResultado.getPersonaDni();
                    apellido = fResultado.getPersonaApellido() + ", " + fResultado.getPersonaNombre();
                    //   nota_c = fResultado.;
                }
                Row<PDPage> rew = Cursoaño.createRow(10);
                float altura = 0;
                cell = rew.createCell(10, String.valueOf(i + 1));//año
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFont(PDType1Font.HELVETICA);
                cell.setFontSize(8);
                Cell<PDPage> cellOrden = rew.createCell(10, permiso_id);
                cellOrden.setAlign(HorizontalAlignment.CENTER);
                cellOrden.setValign(VerticalAlignment.MIDDLE);
                cellOrden.setFont(PDType1Font.HELVETICA);
                cellOrden.setFontSize(8);
                // Celda para la columna "Nombre Materia"
                Cell<PDPage> cellNombreMateria = rew.createCell(15, dni);
                cellNombreMateria.setAlign(HorizontalAlignment.CENTER);
                cellNombreMateria.setValign(VerticalAlignment.MIDDLE);
                cellNombreMateria.setFont(PDType1Font.HELVETICA);
                cellNombreMateria.setFontSize(8);
                // Celda para la columna "Nota Final"
                Cell<PDPage> cellNotaFinal = rew.createCell(40, apellido);
                cellNotaFinal.setAlign(HorizontalAlignment.LEFT);
                cellNotaFinal.setValign(VerticalAlignment.MIDDLE);
                cellNotaFinal.setFont(PDType1Font.HELVETICA);
                cellNotaFinal.setFontSize(8);
                // Celda para la columna "Nota Final"
                Cell<PDPage> cellEscrito = rew.createCell(7, "");
                cellEscrito.setAlign(HorizontalAlignment.CENTER);
                cellEscrito.setValign(VerticalAlignment.MIDDLE);
                cellEscrito.setFont(PDType1Font.HELVETICA);
                // Celda para la columna "Nota Final"
                Cell<PDPage> cellOral = rew.createCell(5, "");
                cellOral.setAlign(HorizontalAlignment.CENTER);
                cellOral.setValign(VerticalAlignment.MIDDLE);
                cellOral.setFont(PDType1Font.HELVETICA);
                // Celda para la columna "Nota Final"
                Cell<PDPage> cellPromedio = rew.createCell(9, "");
                cellPromedio.setAlign(HorizontalAlignment.CENTER);
                cellPromedio.setValign(VerticalAlignment.MIDDLE);
                cellPromedio.setFont(PDType1Font.HELVETICA);
                // Celda para la columna "Nota Final"
                Cell<PDPage> cellBescrito = rew.createCell(7, "");
                cellBescrito.setAlign(HorizontalAlignment.CENTER);
                cellBescrito.setValign(VerticalAlignment.MIDDLE);
                cellBescrito.setFont(PDType1Font.HELVETICA);
                Cell<PDPage> cellBoral = rew.createCell(7, "");
                cellBoral.setAlign(HorizontalAlignment.CENTER);
                cellBoral.setValign(VerticalAlignment.MIDDLE);
                cellBoral.setFont(PDType1Font.HELVETICA);
                float filaHeight = rew.getHeight();
                H = H + filaHeight;
            }

            PDPageContentStream pie = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            n = -17;//distancia entre lineas
            pie.beginText();
            pie.setFont(PDType1Font.HELVETICA_BOLD, 12);
            pie.newLineAtOffset(40, yStart - H - 70);
            pie.setFont(PDType1Font.HELVETICA, 10);
            pie.showText("Presidente:___________________________________ Vocal:___________________Vocal______________");
            pie.newLineAtOffset(0, n);
            System.out.println("la fecha antes es: "+cursadaExamen.getFecha());
            pie.showText(fechaFija(cursadaExamen.getFecha()));
            n = -10;
            pie.newLineAtOffset(410, 0);
            pie.showText("Total de alumnos:______");
            pie.newLineAtOffset(25.5f, n);
            pie.showText(" Aprobados:______");
            pie.newLineAtOffset(4.5f, n);
            pie.showText("Aplazados:______");
            pie.newLineAtOffset(5, n);
            pie.showText("Ausentes:______");
            pie.endText();
            //=================================
            //=================================
            pie.close();
            Cursoaño.draw();
            cuadro.endText();
            cuadro.close();
            //  if (!dir.equals("IMP")) {
            //      Documento.save(dir + ".pdf");
            //  }
            //  Documento.close();
        } catch (IOException e) {
        }
        return Documento;
    }


    @Override
    public PDDocument generaTramite(String carreraId, String legajoId, String Autoridades) {

        String fecha = "";
        String materia = "";
        String calificacion = "";
        PDImageXObject Iesc1, Iesc2;
        Carrera carreraObj = this.carreraService.findCarreraById(carreraId);
        Persona persona = this.alumnoService.obtenerAlumnoPorLegajoId(legajoId);
        Legajo legajo = legajoService.findLegajoById(legajoId);
        NotaMateriaDTO ultimaNota = notaService.obtenerUltimaNota(legajoId);
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");//divujar desde el path
            // byte[] be = IOUtils.toByteArray(iesc2I);
            //     Iesc2 = PDImageXObject.createFromByteArray(Documento, be, "esc2.png");//divujar desde el path

            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);

            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);

// Altura de la página
            float pageHeight = PDRectangle.A4.getHeight();

// Ancho de la página
            float pageWidth = PDRectangle.A4.getWidth();

// Texto para cada línea
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };

// Distancia entre líneas
            int n = -10;

// Comienza a escribir el texto
            contenido.beginText();

            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);

            for (String line : lines) {
                // Calcula el ancho de cada línea
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;

                // Calcula la posición x para centrar el texto
                float xStart = (pageWidth - textWidth) / 2;

                // Mueve la posición x
                contenido.newLineAtOffset(xStart, 0);

                // Escribe la línea
                contenido.showText(line);

                // Mueve a la siguiente línea
                contenido.newLineAtOffset(-xStart, n);
            }

            contenido.endText();
            contenido.close();


            //imagen del encavezado izquierda
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();
            //imagen derecha del envavezado
            //  PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //   PDesc2.moveTo(200, 100);//image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            //  PDesc2.drawImage(Iesc2, 510, 770, 60, 60);//Draw an image at the x,y coordinates, with the given size.
            //    PDesc2.close();


            //================================================


            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //texto de constancia
            n = -10;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 10);
            regular.newLineAtOffset(200, 720);//titulo
            regular.showText("CONSTANCIA DE TITULO EN TRAMITE");
            regular.newLineAtOffset(0, 0);
            regular.showText("__________________________________");
            regular.endText();
            ///=============================================================
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            String hola = " __________________";
            String texto = Dividir(hola, width, letra);
            lineas = procesar(texto, letra);
            //===================================================================
            // Justificar texto
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
            //consulta

            String genero = persona.getPersonaGenero();
            String resolucion = carreraObj.getCarreraResolucion();
            String genero2 = "";
            String genero1 = "";
            if (genero.equals("Masculino")) {
                genero1 = "el SR";
                genero2 = "el interesado";
                System.out.println("Es Hombre");
            } else {
                genero1 = "la Sra";
                genero2 = "la interesada";
                System.out.println("Es Mujer");
            }
            regular.beginText();
            regular.setFont(normal, letra);
            regular.newLineAtOffset(50, 680);
            String t1 = ("-----Por la presente, la Rectora del ");
            String t2 = ("Instituto de Educación Superior Intercultural CAMPINTA ");
            //regular.setCharacterSpacing(charspacing(longitud,t1+t2,letra));//espacio entre caracteres
            regular.showText(t1);
            regular.newLineAtOffset(tamaño(t1, letra, normal), 0);
            regular.setFont(negrita, letra);
            //String t2=("Instituto de Educación Superior Intercultural CAMPINTA");
            regular.showText(t2);
            float longitud = tamaño(t1 + t2, letra, PDType1Font.HELVETICA) + 20;//longitud permitida para justificar
            regular.newLineAtOffset(-tamaño(t1, letra, normal), -20);//nueva linea abajo justo al inicio
            String t3 = ("GUAZU GLORIA PEREZ ");
            String t4 = ("Prof. Cristina Noemi Martínez, deja ");//CONSTANCIA que ");
            String t41 = ("constancia");
            String t5 = (" de que " + genero1);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41));//espacio entre caracteres
            regular.showText(t3);
            regular.setFont(PDType1Font.HELVETICA, letra);
            regular.newLineAtOffset(tamaño(t3, letra, negrita) + t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            //String t4=(" Prof. Cristina Noemi Martínez, deja CONSTANCIA que el Sr");
            regular.showText(t4);
            regular.newLineAtOffset(tamaño(t4, letra, normal) + t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t41);
            regular.newLineAtOffset(tamaño(t41, letra, normal) + t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t5);
            regular.newLineAtOffset(-tamaño(t3, letra, negrita) - tamaño(t41, letra, negrita) - tamaño(t4, letra, normal) - t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), -20);////charspacing(longitud, tamaño(t3, letra, normal),t3+t4)-tamaño(t4,letra, PDType1Font.HELVETICA)-t4.length()*charspacing(longitud, tamaño(t3, letra, normal),t3+t4+t5),-20 );
            String nombre = persona.getPersonaNombre();
            String apellido = persona.getPersonaApellido();
            String t6 = (apellido + " " + nombre + " D.N.I: " + persona.getPersonaDni() + " ");
            String t7 = (", ha acreditado la totalidad de los espacios ");
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, +tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7));//espacio entre caracteres
            regular.showText(t6);
            regular.newLineAtOffset(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t7);
            regular.newLineAtOffset(-(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7)), -20);//linea nueav
            String t10 = ("curriculares corrrespondientes al Plan de Estudios de la Carrera: ");
            String tec = "Tecnicatura Superior";
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t10, letra, normal) + tamaño(tec, letra, negrita), t10 + tec));//espacio entre caracteres
            regular.showText(t10);
            regular.setFont(negrita, letra);
            regular.showText(tec);
            regular.newLineAtOffset(0, -20);//linea nueav
            String carrera = "en " + carreraObj.getCarreraNombre();

            String resolucionBis = " aprobada mediante Resolución Ministerial: " + resolucion;


//            String t11=(" Estando el respectivo titulo en tramite");
//            String relleno=rellenar(carrera+t11,"",letra,longitud);
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(resolucionBis, letra, normal), carrera + resolucionBis));//espacio entre caracteres
            regular.showText(carrera);
            regular.setFont(normal, letra);
            regular.showText(resolucionBis);
//            regular.showText(relleno);
            regular.newLineAtOffset(0, -20);

            String mat1 = "Su última materia aprobada ha sido: " + ultimaNota.getMateriaNombre();
            String tmat1 = rellenar(mat1, mat1, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(tmat1, letra, normal), tmat1));//espacio entre caracteres
            regular.showText(tmat1);

            regular.newLineAtOffset(0, -20);

            String t11 = ("con calificación: " + ultimaNota.getNotaCalificacionNumero() + "(" + ultimaNota.getNotaCalificacionLetra() + ")" + ", el dia: " + formatearFechaCompleta(ultimaNota.getNotaFecha()) + ", encontrándose el título en trámite.");
            String relleno = rellenar(t11, "", letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t11, letra, normal), t11));//espacio entre caracteres
            regular.showText(t11);

            regular.newLineAtOffset(0, -20);

//
//
//            String validez = "Título: Técnico Superior en " + carreraObj.getCarreraNombre()
//                    + " – Carga horaria: " + carreraObj.getCarreraHorasReloj()
//                    + " – Validez: Nacional.";
//           // String rellenovalidez=rellenar(Validez,"",letra,longitud);
//            regular.setCharacterSpacing(charspacing(longitud, tamaño(validez, letra, normal), validez));//espacio entre caracteres
//            regular.showText(validez);
//
//
//
//
//            regular.newLineAtOffset(0,-20 );
            String t14 = ("-----Se expide la presente CONSTANCIA a solicitud de " + genero2 + "  y al solo efecto de ser");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal), t14));//espacio entre caracteres
            regular.setFont(normal, letra);
            regular.showText(t14);
            String t15 = ("presentada ante las Autoridades del ");
            String t16 = Autoridades;
            System.out.println("autoridades " + t16);
            if (t16.equals("que lo requieran")) {
                t15 = "presentada ante las Autoridades";
            }
            regular.setFont(normal, letra);
            t16 = rellenar(t15 + t16, t16, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, negrita), t15 + t16));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);
            regular.setFont(normal, letra);
            regular.showText(t15);
            // String t16=("B.E.G.U.P");
            regular.newLineAtOffset(tamaño(t15, letra, normal) + t15.length() * charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, normal), t15 + t16), 0);
            regular.setFont(normal, letra);
            regular.showText(t16);
            // String t17=("------------------SAN SALVADOR DE JUJUY, "+fecha());
            String t17 = ("-----SAN SALVADOR DE JUJUY, " + fecha() + "-------------");
            regular.newLineAtOffset(-(tamaño(t15, letra, normal) + (t15.length()) * charspacing(longitud, tamaño(t16, letra, normal) + tamaño(t15, letra, normal), t15 + t16)), -20);//linea nueav
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t17, letra, normal), t17));//espacio entre caracteres
            regular.showText(t17);
            regular.newLineAtOffset(0, -20);
            margin = 30;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (4 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = yStartNewPage + 30;
            float bottomMargin = 70;
            float yPosition = 550;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, Documento, Pagina, true, drawContent);
            Row<PDPage> headerRow = table.createRow(400);
            Cell<PDPage> cell = headerRow.createCell(100, " ");
            //cell.setText("hoal a atodsssss");
            table.draw();
            regular.endText();
            regular.close();
            //System.out.println("se divujo la imagen");
            // Documento.save(dir+".pdf");
            //Documento.close();
        } catch (IOException e) {
        }
        return Documento;
    }

    public static String formatearFechaCompleta(LocalDate fecha) {
        if (fecha == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        return fecha.format(formatter);
    }



    @Override
    public PDDocument generaCertificadoAsistencia(String alumnoDNI, String legajoId, String autoridades, String curso, String entrada, String salida, String fecT, String accion) {
        PDImageXObject Iesc1, Iesc2;
        Carrera carreraObj = this.carreraService.obtenerCarreraPorLegajoId(legajoId);
        Persona persona = this.alumnoService.findAlumnoById(alumnoDNI);
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");
            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);
            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);
            float pageHeight = PDRectangle.A4.getHeight();
            float pageWidth = PDRectangle.A4.getWidth();
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };
            int n = -10;
            contenido.beginText();
            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);

            for (String line : lines) {
                // Calcula el ancho de cada línea
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;

                // Calcula la posición x para centrar el texto
                float xStart = (pageWidth - textWidth) / 2;

                // Mueve la posición x
                contenido.newLineAtOffset(xStart, 0);

                // Escribe la línea
                contenido.showText(line);

                // Mueve a la siguiente línea
                contenido.newLineAtOffset(-xStart, n);
            }
            contenido.endText();
            contenido.close();
            //imagen del encavezado izquierda
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();
            //imagen derecha del envavezado
            //  PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //   PDesc2.moveTo(200, 100);//image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            //  PDesc2.drawImage(Iesc2, 510, 770, 60, 60);//Draw an image at the x,y coordinates, with the given size.
            //    PDesc2.close();


            //================================================
            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //texto de constancia
            n = -10;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 10);
            regular.newLineAtOffset(210, 720);//titulo
            regular.showText("CONSTANCIA DE ASISTENCIA A CLASES");
            regular.newLineAtOffset(0, 0);
            regular.showText("____________________________________");
            regular.endText();
            ///=============================================================
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            String hola = " ____________________";
            String texto = Dividir(hola, width, letra);
            lineas = procesar(texto, letra);
            //===================================================================
//Justificar texto
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;

            //consulta
            String genero = persona.getPersonaGenero();
            String genero2 = "";
            String genero1 = "";
            if (genero.equals("Masculino")) {
                genero1 = "el SR";
                genero2 = "el interesado";
                System.out.println("Es Hombre");
            } else {
                genero1 = "la Sra";
                genero2 = "la interesada";
                System.out.println("Es Mujer");
            }
            regular.beginText();
            regular.setFont(normal, letra);
            regular.newLineAtOffset(50, 680);
            String t1 = ("-----Por la presente, la Rectora del ");
            String t2 = ("Instituto de Educación Superior Intercultural CAMPINTA ");
            //regular.setCharacterSpacing(charspacing(longitud,t1+t2,letra));//espacio entre caracteres
            regular.showText(t1);
            regular.newLineAtOffset(tamaño(t1, letra, normal), 0);
            regular.setFont(negrita, letra);
            //String t2=("Instituto de Educación Superior Intercultural CAMPINTA");
            regular.showText(t2);
            float longitud = tamaño(t1 + t2, letra, PDType1Font.HELVETICA) + 20;//longitud permitida para justificar
            regular.newLineAtOffset(-tamaño(t1, letra, normal), -20);//nueva linea abajo justo al inicio
            String t3 = ("GUAZU GLORIA PEREZ ");
            String t4 = ("Prof. Cristina Noemi Martínez, deja ");//CONSTANCIA que ");
            String t41 = ("CONSTANCIA ");
            String t5 = ("que " + genero1);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41));//espacio entre caracteres
            regular.showText(t3);
            regular.setFont(PDType1Font.HELVETICA, letra);
            regular.newLineAtOffset(tamaño(t3, letra, negrita) + t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            //String t4=(" Prof. Cristina Noemi Martínez, deja CONSTANCIA que el Sr");
            regular.showText(t4);

            regular.newLineAtOffset(tamaño(t4, letra, normal) + t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t41);
            regular.newLineAtOffset(tamaño(t41, letra, normal) + t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t5);
            regular.newLineAtOffset(-tamaño(t3, letra, negrita) - tamaño(t41, letra, negrita) - tamaño(t4, letra, normal) - t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), -20);////charspacing(longitud, tamaño(t3, letra, normal),t3+t4)-tamaño(t4,letra, PDType1Font.HELVETICA)-t4.length()*charspacing(longitud, tamaño(t3, letra, normal),t3+t4+t5),-20 );

            String nombre = persona.getPersonaNombre();
            String apellido = persona.getPersonaApellido();

            String t6 = (apellido + " " + nombre + " D.N.I: " + persona.getPersonaDni() + " ");

            String t7 = (", estudiante del " + curso + " de la carrera:");
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, +tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7));//espacio entre caracteres
            regular.showText(t6);
            regular.newLineAtOffset(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t7);
            regular.newLineAtOffset(-(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7)), -20);//linea nueav

            String carrera = "Tecnicatura Superior en " + carreraObj.getCarreraNombre();
            String t10 = ("");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10));//espacio entre caracteres
            regular.setFont(negrita, letra);
            regular.showText(carrera);
            regular.newLineAtOffset(tamaño(carrera, letra, negrita) + carrera.length() * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t10);
            regular.newLineAtOffset(-(tamaño(carrera, letra, negrita) + (carrera.length()) * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10)), -20);//linea nueav
            String t12 = "";
            String tcond = fecT;
            int contadorComas = tcond.length() - tcond.replace(",", "").length();
            String losdias = "";
            if (contadorComas > 0) {
                losdias = " a clases los dias: ";
            } else {

                losdias = " a clases el dia: ";
            }


            String t11 = accion + losdias + fecT;
            String t13 = (", en el horario de :" + entrada + "-" + salida);
            t13 = rellenar(t11 + t12 + t13, t13, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13));//espacio entre caracteres
            regular.showText(t11);
            regular.newLineAtOffset(tamaño(t11, letra, normal) + t11.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t12);
            System.out.println(t13 + "//////////////////");
//String t13=("Año de este Instituto");
            regular.newLineAtOffset(tamaño(t12, letra, normal) + t12.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t13);
            String t14 = ("-----Se expide la presente CONSTANCIA a solicitud de " + genero2 + "  y al solo efecto de ser");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal), t14));//espacio entre caracteres
            regular.newLineAtOffset(-(tamaño(t11, letra, normal) + (t11.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)) - (tamaño(t12, letra, PDType1Font.HELVETICA) + (t12.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)), -20);//linea nueav

            regular.showText(t14);
            String t15 = ("presentada ante las Autoridades del ");
            String t16 = autoridades;
            System.out.println("autoridades " + t16);
            if (t16.equals("que lo requieran")) {
                t15 = "presentada ante las Autoridades";
            }

            t16 = rellenar(t15 + t16, t16, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, negrita), t15 + t16));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);
            regular.setFont(normal, letra);
            regular.showText(t15);
            // String t16=("B.E.G.U.P");
            regular.newLineAtOffset(tamaño(t15, letra, normal) + t15.length() * charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, normal), t15 + t16), 0);
            regular.setFont(normal, letra);
            regular.showText(t16);
            //String t17=("------------------SAN SALVADOR DE JUJUY, "+fecha());
            String t17 = ("-----SAN SALVADOR DE JUJUY, " + fecha() + "-------------");
            regular.newLineAtOffset(-(tamaño(t15, letra, normal) + (t15.length()) * charspacing(longitud, tamaño(t16, letra, normal) + tamaño(t15, letra, normal), t15 + t16)), -20);//linea nueav
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t17, letra, normal), t17));//espacio entre caracteres
            regular.showText(t17);
            regular.newLineAtOffset(0, -20);
            margin = 30;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (4 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = yStartNewPage + 30;
            float bottomMargin = 70;
            float yPosition = 550;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, Documento, Pagina, true, drawContent);
            Row<PDPage> headerRow = table.createRow(300);
            Cell<PDPage> cell = headerRow.createCell(100, " ");
            //cell.setText("hoal a atodsssss");
            table.draw();
            regular.endText();
            regular.close();
            //Documento.close();
        } catch (IOException e) {
        }
        return Documento;
    }

    private String formatearRol(String roleNombre) {
        if (roleNombre != null && roleNombre.startsWith("ROLE_")) {
            String limpio = roleNombre.substring(5).toLowerCase(); // Quita "ROLE_" y pasa a minúsculas
            return limpio.substring(0, 1).toUpperCase() + limpio.substring(1); // Capitaliza
        }
        return roleNombre; // Devuelve tal cual si no empieza con "ROLE_"
    }

    @Override
    public PDDocument generaFichaActualizacion(String legajoId) throws IOException {
        Persona persona = this.alumnoService.obtenerAlumnoPorLegajoId(legajoId);
        Legajo legajo = this.legajoService.findLegajoById(legajoId);
        Carrera carrera = this.carreraService.obtenerCarreraPorLegajoId(legajoId);
        String userName = userService.getAuthenticatedUser().get().getUserNombre();
        String userApellido = userService.getAuthenticatedUser().get().getUserApellido();
        String userRol = userService.getAuthenticatedUser().get().getRoles().get(0).getRoleNombre();
        String rolLimpio = formatearRol(userRol);
        System.out.println("Rol limpio: " + rolLimpio);

        PDDocument Documento = new PDDocument();
        PDPage Pagina = new PDPage(PDRectangle.A4);
        Documento.addPage(Pagina);
        PDImageXObject Iesc1, Iesc2;
        PDPageContentStream encabezado = new PDPageContentStream(Documento, Pagina);
        PDType1Font font = PDType1Font.HELVETICA;
        PDType1Font fontN = PDType1Font.HELVETICA_BOLD;
        float margin = 30;//
        PDRectangle mediabox = Pagina.getMediaBox();
        float width = mediabox.getWidth() - 4 * margin;
        float X = mediabox.getLowerLeftX() + margin;
        float Y = mediabox.getUpperRightY() - margin;
        List<String> lineas = new ArrayList<String>();
        float yStartNewPage = Pagina.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin ofcourse)
        float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float yStart = 820;//yStartNewPage;
        float bottomMargin = 40;
// y position is your coordinate of top left corner of the table
        float yPosition = 100;
        encabezado.beginText();
        int n = -9;
        encabezado.newLineAtOffset(180, 820);
        encabezado.setFont(font, 10);
        encabezado.showText("INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL");
        encabezado.newLineAtOffset(45, n);
        encabezado.showText("“CAMPINTA GUAZU GLORIA PEREZ”");
        encabezado.newLineAtOffset(0, n);
        encabezado.setFont(font, 7);
        encabezado.showText("Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15");
        encabezado.newLineAtOffset(-15, n);
        encabezado.showText("Bahia Blanca Nº 235 Bº .Kennedy – Tel. N° 0388-3428370");
        encabezado.newLineAtOffset(-45, n);
        encabezado.showText("(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy – Kollasuyu- República Argentina");
        encabezado.newLineAtOffset(-140, -5);
        encabezado.showText("____________________________________________________________________________________________________________________________________________");
        encabezado.newLineAtOffset(220, n - 5);//100
        encabezado.setFont(fontN, 10);
        encabezado.showText("Resumen de legajo");
        encabezado.newLineAtOffset(0, -1);
        encabezado.showText("_________________");
        encabezado.setFont(font, 10);
        encabezado.newLineAtOffset(-215, n - 5);//(xx,yy)//180
        // Obtener la fecha actual
        LocalDate fechaActual = LocalDate.now();
// Formatear la fecha según tus necesidades
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fechaFormateada = fechaActual.format(formatoFecha);

// Mostrar la fecha en el encabezado
        encabezado.showText("Hasta la Fecha: " + fechaFormateada + ", se registra la siguiente situcion en el legajo de el/la estudiante:");
        encabezado.endText();
        encabezado.close();
        InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
        if (iesc1I == null) {
            System.out.println("readFilesInBytes: File " + "file" + " does not exist");
        }
        byte[] ba = IOUtils.toByteArray(iesc1I);
        Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");//divujar desde el path

        //imagen del encavezado izquierda
        PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
        PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
        PDesc1.drawImage(Iesc1, 30, 780, 50, 50);//Draw an image at the x,y coordinates, with the given size.
        PDesc1.close();
        PDPageContentStream cuerpo = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
        cuerpo.beginText();
        cuerpo.setFont(font, 10);
        n = -15;
        cuerpo.newLineAtOffset(30, 730);//XX,YY
        cuerpo.showText("Apellido y Nombre: " + persona.getPersonaApellido() + ", " + persona.getPersonaNombre());
        cuerpo.newLineAtOffset(300, 0);
        cuerpo.showText("Libreta Estudiantil: " + legajoId);
        cuerpo.newLineAtOffset(-300, n);
        //cuerpo.newLineAtOffset(0,n);
        cuerpo.showText("DNI: " + persona.getPersonaDni());
        cuerpo.newLineAtOffset(30, -24);
        cuerpo.showText("1.- Fotocopia DNI");
        cuerpo.newLineAtOffset(300, 0);
        cuerpo.showText("5.- Planilla prontuarial actualizada.");
        cuerpo.newLineAtOffset(-300, n);
        cuerpo.showText("2.- Certificado de Nacimiento actualizado.");
        cuerpo.newLineAtOffset(300, 0);
        cuerpo.showText("6.– Carnet sanitario o Ficha de buena salud.");
        cuerpo.newLineAtOffset(-300, n);
        cuerpo.showText("3.– Fotocopia del título secundario autenticado.");
        cuerpo.newLineAtOffset(300, 0);
        cuerpo.showText("7.- Libreta.");
        cuerpo.newLineAtOffset(-300, n);
        cuerpo.showText("4.- Constancia de título en trámite actualizada.");
        cuerpo.newLineAtOffset(300, 0);
        cuerpo.showText("8. – foto 4 x 4.");

        int matriz[][] = new int[4][2];
        if (legajo.getLegajoFotocopiaDni().equals("Si")) {
            matriz[0][0] = 1;
        } else {
            matriz[0][0] = 0;
        }
        if (legajo.getLegajoPlanillaProntuarial().equals("Si")) {
            matriz[0][1] = 1;
        } else {
            matriz[0][1] = 0;
        }
        if (legajo.getLegajoCertificadoNacimiento().equals("Si")) {
            matriz[1][0] = 1;
        } else {
            matriz[1][0] = 0;
        }
        if (legajo.getLegajoCarnetSanitario().equals("Si")) {
            matriz[1][1] = 1;
        } else {
            matriz[1][1] = 0;
        }
        String indice = legajo.getLegajoFotocopiaTitulo();
        matriz[2][0] = 0;
        matriz[3][0] = 0;
        if (indice.equals("Secundario")) {
            matriz[2][0] = 1;
        } else if (indice.equals("Certificado Titulo en Tramite")) {
            matriz[3][0] = 1;
        }

        if (legajo.getLegajoAval().equals("Si")) {
            matriz[2][1] = 1;
        } else {
            matriz[2][1] = 0;
        }
        if (legajo.getLegajoFoto().equals("Si")) {
            matriz[3][1] = 1;
        } else {
            matriz[3][1] = 0;
        }



        PDImageXObject casilla0, casilla1;
        System.out.println("SE va a dibujar los cuadritos xd");
        InputStream cas0 = CertificadoServiceImpl.class.getClassLoader().getResourceAsStream("imagenes/casilla0.png");
        if (cas0 == null) {
            System.out.println("readFilesInBytes: File " + "file" + " does not exist");
        }
        InputStream cas1 = CertificadoServiceImpl.class.getClassLoader().getResourceAsStream("imagenes/casilla1.png");
        if (cas1 == null) {
            System.out.println("readFilesInBytes: File " + "file" + " does not exist");
        }
        byte[] bo = IOUtils.toByteArray(cas0);
        casilla0 = PDImageXObject.createFromByteArray(Documento, bo, "casilla0.png");//divujar desde el path
        byte[] bu = IOUtils.toByteArray(cas1);
        casilla1 = PDImageXObject.createFromByteArray(Documento, bu, "casilla1.png");//divujar desde el path
        System.out.println("SE va a dibujar los cuadritos xd y ahora s eva a recorrer el vector");

        int yy = 690;
        for (int i = 0; i < 4; i++) {
            System.out.println("funciana el primer for i:" + i);
            for (int j = 0; j < 2; j++) {
                System.out.println("funciana el segundo for" + j);
                System.out.println(matriz[i][j]);
                if (j == 0) {//izquierda

                    if (matriz[i][j] == 0) {//si es nulo
                        PDPageContentStream PDesc11 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
                        PDesc11.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
                        PDesc11.drawImage(casilla0, 45, yy, 10, 10);//Draw an image at the x,y coordinates, with the given size.
                        PDesc11.close();
                        System.out.println("Se divuja 1");
                    } else {//si es afirmativo
                        PDPageContentStream PDesc11 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
                        PDesc11.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
                        PDesc11.drawImage(casilla1, 45, yy, 10, 10);//Draw an image at the x,y coordinates, with the given size.
                        PDesc11.close();
                        System.out.println("Se divuja 2");
                    }
                } else {//derecha
                    if (matriz[i][j] == 0) {//si es nulo
                        PDPageContentStream PDesc11 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
                        PDesc11.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
                        PDesc11.drawImage(casilla0, 345, yy, 10, 10);//Draw an image at the x,y coordinates, with the given size.
                        PDesc11.close();
                        System.out.println("Se divuja 3");
                    } else {//si es afirmativo
                        PDPageContentStream PDesc11 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
                        PDesc11.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
                        PDesc11.drawImage(casilla1, 345, yy, 10, 10);//Draw an image at the x,y coordinates, with the given size.
                        PDesc11.close();
                        System.out.println("Se divuja 4");
                    }
                }
            }
            yy = yy - 15;
        }
//        cuerpo.newLineAtOffset(-300, -30);

        List<Aporte> aportes = this.aporteService.obtenerAportesDelAnioActualPorLegajo(legajoId)
                .stream()
                .filter(distinctByKeys(a -> Arrays.asList(
//                        a.getAporteFecha(), a.getAporteTalonarioRecibo(), a.getAporteNroRecibo(), a.getAporteMonto()
                        a.getAporteFecha(),  a.getAporteMonto()
                )))
                .collect(Collectors.toList());


        cuerpo.setFont(PDType1Font.HELVETICA_BOLD, 10);
        cuerpo.newLineAtOffset(-330, -20);
        cuerpo.showText("Pagos 2025:");
        if (aportes.isEmpty()) {
            cuerpo.setFont(PDType1Font.HELVETICA, 10);
            cuerpo.newLineAtOffset(0, n);
            cuerpo.showText("Sin pagos registrados en el año actual.");
        } else {
            for (Aporte aporte : aportes) {
                cuerpo.setFont(PDType1Font.HELVETICA, 10);
                cuerpo.newLineAtOffset(0, n); // Un pequeño margen para el detalle
//                String detalle = String.format("Fecha: %s - Recibo: %s/%s - Monto: $%s",
//                        aporte.getAporteFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
//                        aporte.getAporteTalonarioRecibo(),
//                        aporte.getAporteNroRecibo(),
//                        aporte.getAporteMonto());

                String detalle = String.format("Fecha: %s - Talonario: %s - Recibo: %s  ",
                        aporte.getAporteFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        "____",
                "____");
                cuerpo.showText(detalle);
            }
        }


        cuerpo.newLineAtOffset(300, 0);
        cuerpo.showText("__________________________________");

        cuerpo.newLineAtOffset(20, -15);
        cuerpo.showText(userApellido + ", " + userName + "-" + rolLimpio);

        cuerpo.newLineAtOffset(-320, -20);
        Optional<Observacione> observacion = observacionesService.obtenerUltimaObservacionPorLegajo(legajoId);
        if (observacion.isEmpty()) {
            cuerpo.showText("Observaciones: No registra------------------------------------------------");
        } else {
            String textoObservaciones = observacion.stream()
                    .map(Observacione::getObservaciones) // Obtener solo el texto
                    .filter(obs -> obs != null && !obs.isBlank()) // Evitar nulos o vacíos
                    .collect(Collectors.joining(" | ")); // Separar con " | " u otro separador

            cuerpo.showText("Observaciones: " + textoObservaciones);
        }


        cuerpo.endText();
        cuerpo.close();
        System.out.println("se divujo la tabla");
        // Documento.save(dir+".pdf");//VEr el tema del directorio
        //Documento.close();
        return Documento;
    }

    public static <T> Predicate<T> distinctByKeys(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }


    @Override
    public PDDocument generaPlanillaTutores(String carreraId, String estado, String ape) {
        Carrera carrera = this.carreraService.findCarreraById(carreraId);
        List<AlumnoLegajoInscripcionCarreraDTO> listado = this.alumnoLegajoService.obtenerAlumnosConCursadas(carreraId, estado, ape);
        PDImageXObject Iesc1, Iesc2;
        String año = carrera.getCarreraYear().toString();
        String tecnicatura = carrera.getCarreraNombre();
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }

            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");//divujar desde el path
            PDPage Pagina = new PDPage(PDRectangle.A4);
            //Pagina.setRotation(90);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);
            int n = -10;//distancia entre lineas
            contenido.beginText();
            contenido.setFont(PDType1Font.HELVETICA, 8);
            contenido.newLineAtOffset(200, 820);
            contenido.showText("INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL");
            contenido.newLineAtOffset(40, n);
            contenido.showText("“CAMPINTA GUAZU GLORIA PEREZ”");
            contenido.newLineAtOffset(-25, n);
            contenido.showText("Del Consejo de Organizaciones Aborígenes de Jujuy");
            contenido.newLineAtOffset(-6, n);
            contenido.showText("Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15");
            contenido.newLineAtOffset(-15, n);
            contenido.showText("Bahia Blanca Nº 235 Bº .Kennedy – Tel. Fax. N° (0388)-4237323");
            contenido.newLineAtOffset(-55, n);
            contenido.showText("(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy – Kollasuyu- República Argentina");
            contenido.newLineAtOffset(-108, n);
            contenido.showText("________________________________________________________________________________________________________________________");
            contenido.endText();
            contenido.close();
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();

            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            n = -18;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 15);
            regular.newLineAtOffset(40, 740);//titulo
            regular.showText("Planilla de Tutores");
            regular.newLineAtOffset(0, n);
            regular.setFont(PDType1Font.HELVETICA, 10);
            regular.showText("CARRERA: " + tecnicatura);
            regular.newLineAtOffset(0, n);
            String curso = "";
            int anioActual = java.time.Year.now().getValue();
            if (Integer.valueOf(carrera.getCarreraYear()) == anioActual) {
                curso = "1er año";
                System.out.println("1er año");
            } else if (Integer.valueOf(carrera.getCarreraYear()) == anioActual - 1) {
                curso = "2do año";
                System.out.println("2do año");
            } else if (Integer.valueOf(carrera.getCarreraYear()) == anioActual - 2) {
                curso = "3er año";
                System.out.println("3er año");
            }
            regular.showText("Curso: " + curso);

            regular.endText();
            regular.close();
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
            PDPageContentStream cuadro = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            margin = 60;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (1 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 660;//650yStartNewPage;
            float bottomMargin = 70;
            float auxmargin = 40;//55
            float yPosition = 300;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            int espaciado = 0;
            cuadro.beginText();
            cuadro.newLineAtOffset(-10, 660);//X=600
            Row<PDPage> headerRow = table.createRow(50);
            int a = 5;
            Cell<PDPage> cell = headerRow.createCell(7, "Nº");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFontSize(8);
            cuadro.setCharacterSpacing(espaciado);
            cell = headerRow.createCell(15, "DNI");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFontSize(8);
            cell = headerRow.createCell(40, "APELLIDO Y NOMBRE");//30
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFontSize(8);
            cell = headerRow.createCell(15, "Celular");//30
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFontSize(8);
            cell = headerRow.createCell(25, "Correo electronico");//30
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFontSize(8);

            table.draw();

            BaseTable Cursoaño = new BaseTable(yStart - headerRow.getHeight() + 1, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            float H = 0;
            int i = 0;
            for (AlumnoLegajoInscripcionCarreraDTO dto : listado) {
                i++;
                Long dni = dto.getAlumnoDni();
                String apellido = dto.getAlumnoApellido() + ", " + dto.getAlumnoNombre();
                String celular = dto.getDomicilioAlumnoCelular();
                String correo = dto.getDomicilioAlumnoCorreo();

                Row<PDPage> rew = Cursoaño.createRow(5);
                float altura = 0;
                cell = rew.createCell(7, String.valueOf(i));//año
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFont(PDType1Font.HELVETICA);
                cell.setFontSize(8);

                // Celda para la columna "Nombre Materia"
                Cell<PDPage> cellNombreMateria = rew.createCell(15, String.valueOf(dni));
                cellNombreMateria.setAlign(HorizontalAlignment.CENTER);
                cellNombreMateria.setValign(VerticalAlignment.MIDDLE);
                cellNombreMateria.setFont(PDType1Font.HELVETICA);
                cellNombreMateria.setFontSize(8);
                // Celda para la columna "Nota Final"
                Cell<PDPage> cellNotaFinal = rew.createCell(40, apellido);
                cellNotaFinal.setAlign(HorizontalAlignment.LEFT);
                cellNotaFinal.setValign(VerticalAlignment.MIDDLE);
                cellNotaFinal.setFont(PDType1Font.HELVETICA);
                cellNotaFinal.setFontSize(8);
                Cell<PDPage> cellFirma = rew.createCell(15, celular);
                cellFirma.setAlign(HorizontalAlignment.LEFT);
                cellFirma.setValign(VerticalAlignment.MIDDLE);
                cellFirma.setFont(PDType1Font.HELVETICA);
                cellFirma.setFontSize(8);
                Cell<PDPage> cellcorreo = rew.createCell(25, correo);
                cellcorreo.setAlign(HorizontalAlignment.LEFT);
                cellcorreo.setValign(VerticalAlignment.MIDDLE);
                cellcorreo.setFont(PDType1Font.HELVETICA);
                cellcorreo.setFontSize(8);
                // Celda para la columna "Nota Final"
                float filaHeight = rew.getHeight();
                H = H + filaHeight;
            }

            Cursoaño.draw();
            cuadro.endText();
            cuadro.close();
        } catch (IOException e) {
        }
        return Documento;
    }

    @Override
    public PDDocument generaPlanillaAsistencia(Long id) {
        MateriaCarrera materiaCarrera = this.materiaCarreraRepository.findById(id).get();
        System.out.println(materiaCarrera.getCarrera().getCarreraId() + "___________________");
        //List<AlumnoLegajoInscripcionCarreraDTO> listado = this.alumnoLegajoService.obtenerAlumnosLegajos(materiaCarrera.getCarrera().getCarreraId(), "Activo", "");
        List<AlumnoLegajoInscripcionCarreraDTO> listado = this.alumnoLegajoService.obtenerAlumnosMateriaCursadaId(id, "Activo");
        PDImageXObject Iesc1, Iesc2;
        String materia = materiaCarrera.getMateria().getMateriaNombre();
        Personal profesor = this.personalService.findById(materiaCarrera.getFmcDocente().toString()).get();
        String profe = profesor.getPersonalApellido() + ", " + profesor.getPersonalNombre();
        String modalidad = materiaCarrera.getMateria().getMateriaModalidad();
        String año = materiaCarrera.getMateria().getMateriaNivel();
        String division = materiaCarrera.getDivision();
        String turno = materiaCarrera.getTurno();
        String fechacierre = String.valueOf(materiaCarrera.getFecha());
        String tecnicatura = materiaCarrera.getCarrera().getCarreraNombre();
        String horario = materiaCarrera.getInicio() + "-" + materiaCarrera.getFin();
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");//divujar desde el path
            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);
            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);
            float pageHeight = PDRectangle.A4.getHeight();
            float pageWidth = PDRectangle.A4.getWidth();
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };
// Distancia entre líneas
            int n = -10;
// Comienza a escribir el texto
            contenido.beginText();
            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);
            for (String line : lines) {
                // Calcula el ancho de cada línea
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;
                // Calcula la posición x para centrar el texto
                float xStart = (pageWidth - textWidth) / 2;
                // Mueve la posición x
                contenido.newLineAtOffset(xStart, 0);
                // Escribe la línea
                contenido.showText(line);
                // Mueve a la siguiente línea
                contenido.newLineAtOffset(-xStart, n);
            }

            contenido.endText();
            contenido.close();

            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();
            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            n = -18;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 15);
            regular.newLineAtOffset(40, 740);//titulo
            regular.showText("Planilla de Asistencia");
            regular.newLineAtOffset(0, n);
            regular.setFont(PDType1Font.HELVETICA, 10);
            regular.showText("CARRERA: " + tecnicatura);
            regular.newLineAtOffset(0, n);
            regular.showText("UNIDAD CURRICULAR: " + materia);
            regular.newLineAtOffset(0, n);
            regular.showText("PROFESOR: " + profe);
            regular.newLineAtOffset(0, n);
            regular.showText("MES: " + getMesEnLetras() + "    Horario: " + horario + "     CURSO:" + año + "      DIVISON: " + division + "    TURNO: " + turno);

            regular.endText();
            regular.close();
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
            PDPageContentStream cuadro = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            margin = 60;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (1 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 660;//650yStartNewPage;
            float bottomMargin = 70;//70
            float auxmargin = 30;//40
            float yPosition = 300;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            int espaciado = 0;
            cuadro.beginText();
            cuadro.newLineAtOffset(-10, 600);//X=40
            Row<PDPage> headerRow = table.createRow(50);
            int a = 5;
            Cell<PDPage> cell = headerRow.createCell(7, "Nº");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFontSize(8);
            cuadro.setCharacterSpacing(espaciado);
            cell = headerRow.createCell(10, "DNI");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFontSize(8);
            cell = headerRow.createCell(35, "APELLIDO Y NOMBRE");//30
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFontSize(8);
            int celdas = 4;

            if (celdas == 1) {
                cell = headerRow.createCell(15, "Firma");//30
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFontSize(8);
            } else {
                for (int i = 0; i < celdas; i++) {
                    cell = headerRow.createCell(15, "___/___/_____");//30
                    cell.setAlign(HorizontalAlignment.CENTER);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(8);
                }

            }

            table.draw();

            BaseTable Cursoaño = new BaseTable(yStart - headerRow.getHeight() + 1, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            float H = 0;


//            int i=0;
//            for (AlumnoLegajoInscripcionCarreraDTO dto : listado) {
//                i++;
//                String dni = dto.getPersonaDni().toString(); // Asigna el valor de la segunda columna (dni) a dni
//                String apellido = dto.getAlumnoApellido()+", "+dto.getAlumnoNombre(); // Asigna el valor de la tercera columna (apellido+nombre) a apellido
//                //        nota_c = dto.;
//                if (i == listado.size()) {
//                    dni ="Docente";
//                    apellido = profesor.getPersonalApellido()+", "+profesor.getPersonalNombre();
//
//                }
//
//                Row<PDPage> rew = Cursoaño.createRow(5);
//                float altura = 0;
//                cell = rew.createCell(7, String.valueOf(i + 1));//aÃ±o
//                cell.setAlign(HorizontalAlignment.CENTER);
//                cell.setValign(VerticalAlignment.MIDDLE);
//                cell.setFont(PDType1Font.HELVETICA);
//                cell.setFontSize(8);
//
//                // Celda para la columna "Nombre Materia"
//                Cell<PDPage> cellNombreMateria = rew.createCell(10, dni);
//                cellNombreMateria.setAlign(HorizontalAlignment.CENTER);
//                cellNombreMateria.setValign(VerticalAlignment.MIDDLE);
//                cellNombreMateria.setFont(PDType1Font.HELVETICA);
//                cellNombreMateria.setFontSize(8);
//                // Celda para la columna "Nota Final"
//                Cell<PDPage> cellNotaFinal = rew.createCell(35, apellido);
//                cellNotaFinal.setAlign(HorizontalAlignment.LEFT);
//                cellNotaFinal.setValign(VerticalAlignment.MIDDLE);
//                cellNotaFinal.setFont(PDType1Font.HELVETICA);
//                cellNotaFinal.setFontSize(8);
//
//                if (celdas == 1) {
//                    Cell<PDPage> cellFirma = rew.createCell(15, "");
//                    cellFirma.setAlign(HorizontalAlignment.LEFT);
//                    cellFirma.setValign(VerticalAlignment.MIDDLE);
//                    cellFirma.setFont(PDType1Font.HELVETICA);
//                    cellFirma.setFontSize(8);
//                } else {
//                    for (int m = 0; m < celdas; m++) {
//                        Cell<PDPage> cellFirma = rew.createCell(15, "");
//                        cellFirma.setAlign(HorizontalAlignment.LEFT);
//                        cellFirma.setValign(VerticalAlignment.MIDDLE);
//                        cellFirma.setFont(PDType1Font.HELVETICA);
//                        cellFirma.setFontSize(8);
//                    }
//
//                }
//                float filaHeight = rew.getHeight();
//                H = H + filaHeight;
//            }

            int i = 0;

// 🔁 Recorrer alumnos
            for (AlumnoLegajoInscripcionCarreraDTO dto : listado) {
                String dni = dto.getAlumnoDni().toString();
                String apellido = dto.getAlumnoApellido() + ", " + dto.getAlumnoNombre();

                Row<PDPage> rew = Cursoaño.createRow(5);

                Cell<PDPage> cellNro = rew.createCell(7, String.valueOf(i + 1));
                cellNro.setAlign(HorizontalAlignment.CENTER);
                cellNro.setValign(VerticalAlignment.MIDDLE);
                cellNro.setFont(PDType1Font.HELVETICA);
                cellNro.setFontSize(8);

                Cell<PDPage> cellDni = rew.createCell(10, dni);
                cellDni.setAlign(HorizontalAlignment.CENTER);
                cellDni.setValign(VerticalAlignment.MIDDLE);
                cellDni.setFont(PDType1Font.HELVETICA);
                cellDni.setFontSize(8);

                Cell<PDPage> cellApellidoNombre = rew.createCell(35, apellido);
                cellApellidoNombre.setAlign(HorizontalAlignment.LEFT);
                cellApellidoNombre.setValign(VerticalAlignment.MIDDLE);
                cellApellidoNombre.setFont(PDType1Font.HELVETICA);
                cellApellidoNombre.setFontSize(8);

                for (int m = 0; m < celdas; m++) {
                    Cell<PDPage> cellFirma = rew.createCell(15, "");
                    cellFirma.setAlign(HorizontalAlignment.LEFT);
                    cellFirma.setValign(VerticalAlignment.MIDDLE);
                    cellFirma.setFont(PDType1Font.HELVETICA);
                    cellFirma.setFontSize(8);
                }

                H += rew.getHeight();
                i++; // Se incrementa después de procesar cada alumno
            }

// ➕ Fila del docente
            Row<PDPage> filaDocente = Cursoaño.createRow(5);

            Cell<PDPage> cellDocNro = filaDocente.createCell(7, String.valueOf(i + 1));
            cellDocNro.setAlign(HorizontalAlignment.CENTER);
            cellDocNro.setValign(VerticalAlignment.MIDDLE);
            cellDocNro.setFont(PDType1Font.HELVETICA);
            cellDocNro.setFontSize(8);

            Cell<PDPage> cellDocDni = filaDocente.createCell(10, "Docente");
            cellDocDni.setAlign(HorizontalAlignment.CENTER);
            cellDocDni.setValign(VerticalAlignment.MIDDLE);
            cellDocDni.setFont(PDType1Font.HELVETICA);
            cellDocDni.setFontSize(8);

            Cell<PDPage> cellDocNombre = filaDocente.createCell(35, profesor.getPersonalApellido() + ", " + profesor.getPersonalNombre());
            cellDocNombre.setAlign(HorizontalAlignment.LEFT);
            cellDocNombre.setValign(VerticalAlignment.MIDDLE);
            cellDocNombre.setFont(PDType1Font.HELVETICA);
            cellDocNombre.setFontSize(8);

            for (int m = 0; m < celdas; m++) {
                Cell<PDPage> cellFirma = filaDocente.createCell(15, "");
                cellFirma.setAlign(HorizontalAlignment.LEFT);
                cellFirma.setValign(VerticalAlignment.MIDDLE);
                cellFirma.setFont(PDType1Font.HELVETICA);
                cellFirma.setFontSize(8);
            }

            H += filaDocente.getHeight();


            PDPageContentStream pie = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            n = -17;//distancia entre lineas
            pie.beginText();
            pie.setFont(PDType1Font.HELVETICA_BOLD, 12);
            pie.newLineAtOffset(40, yStart - H - 70 - 20);
            pie.setFont(PDType1Font.HELVETICA, 9);
            pie.showText("* El estudiante que no se encuentre en la lista deben dirigirse a Mesa de Entrada para rectificar su inscripcion en esta materia.");
            //pie.newLineAtOffset(0, -20);
            //pie.showText("Aclartacion__________________________");
            pie.endText();

            //====================================================
            //====================================================
            pie.close();
            Cursoaño.draw();
            cuadro.endText();
            cuadro.close();

            //Documento.save(dir+".pdf");
            //  Documento.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return Documento;
    }

    public String getMesEnLetras() {
        LocalDate fechaActual = LocalDate.now();
        String mesEnLetras = fechaActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        return mesEnLetras;
    }


    @Override
    public PDDocument generaPlanilla(String carreraId, String materiaId, Boolean inscripto) {
        PDImageXObject Iesc1, Iesc2;
        MateriaCarrera materiaCarrera = this.materiaCarreraRepository.findByCarrera_CarreraIdAndMateria_MateriaId(carreraId, materiaId).get();
        String materia = materiaCarrera.getMateria().getMateriaNombre();
        String profe = String.valueOf(materiaCarrera.getFmcDocente());
        String modalidad = materiaCarrera.getMateria().getMateriaModalidad();
        String año = materiaCarrera.getMateria().getMateriaNivel();
        String division = materiaCarrera.getDivision();
        String turno = materiaCarrera.getTurno();
        String fechacierre = String.valueOf(materiaCarrera.getFecha());
        String tecnicatura = materiaCarrera.getCarrera().getCarreraNombre();
        String regimen = materiaCarrera.getMateria().getMateriaRegimen();
        List<NotaCursadaDTO> cursadas;
        if (materiaCarrera.getMateria().getMateriaNivel().equals("1ro")) {
            cursadas = this.notaService.findNotasByCarreraAndMateriaAll(carreraId, materiaId, inscripto);
        } else {
            cursadas = this.notaService.findNotasByCarreraAndMateria(carreraId, materiaId, inscripto);
        }
//        List<NotaCursadaDTO> cursadas=this.notaService.findNotasByCarreraAndMateria(carreraId, materiaId, inscripto);
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");//divujar desde el path
            PDPage Pagina = new PDPage(PDRectangle.A4);
            //Pagina.setRotation(90);
            Documento.addPage(Pagina);
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
            PDPageContentStream cuadro = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            margin = 60;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (3 * margin) - 20;
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 640;//660
            float bottomMargin = 70;
            float auxmargin = 40;//55
            float yPosition = 300;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            int espaciado = 0;
            cuadro.beginText();
            cuadro.newLineAtOffset(-10, 600);//X=40 Y=600
            Row<PDPage> headerRow = table.createRow(50);
            int a = 5;
            Cell<PDPage> cell = headerRow.createCell(5, "Nº");//7
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFontSize(8);
            //  cell.setTextRotated(true);
            System.out.println(cell.getHeight());
            float b = cell.getInnerWidth();
            cuadro.setCharacterSpacing(espaciado);
            cell = headerRow.createCell(10, "DNI");//15
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFontSize(8);
            float c = cell.getExtraWidth();
            cell = headerRow.createCell(40, "APELLIDO Y NOMBRE");//30
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float r = cell.getInnerWidth();
            cell.setFontSize(8);
            // Calcular la posición yStart para la nueva tabla basado en la altura de la tabla anterior y un margen---293.8f
            BaseTable calificaciones_bolillas = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, 260.7f + auxmargin, Documento, Pagina, true, drawContent);
            Row<PDPage> cabecalfbol = calificaciones_bolillas.createRow(27);
            Cell<PDPage> cab = cabecalfbol.createCell(58.2f, "CALIFICACIONES");//49
            cab.setAlign(HorizontalAlignment.CENTER);
            cab.setValign(VerticalAlignment.MIDDLE);
            cab.setFontSize(8);
            BaseTable fila = new BaseTable(yStart - 26, yStartNewPage, bottomMargin, tableWidth, 260.5f + auxmargin, Documento, Pagina, true, drawContent);
            Row<PDPage> cabfila = fila.createRow(24);//25

            Cell<PDPage> cabfilaabajo = cabfila.createCell(6.52f, "1er Parcial");//8.2f
            cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajo.setFontSize(6);
            cabfilaabajo = cabfila.createCell(6.5f, "2do Parcial");
            cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajo.setFontSize(6);
            cabfilaabajo = cabfila.createCell(8, "Trabajos Practicos");//8
            cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajo.setFontSize(6);
            cabfilaabajo = cabfila.createCell(8.2f, "Asistencia %");//9
            cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajo.setFontSize(6);
            cabfilaabajo = cabfila.createCell(8, "Concepto");//8
            cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajo.setFontSize(6);
            cabfilaabajo = cabfila.createCell(12, "Nota Final");//8
            cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajo.setFontSize(6);
            cabfilaabajo = cabfila.createCell(9, "Condicion");//8
            cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajo.setFontSize(6);


            fila.draw();
            table.draw();
            calificaciones_bolillas.draw();
            BaseTable Cursoaño = new BaseTable(yStart - headerRow.getHeight() + 1, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            float H = 0;
            for (int i = 0; i < cursadas.size(); i++) {
                String dni = "-";
                String apellido = "-";
                String nota_c = "-";
                String status = "-";
                if (i < cursadas.size()) {
                    NotaCursadaDTO fResultado = cursadas.get(i);

                    dni = fResultado.getPersonaDni(); // Asigna el valor de la segunda columna (dni) a dni
                    apellido = fResultado.getPersonaApellido() + "," + fResultado.getPersonaNombre(); // Asigna el valor de la tercera columna (apellido+nombre) a apellido
                    nota_c = String.valueOf(fResultado.getNotaCalificacionNotaNumero());
                    status = fResultado.getCursadaStatus();
                }
                Color color;
                color = Color.BLACK;//==============borrar
                String[] partes = regimen.split("-");
                String ultimaParte = partes[partes.length - 1].trim(); // "EXAMEN FINAL"
                System.out.println("Estatus vale:  " + status);

                if ("Provisoria".equals(status) && (!ultimaParte.equals("EXAMEN FINAL"))) {
                    System.out.println("::::::" + regimen);
                    color = Color.RED;
                } else {
                    color = Color.BLACK;
                }

                Row<PDPage> rew = Cursoaño.createRow(5);
                float altura = 0;

                cell = rew.createCell(5, String.valueOf(i + 1));//año
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFont(PDType1Font.HELVETICA);
                cell.setFontSize(8);
                cell.setTextColor(color);

                // Celda para la columna "Nombre Materia"
                Cell<PDPage> cellNombreMateria = rew.createCell(10, dni);
                cellNombreMateria.setAlign(HorizontalAlignment.CENTER);
                cellNombreMateria.setValign(VerticalAlignment.MIDDLE);
                cellNombreMateria.setFont(PDType1Font.HELVETICA);
                cellNombreMateria.setFontSize(8);
                cellNombreMateria.setTextColor(color);
                // Celda para la columna "Nota Final"
                Cell<PDPage> cellNotaFinal = rew.createCell(40, apellido);
                cellNotaFinal.setAlign(HorizontalAlignment.LEFT);
                cellNotaFinal.setValign(VerticalAlignment.MIDDLE);
                cellNotaFinal.setFont(PDType1Font.HELVETICA);
                cellNotaFinal.setTextColor(color);

                // Celda para la columna "Nota Final"
                Cell<PDPage> cellEscrito = rew.createCell(6.37f, "");
                cellEscrito.setAlign(HorizontalAlignment.CENTER);
                cellEscrito.setValign(VerticalAlignment.MIDDLE);
                cellEscrito.setFont(PDType1Font.HELVETICA);
                cellEscrito.setTextColor(color);
                // Celda para la columna "Nota Final"
                Cell<PDPage> cellOral = rew.createCell(6.5f, "");
                cellOral.setAlign(HorizontalAlignment.CENTER);
                cellOral.setValign(VerticalAlignment.MIDDLE);
                cellOral.setFont(PDType1Font.HELVETICA);
                cellOral.setTextColor(color);
                // Celda para la columna "Nota Final"
                Cell<PDPage> cellPromedio = rew.createCell(8, "");
                cellPromedio.setAlign(HorizontalAlignment.CENTER);
                cellPromedio.setValign(VerticalAlignment.MIDDLE);
                cellPromedio.setFont(PDType1Font.HELVETICA);
                cellPromedio.setTextColor(color);
                // Celda para la columna "Nota Final"
                Cell<PDPage> cellBescrito = rew.createCell(8.2f, "");
                cellBescrito.setAlign(HorizontalAlignment.CENTER);
                cellBescrito.setValign(VerticalAlignment.MIDDLE);
                cellBescrito.setFont(PDType1Font.HELVETICA);
                cellBescrito.setTextColor(color);
                Cell<PDPage> cellBoral = rew.createCell(8, "");//8
                cellBoral.setAlign(HorizontalAlignment.CENTER);
                cellBoral.setValign(VerticalAlignment.MIDDLE);
                cellBoral.setFont(PDType1Font.HELVETICA);
                cellBoral.setTextColor(color);
                Cell<PDPage> cellcond = rew.createCell(12, "");//8
                cellcond.setAlign(HorizontalAlignment.CENTER);
                cellcond.setValign(VerticalAlignment.MIDDLE);
                cellcond.setFont(PDType1Font.HELVETICA);
                cellcond.setTextColor(color);
                Cell<PDPage> cellcond1 = rew.createCell(9, "");//8
                cellcond1.setAlign(HorizontalAlignment.CENTER);
                cellcond1.setValign(VerticalAlignment.MIDDLE);
                cellcond1.setFont(PDType1Font.HELVETICA);
                cellcond1.setTextColor(color);


                float filaHeight = rew.getHeight();
                H = H + filaHeight;
            }

            /*
            PDPageContentStream pie=new PDPageContentStream(Documento,Pagina, PDPageContentStream.AppendMode.APPEND, true);
            n=-17;//distancia entre lineas
            pie.beginText();
            pie.setFont(PDType1Font.HELVETICA_BOLD, 12);
            pie.newLineAtOffset(40,60);
            pie.setFont(PDType1Font.HELVETICA, 10);
            pie.showText("Docente:________________________________________________________Firma:___________________________");
            pie.newLineAtOffset(0,n );
            pie.showText("---------------------------------------------------SAN SALVADOR DE JUJUY, __________de_________________del 20______");
            n=-10;

            pie.endText();
            pie.close();
             */
            Cursoaño.draw();
            cuadro.endText();
            cuadro.close();

            int i = 1;
            Personal personal = this.personalService.findById(String.valueOf(materiaCarrera.getFmcDocente())).get();

            for (PDPage page : Documento.getPages()) {
                addHeader(Documento, page, Iesc1, materiaCarrera, personal);
                addFooter(Documento, page, i, Documento.getNumberOfPages());
                i++;
            }

            int numPagina = 0;
            String[] parts = regimen.split("-");
            if (parts.length > 1) {
                String cond = parts[1].trim(); // Obtener la parte después del guion y quitar espacios

                // Usar switch para evaluar el valor de cond
                switch (cond) {
                    case "PROMOCION":
                        numPagina = 1;
                        System.out.println("La condición es: PROMOCION");
                        break;
                    case "EXAMEN FINAL":
                        numPagina = 2;
                        System.out.println("La condición es: EXAMEN FINAL");
                        break;
                    case "PROMOCION/EXAMEN FINAL":
                        numPagina = 0;
                        System.out.println("La condición es: PROMOCION/EXAMEN FINAL");
                        break;

                }
            } else {
                System.out.println("No se encontró un guion en la cadena.");
            }

            //  String newPdfPath = "pdf/regPlanillas.pdf"; // Ruta del nuevo PDF en el paquete
            //  InputStream newDocumentStream = CertificadoServiceImpl.class.getClassLoader().getResourceAsStream(newPdfPath);
            //   PDDocument newDocument = PDDocument.load(newDocumentStream);
            //   PDPage pageToAdd = newDocument.getPage(numPagina); // Obtener la página
            //    PDDocument tempDocument = new PDDocument();
            //  tempDocument.addPage(pageToAdd);

            //Documento.addPage(pageToAdd);
            //    for (PDPage page : Documento.getPages()) {
            //    tempDocument.addPage(page);
            //    }

            //tempDocument.save(dir + ".pdf");
            //Documento.save(dir+".pdf");
            //Documento.close();
            //    tempDocument.close();
        } catch (IOException e) {
        }
        return Documento;
    }

    private static void addFooter(PDDocument document, PDPage page, int i, int total) {
        try {
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
            float margin = 40;
            float yPosition = 60;
            float fontSize = 10;
            float leading = 20; // Espacio entre líneas
            contentStream.beginText();
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Docente:________________________________________________________Firma:____________________________");
            contentStream.newLineAtOffset(0, -leading);
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.showText("-----------------------------------------------------SAN SALVADOR DE JUJUY, __________de_________________del 20______");
            contentStream.newLineAtOffset(0, -leading);
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.showText("Pagina " + i + "/" + total);
            contentStream.endText();

            contentStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addHeader(PDDocument document, PDPage page, PDImageXObject image, MateriaCarrera materiaCarrera, Personal personal) {

        try {
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
            int n = -10;
            contentStream.drawImage(image, 30, 770, 60, 60);
            contentStream.beginText();
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            contentStream.newLineAtOffset(200, 820);
            contentStream.showText("INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL");
            contentStream.newLineAtOffset(40, n);
            contentStream.showText("“CAMPINTA GUAZU GLORIA PEREZ”");
            contentStream.newLineAtOffset(-25, n);
            contentStream.showText("Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15");
            contentStream.newLineAtOffset(-1, n);
            contentStream.showText("Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370");
            contentStream.newLineAtOffset(-50, n);
            contentStream.showText("(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina");
            contentStream.newLineAtOffset(-120, n);
            contentStream.showText("________________________________________________________________________________________________________________________");
            contentStream.endText();
            contentStream.close();
            PDPageContentStream regular = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
            n = -18;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 15);
            regular.newLineAtOffset(40, 740);//titulo
            regular.showText("Planilla de seguimiento");
            regular.newLineAtOffset(0, n);
            regular.setFont(PDType1Font.HELVETICA, 10);
            regular.showText("CARRERA: " + materiaCarrera.getCarrera().getCarreraNombre());
            regular.newLineAtOffset(0, n);
            regular.showText("PROFESOR: " + personal.getPersonalApellido() + ", " + personal.getPersonalNombre());
            regular.newLineAtOffset(0, n);
            regular.showText("UNIDAD CURRICULAR: " + materiaCarrera.getMateria().getMateriaNombre());
            regular.newLineAtOffset(0, n);
            regular.showText("REGIMEN: " + materiaCarrera.getMateria().getMateriaRegimen() + " - " + materiaCarrera.getMateria().getMateriaModalidad());
            regular.newLineAtOffset(0, n);
            regular.showText("FECHA: " + materiaCarrera.getFecha() + "     CURSO:" + materiaCarrera.getMateria().getMateriaNivel() + "      DIVISION: " + materiaCarrera.getDivision() + "    TURNO: " + materiaCarrera.getTurno());
            regular.endText();
            regular.close();
            float margin = 20;
            float yStartNewPage = page.getMediaBox().getHeight() - (3 * margin) - 20;
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 650;//660
            float bottomMargin = 70;
            float auxmargin = 40;//55
            float yPosition = 300;
            BaseTable filafecha = new BaseTable(yStart + 100, yStart + 100, bottomMargin, tableWidth, 425 + auxmargin, document, page, true, drawContent);
            Row<PDPage> cabfilafecha = filafecha.createRow(20);
            Cell<PDPage> cabfilaabajofecha = cabfilafecha.createCell(10, "Libro");
            cabfilaabajofecha.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajofecha.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajofecha.setFontSize(8);
            cabfilaabajofecha = cabfilafecha.createCell(10, "Folio");
            cabfilaabajofecha.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajofecha.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajofecha.setFontSize(8);
            // Crear una nueva fila debajo de cabfilafecha
            Row<PDPage> nuevaFilaFecha = filafecha.createRow(20);
            Cell<PDPage> celdaDia = nuevaFilaFecha.createCell(10, "");
            celdaDia.setAlign(HorizontalAlignment.CENTER);
            celdaDia.setValign(VerticalAlignment.MIDDLE);
            celdaDia.setFontSize(5);
            Cell<PDPage> celdaMes = nuevaFilaFecha.createCell(10, "");
            celdaMes.setAlign(HorizontalAlignment.CENTER);
            celdaMes.setValign(VerticalAlignment.MIDDLE);
            celdaMes.setFontSize(8);
            filafecha.draw();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generarCodigoDeBarrasBase64(String texto) {
        try {
            Code128Writer barcodeWriter = new Code128Writer();
            BitMatrix bitMatrix = barcodeWriter.encode(texto, BarcodeFormat.CODE_128, 300, 80);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Error generando código de barras", e);
        }
    }


    public String generarQRBase64(String contenido) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE, 150, 150);

            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);

            byte[] imageBytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);

        } catch (WriterException | IOException e) {
            throw new RuntimeException("Error generando QR", e);
        }
    }

    @Override
    public byte[] generarCredencialEstudiantil(String legajoId) {
        try {
            Persona persona = alumnoService.obtenerAlumnoPorLegajoId(legajoId);
            Legajo legajo = legajoService.findLegajoById(legajoId);
            Carrera carrera = carreraService.obtenerCarreraPorLegajoId(legajoId);
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("nombre", persona.getPersonaApellido() + ", " + persona.getPersonaNombre());
            templateData.put("dni", persona.getPersonaDni());
            templateData.put("legajoId", legajoId);
            templateData.put("carrera", carrera.getCarreraNombre());
            templateData.put("telefono", persona.getPersonaDomicilioCelular());
            templateData.put("correo", persona.getPersonaCorreo());
            templateData.put("emision", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            templateData.put("fotocopia_dni", "Si".equalsIgnoreCase(legajo.getLegajoFotocopiaDni()) ? "✔" : "✘");
            templateData.put("cert_nacimiento", "Si".equalsIgnoreCase(legajo.getLegajoCertificadoNacimiento()) ? "✔" : "✘");
            templateData.put("carnet_sanitario", "Si".equalsIgnoreCase(legajo.getLegajoCarnetSanitario()) ? "✔" : "✘");
            templateData.put("titulo_secundario",
                    "Secundario".equalsIgnoreCase(legajo.getLegajoFotocopiaTitulo()) ? "✔" :
                            "Constancia Titulo Tramite".equalsIgnoreCase(legajo.getLegajoFotocopiaTitulo()) ? "⏳" : "✘");
            templateData.put("foto", "Si".equalsIgnoreCase(legajo.getLegajoFoto()) ? "✔" : "✘");
            Documento documento = documentoService.findByEntidadIdAndTipoDocumento(legajo.getLegajoId(), "fotoId").get();
            System.out.println("LA ruta es: " + documento.getRuta());
            byte[] bytes = Files.readAllBytes(Paths.get(documento.getRuta()));
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String fotoBase64 = "data:image/png;base64," + base64;
            templateData.put("photo", fotoBase64);
            try {
                templateData.put("codigo_barras", generarCodigoDeBarrasBase64(legajoId));
            } catch (Exception e) {
                System.err.println("Error al generar código de barras:");
                e.printStackTrace();
                templateData.put("codigo_barras", "");
            }
            try {
                templateData.put("qr_base64", generarQRBase64(legajoId));
            } catch (Exception e) {
                System.err.println("Error al generar QR:");
                e.printStackTrace();
                templateData.put("qr_base64", "");
            }
            String html;
            try {
                html = htmlService.procesarHtmlPlano("credencialEstudiantil", templateData);
            } catch (Exception e) {
                System.err.println("Error al procesar el HTML con la plantilla:");
                e.printStackTrace();
                throw new RuntimeException("Fallo en el procesamiento de la plantilla HTML", e);
            }

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ITextRenderer renderer = new ITextRenderer();

                try {
                    String fontPath = getClass().getClassLoader().getResource("fuente/DejaVuSans.ttf").getPath();
                    renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                } catch (Exception e) {
                    System.err.println("Error al cargar la fuente:");
                    e.printStackTrace();
                }

                renderer.setDocumentFromString(html);
                renderer.layout();
                renderer.createPDF(outputStream);

                return outputStream.toByteArray();
            }

        } catch (Exception e) {
            System.err.println("Error general al generar la credencial PDF:");
            e.printStackTrace();
            throw new RuntimeException("Error al generar la credencial PDF", e);
        }
    }

@Override
public PDDocument generaFinalizacionEstudios(String legajoId, String alumnoDNI, String autoridades) {
        PDImageXObject Iesc1, Iesc2;
        Carrera carreraObj = this.carreraService.obtenerCarreraPorLegajoId(legajoId);
        Persona persona = this.alumnoService.findAlumnoById(alumnoDNI);
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");

            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);

            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);

// Altura de la página
            float pageHeight = PDRectangle.A4.getHeight();

// Ancho de la página
            float pageWidth = PDRectangle.A4.getWidth();

// Texto para cada línea
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };

// Distancia entre líneas
            int n = -10;

// Comienza a escribir el texto
            contenido.beginText();

            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);

            for (String line : lines) {
                // Calcula el ancho de cada línea
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;

                // Calcula la posición x para centrar el texto
                float xStart = (pageWidth - textWidth) / 2;

                // Mueve la posición x
                contenido.newLineAtOffset(xStart, 0);

                // Escribe la línea
                contenido.showText(line);

                // Mueve a la siguiente línea
                contenido.newLineAtOffset(-xStart, n);
            }
            contenido.endText();
            contenido.close();
            //imagen del encavezado izquierda
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();
            //imagen derecha del envavezado
            //  PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //   PDesc2.moveTo(200, 100);//image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            //  PDesc2.drawImage(Iesc2, 510, 770, 60, 60);//Draw an image at the x,y coordinates, with the given size.
            //    PDesc2.close();

            //================================================

            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //texto de constancia
            n = -10;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 10);
            regular.newLineAtOffset(170, 720);//titulo
            regular.showText("CONSTANCIA DE FINALIZACION DE ESTUDIOS");
            regular.newLineAtOffset(0, 0);
            regular.showText("_________________________________________");
            regular.endText();
            ///=============================================================
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            String hola = " __________________";
            String texto = Dividir(hola, width, letra);
            lineas = procesar(texto, letra);
            //===================================================================
//Justificar texto
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
            //consulta
            String carrera_id = carreraObj.getCarreraNombre();
            String genero = persona.getPersonaGenero();
            String resolucion = carreraObj.getCarreraResolucion();
            String genero2 = "";
            String genero1 = "";
            if (genero.equals("Masculino")) {
                genero1 = "el SR";
                genero2 = "el interesado";
                System.out.println("Es Hombre");
            } else {
                genero1 = "la Sra";
                genero2 = "la interesada";
                System.out.println("Es Mujer");
            }
            regular.beginText();
            regular.setFont(normal, letra);
            regular.newLineAtOffset(50, 680);
            String t1 = ("-----Por la presente, la Rectora del ");
            String t2 = ("Instituto de Educación Superior Intercultural CAMPINTA ");
            //regular.setCharacterSpacing(charspacing(longitud,t1+t2,letra));//espacio entre caracteres
            regular.showText(t1);
            regular.newLineAtOffset(tamaño(t1, letra, normal), 0);
            regular.setFont(negrita, letra);
            //String t2=("Instituto de Educación Superior Intercultural CAMPINTA");
            regular.showText(t2);
            float longitud = tamaño(t1 + t2, letra, PDType1Font.HELVETICA) + 20;//longitud permitida para justificar
            regular.newLineAtOffset(-tamaño(t1, letra, normal), -20);//nueva linea abajo justo al inicio
            String t3 = ("GUAZU GLORIA PEREZ ");
            String t4 = ("Prof. Cristina Noemi Martínez, deja ");//CONSTANCIA que ");
            String t41 = ("CONSTANCIA ");
            String t5 = ("que " + genero1);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41));//espacio entre caracteres
            regular.showText(t3);
            regular.setFont(PDType1Font.HELVETICA, letra);
            regular.newLineAtOffset(tamaño(t3, letra, negrita) + t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            //String t4=(" Prof. Cristina Noemi Martínez, deja CONSTANCIA que el Sr");
            regular.showText(t4);

            regular.newLineAtOffset(tamaño(t4, letra, normal) + t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t41);
            regular.newLineAtOffset(tamaño(t41, letra, normal) + t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t5);

            regular.newLineAtOffset(-tamaño(t3, letra, negrita) - tamaño(t41, letra, negrita) - tamaño(t4, letra, normal) - t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), -20);////charspacing(longitud, tamaño(t3, letra, normal),t3+t4)-tamaño(t4,letra, PDType1Font.HELVETICA)-t4.length()*charspacing(longitud, tamaño(t3, letra, normal),t3+t4+t5),-20 );

            String nombre = persona.getPersonaNombre();
            String apellido = persona.getPersonaApellido();
            String alumno_dni = String.valueOf(persona.getPersonaDni());
            String t6 = (apellido + " " + nombre + " D.N.I: " + alumno_dni + " ");
            String t7 = (", ha finalizado el cursado la carrera: ");
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, +tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7));//espacio entre caracteres
            regular.showText(t6);
            regular.newLineAtOffset(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t7);
            regular.newLineAtOffset(-(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7)), -20);//linea nueav

            String carrera = "Tecnicatura Superior en " + carreraObj.getCarreraNombre();
            String t10 = (" aprobada mediante");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10));//espacio entre caracteres
            regular.setFont(negrita, letra);
            regular.showText(carrera);
            regular.newLineAtOffset(tamaño(carrera, letra, negrita) + carrera.length() * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t10);
            regular.newLineAtOffset(-(tamaño(carrera, letra, negrita) + (carrera.length()) * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10)), -20);//linea nueav
            String t11 = ("Resolucion Ministerial " + resolucion);
            String t12 = "";
            String t13 = ("");
            t13 = rellenar(t11 + t12 + t13, t13, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13));//espacio entre caracteres
            regular.showText(t11);
            regular.newLineAtOffset(tamaño(t11, letra, normal) + t11.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t12);
            System.out.println(t13 + "//////////////////");
//String t13=("Año de este Instituto");
            regular.newLineAtOffset(tamaño(t12, letra, normal) + t12.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t13);
            String t14 = ("-----Se expide la presente CONSTANCIA a solicitud de " + genero2 + "  y al solo efecto de ser");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal), t14));//espacio entre caracteres
            regular.newLineAtOffset(-(tamaño(t11, letra, normal) + (t11.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)) - (tamaño(t12, letra, PDType1Font.HELVETICA) + (t12.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)), -20);//linea nueav

            regular.showText(t14);
            String t15 = ("presentada ante las Autoridades del ");
            String t16 = autoridades;
            System.out.println("autoridades " + t16);
            if (t16.equals("que lo requieran")) {
                t15 = "presentada ante las Autoridades";
            }

            t16 = rellenar(t15 + t16, t16, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, negrita), t15 + t16));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);
            regular.setFont(normal, letra);
            regular.showText(t15);
            // String t16=("B.E.G.U.P");
            regular.newLineAtOffset(tamaño(t15, letra, normal) + t15.length() * charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, normal), t15 + t16), 0);
            regular.setFont(normal, letra);
            regular.showText(t16);
            // String t17=("------------------SAN SALVADOR DE JUJUY, "+fecha());
            String t17 = ("-----SAN SALVADOR DE JUJUY, " + fecha() + "-------------");
            regular.newLineAtOffset(-(tamaño(t15, letra, normal) + (t15.length()) * charspacing(longitud, tamaño(t16, letra, normal) + tamaño(t15, letra, normal), t15 + t16)), -20);//linea nueav
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t17, letra, normal), t17));//espacio entre caracteres
            regular.showText(t17);
            regular.newLineAtOffset(0, -20);
            margin = 30;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (4 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = yStartNewPage + 30;
            float bottomMargin = 70;
            float yPosition = 550;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, Documento, Pagina, true, drawContent);
            Row<PDPage> headerRow = table.createRow(300);
            Cell<PDPage> cell = headerRow.createCell(100, " ");
            //cell.setText("hoal a atodsssss");
            table.draw();
            regular.endText();
            regular.close();
            System.out.println("se divujo la imagen");
            //Documento.save(dir+".pdf");
            //Documento.close();
        } catch (IOException e) {
        }
        return Documento;
    }


    private static String obtenerFecha(Date fechaDate) {
        String f = "";
        SimpleDateFormat formateador = new SimpleDateFormat("EEEE dd 'de' MMMM 'del año' yyyy", new Locale("ES"));
        String fechaFormateada = formateador.format(fechaDate);
        f = fechaFormateada;
        return f;
    }


    @Override
    public PDDocument generaAsistenciaSalidaCampo(String legajoId, String autoridades, String fechaSeleccionada, String curso, String accion, String lugar) {
        PDImageXObject Iesc1, Iesc2;
        Carrera carreraObj = this.carreraService.obtenerCarreraPorLegajoId(legajoId);
        Persona persona = this.alumnoService.obtenerAlumnoPorLegajoId(legajoId);
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");

            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);

            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);

// Altura de la página
            float pageHeight = PDRectangle.A4.getHeight();

// Ancho de la página
            float pageWidth = PDRectangle.A4.getWidth();

// Texto para cada línea
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };

// Distancia entre líneas
            int n = -10;

// Comienza a escribir el texto
            contenido.beginText();

            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);

            for (String line : lines) {
                // Calcula el ancho de cada línea
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;

                // Calcula la posición x para centrar el texto
                float xStart = (pageWidth - textWidth) / 2;

                // Mueve la posición x
                contenido.newLineAtOffset(xStart, 0);

                // Escribe la línea
                contenido.showText(line);

                // Mueve a la siguiente línea
                contenido.newLineAtOffset(-xStart, n);
            }

            contenido.endText();
            contenido.close();


            //imagen del encavezado izquierda
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();
            //imagen derecha del envavezado
            //  PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //   PDesc2.moveTo(200, 100);//image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            //  PDesc2.drawImage(Iesc2, 510, 770, 60, 60);//Draw an image at the x,y coordinates, with the given size.
            //    PDesc2.close();


            //================================================


            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //texto de constancia
            n = -10;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 10);
            regular.newLineAtOffset(200, 720);//titulo210
            regular.showText("CONSTANCIA DE SALIDA A CAMPO");
            regular.newLineAtOffset(0, 0);
            regular.showText("_______________________________");
            regular.endText();
            ///=============================================================
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            String hola = " ____________________";
            String texto = Dividir(hola, width, letra);
            lineas = procesar(texto, letra);
            //===================================================================
//Justificar texto
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;

            //consulta
            String genero = persona.getPersonaGenero();
            String genero2 = "";
            String genero1 = "";
            if (genero.equals("Masculino")) {
                genero1 = "el SR";
                genero2 = "el interesado";
                System.out.println("Es Hombre");
            } else {
                genero1 = "la Sra";
                genero2 = "la interesada";
                System.out.println("Es Mujer");
            }
            regular.beginText();
            regular.setFont(normal, letra);
            regular.newLineAtOffset(50, 680);
            String t1 = ("-----Por la presente, la Rectora del ");
            String t2 = ("Instituto de Educación Superior Intercultural CAMPINTA ");
            //regular.setCharacterSpacing(charspacing(longitud,t1+t2,letra));//espacio entre caracteres
            regular.showText(t1);
            regular.newLineAtOffset(tamaño(t1, letra, normal), 0);
            regular.setFont(negrita, letra);
            //String t2=("Instituto de Educación Superior Intercultural CAMPINTA");
            regular.showText(t2);
            float longitud = tamaño(t1 + t2, letra, PDType1Font.HELVETICA) + 20;//longitud permitida para justificar
            regular.newLineAtOffset(-tamaño(t1, letra, normal), -20);//nueva linea abajo justo al inicio
            String t3 = ("GUAZU GLORIA PEREZ ");
            String t4 = ("Prof. Cristina Noemi Martínez, deja ");//CONSTANCIA que ");
            String t41 = ("CONSTANCIA ");
            String t5 = ("que " + genero1);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41));//espacio entre caracteres
            regular.showText(t3);
            regular.setFont(PDType1Font.HELVETICA, letra);
            regular.newLineAtOffset(tamaño(t3, letra, negrita) + t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            //String t4=(" Prof. Cristina Noemi Martínez, deja CONSTANCIA que el Sr");
            regular.showText(t4);

            regular.newLineAtOffset(tamaño(t4, letra, normal) + t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t41);
            regular.newLineAtOffset(tamaño(t41, letra, normal) + t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t5);
            regular.newLineAtOffset(-tamaño(t3, letra, negrita) - tamaño(t41, letra, negrita) - tamaño(t4, letra, normal) - t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), -20);////charspacing(longitud, tamaño(t3, letra, normal),t3+t4)-tamaño(t4,letra, PDType1Font.HELVETICA)-t4.length()*charspacing(longitud, tamaño(t3, letra, normal),t3+t4+t5),-20 );

            String nombre = persona.getPersonaNombre();
            String apellido = persona.getPersonaApellido();
            String alumno_dni = String.valueOf(persona.getPersonaDni());
            String t6 = (apellido + " " + nombre + " D.N.I: " + alumno_dni + " ");

            String t7 = (", estudiante del " + curso + " de la carrera:");
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, +tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7));//espacio entre caracteres
            regular.showText(t6);
            regular.newLineAtOffset(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t7);
            regular.newLineAtOffset(-(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7)), -20);//linea nueav

            String carrera = "Tecnicatura Superior en " + carreraObj.getCarreraNombre();
            String t10 = ("");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10));//espacio entre caracteres
            regular.setFont(negrita, letra);
            regular.showText(carrera);
            regular.newLineAtOffset(tamaño(carrera, letra, negrita) + carrera.length() * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t10);
            regular.newLineAtOffset(-(tamaño(carrera, letra, negrita) + (carrera.length()) * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10)), -20);//linea nueav

            String t12 = "";

            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha = parser.parse(fechaSeleccionada);

            String fechaFormateada = obtenerFecha(fecha); // ✅ Correcto

            String t11 = (accion + "a la salida de campo el dia: " + fechaFormateada + " a la: ");
            String t13 = ("");
            t13 = rellenar(t11 + t12 + t13, t13, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13));//espacio entre caracteres
            regular.showText(t11);
            regular.newLineAtOffset(tamaño(t11, letra, normal) + t11.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t12);


            System.out.println(t13 + "//////////////////");
//String t13=("Año de este Instituto");
            regular.newLineAtOffset(tamaño(t12, letra, normal) + t12.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t13);

            String mat = rellenar(lugar, lugar, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(mat, letra, normal), mat));//espacio entre caracteres
            regular.newLineAtOffset(-(tamaño(t11, letra, normal) + (t11.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)) - (tamaño(t12, letra, PDType1Font.HELVETICA) + (t12.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)), -20);//linea nueav
            regular.showText(mat);


            String t14 = ("-----Se expide la presente CONSTANCIA a solicitud de " + genero2 + "  y al solo efecto de ser");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal), t14));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);//linea nueav


            regular.showText(t14);
            String t15 = ("presentada ante las Autoridades del ");
            String t16 = autoridades;
            System.out.println("autoridades " + t16);
            if (t16.equals("que lo requieran")) {
                t15 = "presentada ante las Autoridades";
            }

            t16 = rellenar(t15 + t16, t16, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, negrita), t15 + t16));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);
            regular.setFont(normal, letra);
            regular.showText(t15);
            // String t16=("B.E.G.U.P");
            regular.newLineAtOffset(tamaño(t15, letra, normal) + t15.length() * charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, normal), t15 + t16), 0);
            regular.setFont(normal, letra);
            regular.showText(t16);
            //String t17=("------------------SAN SALVADOR DE JUJUY, "+fecha());
            String t17 = ("-----SAN SALVADOR DE JUJUY, " + fecha() + "-------------");
            regular.newLineAtOffset(-(tamaño(t15, letra, normal) + (t15.length()) * charspacing(longitud, tamaño(t16, letra, normal) + tamaño(t15, letra, normal), t15 + t16)), -20);//linea nueav
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t17, letra, normal), t17));//espacio entre caracteres
            regular.showText(t17);
            regular.newLineAtOffset(0, -20);
            margin = 30;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (4 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = yStartNewPage + 30;
            float bottomMargin = 70;
            float yPosition = 550;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, Documento, Pagina, true, drawContent);
            Row<PDPage> headerRow = table.createRow(300);
            Cell<PDPage> cell = headerRow.createCell(100, " ");
            //cell.setText("hoal a atodsssss");
            table.draw();
            regular.endText();
            regular.close();
            System.out.println("se divujo la imagen");
            // Documento.save(dir+".pdf");
            //Documento.close();
        } catch (IOException e) {
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return Documento;
    }


    @Override
    public PDDocument generaAsistenciaParcial(String legajoId, String autoridades, String curso, String fechaSeleccionada,
                                              String accion, String entrada, String salida, String materia) {
        System.out.println("fecha Seelccionada" + fechaSeleccionada);
        PDImageXObject Iesc1, Iesc2;
        Carrera carreraObj = this.carreraService.obtenerCarreraPorLegajoId(legajoId);
        Persona persona = this.alumnoService.obtenerAlumnoPorLegajoId(legajoId);
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");

            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);

            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);

// Altura de la página
            float pageHeight = PDRectangle.A4.getHeight();

// Ancho de la página
            float pageWidth = PDRectangle.A4.getWidth();

// Texto para cada línea
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };

// Distancia entre líneas
            int n = -10;

// Comienza a escribir el texto
            contenido.beginText();

            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);

            for (String line : lines) {
                // Calcula el ancho de cada línea
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;

                // Calcula la posición x para centrar el texto
                float xStart = (pageWidth - textWidth) / 2;
                // Mueve la posición x
                contenido.newLineAtOffset(xStart, 0);
                // Escribe la línea
                contenido.showText(line);
                // Mueve a la siguiente línea
                contenido.newLineAtOffset(-xStart, n);
            }
            contenido.endText();
            contenido.close();
            //imagen del encavezado izquierda
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();
            //imagen derecha del envavezado
            //  PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //   PDesc2.moveTo(200, 100);//image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            //  PDesc2.drawImage(Iesc2, 510, 770, 60, 60);//Draw an image at the x,y coordinates, with the given size.
            //    PDesc2.close();
            //================================================
            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //texto de constancia
            n = -10;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 10);
            regular.newLineAtOffset(180, 720);//titulo210
            regular.showText("CONSTANCIA DE ASISTENCIA A EXAMEN PARCIAL");
            regular.newLineAtOffset(0, 0);
            regular.showText("_____________________________________________");
            regular.endText();
            ///=============================================================
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            String hola = " ____________________";
            String texto = Dividir(hola, width, letra);
            lineas = procesar(texto, letra);
            //===================================================================
//Justificar texto
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
            //consulta
            String genero = persona.getPersonaGenero();
            String genero2 = "";
            String genero1 = "";
            if (genero.equals("Masculino")) {
                genero1 = "el SR";
                genero2 = "el interesado";
                System.out.println("Es Hombre");
            } else {
                genero1 = "la Sra";
                genero2 = "la interesada";
                System.out.println("Es Mujer");
            }
            regular.beginText();
            regular.setFont(normal, letra);
            regular.newLineAtOffset(50, 680);
            String t1 = ("-----Por la presente, la Rectora del ");
            String t2 = ("Instituto de Educación Superior Intercultural CAMPINTA ");
            //regular.setCharacterSpacing(charspacing(longitud,t1+t2,letra));//espacio entre caracteres
            regular.showText(t1);
            regular.newLineAtOffset(tamaño(t1, letra, normal), 0);
            regular.setFont(negrita, letra);
            //String t2=("Instituto de Educación Superior Intercultural CAMPINTA");
            regular.showText(t2);
            float longitud = tamaño(t1 + t2, letra, PDType1Font.HELVETICA) + 20;//longitud permitida para justificar
            regular.newLineAtOffset(-tamaño(t1, letra, normal), -20);//nueva linea abajo justo al inicio
            String t3 = ("GUAZU GLORIA PEREZ ");
            String t4 = ("Prof. Cristina Noemi Martínez, deja ");//CONSTANCIA que ");
            String t41 = ("CONSTANCIA ");
            String t5 = ("que " + genero1);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41));//espacio entre caracteres
            regular.showText(t3);
            regular.setFont(PDType1Font.HELVETICA, letra);
            regular.newLineAtOffset(tamaño(t3, letra, negrita) + t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            //String t4=(" Prof. Cristina Noemi Martínez, deja CONSTANCIA que el Sr");
            regular.showText(t4);

            regular.newLineAtOffset(tamaño(t4, letra, normal) + t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t41);
            regular.newLineAtOffset(tamaño(t41, letra, normal) + t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t5);
            regular.newLineAtOffset(-tamaño(t3, letra, negrita) - tamaño(t41, letra, negrita) - tamaño(t4, letra, normal) - t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), -20);////charspacing(longitud, tamaño(t3, letra, normal),t3+t4)-tamaño(t4,letra, PDType1Font.HELVETICA)-t4.length()*charspacing(longitud, tamaño(t3, letra, normal),t3+t4+t5),-20 );

            String nombre = persona.getPersonaNombre();

            String apellido = persona.getPersonaApellido();

            String alumno_dni = String.valueOf(persona.getPersonaDni());

            String t6 = (apellido + " " + nombre + " D.N.I: " + alumno_dni + " ");

            String t7 = (", alumno del " + curso + " de la carrera:");
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, +tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7));//espacio entre caracteres
            regular.showText(t6);
            regular.newLineAtOffset(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t7);
            regular.newLineAtOffset(-(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7)), -20);//linea nueav

            String carrera = "Tecnicatura Superior en " + carreraObj.getCarreraNombre();
            String t10 = ("");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10));//espacio entre caracteres
            regular.setFont(negrita, letra);
            regular.showText(carrera);
            regular.newLineAtOffset(tamaño(carrera, letra, negrita) + carrera.length() * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t10);
            regular.newLineAtOffset(-(tamaño(carrera, letra, negrita) + (carrera.length()) * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10)), -20);//linea nueav

            String t12 = "";

            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha = parser.parse(fechaSeleccionada);
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
            String fechaFormateada = formatoFecha.format(fecha);

            String t11 = (accion + " el dia: " + fechaFormateada + " de " + entrada + " a " + salida + " al EXAMEN PARCIAL de la materia: ");
            String t13 = ("");
            t13 = rellenar(t11 + t12 + t13, t13, letra, longitud);

            regular.setCharacterSpacing(charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13));//espacio entre caracteres
            regular.showText(t11);
            regular.newLineAtOffset(tamaño(t11, letra, normal) + t11.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t12);

            System.out.println(t13 + "//////////////////");
//String t13=("Año de este Instituto");
            regular.newLineAtOffset(tamaño(t12, letra, normal) + t12.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t13);

            String mat = rellenar(materia, materia, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(mat, letra, normal), mat));//espacio entre caracteres
            regular.newLineAtOffset(-(tamaño(t11, letra, normal) + (t11.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)) - (tamaño(t12, letra, PDType1Font.HELVETICA) + (t12.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)), -20);//linea nueav
            regular.showText(mat);


            String t14 = ("-----Se expide la presente CONSTANCIA a solicitud de " + genero2 + "  y al solo efecto de ser");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal), t14));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);//linea nueav


            regular.showText(t14);
            String t15 = ("presentada ante las Autoridades del ");
            String t16 = autoridades;
            System.out.println("autoridades " + t16);
            if (t16.equals("que lo requieran")) {
                t15 = "presentada ante las Autoridades";
            }

            t16 = rellenar(t15 + t16, t16, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, negrita), t15 + t16));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);
            regular.setFont(normal, letra);
            regular.showText(t15);
            // String t16=("B.E.G.U.P");
            regular.newLineAtOffset(tamaño(t15, letra, normal) + t15.length() * charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, normal), t15 + t16), 0);
            regular.setFont(normal, letra);
            regular.showText(t16);
            //String t17=("------------------SAN SALVADOR DE JUJUY, "+fecha());
            String t17 = ("-----SAN SALVADOR DE JUJUY, " + fecha() + "-------------");
            regular.newLineAtOffset(-(tamaño(t15, letra, normal) + (t15.length()) * charspacing(longitud, tamaño(t16, letra, normal) + tamaño(t15, letra, normal), t15 + t16)), -20);//linea nueav
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t17, letra, normal), t17));//espacio entre caracteres
            regular.showText(t17);
            regular.newLineAtOffset(0, -20);
            margin = 30;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (4 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = yStartNewPage + 30;
            float bottomMargin = 70;
            float yPosition = 550;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, Documento, Pagina, true, drawContent);
            Row<PDPage> headerRow = table.createRow(300);
            Cell<PDPage> cell = headerRow.createCell(100, " ");
            //cell.setText("hoal a atodsssss");
            table.draw();
            regular.endText();
            regular.close();
            System.out.println("se divujo la imagen");
            //Documento.save(dir+".pdf");
            //Documento.close();
        } catch (IOException e) {
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return Documento;
    }


    private PDDocument generaAsistenciaExamen(String legajoId, String alumnoDNI, String autoridades, String curso, Date fechaSeleccionada,
                                              String accion, String entrada, String salida, String materia, String calificacion, String fecha) {
        PDImageXObject Iesc1, Iesc2;
        Carrera carreraObj = this.carreraService.obtenerCarreraPorLegajoId(legajoId);
        Persona persona = this.alumnoService.findAlumnoById(alumnoDNI);
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");

            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);

            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);

// Altura de la página
            float pageHeight = PDRectangle.A4.getHeight();

// Ancho de la página
            float pageWidth = PDRectangle.A4.getWidth();

// Texto para cada línea
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };

// Distancia entre líneas
            int n = -10;

// Comienza a escribir el texto
            contenido.beginText();

            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);

            for (String line : lines) {
                // Calcula el ancho de cada línea
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;

                // Calcula la posición x para centrar el texto
                float xStart = (pageWidth - textWidth) / 2;

                // Mueve la posición x
                contenido.newLineAtOffset(xStart, 0);

                // Escribe la línea
                contenido.showText(line);

                // Mueve a la siguiente línea
                contenido.newLineAtOffset(-xStart, n);
            }

            contenido.endText();
            contenido.close();


            //imagen del encavezado izquierda
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();
            //imagen derecha del envavezado
            //  PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //   PDesc2.moveTo(200, 100);//image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            //  PDesc2.drawImage(Iesc2, 510, 770, 60, 60);//Draw an image at the x,y coordinates, with the given size.
            //    PDesc2.close();


            //================================================


            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //texto de constancia
            n = -10;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 10);
            regular.newLineAtOffset(160, 720);//titulo210
            regular.showText("CONSTANCIA DE ASISTENCIA A MESA DE EXAMEN FINAL");
            regular.newLineAtOffset(0, 0);
            regular.showText("___________________________________________________");
            regular.endText();
            ///=============================================================
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            String hola = " ____________________";
            String texto = Dividir(hola, width, letra);
            lineas = procesar(texto, letra);
            //===================================================================
//Justificar texto
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;

            //consulta
            String genero = persona.getPersonaGenero();
            String genero2 = "";
            String genero1 = "";
            if (genero.equals("Masculino")) {
                genero1 = "el SR";
                genero2 = "el interesado";
                System.out.println("Es Hombre");
            } else {
                genero1 = "la Sra";
                genero2 = "la interesada";
                System.out.println("Es Mujer");
            }
            regular.beginText();
            regular.setFont(normal, letra);
            regular.newLineAtOffset(50, 680);
            String t1 = ("-----Por la presente, la Rectora del ");
            String t2 = ("Instituto de Educación Superior Intercultural CAMPINTA ");
            //regular.setCharacterSpacing(charspacing(longitud,t1+t2,letra));//espacio entre caracteres
            regular.showText(t1);
            regular.newLineAtOffset(tamaño(t1, letra, normal), 0);
            regular.setFont(negrita, letra);
            //String t2=("Instituto de Educación Superior Intercultural CAMPINTA");
            regular.showText(t2);
            float longitud = tamaño(t1 + t2, letra, PDType1Font.HELVETICA) + 20;//longitud permitida para justificar
            regular.newLineAtOffset(-tamaño(t1, letra, normal), -20);//nueva linea abajo justo al inicio
            String t3 = ("GUAZU GLORIA PEREZ ");
            String t4 = ("Prof. Cristina Noemi Martínez, deja ");//CONSTANCIA que ");
            String t41 = ("CONSTANCIA ");
            String t5 = ("que " + genero1);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41));//espacio entre caracteres
            regular.showText(t3);
            regular.setFont(PDType1Font.HELVETICA, letra);
            regular.newLineAtOffset(tamaño(t3, letra, negrita) + t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            //String t4=(" Prof. Cristina Noemi Martínez, deja CONSTANCIA que el Sr");
            regular.showText(t4);

            regular.newLineAtOffset(tamaño(t4, letra, normal) + t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t41);
            regular.newLineAtOffset(tamaño(t41, letra, normal) + t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t5);
            regular.newLineAtOffset(-tamaño(t3, letra, negrita) - tamaño(t41, letra, negrita) - tamaño(t4, letra, normal) - t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), -20);////charspacing(longitud, tamaño(t3, letra, normal),t3+t4)-tamaño(t4,letra, PDType1Font.HELVETICA)-t4.length()*charspacing(longitud, tamaño(t3, letra, normal),t3+t4+t5),-20 );

            String nombre = persona.getPersonaNombre();
            String apellido = persona.getPersonaApellido();
            String alumno_dni = String.valueOf(persona.getPersonaDni());
            String t6 = (apellido + " " + nombre + " D.N.I: " + alumno_dni + " ");

            String t7 = (", alumno regular del " + curso + " de la carrera:");
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, +tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7));//espacio entre caracteres
            regular.showText(t6);
            regular.newLineAtOffset(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t7);
            regular.newLineAtOffset(-(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7)), -20);//linea nueav

            String carrera = "Tecnicatura Superior en " + carreraObj.getCarreraNombre();
            String t10 = ("");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10));//espacio entre caracteres
            regular.setFont(negrita, letra);
            regular.showText(carrera);
            regular.newLineAtOffset(tamaño(carrera, letra, negrita) + carrera.length() * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t10);
            regular.newLineAtOffset(-(tamaño(carrera, letra, negrita) + (carrera.length()) * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10)), -20);//linea nueav

            String t12 = "";

            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
            String fechaFormateada = formatoFecha.format(fechaSeleccionada);

            String t11 = (accion + " el dia: " + fechaFormateada + " a la MESA DE EXAMEN FINAL correspondiente a la materia: ");
            String t13 = ("");
            t13 = rellenar(t11 + t12 + t13, t13, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13));//espacio entre caracteres
            regular.showText(t11);
            regular.newLineAtOffset(tamaño(t11, letra, normal) + t11.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t12);


            System.out.println(t13 + "//////////////////");
//String t13=("Año de este Instituto");
            regular.newLineAtOffset(tamaño(t12, letra, normal) + t12.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t13);

            String mat = rellenar(materia, materia, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(mat, letra, normal), mat));//espacio entre caracteres
            regular.newLineAtOffset(-(tamaño(t11, letra, normal) + (t11.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)) - (tamaño(t12, letra, PDType1Font.HELVETICA) + (t12.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)), -20);//linea nueav
            regular.showText(mat);


            String t14 = ("-----Se expide la presente CONSTANCIA a solicitud de " + genero2 + "  y al solo efecto de ser");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal), t14));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);//linea nueav


            regular.showText(t14);
            String t15 = ("presentada ante las Autoridades del ");
            String t16 = autoridades;
            System.out.println("autoridades " + t16);
            if (t16.equals("que lo requieran")) {
                t15 = "presentada ante las Autoridades";
            }

            t16 = rellenar(t15 + t16, t16, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, negrita), t15 + t16));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);
            regular.setFont(normal, letra);
            regular.showText(t15);
            // String t16=("B.E.G.U.P");
            regular.newLineAtOffset(tamaño(t15, letra, normal) + t15.length() * charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, normal), t15 + t16), 0);
            regular.setFont(normal, letra);
            regular.showText(t16);
            //String t17=("------------------SAN SALVADOR DE JUJUY, "+fecha());
            String t17 = ("-----SAN SALVADOR DE JUJUY, " + fecha() + "-------------");
            regular.newLineAtOffset(-(tamaño(t15, letra, normal) + (t15.length()) * charspacing(longitud, tamaño(t16, letra, normal) + tamaño(t15, letra, normal), t15 + t16)), -20);//linea nueav
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t17, letra, normal), t17));//espacio entre caracteres
            regular.showText(t17);
            regular.newLineAtOffset(0, -20);
            margin = 30;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (4 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = yStartNewPage + 30;
            float bottomMargin = 70;
            float yPosition = 550;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, Documento, Pagina, true, drawContent);
            Row<PDPage> headerRow = table.createRow(300);
            Cell<PDPage> cell = headerRow.createCell(100, " ");
            //cell.setText("hoal a atodsssss");
            table.draw();
            regular.endText();
            regular.close();
            System.out.println("se divujo la imagen");
            //Documento.save(dir+".pdf");
            //Documento.close();
        } catch (IOException e) {
        }
        return Documento;
    }

@Override
public PDDocument generaUltimaMateria(String legajoId, String autoridades) {

    NotaMateriaDTO ultimaNota = notaService.obtenerUltimaNota(legajoId);

        PDImageXObject Iesc1, Iesc2;
        Carrera carreraObj = this.carreraService.obtenerCarreraPorLegajoId(legajoId);
        Persona persona = this.alumnoService.obtenerAlumnoPorLegajoId(legajoId);
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");

            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);

            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);

// Altura de la página
            float pageHeight = PDRectangle.A4.getHeight();

// Ancho de la página
            float pageWidth = PDRectangle.A4.getWidth();

// Texto para cada línea
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };

// Distancia entre líneas
            int n = -10;

// Comienza a escribir el texto
            contenido.beginText();

            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);

            for (String line : lines) {
                // Calcula el ancho de cada línea
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;

                // Calcula la posición x para centrar el texto
                float xStart = (pageWidth - textWidth) / 2;

                // Mueve la posición x
                contenido.newLineAtOffset(xStart, 0);

                // Escribe la línea
                contenido.showText(line);

                // Mueve a la siguiente línea
                contenido.newLineAtOffset(-xStart, n);
            }

            contenido.endText();
            contenido.close();


            //imagen del encavezado izquierda
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();
            //imagen derecha del envavezado
            //  PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //   PDesc2.moveTo(200, 100);//image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            //  PDesc2.drawImage(Iesc2, 510, 770, 60, 60);//Draw an image at the x,y coordinates, with the given size.
            //    PDesc2.close();
            ///======================================

            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //texto de constancia
            n = -10;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 10);
            regular.newLineAtOffset(170, 720);//titulo
            regular.showText("CONSTANCIA DE ULTIMA MATERIA APROBADA");
            regular.newLineAtOffset(0, 0);
            regular.showText("__________________________________________");
            regular.endText();
            ///=============================================================
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            String hola = " __________________";
            String texto = Dividir(hola, width, letra);
            lineas = procesar(texto, letra);
            //===================================================================
//Justificar texto
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;

            //consulta
            String carrera_id = carreraObj.getCarreraId();
            String genero = persona.getPersonaGenero();
            String resolucion = carreraObj.getCarreraResolucion();
            String genero2 = "";
            String genero1 = "";
            if (genero.equals("Masculino")) {
                genero1 = "el SR";
                genero2 = "el interesado";
                System.out.println("Es Hombre");
            } else {
                genero1 = "la Sra";
                genero2 = "la interesada";
                System.out.println("Es Mujer");
            }
            regular.beginText();
            regular.setFont(normal, letra);
            regular.newLineAtOffset(50, 680);
            String t1 = ("-----Por la presente, la Rectora del ");
            String t2 = ("Instituto de Educación Superior Intercultural CAMPINTA ");
            //regular.setCharacterSpacing(charspacing(longitud,t1+t2,letra));//espacio entre caracteres
            regular.showText(t1);
            regular.newLineAtOffset(tamaño(t1, letra, normal), 0);
            regular.setFont(negrita, letra);
            //String t2=("Instituto de Educación Superior Intercultural CAMPINTA");
            regular.showText(t2);
            float longitud = tamaño(t1 + t2, letra, PDType1Font.HELVETICA) + 20;//longitud permitida para justificar
            regular.newLineAtOffset(-tamaño(t1, letra, normal), -20);//nueva linea abajo justo al inicio
            String t3 = ("GUAZU GLORIA PEREZ ");
            String t4 = ("Prof. Cristina Noemi Martínez, deja ");//CONSTANCIA que ");
            String t41 = ("CONSTANCIA ");
            String t5 = ("que " + genero1);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41));//espacio entre caracteres
            regular.showText(t3);
            regular.setFont(PDType1Font.HELVETICA, letra);
            regular.newLineAtOffset(tamaño(t3, letra, negrita) + t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            //String t4=(" Prof. Cristina Noemi Martínez, deja CONSTANCIA que el Sr");
            regular.showText(t4);

            regular.newLineAtOffset(tamaño(t4, letra, normal) + t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t41);
            regular.newLineAtOffset(tamaño(t41, letra, normal) + t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t5);

            regular.newLineAtOffset(-tamaño(t3, letra, negrita) - tamaño(t41, letra, negrita) - tamaño(t4, letra, normal) - t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), -20);////charspacing(longitud, tamaño(t3, letra, normal),t3+t4)-tamaño(t4,letra, PDType1Font.HELVETICA)-t4.length()*charspacing(longitud, tamaño(t3, letra, normal),t3+t4+t5),-20 );

            String nombre = persona.getPersonaNombre();
            String apellido = persona.getPersonaApellido();
            String alumno_dni = String.valueOf(persona.getPersonaDni());
            String t6 = (apellido + " " + nombre + " D.N.I: " + alumno_dni + " ");

            String t7 = (", ha finalizado el cursado de la carrera: ");
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, +tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7));//espacio entre caracteres
            regular.showText(t6);
            regular.newLineAtOffset(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t7);
            regular.newLineAtOffset(-(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7)), -20);//linea nueav

            String carrera = "Tecnicatura Superior en " + carreraObj.getCarreraNombre();
            String t10 = (" aprobada mediante");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10));//espacio entre caracteres
            regular.setFont(negrita, letra);
            regular.showText(carrera);
            regular.newLineAtOffset(tamaño(carrera, letra, negrita) + carrera.length() * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t10);
            regular.newLineAtOffset(-(tamaño(carrera, letra, negrita) + (carrera.length()) * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10)), -20);//linea nueav
            String t11 = ("Resolucion Ministerial " + resolucion);
            String t12 = "";
            String t13 = ("");
            t13 = rellenar(t11 + t12 + t13, t13, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13));//espacio entre caracteres
            regular.showText(t11);
            regular.newLineAtOffset(tamaño(t11, letra, normal) + t11.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t12);

            System.out.println(t13 + "//////////////////");
//String t13=("Año de este Instituto");

            regular.newLineAtOffset(tamaño(t12, letra, normal) + t12.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t13);
            //===================================
            String mat1 = "Su última materia aprobada ha sido: " + ultimaNota.getMateriaNombre();
            String tmat1 = rellenar(mat1, mat1, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(tmat1, letra, normal), tmat1));//espacio entre caracteres
            regular.showText(tmat1);

            regular.newLineAtOffset(0, -20);

            String t112 = ("con calificación: " + ultimaNota.getNotaCalificacionNumero() + "(" + ultimaNota.getNotaCalificacionLetra() + ")" + ", el dia: " + formatearFechaCompleta(ultimaNota.getNotaFecha()) + ", encontrándose el título en trámite.");
            String relleno = rellenar(t11, "", letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t11, letra, normal), t11));//espacio entre caracteres
            regular.showText(t112);

            regular.newLineAtOffset(0, -20);

            //==============================================
            String t14 = ("-----Se expide la presente CONSTANCIA a solicitud de " + genero2 + "  y al solo efecto de ser");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal), t14));//espacio entre caracteres
            regular.newLineAtOffset(0 //-(tamaño(tmat1, letra, normal) + (tmat1.length()) * charspacing(longitud, tamaño(tmat1, letra, normal), tmat1))
                    // - (tamaño(t12, letra, PDType1Font.HELVETICA) + (t12.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13))
                    ,
                    -20);//linea nueav

            regular.showText(t14);
            String t15 = ("presentada ante las Autoridades del: ");
            String t16 = autoridades;
            System.out.println("autoridades " + t16);
            if (t16.equals("que lo requieran")) {
                t15 = "presentada ante las Autoridades";
            }

            t16 = rellenar(t15 + t16, t16, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, negrita), t15 + t16));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);
            regular.setFont(normal, letra);
            regular.showText(t15);
            // String t16=("B.E.G.U.P");
            regular.newLineAtOffset(tamaño(t15, letra, normal) + t15.length() * charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, normal), t15 + t16), 0);
            regular.setFont(normal, letra);
            regular.showText(t16);
            // String t17=("------------------SAN SALVADOR DE JUJUY, "+fecha());
            String t17 = ("-----SAN SALVADOR DE JUJUY, " + fecha() + "-------------");
            regular.newLineAtOffset(-(tamaño(t15, letra, normal) + (t15.length()) * charspacing(longitud, tamaño(t16, letra, normal) + tamaño(t15, letra, normal), t15 + t16)), -20);//linea nueav
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t17, letra, normal), t17));//espacio entre caracteres
            regular.showText(t17);
            regular.newLineAtOffset(0, -20);
            margin = 30;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (4 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = yStartNewPage + 30;
            float bottomMargin = 70;
            float yPosition = 550;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, Documento, Pagina, true, drawContent);
            Row<PDPage> headerRow = table.createRow(300);
            Cell<PDPage> cell = headerRow.createCell(100, " ");
            //cell.setText("hoal a atodsssss");
            table.draw();
            regular.endText();
            regular.close();
            System.out.println("se divujo la imagen");
            //Documento.save(dir + ".pdf");
            //Documento.close();
        } catch (IOException e) {
        }
        return Documento;
    }


    @Override
    public ByteArrayInputStream generaPlanillaExcel(String carreraId, String materiaId, Boolean inscripto) {
        try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Planilla");
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("N°");
            headerRow.createCell(1).setCellValue("Apellido y Nombre");
            headerRow.createCell(2).setCellValue("DNI");
            MateriaCarrera materiaCarrera = this.materiaCarreraRepository
                    .findByCarrera_CarreraIdAndMateria_MateriaId(carreraId, materiaId).get();
            List<NotaCursadaDTO> cursadas;
            if ("1ro".equals(materiaCarrera.getMateria().getMateriaNivel())) {
                cursadas = this.notaService.findNotasByCarreraAndMateriaAll(carreraId, materiaId, inscripto);
            } else {
                cursadas = this.notaService.findNotasByCarreraAndMateria(carreraId, materiaId, inscripto);
            }

            int rowIdx = 1;
            for (int i = 0; i < cursadas.size(); i++) {
                NotaCursadaDTO nota = cursadas.get(i);
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(nota.getPersonaApellido() + ", " + nota.getPersonaNombre());
                row.createCell(2).setCellValue(nota.getPersonaDni());
            }

//            // Ajuste de tamaño automático de columnas
//            for (int i = 0; i < 3; i++) {
//                sheet.autoSizeColumn(i);
//            }

// Ajuste manual del ancho de columnas según contenido esperado
            sheet.setColumnWidth(0, 8 * 256);   // Columna N°
            sheet.setColumnWidth(1, 35 * 256);  // Columna Apellido y Nombre
            sheet.setColumnWidth(2, 12 * 256);  // Columna DNI
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return new ByteArrayInputStream(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public PDDocument generaAsistenciaExamenFinal(String legajoId, String autoridades, String curso,
                                                  String fechaSeleccionada, String accion, String materia) {
        PDImageXObject Iesc1;
        Carrera carreraObj = this.carreraService.obtenerCarreraPorLegajoId(legajoId);
        Persona persona = this.alumnoService.obtenerAlumnoPorLegajoId(legajoId);
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");

            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);

            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);

// Altura de la página
            float pageHeight = PDRectangle.A4.getHeight();

// Ancho de la página
            float pageWidth = PDRectangle.A4.getWidth();

// Texto para cada línea
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };

// Distancia entre líneas
            int n = -10;

// Comienza a escribir el texto
            contenido.beginText();

            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);

            for (String line : lines) {
                // Calcula el ancho de cada línea
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;

                // Calcula la posición x para centrar el texto
                float xStart = (pageWidth - textWidth) / 2;

                // Mueve la posición x
                contenido.newLineAtOffset(xStart, 0);

                // Escribe la línea
                contenido.showText(line);

                // Mueve a la siguiente línea
                contenido.newLineAtOffset(-xStart, n);
            }

            contenido.endText();
            contenido.close();


            //imagen del encavezado izquierda
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();
            //imagen derecha del envavezado
            //  PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //   PDesc2.moveTo(200, 100);//image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            //  PDesc2.drawImage(Iesc2, 510, 770, 60, 60);//Draw an image at the x,y coordinates, with the given size.
            //    PDesc2.close();


            //================================================


            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //texto de constancia
            n = -10;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 10);
            regular.newLineAtOffset(160, 720);//titulo210
            regular.showText("CONSTANCIA DE ASISTENCIA A MESA DE EXAMEN FINAL");
            regular.newLineAtOffset(0, 0);
            regular.showText("___________________________________________________");
            regular.endText();
            ///=============================================================
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            String hola = " ____________________";
            String texto = Dividir(hola, width, letra);
            lineas = procesar(texto, letra);
            //===================================================================
//Justificar texto
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;

            //consulta
            String genero = persona.getPersonaGenero();
            String genero2 = "";
            String genero1 = "";
            if (genero.equals("Masculino")) {
                genero1 = "el SR";
                genero2 = "el interesado";
                System.out.println("Es Hombre");
            } else {
                genero1 = "la Sra";
                genero2 = "la interesada";
                System.out.println("Es Mujer");
            }
            regular.beginText();
            regular.setFont(normal, letra);
            regular.newLineAtOffset(50, 680);
            String t1 = ("-----Por la presente, la Rectora del ");
            String t2 = ("Instituto de Educación Superior Intercultural CAMPINTA ");
            //regular.setCharacterSpacing(charspacing(longitud,t1+t2,letra));//espacio entre caracteres
            regular.showText(t1);
            regular.newLineAtOffset(tamaño(t1, letra, normal), 0);
            regular.setFont(negrita, letra);
            //String t2=("Instituto de Educación Superior Intercultural CAMPINTA");
            regular.showText(t2);
            float longitud = tamaño(t1 + t2, letra, PDType1Font.HELVETICA) + 20;//longitud permitida para justificar
            regular.newLineAtOffset(-tamaño(t1, letra, normal), -20);//nueva linea abajo justo al inicio
            String t3 = ("GUAZU GLORIA PEREZ ");
            String t4 = ("Prof. Cristina Noemi Martínez, deja ");//CONSTANCIA que ");
            String t41 = ("CONSTANCIA ");
            String t5 = ("que " + genero1);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41));//espacio entre caracteres
            regular.showText(t3);
            regular.setFont(PDType1Font.HELVETICA, letra);
            regular.newLineAtOffset(tamaño(t3, letra, negrita) + t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            //String t4=(" Prof. Cristina Noemi Martínez, deja CONSTANCIA que el Sr");
            regular.showText(t4);

            regular.newLineAtOffset(tamaño(t4, letra, normal) + t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t41);
            regular.newLineAtOffset(tamaño(t41, letra, normal) + t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t5);
            regular.newLineAtOffset(-tamaño(t3, letra, negrita) - tamaño(t41, letra, negrita) - tamaño(t4, letra, normal) - t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), -20);////charspacing(longitud, tamaño(t3, letra, normal),t3+t4)-tamaño(t4,letra, PDType1Font.HELVETICA)-t4.length()*charspacing(longitud, tamaño(t3, letra, normal),t3+t4+t5),-20 );
            String nombre = persona.getPersonaNombre();
            String apellido = persona.getPersonaApellido();
            String alumno_dni = String.valueOf(persona.getPersonaDni());
            String t6 = (apellido + " " + nombre + " D.N.I: " + alumno_dni + " ");
            String t7 = (", alumno regular del " + curso + " de la carrera:");
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, +tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7));//espacio entre caracteres
            regular.showText(t6);
            regular.newLineAtOffset(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t7);
            regular.newLineAtOffset(-(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7)), -20);//linea nueav
            String carrera = "Tecnicatura Superior en " + carreraObj.getCarreraNombre();
            String t10 = ("");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10));//espacio entre caracteres
            regular.setFont(negrita, letra);
            regular.showText(carrera);
            regular.newLineAtOffset(tamaño(carrera, letra, negrita) + carrera.length() * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t10);
            regular.newLineAtOffset(-(tamaño(carrera, letra, negrita) + (carrera.length()) * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10)), -20);//linea nuea
            String t12 = "";

            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha = parser.parse(fechaSeleccionada);
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
            String fechaFormateada = formatoFecha.format(fecha);

            String t11 = (accion + " el dia: " + fechaFormateada + " a la MESA DE EXAMEN FINAL correspondiente a la materia: ");
            String t13 = ("");
            t13 = rellenar(t11 + t12 + t13, t13, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13));//espacio entre caracteres
            regular.showText(t11);
            regular.newLineAtOffset(tamaño(t11, letra, normal) + t11.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t12);
            System.out.println(t13 + "//////////////////");
            //String t13=("Año de este Instituto");
            regular.newLineAtOffset(tamaño(t12, letra, normal) + t12.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t13);
            String mat = rellenar(materia, materia, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(mat, letra, normal), mat));//espacio entre caracteres
            regular.newLineAtOffset(-(tamaño(t11, letra, normal) + (t11.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)) - (tamaño(t12, letra, PDType1Font.HELVETICA) + (t12.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)), -20);//linea nueav
            regular.showText(mat);
            String t14 = ("-----Se expide la presente CONSTANCIA a solicitud de " + genero2 + "  y al solo efecto de ser");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal), t14));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);//linea nueav
            regular.showText(t14);
            String t15 = ("presentada ante las Autoridades del ");
            String t16 = autoridades;
            System.out.println("autoridades " + t16);
            if (t16.equals("que lo requieran")) {
                t15 = "presentada ante las Autoridades";
            }

            t16 = rellenar(t15 + t16, t16, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, negrita), t15 + t16));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);
            regular.setFont(normal, letra);
            regular.showText(t15);
            // String t16=("B.E.G.U.P");
            regular.newLineAtOffset(tamaño(t15, letra, normal) + t15.length() * charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, normal), t15 + t16), 0);
            regular.setFont(normal, letra);
            regular.showText(t16);
            //String t17=("------------------SAN SALVADOR DE JUJUY, "+fecha());
            String t17 = ("-----SAN SALVADOR DE JUJUY, " + fecha() + "-------------");
            regular.newLineAtOffset(-(tamaño(t15, letra, normal) + (t15.length()) * charspacing(longitud, tamaño(t16, letra, normal) + tamaño(t15, letra, normal), t15 + t16)), -20);//linea nueav
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t17, letra, normal), t17));//espacio entre caracteres
            regular.showText(t17);
            regular.newLineAtOffset(0, -20);
            margin = 30;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (4 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = yStartNewPage + 30;
            float bottomMargin = 70;
            float yPosition = 550;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, Documento, Pagina, true, drawContent);
            Row<PDPage> headerRow = table.createRow(300);
            Cell<PDPage> cell = headerRow.createCell(100, " ");
            //cell.setText("hoal a atodsssss");
            table.draw();
            regular.endText();
            regular.close();
            System.out.println("se divujo la imagen");
            //   Documento.save(dir+".pdf");
            //Documento.close();
        } catch (IOException e) {
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return Documento;
    }

    @Override
    public PDDocument crearPDFPorMes(LocalDate fechaInicio, LocalDate fechaFin) {
        PDImageXObject Iesc1, Iesc2;
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");//divujar desde el path
            PDPage Pagina = new PDPage(PDRectangle.LEGAL);
            //Pagina.setRotation(90);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);
            int n = -10;//distancia entre lineas
            contenido.beginText();
            contenido.setFont(PDType1Font.HELVETICA, 8);
            contenido.newLineAtOffset(200, 990);
            contenido.showText("INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL");
            contenido.newLineAtOffset(40, n);
            contenido.showText("“CAMPINTA GUAZU GLORIA PEREZ”");
            contenido.newLineAtOffset(-25, n);
            contenido.showText("Del Consejo de Organizaciones Aborígenes de Jujuy");
            contenido.newLineAtOffset(-6, n);
            contenido.showText("Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15");
            contenido.newLineAtOffset(-15, n);
            contenido.showText("Bahia Blanca Nº 235 Bº .Kennedy – Tel. Fax. N° (0388)-4237323");
            contenido.newLineAtOffset(-55, n);
            contenido.showText("(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy – Kollasuyu- República Argentina");
            contenido.newLineAtOffset(-108, n);
            contenido.showText("________________________________________________________________________________________________________________________");
            contenido.endText();
            contenido.close();
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 940, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();


            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            n = -18;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 15);
            regular.newLineAtOffset(40, 900);//titulo
            regular.showText("REPORTE MESUAL DE ASISTENCIA");
            regular.newLineAtOffset(0, n);
            regular.setFont(PDType1Font.HELVETICA, 10);
            regular.showText("INSTITUTO DE EDUCACION INTERCULTURAL CAMPINTA GUAZU GLORIA PEREZ");
            regular.newLineAtOffset(0, n);
            regular.showText("Fechas: " + fechaInicio +" - "+fechaFin);
            regular.endText();
            regular.close();
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;

            PDPageContentStream cuadro = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);

            margin = 20;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (2 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 820;//yStartNewPage;
            float bottomMargin = 40;
            float auxmargin = 40;
// y position is your coordinate of top left corner of the table
            float yPosition = 300;
            //==========================================
            //==========================================
            //==========================================

            BaseTable table = new BaseTable(yStart + 30, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            int espaciado = 0;
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 600);//X=40
            Row<PDPage> headerRow = table.createRow(20);
            int a = 5;
            Cell<PDPage> cell = headerRow.createCell(5, "i");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setTextRotated(true);
            System.out.println(cell.getHeight());
            float h = cell.getInnerWidth();
            cell = headerRow.createCell(20, "Personal");//30
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            // cell.setTextRotated(true);
            float b = cell.getInnerWidth();
            cuadro.setCharacterSpacing(espaciado);
            cell = headerRow.createCell(25, "Cargo");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float bb = cell.getInnerWidth();
            cuadro.setCharacterSpacing(espaciado);
            cell = headerRow.createCell(10, "DNI");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);

            float c = cell.getExtraWidth();
            cell = headerRow.createCell(6, "Asist.");//presente
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float r = cell.getInnerWidth();
            cell = headerRow.createCell(6, "Tard.");//tardanxa
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float d = cell.getExtraWidth();
            cell = headerRow.createCell(8, "S/Temp.");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float dd = cell.getExtraWidth();
            cell.setFont(PDType1Font.HELVETICA);
            cell = headerRow.createCell(6, "F.J");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);

            cell.setFont(PDType1Font.HELVETICA);
            cell = headerRow.createCell(6, "F.I");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            //Row<PDPage> row = table.createRow(12);
            //int año = 0;
            table.draw();
            BaseTable Materiasaño = new BaseTable(yStart - headerRow.getHeight() + 1 + 30, yStartNewPage + 1, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            float H = 0;
            int j = 1;

//            int numeroMes = convertirMesANumero(mes);
//            System.out.println("Numero del mes: " + numeroMes);
            List<ReporteFaltasDTO> reporte = asistenciaPersonalService.cargarAsistenciasDelMesRango(fechaInicio, fechaFin);

            for (ReporteFaltasDTO dato : reporte) {
                SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat formatoNuevo = new SimpleDateFormat("dd-MM-yyyy");
                int nk = 6;
                Row<PDPage> rew = Materiasaño.createRow(5);//19
                // Celda para la columna "Orden"
                Cell<PDPage> cellOrden = rew.createCell(5, String.valueOf(j));
                cellOrden.setAlign(HorizontalAlignment.CENTER);
                cellOrden.setValign(VerticalAlignment.MIDDLE);
                cellOrden.setFont(PDType1Font.HELVETICA);
                cellOrden.setFontSize(nk);
                // Celda para la columna "Nombre Materia"
                Cell<PDPage> cellNombreMateria = rew.createCell(20, dato.getNombreCompleto());
                cellNombreMateria.setFontSize(nk);
                cellNombreMateria.setValign(VerticalAlignment.MIDDLE);
                cellNombreMateria.setFont(PDType1Font.HELVETICA);
                // Celda para la columna "Nota Final"
                Cell<PDPage> cellNotaFinal = rew.createCell(25, dato.getMateriaNombre());
                cellNotaFinal.setFontSize(nk);
                cellNotaFinal.setAlign(HorizontalAlignment.CENTER);
                cellNotaFinal.setValign(VerticalAlignment.MIDDLE);
                cellNotaFinal.setFont(PDType1Font.HELVETICA);
                Cell<PDPage> cellNotaFinall = rew.createCell(10, dato.getDni());
                cellNotaFinall.setFontSize(nk);
                cellNotaFinall.setAlign(HorizontalAlignment.CENTER);
                cellNotaFinall.setValign(VerticalAlignment.MIDDLE);
                cellNotaFinall.setFont(PDType1Font.HELVETICA);
                Cell<PDPage> cellyear = rew.createCell(6, dato.getEstado0().toString());
                cellyear.setFontSize(nk);
                cellyear.setAlign(HorizontalAlignment.CENTER);
                cellyear.setValign(VerticalAlignment.MIDDLE);
                cellyear.setFont(PDType1Font.HELVETICA);
                Cell<PDPage> cellyeart = rew.createCell(6, dato.getEstado1().toString());
                cellyeart.setFontSize(nk);
                cellyeart.setAlign(HorizontalAlignment.CENTER);
                cellyeart.setValign(VerticalAlignment.MIDDLE);
                cellyeart.setFont(PDType1Font.HELVETICA);
                Cell<PDPage> cellyeartt = rew.createCell(8, dato.getEstado2().toString());
                cellyeartt.setFontSize(nk);
                cellyeartt.setAlign(HorizontalAlignment.CENTER);
                cellyeartt.setValign(VerticalAlignment.MIDDLE);
                cellyeartt.setFont(PDType1Font.HELVETICA);
                Cell<PDPage> cellyearttt = rew.createCell(6, dato.getEstado3().toString());
                cellyearttt.setFontSize(nk);
                cellyearttt.setAlign(HorizontalAlignment.CENTER);
                cellyearttt.setValign(VerticalAlignment.MIDDLE);
                cellyearttt.setFont(PDType1Font.HELVETICA);
                Cell<PDPage> cellyeartttt = rew.createCell(6, dato.getEstado4().toString());
                cellyeartttt.setFontSize(nk);
                cellyeartttt.setAlign(HorizontalAlignment.CENTER);
                cellyeartttt.setValign(VerticalAlignment.MIDDLE);
                cellyeartttt.setFont(PDType1Font.HELVETICA);
                float filaHeight = rew.getHeight();
                H = H + filaHeight;
                j++;
            }
            Materiasaño.draw();
            cuadro.endText();
            cuadro.close();

            //Documento.close();
        } catch (IOException e) {
        }
        return Documento;
    }

    public int convertirMesANumero(String mes) {
        Map<String, Integer> meses = Map.ofEntries(
                Map.entry("ENERO", 1),
                Map.entry("FEBRERO", 2),
                Map.entry("MARZO", 3),
                Map.entry("ABRIL", 4),
                Map.entry("MAYO", 5),
                Map.entry("JUNIO", 6),
                Map.entry("JULIO", 7),
                Map.entry("AGOSTO", 8),
                Map.entry("SEPTIEMBRE", 9),
                Map.entry("OCTUBRE", 10),
                Map.entry("NOVIEMBRE", 11),
                Map.entry("DICIEMBRE", 12)
        );
        if (mes == null) {
            return 0; // o lanzar excepción si preferís
        }
        return meses.getOrDefault(mes.toUpperCase(), 0); // Devuelve 0 si no se encuentra
    }

//    @Override
//    public PDDocument crearPDFPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
//        // Aquí podés usar las fechas para filtrar datos de tu base
//        System.out.println("Generando PDF desde " + fechaInicio + " hasta " + fechaFin);
//        PDImageXObject Iesc1, Iesc2;
//        PDDocument Documento = new PDDocument();
//        try {
//            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
//            if (iesc1I == null) {
//                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
//            }
//            byte[] ba = IOUtils.toByteArray(iesc1I);
//            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");
//            PDPage Pagina = new PDPage(PDRectangle.A4);
//            Documento.addPage(Pagina);
//            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);
//            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
//            int fontSize = 8; // Tamaño de la fuente
//            contenido.setFont(font, fontSize);
//            float pageHeight = PDRectangle.A4.getHeight();
//            float pageWidth = PDRectangle.A4.getWidth();
//            String[] lines = {
//                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
//                    "“CAMPINTA GUAZU GLORIA PEREZ”",
//                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
//                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
//                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
//                    "________________________________________________________________________________________________________________________"
//            };
//            int n = -10;
//            contenido.beginText();
//            float iStart = 820;
//            contenido.newLineAtOffset(0, iStart);
//            for (String line : lines) {
//                // Calcula el ancho de cada línea
//                float textWidth = font.getStringWidth(line) / 1000 * fontSize;
//                // Calcula la posición x para centrar el texto
//                float xStart = (pageWidth - textWidth) / 2;
//                // Mueve la posición x
//                contenido.newLineAtOffset(xStart, 0);
//                // Escribe la línea
//                contenido.showText(line);
//                // Mueve a la siguiente línea
//                contenido.newLineAtOffset(-xStart, n);
//            }
//            contenido.endText();
//            contenido.close();
//            //imagen del encavezado izquierda
//            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
//            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
//            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
//            PDesc1.close();
//            //imagen derecha del envavezado
//            //  PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
//            //   PDesc2.moveTo(200, 100);//image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
//            //  PDesc2.drawImage(Iesc2, 510, 770, 60, 60);//Draw an image at the x,y coordinates, with the given size.
//            //    PDesc2.close();
//            ///======================================
//
//            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
//            n = -18;//distancia entre lineas
//            regular.beginText();
//            regular.setFont(PDType1Font.HELVETICA_BOLD, 15);
//            regular.newLineAtOffset(40, 740);//titulo
//            regular.showText("REGISTRO DE ASISTENCIA");
//            regular.newLineAtOffset(0, n);
//            regular.setFont(PDType1Font.HELVETICA, 10);
//            regular.showText("INSTITUTO DE EDUCACION INTERCULTURAL CAMPINTA GUAZU GLORIA PEREZ");
//            regular.newLineAtOffset(0, n);
////            LocalDate fechaOriginal4 = fechaInicio;
////            LocalDate localDate4 = fechaOriginal4.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
////            int año = localDate4.getYear();
//            DateTimeFormatter formatter4 = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'del año' yyyy", new Locale("es", "AR"));
//            String fechaFormateada4 = fechaInicio.format(formatter4);
//            System.out.println("Fecha formateada: " + fechaFormateada4);
//            regular.showText("FECHA: " + fechaFormateada4);
//            regular.endText();
//            regular.close();
//            PDRectangle mediabox = Pagina.getMediaBox();
//            float margin = 20;
//            float width = mediabox.getWidth() - 4 * margin;
//            float X = mediabox.getLowerLeftX() + margin;
//            float Y = mediabox.getUpperRightY() - margin;
//            List<String> lineas = new ArrayList<String>();
//            int letra = 12;
//            PDType1Font normal = PDType1Font.HELVETICA;
//            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
//            PDPageContentStream cuadro = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
//            margin = 20;
//            // starting y position is whole page height subtracted by top and bottom margin
//            float yStartNewPage = Pagina.getMediaBox().getHeight() - (2 * margin);
//            // we want table across whole page width (subtracted by left and right margin ofcourse)
//            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
//            boolean drawContent = true;
//            float yStart = 650;//yStartNewPage;
//            float bottomMargin = 20;
//            float auxmargin = 20;
//// y position is your coordinate of top left corner of the table
//            float yPosition = 300;
//            //==========================================
//            //==========================================
//            //==========================================
//            BaseTable table = new BaseTable(yStart + 30, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
//            int espaciado = 0;
//            cuadro.beginText();
//            cuadro.newLineAtOffset(0, 600);//X=40
//            Row<PDPage> headerRow = table.createRow(20);
//            int a = 5;
//            Cell<PDPage> cell = headerRow.createCell(5, "ID");
//            cell.setAlign(HorizontalAlignment.CENTER);
//            cell.setValign(VerticalAlignment.MIDDLE);
//            cell.setTextRotated(true);
//            System.out.println(cell.getHeight());
//            float h = cell.getInnerWidth();
//            cell = headerRow.createCell(15, "Materia");//30
//            cell.setAlign(HorizontalAlignment.CENTER);
//            cell.setValign(VerticalAlignment.MIDDLE);
//            // cell.setTextRotated(true);
//            float b = cell.getInnerWidth();
//            cuadro.setCharacterSpacing(espaciado);
//            cell = headerRow.createCell(35, "Docente");
//            cell.setAlign(HorizontalAlignment.CENTER);
//            cell.setValign(VerticalAlignment.MIDDLE);
//            float bb = cell.getInnerWidth();
//            cuadro.setCharacterSpacing(espaciado);
//            cell = headerRow.createCell(10, "DNI");
//            cell.setAlign(HorizontalAlignment.CENTER);
//            cell.setValign(VerticalAlignment.MIDDLE);
//            float c = cell.getExtraWidth();
//            cell = headerRow.createCell(8, "Entrada");//11
//            cell.setAlign(HorizontalAlignment.CENTER);
//            cell.setValign(VerticalAlignment.MIDDLE);
//            float r = cell.getInnerWidth();
//            cell = headerRow.createCell(8, "Salida");
//            cell.setAlign(HorizontalAlignment.CENTER);
//            cell.setValign(VerticalAlignment.MIDDLE);
//            float d = cell.getExtraWidth();
//            cell.setFont(PDType1Font.HELVETICA);
//            cell = headerRow.createCell(17, "Obs");
//            cell.setAlign(HorizontalAlignment.CENTER);
//            cell.setValign(VerticalAlignment.MIDDLE);
//            float E = cell.getExtraWidth();
//            cell.setFont(PDType1Font.HELVETICA);
//
//            table.draw();
//            BaseTable Materiasaño = new BaseTable(yStart - headerRow.getHeight() + 1 + 30, yStartNewPage + 1, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
//            float H = 0;
//            int j = 1;
//
//
//            List<AsistenciaDetalleDTO> asistencias = asistenciaPersonalService.obtenerAsistenciasPorFecha(fechaInicio);
//            for (AsistenciaDetalleDTO dato : asistencias) {
//                SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                SimpleDateFormat formatoNuevo = new SimpleDateFormat("dd-MM-yyyy");
//                int nk = 6;
//                Row<PDPage> rew = Materiasaño.createRow(5);//19
//                // Celda para la columna "Orden"
//                Cell<PDPage> cellOrden = rew.createCell(5, String.valueOf(j));
//                cellOrden.setAlign(HorizontalAlignment.CENTER);
//                cellOrden.setValign(VerticalAlignment.MIDDLE);
//                cellOrden.setFont(PDType1Font.HELVETICA);
//                cellOrden.setFontSize(nk);
//                Cell<PDPage> cellNombreMateria = rew.createCell(15, dato.getMateriaNombre());
//                cellNombreMateria.setFontSize(nk);
//                cellNombreMateria.setValign(VerticalAlignment.MIDDLE);
//                cellNombreMateria.setFont(PDType1Font.HELVETICA);
//                // Celda para la columna "Nota Final"
//                Cell<PDPage> cellNotaFinal = rew.createCell(35, dato.getApellido() + ", " + dato.getNombre());
//                cellNotaFinal.setFontSize(nk);
//                cellNotaFinal.setAlign(HorizontalAlignment.CENTER);
//                cellNotaFinal.setValign(VerticalAlignment.MIDDLE);
//                cellNotaFinal.setFont(PDType1Font.HELVETICA);
//                Cell<PDPage> cellNotaFinall = rew.createCell(10, dato.getDni());
//                cellNotaFinall.setFontSize(nk);
//                cellNotaFinall.setAlign(HorizontalAlignment.CENTER);
//                cellNotaFinall.setValign(VerticalAlignment.MIDDLE);
//                cellNotaFinall.setFont(PDType1Font.HELVETICA);
//                Cell<PDPage> cellyear = rew.createCell(8, dato.getHoraEntrada());
//                cellyear.setFontSize(nk);
//                cellyear.setAlign(HorizontalAlignment.CENTER);
//                cellyear.setValign(VerticalAlignment.MIDDLE);
//                cellyear.setFont(PDType1Font.HELVETICA);
//                Cell<PDPage> cellyeart = rew.createCell(8, dato.getHoraSalida());
//                cellyeart.setFontSize(nk);
//                cellyeart.setAlign(HorizontalAlignment.CENTER);
//                cellyeart.setValign(VerticalAlignment.MIDDLE);
//                cellyeart.setFont(PDType1Font.HELVETICA);
//                Cell<PDPage> cellyeartt = rew.createCell(17, dato.getObservaciones());
//                cellyeartt.setFontSize(nk);
//                cellyeartt.setAlign(HorizontalAlignment.CENTER);
//                cellyeartt.setValign(VerticalAlignment.MIDDLE);
//                cellyeartt.setFont(PDType1Font.HELVETICA);
//                float filaHeight = rew.getHeight();
//                H = H + filaHeight;
//                j++;
//            }
//            Materiasaño.draw();
//            cuadro.endText();
//            cuadro.close();
//            //============================
//            PDPageContentStream pie = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
//            n = -17;//distancia entre lineas
//            pie.beginText();
//            pie.setFont(PDType1Font.HELVETICA_BOLD, 12);
//            pie.newLineAtOffset(40, yStart - H - 70);
//            pie.setFont(PDType1Font.HELVETICA, 10);
//            n = -10;
//            pie.newLineAtOffset(410, 0);
//            pie.newLineAtOffset(5, n);
//            pie.showText("Ausentes:______");
//            pie.endText();
//            pie.close();
//            cuadro.close();
//            //Documento.close();
//        } catch (IOException e) {
//        }
//        return Documento;
//    }


@Override
public PDDocument crearPDFPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
    PDDocument Documento = new PDDocument();
    try {
        LocalDate fechaActual = fechaInicio;
        while (!fechaActual.isAfter(fechaFin)) {
            System.out.println("Generando PDF para: " + fechaActual);
            PDImageXObject Iesc1;
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");
            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);
            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);
            float pageHeight = PDRectangle.A4.getHeight();
            float pageWidth = PDRectangle.A4.getWidth();
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };
            int n = -10;
            contenido.beginText();
            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);
            for (String line : lines) {
                // Calcula el ancho de cada línea
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;
                // Calcula la posición x para centrar el texto
                float xStart = (pageWidth - textWidth) / 2;
                // Mueve la posición x
                contenido.newLineAtOffset(xStart, 0);
                // Escribe la línea
                contenido.showText(line);
                // Mueve a la siguiente línea
                contenido.newLineAtOffset(-xStart, n);
            }
            contenido.endText();
            contenido.close();
            //imagen del encavezado izquierda
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();

            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            n = -18;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 15);
            regular.newLineAtOffset(40, 740);//titulo
            regular.showText("REGISTRO DE ASISTENCIA");
            regular.newLineAtOffset(0, n);
            regular.setFont(PDType1Font.HELVETICA, 10);
            regular.showText("INSTITUTO DE EDUCACION INTERCULTURAL CAMPINTA GUAZU GLORIA PEREZ");
            regular.newLineAtOffset(0, n);
            DateTimeFormatter formatter4 = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'del año' yyyy", new Locale("es", "AR"));
            String fechaFormateada4 = fechaActual.format(formatter4);
            System.out.println("Fecha formateada: " + fechaFormateada4);
            regular.showText("FECHA: " + fechaFormateada4);
            regular.endText();
            regular.close();
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
            PDPageContentStream cuadro = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            margin = 20;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (2 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 650;//yStartNewPage;
            float bottomMargin = 30;
            float auxmargin = 20;
            float yPosition = 300;

            //==========================================

            BaseTable table = new BaseTable(yStart + 30, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            int espaciado = 0;
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 600);//X=40
            Row<PDPage> headerRow = table.createRow(20);
            int a = 5;
            Cell<PDPage> cell = headerRow.createCell(5, "ID");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setTextRotated(true);
            System.out.println(cell.getHeight());
            float h = cell.getInnerWidth();
            cell = headerRow.createCell(15, "Materia");//30
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            // cell.setTextRotated(true);
            float b = cell.getInnerWidth();
            cuadro.setCharacterSpacing(espaciado);
            cell = headerRow.createCell(25, "Docente");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float bb = cell.getInnerWidth();
            cuadro.setCharacterSpacing(espaciado);
            cell = headerRow.createCell(10, "DNI");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float c = cell.getExtraWidth();

            cell = headerRow.createCell(10, "Estado");//11
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float r = cell.getInnerWidth();

            cell = headerRow.createCell(8, "Entrada");//11
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float rr = cell.getInnerWidth();
            cell = headerRow.createCell(8, "Salida");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float d = cell.getExtraWidth();
            cell.setFont(PDType1Font.HELVETICA);

            cell = headerRow.createCell(17, "Obs");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float E = cell.getExtraWidth();
            cell.setFont(PDType1Font.HELVETICA);

            table.draw();
            BaseTable Materiasaño = new BaseTable(yStart - headerRow.getHeight() + 1 + 30, yStartNewPage + 1, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            float H = 0;
            int j = 1;


            List<AsistenciaDetalleDTO> asistencias = asistenciaPersonalService.obtenerAsistenciasPorFecha(fechaActual);
            for (AsistenciaDetalleDTO dato : asistencias) {
                SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat formatoNuevo = new SimpleDateFormat("dd-MM-yyyy");
                int nk = 6;
                Row<PDPage> rew = Materiasaño.createRow(5);//19
                // Celda para la columna "Orden"
                Cell<PDPage> cellOrden = rew.createCell(5, String.valueOf(j));
                cellOrden.setAlign(HorizontalAlignment.CENTER);
                cellOrden.setValign(VerticalAlignment.MIDDLE);
                cellOrden.setFont(PDType1Font.HELVETICA);
                cellOrden.setFontSize(nk);
                Cell<PDPage> cellNombreMateria = rew.createCell(15, dato.getMateriaNombre());
                cellNombreMateria.setFontSize(nk);
                cellNombreMateria.setValign(VerticalAlignment.MIDDLE);
                cellNombreMateria.setFont(PDType1Font.HELVETICA);
                // Celda para la columna "Nota Final"
                Cell<PDPage> cellNotaFinal = rew.createCell(25, dato.getApellido() + ", " + dato.getNombre());
                cellNotaFinal.setFontSize(nk);
                cellNotaFinal.setAlign(HorizontalAlignment.CENTER);
                cellNotaFinal.setValign(VerticalAlignment.MIDDLE);
                cellNotaFinal.setFont(PDType1Font.HELVETICA);
                Cell<PDPage> cellNotaFinall = rew.createCell(10, dato.getDni());
                cellNotaFinall.setFontSize(nk);
                cellNotaFinall.setAlign(HorizontalAlignment.CENTER);
                cellNotaFinall.setValign(VerticalAlignment.MIDDLE);
                cellNotaFinall.setFont(PDType1Font.HELVETICA);

                Cell<PDPage> cellNotaFinalll = rew.createCell(10, dato.getEstado());
                cellNotaFinalll.setFontSize(nk);
                cellNotaFinalll.setAlign(HorizontalAlignment.CENTER);
                cellNotaFinalll.setValign(VerticalAlignment.MIDDLE);
                cellNotaFinalll.setFont(PDType1Font.HELVETICA);

                Cell<PDPage> cellyear = rew.createCell(8, dato.getHoraEntrada());
                cellyear.setFontSize(nk);
                cellyear.setAlign(HorizontalAlignment.CENTER);
                cellyear.setValign(VerticalAlignment.MIDDLE);
                cellyear.setFont(PDType1Font.HELVETICA);
                Cell<PDPage> cellyeart = rew.createCell(8, dato.getHoraSalida());
                cellyeart.setFontSize(nk);
                cellyeart.setAlign(HorizontalAlignment.CENTER);
                cellyeart.setValign(VerticalAlignment.MIDDLE);
                cellyeart.setFont(PDType1Font.HELVETICA);
                Cell<PDPage> cellyeartt = rew.createCell(17, dato.getObservaciones());
                cellyeartt.setFontSize(nk);
                cellyeartt.setAlign(HorizontalAlignment.CENTER);
                cellyeartt.setValign(VerticalAlignment.MIDDLE);
                cellyeartt.setFont(PDType1Font.HELVETICA);
                float filaHeight = rew.getHeight();
                H = H + filaHeight;
                j++;
            }
            Materiasaño.draw();
            cuadro.endText();
            cuadro.close();
            //============================
            PDPageContentStream pie = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            n = -17;//distancia entre lineas
            pie.beginText();
            pie.setFont(PDType1Font.HELVETICA_BOLD, 12);
            pie.newLineAtOffset(40, yStart - H - 70);
            pie.setFont(PDType1Font.HELVETICA, 10);
            n = -10;
            pie.newLineAtOffset(410, 0);
            pie.newLineAtOffset(5, n);
            pie.showText("Ausentes:______");
            pie.endText();
            pie.close();
            cuadro.close();
            //Documento.close();
            // Incrementar la fecha
            fechaActual = fechaActual.plusDays(1);
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
    return Documento;
}

    @Override
    public PDDocument generaPlanillaSeguimiento(String carreraId, String materiaId, Boolean inscripto) {
        PDImageXObject Iesc1, Iesc2;
        MateriaCarrera materiaCarrera = this.materiaCarreraRepository.findByCarrera_CarreraIdAndMateria_MateriaId(carreraId, materiaId).get();
        String materia = materiaCarrera.getMateria().getMateriaNombre();
        String profe = String.valueOf(materiaCarrera.getFmcDocente());
        String modalidad = materiaCarrera.getMateria().getMateriaModalidad();
        String año = materiaCarrera.getMateria().getMateriaNivel();
        String division = materiaCarrera.getDivision();
        String turno = materiaCarrera.getTurno();
        String fechacierre = String.valueOf(materiaCarrera.getFecha());
        String tecnicatura = materiaCarrera.getCarrera().getCarreraNombre();
        String regimen = materiaCarrera.getMateria().getMateriaRegimen();
        List<NotaCursadaDTO> cursadas;
        if (materiaCarrera.getMateria().getMateriaNivel().equals("1ro")) {
            cursadas = this.notaService.findNotasByCarreraAndMateriaAll(carreraId, materiaId, inscripto);
        } else {
            cursadas = this.notaService.findNotasByCarreraAndMateria(carreraId, materiaId, inscripto);
        }
//        List<NotaCursadaDTO> cursadas=this.notaService.findNotasByCarreraAndMateria(carreraId, materiaId, inscripto);
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");
            PDRectangle a4 = PDRectangle.A4;
            PDRectangle a4Landscape = new PDRectangle(a4.getHeight(), a4.getWidth());
            PDPage Pagina = new PDPage(a4Landscape);
            Documento.addPage(Pagina);
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 10;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 9;
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD; margin = 60;
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (3 * margin) - 20;
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 450;
            float bottomMargin = 70;
            float auxmargin = 20;
            float yPosition = 300;
            BaseTable Cursoaño = new BaseTable(yStart - 55 + 1, yStart - 55 + 1, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            float H = 0;
            for (int i = 0; i < cursadas.size(); i++) {
                String dni = "-";
                String apellido = "-";
                String nota_c = "-";
                String status = "-";

                String primerParcial = "-";
                String recuperatorio1 = "-";
                String segundoParcial = "-";
                String recuperatorio2 = "-";
                String trabajosPracticos = "-";
                String asistencia = "-";
                String coloquio = "-";
                String trabajoInstitucional = "-";
                String estado = "-";

                if (i < cursadas.size()) {
                    NotaCursadaDTO fResultado = cursadas.get(i);

                    dni = fResultado.getPersonaDni() != null ? fResultado.getPersonaDni() : "-";
                    apellido = (fResultado.getPersonaApellido() != null ? fResultado.getPersonaApellido() : "") + ", " +
                            (fResultado.getPersonaNombre() != null ? fResultado.getPersonaNombre() : "");

                    String numero = fResultado.getNotaCalificacionNotaNumero() != null ? fResultado.getNotaCalificacionNotaNumero().toString() : "-";
                    String letraL = fResultado.getNotaCalificacionNotaLetra() != null ? fResultado.getNotaCalificacionNotaLetra() : "-";
                    nota_c = numero + " (" + letraL + ")";
                    status = fResultado.getCursadaStatus() != null ? fResultado.getCursadaStatus() : "-";
                    primerParcial = fResultado.getPrimerParcial() != null ? fResultado.getPrimerParcial().toString() : "-";
                    recuperatorio1 = fResultado.getRecuperatorio1() != null ? fResultado.getRecuperatorio1().toString() : "-";
                    segundoParcial = fResultado.getSegundoParcial() != null ? fResultado.getSegundoParcial().toString() : "-";
                    recuperatorio2 = fResultado.getRecuperatorio2() != null ? fResultado.getRecuperatorio2().toString() : "-";
                    trabajosPracticos = fResultado.getTrabajosPracticos() != null ? fResultado.getTrabajosPracticos().toString() : "-";
                    asistencia = fResultado.getAsistencia() != null ? fResultado.getAsistencia().toString() : "-";
                    coloquio = fResultado.getColoquio() != null ? fResultado.getColoquio().toString() : "-";
                    trabajoInstitucional = fResultado.getTrabajoInstitucional() != null ? fResultado.getTrabajoInstitucional().toString() : "-";


                  estado=fResultado.getNotaEstado() != null ? fResultado.getNotaEstado() : "-";

//                    estado = (!materiaCarrera.getMateria().getMateriaRegimen().equals("1ER CUATRIMESTRE")
//                            && LocalDate.now().getMonthValue() < 11)
//                            ? "Cursando"
//                            : (fResultado.getNotaEstado() != null ? fResultado.getNotaEstado() : "-");

                }

                // Imprimir todos los valores
                System.out.println("DNI: " + dni);
                System.out.println("Apellido y Nombre: " + apellido);
                System.out.println("Nota: " + nota_c);
                System.out.println("Estado: " + status);
                System.out.println("Primer Parcial: " + primerParcial);
                System.out.println("Recuperatorio 1: " + recuperatorio1);
                System.out.println("Segundo Parcial: " + segundoParcial);
                System.out.println("Recuperatorio 2: " + recuperatorio2);
                System.out.println("Trabajos Prácticos: " + trabajosPracticos);
                System.out.println("Asistencia: " + asistencia);
                System.out.println("Coloquio: " + coloquio);
                System.out.println("Trabajo Institucional: " + trabajoInstitucional);

                Color color;
                color = Color.BLACK;//==============borrar
                String[] partes = regimen.split("-");
                String ultimaParte = partes[partes.length - 1].trim(); // "EXAMEN FINAL"
//                if ("Provisoria".equals(status) && (!ultimaParte.equals("EXAMEN FINAL"))) {
//                    System.out.println("::::::" + regimen);
//                    color = Color.RED;
//                } else {
//                    color = Color.BLACK;
//                }
                Row<PDPage> rew = Cursoaño.createRow(5);
                float altura = 0;
                // Celda para la columna "Orden"
                Cell<PDPage> cell = rew.createCell(5, String.valueOf(i + 1));//año
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFont(PDType1Font.HELVETICA);
                cell.setFontSize(letra);
                cell.setTextColor(color);
                // Celda para la columna "Nombre Materia"
                Cell<PDPage> cellNombreMateria = rew.createCell(8, dni);
                cellNombreMateria.setAlign(HorizontalAlignment.CENTER);
                cellNombreMateria.setValign(VerticalAlignment.MIDDLE);
                cellNombreMateria.setFont(PDType1Font.HELVETICA);
                cellNombreMateria.setFontSize(letra);
                cellNombreMateria.setTextColor(color);
                // Celda para la columna "Apellido"
                Cell<PDPage> cellNotaFinal = rew.createCell(25.1f, apellido);
                cellNotaFinal.setAlign(HorizontalAlignment.LEFT);
                cellNotaFinal.setValign(VerticalAlignment.MIDDLE);
                cellNotaFinal.setFont(PDType1Font.HELVETICA);
                cellNotaFinal.setTextColor(color);
                // Celda para la columna "1er Parcial"
                Cell<PDPage> cellEscrito = rew.createCell(5.8f, primerParcial);
                cellEscrito.setAlign(HorizontalAlignment.CENTER);
                cellEscrito.setValign(VerticalAlignment.MIDDLE);
                cellEscrito.setFont(PDType1Font.HELVETICA);
                cellEscrito.setTextColor(color);
                // Celda para la columna "rec"
                Cell<PDPage> cellOral = rew.createCell(5.0f, recuperatorio1);
                cellOral.setAlign(HorizontalAlignment.CENTER);
                cellOral.setValign(VerticalAlignment.MIDDLE);
                cellOral.setFont(PDType1Font.HELVETICA);
                cellOral.setTextColor(color);
                // Celda para la columna "2do Parcial"
                Cell<PDPage> cellPromedio = rew.createCell(6.0f, segundoParcial);
                cellPromedio.setAlign(HorizontalAlignment.CENTER);
                cellPromedio.setValign(VerticalAlignment.MIDDLE);
                cellPromedio.setFont(PDType1Font.HELVETICA);
                cellPromedio.setTextColor(color);
                // Celda para la columna "Rec"
                Cell<PDPage> cellBescrito = rew.createCell(5.0f, recuperatorio2);
                cellBescrito.setAlign(HorizontalAlignment.CENTER);
                cellBescrito.setValign(VerticalAlignment.MIDDLE);
                cellBescrito.setFont(PDType1Font.HELVETICA);
                cellBescrito.setTextColor(color);
                // Celda para la columna "Trabajos Practicos"
                Cell<PDPage> cellBoral = rew.createCell(7, trabajosPracticos);//8
                cellBoral.setAlign(HorizontalAlignment.CENTER);
                cellBoral.setValign(VerticalAlignment.MIDDLE);
                cellBoral.setFont(PDType1Font.HELVETICA);
                cellBoral.setTextColor(color);
                // Celda para la columna "Asistencia"
                Cell<PDPage> cellcond = rew.createCell(7, asistencia);//8
                cellcond.setAlign(HorizontalAlignment.CENTER);
                cellcond.setValign(VerticalAlignment.MIDDLE);
                cellcond.setFont(PDType1Font.HELVETICA);
                cellcond.setTextColor(color);
                // Celda para la columna "Coloquio"
                Cell<PDPage> cellcond1 = rew.createCell(6, coloquio);//8
                cellcond1.setAlign(HorizontalAlignment.CENTER);
                cellcond1.setValign(VerticalAlignment.MIDDLE);
                cellcond1.setFont(PDType1Font.HELVETICA);
                cellcond1.setTextColor(color);
                // Celda para la columna "trabajo institucional"
                Cell<PDPage> cellcond2 = rew.createCell(8, trabajoInstitucional);//8
                cellcond2.setAlign(HorizontalAlignment.CENTER);
                cellcond2.setValign(VerticalAlignment.MIDDLE);
                cellcond2.setFont(PDType1Font.HELVETICA);
                cellcond2.setTextColor(color);
                // Celda para la columna "Promedio Final"
                Cell<PDPage> cellcond3 = rew.createCell(13, nota_c);
                cellcond3.setAlign(HorizontalAlignment.CENTER);
                cellcond3.setValign(VerticalAlignment.MIDDLE);
                cellcond3.setFont(PDType1Font.HELVETICA);
                cellcond3.setTextColor(color);
                // Celda para la columna "Condicion"
                Cell<PDPage> cellcond4 = rew.createCell(10, estado);//8
                cellcond4.setAlign(HorizontalAlignment.CENTER);
                cellcond4.setValign(VerticalAlignment.MIDDLE);
                cellcond4.setFont(PDType1Font.HELVETICA);
                cellcond4.setTextColor(color);
                // Celda para la columna "Firma"
//                Cell<PDPage> cellcond5 = rew.createCell(10.1f, "");//8
//                cellcond5.setAlign(HorizontalAlignment.CENTER);
//                cellcond5.setValign(VerticalAlignment.MIDDLE);
//                cellcond5.setFont(PDType1Font.HELVETICA);
//                cellcond5.setTextColor(color);
                float filaHeight = rew.getHeight();
                H = H + filaHeight;
            }
            Cursoaño.draw();
//            cuadro.endText();
//            cuadro.close();
            int i = 1;
            Personal personal = this.personalService.findById(String.valueOf(materiaCarrera.getFmcDocente())).get();
            for (PDPage page : Documento.getPages()) {
                addHeaderA4(Documento, page, Iesc1, materiaCarrera, personal);
                addTableHeaderA4(Documento, page);
                addFooterA4(Documento, page, i, Documento.getNumberOfPages());
                i++;
            }
            int numPagina = 0;
            String[] parts = regimen.split("-");
            if (parts.length > 1) {
                String cond = parts[1].trim(); // Obtener la parte después del guion y quitar espacios

                // Usar switch para evaluar el valor de cond
                switch (cond) {
                    case "PROMOCION":
                        numPagina = 1;
                        System.out.println("La condición es: PROMOCION");
                        break;
                    case "EXAMEN FINAL":
                        numPagina = 2;
                        System.out.println("La condición es: EXAMEN FINAL");
                        break;
                    case "PROMOCION/EXAMEN FINAL":
                        numPagina = 0;
                        System.out.println("La condición es: PROMOCION/EXAMEN FINAL");
                        break;
                }
            } else {
                System.out.println("No se encontró un guion en la cadena.");
            }
        } catch (IOException e) {
        }
        return Documento;
    }


    public PDDocument generaPlanillaSeguimientoLegal(String carreraId, String materiaId, Boolean inscripto) {
        PDImageXObject Iesc1, Iesc2;
        MateriaCarrera materiaCarrera = this.materiaCarreraRepository.findByCarrera_CarreraIdAndMateria_MateriaId(carreraId, materiaId).get();
        String materia = materiaCarrera.getMateria().getMateriaNombre();
        String profe = String.valueOf(materiaCarrera.getFmcDocente());
        String modalidad = materiaCarrera.getMateria().getMateriaModalidad();
        String año = materiaCarrera.getMateria().getMateriaNivel();
        String division = materiaCarrera.getDivision();
        String turno = materiaCarrera.getTurno();
        String fechacierre = String.valueOf(materiaCarrera.getFecha());
        String tecnicatura = materiaCarrera.getCarrera().getCarreraNombre();
        String regimen = materiaCarrera.getMateria().getMateriaRegimen();
        List<NotaCursadaDTO> cursadas;
        if (materiaCarrera.getMateria().getMateriaNivel().equals("1ro")) {
            cursadas = this.notaService.findNotasByCarreraAndMateriaAll(carreraId, materiaId, inscripto);
        } else {
            cursadas = this.notaService.findNotasByCarreraAndMateria(carreraId, materiaId, inscripto);
        }
//        List<NotaCursadaDTO> cursadas=this.notaService.findNotasByCarreraAndMateria(carreraId, materiaId, inscripto);
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");
            PDRectangle a4 = PDRectangle.LEGAL;
            PDRectangle a4Landscape = new PDRectangle(a4.getHeight(), a4.getWidth());
            PDPage Pagina = new PDPage(a4Landscape);
            Documento.addPage(Pagina);
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 9;
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD; margin = 60;
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (3 * margin) - 20;
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 450;
            float bottomMargin = 70;
            float auxmargin = 40;
            float yPosition = 300;
            BaseTable Cursoaño = new BaseTable(yStart - 55 + 1, yStart - 55 + 1, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            float H = 0;
            for (int i = 0; i < cursadas.size(); i++) {
                String dni = "-";
                String apellido = "-";
                String nota_c = "-";
                String status = "-";

                String primerParcial = "-";
                String recuperatorio1 = "-";
                String segundoParcial = "-";
                String recuperatorio2 = "-";
                String trabajosPracticos = "-";
                String asistencia = "-";
                String coloquio = "-";
                String trabajoInstitucional = "-";
                String estado = "-";

                if (i < cursadas.size()) {
                    NotaCursadaDTO fResultado = cursadas.get(i);

                    dni = fResultado.getPersonaDni() != null ? fResultado.getPersonaDni() : "-";
                    apellido = (fResultado.getPersonaApellido() != null ? fResultado.getPersonaApellido() : "") + ", " +
                            (fResultado.getPersonaNombre() != null ? fResultado.getPersonaNombre() : "");

                    String numero = fResultado.getNotaCalificacionNotaNumero() != null ? fResultado.getNotaCalificacionNotaNumero().toString() : "-";
                    String letraL = fResultado.getNotaCalificacionNotaLetra() != null ? fResultado.getNotaCalificacionNotaLetra() : "-";
                    nota_c = numero + " (" + letraL + ")";
                    status = fResultado.getCursadaStatus() != null ? fResultado.getCursadaStatus() : "-";
                    primerParcial = fResultado.getPrimerParcial() != null ? fResultado.getPrimerParcial().toString() : "-";
                    recuperatorio1 = fResultado.getRecuperatorio1() != null ? fResultado.getRecuperatorio1().toString() : "-";
                    segundoParcial = fResultado.getSegundoParcial() != null ? fResultado.getSegundoParcial().toString() : "-";
                    recuperatorio2 = fResultado.getRecuperatorio2() != null ? fResultado.getRecuperatorio2().toString() : "-";
                    trabajosPracticos = fResultado.getTrabajosPracticos() != null ? fResultado.getTrabajosPracticos().toString() : "-";
                    asistencia = fResultado.getAsistencia() != null ? fResultado.getAsistencia().toString() : "-";
                    coloquio = fResultado.getColoquio() != null ? fResultado.getColoquio().toString() : "-";
                    trabajoInstitucional = fResultado.getTrabajoInstitucional() != null ? fResultado.getTrabajoInstitucional().toString() : "-";


//                    estado=fResultado.getNotaEstado() != null ? fResultado.getNotaEstado() : "-";

                    estado = (!materiaCarrera.getMateria().getMateriaRegimen().equals("1ER CUATRIMESTRE")
                            && LocalDate.now().getMonthValue() < 11)
                            ? "Cursando"
                            : (fResultado.getNotaEstado() != null ? fResultado.getNotaEstado() : "-");

                }

                // Imprimir todos los valores
                System.out.println("DNI: " + dni);
                System.out.println("Apellido y Nombre: " + apellido);
                System.out.println("Nota: " + nota_c);
                System.out.println("Estado: " + status);
                System.out.println("Primer Parcial: " + primerParcial);
                System.out.println("Recuperatorio 1: " + recuperatorio1);
                System.out.println("Segundo Parcial: " + segundoParcial);
                System.out.println("Recuperatorio 2: " + recuperatorio2);
                System.out.println("Trabajos Prácticos: " + trabajosPracticos);
                System.out.println("Asistencia: " + asistencia);
                System.out.println("Coloquio: " + coloquio);
                System.out.println("Trabajo Institucional: " + trabajoInstitucional);

                Color color;
                color = Color.BLACK;//==============borrar
                String[] partes = regimen.split("-");
                String ultimaParte = partes[partes.length - 1].trim(); // "EXAMEN FINAL"
//                if ("Provisoria".equals(status) && (!ultimaParte.equals("EXAMEN FINAL"))) {
//                    System.out.println("::::::" + regimen);
//                    color = Color.RED;
//                } else {
//                    color = Color.BLACK;
//                }
                Row<PDPage> rew = Cursoaño.createRow(5);
                float altura = 0;
                // Celda para la columna "Orden"
                Cell<PDPage> cell = rew.createCell(5, String.valueOf(i + 1));//año
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFont(PDType1Font.HELVETICA);
                cell.setFontSize(letra);
                cell.setTextColor(color);
                // Celda para la columna "Nombre Materia"
                Cell<PDPage> cellNombreMateria = rew.createCell(8, dni);
                cellNombreMateria.setAlign(HorizontalAlignment.CENTER);
                cellNombreMateria.setValign(VerticalAlignment.MIDDLE);
                cellNombreMateria.setFont(PDType1Font.HELVETICA);
                cellNombreMateria.setFontSize(letra);
                cellNombreMateria.setTextColor(color);
                // Celda para la columna "Apellido"
                Cell<PDPage> cellNotaFinal = rew.createCell(20, apellido);
                cellNotaFinal.setAlign(HorizontalAlignment.LEFT);
                cellNotaFinal.setValign(VerticalAlignment.MIDDLE);
                cellNotaFinal.setFont(PDType1Font.HELVETICA);
                cellNotaFinal.setTextColor(color);
                // Celda para la columna "1er Parcial"
                Cell<PDPage> cellEscrito = rew.createCell(4.9f, primerParcial);
                cellEscrito.setAlign(HorizontalAlignment.CENTER);
                cellEscrito.setValign(VerticalAlignment.MIDDLE);
                cellEscrito.setFont(PDType1Font.HELVETICA);
                cellEscrito.setTextColor(color);
                // Celda para la columna "rec"
                Cell<PDPage> cellOral = rew.createCell(5.0f, recuperatorio1);
                cellOral.setAlign(HorizontalAlignment.CENTER);
                cellOral.setValign(VerticalAlignment.MIDDLE);
                cellOral.setFont(PDType1Font.HELVETICA);
                cellOral.setTextColor(color);
                // Celda para la columna "2do Parcial"
                Cell<PDPage> cellPromedio = rew.createCell(5.0f, segundoParcial);
                cellPromedio.setAlign(HorizontalAlignment.CENTER);
                cellPromedio.setValign(VerticalAlignment.MIDDLE);
                cellPromedio.setFont(PDType1Font.HELVETICA);
                cellPromedio.setTextColor(color);
                // Celda para la columna "Rec"
                Cell<PDPage> cellBescrito = rew.createCell(5.0f, recuperatorio2);
                cellBescrito.setAlign(HorizontalAlignment.CENTER);
                cellBescrito.setValign(VerticalAlignment.MIDDLE);
                cellBescrito.setFont(PDType1Font.HELVETICA);
                cellBescrito.setTextColor(color);
                // Celda para la columna "Trabajos Practicos"
                Cell<PDPage> cellBoral = rew.createCell(6, trabajosPracticos);//8
                cellBoral.setAlign(HorizontalAlignment.CENTER);
                cellBoral.setValign(VerticalAlignment.MIDDLE);
                cellBoral.setFont(PDType1Font.HELVETICA);
                cellBoral.setTextColor(color);
                // Celda para la columna "Asistencia"
                Cell<PDPage> cellcond = rew.createCell(6, asistencia);//8
                cellcond.setAlign(HorizontalAlignment.CENTER);
                cellcond.setValign(VerticalAlignment.MIDDLE);
                cellcond.setFont(PDType1Font.HELVETICA);
                cellcond.setTextColor(color);
                // Celda para la columna "Coloquio"
                Cell<PDPage> cellcond1 = rew.createCell(6, coloquio);//8
                cellcond1.setAlign(HorizontalAlignment.CENTER);
                cellcond1.setValign(VerticalAlignment.MIDDLE);
                cellcond1.setFont(PDType1Font.HELVETICA);
                cellcond1.setTextColor(color);
                // Celda para la columna "trabajo institucional"
                Cell<PDPage> cellcond2 = rew.createCell(7, trabajoInstitucional);//8
                cellcond2.setAlign(HorizontalAlignment.CENTER);
                cellcond2.setValign(VerticalAlignment.MIDDLE);
                cellcond2.setFont(PDType1Font.HELVETICA);
                cellcond2.setTextColor(color);
                // Celda para la columna "Promedio Final"
                Cell<PDPage> cellcond3 = rew.createCell(10, nota_c);
                cellcond3.setAlign(HorizontalAlignment.CENTER);
                cellcond3.setValign(VerticalAlignment.MIDDLE);
                cellcond3.setFont(PDType1Font.HELVETICA);
                cellcond3.setTextColor(color);
                // Celda para la columna "Condicion"
                Cell<PDPage> cellcond4 = rew.createCell(8, estado);//8
                cellcond4.setAlign(HorizontalAlignment.CENTER);
                cellcond4.setValign(VerticalAlignment.MIDDLE);
                cellcond4.setFont(PDType1Font.HELVETICA);
                cellcond4.setTextColor(color);
                // Celda para la columna "Firma"
                Cell<PDPage> cellcond5 = rew.createCell(9.1f, "");//8
                cellcond5.setAlign(HorizontalAlignment.CENTER);
                cellcond5.setValign(VerticalAlignment.MIDDLE);
                cellcond5.setFont(PDType1Font.HELVETICA);
                cellcond5.setTextColor(color);
                float filaHeight = rew.getHeight();
                H = H + filaHeight;
            }
            Cursoaño.draw();
//            cuadro.endText();
//            cuadro.close();
            int i = 1;
            Personal personal = this.personalService.findById(String.valueOf(materiaCarrera.getFmcDocente())).get();
            for (PDPage page : Documento.getPages()) {
                addHeaderOffice(Documento, page, Iesc1, materiaCarrera, personal);
                addTableHeader(Documento, page);
                addFooterOffice(Documento, page, i, Documento.getNumberOfPages());
                i++;
            }
            int numPagina = 0;
            String[] parts = regimen.split("-");
            if (parts.length > 1) {
                String cond = parts[1].trim(); // Obtener la parte después del guion y quitar espacios

                // Usar switch para evaluar el valor de cond
                switch (cond) {
                    case "PROMOCION":
                        numPagina = 1;
                        System.out.println("La condición es: PROMOCION");
                        break;
                    case "EXAMEN FINAL":
                        numPagina = 2;
                        System.out.println("La condición es: EXAMEN FINAL");
                        break;
                    case "PROMOCION/EXAMEN FINAL":
                        numPagina = 0;
                        System.out.println("La condición es: PROMOCION/EXAMEN FINAL");
                        break;
                }
            } else {
                System.out.println("No se encontró un guion en la cadena.");
            }
        } catch (IOException e) {
        }
        return Documento;
    }

    private void addHeaderOffice(PDDocument document, PDPage page, PDImageXObject image, MateriaCarrera materiaCarrera, Personal personal) {
        try {
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
            int n = -10;
            contentStream.drawImage(image, 30, 550, 40, 40);
            contentStream.beginText();
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            contentStream.newLineAtOffset(400, 600);
            contentStream.showText("INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL");
            contentStream.newLineAtOffset(40, n);
            contentStream.showText("“CAMPINTA GUAZU GLORIA PEREZ”");
            contentStream.newLineAtOffset(-25, n);
            contentStream.showText("Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15");
            contentStream.newLineAtOffset(-1, n);
            contentStream.showText("Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370");
            contentStream.newLineAtOffset(-50, n);
            contentStream.showText("(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina");
            contentStream.newLineAtOffset(-345, n);
            contentStream.showText("____________________________________________________________________________________________________________________________________________________________________________________________________________________________________");
            contentStream.endText();
            contentStream.close();
            PDPageContentStream regular = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
            n = -18;//distancia entre lineas
            float y = 500;
            float leading = -14;
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 12);
            regular.newLineAtOffset(40, 520);
            regular.showText("Planilla de calificaciones");
            regular.endText();

// Columna izquierda
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA, 10);
            regular.newLineAtOffset(40, y);
            regular.showText("CARRERA: " + materiaCarrera.getCarrera().getCarreraNombre());
            regular.newLineAtOffset(0, leading);
            regular.showText("UNIDAD CURRICULAR: " + materiaCarrera.getMateria().getMateriaNombre());
            regular.newLineAtOffset(0, leading);
            regular.showText("PROFESOR: " + personal.getPersonalApellido() + ", " + personal.getPersonalNombre());
            regular.newLineAtOffset(0, leading);
            regular.showText("RÉGIMEN: " + materiaCarrera.getMateria().getMateriaRegimen() + " - " + materiaCarrera.getMateria().getMateriaModalidad());
            regular.endText();

// Columna derecha (alineada con la línea de "CARRERA")
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA, 10);
            regular.newLineAtOffset(500, y);  // MISMA altura que y = 500
            regular.showText("CURSO: " + materiaCarrera.getMateria().getMateriaNivel());
            regular.newLineAtOffset(0, leading);
            regular.showText("DIVISIÓN: " + materiaCarrera.getDivision());
            regular.newLineAtOffset(0, leading);
            regular.showText("TURNO: " + materiaCarrera.getTurno());
            regular.newLineAtOffset(0, leading);
            regular.showText("FECHA: " + materiaCarrera.getFecha());
            regular.endText();

            regular.close();


            //=====================
            float margin = 20;
            float yStartNewPage = page.getMediaBox().getHeight() - (3 * margin) - 20;
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 510;
            float bottomMargin = 70;
            float auxmargin = 40;//55
            float yPosition = 300;
            BaseTable filafecha = new BaseTable(yStart, yStart + 100, bottomMargin, tableWidth, 835 + auxmargin, document, page, true, drawContent);
            Row<PDPage> cabfilafecha = filafecha.createRow(25);
            Cell<PDPage> cabfilaabajofecha = cabfilafecha.createCell(5, "Libro");
            cabfilaabajofecha.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajofecha.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajofecha.setFontSize(12);
            cabfilaabajofecha = cabfilafecha.createCell(5, "Folio");
            cabfilaabajofecha.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajofecha.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajofecha.setFontSize(12);
            // Crear una nueva fila debajo de cabfilafecha
            Row<PDPage> nuevaFilaFecha = filafecha.createRow(30);
            Cell<PDPage> celdaDia = nuevaFilaFecha.createCell(5, "");
            celdaDia.setAlign(HorizontalAlignment.CENTER);
            celdaDia.setValign(VerticalAlignment.MIDDLE);
            celdaDia.setFontSize(5);
            Cell<PDPage> celdaMes = nuevaFilaFecha.createCell(5, "");
            celdaMes.setAlign(HorizontalAlignment.CENTER);
            celdaMes.setValign(VerticalAlignment.MIDDLE);
            celdaMes.setFontSize(8);
            filafecha.draw();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void addFooterOffice(PDDocument document, PDPage page, int i, int total) {
        try {
            PDPageContentStream contentStream = new PDPageContentStream(document, page,
                    PDPageContentStream.AppendMode.APPEND, true);

            float fontSize = 10;
            float yBase = 60;
            float leading = 16;

            // ===== BLOQUE IZQUIERDO =====
            contentStream.beginText();
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(40, 30);
            contentStream.showText("Docente:__________________________________________Firma:__________________________, SAN SALVADOR DE JUJUY, __________ de _________________ del 20______");
            contentStream.newLineAtOffset(0, -leading);
            contentStream.showText("Página " + i + " / " + total);
            contentStream.endText();

            // ===== BLOQUE DERECHO =====
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(855, yBase);  // MISMA ALTURA Y
            contentStream.showText("    Promocionados: _____");
            contentStream.newLineAtOffset(0, -leading);
            contentStream.showText("    Regularizados: ______");
            contentStream.newLineAtOffset(0, -leading);
            contentStream.showText("                 Libres: ______");
            contentStream.newLineAtOffset(0, -leading);
            contentStream.showText("Total de alumnos: ______");
            contentStream.endText();
            contentStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void addTableHeader(PDDocument Documento, PDPage page) throws IOException {

        PDRectangle mediabox = page.getMediaBox();
        float margin = 20;
        float width = mediabox.getWidth() - 4 * margin;
        float X = mediabox.getLowerLeftX() + margin;
        float Y = mediabox.getUpperRightY() - margin;
        List<String> lineas = new ArrayList<String>();
        int letra = 9;
        PDType1Font normal = PDType1Font.HELVETICA;
        PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
        PDPageContentStream cuadro = new PDPageContentStream(Documento, page, PDPageContentStream.AppendMode.APPEND, true);
        margin = 60;
        float yStartNewPage = page.getMediaBox().getHeight() - (3 * margin) - 20;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float yStart = 450;
        float bottomMargin = 70;
        float auxmargin = 40;
        float yPosition = 300;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, page, true, drawContent);
        int espaciado = 0;
        cuadro.beginText();
        cuadro.newLineAtOffset(-10, 600);
        Row<PDPage> headerRow = table.createRow(55);
        int a = 5;
        Cell<PDPage> cell = headerRow.createCell(5, "Nº");
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setFontSize(letra);
        //cell.setTextRotated(true);
        System.out.println(cell.getHeight());
        float b = cell.getInnerWidth();
        cuadro.setCharacterSpacing(espaciado);
        cell = headerRow.createCell(8, "DNI");//15
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setFontSize(letra);
        float c = cell.getExtraWidth();
        cell = headerRow.createCell(20, "APELLIDO Y NOMBRE");//30
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        float r = cell.getInnerWidth();
        cell.setFontSize(letra);
        // Calcular la posición yStart para la nueva tabla basado en la altura de la tabla anterior y un margen---293.8f
        BaseTable calificaciones_bolillas = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, 292.1f + auxmargin, Documento, page, true, drawContent);
        Row<PDPage> cabecalfbol = calificaciones_bolillas.createRow(27);
        Cell<PDPage> cab = cabecalfbol.createCell(72.1f, "CALIFICACIONES");//49
        cab.setAlign(HorizontalAlignment.CENTER);
        cab.setValign(VerticalAlignment.MIDDLE);
        cab.setFontSize(letra);
        BaseTable fila = new BaseTable(yStart - 26, yStartNewPage, bottomMargin, tableWidth, 292.1f + auxmargin, Documento, page, true, drawContent);
        Row<PDPage> cabfila = fila.createRow(24);//25

        Cell<PDPage> cabfilaabajo = cabfila.createCell(5, "1er Parcial");//8.2f
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(letra);
        cabfilaabajo = cabfila.createCell(5, "Rec.");
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(letra);
        cabfilaabajo = cabfila.createCell(5, "2do Parcial");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(letra);
        cabfilaabajo = cabfila.createCell(5, "Rec.");//9
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(letra);
        cabfilaabajo = cabfila.createCell(6, "Trabajos Practicos");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(letra);
        cabfilaabajo = cabfila.createCell(6, "Asistencia");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(letra);
        cabfilaabajo = cabfila.createCell(6, "Coloquio");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(letra);
        cabfilaabajo = cabfila.createCell(7, "Trabajo Institucional");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(letra);
        cabfilaabajo = cabfila.createCell(10, "Promedio Final");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(letra);
        cabfilaabajo = cabfila.createCell(8, "Condicion");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(letra);
        cabfilaabajo = cabfila.createCell(9.1f, "Firma del Alumno");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(letra);
        fila.draw();
        table.draw();
        calificaciones_bolillas.draw();
        cuadro.endText();
        cuadro.close();
    }

    //    private static String fechaEnletra(Date fechaDate) {
    private static String fechaEnletra(LocalDate fechaDate) {
        String f = "";
        SimpleDateFormat formateador = new SimpleDateFormat("dd 'del mes de' MMMM 'del año' yyyy", new Locale("ES"));
        String fecha = formateador.format(fechaDate);
        f = fecha;
        return f;
    }


    //=======================

    private void addHeaderA4(PDDocument document, PDPage page, PDImageXObject image, MateriaCarrera materiaCarrera, Personal personal) {
        try {
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
            int n = -10;
            contentStream.drawImage(image, 30, 540, 50, 50);
            contentStream.beginText();
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            contentStream.newLineAtOffset(330, 578);
            contentStream.showText("INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL");
            contentStream.newLineAtOffset(40, n);
            contentStream.showText("“CAMPINTA GUAZU GLORIA PEREZ”");
            contentStream.newLineAtOffset(-25, n);
            contentStream.showText("Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15");
            contentStream.newLineAtOffset(-1, n);
            contentStream.showText("Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370");
            contentStream.newLineAtOffset(-50, n);
            contentStream.showText("(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina");
            contentStream.newLineAtOffset(-275, -5);
            contentStream.showText("__________________________________________________________________________________________________________________________________________________________________________________________________");
            contentStream.endText();
            contentStream.close();
            PDPageContentStream regular = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
            n = -18;//distancia entre lineas
            float y = 500;
            float leading = -14;
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 12);
            regular.newLineAtOffset(20, 518);
            regular.showText("Planilla de calificaciones");
            regular.endText();
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA, 10);
            regular.newLineAtOffset(20, y);
            regular.showText("CARRERA: " + materiaCarrera.getCarrera().getCarreraNombre());
            regular.newLineAtOffset(0, leading);
            regular.showText("UNIDAD CURRICULAR: " + materiaCarrera.getMateria().getMateriaNombre());
            regular.newLineAtOffset(0, leading);
            regular.showText("PROFESOR: " + personal.getPersonalApellido() + ", " + personal.getPersonalNombre());
            regular.newLineAtOffset(0, leading);
            regular.showText("RÉGIMEN: " + materiaCarrera.getMateria().getMateriaRegimen() + " - " + materiaCarrera.getMateria().getMateriaModalidad());
            regular.endText();

// Columna derecha (alineada con la línea de "CARRERA")
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA, 10);
            regular.newLineAtOffset(600, y);  // MISMA altura que y = 500
            regular.showText("CURSO: " + materiaCarrera.getMateria().getMateriaNivel());
            regular.newLineAtOffset(0, leading);
            regular.showText("DIVISIÓN: " + materiaCarrera.getDivision());
            regular.newLineAtOffset(0, leading);
            regular.showText("TURNO: " + materiaCarrera.getTurno());
            regular.newLineAtOffset(0, leading);
            regular.showText("FECHA: " + materiaCarrera.getFecha());
            regular.endText();
            regular.close();
            //=====================
            float margin = 20;
            float yStartNewPage = page.getMediaBox().getHeight() - (3 * margin) - 20;
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 510;
            float bottomMargin = 70;
            float auxmargin = 40;//55
            float yPosition = 300;
            BaseTable filafecha = new BaseTable(yStart, yStart + 100, bottomMargin, tableWidth, 700+ auxmargin, document, page, true, drawContent);
            Row<PDPage> cabfilafecha = filafecha.createRow(25);
            Cell<PDPage> cabfilaabajofecha = cabfilafecha.createCell(5, "Libro");
            cabfilaabajofecha.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajofecha.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajofecha.setFontSize(12);
            cabfilaabajofecha = cabfilafecha.createCell(5, "Folio");
            cabfilaabajofecha.setAlign(HorizontalAlignment.CENTER);
            cabfilaabajofecha.setValign(VerticalAlignment.MIDDLE);
            cabfilaabajofecha.setFontSize(12);
            // Crear una nueva fila debajo de cabfilafecha
            Row<PDPage> nuevaFilaFecha = filafecha.createRow(30);
            Cell<PDPage> celdaDia = nuevaFilaFecha.createCell(5, "");
            celdaDia.setAlign(HorizontalAlignment.CENTER);
            celdaDia.setValign(VerticalAlignment.MIDDLE);
            celdaDia.setFontSize(5);
            Cell<PDPage> celdaMes = nuevaFilaFecha.createCell(5, "");
            celdaMes.setAlign(HorizontalAlignment.CENTER);
            celdaMes.setValign(VerticalAlignment.MIDDLE);
            celdaMes.setFontSize(8);
            filafecha.draw();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void addFooterA4(PDDocument document, PDPage page, int i, int total) {
        try {
            PDPageContentStream contentStream = new PDPageContentStream(document, page,
                    PDPageContentStream.AppendMode.APPEND, true);

            float fontSize = 10;
            float yBase = 60;
            float leading = 16;

            // ===== BLOQUE IZQUIERDO =====
            contentStream.beginText();
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(20, 30);
            contentStream.showText("Docente:______________________________________Firma:__________________, SAN SALVADOR DE JUJUY, ______ de _________ del 20___");
            contentStream.newLineAtOffset(0, -leading);
            contentStream.showText("Página " + i + " / " + total);
            contentStream.endText();

            // ===== BLOQUE DERECHO =====
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(705, yBase);  // MISMA ALTURA Y
            contentStream.showText("    Promocionados: _____");
            contentStream.newLineAtOffset(0, -leading);
            contentStream.showText("    Regularizados: ______");
            contentStream.newLineAtOffset(0, -leading);
            contentStream.showText("                 Libres: ______");
            contentStream.newLineAtOffset(0, -leading);
            contentStream.showText("Total de alumnos: ______");
            contentStream.endText();
            contentStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void addTableHeaderA4(PDDocument Documento, PDPage page) throws IOException {

        PDRectangle mediabox = page.getMediaBox();
        float margin = 10;
        float width = mediabox.getWidth() - 4 * margin;
        float X = mediabox.getLowerLeftX() + margin;
        float Y = mediabox.getUpperRightY() - margin;
        List<String> lineas = new ArrayList<String>();
        int letra = 9;
        PDType1Font normal = PDType1Font.HELVETICA;
        PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
        PDPageContentStream cuadro = new PDPageContentStream(Documento, page, PDPageContentStream.AppendMode.APPEND, true);
        margin = 60;
        float yStartNewPage = page.getMediaBox().getHeight() - (3 * margin) - 20;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float yStart = 450;
        float bottomMargin = 70;
        float auxmargin = 20;
        float yPosition = 300;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, page, true, drawContent);
        int espaciado = 0;
        cuadro.beginText();
        cuadro.newLineAtOffset(-10, 600);
        Row<PDPage> headerRow = table.createRow(55);
        int a = 5;
        Cell<PDPage> cell = headerRow.createCell(5, "Nº");
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setFontSize(letra);
        //cell.setTextRotated(true);
        System.out.println(cell.getHeight());
        float b = cell.getInnerWidth();
        cuadro.setCharacterSpacing(espaciado);
        cell = headerRow.createCell(8, "DNI");//15
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setFontSize(letra);
        float c = cell.getExtraWidth();
        cell = headerRow.createCell(25.1f, "APELLIDO Y NOMBRE");//30
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        float r = cell.getInnerWidth();
        cell.setFontSize(letra);

        // Calcular la posición yStart para la nueva tabla basado en la altura de la tabla anterior y un margen---293.8f// 237
        BaseTable calificaciones_bolillas = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, 273.6f + auxmargin, Documento, page, true, drawContent);
        Row<PDPage> cabecalfbol = calificaciones_bolillas.createRow(27);
        Cell<PDPage> cab = cabecalfbol.createCell(73, "CALIFICACIONES");//49
        cab.setAlign(HorizontalAlignment.CENTER);
        cab.setValign(VerticalAlignment.MIDDLE);
        cab.setFontSize(letra);
        BaseTable fila = new BaseTable(yStart - 26, yStartNewPage, bottomMargin, tableWidth, 273.6f + auxmargin, Documento, page, true, drawContent);
        Row<PDPage> cabfila = fila.createRow(29);//25

        Cell<PDPage> cabfilaabajo = cabfila.createCell(6, "1er Parcial");//8.2f
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(8);
        cabfilaabajo = cabfila.createCell(5, "Rec.");
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(8);
        cabfilaabajo = cabfila.createCell(6, "2do Parcial");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(8);
        cabfilaabajo = cabfila.createCell(5, "Rec.");//9
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(8);
        cabfilaabajo = cabfila.createCell(7, "Trabajos Practicos");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(8);
        cabfilaabajo = cabfila.createCell(7, "Asistencia");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(8);
        cabfilaabajo = cabfila.createCell(6, "Coloquio");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(8);
        cabfilaabajo = cabfila.createCell(8, "Trabajo Institucional");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(8);
        cabfilaabajo = cabfila.createCell(13, "Promedio Final");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(8);
        cabfilaabajo = cabfila.createCell(10, "Condicion");//8
        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
        cabfilaabajo.setFontSize(8);
//        cabfilaabajo = cabfila.createCell(10.1f, "Firma del Alumno");//8
//        cabfilaabajo.setAlign(HorizontalAlignment.CENTER);
//        cabfilaabajo.setValign(VerticalAlignment.MIDDLE);
//        cabfilaabajo.setFontSize(8);
        fila.draw();
        table.draw();
        calificaciones_bolillas.draw();
        cuadro.endText();
        cuadro.close();
    }

//    //    private static String fechaEnletra(Date fechaDate) {
//    private static String fechaEnletra(LocalDate fechaDate) {
//        String f = "";
//        SimpleDateFormat formateador = new SimpleDateFormat("dd 'del mes de' MMMM 'del año' yyyy", new Locale("ES"));
//        String fecha = formateador.format(fechaDate);
//        f = fecha;
//        return f;
//    }


@Override
public PDDocument generaCalificador(String legajoId) {

    Boolean LF=false;
        Persona persona = this.alumnoService.obtenerAlumnoPorLegajoId(legajoId);
        Carrera carrera = this.carreraService.obtenerCarreraPorLegajoId(legajoId);
        Legajo legajo=this.legajoService.findLegajoById(legajoId);
        double nuevoProm = 0;
        int contProm = 0;
        List<NotaMateriaDTO> listaMaterias = new ArrayList<NotaMateriaDTO>();
        listaMaterias = this.notaService.obtenerTodasNotasPorLegajoAnalitico(legajoId);
        PDImageXObject Iesc1, Iesc2, casilla0, casilla1;
        PDDocument Documento = new PDDocument();
        try {
            String carrera_id = carrera.getCarreraId();
            int nMaterias = 0;//cantidad de matirias
            int materiasPrimero = materiaCarreraRepository.contarMateriasPorNivel(carrera_id, "1ro");
            int materiasSegundo = materiaCarreraRepository.contarMateriasPorNivel(carrera_id, "2do");
            int materiasTercero = materiaCarreraRepository.contarMateriasPorNivel(carrera_id, "3ro");
            int n = -10;//distancia entre lineas
            int letra = 11;//Tamaño de letras
            String resolucion = carrera.getCarreraResolucion();
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
            //Creando documento nuevo
            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);
            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);
            float pageHeight = PDRectangle.A4.getHeight();
            float pageWidth = PDRectangle.A4.getWidth();
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "_____________________________________________________________________________________________________________________________"
            };
            n = -10;
            contenido.beginText();
            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);
            System.out.println("Aca es el 1");
            for (String line : lines) {
                // Calcula el ancho de cada línea
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;

                // Calcula la posición x para centrar el texto
                float xStart = (pageWidth - textWidth) / 2;

                // Mueve la posición x
                contenido.newLineAtOffset(xStart, 0);

                // Escribe la línea
                contenido.showText(line);

                // Mueve a la siguiente línea
                contenido.newLineAtOffset(-xStart, n);
            }
            System.out.println("Aca es el 2");
            contenido.endText();
            contenido.close();
            //imagen del encavezado izquierda
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();
            System.out.println("Aca es el 3");
            //===================================================================
//Justificar texto
            int margin = 60;
            PDPageContentStream cuadro = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDRectangle mediabox = Pagina.getMediaBox();
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();

            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (2 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 650;//yStartNewPage;
            float bottomMargin = 70;
            float auxmargin = 35;
// y position is your coordinate of top left corner of the table
            float yPosition = 300;
            ////================================
            int tan = 10;
            BaseTable tablel1 = new BaseTable(yStart + 110, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
            Row<PDPage> l1 = tablel1.createRow(10);
            Cell<PDPage> cellL1 = l1.createCell(60, "Alumno/a :" + persona.getPersonaApellido() + ", " + persona.getPersonaNombre());
            cellL1.setAlign(HorizontalAlignment.LEFT);
            cellL1.setValign(VerticalAlignment.MIDDLE);
            cellL1.setFontSize(tan);
            //cellL1.setBorderStyle(null);
            cellL1 = l1.createCell(20, "DNI: " + persona.getPersonaDni());
            cellL1.setAlign(HorizontalAlignment.CENTER);
            cellL1.setValign(VerticalAlignment.MIDDLE);
            cellL1.setFontSize(tan);
            // cellL1.setBorderStyle(null);

//            cellL1 = l1.createCell(10, "L.E:");
//            cellL1.setAlign(HorizontalAlignment.LEFT);
//            cellL1.setValign(VerticalAlignment.MIDDLE);
//            cellL1.setFontSize(tan);
            //  cellL1.setBorderStyle(null);

            cellL1 = l1.createCell(25, "L.E N°: " + legajoId);
            cellL1.setAlign(HorizontalAlignment.LEFT);
            cellL1.setValign(VerticalAlignment.MIDDLE);
            cellL1.setFontSize(tan);
            //  cellL1.setBorderStyle(null);

            cellL1 = l1.createCell(13, "F°: ");
            cellL1.setAlign(HorizontalAlignment.LEFT);
            cellL1.setValign(VerticalAlignment.MIDDLE);
            cellL1.setFontSize(tan);
            //    cellL1.setBorderStyle(null);
            tablel1.draw();

//            System.out.println("Aca es el 4");
//            BaseTable tablel2 = new BaseTable(yStart + 90, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
//            cuadro.beginText();
//            cuadro.newLineAtOffset(0, 800);//X=40
//            Row<PDPage> l2 = tablel2.createRow(10);
//            Cell<PDPage> cellL2 = l2.createCell(55, "Nacido en la localidad de :" + persona.getPersonaLocalidadNacimiento());
//            cellL2.setAlign(HorizontalAlignment.LEFT);
//            cellL2.setValign(VerticalAlignment.MIDDLE);
//            cellL2.setFontSize(tan);
//            // cellL2.setBorderStyle(null);
//
//            cellL2 = l2.createCell(55, "Departamento: " + persona.getPersonaDepartamentoNacimiento());
//            cellL2.setAlign(HorizontalAlignment.LEFT);
//            cellL2.setValign(VerticalAlignment.MIDDLE);
//            cellL2.setFontSize(tan);
//            //  cellL2.setBorderStyle(null);
//            cuadro.endText();
//            tablel2.draw();


//            System.out.println("Aca es el 4.1");
//            BaseTable tablel3 = new BaseTable(yStart + 70, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
//            Row<PDPage> l3 = tablel3.createRow(10);
//            Cell<PDPage> cellL3 = l3.createCell(55, "Provincia : " + persona.getPersonaPaisNacimiento());
//            System.out.println("l3.createCell");
//            cellL3.setAlign(HorizontalAlignment.LEFT);
//            cellL3.setValign(VerticalAlignment.MIDDLE);
//            cellL3.setFontSize(tan);
//
//            // cellL3.setBorderStyle(null);
//            System.out.println("IMPRIME?");
////            System.out.println(fechaEnletra(alumno.getAlumnoFechaNacimiento()));
//            System.out.println("cellL3.setFontSize(tan);");
////            cellL3 = l3.createCell(55, "El dia: " + fechaEnletra(alumno.getAlumnoFechaNacimiento()));
//            cellL3 = l3.createCell(55, "El dia: "+ persona.getPersonaFechaNacimiento().toString());
//            cellL3.setAlign(HorizontalAlignment.LEFT);
//            cellL3.setValign(VerticalAlignment.MIDDLE);
//            cellL3.setFontSize(tan);
//            //cellL3.setBorderStyle(null);
//            System.out.println("cuadro.endText();");
//            tablel3.draw();
//            System.out.println("Aca es el 4.1.1");
//            BaseTable tablel4 = new BaseTable(yStart + 50, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
//            Row<PDPage> l4 = tablel4.createRow(20);
//            Cell<PDPage> cellL4 = l4.createCell(100, "Titulo Nivel Medio: " + persona.getPersonaTitulo());
//            cellL4.setAlign(HorizontalAlignment.LEFT);
//            cellL4.setValign(VerticalAlignment.MIDDLE);
//            cellL4.setFontSize(tan);
//            cellL4.setRightBorderStyle(null);
//            //     cellL4.setBorderStyle(null);
//            cellL4 = l4.createCell(10, "");
//            cellL4.setAlign(HorizontalAlignment.LEFT);
//            cellL4.setValign(VerticalAlignment.MIDDLE);
//            cellL4.setFontSize(tan);
//            cellL4.setLeftBorderStyle(null);
//            // cellL4.setBorderStyle(null);
//            tablel4.draw();
//            System.out.println("Aca es el 4.1.2");
//            BaseTable tablel5 = new BaseTable(yStart + 30, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
//            Row<PDPage> l5 = tablel5.createRow(20);
//            Cell<PDPage> cellL5 = l5.createCell(100, "Expedido Por : " + persona.getPersonaEscuela());
//            cellL5.setAlign(HorizontalAlignment.LEFT);
//            cellL5.setValign(VerticalAlignment.MIDDLE);
//            cellL5.setFontSize(tan);
//            cellL5.setRightBorderStyle(null);
//            cellL5 = l5.createCell(10, "");
//            cellL5.setAlign(HorizontalAlignment.LEFT);
//            cellL5.setValign(VerticalAlignment.MIDDLE);
//            cellL5.setFontSize(tan);
//            cellL5.setLeftBorderStyle(null);
//            tablel5.draw();
//

            yStart=yStart+90;

            System.out.println("Aca es el 4.2");
            BaseTable tablel6 = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);

            Row<PDPage> l6 = tablel6.createRow(20);
            Cell<PDPage> cellL6 = l6.createCell(98,"Carrera: " + carrera.getCarreraNombre());
            cellL6.setAlign(HorizontalAlignment.LEFT);
            cellL6.setValign(VerticalAlignment.MIDDLE);
            cellL6.setFontSize(tan);
            cellL6 = l6.createCell(20, "Cohorte: " + carrera.getCarreraYear());
            cellL6.setAlign(HorizontalAlignment.LEFT);
            cellL6.setValign(VerticalAlignment.MIDDLE);
            cellL6.setFontSize(tan);
            tablel6.draw();
            System.out.println("Aca es el 4.3");


            BaseTable tablel7 = new BaseTable(yStart - 20, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 800);//X=40
            Row<PDPage> l7 = tablel7.createRow(20);
            Cell<PDPage> cellL7 = l7.createCell(70, "Estructura Curricular Aprobado por : " + resolucion);
            cellL7.setAlign(HorizontalAlignment.LEFT);
            cellL7.setValign(VerticalAlignment.MIDDLE);
            cellL7.setFontSize(tan);
            //  cellL7.setBorderStyle(null);
            cellL7 = l7.createCell(48, "Validez Nacional: ");
            cellL7.setAlign(HorizontalAlignment.LEFT);
            cellL7.setValign(VerticalAlignment.MIDDLE);
            cellL7.setFontSize(tan);
            //  cellL7.setBorderStyle(null);
            cuadro.endText();
            tablel7.draw();


            BaseTable tablel8 = new BaseTable(yStart - 40, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 800);//X=40
            Row<PDPage> l8 = tablel8.createRow(20);
            Cell<PDPage> cellL8 = l8.createCell(15, "DNI: "+legajo.getLegajoFotocopiaDni());
            cellL8.setAlign(HorizontalAlignment.LEFT);
            cellL8.setValign(VerticalAlignment.MIDDLE);
            cellL8.setFontSize(tan);
            //  cellL7.setBorderStyle(null);
            cellL8 = l8.createCell(20, "Nacimiento: "+legajo.getLegajoCertificadoNacimiento());
            cellL8.setAlign(HorizontalAlignment.LEFT);
            cellL8.setValign(VerticalAlignment.MIDDLE);
            cellL8.setFontSize(tan);
            //  cellL7.setBorderStyle(null);
            cellL8 = l8.createCell(38, "Titulo: "+legajo.getLegajoFotocopiaTitulo());
            cellL8.setAlign(HorizontalAlignment.LEFT);
            cellL8.setValign(VerticalAlignment.MIDDLE);
            cellL8.setFontSize(tan);

            //  cellL7.setBorderStyle(null);
            cellL8 = l8.createCell(15, "Salud: "+legajo.getLegajoCarnetSanitario());
            cellL8.setAlign(HorizontalAlignment.LEFT);
            cellL8.setValign(VerticalAlignment.MIDDLE);
            cellL8.setFontSize(tan);


            //  cellL7.setBorderStyle(null);
            cellL8 = l8.createCell(20, "Prontuarial: "+legajo.getLegajoPlanillaProntuarial());
            cellL8.setAlign(HorizontalAlignment.LEFT);
            cellL8.setValign(VerticalAlignment.MIDDLE);
            cellL8.setFontSize(tan);

            //  cellL7.setBorderStyle(null);
            cellL8 = l8.createCell(10, "Foto: "+legajo.getLegajoFoto());
            cellL8.setAlign(HorizontalAlignment.LEFT);
            cellL8.setValign(VerticalAlignment.MIDDLE);
            cellL8.setFontSize(tan);




            //================================
//            if (legajo.getLegajoFotocopiaDni().equals("Si"))
//            if (legajo.getLegajoPlanillaProntuarial().equals("Si")) {
//            if (legajo.getLegajoCertificadoNacimiento().equals("Si")) {
//            if (legajo.getLegajoCarnetSanitario().equals("Si")) {
//             legajo.getLegajoFotocopiaTitulo();
//
            //=================================


            //  cellL7.setBorderStyle(null);
            cuadro.endText();
            tablel8.draw();








            yStart=yStart-10-l8.getHeight();
            BaseTable table = new BaseTable(yStart - 35, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
            int espaciado = 0;
            System.out.println("Aca es el 5");
            cuadro.beginText();
            int left=-10;
            cuadro.newLineAtOffset(0, 600);//X=40
            Row<PDPage> headerRow = table.createRow(48);
            int a = 5;
            int fontNormal=10;
            Cell<PDPage> cell = headerRow.createCell(5, "CURSO");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setTextRotated(true);
            System.out.println(cell.getHeight());
            float h = cell.getInnerWidth();
            cell = headerRow.createCell(5, "ORDEN");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setTextRotated(true);
            float b = cell.getInnerWidth();
            cuadro.setCharacterSpacing(espaciado);
            cell = headerRow.createCell(55, "ESPACIO CURRICULAR");//45
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float c = cell.getExtraWidth();
            cell = headerRow.createCell(48, "CALIFICACIONES");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.TOP);

            float r = cell.getInnerWidth();
            cell = headerRow.createCell(5, "Institucion");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            float d = cell.getExtraWidth();
            cell.setFont(PDType1Font.HELVETICA);
            cell.setTextRotated(true);
            cuadro.endText();
            table.draw();
            System.out.println("Aca es el6");

            BaseTable tableitm2 = new BaseTable(yStart - 52, yStartNewPage, bottomMargin, tableWidth, 260 + auxmargin + 33, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 600);//X=40
            int lfmn = 5;
            Row<PDPage> headerRow2 = tableitm2.createRow(31);
            Cell<PDPage> cellh2 = headerRow2.createCell(6, "Num");
            cellh2.setAlign(HorizontalAlignment.CENTER);
            cellh2.setValign(VerticalAlignment.MIDDLE);
            cellh2.setFontSize(lfmn);
            cellh2 = headerRow2.createCell(8, "Letra");
            cellh2.setAlign(HorizontalAlignment.CENTER);
            cellh2.setValign(VerticalAlignment.MIDDLE);
            cellh2.setFontSize(lfmn);
            cellh2 = headerRow2.createCell(12, "Libro Actas");
            cellh2.setAlign(HorizontalAlignment.CENTER);
            cellh2.setValign(VerticalAlignment.TOP);
            cellh2.setFontSize(lfmn);
            cellh2 = headerRow2.createCell(6, "F°");
            cellh2.setAlign(HorizontalAlignment.CENTER);
            cellh2.setValign(VerticalAlignment.MIDDLE);
            cellh2.setFontSize(lfmn);
//            cellh2 = headerRow2.createCell(9.21f, "Fecha");
            cellh2 = headerRow2.createCell(12, "Fecha");
            cellh2.setAlign(HorizontalAlignment.CENTER);
            cellh2.setValign(VerticalAlignment.MIDDLE);
            cellh2.setFontSize(lfmn);
            cellh2 = headerRow2.createCell(4.2f, "Eq");
            cellh2.setAlign(HorizontalAlignment.CENTER);
            cellh2.setValign(VerticalAlignment.MIDDLE);
            cellh2.setFontSize(lfmn);
            cuadro.endText();
            tableitm2.draw();
            BaseTable tableitm3 = new BaseTable(yStart - 66.2f, yStartNewPage, bottomMargin, tableWidth, 325 + auxmargin + 33.5f, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 600);//X=40
            Row<PDPage> headerRow3 = tableitm3.createRow(1);
            Cell<PDPage> cellh3 = headerRow3.createCell(6, "Prom");
            cellh3.setAlign(HorizontalAlignment.CENTER);
            cellh3.setValign(VerticalAlignment.MIDDLE);
            cellh3.setFontSize(lfmn);
            cellh3 = headerRow3.createCell(6.2f, "Ex. Fin");
            cellh3.setAlign(HorizontalAlignment.CENTER);
            cellh3.setValign(VerticalAlignment.MIDDLE);
            cellh3.setFontSize(lfmn);
            cuadro.endText();
            tableitm3.draw();
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 600);//X=40
            int año = 3;
            BaseTable Cursoaño = new BaseTable(yStart - 35 - headerRow.getHeight() + 1, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
            BaseTable Materiasaño = new BaseTable(yStart - 35 - headerRow.getHeight() + 1, yStartNewPage + 1, bottomMargin, tableWidth, 22.9f + auxmargin - 15, Documento, Pagina, true, drawContent);
            BaseTable institucion = new BaseTable(yStart - 35 - headerRow.getHeight() + 1, yStartNewPage + 1, bottomMargin, tableWidth, 492.5f + auxmargin + 29, Documento, Pagina, true, drawContent);
            float H = 0;
            for (int i = 0; i < año; i++) {// primer for, este genera los años, es decir las materias que llevan cada año
                List<NotaMateriaDTO> listaMateriasyear = materiasyear(listaMaterias, i);
                int materias = listaMateriasyear.size();
                // Recorre la lista y muestra los elementos por pantalla
                String x = "1ro";
                String xx = "I.E.S.I";
                Row<PDPage> raw = Cursoaño.createRow(materias * 19);
                Row<PDPage> rtw = institucion.createRow(materias * 19);
                if (i == 1) {
                    x = "2do";
                } else if (i == 2) {
                    x = "3ro";
                }
                float altura = 0;
                cell = raw.createCell(5, x);//año
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFont(PDType1Font.HELVETICA);
                cell.setTextRotated(true);
                cell = rtw.createCell(5.15f, xx);//año
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFont(PDType1Font.HELVETICA);
                cell.setTextRotated(true);
                for (int j = 0; j < listaMateriasyear.size(); j++) {
                    int nk = 8;
                    NotaMateriaDTO mat = new NotaMateriaDTO();
                    mat = listaMateriasyear.get(j);
                    Row<PDPage> rew = Materiasaño.createRow(5);//19
                    // Celda para la columna "Orden"
                    Cell<PDPage> cellOrden = rew.createCell(5.2f, mat.getMateriaOrden().toString());
                    cellOrden.setAlign(HorizontalAlignment.CENTER);
                    cellOrden.setValign(VerticalAlignment.MIDDLE);
                    cellOrden.setFont(PDType1Font.HELVETICA);
                    cellOrden.setFontSize(nk);
                    // Celda para la columna "Nombre Materia"
                    Cell<PDPage> cellNombreMateria = rew.createCell(55, mat.getMateriaNombre());
                    cellNombreMateria.setFontSize(nk);
                    cellNombreMateria.setValign(VerticalAlignment.MIDDLE);
                    cellNombreMateria.setFont(PDType1Font.HELVETICA);
                    String vf = "";
                    String vv = "";
                    String fecha = "";
                    String not = "";
                    String fol = "";
                    if (mat.getNotaEstado().equals("Aprobado")) {
                        fecha = mat.getNotaFecha().toString();
                        not = String.valueOf(mat.getNotaCalificacionNumero());



                        if(LF) {
                            fol = mat.getNotaFolio();
                            if (mat.getNotaCondicion().equals("Cursada")) {
                                vf = mat.getNotaLibro();
                            } else {
                                vv = mat.getNotaLibro();
                            }

                        }


                    }
                    // Celda para la columna "Nota numero"
                    Cell<PDPage> cellNotaNumer = rew.createCell(5.8f, not);
                    cellNotaNumer.setFontSize(nk);
                    cellNotaNumer.setAlign(HorizontalAlignment.CENTER);
                    cellNotaNumer.setValign(VerticalAlignment.MIDDLE);
                    cellNotaNumer.setFont(PDType1Font.HELVETICA);
                    // Celda para la columna "Nota Letra"
                    Cell<PDPage> cellNotaLetra = rew.createCell(7.99f, "");
                    cellNotaLetra.setFontSize(nk);
                    cellNotaLetra.setAlign(HorizontalAlignment.CENTER);
                    cellNotaLetra.setValign(VerticalAlignment.MIDDLE);
                    cellNotaLetra.setFont(PDType1Font.HELVETICA);
                    // Celda para la columna "Nota libro Promocional"
                    Cell<PDPage> cellLprom = rew.createCell(5.75f, vf);
                    cellLprom.setFontSize(nk);
                    cellLprom.setAlign(HorizontalAlignment.CENTER);
                    cellLprom.setValign(VerticalAlignment.MIDDLE);
                    cellLprom.setFont(PDType1Font.HELVETICA);
                    // Celda para la columna "Nota Libro Final"
                    Cell<PDPage> cellLFinal = rew.createCell(6.26f, vv);
                    cellLFinal.setFontSize(nk);
                    cellLFinal.setAlign(HorizontalAlignment.CENTER);
                    cellLFinal.setValign(VerticalAlignment.MIDDLE);
                    cellLFinal.setFont(PDType1Font.HELVETICA);
                    // Celda para la columna "Folio"
                    Cell<PDPage> cellFolio = rew.createCell(6, fol);
                    cellFolio.setFontSize(nk);
                    cellFolio.setAlign(HorizontalAlignment.CENTER);
                    cellFolio.setValign(VerticalAlignment.MIDDLE);
                    cellFolio.setFont(PDType1Font.HELVETICA);
                    // Celda para la columna "Nota Fecha"

//                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                    String fechaFormateada = sdf.format(fecha);
//                    Cell<PDPage> cellFecha = rew.createCell(9.2f, fechaFormateada);
//                    Cell<PDPage> cellFecha = rew.createCell(9.2f, fecha);

                    // Formato que trae la fecha

                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String fechaFormateada = "";
                    if (fecha != null && !fecha.isBlank()) {
                        LocalDate date = LocalDate.parse(fecha, inputFormatter);
                        fechaFormateada = date.format(outputFormatter);
                    }


//                    LocalDate date = LocalDate.parse(fecha, inputFormatter);
//                    String fechaFormateada = date.format(outputFormatter);

                    Cell<PDPage> cellFecha = rew.createCell(12, fechaFormateada);

                    cellFecha.setFontSize(nk);
                    cellFecha.setAlign(HorizontalAlignment.CENTER);
                    cellFecha.setValign(VerticalAlignment.MIDDLE);
                    cellFecha.setFont(PDType1Font.HELVETICA);
                    // Celda para la columna "Nota equivalencia"
                    Cell<PDPage> cellEquivalencia = rew.createCell(4.2f, "");
                    cellEquivalencia.setFontSize(nk);
                    cellEquivalencia.setAlign(HorizontalAlignment.CENTER);
                    cellEquivalencia.setValign(VerticalAlignment.MIDDLE);
                    cellEquivalencia.setFont(PDType1Font.HELVETICA);
                    float filaHeight = rew.getHeight();
                    altura = altura + filaHeight;
                }
                H = H + altura;
                raw.setHeight(altura);
                rtw.setHeight(altura);
            }
            System.out.println("Aca es el 7");
            cuadro.endText();
            Cursoaño.draw();
            institucion.draw();
            Materiasaño.draw();
            System.out.println("Aca es el 8");
            cuadro.close();
            //Documento.close();
        } catch (Exception e) {
        }
        return Documento;
    }

@Override
public PDDocument generaTroquelTramite(String legajoId, Integer atencionId) {
        Persona persona = this.alumnoService.obtenerAlumnoPorLegajoId(legajoId);
        Carrera carrera = this.carreraService.obtenerCarreraPorLegajoId(legajoId);
        Atencion atencion= this.atencionService.findById(atencionId).get();
        Legajo legajo=this.legajoService.findLegajoById(legajoId);
        double nuevoProm = 0;
        int contProm = 0;
        PDImageXObject Iesc1, Iesc2, casilla0, casilla1;
        PDDocument Documento = new PDDocument();
        try {
            String carrera_id = carrera.getCarreraId();
            int n = -10;//distancia entre lineas
            int letra = 11;//Tamaño de letras
            String resolucion = carrera.getCarreraResolucion();
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
            //Creando documento nuevo
            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");

            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);

            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);

// Altura de la página
            float pageHeight = PDRectangle.A4.getHeight();

// Ancho de la página
            float pageWidth = PDRectangle.A4.getWidth();

// Texto para cada línea
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };

// Distancia entre líneas
            n = -10;

// Comienza a escribir el texto
            contenido.beginText();

            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);

            for (String line : lines) {
                // Calcula el ancho de cada línea
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;

                // Calcula la posición x para centrar el texto
                float xStart = (pageWidth - textWidth) / 2;

                // Mueve la posición x
                contenido.newLineAtOffset(xStart, 0);

                // Escribe la línea
                contenido.showText(line);

                // Mueve a la siguiente línea
                contenido.newLineAtOffset(-xStart, n);
            }

            contenido.endText();
            contenido.close();


            String aporteSumas = "";

            List<Aporte> aportes = this.aporteService.obtenerAportesDelAnioActualPorLegajo(legajoId)
                    .stream()
                    .filter(distinctByKeys(a -> Arrays.asList(
                            a.getAporteFecha(),
                            a.getAporteMonto()
                    )))
                    .collect(Collectors.toList());

            if (aportes.isEmpty()) {
                aporteSumas = "Sin pagos";
            } else {
                // Convertimos cada Double a BigDecimal
                BigDecimal sumaTotal = aportes.stream()
                        .map(a -> BigDecimal.valueOf(a.getAporteMonto()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Formateo como moneda argentina
                NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
                aporteSumas =formatoMoneda.format(sumaTotal);

                for (Aporte aporte : aportes) {
                    String detalle = String.format(
                            "Fecha: %s - Recibo: %s/%s - Monto: %s",
                            aporte.getAporteFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            aporte.getAporteTalonarioRecibo(),
                            aporte.getAporteNroRecibo(),
                            formatoMoneda.format(BigDecimal.valueOf(aporte.getAporteMonto()))
                    );

                    // Si querés concatenar detalles al string final:
                    // aporteSumas += "\n" + detalle;
                }
            }

            //imagen del encavezado izquierda
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();
            //===================================================================
//Justificar texto
            int margin = 60;
            float longitud = 500;//longitud permitida para justificar
            PDPageContentStream cuadro = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDRectangle mediabox = Pagina.getMediaBox();
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();

            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (2 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 650;//yStartNewPage;
            float bottomMargin = 70;
            float auxmargin = 55;
// y position is your coordinate of top left corner of the table
            float yPosition = 300;
            ////================================
            int tan = 10;


            BaseTable TablaF0 = new BaseTable(yStart + 110, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.setFont(PDType1Font.HELVETICA, 20);
            cuadro.newLineAtOffset(0, 800);//X=40
            Row<PDPage> l0 = TablaF0.createRow(10);
            Cell<PDPage> cell0 = l0.createCell(25, "Tramite: "+atencion.getId());
            cell0.setAlign(HorizontalAlignment.LEFT);
            cell0.setValign(VerticalAlignment.MIDDLE);
            cell0.setFontSize(tan);
            //cellL1.setBorderStyle(null);
            cell0 = l0.createCell(40, "Tipo: "+atencion.getAtencionTipo());
            cell0.setAlign(HorizontalAlignment.LEFT);
            cell0.setValign(VerticalAlignment.MIDDLE);
            cell0.setFontSize(tan);
            // cellL1.setBorderStyle(null);

            cell0 = l0.createCell(25, "Fecha: "+ atencion.getAtencionFecha());
            cell0.setAlign(HorizontalAlignment.LEFT);
            cell0.setValign(VerticalAlignment.MIDDLE);
            cell0.setFontSize(tan);
            //  cellL1.setBorderStyle(null);

            cell0 = l0.createCell(20, "Resuelto: "+atencion.getAtencionResuelto());
            cell0.setAlign(HorizontalAlignment.LEFT);
            cell0.setValign(VerticalAlignment.MIDDLE);
            cell0.setFontSize(tan);
            //  cellL1.setBorderStyle(null);

            cuadro.endText();
            TablaF0.draw();

            BaseTable tablel1 = new BaseTable(yStart + 90, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.setFont(PDType1Font.HELVETICA, 20);
            cuadro.newLineAtOffset(0, 800);//X=40
            Row<PDPage> l1 = tablel1.createRow(10);
            Cell<PDPage> cellL1 = l1.createCell(60, "Alumno/a :" + persona.getPersonaApellido() + ", " + persona.getPersonaNombre());
            cellL1.setAlign(HorizontalAlignment.LEFT);
            cellL1.setValign(VerticalAlignment.MIDDLE);
            cellL1.setFontSize(tan);
            //cellL1.setBorderStyle(null);
            cellL1 = l1.createCell(20, "DNI: " + persona.getPersonaDni());
            cellL1.setAlign(HorizontalAlignment.CENTER);
            cellL1.setValign(VerticalAlignment.MIDDLE);
            cellL1.setFontSize(tan);
            // cellL1.setBorderStyle(null);

            cellL1 = l1.createCell(30, "L.E N°: " + legajoId);
            cellL1.setAlign(HorizontalAlignment.LEFT);
            cellL1.setValign(VerticalAlignment.MIDDLE);
            cellL1.setFontSize(tan);
            //  cellL1.setBorderStyle(null);

            cuadro.endText();
            tablel1.draw();

            BaseTable tablel6 = new BaseTable(yStart +70 , yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 800);//X=40
            Row<PDPage> l6 = tablel6.createRow(20);
            Cell<PDPage> cellL6 = l6.createCell(80, "Carrera: " + carrera.getCarreraNombre());
            cellL6.setAlign(HorizontalAlignment.LEFT);
            cellL6.setValign(VerticalAlignment.MIDDLE);
            cellL6.setFontSize(tan);
            cellL6 = l6.createCell(30, "Cohorte: " + carrera.getCarreraYear());
            cellL6.setAlign(HorizontalAlignment.LEFT);
            cellL6.setValign(VerticalAlignment.MIDDLE);
            cellL6.setFontSize(tan);
            cuadro.endText();
            tablel6.draw();

            BaseTable tablel2 = new BaseTable(yStart + 50, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 800);//X=40
            Row<PDPage> l2 = tablel2.createRow(10);
            Cell<PDPage> cellL2 = l2.createCell(10, "Legajo");
            cellL2.setAlign(HorizontalAlignment.LEFT);
            cellL2.setValign(VerticalAlignment.MIDDLE);
            cellL2.setFontSize(tan);
            // cellL2.setBorderStyle(null);

            cellL2 = l2.createCell(10, "DNI:"+legajo.getLegajoFotocopiaDni());
            cellL2.setAlign(HorizontalAlignment.LEFT);
            cellL2.setValign(VerticalAlignment.MIDDLE);
            cellL2.setFontSize(tan);
            //  cellL2.setBorderStyle(null);

            cellL2 = l2.createCell(10, "Nac:"+legajo.getLegajoCertificadoNacimiento());
            cellL2.setAlign(HorizontalAlignment.LEFT);
            cellL2.setValign(VerticalAlignment.MIDDLE);
            cellL2.setFontSize(tan);
            //  cellL2.setBorderStyle(null);

            cellL2 = l2.createCell(25, "Tit:"+legajo.getLegajoFotocopiaTitulo());
            cellL2.setAlign(HorizontalAlignment.LEFT);
            cellL2.setValign(VerticalAlignment.MIDDLE);
            cellL2.setFontSize(tan);
            //  cellL2.setBorderStyle(null);

            cellL2 = l2.createCell(10, "Sal:"+legajo.getLegajoCarnetSanitario());
            cellL2.setAlign(HorizontalAlignment.LEFT);
            cellL2.setValign(VerticalAlignment.MIDDLE);
            cellL2.setFontSize(tan);
            //  cellL2.setBorderStyle(null);

            cellL2 = l2.createCell(15, "Fotos:"+legajo.getLegajoFoto());
            cellL2.setAlign(HorizontalAlignment.LEFT);
            cellL2.setValign(VerticalAlignment.MIDDLE);
            cellL2.setFontSize(tan);
            //  cellL2.setBorderStyle(null);

            cellL2 = l2.createCell(30, "Inscripcion:"+aporteSumas);
            cellL2.setAlign(HorizontalAlignment.LEFT);
            cellL2.setValign(VerticalAlignment.MIDDLE);
            cellL2.setFontSize(tan);
            //  cellL2.setBorderStyle(null);


            cuadro.endText();
            tablel2.draw();

            BaseTable tablel3 = new BaseTable(yStart + 30, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 800);//X=40
            Row<PDPage> l3 = tablel3.createRow(10);



            List<CertificadoEstudiante> listaCer = atencion.getCertificados();
            String tiposCertificados;
            if (listaCer == null || listaCer.isEmpty()) {
                tiposCertificados = "Sin solicitados.";
            } else {
                tiposCertificados = listaCer.stream()
                        .map(CertificadoEstudiante::getTipo) // adaptalo si tiene otro nombre el método
                        .distinct()
                        .collect(Collectors.joining(" | "));
            }




            Cell<PDPage> cellL3 = l3.createCell(80, "Constancias:"+tiposCertificados);
            cellL3.setAlign(HorizontalAlignment.LEFT);
            cellL3.setValign(VerticalAlignment.MIDDLE);
            cellL3.setFontSize(tan);
            // cellL3.setBorderStyle(null);

            Pago pago=pagoService.findByAtencionId(atencion.getId()).get();
            cellL3 = l3.createCell(30, "Pago: "+ pago.getMontoTotal()+"-"+pago.getEstado());

            cellL3.setAlign(HorizontalAlignment.LEFT);
            cellL3.setValign(VerticalAlignment.MIDDLE);
            cellL3.setFontSize(tan);
            //cellL3.setBorderStyle(null);

            cuadro.endText();
            tablel3.draw();

            BaseTable tablel4 = new BaseTable(yStart + 10, yStartNewPage, bottomMargin, tableWidth, auxmargin - 15, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 800);//X=40
            Row<PDPage> l4 = tablel4.createRow(20);

            Cell<PDPage> cellL4 = l4.createCell(100, "Observaciones"+atencion.getAtencionObservaciones());
            cellL4.setAlign(HorizontalAlignment.LEFT);
            cellL4.setValign(VerticalAlignment.MIDDLE);
            cellL4.setFontSize(tan);

            cellL4.setRightBorderStyle(null);
            cellL4 = l4.createCell(10, "");
            cellL4.setAlign(HorizontalAlignment.LEFT);
            cellL4.setValign(VerticalAlignment.MIDDLE);
            cellL4.setFontSize(tan);
            cellL4.setLeftBorderStyle(null);

            // cellL4.setBorderStyle(null);
            cuadro.endText();
            tablel4.draw();

            cuadro.close();
            //Documento.close();
        } catch (Exception e) {
                e.printStackTrace();
                return null;

        }
        return Documento;
    }




    @Override
    public PDDocument generaTroquelNotaIngresante(Integer atencionId) {
        Atencion atencion = this.atencionService.findById(atencionId)
                .orElseThrow(() ->
                        new RuntimeException("No se encontró la atención con id: " + atencionId)
                );
        double nuevoProm = 0;
        int contProm = 0;
        PDImageXObject Iesc1, Iesc2, casilla0, casilla1;
        PDDocument Documento = new PDDocument();
        try {
            int n = -10;//distancia entre lineas
            int letra = 11;//Tamaño de letras
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
            //Creando documento nuevo
            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(normal, fontSize);
            float pageHeight = PDRectangle.A4.getHeight();
            float pageWidth = PDRectangle.A4.getWidth();
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };
            n = -10;
            contenido.beginText();
            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);
            for (String line : lines) {
                float textWidth = normal.getStringWidth(line) / 1000 * fontSize;
                float xStart = (pageWidth - textWidth) / 2;
                contenido.newLineAtOffset(xStart, 0);
                contenido.showText(line);
                contenido.newLineAtOffset(-xStart, n);
            }
            contenido.endText();
//            contenido.close();
            String aporteSumas = "";
            //imagen del encavezado izquierda
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();
            //===================================================================
//Justificar texto
            int margin = 30;
            float longitud = 500;//longitud permitida para justificar
            PDPageContentStream cuadro = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDRectangle mediabox = Pagina.getMediaBox();
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();

            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (2 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart =Pagina.getMediaBox().getHeight()-90;
            float bottomMargin = 70;
            float auxmargin = 30;
            ////================================
            int tan = 10;

            contenido.beginText();
            contenido.newLineAtOffset(margin, yStart);
            contenido.setFont(negrita, 15);
            contenido.showText(atencion.getAtencionTipo()+" N°: "+atencion.getNumeroTipo());
            contenido.endText();
            contenido.close();


            yStart -=10;
            Float H=0f;

            BaseTable TablaF0 = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.setFont(PDType1Font.HELVETICA, 20);
            cuadro.newLineAtOffset(0, 800);//X=40
            Row<PDPage> l0 = TablaF0.createRow(10);

            Cell<PDPage> cell0 = l0.createCell(15, "Tramite: "+atencion.getId());
            cell0.setAlign(HorizontalAlignment.LEFT);
            cell0.setValign(VerticalAlignment.MIDDLE);
            cell0.setFontSize(tan);
            cell0.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cell0.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));


            //cellL1.setBorderStyle(null);
            cell0 = l0.createCell(30, "Cod Seguimiento: "+atencion.getCodigoSeguimiento());
            cell0.setAlign(HorizontalAlignment.LEFT);
            cell0.setValign(VerticalAlignment.MIDDLE);
            cell0.setFontSize(tan);
            cell0.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cell0.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));

            cell0 = l0.createCell(30,  atencion.getAtencionFechaFormateada());
            cell0.setAlign(HorizontalAlignment.LEFT);
            cell0.setValign(VerticalAlignment.MIDDLE);
            cell0.setFontSize(tan);
            cell0.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cell0.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));

            cell0 = l0.createCell(25, "Prioridad: "+atencion.getAtencionPrioridad());
            cell0.setAlign(HorizontalAlignment.LEFT);
            cell0.setValign(VerticalAlignment.MIDDLE);
            cell0.setFontSize(tan);
            cell0.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cell0.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));

            cuadro.endText();
            TablaF0.draw();
            H=H+TablaF0.getHeaderAndDataHeight();

            BaseTable tablel1 = new BaseTable(yStart - H, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.setFont(PDType1Font.HELVETICA, 20);
            cuadro.newLineAtOffset(0, 800);//X=40
            Row<PDPage> l1 = tablel1.createRow(10);
            Cell<PDPage> cellL1 = l1.createCell(50, "Remitente:" + atencion.getAtencionApellidoNombre());
            cellL1.setAlign(HorizontalAlignment.LEFT);
            cellL1.setValign(VerticalAlignment.MIDDLE);
            cellL1.setFontSize(tan);
            cellL1.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL1.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));

            cellL1.setTopBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL1.setBottomBorderStyle(new LineStyle(Color.WHITE, 0f));


            //cellL1.setBorderStyle(null);
            cellL1 = l1.createCell(20, "Celular: " + atencion.getAtencionCelular());
            cellL1.setAlign(HorizontalAlignment.CENTER);
            cellL1.setValign(VerticalAlignment.MIDDLE);
            cellL1.setFontSize(tan);
            cellL1.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL1.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL1.setTopBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL1.setBottomBorderStyle(new LineStyle(Color.WHITE, 0f));

            cellL1 = l1.createCell(30, "Destino: " + atencion.getAtencionDestino());
            cellL1.setAlign(HorizontalAlignment.LEFT);
            cellL1.setValign(VerticalAlignment.MIDDLE);
            cellL1.setFontSize(tan);
            cellL1.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL1.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL1.setTopBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL1.setBottomBorderStyle(new LineStyle(Color.WHITE, 0f));
            cuadro.endText();
            tablel1.draw();
            H=H+tablel1.getHeaderAndDataHeight();

            BaseTable tablel6 = new BaseTable(yStart -H , yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 800);//X=40

            String asunto=atencion.getAtencionAsunto();

            if(atencion.getAtencionAsunto().equals("Certificado Estudiante")) {
                Carrera carrera = this.carreraService.obtenerCarreraPorLegajoId(atencion.getLegajoId());
                    String carr = carrera.getCarreraNombre();
                    String cohorte = carrera.getCarreraYear().toString();

                    asunto = asunto+ "| Carrera: "+carrera.getCarreraNombre();
                }





            Row<PDPage> l6 = tablel6.createRow(20);
            Cell<PDPage> cellL6 = l6.createCell(90, "Asunto: " + asunto);
            cellL6.setAlign(HorizontalAlignment.LEFT);
            cellL6.setValign(VerticalAlignment.MIDDLE);
            cellL6.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL6.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL6.setFontSize(tan);
            cellL6 = l6.createCell(
                    10,
                    "Folios: " + (atencion.getAtencionFolios() == null ? "" : atencion.getAtencionFolios())
            );

            cellL6.setAlign(HorizontalAlignment.LEFT);
            cellL6.setValign(VerticalAlignment.MIDDLE);
            cellL6.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL6.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL6.setFontSize(tan);
            cuadro.endText();
            tablel6.draw();
            H=H+tablel6.getHeaderAndDataHeight();
            Float altQr=H;

            BaseTable tablel2 = new BaseTable(yStart - H, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 800);//X=40
            Row<PDPage> l2 = tablel2.createRow(10);

            String problema = atencion.getAtencionProblema();
            if (problema != null) {
                problema = problema.replaceAll("\\r?\\n", " ").trim();
            }


            if(atencion.getAtencionAsunto().equals("Certificado Estudiante")) {
                List<CertificadoEstudiante> listaCer = atencion.getCertificados();
                String tiposCertificados;
                if (listaCer == null || listaCer.isEmpty()) {
                    tiposCertificados = "Sin solicitados.";
                } else {
                    tiposCertificados = listaCer.stream()
                            .map(CertificadoEstudiante::getTipo) // adaptalo si tiene otro nombre el método
                            .distinct()
            .collect(Collectors.joining(" | "));
                    problema = tiposCertificados;
                }
            }

            Cell<PDPage> cellL2 = l2.createCell(80, "Solicitud: " + problema);//100
            cellL2.setAlign(HorizontalAlignment.LEFT);
            cellL2.setValign(VerticalAlignment.MIDDLE);
            cellL2.setFontSize(tan);
            cellL2.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL2.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));

            cellL2.setTopBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL2.setBottomBorderStyle(new LineStyle(Color.WHITE, 0f));


            // cellL2.setBorderStyle(null);
//
//            cellL2 = l2.createCell(10, "DNI:"+"legajo.getLegajoFotocopiaDni()");
//            cellL2.setAlign(HorizontalAlignment.LEFT);
//            cellL2.setValign(VerticalAlignment.MIDDLE);
//            cellL2.setFontSize(tan);
//            cellL2.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
//            cellL2.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
//            //  cellL2.setBorderStyle(null);
//
//            cellL2 = l2.createCell(10, "Nac:"+"legajo.getLegajoCertificadoNacimiento()");
//            cellL2.setAlign(HorizontalAlignment.LEFT);
//            cellL2.setValign(VerticalAlignment.MIDDLE);
//            cellL2.setFontSize(tan);
//            cellL2.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
//            cellL2.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
//            //  cellL2.setBorderStyle(null);
//
//            cellL2 = l2.createCell(25, "Tit:"+"legajo.getLegajoFotocopiaTitulo()");
//            cellL2.setAlign(HorizontalAlignment.LEFT);
//            cellL2.setValign(VerticalAlignment.MIDDLE);
//            cellL2.setFontSize(tan);
//            cellL2.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
//            cellL2.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
//            //  cellL2.setBorderStyle(null);
//
//            cellL2 = l2.createCell(10, "Sal:"+"legajo.getLegajoCarnetSanitario()");
//            cellL2.setAlign(HorizontalAlignment.LEFT);
//            cellL2.setValign(VerticalAlignment.MIDDLE);
//            cellL2.setFontSize(tan);
//            cellL2.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
//            cellL2.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
//            //  cellL2.setBorderStyle(null);
//
//            cellL2 = l2.createCell(15, "Fotos:"+"legajo.getLegajoFoto()");
//            cellL2.setAlign(HorizontalAlignment.LEFT);
//            cellL2.setValign(VerticalAlignment.MIDDLE);
//            cellL2.setFontSize(tan);
//            cellL2.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
//            cellL2.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
//            //  cellL2.setBorderStyle(null);
//
//            cellL2 = l2.createCell(20, "Inscripcion:"+aporteSumas);
//            cellL2.setAlign(HorizontalAlignment.LEFT);
//            cellL2.setValign(VerticalAlignment.MIDDLE);
//            cellL2.setFontSize(tan);
//            cellL2.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
//            cellL2.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
//            //  cellL2.setBorderStyle(null);


            cuadro.endText();
            tablel2.draw();
            H=H+tablel2.getHeaderAndDataHeight();
            BaseTable tablel3 = new BaseTable(yStart - H, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 800);//X=40
            Row<PDPage> l3 = tablel3.createRow(10);
            Cell<PDPage> cellL3 = l3.createCell(50, "Recepcionado por: "+atencion.getAtencionUsuario());//70
            cellL3.setAlign(HorizontalAlignment.LEFT);
            cellL3.setValign(VerticalAlignment.MIDDLE);
            cellL3.setFontSize(tan);
            cellL3.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL3.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
            // cellL3.setBorderStyle(null);
            cellL3 = l3.createCell(35, "Enviar a: ");
            cellL3.setAlign(HorizontalAlignment.LEFT);
            cellL3.setValign(VerticalAlignment.MIDDLE);
            cellL3.setFontSize(tan);
            cellL3.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL3.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
            //cellL3.setBorderStyle(null);
            cuadro.endText();
            tablel3.draw();
            H=H+tablel3.getHeaderAndDataHeight();

            BaseTable tablel4 = new BaseTable(yStart - H, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 800);//X=40
            Row<PDPage> l4 = tablel4.createRow(20);
            Cell<PDPage> cellL4 = l4.createCell(85, "Observaciones"+atencion.getAtencionObservaciones());//100
            cellL4.setAlign(HorizontalAlignment.LEFT);
            cellL4.setValign(VerticalAlignment.MIDDLE);
            cellL4.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL4.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL4.setTopBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL4.setFontSize(tan);
            cuadro.endText();
            tablel4.draw();
            H=H+tablel4.getHeaderAndDataHeight();

            BaseTable tablel5 = new BaseTable(yStart - H, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            cuadro.beginText();
            cuadro.newLineAtOffset(0, 800);//X=40
            Row<PDPage> l5 = tablel5.createRow(30);
            Cell<PDPage> cellL5 = l5.createCell(40, "Firma Retira tramite: ");//100
            cellL5.setAlign(HorizontalAlignment.LEFT);
            cellL5.setValign(VerticalAlignment.TOP);
            cellL5.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL5.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL5.setTopBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL5.setFontSize(7);

            cellL5 = l5.createCell(30, "Aclaracion: ");
            cellL5.setAlign(HorizontalAlignment.LEFT);
            cellL5.setValign(VerticalAlignment.TOP);
            cellL5.setFontSize(7);
            cellL5.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL5.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL5.setTopBorderStyle(new LineStyle(Color.WHITE, 0f));



            cellL5 = l5.createCell(15, "Fecha: ");
            cellL5.setAlign(HorizontalAlignment.LEFT);
            cellL5.setValign(VerticalAlignment.TOP);
            cellL5.setFontSize(7);
            cellL5.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL5.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL5.setTopBorderStyle(new LineStyle(Color.WHITE, 0f));


            cellL5 = l5.createCell(15, "El trámite podrá ser seguido escaneando el código QR.");
            cellL5.setAlign(HorizontalAlignment.CENTER);
            cellL5.setValign(VerticalAlignment.BOTTOM);
            cellL5.setFontSize(5);
            cellL5.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL5.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
            cellL5.setTopBorderStyle(new LineStyle(Color.WHITE, 0f));
            cuadro.endText();
            tablel5.draw();
            H=H+tablel5.getHeaderAndDataHeight();
            cuadro.close();
//
//            BufferedImage qrImage = generarQRImagen("http://localhost:4200/seguimientoTramite/"+atencion.getCodigoSeguimiento());
//            PDImageXObject pdImage = LosslessFactory.createFromImage(Documento, qrImage);
//            PDPageContentStream contentStream =
//                    new PDPageContentStream(Documento, Pagina);
//            contentStream.drawImage(pdImage, 50, 500, 150, 150);
//            contentStream.close();


            cuadro.close(); // 👈 PRIMERO cerrar el stream anterior

            BufferedImage qrImage = generarQRImagen(
                    "https://gestionacademica.iesijujuy.edu.ar/seguimientoTramite/" +
                            atencion.getCodigoSeguimiento()
            );

            PDImageXObject pdImage =
                    LosslessFactory.createFromImage(Documento, qrImage);

            PDPageContentStream contentStream =
                    new PDPageContentStream(
                            Documento,
                            Pagina,
                            PDPageContentStream.AppendMode.APPEND,
                            true,
                            true
                    );

            contentStream.drawImage(pdImage, 495, 670-altQr, 65, 65);
            contentStream.close();




            //Documento.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return Documento;
    }


    public BufferedImage generarQRImagen(String contenido) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN,1); // 🔥 eliminar borde blanco

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    contenido,
                    BarcodeFormat.QR_CODE,
                    70,
                    70,
                    hints
            );

            return MatrixToImageWriter.toBufferedImage(bitMatrix);

        } catch (WriterException e) {
            throw new RuntimeException("Error generando QR", e);
        }
    }




    @Override
    public PDDocument generaTroquelPase(Integer paseId) {
        System.out.println("Inicio generaTroquelPase, paseId=" + paseId);

        Pases pase = this.paseService.findById(Long.valueOf(paseId));
        if (pase == null) {
            System.out.println("Pase no encontrado, retornando null");
            return null;
        }

        PDDocument documento = new PDDocument();
        System.out.println("Documento PDF creado");

        try {
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);
            System.out.println("Página añadida");

            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;

            float margin = 30;
            float yStart = pagina.getMediaBox().getHeight() - 10;
            float bottomMargin = 70;
            float tableWidth = pagina.getMediaBox().getWidth() - 2 * margin;
            float yStartNewPage = pagina.getMediaBox().getHeight() - margin;

            System.out.println("Creando tabla BaseTable...");
            BaseTable tabla = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, documento, pagina, true, true);
            int fontSize = 10;

            // Trámite ID
            Row<PDPage> filaTramite = tabla.createRow(15);
            Cell<PDPage> c1 = filaTramite.createCell(70, "Trámite N°: " + (pase.getTramite() != null ? pase.getTramite().getId() : ""));
            c1.setFontSize(fontSize);
            c1.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            c1.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fechaCreacion = pase.getCreatedAt() != null ? pase.getCreatedAt().format(formatter) : "";
            c1 = filaTramite.createCell(30, "Fecha: " + fechaCreacion);
            c1.setFontSize(fontSize);
            c1.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            c1.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));
            System.out.println("Fila trámite creada");

            // De Usuario
            Row<PDPage> filaDe = tabla.createRow(15);
            String deUsuario = "Sin asignar";
            String deArea = "Sin área";
            if (pase.getDeUsuario() != null) {
                deUsuario = (pase.getDeUsuario().getPersonalNombre() != null ? pase.getDeUsuario().getPersonalNombre() : "")
                        + " " + (pase.getDeUsuario().getPersonalApellido() != null ? pase.getDeUsuario().getPersonalApellido() : "");
                if (pase.getDeUsuario().getDestino() != null && pase.getDeUsuario().getDestino().getNombre() != null) {
                    deArea = pase.getDeUsuario().getDestino().getNombre();
                }
            }
            System.out.println("De Usuario: " + deUsuario + ", Area: " + deArea);

            Cell<PDPage> c2 = filaDe.createCell(50, "De Usuario: " + deUsuario);
            c2.setFontSize(fontSize);
            c2.setLeftBorderStyle(new LineStyle(Color.WHITE, 0f));
            c2.setRightBorderStyle(new LineStyle(Color.WHITE, 0f));

            c2 = filaDe.createCell(50, " Area: " + deArea);
            c2.setFontSize(fontSize);

            // Para Usuario
            Row<PDPage> filaParaUsuario = tabla.createRow(15);
            String paraUsuario = "Sin asignar";
            String paraArea = "Sin área";
            if (pase.getParaUsuario() != null) {
                paraUsuario = (pase.getParaUsuario().getPersonalNombre() != null ? pase.getParaUsuario().getPersonalNombre() : "")
                        + " " + (pase.getParaUsuario().getPersonalApellido() != null ? pase.getParaUsuario().getPersonalApellido() : "");
                if (pase.getParaUsuario().getDestino() != null && pase.getParaUsuario().getDestino().getNombre() != null) {
                    paraArea = pase.getParaUsuario().getDestino().getNombre();
                }
            }
            System.out.println("Para Usuario: " + paraUsuario + ", Area: " + paraArea);

            Cell<PDPage> c4 = filaParaUsuario.createCell(50, "Para Usuario: " + paraUsuario);
            c4.setFontSize(fontSize);

            c4 = filaParaUsuario.createCell(50, " Area: " + paraArea);
            c4.setFontSize(fontSize);

            // Observaciones
            Row<PDPage> filaObs = tabla.createRow(15);
            Cell<PDPage> c7 = filaObs.createCell(100, "Observaciones: " + (pase.getObservaciones() != null ? pase.getObservaciones() : ""));
            c7.setFontSize(fontSize);

            System.out.println("Dibujando tabla...");
            tabla.draw();
            System.out.println("Tabla dibujada correctamente");

        } catch (Exception e) {
            System.out.println("ERROR en generaTroquelPase:");
            e.printStackTrace();
            return null;
        }

        System.out.println("PDF generado correctamente, retornando documento");
        return documento;
    }





@Override
public PDDocument crearPDFPorUsuario(String dni, LocalDate fechaInicio, LocalDate fechaFin) {
        PDDocument Documento = new PDDocument();
        Personal personal= personalService.findById(dni).get();
        try {
            LocalDate fechaActual = fechaInicio;
            while (!fechaActual.isAfter(fechaFin)) {
                System.out.println("Generando PDF para: " + fechaActual);
                PDImageXObject Iesc1;
                InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
                if (iesc1I == null) {
                    System.out.println("readFilesInBytes: File " + "file" + " does not exist");
                }
                byte[] ba = IOUtils.toByteArray(iesc1I);
                Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");
                PDPage Pagina = new PDPage(PDRectangle.A4);
                Documento.addPage(Pagina);
                PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);
                PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
                int fontSize = 8; // Tamaño de la fuente
                contenido.setFont(font, fontSize);
                float pageHeight = PDRectangle.A4.getHeight();
                float pageWidth = PDRectangle.A4.getWidth();
                String[] lines = {
                        "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                        "“CAMPINTA GUAZU GLORIA PEREZ”",
                        "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                        "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                        "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                        "________________________________________________________________________________________________________________________"
                };
                int n = -10;
                contenido.beginText();
                float iStart = 820;
                contenido.newLineAtOffset(0, iStart);
                for (String line : lines) {
                    // Calcula el ancho de cada línea
                    float textWidth = font.getStringWidth(line) / 1000 * fontSize;
                    // Calcula la posición x para centrar el texto
                    float xStart = (pageWidth - textWidth) / 2;
                    // Mueve la posición x
                    contenido.newLineAtOffset(xStart, 0);
                    // Escribe la línea
                    contenido.showText(line);
                    // Mueve a la siguiente línea
                    contenido.newLineAtOffset(-xStart, n);
                }
                contenido.endText();
                contenido.close();
                //imagen del encavezado izquierda
                PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
                PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
                PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
                PDesc1.close();

                PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
                n = -18;//distancia entre lineas
                regular.beginText();
                regular.setFont(PDType1Font.HELVETICA_BOLD, 15);
                regular.newLineAtOffset(40, 740);//titulo
                regular.showText("REGISTRO DE ASISTENCIA");
                regular.newLineAtOffset(0, n);
                regular.setFont(PDType1Font.HELVETICA, 10);
                regular.showText("INSTITUTO DE EDUCACION INTERCULTURAL CAMPINTA GUAZU GLORIA PEREZ");
                regular.newLineAtOffset(0, n);
                regular.showText("Personal: " + personal.getPersonalApellido()+", "+personal.getPersonalNombre());
                regular.newLineAtOffset(0, n);
                regular.showText("DNI: " + personal.getId());

                regular.endText();
                regular.close();
                PDRectangle mediabox = Pagina.getMediaBox();
                float margin = 20;
                float width = mediabox.getWidth() - 4 * margin;
                float X = mediabox.getLowerLeftX() + margin;
                float Y = mediabox.getUpperRightY() - margin;
                List<String> lineas = new ArrayList<String>();
                int letra = 12;
                PDType1Font normal = PDType1Font.HELVETICA;
                PDType1Font negrita = PDType1Font.HELVETICA_BOLD;
                PDPageContentStream cuadro = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
                margin = 20;
                // starting y position is whole page height subtracted by top and bottom margin
                float yStartNewPage = Pagina.getMediaBox().getHeight() - (2 * margin);
                // we want table across whole page width (subtracted by left and right margin ofcourse)
                float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
                boolean drawContent = true;
                float yStart = 650;//yStartNewPage;
                float bottomMargin = 30;
                float auxmargin = 40;
                float yPosition = 300;

                //==========================================
                BaseTable table = new BaseTable(yStart + 30, yStartNewPage, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
                int espaciado = 0;
                cuadro.beginText();
                cuadro.newLineAtOffset(0, 600);//X=40
                Row<PDPage> headerRow = table.createRow(20);
                int a = 5;
                Cell<PDPage> cell = headerRow.createCell(5, "ID");
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                System.out.println(cell.getHeight());
                float h = cell.getInnerWidth();
                cell = headerRow.createCell(30, "Razon");//30
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                // cell.setTextRotated(true);
                float b = cell.getInnerWidth();
                cuadro.setCharacterSpacing(espaciado);

                cell = headerRow.createCell(10, "Estado");//11
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                float r = cell.getInnerWidth();

                cell = headerRow.createCell(10, "Fecha");//11
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                float jr = cell.getInnerWidth();

                cell = headerRow.createCell(8, "Entrada");//11
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                float rr = cell.getInnerWidth();
                cell = headerRow.createCell(8, "Salida");
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                float d = cell.getExtraWidth();
                cell.setFont(PDType1Font.HELVETICA);

                cell = headerRow.createCell(20, "Obs");
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                float E = cell.getExtraWidth();
                cell.setFont(PDType1Font.HELVETICA);

                table.draw();
                BaseTable Materiasaño = new BaseTable(yStart - headerRow.getHeight() + 1 + 30, yStartNewPage + 1, bottomMargin, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
                float H = 0;
                int j = 1;


                List<DetalleAsistenciaPersonalDTO> asistencias = asistenciaPersonalService.obtenerDetallePorDNI(personal.getId());

                for (DetalleAsistenciaPersonalDTO dato : asistencias) {
                    SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat formatoNuevo = new SimpleDateFormat("dd-MM-yyyy");
                    int nk = 6;
                    Row<PDPage> rew = Materiasaño.createRow(5);//19
                    // Celda para la columna "Orden"
                    Cell<PDPage> cellOrden = rew.createCell(5, String.valueOf(j));
                    cellOrden.setAlign(HorizontalAlignment.CENTER);
                    cellOrden.setValign(VerticalAlignment.MIDDLE);
                    cellOrden.setFont(PDType1Font.HELVETICA);
                    cellOrden.setFontSize(nk);
                    Cell<PDPage> cellNombreMateria = rew.createCell(30, dato.getMateriaNombre());
                    cellNombreMateria.setFontSize(nk);
                    cellNombreMateria.setValign(VerticalAlignment.MIDDLE);
                    cellNombreMateria.setFont(PDType1Font.HELVETICA);

                    Cell<PDPage> cellNotaFinalll = rew.createCell(10, getEstadoTexto(dato.getEstado()));

                    cellNotaFinalll.setFontSize(nk);
                    cellNotaFinalll.setAlign(HorizontalAlignment.CENTER);
                    cellNotaFinalll.setValign(VerticalAlignment.MIDDLE);
                    cellNotaFinalll.setFont(PDType1Font.HELVETICA);

                    String fechaFormateada = dato.getFecha()
                            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                    Cell<PDPage> cellNotaFinallll = rew.createCell(10, fechaFormateada);
                    cellNotaFinallll.setFontSize(nk);
                    cellNotaFinallll.setAlign(HorizontalAlignment.CENTER);
                    cellNotaFinallll.setValign(VerticalAlignment.MIDDLE);
                    cellNotaFinallll.setFont(PDType1Font.HELVETICA);

                    Cell<PDPage> cellyear = rew.createCell(8, dato.getHoraEntrada());
                    cellyear.setFontSize(nk);
                    cellyear.setAlign(HorizontalAlignment.CENTER);
                    cellyear.setValign(VerticalAlignment.MIDDLE);
                    cellyear.setFont(PDType1Font.HELVETICA);
                    Cell<PDPage> cellyeart = rew.createCell(8, dato.getHoraSalida());
                    cellyeart.setFontSize(nk);
                    cellyeart.setAlign(HorizontalAlignment.CENTER);
                    cellyeart.setValign(VerticalAlignment.MIDDLE);
                    cellyeart.setFont(PDType1Font.HELVETICA);
                    Cell<PDPage> cellyeartt = rew.createCell(20, dato.getObservaciones());
                    cellyeartt.setFontSize(nk);
                    cellyeartt.setAlign(HorizontalAlignment.CENTER);
                    cellyeartt.setValign(VerticalAlignment.MIDDLE);
                    cellyeartt.setFont(PDType1Font.HELVETICA);
                    float filaHeight = rew.getHeight();
                    H = H + filaHeight;
                    j++;
                }
                Materiasaño.draw();
                cuadro.endText();
                cuadro.close();
                //============================
                PDPageContentStream pie = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
                n = -17;//distancia entre lineas
                pie.beginText();
                pie.setFont(PDType1Font.HELVETICA_BOLD, 12);
                pie.newLineAtOffset(40, yStart - H - 70);
                pie.setFont(PDType1Font.HELVETICA, 10);
                n = -10;
                pie.newLineAtOffset(410, 0);
                pie.newLineAtOffset(5, n);
                LocalDate hoy = LocalDate.now();
                String fechaFormateada = hoy.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                pie.showText("Registro generado el día " + fechaFormateada);
                pie.endText();
                pie.close();
                cuadro.close();
                //Documento.close();
                // Incrementar la fecha
                fechaActual = fechaActual.plusDays(1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Documento;
    }

    private String getEstadoTexto(Integer estado) {
        if (estado == null) return "DESCONOCIDO";

        switch (estado) {
            case 0: return "PRESENTE";
            case 1: return "TARDANZA";
            case 2: return "SALIDA TEMPRANA";
            case 3: return "FALTA JUSTIFICADA";
            case 4: return "FALTA INJUSTIFICADA";
            case 5: return "REQUIERE INFORMACION";
            case 6: return "MESA DE EXAMEN";
            case 7: return "JORNADA INSTITUCIONAL";
            case 8: return "FERIADO";
            case 9: return "SUSPENSION DE ACTIVIDADES";
            default: return "DESCONOCIDO";
        }
    }



    @Override
    public PDDocument generaAsistenciaExamenFinalDocente(String dni, String autoridades, String carreraSol,
                                                         String fechaSeleccionada, String accion, String materia) {
        PDImageXObject Iesc1;
        Persona persona = this.alumnoService.findAlumnoById(dni);
        PDDocument Documento = new PDDocument();
        try {
            InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc1I == null) {
                System.out.println("readFilesInBytes: File " + "file" + " does not exist");
            }
            byte[] ba = IOUtils.toByteArray(iesc1I);
            Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");

            PDPage Pagina = new PDPage(PDRectangle.A4);
            Documento.addPage(Pagina);
            PDPageContentStream contenido = new PDPageContentStream(Documento, Pagina);

            PDType1Font font = PDType1Font.HELVETICA; // Definimos la fuente
            int fontSize = 8; // Tamaño de la fuente
            contenido.setFont(font, fontSize);

// Altura de la página
            float pageHeight = PDRectangle.A4.getHeight();

// Ancho de la página
            float pageWidth = PDRectangle.A4.getWidth();

// Texto para cada línea
            String[] lines = {
                    "INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL",
                    "“CAMPINTA GUAZU GLORIA PEREZ”",
                    "Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15",
                    "Bahia Blanca Nº 235 Bº Kennedy – Tel.N°(0388)-3428370)",
                    "(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina",
                    "________________________________________________________________________________________________________________________"
            };

            int n = -10;
            contenido.beginText();
            float iStart = 820;
            contenido.newLineAtOffset(0, iStart);
            for (String line : lines) {
                float textWidth = font.getStringWidth(line) / 1000 * fontSize;
                float xStart = (pageWidth - textWidth) / 2;
                contenido.newLineAtOffset(xStart, 0);
                contenido.showText(line);
                contenido.newLineAtOffset(-xStart, n);
            }
            contenido.endText();
            contenido.close();
            PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
            PDesc1.drawImage(Iesc1, 30, 770, 65, 60);//Draw an image at the x,y coordinates, with the given size.
            PDesc1.close();

            PDPageContentStream regular = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //texto de constancia
            n = -10;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 10);
            regular.newLineAtOffset(160, 720);//titulo210
            regular.showText("CONSTANCIA DE ASISTENCIA A MESA DE EXAMEN FINAL");
            regular.newLineAtOffset(0, 0);
            regular.showText("___________________________________________________");
            regular.endText();
            ///=============================================================
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            int letra = 12;
            String hola = " ____________________";
            String texto = Dividir(hola, width, letra);
            lineas = procesar(texto, letra);
            //===================================================================
//Justificar texto
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;

            //consulta
            String genero = persona.getPersonaGenero();
            String genero2 = "";
            String genero1 = "";
            if (genero.equals("Masculino")) {
                genero1 = "el SR";
                genero2 = "el interesado";
                System.out.println("Es Hombre");
            } else {
                genero1 = "la Sra";
                genero2 = "la interesada";
                System.out.println("Es Mujer");
            }
            regular.beginText();
            regular.setFont(normal, letra);
            regular.newLineAtOffset(50, 680);
            String t1 = ("-----Por la presente, la Rectora del ");
            String t2 = ("Instituto de Educación Superior Intercultural CAMPINTA ");
            //regular.setCharacterSpacing(charspacing(longitud,t1+t2,letra));//espacio entre caracteres
            regular.showText(t1);
            regular.newLineAtOffset(tamaño(t1, letra, normal), 0);
            regular.setFont(negrita, letra);
            //String t2=("Instituto de Educación Superior Intercultural CAMPINTA");
            regular.showText(t2);
            float longitud = tamaño(t1 + t2, letra, PDType1Font.HELVETICA) + 20;//longitud permitida para justificar
            regular.newLineAtOffset(-tamaño(t1, letra, normal), -20);//nueva linea abajo justo al inicio
            String t3 = ("GUAZU GLORIA PEREZ ");
            String t4 = ("Prof. Cristina Noemi Martínez, deja ");//CONSTANCIA que ");
            String t41 = ("CONSTANCIA ");
            String t5 = ("que " + genero1);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41));//espacio entre caracteres
            regular.showText(t3);
            regular.setFont(PDType1Font.HELVETICA, letra);
            regular.newLineAtOffset(tamaño(t3, letra, negrita) + t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            //String t4=(" Prof. Cristina Noemi Martínez, deja CONSTANCIA que el Sr");
            regular.showText(t4);

            regular.newLineAtOffset(tamaño(t4, letra, normal) + t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t41);
            regular.newLineAtOffset(tamaño(t41, letra, normal) + t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), 0);
            regular.showText(t5);
            regular.newLineAtOffset(-tamaño(t3, letra, negrita) - tamaño(t41, letra, negrita) - tamaño(t4, letra, normal) - t3.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t4.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41) - t41.length() * charspacing(longitud, tamaño(t4, letra, normal) + tamaño(t3, letra, negrita) + tamaño(t41, letra, negrita) + tamaño(t5, letra, normal), t3 + t4 + t5 + t41), -20);////charspacing(longitud, tamaño(t3, letra, normal),t3+t4)-tamaño(t4,letra, PDType1Font.HELVETICA)-t4.length()*charspacing(longitud, tamaño(t3, letra, normal),t3+t4+t5),-20 );
            String nombre = persona.getPersonaNombre();
            String apellido = persona.getPersonaApellido();
            String alumno_dni = String.valueOf(persona.getPersonaDni());
            String t6 = (apellido + " " + nombre + " D.N.I: " + alumno_dni + " ");
            String t7 = (", Docente de la carrera");
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, +tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7));//espacio entre caracteres
            regular.showText(t6);
            regular.newLineAtOffset(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t7);
            regular.newLineAtOffset(-(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7)), -20);//linea nueav
            String carrera = "Tecnicatura Superior en " + carreraSol;
            String t10 = ("");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10));//espacio entre caracteres
            regular.setFont(negrita, letra);
            regular.showText(carrera);
            regular.newLineAtOffset(tamaño(carrera, letra, negrita) + carrera.length() * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t10);
            regular.newLineAtOffset(-(tamaño(carrera, letra, negrita) + (carrera.length()) * charspacing(longitud, tamaño(carrera, letra, negrita) + tamaño(t10, letra, normal), carrera + t10)), -20);//linea nuea
            String t12 = "";

            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha = parser.parse(fechaSeleccionada);
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
            String fechaFormateada = formatoFecha.format(fecha);

            String t11 = (accion + " el dia: " + fechaFormateada + " a la MESA DE EXAMEN FINAL correspondiente a la materia: ");
            String t13 = ("");
            t13 = rellenar(t11 + t12 + t13, t13, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13));//espacio entre caracteres
            regular.showText(t11);
            regular.newLineAtOffset(tamaño(t11, letra, normal) + t11.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t12);
            System.out.println(t13 + "//////////////////");
            //String t13=("Año de este Instituto");
            regular.newLineAtOffset(tamaño(t12, letra, normal) + t12.length() * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13), 0);
            regular.showText(t13);
            String mat = rellenar(materia, materia, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(mat, letra, normal), mat));//espacio entre caracteres
            regular.newLineAtOffset(-(tamaño(t11, letra, normal) + (t11.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)) - (tamaño(t12, letra, PDType1Font.HELVETICA) + (t12.length()) * charspacing(longitud, tamaño(t11, letra, normal) + tamaño(t12, letra, normal) + tamaño(t13, letra, normal), t11 + t12 + t13)), -20);//linea nueav
            regular.showText(mat);
            String t14 = ("-----Se expide la presente CONSTANCIA a solicitud de " + genero2 + "  y al solo efecto de ser");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal), t14));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);//linea nueav
            regular.showText(t14);
            String t15 = ("presentada ante las Autoridades del ");
            String t16 = autoridades;
            System.out.println("autoridades " + t16);
            if (t16.equals("que lo requieran")) {
                t15 = "presentada ante las Autoridades";
            }

            t16 = rellenar(t15 + t16, t16, letra, longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, negrita), t15 + t16));//espacio entre caracteres
            regular.newLineAtOffset(0, -20);
            regular.setFont(normal, letra);
            regular.showText(t15);
            // String t16=("B.E.G.U.P");
            regular.newLineAtOffset(tamaño(t15, letra, normal) + t15.length() * charspacing(longitud, tamaño(t15, letra, normal) + tamaño(t16, letra, normal), t15 + t16), 0);
            regular.setFont(normal, letra);
            regular.showText(t16);
            //String t17=("------------------SAN SALVADOR DE JUJUY, "+fecha());
            String t17 = ("-----SAN SALVADOR DE JUJUY, " + fecha() + "-------------");
            regular.newLineAtOffset(-(tamaño(t15, letra, normal) + (t15.length()) * charspacing(longitud, tamaño(t16, letra, normal) + tamaño(t15, letra, normal), t15 + t16)), -20);//linea nueav
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t17, letra, normal), t17));//espacio entre caracteres
            regular.showText(t17);
            regular.newLineAtOffset(0, -20);
            margin = 30;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (4 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = yStartNewPage + 30;
            float bottomMargin = 70;
            float yPosition = 550;
            BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, Documento, Pagina, true, drawContent);
            Row<PDPage> headerRow = table.createRow(300);
            Cell<PDPage> cell = headerRow.createCell(100, " ");
            //cell.setText("hoal a atodsssss");
            table.draw();
            regular.endText();
            regular.close();
            System.out.println("se divujo la imagen");
            //   Documento.save(dir+".pdf");
            //Documento.close();
        } catch (IOException e) {
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return Documento;
    }



@Override
public PDDocument generaPermiso(String libreta, String turno, String usuarioNombre) {
        int columnas = 6;
        int n = -8; // distancia entre lineas
        int letra = 10; // Tamaño de letras
        double nuevoProm = 0;
        int contProm = 0;
        PDImageXObject Iesc1, Iesc2;
        PDDocument Documento = new PDDocument();
        try {
//            Optional<Legajo> legajo= legajoService.findById(libreta);
//            Alumno alumno = legajo.get().getLegajoAlumnoDni();

            Legajo legajo = legajoService.findById(libreta)
                    .orElseThrow(() -> new RuntimeException("No se encontró el legajo con ID: " + libreta));

            Persona persona = legajo.getLegajoPersonaDni();
            Turno turnoT=turnoService.obtenerTurnoPorId(turno);

            String carrera = permisoService.obtenerCarreraPorLibreta(libreta);
            Optional<Permiso> permiso = permisoService.findPermisoByLegajoAndTurnoOrdered(libreta, turno);
            int dni = permisoService.obtenerDniPorLibreta(libreta);
            String nombre = permisoService.obtenerNombrePorDni(dni);
            String apellido = permisoService.obtenerApellidoPorDni(dni);
            List<InscripcionExamenDTO> inscripcionesActivas = examenService.completarCursadas(libreta, turno);
            PDType1Font normal = PDType1Font.HELVETICA;
            PDType1Font negrita = PDType1Font.HELVETICA_BOLD;

            PDRectangle a4 = PDRectangle.A4;
            PDRectangle a4Landscape = new PDRectangle(a4.getHeight(), a4.getWidth());
            PDPage Pagina = new PDPage(a4Landscape);
            Documento.addPage(Pagina);
            PDPageContentStream encabezado = new PDPageContentStream(Documento, Pagina);
            float margin = 25; //40
            InputStream iesc2I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
            if (iesc2I == null) {
                System.out.println("readFilesInBytes: File does not exist");
            }
            byte[] be = IOUtils.toByteArray(iesc2I);
            Iesc2 = PDImageXObject.createFromByteArray(Documento, be, "static/imagenes/esc2.png");

            //===================================Texto del encabezado==============================================//
            int inicio=421;//desde le borde o desde el centro como esta hoja es horizontal
            encabezado.beginText();
            encabezado.setFont(PDType1Font.HELVETICA, 8);
            encabezado.newLineAtOffset(inicio+105, 580);//580
            encabezado.showText("INSTITUTO DE EDUCACIÓN SUPERIOR INTERCULTURAL");
            encabezado.newLineAtOffset(40, n);
            encabezado.showText("“CAMPINTA GUAZÚ GLORIA PÉREZ”");
            encabezado.newLineAtOffset(-25, n);
            encabezado.showText("Incorporado a la Enseñanza Oficial – Resol. Nº 2936-E-15");
            encabezado.newLineAtOffset(-15, n);
            encabezado.showText("Bahía Blanca Nº 235, Bº Kennedy – Tel N° (0388) 3428370");
            encabezado.newLineAtOffset(-40, n);
            encabezado.showText("(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. de Jujuy – República Argentina");
            encabezado.newLineAtOffset(-40, -3);

            encabezado.showText("_____________________________________________________________________________________");
            encabezado.endText();
            encabezado.close();
            PDPageContentStream PDesc2 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            PDesc2.moveTo(200, 100);
            PDesc2.drawImage(Iesc2, 15+inicio, 550, 40, 40);
            PDesc2.close();
            PDPageContentStream titulo = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            // Texto de constancia
            n = -11; // distancia entre lineas
            titulo.beginText();
            titulo.setFont(PDType1Font.HELVETICA_BOLD, 10);
            titulo.newLineAtOffset(160+inicio, 530); // titulo/(250,745)
            titulo.showText("Permiso de Examen");
            titulo.newLineAtOffset(0, 0);
            titulo.showText("_________________");
            titulo.endText();
            titulo.close();
            PDPageContentStream pTexto = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            String genero1 = "";
            String genero = persona.getPersonaGenero();
            if (genero.equals("Masculino")) {
                genero1 = "el ";
            } else {
                genero1 = "la";
            }
            float longitud = 375; // longitud permitida para justificar
            pTexto.beginText();
            pTexto.setFont(normal, letra);
            pTexto.newLineAtOffset(25+inicio, 515);
            String carrera_nombre = carrera;
            String t1 =  "Permiso N°:" + permiso.get().getId() + "      Turno:" +turnoT.getTurnoMes()+"  Llamado: "+turnoT.getLlamado();
            String t2 = "Conste que por la presente, " + genero1 + " estudiante: " + persona.getPersonaApellido() + " " + persona.getPersonaNombre() + ",";
            String t3 = "DNI: " + dni + ", está habilitado para rendir las siguientes Unidades Curriculares:";
            String t4 = "correspondientes a la carrera: " + carrera_nombre + ".";
            pTexto.setCharacterSpacing(charspacing(longitud, tamaño(t1, letra, normal), t1));//espacio entre caracteres
            pTexto.showText(t1);
            pTexto.newLineAtOffset(0, n);
            pTexto.setCharacterSpacing(charspacing(longitud, tamaño(t2, letra, normal), t2));//espacio entre caracteres
            pTexto.showText(t2);
            pTexto.newLineAtOffset(0, n);
            pTexto.setCharacterSpacing(charspacing(longitud, tamaño(t3, letra, normal), t3));//espacio entre caracteres
            pTexto.showText(t3);
            pTexto.newLineAtOffset(0, n);
            pTexto.setCharacterSpacing(charspacing(longitud, tamaño(t4, letra, normal), t4));//espacio entre caracteres
            pTexto.showText(t4);
            pTexto.setCharacterSpacing(0);
            pTexto.endText();
            pTexto.close();
            PDRectangle mediabox = Pagina.getMediaBox();
            float width = mediabox.getWidth() - 4 * margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas = new ArrayList<String>();
            margin = 60;
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (2 * margin);
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = 480; // yStartNewPage;
            float bottomMargin = 70;
            float auxmargin = 25+inicio;
            float yPosition = 300;
            BaseTable table = new BaseTable(yStart-5, yStartNewPage, 0, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            Row<PDPage> headerRow = table.createRow(20);
            Cell<PDPage> cell = headerRow.createCell(5, "N°");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setTextRotated(false);
            cell = headerRow.createCell(7, "Condicion");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setTextRotated(false);
            cell = headerRow.createCell(18, "Unidad curricular");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell = headerRow.createCell(8, "Fecha");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell = headerRow.createCell(5, "Hora");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell = headerRow.createCell(9, "Nota");
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setValign(VerticalAlignment.MIDDLE);
            yStart = yStart - headerRow.getHeight();
            int indice = 0;



            for (int row1 = 0; row1 < inscripcionesActivas.size(); row1++) {
                //Boolean isInscripto = (Boolean) inscripcionesActivas.getValueAt(row1, 5);
                Boolean isInscripto = (Boolean) inscripcionesActivas.get(row1).getInscripto();
                if (isInscripto != null && isInscripto) {
                    indice++;
                    String materia = (String) inscripcionesActivas.get(row1).getMateriaNombre();
                    String condicion = (String) inscripcionesActivas.get(row1).getCondicion();

                    if (!condicion.equals("Regular")) {
                        condicion = "Libre";
                    }
                    Row<PDPage> row = table.createRow(20);

// Celda 1: Número de orden, alineado al centro
                    Cell<PDPage> cellNumero = row.createCell(5, String.valueOf(indice));
                    cellNumero.setAlign(HorizontalAlignment.CENTER);
                    cellNumero.setValign(VerticalAlignment.MIDDLE);

// Celda 2: Condición (sin alineación especial, si querés podés centrarla también)
                    Cell<PDPage> cellCondicion = row.createCell(7, condicion);
                    cellCondicion.setAlign(HorizontalAlignment.LEFT); // O podés poner CENTER si lo querés centrado
                    cellCondicion.setValign(VerticalAlignment.MIDDLE);

// Celda 3: Unidad Curricular (Materia)
                    Cell<PDPage> cellMateria = row.createCell(18, materia);
                    cellMateria.setAlign(HorizontalAlignment.LEFT); // Generalmente los textos largos van alineados a la izquierda
                    cellMateria.setValign(VerticalAlignment.MIDDLE);

                    String turno_id = turno;
                    String materia_id = (String) inscripcionesActivas.get(row1).getMateriaId();
                    String fecha = inscripcionesActivas.get(row1).getFecha();
                    if (fecha != null && !fecha.isEmpty()) {
                        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd"); // Asumiendo que la fecha viene en formato yyyy-MM-dd
                        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date fechaDate = formatoEntrada.parse(fecha);
                            fecha = formatoSalida.format(fechaDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    String hora =  inscripcionesActivas.get(row1).getHora();
                    // Celda 4: Fecha
                    Cell<PDPage> cellFecha = row.createCell(8, fecha);
                    cellFecha.setAlign(HorizontalAlignment.CENTER);
                    cellFecha.setValign(VerticalAlignment.MIDDLE);

// Celda 5: Hora
                    Cell<PDPage> cellHora = row.createCell(5, hora);
                    cellHora.setAlign(HorizontalAlignment.CENTER);
                    cellHora.setValign(VerticalAlignment.MIDDLE);

// Celda 6: Firma (espacio vacío)
                    Cell<PDPage> cellFirma = row.createCell(9, " ");
                    cellFirma.setAlign(HorizontalAlignment.CENTER);
                    cellFirma.setValign(VerticalAlignment.MIDDLE);

                    yStart = yStart - row.getHeight();
                    // Asignar a strings y hacer algo con ellos (por ejemplo, imprimirlos)
                    System.out.println("Materia: " + materia + ", Condicion: " + condicion);
                }
            }

            System.out.println("indice vale: " + indice);
            if (indice < 6) {
                int rowsToAdd = 6 - indice;
                System.out.println("filas agregar vale: " + rowsToAdd);
                for (int i = 0; i < rowsToAdd; i++) {
                    indice++;
                    Row<PDPage> row = table.createRow(20);

// Celda: Orden
                    Cell<PDPage> cellOrden = row.createCell(5, String.valueOf(indice));
                    cellOrden.setAlign(HorizontalAlignment.CENTER);
                    cellOrden.setValign(VerticalAlignment.MIDDLE);

// Celda: Condición
                    Cell<PDPage> cellCondicion = row.createCell(7, "");
                    cellCondicion.setAlign(HorizontalAlignment.CENTER);
                    cellCondicion.setValign(VerticalAlignment.MIDDLE);

// Celda: Unidad Curricular
                    Cell<PDPage> cellMateria = row.createCell(18, "");
                    cellMateria.setAlign(HorizontalAlignment.CENTER);
                    cellMateria.setValign(VerticalAlignment.MIDDLE);

// Celda: Fecha
                    Cell<PDPage> cellFecha = row.createCell(8, " ");
                    cellFecha.setAlign(HorizontalAlignment.CENTER);
                    cellFecha.setValign(VerticalAlignment.MIDDLE);

// Celda: Calificación
                    Cell<PDPage> cellCalificacion = row.createCell(5, " ");
                    cellCalificacion.setAlign(HorizontalAlignment.CENTER);
                    cellCalificacion.setValign(VerticalAlignment.MIDDLE);

// Celda: Firma
                    Cell<PDPage> cellFirma = row.createCell(9, " ");
                    cellFirma.setAlign(HorizontalAlignment.CENTER);
                    cellFirma.setValign(VerticalAlignment.MIDDLE);


                    yStart = yStart - row.getHeight();
                }
            }
            yStart = yStart - 40;//Ajuste 20
            table.draw();
            PDPageContentStream fin = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            fin.beginText();
            fin.setFont(normal, letra);
            fin.newLineAtOffset(inicio+25, yStart);
            fin.setCharacterSpacing(0);
            SimpleDateFormat form = new SimpleDateFormat("dd '-' MMMM '-' yyyy", new Locale("ES"));
            Date fechaDatee = new Date();
            String fec = form.format(fechaDatee);
            String p1 = "San Salvador de Jujuy, " + fec;
            String firma = "    ______________________                                  ________________________";
            //String firma1 = "               " + userService.getAuthenticatedUser().get().getUserApellido()+"                                                  Firma Alumno";
            String firma1 = "               Firma del Secretario                                                      Firma Alumno";

            String p2 = "El día del examen, el estudiante deberá presentar: libreta, permiso de examen y D.N.I.";
            String p6 = "------------------------------------------------------------------------";
            fin.showText(firma);
            fin.newLineAtOffset(0, n); // Mover cursor hacia abajo para la siguiente línea
            fin.showText(firma1);
            fin.newLineAtOffset(0, n); // Mover cursor hacia abajo para la siguiente línea
            fin.showText(p1);
            fin.setFont(normal, 8);
            fin.newLineAtOffset(0, n); // Mover cursor hacia abajo para la siguiente línea
            fin.showText(p2);
            fin.newLineAtOffset(0, -10); // Mover cursor hacia abajo para la siguiente línea
            fin.setCharacterSpacing(charspacing(longitud, tamaño(p6, letra, normal), p6));//espacio entre caracteres
            fin.showText(p6);
            fin.setFont(normal, letra);
            fin.setCharacterSpacing(0);
            String titulop = "                        Constancia de Solicitud de permiso de examen";
            String subtitulo = "                      _________________________________________";
            String p7 = "Permiso N°:" + permiso.get().getId() + "      Turno:" +turnoT.getTurnoMes()+"  Llamado: "+turnoT.getLlamado();
            String p8 = "Apellido y Nombre " + persona.getPersonaApellido() + " " + persona.getPersonaNombre() + ", DNI:" + dni;
            fin.newLineAtOffset(0, n); // Mover cursor hacia abajo para la siguiente línea
            fin.setFont(negrita, letra);
            fin.showText(titulop);
            fin.newLineAtOffset(0, -1); // Mover cursor hacia abajo para la siguiente línea
            fin.showText(subtitulo);
            fin.setFont(normal, letra);
            fin.newLineAtOffset(0, n-5); // Mover cursor hacia abajo para la siguiente línea
            fin.setCharacterSpacing(charspacing(longitud, tamaño(p7, letra, normal), p7));//espacio entre caracteres
            fin.showText(p7);
            fin.newLineAtOffset(0, n); // Mover cursor hacia abajo para la siguiente línea
            fin.setCharacterSpacing(charspacing(longitud, tamaño(p8, letra, normal), p8));//espacio entre caracteres
            fin.showText(p8);
            fin.setCharacterSpacing(0);
            yStart = yStart - 85;//AJUSTE
            float delta = 0;
            BaseTable table1 = new BaseTable(yStart, yStartNewPage, 0, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            Row<PDPage> headerRow1 = table1.createRow(20);
            Cell<PDPage> cell1 = headerRow1.createCell(5, "N°");
            cell1.setAlign(HorizontalAlignment.CENTER);
            cell1.setValign(VerticalAlignment.MIDDLE);
            cell1.setTextRotated(false);
            cell1 = headerRow1.createCell(8, "Condicion");
            cell1.setAlign(HorizontalAlignment.CENTER);
            cell1.setValign(VerticalAlignment.MIDDLE);
            cell1.setTextRotated(false);
            cell1 = headerRow1.createCell(27, "Unidad Curricular");
            cell1.setAlign(HorizontalAlignment.CENTER);
            cell1.setValign(VerticalAlignment.MIDDLE);

            cell1 = headerRow1.createCell(12, "Fecha Hora");
            cell1.setAlign(HorizontalAlignment.CENTER);
            cell1.setValign(VerticalAlignment.MIDDLE);
            delta = headerRow1.getHeight();
            indice = 0;
            for (int row1 = 0; row1 < inscripcionesActivas.size(); row1++) {
                Boolean isInscripto = (Boolean) inscripcionesActivas.get(row1).getInscripto();
                if (isInscripto != null && isInscripto) {
                    indice++;
                    String materia = (String) inscripcionesActivas.get(row1).getMateriaNombre();
                    String condicion = (String) inscripcionesActivas.get(row1).getCondicion();
                    String fecha = inscripcionesActivas.get(row1).getFecha();
                    if (fecha != null && !fecha.isEmpty()) {
                        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd"); // Asumiendo que la fecha viene en formato yyyy-MM-dd
                        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date fechaDate = formatoEntrada.parse(fecha);
                            fecha = formatoSalida.format(fechaDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    String hora =  inscripcionesActivas.get(row1).getHora();




                    if (!condicion.equals("Regular")) {
                        condicion = "Libre";
                    }
                    Row<PDPage> row = table1.createRow(20);

// Celda: Orden
                    Cell<PDPage> cellOrden = row.createCell(5, String.valueOf(indice));
                    cellOrden.setAlign(HorizontalAlignment.CENTER);
                    cellOrden.setValign(VerticalAlignment.MIDDLE);

// Celda: Condición
                    Cell<PDPage> cellCondicion = row.createCell(8, condicion);
                    cellCondicion.setAlign(HorizontalAlignment.CENTER);
                    cellCondicion.setValign(VerticalAlignment.MIDDLE);

// Celda: Unidad Curricular (Materia)
                    Cell<PDPage> cellMateria = row.createCell(27, materia);
                    cellMateria.setAlign(HorizontalAlignment.CENTER);
                    cellMateria.setValign(VerticalAlignment.MIDDLE);

// Celda: Fecha y Hora
                    Cell<PDPage> cellFechaHora = row.createCell(12, fecha + " - " + hora);
                    cellFechaHora.setAlign(HorizontalAlignment.CENTER);
                    cellFechaHora.setValign(VerticalAlignment.MIDDLE);

                    delta = delta + row.getHeight();
                }
            }
            System.out.println("Indice vale:" + indice);
            if (indice < 6) {
                int rowsToAdd = 6 - indice;

                System.out.println("filas a agregar v vale:" + rowsToAdd);
                for (int i = 0; i < rowsToAdd; i++) {
                    indice++;
                    Row<PDPage> row = table1.createRow(20);

// Celda: Orden
                    Cell<PDPage> cellOrden = row.createCell(5, String.valueOf(indice));
                    cellOrden.setAlign(HorizontalAlignment.CENTER);
                    cellOrden.setValign(VerticalAlignment.MIDDLE);

// Celda: Condición (vacía)
                    Cell<PDPage> cellCondicion = row.createCell(8, "");
                    cellCondicion.setAlign(HorizontalAlignment.CENTER);
                    cellCondicion.setValign(VerticalAlignment.MIDDLE);

// Celda: Unidad Curricular (vacía)
                    Cell<PDPage> cellMateria = row.createCell(27, "");
                    cellMateria.setAlign(HorizontalAlignment.CENTER);
                    cellMateria.setValign(VerticalAlignment.MIDDLE);

                    Cell<PDPage> cellFec = row.createCell(12, "");
                    cellFec.setAlign(HorizontalAlignment.CENTER);
                    cellFec.setValign(VerticalAlignment.MIDDLE);






                    delta = delta + row.getHeight();
                }
            }

            table1.draw();
            float res = delta - yStart;
            System.out.println("delta vale vale:" + delta);
            System.out.println("res yStart vale:" + yStart);
            System.out.println("res vale:" + res);
            fin.newLineAtOffset(0, -delta - 15);//-tam+80
            String j = "Usuario: " + userService.getAuthenticatedUser().get().getUserApellido()+ ", Recibo N°____, Fecha: " + fec + ", Firma Rendido________";
            fin.setCharacterSpacing(charspacing(longitud, tamaño(j, letra, normal), j));//espacio entre caracteres
            fin.showText(j);
            // fin.setCharacterSpacing(0);//espacio entre caracteres
            fin.endText();
            fin.close();
            System.out.println("se divujo la tabla");
            //  Documento.save(dir + ".pdf");
            //  Documento.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Documento;
    }

    @Autowired
    private EmailService emailService;

    @Override
    public void enviarPermisoPorEmail(String libreta, String turno, String usuarioNombre, String destinatario) {
        try {
            PDDocument documento = generaPermiso(libreta, turno, usuarioNombre);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            documento.save(baos);
            documento.close();
            byte[] pdfBytes = baos.toByteArray();

            Map<String, Object> variables = Map.of(
                    "nombre", usuarioNombre,
                    "turno", turno,
                    "libreta", libreta
            );

            emailService.enviarCorreoConAdjunto(
                    destinatario,
                    "Permiso de Examen IESI",
                    "permiso-template", // nombre del archivo HTML de la plantilla (por ejemplo: resources/templates/permiso-template.html)
                    variables,
                    pdfBytes,
                    "permiso_" + libreta + "_" + turno + ".pdf"
            );

            System.out.println("📤 Permiso enviado a " + destinatario);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al enviar permiso por email: " + e.getMessage());
        }
    }


}


