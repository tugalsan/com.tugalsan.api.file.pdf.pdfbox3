package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;
import org.apache.pdfbox.pdmodel.PDDocument;

public class TS_FilePdfBox3UtilsPageRemove {

    public static void remove(PDDocument doc, int pageIdx) {
        doc.removePage(pageIdx);
    }

    public static TGS_UnionExcuseVoid remove(Path pdfSrc, Path pdfDest, int pageIdx, boolean compress) {
        return TS_FilePdfBox3UtilsDocument.run_randomAccess(pdfSrc, doc -> {
            remove(doc, pageIdx);
            var u_save = TS_FilePdfBox3UtilsSave.save(doc, pdfDest, compress);
            if (u_save.isExcuse()) {
                TGS_UnSafe.thrw(u_save.excuse());
            }
        });
    }
}
