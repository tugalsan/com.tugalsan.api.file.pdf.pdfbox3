package com.tugalsan.api.file.pdf.pdfbox3.server;

import org.apache.pdfbox.pdmodel.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class TS_FilePdfBox3UtilsText {

    final private static TS_Log d = TS_Log.of(TS_FilePdfBox3UtilsText.class);

    public static Path createPageText(Path path, String text) {
        return TGS_UnSafe.call(() -> {
            try (var document = new PDDocument();) {
//                var acroform = document.getDocumentCatalog().getAcroForm();
//                if (acroform != null) {
//                    acroform.setNeedAppearances(false);
//                    acroform.refreshAppearances();
//                }
                var page = new PDPage();
                document.addPage(page);
                try (var contentStream = new PDPageContentStream(document, page);) {
                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                    contentStream.newLineAtOffset(100, 700);
                    contentStream.showText(text);
                    contentStream.endText();
                }
                document.save(path.toFile());
                return path;
            }
        }, e -> {
            d.ce("createPageText", "failed", e.getMessage());
            return TGS_UnSafe.thrw(e);
        });
    }

}
