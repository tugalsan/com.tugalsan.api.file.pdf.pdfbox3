/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tugalsan.api.file.pdf.pdfbox3.server.tut;

import com.tugalsan.api.unsafe.client.*;
import java.io.File;
import java.io.IOException;
import java.awt.geom.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

/**
 * Example of a custom PDFGraphicsStreamEngine subclass. Allows text and
 * graphics to be processed in a custom manner. This example simply prints the
 * operations to stdout.
 *
 * <p>
 * See {@link PDFStreamEngine} for further methods which may be overridden.
 *
 * @author John Hewson
 */
public class CustomGraphicsStreamEngine extends PDFGraphicsStreamEngine {

    /**
     * Constructor.
     *
     * @param page PDF Page
     */
    protected CustomGraphicsStreamEngine(PDPage page) {
        super(page);
    }

    public static void main(String[] args) {
        TGS_UnSafe.run(() -> {
            var file = new File("src/main/resources/org/apache/pdfbox/examples/rendering/",
                    "custom-render-demo.pdf");

            try ( var doc = Loader.loadPDF(new RandomAccessReadBufferedFile(file))) {
                var page = doc.getPage(0);
                var engine = new CustomGraphicsStreamEngine(page);
                engine.run();
            }
        });
    }

    /**
     * Runs the engine on the current page.
     *
     * @throws IOException If there is an IO error while drawing the page.
     */
    public void run() {
        TGS_UnSafe.run(() -> {
            processPage(getPage());
            for (var annotation : getPage().getAnnotations()) {
                showAnnotation(annotation);
            }
        });
    }

    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
        System.out.printf("appendRectangle %.2f %.2f, %.2f %.2f, %.2f %.2f, %.2f %.2f%n",
                p0.getX(), p0.getY(), p1.getX(), p1.getY(),
                p2.getX(), p2.getY(), p3.getX(), p3.getY());
    }

    @Override
    public void drawImage(PDImage pdImage) {
        System.out.println("drawImage");
    }

    @Override
    public void clip(int windingRule) {
        System.out.println("clip");
    }

    @Override
    public void moveTo(float x, float y) {
        System.out.printf("moveTo %.2f %.2f%n", x, y);
    }

    @Override
    public void lineTo(float x, float y) {
        System.out.printf("lineTo %.2f %.2f%n", x, y);
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        System.out.printf("curveTo %.2f %.2f, %.2f %.2f, %.2f %.2f%n", x1, y1, x2, y2, x3, y3);
    }

    @Override
    public Point2D getCurrentPoint() {
        // if you want to build paths, you'll need to keep track of this like PageDrawer does
        return new Point2D.Float(0, 0);
    }

    @Override
    public void closePath() {
        System.out.println("closePath");
    }

    @Override
    public void endPath() {
        System.out.println("endPath");
    }

    @Override
    public void strokePath() {
        System.out.println("strokePath");
    }

    @Override
    public void fillPath(int windingRule) {
        System.out.println("fillPath");
    }

    @Override
    public void fillAndStrokePath(int windingRule) {
        System.out.println("fillAndStrokePath");
    }

    @Override
    public void shadingFill(COSName shadingName) {
        System.out.println("shadingFill " + shadingName.toString());
    }

    /**
     * Overridden from PDFStreamEngine.
     */
    @Override
    public void showTextString(byte[] string) {
        TGS_UnSafe.run(() -> {
            System.out.print("showTextString \"");
            super.showTextString(string);
            System.out.println("\"");
        });
    }

    /**
     * Overridden from PDFStreamEngine.
     */
    @Override
    public void showTextStrings(COSArray array) {
        TGS_UnSafe.run(() -> {
            System.out.print("showTextStrings \"");
            super.showTextStrings(array);
            System.out.println("\"");
        });
    }

    /**
     * Overridden from PDFStreamEngine.
     */
    @Override
    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, Vector displacement) {
        TGS_UnSafe.run(() -> {
            System.out.print("showGlyph " + code);
            super.showGlyph(textRenderingMatrix, font, code, displacement);
        });
    }

// NOTE: there are may more methods in PDFStreamEngine which can be overridden here too.
}
