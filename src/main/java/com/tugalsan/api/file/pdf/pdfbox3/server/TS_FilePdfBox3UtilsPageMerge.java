package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdfwriter.compress.CompressParameters;

public class TS_FilePdfBox3UtilsPageMerge {

    private static final TS_Log d = TS_Log.of(TS_FilePdfBox3UtilsPageMerge.class);

    public static TGS_UnionExcuseVoid merge(List<Path> pdfSrcFiles, Path pdfDstFile, boolean compressOnSave, String title, String creator, String subject) {
        return TGS_UnSafe.call(() -> {
            try (var cosStream = new COSStream()) {
                TS_FileUtils.deleteFileIfExists(pdfDstFile);
                var pdfMerger = new PDFMergerUtility();
                pdfMerger.setDestinationFileName(pdfDstFile.toAbsolutePath().toString());
                for (var nextPdfSrcFile : pdfSrcFiles) {//not streamable: IO EXCEPTION
                    pdfMerger.addSource(nextPdfSrcFile.toFile());
                }
                TS_FilePdfBox3UtilsInfo.set(pdfMerger, cosStream, title, creator, subject);
                pdfMerger.mergeDocuments(IOUtils.createMemoryOnlyStreamCache(), compressOnSave ? CompressParameters.DEFAULT_COMPRESSION : CompressParameters.NO_COMPRESSION);
                return TGS_UnionExcuseVoid.ofVoid();
            }
        }, e -> {
            return TGS_UnionExcuseVoid.ofExcuse(e);
        });
    }

    public TGS_UnionExcuse<InputStream> merge(final List<RandomAccessRead> sources, boolean compressOnSave, String title, String creator, String subject) {
        return TGS_UnSafe.call(() -> {
            try (var cosStream = new COSStream(); ByteArrayOutputStream mergedPDFOutputStream = new ByteArrayOutputStream()) {
                var pdfMerger = new PDFMergerUtility();
                pdfMerger.addSources(sources);
                pdfMerger.setDestinationStream(mergedPDFOutputStream);
                TS_FilePdfBox3UtilsInfo.set(pdfMerger, cosStream, title, creator, subject);
                pdfMerger.mergeDocuments(IOUtils.createMemoryOnlyStreamCache(), compressOnSave ? CompressParameters.DEFAULT_COMPRESSION : CompressParameters.NO_COMPRESSION);
                return TGS_UnionExcuse.of(new ByteArrayInputStream(mergedPDFOutputStream.toByteArray()));
            }
        }, e -> TGS_UnionExcuse.ofExcuse(e), () -> sources.forEach(IOUtils::closeQuietly));
    }
}
