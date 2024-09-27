package com.tugalsan.api.file.pdf.pdfbox3.server;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class TS_FilePdfBox3UtilsPageSize {

    public static PDRectangle sizePage(PDPage page) {
        return page.getMediaBox();
    }

    public static PDRectangle sizeA(int from_A0_to_A6) {
        if (from_A0_to_A6 <= 0) {
            return PDRectangle.A0;
        }
        if (from_A0_to_A6 == 1) {
            return PDRectangle.A1;
        }
        if (from_A0_to_A6 == 2) {
            return PDRectangle.A2;
        }
        if (from_A0_to_A6 == 3) {
            return PDRectangle.A3;
        }
        if (from_A0_to_A6 == 4) {
            return PDRectangle.A4;
        }
        if (from_A0_to_A6 == 5) {
            return PDRectangle.A5;
        }
        return PDRectangle.A6;
    }

}
