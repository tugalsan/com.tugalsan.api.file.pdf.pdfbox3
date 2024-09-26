package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

public class TS_FilePdfBox3UtilsPageScale {

    @Deprecated //TODO: I just wrote it. Not Tested!
    public static TGS_UnionExcuseVoid scale(Path pdfSrcFile, Path pdfDstFile, float xScale, float yScale) {
        return TGS_UnSafe.call(() -> {
            try (var doc = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfSrcFile.toAbsolutePath().toString()))) {
                var page = doc.getPage(0);
                try (var cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.PREPEND, true);) {
                    var matrix = new Matrix();
                    matrix.scale(xScale, yScale);
                    cs.transform(matrix);
                }
                doc.save(pdfDstFile.toFile());
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

    @Deprecated //TODO: I just wrote it. Not Tested!
    public static TGS_UnionExcuseVoid scaleToA4(Path pdfSrcFile, Path pdfDstFile, float scaleFactor) {
        return TGS_UnSafe.call(() -> {
            try (var doc = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfSrcFile.toAbsolutePath().toString()))) {
                var page = doc.getPage(0);
                try (var cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.PREPEND, true);) {
                    var matrix = new Matrix();
                    var xScale = PDRectangle.A4.getWidth() / page.getMediaBox().getWidth();
                    var yScale = PDRectangle.A4.getHeight() / page.getMediaBox().getHeight();
                    matrix.scale(xScale, yScale);
                    cs.transform(matrix);
                }
                doc.save(pdfDstFile.toFile());
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

}
