package com.example.iesiback.services;

import be.quodlibet.boxable.*;
import com.example.iesiback.dto.NotaExamenDTO;
import com.example.iesiback.dto.NotaMateriaDTO;
import com.example.iesiback.entities.*;
import com.example.iesiback.repositories.MateriaCarreraRepository;
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

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class CertificadoServiceImpl implements CertificadoService {


    private final AlumnoService alumnoService;
    private final CarreraService carreraService;
    private final NotaService notaService;
    private final MateriaCarreraRepository materiaCarreraRepository;
    private final LegajoService legajoSerevice;

    @Autowired
    public CertificadoServiceImpl(@Lazy AlumnoService alumnoService,
                              CarreraService carreraService,
                                  NotaService notaService,
                                  MateriaCarreraRepository materiaCarreraRepository,
                                  LegajoService legajoService) {
        this.alumnoService= alumnoService;
        this.carreraService= carreraService;
        this.notaService= notaService;
        this.materiaCarreraRepository = materiaCarreraRepository;
        this.legajoSerevice=legajoService;
    }

@Override
public PDDocument generaRegular(String dniId, String legajoId, String autoridades, String curso) {
        Alumno alumno=this.alumnoService.findAlumnoById(dniId);//agregar el id
        Carrera carrera=this.carreraService.obtenerCarreraPorLegajoId(legajoId);//agregar el id

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
            String genero = alumno.getAlumnoGenero();
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
            String nombre = alumno.getAlumnoNombre();
            String apellido = alumno.getAlumnoApellido();
            Long dni=alumno.getAlumnoDni();
            String t6 = (apellido + " " + nombre + " D.N.I: " + dni + " ");
            String t7 = (", es estudiante regular de la carrera:");
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, +tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7));//espacio entre caracteres
            regular.showText(t6);
            regular.newLineAtOffset(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7), 0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t7);
            regular.newLineAtOffset(-(tamaño(t6, letra, negrita) + t6.length() * charspacing(longitud, tamaño(t6, letra, negrita) + tamaño(t7, letra, normal), t6 + t7)), -20);//linea nueav
            String carreraCompl = "Tecnicatura Superior en " + carrera.getCarreraNombre() + " ";
            String t10 = ("  y actualmente se");
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
            Row< PDPage> headerRow = table.createRow(300);
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


    private static String fechaFija(LocalDate fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'San Salvador de jujuy ' EEEE dd 'de' MMMM 'del año' yyyy", new Locale("es", "ES"));
        LocalDate fechaActual = LocalDate.now();
        return fechaActual.format(formatter);
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


        @Override
        public PDDocument generaAnalitico(String legajoId, String accion, String autoridades) {
            Alumno alumno=this.alumnoService.obtenerAlumnoPorLegajoId(legajoId);
            Carrera carrera=this.carreraService.obtenerCarreraPorLegajoId(legajoId);

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

        System.out.println(listaMaterias.size()+"Tamaño 1");
        PDImageXObject Iesc1, Iesc2, casilla0, casilla1;
        PDDocument Documento = new PDDocument();
        try {
            String carrera_id =carrera.getCarreraId();
            String carreraNombre =carrera.getCarreraNombre();
            int nMaterias = 0;//cantidad de matirias
            int materiasPrimero = this.materiaCarreraRepository.countMateriasPorNivel(carrera_id,"1ro");
            int materiasSegundo = this.materiaCarreraRepository.countMateriasPorNivel(carrera_id,"2do");
            int materiasTercero = this.materiaCarreraRepository.countMateriasPorNivel(carrera_id,"3ro");

            int n = -10;//distancia entre lineas
            int letra = 11;//Tamaño de letras
            Long dni = alumno.getAlumnoDni();
            String nombre = alumno.getAlumnoNombre();
            String apellido = alumno.getAlumnoApellido();
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
            String genero = alumno.getAlumnoGenero();
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
            Row< PDPage> headerRow = table.createRow(20);
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
                    System.out.println("Materia Nota final" + mat.getNotaFinal()+", "+mat.getNotaId());
                    Cell<PDPage> cellNotaFinal = rew.createCell(25, mat.getNotaFinal());

                    cellNotaFinal.setFontSize(nk);
                    cellNotaFinal.setAlign(HorizontalAlignment.CENTER);
                    cellNotaFinal.setValign(VerticalAlignment.MIDDLE);
                    cellNotaFinal.setFont(PDType1Font.HELVETICA);
                    System.out.println("Aca esta el error de siempre"+mat.getNotaFecha());
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
                            double nota = Double.parseDouble(mat.getNotaCalificacionNumero());
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
            String pr="---------------------------------------------------Promedio: "+resultadoFormateado+"---";
            fin.setCharacterSpacing(charspacing(longitud, tamaño(pr, letra, normal),pr));//espacio entre caracteres
            fin.showText(pr);
            fin.newLineAtOffset(0,-15);//linea nueav
            String t14 = ("----Se extiende la presente constancia en la ciudad de ");
            String ciudad = "San Salvador de Jujuy";
            fin.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal) + tamaño(ciudad, letra, negrita), t14 + ciudad));//espacio entre caracteres
            fin.showText(t14);
            fin.setFont(negrita, letra);
            fin.showText(ciudad);
            fin.setFont(normal, letra);
            fin.newLineAtOffset(0, -15);//linea nueav
            String t15 = ("a los " + fecha()) + " " + "para ser presentado";
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
        List<NotaExamenDTO> listaResultados=this.notaService.obtenerNotasPorCondicion(Long.valueOf(cursadaExamen.getId()), true, modalidad);
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
            regular.showText("EXAMEN DE ALUMNO: " + modalidad + "   AÑO: " + materia.getMateriaNivel()+ "  DIVISON: " + division + "   TURNO: " + turno);

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
            Row< PDPage> headerRow = table.createRow(50);
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
            Row< PDPage> cabfilafecha = filafecha.createRow(15);
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
            Row< PDPage> cabecalfbol = calificaciones_bolillas.createRow(8);
            Cell<PDPage> cab = cabecalfbol.createCell(21.2f, "CALIFICACIONES");
            cab.setAlign(HorizontalAlignment.CENTER);
            cab.setValign(VerticalAlignment.MIDDLE);
            cab.setFontSize(8);
            cab = cabecalfbol.createCell(14, "Nº de las Bolillas");
            cab.setAlign(HorizontalAlignment.CENTER);
            cab.setValign(VerticalAlignment.MIDDLE);
            cab.setFontSize(8);

            BaseTable fila = new BaseTable(yStart - 26, yStartNewPage, bottomMargin, tableWidth, 355.6f + auxmargin, Documento, Pagina, true, drawContent);
            Row< PDPage> cabfila = fila.createRow(24);//25
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
                    dni = fResultado.getAlumnoDni();
                    apellido = fResultado.getAlumnoApellido()+", "+fResultado.getAlumnoNombre();
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
    public PDDocument generaTramite(String carreraId, String alumnoDNI, String Autoridades){
        PDImageXObject Iesc1,Iesc2;
        Carrera carreraObj=this.carreraService.findCarreraById(carreraId);
        Alumno alumno=this.alumnoService.findAlumnoById(alumnoDNI);
        PDDocument Documento=new PDDocument();
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



            PDPageContentStream regular=new PDPageContentStream(Documento,Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //texto de constancia
            n=-10;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 10);
            regular.newLineAtOffset(200,720);//titulo
            regular.showText("CONSTANCIA DE TITULO EN TRAMITE");
            regular.newLineAtOffset(0,0 );
            regular.showText("__________________________________");
            regular.endText();
            ///=============================================================
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4*margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas= new ArrayList<String>();
            int letra=12;
            String hola=" __________________";
            String texto=Dividir(hola,width,letra);
            lineas=procesar(texto,letra);
            //===================================================================
            // Justificar texto
            PDType1Font normal=PDType1Font.HELVETICA;
            PDType1Font negrita=PDType1Font.HELVETICA_BOLD;
            //consulta

            String genero=alumno.getAlumnoGenero();
            String resolucion=carreraObj.getCarreraResolucion();
            String genero2="";
            String genero1="";
            if (genero.equals("Masculino")){
                genero1="el SR";
                genero2="el interesado";
                System.out.println("Es Hombre");
            }else{
                genero1="la Sra" ;
                genero2="la interesada";
                System.out.println("Es Mujer");
            }
            regular.beginText();
            regular.setFont(normal, letra);
            regular.newLineAtOffset(50,680);
            String t1=("-----Por la presente, la Rectora del ");
            String t2=("Instituto de Educación Superior Intercultural CAMPINTA ");
            //regular.setCharacterSpacing(charspacing(longitud,t1+t2,letra));//espacio entre caracteres
            regular.showText(t1);
            regular.newLineAtOffset(tamaño(t1,letra, normal), 0);
            regular.setFont(negrita, letra);
            //String t2=("Instituto de Educación Superior Intercultural CAMPINTA");
            regular.showText(t2);
            float longitud=tamaño(t1+t2,letra, PDType1Font.HELVETICA)+20;//longitud permitida para justificar
            regular.newLineAtOffset(-tamaño(t1,letra, normal), -20);//nueva linea abajo justo al inicio
            String t3= ("GUAZU GLORIA PEREZ ");
            String t4=("Prof. Cristina Noemi Martínez, deja ");//CONSTANCIA que ");
            String t41=("CONSTANCIA ");
            String t5=("que "+genero1);
            regular.setCharacterSpacing(charspacing(longitud,tamaño(t4,letra,normal)+tamaño(t3,letra,negrita)+tamaño(t41,letra,negrita)+tamaño(t5,letra,normal),t3+t4+t5+t41));//espacio entre caracteres
            regular.showText(t3);
            regular.setFont(PDType1Font.HELVETICA, letra);
            regular.newLineAtOffset(tamaño(t3,letra,negrita)+t3.length()*charspacing(longitud,tamaño(t4,letra,normal)+tamaño(t3,letra,negrita)+tamaño(t41,letra,negrita)+tamaño(t5,letra,normal),t3+t4+t5+t41),0 );
            //String t4=(" Prof. Cristina Noemi Martínez, deja CONSTANCIA que el Sr");
            regular.showText(t4);
            regular.newLineAtOffset(tamaño(t4,letra, normal)+t4.length()*charspacing(longitud,tamaño(t4,letra,normal)+tamaño(t3,letra,negrita)+tamaño(t41,letra,negrita)+tamaño(t5,letra,normal),t3+t4+t5+t41),0 );
            regular.showText(t41);
            regular.newLineAtOffset(tamaño(t41,letra, normal)+t41.length()*charspacing(longitud,tamaño(t4,letra,normal)+tamaño(t3,letra,negrita)+tamaño(t41,letra,negrita)+tamaño(t5,letra,normal),t3+t4+t5+t41),0 );
            regular.showText(t5);
            regular.newLineAtOffset(-tamaño(t3,letra, negrita)-tamaño(t41,letra, negrita)-tamaño(t4,letra, normal)-t3.length()*charspacing(longitud,tamaño(t4,letra,normal)+tamaño(t3,letra,negrita)+tamaño(t41,letra,negrita)+tamaño(t5,letra,normal),t3+t4+t5+t41)-t4.length()*charspacing(longitud,tamaño(t4,letra,normal)+tamaño(t3,letra,negrita)+tamaño(t41,letra,negrita)+tamaño(t5,letra,normal),t3+t4+t5+t41)-t41.length()*charspacing(longitud,tamaño(t4,letra,normal)+tamaño(t3,letra,negrita)+tamaño(t41,letra,negrita)+tamaño(t5,letra,normal),t3+t4+t5+t41),-20);////charspacing(longitud, tamaño(t3, letra, normal),t3+t4)-tamaño(t4,letra, PDType1Font.HELVETICA)-t4.length()*charspacing(longitud, tamaño(t3, letra, normal),t3+t4+t5),-20 );
            String nombre=alumno.getAlumnoNombre();
            String apellido=alumno.getAlumnoApellido();
            String t6=(apellido+" "+nombre+" D.N.I: "+alumno.getAlumnoDni()+" ");
            String t7=(", ha acreditado la totalidad de los espacios ");
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, +tamaño(t6, letra, negrita)+tamaño(t7, letra, normal),t6+t7));//espacio entre caracteres
            regular.showText(t6);
            regular.newLineAtOffset(tamaño(t6,letra,negrita)+t6.length()*charspacing(longitud, tamaño(t6, letra, negrita)+tamaño(t7, letra, normal),t6+t7),0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t7);
            regular.newLineAtOffset(-(tamaño(t6,letra,negrita)+t6.length()*charspacing(longitud, tamaño(t6, letra, negrita)+tamaño(t7, letra, normal),t6+t7)),-20 );//linea nueav
            String t10=("curriculares corrrespondientes al Plan de Estudios de la Carrera: ");
            String tec="Tecnicatura Superior";
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t10, letra, normal)+tamaño(tec, letra, negrita),t10+tec));//espacio entre caracteres
            regular.showText(t10);
            regular.setFont(negrita, letra);
            regular.showText(tec);
            regular.newLineAtOffset(0,-20 );//linea nueav
            String carrera="en "+carreraObj.getCarreraNombre();
            String t11=(" Estando el respectivo titulo en tramite");
            String relleno=rellenar(carrera+t11,"",letra,longitud);
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(carrera, letra, negrita)+tamaño(t11, letra, normal)+tamaño(relleno, letra, normal),carrera+t11+relleno));//espacio entre caracteres
            regular.showText(carrera);
            regular.setFont(normal, letra);
            regular.showText(t11);
            regular.showText(relleno);
            regular.newLineAtOffset(0,-20 );

            String t14=("-----Se expide la presente CONSTANCIA a solicitud de "+genero2+"  y al solo efecto de ser");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal),t14));//espacio entre caracteres
            regular.setFont(normal, letra);
            regular.showText(t14);
            String t15=("presentada ante las Autoridades del ");
            String t16=Autoridades;
            System.out.println("autoridades "+t16);
            if(t16.equals("que lo requieran")){
                t15="presentada ante las Autoridades";
            }
            regular.setFont(normal, letra);
            t16=rellenar(t15+t16,t16,letra,longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t15, letra, normal)+tamaño(t16, letra, negrita),t15+t16));//espacio entre caracteres
            regular.newLineAtOffset(0,-20 );
            regular.setFont(normal, letra);
            regular.showText(t15);
            // String t16=("B.E.G.U.P");
            regular.newLineAtOffset(tamaño(t15,letra, normal)+t15.length()*charspacing(longitud, tamaño(t15, letra, normal)+tamaño(t16, letra, normal),t15+t16),0 );
            regular.setFont(normal, letra);
            regular.showText(t16);
            // String t17=("------------------SAN SALVADOR DE JUJUY, "+fecha());
            String t17=("-----SAN SALVADOR DE JUJUY, "+fecha()+"-------------");
            regular.newLineAtOffset(-(tamaño(t15,letra,normal)+(t15.length())*charspacing(longitud, tamaño(t16, letra, normal)+tamaño(t15, letra, normal),t15+t16)),-20 );//linea nueav
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t17, letra, normal),t17));//espacio entre caracteres
            regular.showText(t17);
            regular.newLineAtOffset(0,-20 );
            margin = 30;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (4 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = yStartNewPage+30;
            float bottomMargin = 70;
            float yPosition = 550;
            BaseTable table =  new  BaseTable (yStart, yStartNewPage, bottomMargin, tableWidth, margin, Documento, Pagina, true , drawContent);
            Row< PDPage > headerRow = table.createRow(300);
            Cell<PDPage>cell=headerRow.createCell(100, " " );
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



@Override
public PDDocument generaCertificadoAsistencia(String alumnoDNI, String legajoId, String autoridades, String curso, String entrada, String salida, String fecT, String accion) {
        PDImageXObject Iesc1,Iesc2;
        Carrera carreraObj=this.carreraService.obtenerCarreraPorLegajoId(legajoId);
        Alumno alumno=this.alumnoService.findAlumnoById(alumnoDNI);
        PDDocument Documento=new PDDocument();
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
            PDPageContentStream regular=new PDPageContentStream(Documento,Pagina, PDPageContentStream.AppendMode.APPEND, true);
            //texto de constancia
            n=-10;//distancia entre lineas
            regular.beginText();
            regular.setFont(PDType1Font.HELVETICA_BOLD, 10);
            regular.newLineAtOffset(210,720);//titulo
            regular.showText("CONSTANCIA DE ASISTENCIA A CLASES");
            regular.newLineAtOffset(0,0 );
            regular.showText("____________________________________");
            regular.endText();
            ///=============================================================
            PDRectangle mediabox = Pagina.getMediaBox();
            float margin = 20;
            float width = mediabox.getWidth() - 4*margin;
            float X = mediabox.getLowerLeftX() + margin;
            float Y = mediabox.getUpperRightY() - margin;
            List<String> lineas= new ArrayList<String>();
            int letra=12;
            String hola=" ____________________";
            String texto=Dividir(hola,width,letra);
            lineas=procesar(texto,letra);
            //===================================================================
//Justificar texto
            PDType1Font normal=PDType1Font.HELVETICA;
            PDType1Font negrita=PDType1Font.HELVETICA_BOLD;

            //consulta
            String genero=alumno.getAlumnoGenero();
            String genero2="";
            String genero1="";
            if (genero.equals("Masculino")){
                genero1="el SR";
                genero2="el interesado";
                System.out.println("Es Hombre");
            }else{
                genero1="la Sra" ;
                genero2="la interesada";
                System.out.println("Es Mujer");
            }
            regular.beginText();
            regular.setFont(normal, letra);
            regular.newLineAtOffset(50,680);
            String t1=("-----Por la presente, la Rectora del ");
            String t2=("Instituto de Educación Superior Intercultural CAMPINTA ");
            //regular.setCharacterSpacing(charspacing(longitud,t1+t2,letra));//espacio entre caracteres
            regular.showText(t1);
            regular.newLineAtOffset(tamaño(t1,letra, normal), 0);
            regular.setFont(negrita, letra);
            //String t2=("Instituto de Educación Superior Intercultural CAMPINTA");
            regular.showText(t2);
            float longitud=tamaño(t1+t2,letra, PDType1Font.HELVETICA)+20;//longitud permitida para justificar
            regular.newLineAtOffset(-tamaño(t1,letra, normal), -20);//nueva linea abajo justo al inicio
            String t3= ("GUAZU GLORIA PEREZ ");
            String t4=("Prof. Cristina Noemi Martínez, deja ");//CONSTANCIA que ");
            String t41=("CONSTANCIA ");
            String t5=("que "+genero1);
            regular.setCharacterSpacing(charspacing(longitud,tamaño(t4,letra,normal)+tamaño(t3,letra,negrita)+tamaño(t41,letra,negrita)+tamaño(t5,letra,normal),t3+t4+t5+t41));//espacio entre caracteres
            regular.showText(t3);
            regular.setFont(PDType1Font.HELVETICA, letra);
            regular.newLineAtOffset(tamaño(t3,letra,negrita)+t3.length()*charspacing(longitud,tamaño(t4,letra,normal)+tamaño(t3,letra,negrita)+tamaño(t41,letra,negrita)+tamaño(t5,letra,normal),t3+t4+t5+t41),0 );
            //String t4=(" Prof. Cristina Noemi Martínez, deja CONSTANCIA que el Sr");
            regular.showText(t4);

            regular.newLineAtOffset(tamaño(t4,letra, normal)+t4.length()*charspacing(longitud,tamaño(t4,letra,normal)+tamaño(t3,letra,negrita)+tamaño(t41,letra,negrita)+tamaño(t5,letra,normal),t3+t4+t5+t41),0 );
            regular.showText(t41);
            regular.newLineAtOffset(tamaño(t41,letra, normal)+t41.length()*charspacing(longitud,tamaño(t4,letra,normal)+tamaño(t3,letra,negrita)+tamaño(t41,letra,negrita)+tamaño(t5,letra,normal),t3+t4+t5+t41),0 );
            regular.showText(t5);
            regular.newLineAtOffset(-tamaño(t3,letra, negrita)-tamaño(t41,letra, negrita)-tamaño(t4,letra, normal)-t3.length()*charspacing(longitud,tamaño(t4,letra,normal)+tamaño(t3,letra,negrita)+tamaño(t41,letra,negrita)+tamaño(t5,letra,normal),t3+t4+t5+t41)-t4.length()*charspacing(longitud,tamaño(t4,letra,normal)+tamaño(t3,letra,negrita)+tamaño(t41,letra,negrita)+tamaño(t5,letra,normal),t3+t4+t5+t41)-t41.length()*charspacing(longitud,tamaño(t4,letra,normal)+tamaño(t3,letra,negrita)+tamaño(t41,letra,negrita)+tamaño(t5,letra,normal),t3+t4+t5+t41),-20);////charspacing(longitud, tamaño(t3, letra, normal),t3+t4)-tamaño(t4,letra, PDType1Font.HELVETICA)-t4.length()*charspacing(longitud, tamaño(t3, letra, normal),t3+t4+t5),-20 );

            String nombre=alumno.getAlumnoNombre();
            String apellido=alumno.getAlumnoApellido();

            String t6=(apellido+" "+nombre+" D.N.I: "+alumno.getAlumnoDni()+" ");

            String t7=(", estudiante del "+curso+" de la carrera:");
            regular.setFont(negrita, letra);
            regular.setCharacterSpacing(charspacing(longitud, +tamaño(t6, letra, negrita)+tamaño(t7, letra, normal),t6+t7));//espacio entre caracteres
            regular.showText(t6);
            regular.newLineAtOffset(tamaño(t6,letra,negrita)+t6.length()*charspacing(longitud, tamaño(t6, letra, negrita)+tamaño(t7, letra, normal),t6+t7),0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t7);
            regular.newLineAtOffset(-(tamaño(t6,letra,negrita)+t6.length()*charspacing(longitud, tamaño(t6, letra, negrita)+tamaño(t7, letra, normal),t6+t7)),-20 );//linea nueav

            String carrera="Tecnicatura Superior en "+carreraObj.getCarreraNombre();
            String t10=("");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(carrera, letra, negrita)+tamaño(t10, letra, normal),carrera+t10));//espacio entre caracteres
            regular.setFont(negrita, letra);
            regular.showText(carrera);
            regular.newLineAtOffset(tamaño(carrera,letra,negrita)+carrera.length()*charspacing(longitud, tamaño(carrera, letra, negrita)+tamaño(t10, letra, normal),carrera+t10),0);//linea alejada de la primera palabra
            regular.setFont(normal, letra);
            regular.showText(t10);
            regular.newLineAtOffset(-(tamaño(carrera,letra,negrita)+(carrera.length())*charspacing(longitud,tamaño(carrera, letra, negrita)+tamaño(t10, letra, normal),carrera+t10)),-20);//linea nueav
            String t12="";
            String tcond = fecT;
            int contadorComas = tcond.length() - tcond.replace(",", "").length();
            String losdias="";
            if(contadorComas>0){
                losdias=" a clases los dias: ";
            }else{

                losdias=" a clases el dia: ";
            }


            String t11=accion+losdias+fecT;
            String t13=(", en el horario de :"+entrada+"-"+salida);
            t13= rellenar(t11+t12+t13,t13,letra,longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t11, letra, normal)+tamaño(t12, letra, normal)+tamaño(t13, letra, normal),t11+t12+t13));//espacio entre caracteres
            regular.showText(t11);
            regular.newLineAtOffset(tamaño(t11,letra, normal)+t11.length()*charspacing(longitud,tamaño(t11, letra, normal)+tamaño(t12, letra, normal)+ tamaño(t13, letra, normal),t11+t12+t13),0);
            regular.showText(t12);
            System.out.println(t13+"//////////////////");
//String t13=("Año de este Instituto");
            regular.newLineAtOffset(tamaño(t12,letra, normal)+t12.length()*charspacing(longitud, tamaño(t11, letra, normal)+tamaño(t12, letra, normal)+tamaño(t13, letra, normal),t11+t12+t13),0);
            regular.showText(t13);
            String t14=("-----Se expide la presente CONSTANCIA a solicitud de "+genero2+"  y al solo efecto de ser");
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t14, letra, normal),t14));//espacio entre caracteres
            regular.newLineAtOffset(-(tamaño(t11,letra, normal)+(t11.length())*charspacing(longitud, tamaño(t11, letra, normal)+tamaño(t12, letra, normal)+tamaño(t13, letra, normal),t11+t12+t13))-(tamaño(t12,letra, PDType1Font.HELVETICA)+(t12.length())*charspacing(longitud, tamaño(t11, letra, normal)+tamaño(t12, letra, normal)+tamaño(t13, letra, normal),t11+t12+t13)),-20 );//linea nueav

            regular.showText(t14);
            String t15=("presentada ante las Autoridades del ");
            String t16=autoridades;
            System.out.println("autoridades "+t16);
            if(t16.equals("que lo requieran")){
                t15="presentada ante las Autoridades";
            }

            t16=rellenar(t15+t16,t16,letra,longitud);
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t15, letra, normal)+tamaño(t16, letra, negrita),t15+t16));//espacio entre caracteres
            regular.newLineAtOffset(0,-20 );
            regular.setFont(normal, letra);
            regular.showText(t15);
            // String t16=("B.E.G.U.P");
            regular.newLineAtOffset(tamaño(t15,letra, normal)+t15.length()*charspacing(longitud, tamaño(t15, letra, normal)+tamaño(t16, letra, normal),t15+t16),0 );
            regular.setFont(normal, letra);
            regular.showText(t16);
            //String t17=("------------------SAN SALVADOR DE JUJUY, "+fecha());
            String t17=("-----SAN SALVADOR DE JUJUY, "+fecha()+"-------------");
            regular.newLineAtOffset(-(tamaño(t15,letra,normal)+(t15.length())*charspacing(longitud, tamaño(t16, letra, normal)+tamaño(t15, letra, normal),t15+t16)),-20 );//linea nueav
            regular.setCharacterSpacing(charspacing(longitud, tamaño(t17, letra, normal),t17));//espacio entre caracteres
            regular.showText(t17);
            regular.newLineAtOffset(0,-20 );
            margin = 30;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = Pagina.getMediaBox().getHeight() - (4 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
            boolean drawContent = true;
            float yStart = yStartNewPage+30;
            float bottomMargin = 70;
            float yPosition = 550;
            BaseTable table =  new  BaseTable (yStart, yStartNewPage, bottomMargin, tableWidth, margin, Documento, Pagina, true , drawContent);
            Row< PDPage > headerRow = table.createRow(300);
            Cell<PDPage>cell=headerRow.createCell(100, " " );
            //cell.setText("hoal a atodsssss");
            table.draw();
            regular.endText();
            regular.close();
            //Documento.close();
        } catch (IOException e) {
        }
        return Documento;
    }


@Override
public PDDocument generaFichaActualizacion(String legajoId) throws IOException{
        Alumno alumno=this.alumnoService.obtenerAlumnoPorLegajoId(legajoId);
        Legajo legajo=this.legajoSerevice.findLegajoById(legajoId);
        Carrera carrera=this.carreraService.obtenerCarreraPorLegajoId(legajoId);


        PDDocument Documento=new PDDocument();
        PDPage Pagina= new PDPage(PDRectangle.A4);
        Documento.addPage(Pagina);
        PDImageXObject Iesc1,Iesc2;
        PDPageContentStream encabezado=new PDPageContentStream(Documento,Pagina);
        PDType1Font font=PDType1Font.HELVETICA;
        PDType1Font fontN=PDType1Font.HELVETICA_BOLD;
        float margin = 30;//
        PDRectangle mediabox = Pagina.getMediaBox();
        float width = mediabox.getWidth() - 4*margin;
        float X = mediabox.getLowerLeftX() + margin;
        float Y = mediabox.getUpperRightY() - margin;
        List<String> lineas= new ArrayList<String>();
        float yStartNewPage = Pagina.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin ofcourse)
        float tableWidth = Pagina.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float yStart = 820;//yStartNewPage;
        float bottomMargin = 40;
// y position is your coordinate of top left corner of the table
        float yPosition = 100;
        encabezado.beginText();
        int n=-9;
        encabezado.newLineAtOffset(180,820);
        encabezado.setFont(font, 10);
        encabezado.showText("INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL");
        encabezado.newLineAtOffset(45,n);
        encabezado.showText("“CAMPINTA GUAZU GLORIA PEREZ”");
        encabezado.newLineAtOffset(0,n);
        encabezado.setFont(font,7);
        encabezado.showText("Del Consejo de Organizaciones Aborígenes de Jujuy");
        encabezado.newLineAtOffset(-6,n);
        encabezado.showText("Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15");
        encabezado.newLineAtOffset(-15,n);
        encabezado.showText("Bahia Blanca Nº 235 Bº .Kennedy – Tel. Fax. N° (0388)-4237323");
        encabezado.newLineAtOffset(-45,n);
        encabezado.showText("(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy – Kollasuyu- República Argentina");
        encabezado.newLineAtOffset(-55,0);
        encabezado.showText("________________________________________________________________________________________________");
        encabezado.newLineAtOffset(140, n-5);//100
        encabezado.setFont(fontN, 10);
        encabezado.showText("Actualizacion de legajo");
        encabezado.newLineAtOffset(0, -1);
        encabezado.showText("____________________");
        encabezado.setFont(font,8);
        encabezado.newLineAtOffset(-200, n-5);//(xx,yy)//180
        // Obtener la fecha actual
        LocalDate fechaActual = LocalDate.now();
// Formatear la fecha según tus necesidades
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fechaFormateada = fechaActual.format(formatoFecha);

// Mostrar la fecha en el encabezado
        encabezado.showText("Hasta la Fecha: " + fechaFormateada+", se registra la siguiente situcion en el legajo de el/la estudiante:");
        encabezado.endText();
        encabezado.close();
    InputStream iesc1I = getClass().getClassLoader().getResourceAsStream("static/imagenes/esc2.png");
        if (iesc1I==null){
            System.out.println("readFilesInBytes: File " + "file" + " does not exist");
        }
        byte[]ba =IOUtils.toByteArray(iesc1I);
        Iesc1 = PDImageXObject.createFromByteArray(Documento, ba, "esc1.png");//divujar desde el path

        //imagen del encavezado izquierda
        PDPageContentStream PDesc1 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
        PDesc1.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
        PDesc1.drawImage( Iesc1, 50, 770, 60, 60);//Draw an image at the x,y coordinates, with the given size.
        PDesc1.close();
        PDPageContentStream cuerpo=new PDPageContentStream(Documento,Pagina, PDPageContentStream.AppendMode.APPEND, true);
        cuerpo.beginText();
        cuerpo.setFont(font, 10);
        n=-15;
        //================DATOS PERSONALES
        cuerpo.newLineAtOffset(40, 730);//XX,YY
        cuerpo.showText("Apellido y Nombre: "+alumno.getAlumnoApellido()+", "+alumno.getAlumnoNombre());
        cuerpo.newLineAtOffset(300,0);
        cuerpo.showText("Libreta Estudiantil: "+legajoId);
        cuerpo.newLineAtOffset(-300,n );
        //cuerpo.newLineAtOffset(0,n);
        cuerpo.showText("DNI: "+alumno.getAlumnoDni());
        cuerpo.newLineAtOffset(20, -24);
        cuerpo.showText("1.-Fotocopia dni");
        cuerpo.newLineAtOffset(300, 0);
        cuerpo.showText("5.-planilla prontuarial actualizada.");
        cuerpo.newLineAtOffset(-300, n);
        cuerpo.showText("2.- Certificado de Nacimiento actualizado.");
        cuerpo.newLineAtOffset(300, 0);
        cuerpo.showText("6.– carnet sanitario actualizado.");
        cuerpo.newLineAtOffset(-300, n);
        cuerpo.showText("3.– Fotocopia del título autenticada y actualizado.");
        cuerpo.newLineAtOffset(300, 0);
        cuerpo.showText("7.- Libreta.");
        cuerpo.newLineAtOffset(-300, n);
        cuerpo.showText("4.- Constancia de título en trámite actualizada.");
        cuerpo.newLineAtOffset(300, 0);
        cuerpo.showText("8. – foto 4 x 4.");

        int matriz[][]=new int[4][2];
        if(legajo.getLegajoFotocopiaDni().equals("Si")){
            matriz[0][0]=1;
        }else{
            matriz[0][0]=0;
        }
        if(legajo.getLegajoPlanillaProntuarial().equals("Si")){
            matriz[0][1]=1;
        }else{
            matriz[0][1]=0;
        }
        if(legajo.getLegajoCertificadoNacimiento().equals("Si")){
            matriz[1][0]=1;
        }else{
            matriz[1][0]=0;
        }
        if(legajo.getLegajoCarnetSanitario().equals("Si")){
            matriz[1][1]=1;
        }else{
            matriz[1][1]=0;
        }
        String indice=legajo.getLegajoFotocopiaTitulo();
        matriz[2][0]=0;
        matriz[3][0]=0;
        if(indice.equals("Secundario")){
            matriz[2][0]=1;
        }else if(indice.equals("Constacia Titulo Tramite")){
            matriz[3][0]=1;
        }

        if(legajo.getLegajoAval().equals("Si")){
            matriz[2][1]=1;
        }else{
            matriz[2][1]=0;
        }
        if(legajo.getLegajoFoto().equals("Si")){
            matriz[3][1]=1;
        }else{
            matriz[3][1]=0;
        }
        PDImageXObject casilla0,casilla1;
        System.out.println("SE va a dibujar los cuadritos xd");
        InputStream cas0=CertificadoServiceImpl.class.getClassLoader().getResourceAsStream("imagenes/casilla0.png");
        if (cas0==null){
            System.out.println("readFilesInBytes: File " + "file"+" does not exist");
        }
        InputStream cas1=CertificadoServiceImpl.class.getClassLoader().getResourceAsStream("imagenes/casilla1.png");
        if (cas1==null){
            System.out.println("readFilesInBytes: File " + "file"+ " does not exist");
        }
        byte[]bo =IOUtils.toByteArray(cas0);
        casilla0 = PDImageXObject.createFromByteArray(Documento, bo, "casilla0.png");//divujar desde el path
        byte[]bu =IOUtils.toByteArray(cas1);
        casilla1 = PDImageXObject.createFromByteArray(Documento, bu, "casilla1.png");//divujar desde el path
        System.out.println("SE va a dibujar los cuadritos xd y ahora s eva a recorrer el vector");

        int yy=690;
        for(int i=0;i<4;i++){
            System.out.println("funciana el primer for i:"+i);
            for(int j=0;j<2;j++){
                System.out.println("funciana el segundo for"+j);
                System.out.println(matriz[i][j]);
                if(j==0){//izquierda

                    if(matriz[i][j]==0){//si es nulo
                        PDPageContentStream PDesc11 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
                        PDesc11.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
                        PDesc11.drawImage( casilla0, 45, yy, 10, 10);//Draw an image at the x,y coordinates, with the given size.
                        PDesc11.close();
                        System.out.println("Se divuja 1");

                    }else{//si es afirmativo

                        PDPageContentStream PDesc11 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
                        PDesc11.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
                        PDesc11.drawImage( casilla1, 45, yy, 10, 10);//Draw an image at the x,y coordinates, with the given size.
                        PDesc11.close();
                        System.out.println("Se divuja 2");
                    }

                }else{//derecha
                    if(matriz[i][j]==0){//si es nulo
                        PDPageContentStream PDesc11 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
                        PDesc11.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
                        PDesc11.drawImage( casilla0, 345, yy, 10, 10);//Draw an image at the x,y coordinates, with the given size.
                        PDesc11.close();
                        System.out.println("Se divuja 3");

                    }else{//si es afirmativo

                        PDPageContentStream PDesc11 = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
                        PDesc11.moveTo(200, 100); //image.drawImage(img, 55, 0);//Draw an image at the x,y coordinates, with the default size of the image.
                        PDesc11.drawImage( casilla1, 345, yy, 10, 10);//Draw an image at the x,y coordinates, with the given size.
                        PDesc11.close();
                        System.out.println("Se divuja 4");
                    }
                }
            }
            yy=yy-15;
        }
        cuerpo.newLineAtOffset(-300, -40);
        cuerpo.showText("__________________________________"); ////================LUGAR Y FECHA DE NACIEMIENTO
        cuerpo.newLineAtOffset(300, 0);
        cuerpo.showText("________________________________");
        cuerpo.newLineAtOffset(-280, -10);
        cuerpo.showText("Firma y Aclaración del secretario.");
        cuerpo.newLineAtOffset(300, 0);
        cuerpo.showText("Firma y Aclaración del Alumno.");
        /*
        cuerpo.newLineAtOffset(-320, -20);
        if(observaciones.isEmpty()){
            cuerpo.showText("Observaciones: No registra------------------------------------------------"); ////================LUGAR Y FECHA DE NACIEMIENTO
        }else{
            cuerpo.showText("Observaciones: "+observaciones); ////================LUGAR Y FECHA DE NACIEMIENTO
        }


*/
        cuerpo.endText();
        cuerpo.close();
        System.out.println("se divujo la tabla");
        // Documento.save(dir+".pdf");//VEr el tema del directorio
        //Documento.close();
        return Documento;

    }

}