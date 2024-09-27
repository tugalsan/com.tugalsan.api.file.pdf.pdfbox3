package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.tuple.client.TGS_Tuple2;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.*;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.Sides;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;

public final class TS_FilePdfBox3UtilsPrint {

    public static TGS_UnionExcuseVoid print(PDDocument document) {
        return print(document, false, false, null, null);
    }

    public static TGS_UnionExcuseVoid print(PDDocument document, boolean showDialog) {
        return print(document, showDialog, false, null, null);
    }

    public static TGS_UnionExcuseVoid print(PDDocument document, boolean showDialog, boolean doubleSidedIfPossible, TGS_Tuple2<Integer, Integer> pageIdxRange, TGS_Tuple2<Integer, Integer> customPaperSize) {
        return TGS_UnSafe.call(() -> {
            var job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));
            if (customPaperSize != null && customPaperSize.value0 != null && customPaperSize.value1 != null) {
                var paper = new Paper();
                paper.setSize(customPaperSize.value0, customPaperSize.value1);
                paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight());
                var pageFormat = new PageFormat();
                pageFormat.setPaper(paper);
                var book = new Book();
                book.append(new PDFPrintable(document), pageFormat, document.getNumberOfPages());
                job.setPageable(book);
            }
            var attr = new HashPrintRequestAttributeSet();
            if (pageIdxRange != null && pageIdxRange.value0 != null && pageIdxRange.value1 != null) {
                attr.add(new PageRanges(pageIdxRange.value0, pageIdxRange.value1));
            }
            var vp = document.getDocumentCatalog().getViewerPreferences();
            if (vp != null && vp.getDuplex() != null) {
                var dp = vp.getDuplex();
                if (PDViewerPreferences.DUPLEX.DuplexFlipLongEdge.toString().equals(dp)) {
                    attr.add(Sides.TWO_SIDED_LONG_EDGE);
                } else if (PDViewerPreferences.DUPLEX.DuplexFlipShortEdge.toString().equals(dp)) {
                    attr.add(Sides.TWO_SIDED_SHORT_EDGE);
                } else if (PDViewerPreferences.DUPLEX.Simplex.toString().equals(dp)) {
                    attr.add(Sides.ONE_SIDED);
                }
            }
            if (showDialog) {
                if (job.printDialog()) {
                    job.print();
                }
            } else {
                job.print();
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }
}
