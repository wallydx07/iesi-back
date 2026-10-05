package com.example.iesiback.utils;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Arma las constancias de texto del IESI con un formato único:
 * encabezado con logo, recuadro, título subrayado, párrafos justificados
 * (el espacio se reparte entre palabras, nunca entre letras), fecha de
 * emisión a la derecha y espacio libre para firma y sello.
 *
 * Uso:
 * <pre>
 *   PDDocument doc = new ConstanciaPdf("CONSTANCIA DE SALIDA A CAMPO")
 *       .parrafo(ConstanciaPdf.texto().normal("Por la presente, ").negrita("..."))
 *       .parrafo(...)
 *       .construir();
 * </pre>
 */
public class ConstanciaPdf {

    // ---- Datos fijos de la institución -------------------------------------------------
    public static final String INSTITUTO =
            "Instituto de Educación Superior Intercultural “Campinta Guazu Gloria Perez”";
    public static final String RECTORA = "Prof. Cristina Noemí Martínez";
    public static final String CIUDAD = "San Salvador de Jujuy";
    private static final String LOGO = "static/imagenes/esc2.png";
    private static final String[] ENCABEZADO = {
            "INSTITUTO DE EDUCACIÓN SUPERIOR INTERCULTURAL",
            "“CAMPINTA GUAZU GLORIA PEREZ”",
            "Incorporado a la Enseñanza Oficial – Resol. Nº 2936-E-15",
            "Bahía Blanca Nº 235, Bº Kennedy – Tel. (0388) 6256119",
            "(C.P. 4600) San Salvador de Jujuy – Provincia de Jujuy – República Argentina"
    };

    // ---- Medidas (puntos PDF; 1 cm = 28.35 pt) -----------------------------------------
    private static final float CM = 28.3465f;
    private static final float MARGEN_LAT = 2f * CM;
    private static final float MARGEN_SUP = 1.5f * CM;
    private static final float LOGO_LADO = 2.2f * CM;
    private static final float COL_LOGO = 2.6f * CM;
    private static final float RELLENO_CAJA = 22f;
    private static final float TAM_TITULO = 13f;
    private static final float TAM_TEXTO = 11.5f;
    private static final float INTERLINEA = 20f;
    private static final float SANGRIA = 1.25f * CM;
    private static final float ENTRE_PARRAFOS = 10f;
    private static final float ESPACIO_FIRMA = 3f * CM;

    private static final PDType1Font NORMAL = PDType1Font.HELVETICA;
    private static final PDType1Font NEGRITA = PDType1Font.HELVETICA_BOLD;
    private static final PDType1Font CURSIVA = PDType1Font.HELVETICA_OBLIQUE;
    private static final PDType1Font NEGRITA_CURSIVA = PDType1Font.HELVETICA_BOLD_OBLIQUE;

    private static final Locale ES = new Locale("es", "AR");
    private static final ZoneId ZONA = ZoneId.of("America/Argentina/Jujuy");

    private final String titulo;
    private final List<Texto> parrafos = new ArrayList<>();
    private LocalDate fechaEmision = LocalDate.now(ZONA);

    public ConstanciaPdf(String titulo) {
        this.titulo = titulo;
    }

    public ConstanciaPdf parrafo(Texto texto) {
        parrafos.add(texto);
        return this;
    }

    /** Por defecto es la fecha de hoy. */
    public ConstanciaPdf fechaEmision(LocalDate fecha) {
        this.fechaEmision = fecha;
        return this;
    }

    // ---- Utilidades de formato para usar desde el service ------------------------------

    public static Texto texto() {
        return new Texto();
    }

    /** "lunes 28 de septiembre de 2026" */
    public static String fechaLarga(LocalDate fecha) {
        return fecha.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", ES));
    }

    /** 21818081 → "21.818.081" */
    public static String dni(Object dni) {
        String d = String.valueOf(dni).replaceAll("\\D", "");
        StringBuilder sb = new StringBuilder(d);
        for (int i = sb.length() - 3; i > 0; i -= 3) sb.insert(i, '.');
        return sb.toString();
    }

    /** "FLORES, EULOGIA AMANDA" */
    public static String nombreCompleto(String apellido, String nombre) {
        return (apellido.trim() + ", " + nombre.trim()).toUpperCase(ES);
    }

    // ---- Construcción del PDF --------------------------------------------------------------

    public PDDocument construir() throws IOException {
        PDDocument doc = new PDDocument();
        try {
            PDPage pagina = new PDPage(PDRectangle.A4);
            doc.addPage(pagina);
            float anchoPag = pagina.getMediaBox().getWidth();
            float altoPag = pagina.getMediaBox().getHeight();

            try (PDPageContentStream cs = new PDPageContentStream(doc, pagina)) {
                float y = dibujarEncabezado(doc, cs, anchoPag, altoPag - MARGEN_SUP);

                float cajaX = MARGEN_LAT;
                float cajaAncho = anchoPag - 2 * MARGEN_LAT;
                float cajaArriba = y - 1f * CM;
                float x0 = cajaX + RELLENO_CAJA;
                float anchoUtil = cajaAncho - 2 * RELLENO_CAJA;

                // Título
                y = cajaArriba - RELLENO_CAJA - TAM_TITULO;
                String tit = limpiar(titulo, NEGRITA);
                float wTit = ancho(tit, NEGRITA, TAM_TITULO);
                float xTit = x0 + (anchoUtil - wTit) / 2;
                escribir(cs, tit, NEGRITA, TAM_TITULO, xTit, y);
                cs.setLineWidth(0.8f);
                cs.moveTo(xTit, y - 2.2f);
                cs.lineTo(xTit + wTit, y - 2.2f);
                cs.stroke();
                y -= 0.8f * CM + 8f;

                // Párrafos
                for (Texto p : parrafos) {
                    y = dibujarParrafo(cs, p, x0, y, anchoUtil);
                    y -= ENTRE_PARRAFOS;
                }

                // Fecha de emisión a la derecha
                y -= 0.4f * CM;
                String fecha = CIUDAD + ", " + fechaLarga(fechaEmision) + ".";
                escribir(cs, fecha, NORMAL, TAM_TEXTO, x0 + anchoUtil - ancho(fecha, NORMAL, TAM_TEXTO), y);

                // Espacio para firma y sello, y recuadro
                float cajaAbajo = y - ESPACIO_FIRMA - RELLENO_CAJA;
                cs.setLineWidth(0.9f);
                cs.addRect(cajaX, cajaAbajo, cajaAncho, cajaArriba - cajaAbajo);
                cs.stroke();
            }
            return doc;
        } catch (IOException | RuntimeException e) {
            doc.close();
            throw e;
        }
    }

    /** Devuelve la Y donde termina el encabezado (la línea horizontal). */
    private float dibujarEncabezado(PDDocument doc, PDPageContentStream cs, float anchoPag, float arriba)
            throws IOException {
        float altoFila = LOGO_LADO;
        PDImageXObject logo = cargarLogo(doc);
        if (logo != null) {
            cs.drawImage(logo, MARGEN_LAT, arriba - altoFila, LOGO_LADO, LOGO_LADO);
        }

        float[] tam = {9.5f, 9.5f, 8.5f, 8.5f, 8.5f};
        float[] interl = {12f, 12f, 11f, 11f, 11f};
        float altoTexto = 0;
        for (float l : interl) altoTexto += l;

        float colTextoX = MARGEN_LAT + COL_LOGO;
        float colTextoAncho = anchoPag - 2 * MARGEN_LAT - 2 * COL_LOGO;
        float y = arriba - (altoFila - altoTexto) / 2;
        for (int i = 0; i < ENCABEZADO.length; i++) {
            PDType1Font f = i < 2 ? NEGRITA : NORMAL;
            y -= interl[i];
            String linea = limpiar(ENCABEZADO[i], f);
            float w = ancho(linea, f, tam[i]);
            escribir(cs, linea, f, tam[i], colTextoX + (colTextoAncho - w) / 2, y + 2.5f);
        }

        float yLinea = arriba - altoFila - 8f;
        cs.setLineWidth(0.8f);
        cs.moveTo(MARGEN_LAT, yLinea);
        cs.lineTo(anchoPag - MARGEN_LAT, yLinea);
        cs.stroke();
        return yLinea;
    }

    private PDImageXObject cargarLogo(PDDocument doc) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(LOGO)) {
            if (in == null) return null;
            return PDImageXObject.createFromByteArray(doc, IOUtils.toByteArray(in), "logo.png");
        } catch (IOException e) {
            return null; // sin logo antes que sin constancia
        }
    }

    /**
     * Corta el texto por palabras y justifica repartiendo el espacio sobrante entre
     * palabras. La última línea del párrafo queda alineada a la izquierda.
     */
    private float dibujarParrafo(PDPageContentStream cs, Texto texto, float x0, float yInicio, float anchoUtil)
            throws IOException {
        List<Palabra> palabras = texto.palabras();
        float espacio = ancho(" ", NORMAL, TAM_TEXTO);
        float y = yInicio;
        int i = 0;
        boolean primera = true;

        while (i < palabras.size()) {
            float sangria = primera ? SANGRIA : 0;
            float disponible = anchoUtil - sangria;

            List<Palabra> linea = new ArrayList<>();
            float anchoPalabras = 0;
            while (i < palabras.size()) {
                Palabra p = palabras.get(i);
                float necesario = anchoPalabras + p.ancho + (linea.isEmpty() ? 0 : espacio * linea.size());
                if (!linea.isEmpty() && necesario > disponible) break;
                if (linea.isEmpty() && p.ancho > disponible) {
                    // Palabra más ancha que la línea: se parte por caracteres.
                    Palabra[] partes = p.partir(disponible);
                    palabras.set(i, partes[1]);
                    linea.add(partes[0]);
                    anchoPalabras += partes[0].ancho;
                    break;
                }
                linea.add(p);
                anchoPalabras += p.ancho;
                i++;
            }

            boolean ultima = i >= palabras.size();
            int huecos = linea.size() - 1;
            float hueco = (ultima || huecos == 0) ? espacio : (disponible - anchoPalabras) / huecos;

            float x = x0 + sangria;
            for (Palabra p : linea) {
                for (Fragmento f : p.fragmentos) {
                    escribir(cs, f.texto, f.fuente, TAM_TEXTO, x, y);
                    x += ancho(f.texto, f.fuente, TAM_TEXTO);
                }
                x += hueco;
            }
            y -= INTERLINEA;
            primera = false;
        }
        return y;
    }

    private static void escribir(PDPageContentStream cs, String s, PDType1Font f, float tam, float x, float y)
            throws IOException {
        cs.beginText();
        cs.setFont(f, tam);
        cs.newLineAtOffset(x, y);
        cs.showText(s);
        cs.endText();
    }

    private static float ancho(String s, PDType1Font f, float tam) throws IOException {
        return f.getStringWidth(s) / 1000f * tam;
    }

    /** Quita los caracteres que la fuente no puede dibujar (evita IllegalArgumentException). */
    static String limpiar(String s, PDType1Font f) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ) {
            int cp = s.codePointAt(i);
            String c = new String(Character.toChars(cp));
            i += Character.charCount(cp);
            if (Character.isWhitespace(cp)) { sb.append(' '); continue; }
            try {
                f.encode(c);
                sb.append(c);
            } catch (IllegalArgumentException | IOException e) {
                // carácter no soportado: se omite
            }
        }
        return sb.toString();
    }

    // ---- Texto con estilos ------------------------------------------------------------------

    /** Texto con tramos en normal, negrita y cursiva. */
    public static class Texto {
        private final List<Fragmento> tramos = new ArrayList<>();

        public Texto normal(String s) { return agregar(s, NORMAL); }
        public Texto negrita(String s) { return agregar(s, NEGRITA); }
        public Texto cursiva(String s) { return agregar(s, CURSIVA); }
        public Texto negritaCursiva(String s) { return agregar(s, NEGRITA_CURSIVA); }

        private Texto agregar(String s, PDType1Font f) {
            if (s != null && !s.isEmpty()) tramos.add(new Fragmento(limpiar(s, f), f));
            return this;
        }

        /** Separa en palabras; una palabra puede mezclar estilos (ej.: negrita + coma). */
        List<Palabra> palabras() throws IOException {
            List<Palabra> res = new ArrayList<>();
            List<Fragmento> actual = new ArrayList<>();
            for (Fragmento t : tramos) {
                StringBuilder buf = new StringBuilder();
                for (char c : t.texto.toCharArray()) {
                    if (c == ' ') {
                        if (buf.length() > 0) { actual.add(new Fragmento(buf.toString(), t.fuente)); buf.setLength(0); }
                        if (!actual.isEmpty()) { res.add(new Palabra(actual)); actual = new ArrayList<>(); }
                    } else {
                        buf.append(c);
                    }
                }
                if (buf.length() > 0) actual.add(new Fragmento(buf.toString(), t.fuente));
            }
            if (!actual.isEmpty()) res.add(new Palabra(actual));
            return res;
        }
    }

    private static final class Fragmento {
        final String texto;
        final PDType1Font fuente;
        Fragmento(String texto, PDType1Font fuente) { this.texto = texto; this.fuente = fuente; }
    }

    private static final class Palabra {
        final List<Fragmento> fragmentos;
        final float ancho;

        Palabra(List<Fragmento> fragmentos) throws IOException {
            this.fragmentos = fragmentos;
            float w = 0;
            for (Fragmento f : fragmentos) w += ancho(f.texto, f.fuente, TAM_TEXTO);
            this.ancho = w;
        }

        /** Corta la palabra en el último carácter que entra en {@code max}. */
        Palabra[] partir(float max) throws IOException {
            List<Fragmento> izq = new ArrayList<>(), der = new ArrayList<>();
            float w = 0;
            boolean cortado = false;
            for (Fragmento f : fragmentos) {
                if (cortado) { der.add(f); continue; }
                int k = 0;
                while (k < f.texto.length()
                        && w + ancho(f.texto.substring(0, k + 1), f.fuente, TAM_TEXTO) <= max) k++;
                float wf = ancho(f.texto.substring(0, k), f.fuente, TAM_TEXTO);
                if (k < f.texto.length()) {
                    if (k > 0) izq.add(new Fragmento(f.texto.substring(0, k), f.fuente));
                    der.add(new Fragmento(f.texto.substring(k), f.fuente));
                    cortado = true;
                } else {
                    izq.add(f);
                }
                w += wf;
            }
            if (izq.isEmpty()) { // ni un carácter entra: forzamos uno para no entrar en bucle
                Fragmento f = der.remove(0);
                izq.add(new Fragmento(f.texto.substring(0, 1), f.fuente));
                if (f.texto.length() > 1) der.add(0, new Fragmento(f.texto.substring(1), f.fuente));
            }
            return new Palabra[]{new Palabra(izq), new Palabra(der)};
        }
    }
}