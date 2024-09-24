package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.unsafe.client.*;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.Sides;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;

/**
 * Examples of various different ways to print PDFs using PDFBox.
 */
@Deprecated
public final class TS_FilePdfBox3UtilsPrint {

    private TS_FilePdfBox3UtilsPrint() {
    }

    /**
     * Entry point.
     */
    public static void main(String[] args) {
        TGS_UnSafe.run(() -> {
            if (args.length != 1) {
                System.err.println("usage: java " + TS_FilePdfBox3UtilsPrint.class.getName() + " <input>");
                System.exit(1);
            }

            var filename = args[0];
            try ( var document = Loader.loadPDF(new RandomAccessReadBufferedFile(filename))) {
                // choose your printing method:
                print(document);
                //printWithAttributes(document);
                //printWithDialog(document);
                //printWithDialogAndAttributes(document);
                //printWithPaper(document);
            }
        });
    }

    /**
     * Prints the document at its actual size. This is the recommended way to
     * print.
     */
    private static void print(PDDocument document) {
        TGS_UnSafe.run(() -> {
            var job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));
            job.print();
        });
    }

    /**
     * Prints using custom PrintRequestAttribute values.
     */
    private static void printWithAttributes(PDDocument document) {
        TGS_UnSafe.run(() -> {
            var job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));

            PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(new PageRanges(1, 1)); // pages 1 to 1

            job.print(attr);
        });
    }

    /**
     * Prints with a print preview dialog.
     */
    private static void printWithDialog(PDDocument document) {
        TGS_UnSafe.run(() -> {
            var job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));

            if (job.printDialog()) {
                job.print();
            }
        });
    }

    /**
     * Prints with a print preview dialog and custom PrintRequestAttribute
     * values.
     */
    private static void printWithDialogAndAttributes(PDDocument document) {
        TGS_UnSafe.run(() -> {
            var job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));

            PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(new PageRanges(1, 1)); // pages 1 to 1

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

            if (job.printDialog(attr)) {
                job.print(attr);
            }
        });
    }

    /**
     * Prints using a custom page size and custom margins.
     */
    private static void printWithPaper(PDDocument document) {
        TGS_UnSafe.run(() -> {
            var job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));

            // define custom paper
            var paper = new Paper();
            paper.setSize(306, 396); // 1/72 inch
            paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight()); // no margins

            // custom page format
            var pageFormat = new PageFormat();
            pageFormat.setPaper(paper);

            // override the page format
            var book = new Book();
            // append all pages
            book.append(new PDFPrintable(document), pageFormat, document.getNumberOfPages());
            job.setPageable(book);

            job.print();
        });
    }
}
