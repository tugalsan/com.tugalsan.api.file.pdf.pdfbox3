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
import java.util.Calendar;
import java.util.List;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdfwriter.compress.CompressParameters;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.xml.XmpSerializer;

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
                addDesc(pdfMerger, cosStream, title, creator, subject);
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
                addDesc(pdfMerger, cosStream, title, creator, subject);
                pdfMerger.mergeDocuments(IOUtils.createMemoryOnlyStreamCache(), compressOnSave ? CompressParameters.DEFAULT_COMPRESSION : CompressParameters.NO_COMPRESSION);
                return TGS_UnionExcuse.of(new ByteArrayInputStream(mergedPDFOutputStream.toByteArray()));
            }
        }, e -> TGS_UnionExcuse.ofExcuse(e), () -> sources.forEach(IOUtils::closeQuietly));
    }

    private static void addDesc(PDFMergerUtility pdfMerger, COSStream cosStream, String title, String creator, String subject) {
        TGS_UnSafe.run(() -> {
            {//ADD PDF INFO
                var pdfInfo = new PDDocumentInformation();
                pdfInfo.setTitle(title);
                pdfInfo.setCreator(creator);
                pdfInfo.setSubject(subject);
                pdfMerger.setDestinationDocumentInformation(pdfInfo);
            }
            {//ADD PDF METADATA
                var pdfMeta = XMPMetadata.createXMPMetadata();
                var pdfaSchema = pdfMeta.createAndAddPDFAIdentificationSchema();
                pdfaSchema.setPart(1);
                pdfaSchema.setConformance("B");
                var dublinCoreSchema = pdfMeta.createAndAddDublinCoreSchema();
                dublinCoreSchema.setTitle(title);
                dublinCoreSchema.addCreator(creator);
                dublinCoreSchema.setDescription(subject);
                var basicSchema = pdfMeta.createAndAddXMPBasicSchema();
                Calendar creationDate = Calendar.getInstance();
                basicSchema.setCreateDate(creationDate);
                basicSchema.setModifyDate(creationDate);
                basicSchema.setMetadataDate(creationDate);
                basicSchema.setCreatorTool(creator);
                try (var cosXMPStream = cosStream.createOutputStream()) {
                    new XmpSerializer().serialize(pdfMeta, cosXMPStream, true);
                    cosStream.setName(COSName.TYPE, "Metadata");
                    cosStream.setName(COSName.SUBTYPE, "XML");
                    pdfMerger.setDestinationMetadata(new PDMetadata(cosStream));
                }
            }
        });
    }
}
