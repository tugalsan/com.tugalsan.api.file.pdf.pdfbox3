package com.tugalsan.api.file.pdf.pdfbox3.server.tut;

import com.tugalsan.api.unsafe.client.*;
import java.io.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.geom.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

/**
 * Example showing custom rendering by subclassing PageDrawer.
 *
 * <p>
 * If you want to do custom graphics processing rather than Graphics2D
 * rendering, then you should subclass {@link PDFGraphicsStreamEngine} instead.
 * Subclassing PageDrawer is only suitable for cases where the goal is to render
 * onto a Graphics2D surface.
 *
 * @author John Hewson
 */
public class CustomPageDrawer {

    public static void main(String[] args) {
        TGS_UnSafe.run(() -> {
            var file = new File("src/main/resources/org/apache/pdfbox/examples/rendering/",
                    "custom-render-demo.pdf");

            try ( var doc = Loader.loadPDF(new RandomAccessReadBufferedFile(file))) {
                var renderer = new MyPDFRenderer(doc);
                var image = renderer.renderImage(0);
                ImageIO.write(image, "PNG", new File("custom-render.png"));
            }
        });
    }

    /**
     * Example PDFRenderer subclass, uses MyPageDrawer for custom rendering.
     */
    private static class MyPDFRenderer extends PDFRenderer {

        MyPDFRenderer(PDDocument document) {
            super(document);
        }

        @Override
        protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException {
            return new MyPageDrawer(parameters);
        }
    }

    /**
     * Example PageDrawer subclass with custom rendering.
     */
    private static class MyPageDrawer extends PageDrawer {

        MyPageDrawer(PageDrawerParameters parameters) throws IOException {
            super(parameters);
        }

        /**
         * Color replacement.
         */
        @Override
        protected Paint getPaint(PDColor color) {
            return TGS_UnSafe.call(() -> {
                // if this is the non-stroking color, find red, ignoring alpha channel
                if (getGraphicsState().getNonStrokingColor() == color
                        && color.toRGB() == (Color.RED.getRGB() & 0x00FFFFFF)) {
                    // replace it with blue
                    return Color.BLUE;
                }
                return super.getPaint(color);
            });
        }

        /**
         * Glyph bounding boxes.
         */
        @Override
        protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, Vector displacement) {
            TGS_UnSafe.run(() -> {
                // draw glyph
                super.showGlyph(textRenderingMatrix, font, code, displacement);

                // bbox in EM -> user units
                Shape bbox = new Rectangle2D.Float(0, 0, font.getWidth(code) / 1000, 1);
                var at = textRenderingMatrix.createAffineTransform();
                bbox = at.createTransformedShape(bbox);

                // save
                var graphics = getGraphics();
                var color = graphics.getColor();
                var stroke = graphics.getStroke();
                var clip = graphics.getClip();

                // draw
                graphics.setClip(graphics.getDeviceConfiguration().getBounds());
                graphics.setColor(Color.RED);
                graphics.setStroke(new BasicStroke(.5f));
                graphics.draw(bbox);

                // restore
                graphics.setStroke(stroke);
                graphics.setColor(color);
                graphics.setClip(clip);
            });
        }

        /**
         * Filled path bounding boxes.
         */
        @Override
        public void fillPath(int windingRule) {
            TGS_UnSafe.run(() -> {
                // bbox in user units
                Shape bbox = getLinePath().getBounds2D();

                // draw path (note that getLinePath() is now reset)
                super.fillPath(windingRule);

                // save
                var graphics = getGraphics();
                var color = graphics.getColor();
                var stroke = graphics.getStroke();
                var clip = graphics.getClip();

                // draw
                graphics.setClip(graphics.getDeviceConfiguration().getBounds());
                graphics.setColor(Color.GREEN);
                graphics.setStroke(new BasicStroke(.5f));
                graphics.draw(bbox);

                // restore
                graphics.setStroke(stroke);
                graphics.setColor(color);
                graphics.setClip(clip);
            });
        }

        /**
         * Custom annotation rendering.
         */
        @Override
        public void showAnnotation(PDAnnotation annotation) {
            TGS_UnSafe.run(() -> {
                // save
                saveGraphicsState();

                // 35% alpha
                getGraphicsState().setNonStrokeAlphaConstant(0.35);
                super.showAnnotation(annotation);

                // restore
                restoreGraphicsState();
            });
        }
    }
}
