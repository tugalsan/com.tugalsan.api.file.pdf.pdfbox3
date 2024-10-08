package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class TS_FilePdfBox3UtilsPageCreate {

    final private static TS_Log d = TS_Log.of(TS_FilePdfBox3UtilsPageCreate.class);

    public static PDPage addBlackPage(PDDocument doc) {
        var pdPage = new PDPage();
        doc.addPage(pdPage);
        return pdPage;
    }

    public static TGS_UnionExcuseVoid createBlankFile(Path pdfDest, boolean compress) {
        return TS_FilePdfBox3UtilsDocument.run_new(doc -> {
            addBlackPage(doc);
            var u_save = TS_FilePdfBox3UtilsSave.save(doc, pdfDest, compress);
            if (u_save.isExcuse()) {
                TGS_UnSafe.thrw(u_save.excuse());
            }
        });
    }
}
