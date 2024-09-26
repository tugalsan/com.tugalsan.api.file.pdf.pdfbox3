package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

public class TS_FilePdfBox3UtilsRotate {

    @Deprecated //TODO: I just wrote it. Not Tested!
    public static TGS_UnionExcuseVoid rotatePage(Path pdfSrcFile, Path pdfDstFile, int degree, float rotateX, float rotateY) {
        return TGS_UnSafe.call(() -> {
            try (var doc = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfSrcFile.toAbsolutePath().toString()))) {
                TGS_StreamUtils.of(doc.getDocumentCatalog().getPages()).forEach(page -> {
                    page.setRotation(degree);
                });
                doc.save(pdfDstFile.toFile());
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

    @Deprecated //TODO: I just wrote it. Not Tested!
    public static TGS_UnionExcuseVoid rotateWithCropBox(Path pdfSrcFile, Path pdfDstFile, int degree, float rotateX, float rotateY) {
        return TGS_UnSafe.call(() -> {
            try (var doc = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfSrcFile.toAbsolutePath().toString()))) {
                var page = doc.getDocumentCatalog().getPages().get(0);
                var matrix = Matrix.getRotateInstance(Math.toRadians(degree), rotateX, rotateY);
                try (var cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.PREPEND, false, false);) {
                    cs.transform(matrix);
                }
                var cropBox = page.getCropBox();
                var rectangle = cropBox.transform(matrix).getBounds();
                var newBox = new PDRectangle((float) rectangle.getX(), (float) rectangle.getY(), (float) rectangle.getWidth(), (float) rectangle.getHeight());
                page.setCropBox(newBox);
                page.setMediaBox(newBox);
                doc.save(pdfDstFile.toFile());
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

    @Deprecated //TODO: I just wrote it. Not Tested!
    public static TGS_UnionExcuseVoid rotateAndFitContent(Path pdfSrcFile, Path pdfDstFile, int degree, float rotateX, float rotateY) {
        return TGS_UnSafe.call(() -> {
            try (var doc = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfSrcFile.toAbsolutePath().toString()))) {
                var page = doc.getDocumentCatalog().getPages().get(0);
                try (var cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.PREPEND, false, false);) {
                    var matrix = Matrix.getRotateInstance(Math.toRadians(degree), rotateX, rotateY);
                    var cropBox = page.getCropBox();
                    var tx = (cropBox.getLowerLeftX() + cropBox.getUpperRightX()) / 2;
                    var ty = (cropBox.getLowerLeftY() + cropBox.getUpperRightY()) / 2;
                    var rectangle = cropBox.transform(matrix).getBounds();
                    var scale = Math.min(cropBox.getWidth() / (float) rectangle.getWidth(), cropBox.getHeight() / (float) rectangle.getHeight());
                    cs.transform(Matrix.getTranslateInstance(tx, ty));
                    cs.transform(matrix);
                    cs.transform(Matrix.getScaleInstance(scale, scale));
                    cs.transform(Matrix.getTranslateInstance(-tx + tx, -ty + ty));
                }
                doc.save(pdfDstFile.toFile());
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

}
