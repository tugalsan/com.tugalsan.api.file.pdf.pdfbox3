package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;

public class TS_FilePdfBox3UtilsPageExtract {

    private static final TS_Log d = TS_Log.of(TS_FilePdfBox3UtilsPageExtract.class);

    @Deprecated //TODO: I just wrote it. Not Tested!
    public static TGS_UnionExcuseVoid extract(Path pdfSrcFile, int pageNr, Path pdfDstFile) {
        return TGS_UnSafe.call(() -> {
            TS_FileUtils.deleteFileIfExists(pdfDstFile);
            if (TS_FileUtils.isExistFile(pdfDstFile)) {
                return TGS_UnionExcuseVoid.ofExcuse(d.className, "extract", "TS_FileUtils.isExistFile(pdfDstFile)");
            }
            try (var doc = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfSrcFile.toAbsolutePath().toString()))) {
//                var fromPage = pageNr;
//                var toPage = pageNr;
//                var splitter = new Splitter();
//                splitter.setStartPage(fromPage);
//                splitter.setEndPage(toPage);
//                splitter.setSplitAtPage(toPage - fromPage + 1);
//                var lst = splitter.split(doc);
//                var pdfDocPartial = lst.get(0);
//                pdfDocPartial.save(pdfDstFile.toFile());
                try (var out = new PDDocument();) {
                    out.addPage(doc.getPage(pageNr));
                    out.save(pdfDstFile.toFile());
                }
            }
            if (!TS_FileUtils.isExistFile(pdfDstFile)) {
                return TGS_UnionExcuseVoid.ofExcuse(d.className, "extract", "!TS_FileUtils.isExistFile(pdfDstFile)");
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> {
            return TGS_UnionExcuseVoid.ofExcuse(e);
        });
    }

    @Deprecated //TODO: I just wrote it. Not Tested!
    public static TGS_UnionExcuseVoid extract(Path pdfSrcFile, int[] pageNrs, Path pdfDstFile) {
        return TGS_UnSafe.call(() -> {
            TS_FileUtils.deleteFileIfExists(pdfDstFile);
            if (TS_FileUtils.isExistFile(pdfDstFile)) {
                return TGS_UnionExcuseVoid.ofExcuse(d.className, "extract", "TS_FileUtils.isExistFile(pdfDstFile)");
            }
            try (var doc = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfSrcFile.toAbsolutePath().toString()))) {
                try (var out = new PDDocument();) {
                    for (var pageNr : pageNrs) {
//                        var fromPage = pageNr;
//                        var toPage = pageNr;
//                        var splitter = new Splitter();
//                        splitter.setStartPage(fromPage);
//                        splitter.setEndPage(toPage);
//                        splitter.setSplitAtPage(toPage - fromPage + 1);
//                        var lst = splitter.split(doc);
//                        var pdfDocPartial = lst.get(0);
                        out.addPage(doc.getPage(pageNr));
                    }
                    out.save(pdfDstFile.toFile());
                }
            }
            if (!TS_FileUtils.isExistFile(pdfDstFile)) {
                return TGS_UnionExcuseVoid.ofExcuse(d.className, "extract", "!TS_FileUtils.isExistFile(pdfDstFile)");
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> {
            return TGS_UnionExcuseVoid.ofExcuse(e);
        });
    }
}
