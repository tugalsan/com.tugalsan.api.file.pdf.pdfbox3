package com.tugalsan.api.file.pdf.pdfbox3.server.tut;

import java.io.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.PDPageContentStream.*;
import org.apache.pdfbox.pdmodel.common.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.font.encoding.*;
import org.apache.pdfbox.util.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.unsafe.client.*;

public class Text {

    public static void ShowTextWithPositioning(String message, String outfile, float fontSize_20) {
        TGS_UnSafe.run(() -> {
            // the document
            try ( var doc = new PDDocument();  InputStream is = PDDocument.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf")) {
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

                try ( var contentStream = new PDPageContentStream(doc,
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

    public static void HelloWorld(String message, String filename) {
        TGS_UnSafe.run(() -> {
            try ( var doc = new PDDocument()) {
                var page = new PDPage();
                doc.addPage(page);

                var font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                try ( var contents = new PDPageContentStream(doc, page)) {
                    contents.beginText();
                    contents.setFont(font, 12);
                    contents.newLineAtOffset(100, 700);
                    contents.showText(message);
                    contents.endText();
                }

                doc.save(filename);
            }
        });
    }

}
