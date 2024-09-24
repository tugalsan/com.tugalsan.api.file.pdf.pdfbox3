package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.charset.client.TGS_CharSetCast;
import com.tugalsan.api.file.img.server.TS_FileImageUtils;
import com.tugalsan.api.file.server.TS_DirectoryUtils;
import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.list.client.TGS_ListUtils;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.shape.client.TGS_ShapeDimension;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.util.Matrix;

public class TS_FilePdfBox3UtilsImage {

    final private static TS_Log d = TS_Log.of(TS_FilePdfBox3UtilsImage.class);

    public static PDImageXObject getImage(Path imgFile, PDDocument document) {
        return TGS_UnSafe.call(() -> PDImageXObject.createFromFile(imgFile.toAbsolutePath().toString(), document));
    }

    public static PDImageXObject getImage(BufferedImage bi, PDDocument document) {
        return TGS_UnSafe.call(() -> LosslessFactory.createFromImage(document, bi));
    }

    @Deprecated //TODO: I just wrote it. Not Tested!
    public static TGS_UnionExcuseVoid toJpg(Path pdfSrcFile, Path jpgDstFile, int pageNumber, Integer optionalDPI) {
        return TS_FilePdfBox3UtilsLoad.use(pdfSrcFile, doc -> {
            TGS_UnSafe.run(() -> {
                var renderer = new PDFRenderer(doc);
                var bi = optionalDPI == null
                        ? renderer.renderImage(pageNumber)
                        : renderer.renderImageWithDPI(pageNumber, 300);
                var result = ImageIO.write(bi, "JPEG", jpgDstFile.toFile());
                if (!result) {
                    TGS_UnSafe.thrw(d.className, "toJpg", "!result");
                }
            });
        });
    }

    public static void insertImage(PDDocument document, PDPage page, PDImageXObject pdImage, int offsetX, int offsetY, float scale) {
        TGS_UnSafe.run(() -> {
            try (var contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                contentStream.drawImage(pdImage, offsetX, offsetY, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
            }
        });
    }

    public static List<TGS_UnionExcuse<Path>> castFromIMGtoPDF_A4PORT_AllFiles(Path directory, boolean skipIfExists, boolean deleteIMGAfterConversion, float quality) {
        var subFiles = TS_DirectoryUtils.subFiles(directory, null, false, false);
        List<TGS_UnionExcuse<Path>> convertedFiles = TGS_ListUtils.of();
        subFiles.stream().filter(subFile -> isSupportedIMG(subFile)).forEach(subImg -> {
            var subPdf = subImg.resolveSibling(TS_FileUtils.getNameLabel(subImg) + ".pdf");
            if (TS_FileUtils.isExistFile(subPdf)) {
                if (skipIfExists) {
                    return;
                } else {
                    TS_FileUtils.deleteFileIfExists(subPdf);
                }
            }
            var u = castFromIMGtoPDF_A4PORT(subImg, subPdf, quality);
            if (u.isExcuse()) {
                convertedFiles.add(TGS_UnionExcuse.ofExcuse(u.excuse()));
            } else {
                convertedFiles.add(TGS_UnionExcuse.of(subPdf));
            }
            if (deleteIMGAfterConversion) {
                TS_FileUtils.deleteFileIfExists(subImg);
            }
        });
        return convertedFiles;
    }

    public static TGS_UnionExcuseVoid castFromIMGtoPDF_A4PORT(Path srcIMG, Path dstPDF, float quality) {
        return TGS_UnSafe.call(() -> {
            TS_FileUtils.deleteFileIfExists(dstPDF);
            var bi = TS_FileImageUtils.autoSizeRespectfully(
                    TS_FileImageUtils.readImageFromFile(srcIMG, true),
                    new TGS_ShapeDimension(612, 792),
                    quality
            );
            try (var doc = new PDDocument();) {
                var page = new PDPage();
                doc.addPage(page);
                var pdImage = getImage(bi, doc);
                insertImage(doc, page, pdImage, 0, 0, 1f);
                doc.save(dstPDF.toFile());
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

    public static boolean isSupportedIMG(Path imgFile) {
        var fn = TGS_CharSetCast.current().toLowerCase(imgFile.getFileName().toString());
        return fn.endsWith(".jpg") || fn.endsWith(".jpeg") || fn.endsWith(".tif") || fn.endsWith(".tiff") || fn.endsWith(".gif") || fn.endsWith(".bmp") || fn.endsWith(".png");
    }

    public TGS_UnionExcuseVoid createPDFFromImage(Path inputFile, String imagePath, Path outputFile) {
        return TGS_UnSafe.call(() -> {
            try (var doc = Loader.loadPDF(inputFile.toFile())) {
                var page = doc.getPage(0);
                // call LosslessFactory.createFromImage() instead
                var pdImage = PDImageXObject.createFromFile(imagePath, doc);
                try (var contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true)) {
                    // contentStream.drawImage(ximage, 20, 20 );
                    // better method inspired by http://stackoverflow.com/a/22318681/535646
                    // reduce this value if the image is too large
                    var scale = 1f;
                    contentStream.drawImage(pdImage, 20, 20, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
                }
                doc.save(outputFile.toFile());
                return TGS_UnionExcuseVoid.ofVoid();
            }
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

    public TGS_UnionExcuseVoid addMessageToEachPage(Path inputFile, String message, Path outfile) {
        return TGS_UnSafe.call(() -> {
            try (var doc = Loader.loadPDF(inputFile.toFile())) {
                var font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                float fontSize = 36.0f;

                for (var page : doc.getPages()) {
                    var pageSize = page.getMediaBox();
                    float stringWidth = font.getStringWidth(message) * fontSize / 1000f;
                    // calculate to center of the page
                    var rotation = page.getRotation();
                    var rotate = rotation == 90 || rotation == 270;
                    var pageWidth = rotate ? pageSize.getHeight() : pageSize.getWidth();
                    var pageHeight = rotate ? pageSize.getWidth() : pageSize.getHeight();
                    var centerX = rotate ? pageHeight / 2f : (pageWidth - stringWidth) / 2f;
                    var centerY = rotate ? (pageWidth - stringWidth) / 2f : pageHeight / 2f;

                    // append the content to the existing stream
                    try (var contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true)) {
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
                doc.save(outfile.toFile());
                return TGS_UnionExcuseVoid.ofVoid();
            }
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

    public TGS_UnionExcuseVoid createLandscapePDF(String message, Path outfile) {
        return TGS_UnSafe.call(() -> {
            try (var doc = new PDDocument()) {
                var font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                var page = new PDPage(PDRectangle.A4);
                page.setRotation(90);
                doc.addPage(page);
                var pageSize = page.getMediaBox();
                var pageWidth = pageSize.getWidth();
                var fontSize = 12;
                var stringWidth = font.getStringWidth(message) * fontSize / 1000f;
                var startX = 100;
                var startY = 100;

                try (var contentStream = new PDPageContentStream(doc, page, AppendMode.OVERWRITE, false)) {
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
                doc.save(outfile.toFile());
                return TGS_UnionExcuseVoid.ofVoid();
            }
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

}
