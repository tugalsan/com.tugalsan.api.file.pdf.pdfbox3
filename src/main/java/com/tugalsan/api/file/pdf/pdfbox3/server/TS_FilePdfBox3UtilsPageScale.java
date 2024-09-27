package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.function.client.TGS_Func_OutTyped_In1;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

public class TS_FilePdfBox3UtilsPageScale {

    public static TGS_UnionExcuseVoid scale(Path pdfSrcFile, Path pdfDstFile, boolean compressOnSave, boolean compressOnStream, TGS_Func_OutTyped_In1<Float, PDPage> scaleX, TGS_Func_OutTyped_In1<Float, PDPage> scaleY, int... pageIdxs_optional) {
        return TS_FilePdfBox3UtilsDocument.run_randomAccess(pdfSrcFile, doc -> {
            TS_FilePdfBox3UtilsPageGet.getPages(doc, pageIdxs_optional).forEach(page -> {
                TGS_UnSafe.run(() -> {
                    var matrix = new Matrix();
                    matrix.scale(scaleX.call(page), scaleY.call(page));
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

    public static TGS_UnionExcuseVoid scale(Path pdfSrcFile, Path pdfDstFile, boolean compressOnSave, boolean compressOnStream, float scaleX, float scaleY, int... pageIdxs_optional) {
        return scale(pdfSrcFile, pdfDstFile, compressOnSave, compressOnStream, page -> scaleX, page -> scaleY, pageIdxs_optional);
    }

    public static TGS_UnionExcuseVoid scale(Path pdfSrcFile, Path pdfDstFile, boolean compressOnSave, boolean compressOnStream, float scaleXY, int... pageIdxs_optional) {
        return scale(pdfSrcFile, pdfDstFile, compressOnSave, compressOnStream, scaleXY, scaleXY, pageIdxs_optional);
    }

    public static TGS_UnionExcuseVoid scale(Path pdfSrcFile, Path pdfDstFile, boolean compressOnSave, boolean compressOnStream, PDRectangle pageSize, boolean respectPageSize, int... pageIdxs_optional) {
        TGS_Func_OutTyped_In1<Float, PDPage> scaleX = page -> {
            var xScale = pageSize.getWidth() / page.getMediaBox().getWidth();
            if (respectPageSize) {
                var yScale = pageSize.getHeight() / page.getMediaBox().getHeight();
                return Math.min(xScale, yScale);
            }
            return xScale;
        };
        TGS_Func_OutTyped_In1<Float, PDPage> scaleY = page -> {
            var yScale = pageSize.getHeight() / page.getMediaBox().getHeight();
            if (respectPageSize) {
                var xScale = pageSize.getWidth() / page.getMediaBox().getWidth();
                return Math.min(xScale, yScale);
            }
            return yScale;
        };
        return scale(pdfSrcFile, pdfDstFile, compressOnSave, compressOnStream, scaleX, scaleY, pageIdxs_optional);
    }
}
