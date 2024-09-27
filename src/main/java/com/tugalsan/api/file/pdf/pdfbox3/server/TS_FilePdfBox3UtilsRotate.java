package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

public class TS_FilePdfBox3UtilsRotate {

    public static TGS_UnionExcuseVoid rotatePage(Path pdfSrcFile, Path pdfDstFile, boolean compressOnSave, int degree, float rotateX, float rotateY, int... pageIdxs_optional) {
        return TS_FilePdfBox3UtilsDocument.run_randomAccess(pdfSrcFile, doc -> {
            TS_FilePdfBox3UtilsPageGet.getPages(doc, pageIdxs_optional).forEach(page -> {
                page.setRotation(degree);
            });
            var u_save = TS_FilePdfBox3UtilsSave.save(doc, pdfDstFile, compressOnSave);
            if (u_save.isExcuse()) {
                TGS_UnSafe.thrw(u_save.excuse());
            }
        });
    }

    public static TGS_UnionExcuseVoid rotateWithCropBox(Path pdfSrcFile, Path pdfDstFile, boolean compressOnSave, boolean compressOnStream, int degree, float rotateX, float rotateY, int... pageIdxs_optional) {
        return TS_FilePdfBox3UtilsDocument.run_randomAccess(pdfSrcFile, doc -> {
            TS_FilePdfBox3UtilsPageGet.getPages(doc, pageIdxs_optional).forEach(page -> {
                TGS_UnSafe.run(() -> {
                    var matrix = Matrix.getRotateInstance(Math.toRadians(degree), rotateX, rotateY);
                    try (var cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.PREPEND, compressOnStream, false);) {
                        cs.transform(matrix);
                    }
                    var cropBox = page.getCropBox();
                    var rectangle = cropBox.transform(matrix).getBounds();
                    var newBox = new PDRectangle((float) rectangle.getX(), (float) rectangle.getY(), (float) rectangle.getWidth(), (float) rectangle.getHeight());
                    page.setCropBox(newBox);
                    page.setMediaBox(newBox);
                });
            });
            var u_save = TS_FilePdfBox3UtilsSave.save(doc, pdfDstFile, compressOnSave);
            if (u_save.isExcuse()) {
                TGS_UnSafe.thrw(u_save.excuse());
            }
        });
    }

    public static TGS_UnionExcuseVoid rotateAndFitContent(Path pdfSrcFile, Path pdfDstFile, boolean compressOnSave, boolean compressOnStream, int degree, float rotateX, float rotateY, int... pageIdxs_optional) {
        return TS_FilePdfBox3UtilsDocument.run_randomAccess(pdfSrcFile, doc -> {
            TS_FilePdfBox3UtilsPageGet.getPages(doc, pageIdxs_optional).forEach(page -> {
                TGS_UnSafe.run(() -> {
                    var matrix = Matrix.getRotateInstance(Math.toRadians(degree), rotateX, rotateY);
                    var cropBox = page.getCropBox();
                    var tx = (cropBox.getLowerLeftX() + cropBox.getUpperRightX()) / 2;
                    var ty = (cropBox.getLowerLeftY() + cropBox.getUpperRightY()) / 2;
                    var rectangle = cropBox.transform(matrix).getBounds();
                    var scale = Math.min(cropBox.getWidth() / (float) rectangle.getWidth(), cropBox.getHeight() / (float) rectangle.getHeight());
                    try (var cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.PREPEND, false, false);) {
                        cs.transform(Matrix.getTranslateInstance(tx, ty));
                        cs.transform(matrix);
                        cs.transform(Matrix.getScaleInstance(scale, scale));
                        cs.transform(Matrix.getTranslateInstance(-tx + tx, -ty + ty));
                    }
                });
            });
            var u_save = TS_FilePdfBox3UtilsSave.save(doc, pdfDstFile, compressOnSave);
            if (u_save.isExcuse()) {
                TGS_UnSafe.thrw(u_save.excuse());
            }
        });
    }
}
