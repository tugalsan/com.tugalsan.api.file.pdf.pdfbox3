package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;

public class TS_FilePdfBox3UtilsPageExtract {

    private static final TS_Log d = TS_Log.of(TS_FilePdfBox3UtilsPageExtract.class);

    public static TGS_UnionExcuseVoid extract(Path pdfSrc, Path pdfDst, boolean compressOnSave, int... pageIdxs) {
        return TS_FilePdfBox3UtilsDocument.run_randomAccess(pdfSrc, docIn -> {
            var u_out = TS_FilePdfBox3UtilsDocument.run_new(docOut -> {
                TS_FilePdfBox3UtilsPageGet.streamPageIdx(docIn, pageIdxs).forEachOrdered(pageIdx -> {
//                    var fromPage = pageNr;
//                    var toPage = pageNr;
//                    var splitter = new Splitter();
//                    splitter.setStartPage(fromPage);
//                    splitter.setEndPage(toPage);
//                    splitter.setSplitAtPage(toPage - fromPage + 1);
//                    var lst = splitter.split(doc);
//                    var pdfDocPartial = lst.get(0);
                    docOut.addPage(docIn.getPage(pageIdx));
                });
                var u_save = TS_FilePdfBox3UtilsSave.save(docOut, pdfDst, compressOnSave);
                if (u_save.isExcuse()) {
                    TGS_UnSafe.thrw(u_save.excuse());
                }
            });
            if (u_out.isExcuse()) {
                TGS_UnSafe.thrw(u_out.excuse());
            }
        });
    }
}
