package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.xml.XmpSerializer;

public class TS_FilePdfBox3UtilsInfo {

    public String getCreator(PDDocument doc) {
        return doc.getDocumentInformation().getCreator();
    }

    public String getTitle(PDDocument doc) {
        return doc.getDocumentInformation().getTitle();
    }

    public String getSubject(PDDocument doc) {
        return doc.getDocumentInformation().getSubject();
    }

    public static TGS_UnionExcuseVoid set(PDDocument doc, String title, String producer_creator, String subject_and_description_and_keywords) {
        return TGS_UnSafe.call(() -> {
            {
                var info = doc.getDocumentInformation();
                set(info, title, producer_creator, subject_and_description_and_keywords);
            }
            {
                var xmp = xmp(title, producer_creator, subject_and_description_and_keywords);
                try (var baos = new ByteArrayOutputStream()) {
                    new XmpSerializer().serialize(xmp, baos, false);
                    var metadataStream = new PDMetadata(doc);
                    doc.getDocumentCatalog().setMetadata(metadataStream);
                    metadataStream.importXMPMetadata(baos.toByteArray());
                }
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

    public static TGS_UnionExcuseVoid set(PDFMergerUtility pdfMerger, COSStream cos, String title, String producer_creator, String subject_and_description_and_keywords) {
        return TGS_UnSafe.call(() -> {
            {
                var info = new PDDocumentInformation();
                set(info, title, producer_creator, subject_and_description_and_keywords);
                pdfMerger.setDestinationDocumentInformation(info);
            }
            {
                var xmp = xmp(title, producer_creator, subject_and_description_and_keywords);
                try (var cos_os = cos.createOutputStream()) {
                    new XmpSerializer().serialize(xmp, cos_os, true);
                    cos.setName(COSName.TYPE, "Metadata");
                    cos.setName(COSName.SUBTYPE, "XML");
                    pdfMerger.setDestinationMetadata(new PDMetadata(cos));
                }
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

    private static void set(PDDocumentInformation info, String title, String producer_creator, String subject_and_description_and_keywords) {
        info.setTitle(title);
        info.setCreator(producer_creator);
        info.setSubject(subject_and_description_and_keywords);
    }

    private static XMPMetadata xmp(String title, String producer_creator, String subject_and_description_and_keywords) {
        return TGS_UnSafe.call(() -> {
            var creationDate = Calendar.getInstance();
            var xmp = XMPMetadata.createXMPMetadata();
            {
                var schemaAdobe = xmp.createAndAddAdobePDFSchema();
                schemaAdobe.setProducer(producer_creator);
                schemaAdobe.setKeywords(subject_and_description_and_keywords);
            }
            {
                var xmpId = xmp.createAndAddPDFAIdentificationSchema();
                xmpId.setPart(1);
                xmpId.setConformance("B");
            }
            {
                var xmpDublin = xmp.createAndAddDublinCoreSchema();
                xmpDublin.setTitle(title);
                xmpDublin.addCreator(producer_creator);
                xmpDublin.setDescription(subject_and_description_and_keywords);
            }
            {
                var xmpBasic = xmp.createAndAddXMPBasicSchema();
                xmpBasic.setCreateDate(creationDate);
                xmpBasic.setModifyDate(creationDate);
                xmpBasic.setMetadataDate(creationDate);
                xmpBasic.setCreatorTool(producer_creator);
            }
            return xmp;
        });
    }
}
