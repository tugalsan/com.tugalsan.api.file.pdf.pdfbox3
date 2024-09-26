package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;

public class TS_FilePdfBox3UtilsPageCount {

    @Deprecated //TODO: I just wrote it. Not Tested!
    public static TGS_UnionExcuse<Integer> count(Path pdfFile) {
        return TGS_UnSafe.call(() -> {
            try (var doc = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfFile.toAbsolutePath().toString()))) {
                return TGS_UnionExcuse.of(doc.getNumberOfPages());
            }
        }, e -> {
            return TGS_UnionExcuse.ofExcuse(e);
        });
    }
}
