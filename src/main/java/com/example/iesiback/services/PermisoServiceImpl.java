package com.example.iesiback.services;

import be.quodlibet.boxable.*;
import com.example.iesiback.dto.InscripcionExamenDTO;
import com.example.iesiback.entities.Alumno;
import com.example.iesiback.entities.Legajo;
import com.example.iesiback.entities.Permiso;
import com.example.iesiback.repositories.PermisoRepository;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class PermisoServiceImpl implements PermisoService {

    @Autowired
    private PermisoRepository permisoRepository;
    private final ExamenService examenService;
    @Autowired
    private AlumnoService alumnoService;
    @Autowired
    private LegajoService legajoService;
    @Autowired
    private UserService userService;

    @Autowired
    public PermisoServiceImpl(@Lazy ExamenService examenService) {
        this.examenService = examenService;
    }

    public Permiso obtenerOCrearPermiso(String legajoId, String turnoId) {
        Optional<Permiso> permiso = permisoRepository.findPermisoByLegajoAndTurnoOrdered(legajoId, turnoId);
        return permiso.orElseGet(() -> {
            Permiso nuevoPermiso = new Permiso();
            nuevoPermiso.setPermisoLegajoId(legajoId);
            nuevoPermiso.setPermisoFecha(LocalDate.now());
            nuevoPermiso.setPermisoObs("Generado automáticamente");
            return permisoRepository.save(nuevoPermiso);
        });
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

            Alumno alumno = legajo.getLegajoAlumnoDni();

            String carrera = permisoRepository.obtenerCarreraPorLibreta(libreta);
            Optional<Permiso> permiso = permisoRepository.findPermisoByLegajoAndTurnoOrdered(libreta, turno);
            int dni = permisoRepository.obtenerDniPorLibreta(libreta);
            String nombre = permisoRepository.obtenerNombrePorDni(dni);
            String apellido = permisoRepository.obtenerApellidoPorDni(dni);
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
            encabezado.newLineAtOffset(inicio+105, 580);//105
            encabezado.showText("INSTITUTO DE EDUCACION SUPERIOR INTERCULTURAL");
            encabezado.newLineAtOffset(40, n);
            encabezado.showText("“CAMPINTA GUAZU GLORIA PEREZ”");
            encabezado.newLineAtOffset(-25, n);
            encabezado.showText("Incorporado a la Enseñanza Oficial-Resol. Nº 2936-E-15");
            encabezado.newLineAtOffset(-15, n);
            encabezado.showText("Bahia Blanca Nº 235 Bº .Kennedy – Tel. Fax. N°(0388)-3428370");
            encabezado.newLineAtOffset(-40, n);
            encabezado.showText("(C.P. 4600) – SAN SALVADOR DE JUJUY – Prov. De Jujuy - República Argentina");
            encabezado.newLineAtOffset(-20, 0);
            encabezado.showText("____________________________________________________________________________________");
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
            titulo.showText("Permiso de examen");
            titulo.newLineAtOffset(0, 0);
            titulo.showText("_________________");
            titulo.endText();
            titulo.close();
            PDPageContentStream pTexto = new PDPageContentStream(Documento, Pagina, PDPageContentStream.AppendMode.APPEND, true);
            String genero1 = "";
            String genero = alumno.getAlumnoGenero();
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
            String t1 = "Permiso N°:" + permiso.get().getId() + "                                              Turno: " + turno;
            String t2 = "Conste que por la presente " + genero1 + " estudiante: " + alumno.getAlumnoApellido() + " " + alumno.getAlumnoNombre();
            String t3 = "DNI: " + dni + " Esta habilitado para rendir las siguientes Unidades Curriculares";
            String t4 = "correspondientes a la carrera: " + carrera_nombre;
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
            BaseTable table = new BaseTable(yStart, yStartNewPage, 0, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
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
                    row.createCell(5, String.valueOf(indice)); // Orden
                    row.createCell(7, condicion); // Condicion
                    row.createCell(18, materia); // Unidad Curricular
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
                    row.createCell(8, fecha); // Fecha
                    row.createCell(5, hora); // hora
                    row.createCell(9, " "); // Firma
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
                    row.createCell(5, String.valueOf(indice)); // Orden
                    row.createCell(7, "");  //Condicion
                    row.createCell(18, ""); //Unidad Curricular
                    row.createCell(8, " "); //Fecha
                    row.createCell(5, " "); //Calificacion
                    row.createCell(9, " "); //Firma
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
            String p1 = "San salvador de jujuy " + fec;
            String firma = "    ______________________                                  ________________________";
            //String firma1 = "               " + userService.getAuthenticatedUser().get().getUserApellido()+"                                                  Firma Alumno";
            String firma1 = "               Firma del Secretario                                                      Firma Alumno";

            String p2 = "El dia del examen el estudiante debera presentar: Libreta - Permiso de examen - D.N.I";
            String p6 = "----------------------------------------------------------";
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
            String subtitulo = "                     _________________________________________";
            String p7 = "Permiso N°:" + permiso.get().getId() + "      Turno:" +turno + "-" + carrera_nombre;
            String p8 = "Apellido y Nombre " + alumno.getAlumnoApellido() + " " + alumno.getAlumnoNombre() + ", DNI:" + dni;
            fin.newLineAtOffset(0, n); // Mover cursor hacia abajo para la siguiente línea
            fin.setFont(negrita, letra);
            fin.showText(titulop);
            fin.newLineAtOffset(0, -1); // Mover cursor hacia abajo para la siguiente línea
            fin.showText(subtitulo);
            fin.setFont(normal, letra);
            fin.newLineAtOffset(0, n); // Mover cursor hacia abajo para la siguiente línea
            fin.setCharacterSpacing(charspacing(longitud, tamaño(p7, letra, normal), p7));//espacio entre caracteres
            fin.showText(p7);
            fin.newLineAtOffset(0, n); // Mover cursor hacia abajo para la siguiente línea
            fin.setCharacterSpacing(charspacing(longitud, tamaño(p8, letra, normal), p8));//espacio entre caracteres
            fin.showText(p8);
            fin.setCharacterSpacing(0);
            yStart = yStart - 80;//AJUSTE
            float delta = 0;
            BaseTable table1 = new BaseTable(yStart, yStartNewPage, 0, tableWidth, auxmargin, Documento, Pagina, true, drawContent);
            Row<PDPage> headerRow1 = table1.createRow(20);
            Cell<PDPage> cell1 = headerRow1.createCell(5, "N°");
            cell1.setAlign(HorizontalAlignment.CENTER);
            cell1.setValign(VerticalAlignment.MIDDLE);
            cell1.setTextRotated(false);
            cell1 = headerRow1.createCell(10, "Condicion");
            cell1.setAlign(HorizontalAlignment.CENTER);
            cell1.setValign(VerticalAlignment.MIDDLE);
            cell1.setTextRotated(false);
            cell1 = headerRow1.createCell(37, "Unidad Curricular");
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
                    if (!condicion.equals("Regular")) {
                        condicion = "Libre";
                    }
                    Row<PDPage> row = table1.createRow(20);
                    row.createCell(5, String.valueOf(indice)); // Orden
                    row.createCell(10, condicion); // Condicion
                    row.createCell(37, materia); // Unidad Curricular
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
                    row.createCell(5, String.valueOf(indice)); // Orden
                    row.createCell(10, ""); // Condicion
                    row.createCell(37, ""); // Unidad Curricular
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
