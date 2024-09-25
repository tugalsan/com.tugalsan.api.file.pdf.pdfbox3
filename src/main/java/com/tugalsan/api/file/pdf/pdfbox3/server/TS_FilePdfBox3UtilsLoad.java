package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.function.client.TGS_Func_In1;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;

public class TS_FilePdfBox3UtilsLoad {

    public static TGS_UnionExcuseVoid use_basic(Path pdfSrcFile, TGS_Func_In1<PDDocument> doc) {
        return TGS_UnSafe.call(() -> {
            try (var _doc = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfSrcFile.toFile()))) {
                doc.run(_doc);
                return TGS_UnionExcuseVoid.ofVoid();
            }
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

    public static TGS_UnionExcuseVoid use_randomAccess(Path pdfSrcFile, TGS_Func_In1<PDDocument> doc) {
        return TGS_UnSafe.call(() -> {
            try (var _doc = Loader.loadPDF(pdfSrcFile.toFile())) {
                doc.run(_doc);
                return TGS_UnionExcuseVoid.ofVoid();
            }
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }
}
