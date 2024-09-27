package com.tugalsan.api.file.pdf.pdfbox3.server;

import org.apache.pdfbox.pdmodel.PDDocument;

public class TS_FilePdfBox3UtilsEncrypt {

    public static boolean isEncrypted(PDDocument doc) {
        return doc.isEncrypted();
    }
}
