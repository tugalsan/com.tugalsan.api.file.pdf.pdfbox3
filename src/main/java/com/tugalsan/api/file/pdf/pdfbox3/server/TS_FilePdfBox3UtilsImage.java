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
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

public class TS_FilePdfBox3UtilsImage {

    final private static TS_Log d = TS_Log.of(false, TS_FilePdfBox3UtilsImage.class);

    public static TGS_UnionExcuse<PDImageXObject> ofPDImageXObject(PDDocument document, Path imgFile) {
        return TGS_UnSafe.call(() -> {
            var pdImageXObject = PDImageXObject.createFromFile(imgFile.toAbsolutePath().toString(), document);
            return TGS_UnionExcuse.of(pdImageXObject);
        }, e -> TGS_UnionExcuse.ofExcuse(e));
    }

    public static TGS_UnionExcuse<PDImageXObject> ofPDImageXObject(PDDocument document, BufferedImage bi) {
        return TGS_UnSafe.call(() -> {
            var pdImageXObject = LosslessFactory.createFromImage(document, bi);
            return TGS_UnionExcuse.of(pdImageXObject);
        }, e -> TGS_UnionExcuse.ofExcuse(e));
    }

    public static TGS_UnionExcuse<BufferedImage> ofBufferedImage(Path pdfSrcFile, int pageIndex, Integer optionalDPI_DefaultIs300) {
        return TS_FilePdfBox3UtilsDocument.call_randomAccess(pdfSrcFile, doc -> {
            return TGS_UnSafe.call(() -> {
                var bi = optionalDPI_DefaultIs300 == null
                        ? new PDFRenderer(doc).renderImage(pageIndex)
                        : new PDFRenderer(doc).renderImageWithDPI(pageIndex, 300);
                return TGS_UnionExcuse.of(bi);
            }, e -> TGS_UnionExcuse.ofExcuse(e));
        });
    }

    public static TGS_UnionExcuseVoid insertImage(PDDocument document, PDPage page, PDImageXObject pdImage, int offsetX, int offsetY, float scale, boolean compress) {
        return TGS_UnSafe.call(() -> {
            try (var contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, compress, true)) {
                contentStream.drawImage(pdImage, offsetX, offsetY, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
                return TGS_UnionExcuseVoid.ofVoid();
            }
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

    public static List<TGS_UnionExcuse<Path>> ofPdf_fromImageFolder_A4PORT(Path directory, boolean skipIfExists, boolean deleteIMGAfterConversion, float quality, boolean compressOnInsertImage, boolean compressOnSave, boolean castToRGB) {
        d.ci("ofPdf_fromImageFolder_A4PORT", "directory", directory, "#10");
        var subFiles = TS_DirectoryUtils.subFiles(directory, null, false, false);
        d.ci("ofPdf_fromImageFolder_A4PORT", "directory", directory, "#20");
        List<TGS_UnionExcuse<Path>> convertedFiles = TGS_ListUtils.of();
        d.ci("ofPdf_fromImageFolder_A4PORT", "directory", directory, "#30");
        subFiles.stream().filter(subFile -> isSupported(subFile)).forEach(imgFile -> {
            d.ci("ofPdf_fromImageFolder_A4PORT", "directory", directory, "#100", ":1", imgFile);
            var pdfFile = imgFile.resolveSibling(TS_FileUtils.getNameLabel(imgFile) + ".pdf");
            d.ci("ofPdf_fromImageFolder_A4PORT", "directory", directory, "#100", ":2", imgFile);
            if (TS_FileUtils.isExistFile(pdfFile)) {
                if (skipIfExists) {
                    d.ci("ofPdf_fromImageFolder_A4PORT", "directory", directory, "#100", "skipIfExists", imgFile);
                    return;
                } else {
                    TS_FileUtils.deleteFileIfExists(pdfFile);
                }
            }
            d.ci("ofPdf_fromImageFolder_A4PORT", "directory", directory, "#100", ":3", imgFile);
            var u_file = ofPdf_fromImageFile_A4PORT(imgFile, pdfFile, quality, compressOnInsertImage, compressOnSave, castToRGB);
            d.ci("ofPdf_fromImageFolder_A4PORT", "directory", directory, "#100", ":4", imgFile);
            if (u_file.isExcuse()) {
                d.ce("ofPdf_fromImageFolder_A4PORT", "directory", directory, "#100", "isExcuse", imgFile, u_file.excuse().getMessage());
                convertedFiles.add(u_file.toExcuse());
            } else {
                d.ci("ofPdf_fromImageFolder_A4PORT", "directory", directory, "#100", ":5", imgFile);
                convertedFiles.add(TGS_UnionExcuse.of(pdfFile));
            }
            d.ci("ofPdf_fromImageFolder_A4PORT", "directory", directory, "#100", ":6", imgFile);
            if (deleteIMGAfterConversion) {
                TS_FileUtils.deleteFileIfExists(imgFile);
            }
            d.ci("ofPdf_fromImageFolder_A4PORT", "directory", directory, "#100", ":7", imgFile);
        });
        d.ci("ofPdf_fromImageFolder_A4PORT", "directory", directory, "#200");
        return convertedFiles;
    }

    public static TGS_UnionExcuseVoid ofPdf_fromImageFile_A4PORT(Path srcIMG, Path dstPDF, float quality, boolean compressOnInsertImage, boolean compressOnSave, boolean castToRGB) {
        var a4ImageWidth = 612;
        var a4ImageHeight = 792;
        var offsetX = 0;
        var offsetY = 0;
        var scale = 1f;
        TS_FileUtils.deleteFileIfExists(dstPDF);
        var bi = TS_FileImageUtils.autoSizeRespectfully(
                TS_FileImageUtils.readImageFromFile(srcIMG, castToRGB),
                new TGS_ShapeDimension(a4ImageWidth, a4ImageHeight),
                quality
        );
        return TS_FilePdfBox3UtilsDocument.run_new(doc -> {
            var page = new PDPage();
            doc.addPage(page);
            var u_pdImageXObject = ofPDImageXObject(doc, bi);
            if (u_pdImageXObject.isExcuse()) {
                TGS_UnSafe.thrw(u_pdImageXObject.excuse());
            }
            var u_inserImage = insertImage(doc, page, u_pdImageXObject.value(), offsetX, offsetY, scale, compressOnInsertImage);
            if (u_inserImage.isExcuse()) {
                TGS_UnSafe.thrw(u_inserImage.excuse());
            }
            var u_save = TS_FilePdfBox3UtilsSave.save(doc, dstPDF, compressOnSave);
            if (u_save.isExcuse()) {
                TGS_UnSafe.thrw(u_save.excuse());
            }
        });
    }

    public static boolean isSupported(Path imgFile) {
        var fn = TGS_CharSetCast.current().toLowerCase(imgFile.getFileName().toString());
        return fn.endsWith(".jpg") || fn.endsWith(".jpeg") || fn.endsWith(".tif") || fn.endsWith(".tiff") || fn.endsWith(".gif") || fn.endsWith(".bmp") || fn.endsWith(".png");
    }
}
