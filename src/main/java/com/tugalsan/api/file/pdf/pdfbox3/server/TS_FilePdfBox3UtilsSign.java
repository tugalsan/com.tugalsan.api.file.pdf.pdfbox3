package com.tugalsan.api.file.pdf.pdfbox3.server;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;

public class TS_FilePdfBox3UtilsSign {

    @Deprecated //NOT WORKIG (?)
    public static boolean isPDFSigned(Path filePath) throws IOException {
        try (var doc = Loader.loadPDF(new RandomAccessReadBufferedFile(filePath.toAbsolutePath().toString()))) {
            return !doc.getSignatureDictionaries().isEmpty();
        }
    }
}
