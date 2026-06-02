package com.example.iesiback.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.IOException;

public final class PdfFonts {

    private PdfFonts() {}

    public static PDType0Font normal(PDDocument doc) throws IOException {
        return PDType0Font.load(
                doc,
                PdfFonts.class.getResourceAsStream("/fuente/NotoSans-Regular.ttf")
        );
    }

    public static PDType0Font bold(PDDocument doc) throws IOException {
        return PDType0Font.load(
                doc,
                PdfFonts.class.getResourceAsStream("/fuente/NotoSans-Bold.ttf")
        );
    }

    public static PDType0Font symbols(PDDocument doc) throws IOException {
        return PDType0Font.load(
                doc,
                PdfFonts.class.getResourceAsStream("/fuente/NotoSansSymbols-Light.ttf")
        );
    }
}