package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;
import org.apache.pdfbox.pdfwriter.compress.CompressParameters;
import org.apache.pdfbox.pdmodel.PDDocument;

public class TS_FilePdfBox3UtilsSave {

    public static TGS_UnionExcuseVoid save(PDDocument doc, Path dest, boolean compress) {
        return TGS_UnSafe.call(() -> {
            doc.save(dest.toFile(), compress ? CompressParameters.DEFAULT_COMPRESSION : CompressParameters.NO_COMPRESSION);
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }
}
