package com.example.iesiback.services;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import com.example.iesiback.entities.Persona;
import com.example.iesiback.entities.Carrera;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CertificadoRegular {

    private final PersonaService alumnoService;
    private final CarreraService carreraService;
    @Autowired
    public CertificadoRegular(@Lazy PersonaService alumnoService,
                              CarreraService carreraService) {
        this.alumnoService= alumnoService;
        this.carreraService= carreraService;
    }

    private PDDocument generaRegular(String dir) {
        Persona persona =this.alumnoService.findAlumnoById("--");//agregar el id
        Carrera carrera=this.carreraService.findCarreraById("---");//agregar el id
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
            PDesc2.drawImage(Iesc2, 15, 545, 40, 40);
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
            Long dni= persona.getPersonaDni();
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
            String t12 = "año.getText();";
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
            String t16 = "requiere.getText();";
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
            Documento.save(dir + ".pdf");
            //================

            //Documento.close();
        } catch (IOException e) {
        }
        return Documento;
    }


    /*
    private void rellenaraño() {
        apellido.setText(objcrud.consultaString("select alumno_apellido from alumno inner join legajo on alumno.alumno_dni=legajo.legajo_alumno_dni where legajo.legajo_id='" + libretaEstudiantil + "';", "alumno_apellido", false));
        nombre.setText(objcrud.consultaString("select alumno_nombre from alumno inner join legajo on alumno.alumno_dni=legajo.legajo_alumno_dni where legajo.legajo_id='" + libretaEstudiantil + "';", "alumno_nombre", false));
        carrera.setText(objcrud.consultaString("select carrera_nombre from carrera inner join inscripcion on carrera.carrera_id=inscripcion.carrera_id inner join legajo on inscripcion.legajo_id=legajo.legajo_id where legajo.legajo_id='" + libretaEstudiantil + "';", "carrera_nombre", false));
        Calendar cal = Calendar.getInstance();
        int fin = cal.get(Calendar.YEAR);
        int ini = Integer.valueOf(objcrud.consultaString("select carrera_year from carrera inner join inscripcion on carrera.carrera_id=inscripcion.carrera_id inner join legajo on inscripcion.legajo_id=legajo.legajo_id where legajo.legajo_id='" + libretaEstudiantil + "';", "carrera_year", true));
        System.out.println("ini" + ini + "fin" + fin);
        int yaño = fin - ini;
        System.out.println("yaño" + yaño);
        switch (yaño) {
            case 0:
                año.setText("1er año");
                break;
            case 1:
                año.setText("2do año");
                break;
            case 2:
                año.setText("3er año");
                break;
        }
    }
*/
    private static String rellenar(String Ta, String t13, int letra, float width) throws IOException {
        String todo = "";
        float size = letra * PDType1Font.HELVETICA.getStringWidth(Ta) / 1000;
        float free = width - size;
        //System.out.println(free);
        while (free > 100) {
            t13 = t13 + "-";
            Ta = Ta + "-";
            size = letra * PDType1Font.HELVETICA.getStringWidth(Ta) / 1000;
            free = width - size;
            System.out.println("tamañolinea" + size);
            System.out.println("tamañotodoscaracteres" + Ta.length());
            System.out.println(width + "anchopermitido");
            System.out.println(Ta + "texto");
            System.out.println(free + "espaciolibre");
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
                System.out.println("funciona");
                todo = todo + var;
                todo = todo + SubCadena + "@";
                contador = 1;
                System.out.println("todo___" + todo);
                var = "";
            }
            size = letra + 15 * PDType1Font.HELVETICA.getStringWidth(var) / 1000;
            free = width - size;
            pos = s.indexOf(" ", pos + 1); //se busca el siguiente espacio en blanco
        }
        System.out.println("todo" + todo);
        System.out.println("var" + var);
        System.out.println("subcadena");
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


}
