package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.function.client.TGS_FuncEffectivelyFinal;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

@Deprecated //TODO Migration from TS_FilePdfItext to here
public class TS_FilePdfBox3 {

    final private static TS_Log d = TS_Log.of(TS_FilePdfBox3.class);

    public Path getFile() {
        return file;
    }
    private final Path file;

    public PDDocument getWriter() {
        return document;
    }

    public PDDocument getDocument() {
        return document;
    }
    private PDDocument document;

    public TS_FilePdfBox3(Path file) {
        this.file = file;
    }

    public void createNewPage(int pageSizeAX, boolean landscape, Integer marginLeft0, Integer marginRight0, Integer marginTop0, Integer marginBottom0) {
        TGS_UnSafe.run(() -> {
            d.ci("createNewPage");
            if (document == null) {
                document = new PDDocument();
            }
//            var marginLeft = marginLeft0 == null ? 50 : marginLeft0;
//            var marginRight = marginRight0 == null ? 10 : marginRight0;
//            var marginTop = marginTop0 == null ? 10 : marginTop0;
//            var marginBottom = marginBottom0 == null ? 10 : marginBottom0;
            var page = TGS_FuncEffectivelyFinal.of(PDPage.class)
                    .anoint(val -> new PDPage(PDRectangle.A4))
                    .anointIf(val -> pageSizeAX == 0, val -> new PDPage(PDRectangle.A0))
                    .anointIf(val -> pageSizeAX == 1, val -> new PDPage(PDRectangle.A1))
                    .anointIf(val -> pageSizeAX == 2, val -> new PDPage(PDRectangle.A2))
                    .anointIf(val -> pageSizeAX == 3, val -> new PDPage(PDRectangle.A3))
                    .anointIf(val -> pageSizeAX == 4, val -> new PDPage(PDRectangle.A4))
                    .anointIf(val -> pageSizeAX == 5, val -> new PDPage(PDRectangle.A5))
                    .anointIf(val -> pageSizeAX == 6, val -> new PDPage(PDRectangle.A6))
                    .coronate();
            if (landscape) {
                page.setRotation(90);
            }
            document.addPage(page);
        });
    }
    
    //TODO http://www.java2s.com/example/java-api/org/apache/pdfbox/pdmodel/pdpage/getmediabox-0-2.html
    //TODO http://www.java2s.com/example/java-api/org/apache/pdfbox/pdmodel/pdpage/getmediabox-0-2.html
    //TODO https://stackoverflow.com/questions/14686013/pdfbox-wrap-text
    
}
