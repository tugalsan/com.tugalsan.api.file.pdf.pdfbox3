package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.list.client.TGS_ListSortUtils;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;

public class TS_FilePdfBox3UtilsPageRemove {

    private static final TS_Log d = TS_Log.of(TS_FilePdfBox3UtilsPageRemove.class);

    public static TGS_UnionExcuseVoid remove(Path pdfSrc, Path pdfDest, boolean compressOnSave, int... pageIdxs_optional) {
        return TS_FilePdfBox3UtilsDocument.run_randomAccess(pdfSrc, doc -> {
            if (pageIdxs_optional == null || pageIdxs_optional.length == 0) {
                TGS_UnSafe.thrw(d.className, "remove", "pageIdxs_optional is empty");
            }
            TGS_ListSortUtils.sortPrimativeIntReversed(pageIdxs_optional);
            TS_FilePdfBox3UtilsPageGet.streamPageIdx(doc, pageIdxs_optional).forEachOrdered(pageIdx -> {
                doc.removePage(pageIdx);
            });
            var u_save = TS_FilePdfBox3UtilsSave.save(doc, pdfDest, compressOnSave);
            if (u_save.isExcuse()) {
                TGS_UnSafe.thrw(u_save.excuse());
            }
        });
    }
}
