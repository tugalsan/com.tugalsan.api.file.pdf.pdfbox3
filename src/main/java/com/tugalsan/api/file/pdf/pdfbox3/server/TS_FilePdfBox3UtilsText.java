package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.list.client.TGS_ListUtils;
import org.apache.pdfbox.pdmodel.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.util.Matrix;

public class TS_FilePdfBox3UtilsText {

    final private static TS_Log d = TS_Log.of(TS_FilePdfBox3UtilsText.class);

    public static void helloWorldType1(String message, String file, String pfbPath) {
        TGS_UnSafe.run(() -> {
            try (var doc = new PDDocument()) {
                var page = new PDPage();
                doc.addPage(page);

                PDFont font;
                try (InputStream is = new FileInputStream(pfbPath)) {
                    font = new PDType1Font(doc, is);
                }

                try (var contents = new PDPageContentStream(doc, page)) {
                    contents.beginText();
                    contents.setFont(font, 12);
                    contents.newLineAtOffset(100, 700);
                    contents.showText(message);
                    contents.endText();
                }

                doc.save(file);
                System.out.println(file + " created!");
            }
        });
    }

    public static void helloWorldTTF(String message, String pdfPath, String ttfPath) {
        TGS_UnSafe.run(() -> {
            try (var doc = new PDDocument()) {
                var page = new PDPage();
                doc.addPage(page);

                var font = PDType0Font.load(doc, new File(ttfPath));

                try (var contents = new PDPageContentStream(doc, page)) {
                    contents.beginText();
                    contents.setFont(font, 12);
                    contents.newLineAtOffset(100, 700);
                    contents.showText(message);
                    contents.endText();
                }

                doc.save(pdfPath);
                System.out.println(pdfPath + " created!");
            }
        });
    }

    public static void embeddedFonts(String file) {
        TGS_UnSafe.run(() -> {
            try (var document = new PDDocument()) {
                var page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                var dir = "../pdfbox/src/main/resources/org/apache/pdfbox/resources/ttf/";
                var font = PDType0Font.load(document, new File(dir + "LiberationSans-Regular.ttf"));

                try (var stream = new PDPageContentStream(document, page)) {
                    stream.beginText();
                    stream.setFont(font, 12);
                    stream.setLeading(12 * 1.2f);

                    stream.newLineAtOffset(50, 600);
                    stream.showText("PDFBox's Unicode with Embedded TrueType Font");
                    stream.newLine();

                    stream.showText("Supports full Unicode text â˜º");
                    stream.newLine();

                    stream.showText("English Ñ€ÑƒÑÑĞºĞ¸Ğ¹ ÑĞ·Ñ‹Ğº Tiáº¿ng Viá»‡t");
                    stream.newLine();

                    // ligature
                    stream.showText("Ligatures: \uFB01lm \uFB02ood");

                    stream.endText();
                }

                document.save("example.pdf");
            }
        });
    }

    public static void showTextWithPositioning(String message, String outfile, float fontSize_20) {
        TGS_UnSafe.run(() -> {
            // the document
            try (var doc = new PDDocument(); InputStream is = PDDocument.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf")) {
                // Page 1
                PDFont font = PDType0Font.load(doc, is, true);
                var page = new PDPage(PDRectangle.A4);
                doc.addPage(page);

                // Get the non-justified string width in text space units.
                var stringWidth = font.getStringWidth(message) * fontSize_20;

                // Get the string height in text space units.
                var stringHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() * fontSize_20;

                // Get the width we have to justify in.
                var pageSize = page.getMediaBox();

                try (var contentStream = new PDPageContentStream(doc,
                        page, AppendMode.OVERWRITE, false)) {
                    contentStream.beginText();
                    contentStream.setFont(font, fontSize_20);

                    // Start at top of page.
                    contentStream.setTextMatrix(
                            Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f));

                    // First show non-justified.
                    contentStream.showText(message);

                    // Move to next line.
                    contentStream.setTextMatrix(
                            Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 2));

                    // Now show word justified.
                    // The space we have to make up, in text space units.
                    var justifyWidth = pageSize.getWidth() * 1000f - stringWidth;

                    var text = TGS_ListUtils.of();
                    var parts = message.split("\\s");

                    var spaceWidth = (justifyWidth / (parts.length - 1)) / fontSize_20;

                    for (var i = 0; i < parts.length; i++) {
                        if (i != 0) {
                            text.add(" ");
                            // Positive values move to the left, negative to the right.
                            text.add(-spaceWidth);
                        }
                        text.add(parts[i]);
                    }
                    contentStream.showTextWithPositioning(text.toArray());
                    contentStream.setTextMatrix(Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 3));

                    // Now show letter justified.
                    text = TGS_ListUtils.of();
                    justifyWidth = pageSize.getWidth() * 1000f - stringWidth;
                    var extraLetterWidth = (justifyWidth / (message.codePointCount(0, message.length()) - 1)) / fontSize_20;

                    for (var i = 0; i < message.length(); i += Character.charCount(message.codePointAt(i))) {
                        if (i != 0) {
                            text.add(-extraLetterWidth);
                        }

                        text.add(String.valueOf(Character.toChars(message.codePointAt(i))));
                    }
                    contentStream.showTextWithPositioning(text.toArray());

                    // PDF specification about word spacing:
                    // "Word spacing shall be applied to every occurrence of the single-byte character 
                    // code 32 in a string when using a simple font or a composite font that defines 
                    // code 32 as a single-byte code. It shall not apply to occurrences of the byte 
                    // value 32 in multiple-byte codes.
                    // TrueType font with no word spacing
                    contentStream.setTextMatrix(
                            Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 4));
                    font = PDTrueTypeFont.load(doc, PDDocument.class.getResourceAsStream(
                            "/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"), WinAnsiEncoding.INSTANCE);
                    contentStream.setFont(font, fontSize_20);
                    contentStream.showText(message);

                    var wordSpacing = (pageSize.getWidth() * 1000f - stringWidth) / (parts.length - 1) / 1000;

                    // TrueType font with word spacing
                    contentStream.setTextMatrix(
                            Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 5));
                    font = PDTrueTypeFont.load(doc, PDDocument.class.getResourceAsStream(
                            "/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"), WinAnsiEncoding.INSTANCE);
                    contentStream.setFont(font, fontSize_20);
                    contentStream.setWordSpacing(wordSpacing);
                    contentStream.showText(message);

                    // Type0 font with word spacing that has no effect
                    contentStream.setTextMatrix(
                            Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 6));
                    font = PDType0Font.load(doc, PDDocument.class.getResourceAsStream(
                            "/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"));
                    contentStream.setFont(font, fontSize_20);
                    contentStream.setWordSpacing(wordSpacing);
                    contentStream.showText(message);

                    // Finish up.
                    contentStream.endText();
                }

                doc.save(outfile);
            }
        });
    }

    public static TGS_UnionExcuseVoid createPageText(Path path, String text) {
        return TGS_UnSafe.call(() -> {
            try (var document = new PDDocument();) {
//                var acroform = document.getDocumentCatalog().getAcroForm();
//                if (acroform != null) {
//                    acroform.setNeedAppearances(false);
//                    acroform.refreshAppearances();
//                }
                var page = new PDPage();
                document.addPage(page);
                try (var contentStream = new PDPageContentStream(document, page);) {
                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                    contentStream.newLineAtOffset(100, 700);
                    contentStream.showText(text);
                    contentStream.endText();
                }
                document.save(path.toFile());
                return TGS_UnionExcuseVoid.ofVoid();
            }
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

    public static TGS_UnionExcuseVoid embeddedFonts(Path ttf, Path pdfDst) {
        return TGS_UnSafe.call(() -> {
            try (var document = new PDDocument()) {
                var page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                var font = PDType0Font.load(document, ttf.toFile());
                try (var stream = new PDPageContentStream(document, page)) {
                    stream.beginText();
                    stream.setFont(font, 12);
                    stream.setLeading(12 * 1.2f);

                    stream.newLineAtOffset(50, 600);
                    stream.showText("PDFBox's Unicode with Embedded TrueType Font");
                    stream.newLine();

                    stream.showText("Supports full Unicode text ☺");
                    stream.newLine();

                    stream.showText("English русский язык Tiếng Việt");
                    stream.newLine();

                    // ligature
                    stream.showText("Ligatures: \uFB01lm \uFB02ood");

                    stream.endText();
                }

                document.save(pdfDst.toFile());
                return TGS_UnionExcuseVoid.ofVoid();
            }
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

    public void usingTextMatrix(String message, String outfile) throws IOException {
        // the document
        try (PDDocument doc = new PDDocument()) {
            // Page 1
            PDFont font = new PDType1Font(FontName.HELVETICA);
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            float fontSize = 12.0f;

            PDRectangle pageSize = page.getMediaBox();
            float centeredXPosition = (pageSize.getWidth() - fontSize / 1000f) / 2f;
            float stringWidth = font.getStringWidth(message);
            float centeredYPosition = (pageSize.getHeight() - (stringWidth * fontSize) / 1000f) / 3f;

            PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.OVERWRITE, false);
            contentStream.setFont(font, fontSize);
            contentStream.beginText();
            // counterclockwise rotation
            for (int i = 0; i < 8; i++) {
                contentStream.setTextMatrix(Matrix.getRotateInstance(i * Math.PI * 0.25,
                        centeredXPosition, pageSize.getHeight() - centeredYPosition));
                contentStream.showText(message + " " + i);
            }
            // clockwise rotation
            for (int i = 0; i < 8; i++) {
                contentStream.setTextMatrix(Matrix.getRotateInstance(-i * Math.PI * 0.25,
                        centeredXPosition, centeredYPosition));
                contentStream.showText(message + " " + i);
            }

            contentStream.endText();
            contentStream.close();

            // Page 2
            page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            fontSize = 1.0f;

            contentStream = new PDPageContentStream(doc, page, AppendMode.OVERWRITE, false);
            contentStream.setFont(font, fontSize);
            contentStream.beginText();

            // text scaling and translation
            for (int i = 0; i < 10; i++) {
                contentStream.setTextMatrix(new Matrix(12f + (i * 6), 0, 0, 12f + (i * 6),
                        100, 100f + i * 50));
                contentStream.showText(message + " " + i);
            }
            contentStream.endText();
            contentStream.close();

            // Page 3
            page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            fontSize = 1.0f;

            contentStream = new PDPageContentStream(doc, page, AppendMode.OVERWRITE, false);
            contentStream.setFont(font, fontSize);
            contentStream.beginText();

            int i = 0;
            // text scaling combined with rotation 
            contentStream.setTextMatrix(new Matrix(12, 0, 0, 12, centeredXPosition, centeredYPosition * 1.5f));
            contentStream.showText(message + " " + i++);

            contentStream.setTextMatrix(new Matrix(0, 18, -18, 0, centeredXPosition, centeredYPosition * 1.5f));
            contentStream.showText(message + " " + i++);

            contentStream.setTextMatrix(new Matrix(-24, 0, 0, -24, centeredXPosition, centeredYPosition * 1.5f));
            contentStream.showText(message + " " + i++);

            contentStream.setTextMatrix(new Matrix(0, -30, 30, 0, centeredXPosition, centeredYPosition * 1.5f));
            contentStream.showText(message + " " + i++);

            contentStream.endText();
            contentStream.close();

            doc.save(outfile);
        }
    }

    public static void addMessageToEachPage(String infile, String message, String outfile) {
        TGS_UnSafe.run(() -> {
            try (var doc = Loader.loadPDF(new RandomAccessReadBufferedFile(infile))) {
                var font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                var fontSize = 36.0f;

                for (var page : doc.getPages()) {
                    var pageSize = page.getMediaBox();
                    var stringWidth = font.getStringWidth(message) * fontSize / 1000f;
                    // calculate to center of the page
                    var rotation = page.getRotation();
                    var rotate = rotation == 90 || rotation == 270;
                    var pageWidth = rotate ? pageSize.getHeight() : pageSize.getWidth();
                    var pageHeight = rotate ? pageSize.getWidth() : pageSize.getHeight();
                    var centerX = rotate ? pageHeight / 2f : (pageWidth - stringWidth) / 2f;
                    var centerY = rotate ? (pageWidth - stringWidth) / 2f : pageHeight / 2f;

                    // append the content to the existing stream
                    try (var contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                        contentStream.beginText();
                        // set font and font size
                        contentStream.setFont(font, fontSize);
                        // set text color to red
                        contentStream.setNonStrokingColor(Color.red);
                        if (rotate) {
                            // rotate the text according to the page rotation
                            contentStream.setTextMatrix(Matrix.getRotateInstance(Math.PI / 2, centerX, centerY));
                        } else {
                            contentStream.setTextMatrix(Matrix.getTranslateInstance(centerX, centerY));
                        }
                        contentStream.showText(message);
                        contentStream.endText();
                    }
                }

                doc.save(outfile);
            }
        });
    }

}
