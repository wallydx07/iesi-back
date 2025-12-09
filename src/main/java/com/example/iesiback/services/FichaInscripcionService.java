package com.example.iesiback.services;

import be.quodlibet.boxable.*;
import com.example.iesiback.entities.Persona;
import com.example.iesiback.entities.Aporte;
import com.example.iesiback.entities.Carrera;
import com.example.iesiback.entities.Legajo;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class FichaInscripcionService {

    Persona persona;
    Legajo legajo;
    Aporte aporte;
    Carrera carrera;

    public byte[] generarFichaInscripcion(Persona persona, Legajo legajo, Aporte aporte, Carrera carrera) throws IOException {
        String talonario=String.valueOf(aporte.getId());
        String monto=String.valueOf(aporte.getAporteMonto());
        PDDocument Documento=new PDDocument();
        PDPage Pagina= new PDPage(PDRectangle.A4);
        Documento.addPage(Pagina);
        PDPageContentStream encabezado=new PDPageContentStream(Documento,Pagina);
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("fuente/RobotoL.ttf");
        PDFont font = PDType0Font.load(Documento, inputStream);
        inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("fuente/RobotoB.ttf");
        PDFont fontN = PDType0Font.load(Documento, inputStream);
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
        encabezado.newLineAtOffset(125, 800);
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
        encabezado.newLineAtOffset(100, n-5);
        encabezado.setFont(fontN, 10);
        encabezado.showText("Solicitud de inscripcion");
        encabezado.newLineAtOffset(0, -1);
        encabezado.showText("________________________");
        encabezado.setFont(font,8);
        encabezado.newLineAtOffset(-160, n-5);//(xx,yy)
        encabezado.showText("Fecha de inscripcion: "+legajo.getLegajoFecha());
        encabezado.endText();
        encabezado.close();
        BaseTable table =  new  BaseTable (yStart, yStartNewPage, bottomMargin, tableWidth, margin, Documento, Pagina, true ,drawContent);
        Row< PDPage > headerRow = table.createRow(100);
        Cell<PDPage> cell=headerRow.createCell(80,"");
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        // cell.setTextRotated(true);
        cell=headerRow.createCell(20,"Foto 4*4" );
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        // cell.setTextRotated(true);
        BaseTable Cursoaño0 =  new  BaseTable (yStart-headerRow.getHeight()+1, yStartNewPage,bottomMargin, tableWidth, margin, Documento, Pagina, true ,drawContent);
        int fz=7;
        Row<PDPage> raw0 = Cursoaño0.createRow(20);
        cell.setFont(font);
        cell = raw0.createCell(100, "DATOS ACADEMICOS");//año
        cell.setFillColor(Color.LIGHT_GRAY);
        cell.setFont(fontN);
        cell.setFontSize(fz);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw1 = Cursoaño0.createRow(20);
        cell = raw1.createCell(100,"Tecnicatura Superior en "+carrera.getCarreraNombre());
        cell.setAlign(HorizontalAlignment.LEFT);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw2 = Cursoaño0.createRow(20);
        cell = raw2.createCell(100, "DATOS PERSONALES");//año
        cell.setFillColor(Color.lightGray);
        cell.setFont(fontN);
        cell.setFontSize(fz);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw3 = Cursoaño0.createRow(40);
        cell = raw3.createCell(100, "x");//año
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw4 = Cursoaño0.createRow(20);
        cell = raw4.createCell(100, "LUGAR Y FECHA DE NACIMIENTO");//año
        cell.setFillColor(Color.lightGray);
        cell.setFont(fontN);
        cell.setFontSize(fz);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw5 = Cursoaño0.createRow(40);
        cell = raw5.createCell(100, "x");//año
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw6 = Cursoaño0.createRow(20);
        cell = raw6.createCell(100, "DOMICILIO");//año
        cell.setFillColor(Color.lightGray);
        cell.setFont(fontN);
        cell.setFontSize(fz);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw7= Cursoaño0.createRow(50);
        cell = raw7.createCell(100, "x");//año
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);

        Row<PDPage> raw8 = Cursoaño0.createRow(20);
        cell = raw8.createCell(100, "DATOS DE LA COMUNIDAD DE PERTENENCIA");//año
        cell.setFillColor(Color.lightGray);
        cell.setFont(fontN);
        cell.setFontSize(fz);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw9= Cursoaño0.createRow(40);
        cell = raw9.createCell(100, "x");//año
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw10 = Cursoaño0.createRow(20);
        cell = raw10.createCell(100,  "ESTUDIOS SECUNDARIOS");//año
        cell.setFillColor(Color.lightGray);
        cell.setFont(fontN);
        cell.setFontSize(fz);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw11 = Cursoaño0.createRow(50);
        cell = raw11.createCell(100, "x");//año
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw12 = Cursoaño0.createRow(20);
        cell = raw12.createCell(100,  "CONSIDERACIONES ESPECIALES");//año
        cell.setFillColor(Color.lightGray);
        cell.setFont(fontN);
        cell.setFontSize(fz);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw13= Cursoaño0.createRow(70);
        cell = raw13.createCell(100, "x");//año
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw14 = Cursoaño0.createRow(20);
        cell = raw14.createCell(100, "DOCUMENTACION PRESENTADA");//año
        cell.setFillColor(Color.lightGray);
        cell.setFont(fontN);
        cell.setFontSize(fz);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw15 = Cursoaño0.createRow(80);   //70
        cell = raw15.createCell(100, "x");//año
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw16 = Cursoaño0.createRow(20);
        cell = raw16.createCell(100, "Firma");//año
        cell.setFillColor(Color.lightGray);
        cell.setFont(fontN);
        cell.setFontSize(fz);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw17 = Cursoaño0.createRow(70);
        cell = raw17.createCell(100, "x");//año
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        Row<PDPage> raw18 = Cursoaño0.createRow(30);
        cell = raw18.createCell(100, "OBSERVACIONES:…………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………");//año
        table.draw();
        Cursoaño0.draw();
        PDPageContentStream cuerpo=new PDPageContentStream(Documento,Pagina, PDPageContentStream.AppendMode.APPEND, true);
        cuerpo.beginText();
        cuerpo.setFont(font, 10);
        n=-15;
        //================DATOS PERSONALES
        cuerpo.newLineAtOffset(40, 645);//XX,YY
        cuerpo.showText("Apellido y Nombre: "+ persona.getPersonaApellido()+" "+ persona.getPersonaNombre());
        cuerpo.newLineAtOffset(300,0);
        cuerpo.showText("Libreta Estudiantil: "+legajo.getLegajoId());
        cuerpo.newLineAtOffset(-300,n );
        //cuerpo.newLineAtOffset(0,n);
        cuerpo.showText("DNI: "+ persona.getPersonaDni());
        ////================LUGAR Y FECHA DE NACIEMIENTO
        cuerpo.newLineAtOffset(0,-45 );
        cuerpo.showText("Localidad: "+ persona.getPersonaLocalidadNacimiento());
        cuerpo.newLineAtOffset(300,0);
        cuerpo.showText("Provincia: "+ persona.getPersonaProvinciaNacimiento());
        cuerpo.newLineAtOffset(-300,n );
        cuerpo.showText("Pais: "+ persona.getPersonaPaisNacimiento());
        cuerpo.newLineAtOffset(300,0 );
        cuerpo.showText("Fecha: "+ persona.getPersonaFechaNacimiento());
        //==============================LDomicilio
        cuerpo.newLineAtOffset(-300, -42);
        cuerpo.showText("Calle: "+ persona.getPersonaDomicilioCalle());
        cuerpo.newLineAtOffset(200,0);
        cuerpo.showText("N° "+ persona.getPersonaDomicilioNro());
        cuerpo.newLineAtOffset(100, -0);
        cuerpo.showText("Barrio: "+ persona.getPersonaDomicilioBarrio());
        cuerpo.newLineAtOffset(-300,n);
        cuerpo.showText("Localidad: "+ persona.getPersonaDomicilioLocalidad());
        cuerpo.newLineAtOffset(300,0);
        cuerpo.showText("E-mail: "+ persona.getPersonaCorreo());
        cuerpo.newLineAtOffset(-300, n);
        cuerpo.showText("Telefono fijo: "+ persona.getPersonaDomicilioTelefono());
        cuerpo.newLineAtOffset(300, 0);
        cuerpo.showText("Celular: "+ persona.getPersonaDomicilioCelular());
        ////================DATOS DE LA COMUNIDAD DE PERTENENCIA

        cuerpo.newLineAtOffset(-300, -40);
        cuerpo.showText("Comunidad: "+ persona.getPersonaComunidadNombre());
        cuerpo.newLineAtOffset(300, 0);
        //        cuerpo.showText("N° de Personería Jurídica: "+ alumno.getComunidad_alumno_personeria());
        //         cuerpo.newLineAtOffset(-300, n);
        cuerpo.showText("Pueblo: "+ persona.getPersonaComunidadPueblo());
        cuerpo.newLineAtOffset(-300,n);
        cuerpo.showText("Departamento: "+ persona.getPersonaComunidadDepartamento());
        //cuerpo.newLineAtOffset(-300, n);
        // cuerpo.showText("Autoridad: "+alumno.getComunidad_alumno_autoridad());
        // cuerpo.newLineAtOffset(0, n);
        //   cuerpo.showText("Domicilio legal de la comunidad: "+alumno.getComunidad_alumno_domicilio());
        ////================LESTUDIOS SECUNDARIOS
        cuerpo.newLineAtOffset(0, -45);
        //cuerpo.setFont(Ma6, 20);
        cuerpo.showText("Estudios Secundario/Polimodal: "+ persona.getPersonaSecundarioCompleto());
        cuerpo.newLineAtOffset(300,0);
        cuerpo.showText("año de egreso:  "+ persona.getPersonaSecundarioFecha());
        cuerpo.newLineAtOffset(-300, n);
        cuerpo.showText("Instituto del cual egreso: "+ persona.getPersonaEscuela());
        cuerpo.newLineAtOffset(0, n);
        cuerpo.showText("Titulo Obtenido: "+ persona.getPersonaTitulo());

        ////================MAYORES DE 25 AÑOS – Resol. Prov. N°
        cuerpo.newLineAtOffset(0, -43);
        cuerpo.showText("¿Requiere algún tipo de Acompañamiento específico en el aprendizaje?");
        cuerpo.newLineAtOffset(0, 2*n);
        cuerpo.showText("¿Enfrentas alguna limitación o desafío adicional que debamos conocer?");
        ////================DOCUMENTACION PRESENTADA
        cuerpo.newLineAtOffset(20, -60);
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
        cuerpo.showText("7.- carpeta colgante.");
        cuerpo.newLineAtOffset(-300, n);
        cuerpo.showText("4.- Constancia de título en trámite actualizada.");
        cuerpo.newLineAtOffset(300, 0);
        cuerpo.showText("8. – foto 4 x 4.");
        cuerpo.newLineAtOffset(-300, n);
        cuerpo.showText("ID pago: "+talonario+" Monto: "+monto);






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


        matriz[2][1] = "Si".equals(legajo.getLibreta()) ? 1 : 0;
        matriz[3][1] = "Si".equals(legajo.getLegajoFoto()) ? 1 : 0;








        //===============PONER CASILLAS

        PDImageXObject casilla0,casilla1;
        System.out.println("SE va a dibujar los cuadritos xd");
        InputStream cas0=FichaInscripcionService.class.getClassLoader().getResourceAsStream("imagenes/casilla0.png");
        if (cas0==null){
            System.out.println("readFilesInBytes: File " + "file"+" does not exist");
        }
        InputStream cas1=FichaInscripcionService.class.getClassLoader().getResourceAsStream("imagenes/casilla1.png");
        if (cas1==null){
            System.out.println("readFilesInBytes: File " + "file"+ " does not exist");
        }
        byte[]bo =IOUtils.toByteArray(cas0);
        casilla0 = PDImageXObject.createFromByteArray(Documento, bo, "casilla0.png");//divujar desde el path
        byte[]bu =IOUtils.toByteArray(cas1);
        casilla1 = PDImageXObject.createFromByteArray(Documento, bu, "casilla1.png");//divujar desde el path
        System.out.println("SE va a dibujar los cuadritos xd y ahora s eva a recorrer el vector");

        int yy=235;
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
////===================================================FIRMA==========================
        cuerpo.newLineAtOffset(300, -70);
        cuerpo.showText("__________________________________"); ////================LUGAR Y FECHA DE NACIEMIENTO
        cuerpo.newLineAtOffset(-300, 0);
        cuerpo.showText("________________________________");
        cuerpo.newLineAtOffset(300, -10);
        cuerpo.showText("Firma y Aclaración del secretario.");
        cuerpo.newLineAtOffset(-300, 0);
        cuerpo.showText("Firma y Aclaración del Alumno.");
        cuerpo.endText();
        cuerpo.close();
        System.out.println("se divujo la tabla");
        // Guardar el PDF en memoria
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Documento.save(out);
        Documento.close();

        return out.toByteArray();
    }

}