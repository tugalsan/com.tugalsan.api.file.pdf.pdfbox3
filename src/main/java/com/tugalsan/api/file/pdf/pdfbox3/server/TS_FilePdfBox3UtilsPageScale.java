package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

public class TS_FilePdfBox3UtilsPageScale {

    public static TGS_UnionExcuseVoid scale(Path pdfSrcFile, Path pdfDstFile, boolean compressOnSave, boolean compressOnStream, float xScale, float yScale, int... pageIdxs_optional) {
        return TS_FilePdfBox3UtilsDocument.run_randomAccess(pdfSrcFile, doc -> {
            TS_FilePdfBox3UtilsPageGet.getPages(doc, pageIdxs_optional).forEach(page -> {
                TGS_UnSafe.run(() -> {
                    var matrix = new Matrix();
                    matrix.scale(xScale, yScale);
                    try (var cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.PREPEND, compressOnStream);) {
                        cs.transform(matrix);
                    }
                });
            });
            var u_save = TS_FilePdfBox3UtilsSave.save(doc, pdfDstFile, compressOnSave);
            if (u_save.isExcuse()) {
                TGS_UnSafe.thrw(u_save.excuse());
            }
        });
    }

    public static TGS_UnionExcuseVoid scaleToA4(Path pdfSrcFile, Path pdfDstFile, boolean compressOnSave, boolean compressOnStream, float scaleFactor, int... pageIdxs_optional) {
        return TS_FilePdfBox3UtilsDocument.run_randomAccess(pdfSrcFile, doc -> {
            TS_FilePdfBox3UtilsPageGet.getPages(doc, pageIdxs_optional).forEach(page -> {
                TGS_UnSafe.run(() -> {
                    var xScale = PDRectangle.A4.getWidth() / page.getMediaBox().getWidth();
                    var yScale = PDRectangle.A4.getHeight() / page.getMediaBox().getHeight();
                    var matrix = new Matrix();
                    try (var cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.PREPEND, compressOnStream);) {
                        matrix.scale(xScale, yScale);
                        cs.transform(matrix);
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
