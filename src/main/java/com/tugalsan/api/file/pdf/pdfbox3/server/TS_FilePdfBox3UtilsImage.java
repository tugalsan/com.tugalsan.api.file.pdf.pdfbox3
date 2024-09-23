package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.charset.client.TGS_CharSetCast;
import com.tugalsan.api.file.img.server.TS_FileImageUtils;
import com.tugalsan.api.file.server.TS_DirectoryUtils;
import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.list.client.TGS_ListUtils;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.shape.client.TGS_ShapeDimension;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

public class TS_FilePdfBox3UtilsImage {

    final private static TS_Log d = TS_Log.of(TS_FilePdfBox3UtilsImage.class);

    public static PDImageXObject getImage(Path imgFile, PDDocument document) {
        return TGS_UnSafe.call(() -> PDImageXObject.createFromFile(imgFile.toAbsolutePath().toString(), document));
    }

    public static PDImageXObject getImage(BufferedImage bi, PDDocument document) {
        return TGS_UnSafe.call(() -> LosslessFactory.createFromImage(document, bi));
    }

    @Deprecated //TODO: I just wrote it. Not Tested!
    public static TGS_UnionExcuseVoid toJpg(Path pdfSrcFile, Path jpgDstFile, int pageNumber) {
        return TGS_UnSafe.call(() -> {
            TS_FileUtils.deleteFileIfExists(jpgDstFile);
            if (TS_FileUtils.isExistFile(jpgDstFile)) {
                return TGS_UnionExcuseVoid.ofExcuse(d.className, "toJpg", "TS_FileUtils.isExistFile(jpgDstFile)");
            }
            try (var doc = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfSrcFile.toAbsolutePath().toString()))) {
                var renderer = new PDFRenderer(doc);
                //var image = renderer.renderImage(pageNumber);
                var image = renderer.renderImageWithDPI(pageNumber, 300);
                var result = ImageIO.write(image, "JPEG", jpgDstFile.toFile());
                if (!result) {
                    return TGS_UnionExcuseVoid.ofExcuse(d.className, "toJpg", "!result");
                }
            }
            if (!TS_FileUtils.isExistFile(jpgDstFile)) {
                return TGS_UnionExcuseVoid.ofExcuse(d.className, "toJpg", "!TS_FileUtils.isExistFile(jpgDstFile)");
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> {
            return TGS_UnionExcuseVoid.ofExcuse(e);
        });
    }

    public static void insertImage(PDDocument document, PDPage page, PDImageXObject pdImage, int offsetX, int offsetY, float scale) {
        TGS_UnSafe.run(() -> {
            try (var contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                contentStream.drawImage(pdImage, offsetX, offsetY, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
            }
        });
    }

    public static List<Path> castFromIMGtoPDF_A4PORT_AllFiles(Path directory, boolean skipIfExists, boolean deleteIMGAfterConversion) {
        var subFiles = TS_DirectoryUtils.subFiles(directory, null, false, false);
        List<Path> convertedFiles = TGS_ListUtils.of();
        subFiles.stream().filter(subFile -> isSupportedIMG(subFile)).forEach(subImg -> {
            var subPdf = subImg.resolveSibling(TS_FileUtils.getNameLabel(subImg) + ".pdf");
            if (TS_FileUtils.isExistFile(subPdf)) {
                if (skipIfExists) {
                    return;
                } else {
                    TS_FileUtils.deleteFileIfExists(subPdf);
                }
            }
            castFromIMGtoPDF_A4PORT(subImg, subPdf);
            convertedFiles.add(subPdf);
            if (deleteIMGAfterConversion) {
                TS_FileUtils.deleteFileIfExists(subImg);
            }
        });
        return convertedFiles;
    }

    public static Path castFromIMGtoPDF_A4PORT(Path srcIMG, Path dstPDF) {
        return TGS_UnSafe.call(() -> {
            d.cr("castFromJPGtoPDF", "init", srcIMG, dstPDF);
            TS_FileUtils.deleteFileIfExists(dstPDF);
            var bi = TS_FileImageUtils.autoSizeRespectfully(TS_FileImageUtils.readImageFromFile(srcIMG, true),
                    new TGS_ShapeDimension(612, 792),
                    0.8f
            );
            try (var document = new PDDocument();) {
                var blankPage = new PDPage();
                document.addPage(blankPage);
                var pdImage = getImage(bi, document);
                insertImage(document, blankPage, pdImage, 0, 0, 1f);
                document.save(dstPDF.toFile());
            }
            return dstPDF;
        }, e -> {
            d.ce("castFromIMGtoPDF_A4PORT", "failed", e.getMessage());
            return TGS_UnSafe.thrw(e);
        });
    }

    public static boolean isSupportedIMG(Path imgFile) {
        var fn = TGS_CharSetCast.current().toLowerCase(imgFile.getFileName().toString());
        return fn.endsWith(".jpg") || fn.endsWith(".jpeg") || fn.endsWith(".tif") || fn.endsWith(".tiff") || fn.endsWith(".gif") || fn.endsWith(".bmp") || fn.endsWith(".png");
    }
}
