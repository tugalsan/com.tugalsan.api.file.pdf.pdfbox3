package com.tugalsan.api.file.pdf.pdfbox3.server.tut;

import com.tugalsan.api.unsafe.client.*;
import java.awt.Color;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.util.Matrix;

public class Page {

    public static void AddMessageToEachPage(String infile, String message, String outfile) {
        TGS_UnSafe.run(() -> {
            try ( var doc = Loader.loadPDF(new RandomAccessReadBufferedFile(infile))) {
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
                    try ( var contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
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

    public static void CreateBlankPDF(String inputFile) {
        TGS_UnSafe.run(() -> {
            try ( var doc = new PDDocument()) {
                // a valid PDF document requires at least one page
                var blankPage = new PDPage();
                doc.addPage(blankPage);
                doc.save(inputFile);
            }
        });
    }

    public static void CreateLandscapePDF(String message, String outfile) {
        TGS_UnSafe.run(() -> {
            try ( var doc = new PDDocument()) {
                var font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                var page = new PDPage(PDRectangle.A4);
                page.setRotation(90);
                doc.addPage(page);
                var pageSize = page.getMediaBox();
                var pageWidth = pageSize.getWidth();
                float fontSize = 12;
                var stringWidth = font.getStringWidth(message) * fontSize / 1000f;
                float startX = 100;
                float startY = 100;

                try ( var contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.OVERWRITE, false)) {
                    // add the rotation using the current transformation matrix
                    // including a translation of pageWidth to use the lower left corner as 0,0 reference
                    contentStream.transform(new Matrix(0, 1, -1, 0, pageWidth, 0));
                    contentStream.setFont(font, fontSize);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(startX, startY);
                    contentStream.showText(message);
                    contentStream.newLineAtOffset(0, 100);
                    contentStream.showText(message);
                    contentStream.newLineAtOffset(100, 100);
                    contentStream.showText(message);
                    contentStream.endText();

                    contentStream.moveTo(startX - 2, startY - 2);
                    contentStream.lineTo(startX - 2, startY + 200 + fontSize);
                    contentStream.stroke();

                    contentStream.moveTo(startX - 2, startY + 200 + fontSize);
                    contentStream.lineTo(startX + 100 + stringWidth + 2, startY + 200 + fontSize);
                    contentStream.stroke();

                    contentStream.moveTo(startX + 100 + stringWidth + 2, startY + 200 + fontSize);
                    contentStream.lineTo(startX + 100 + stringWidth + 2, startY - 2);
                    contentStream.stroke();

                    contentStream.moveTo(startX + 100 + stringWidth + 2, startY - 2);
                    contentStream.lineTo(startX - 2, startY - 2);
                    contentStream.stroke();
                }

                doc.save(outfile);
            }
        });
    }

    public static void RemoveFirstPage(String filePath) {
        TGS_UnSafe.run(() -> {
            try ( var document = Loader.loadPDF(new RandomAccessReadBufferedFile(filePath))) {
                if (document.isEncrypted()) {
                    TGS_UnSafe.thrw(Page.class.getSimpleName(), "RemoveFirstPage", "Encrypted documents are not supported for this example");
                }
                if (document.getNumberOfPages() <= 1) {
                    TGS_UnSafe.thrw(Page.class.getSimpleName(), "RemoveFirstPage", "Error: A PDF document must have at least one page, cannot remove the last page!");
                }
                document.removePage(0);
                document.save(filePath);
            }
        });
    }
}
