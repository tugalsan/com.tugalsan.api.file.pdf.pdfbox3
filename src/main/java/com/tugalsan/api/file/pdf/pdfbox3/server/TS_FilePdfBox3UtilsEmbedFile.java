package com.tugalsan.api.file.pdf.pdfbox3.server;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.filespecification.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.interactive.annotation.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.*;
import java.nio.file.Path;

public class TS_FilePdfBox3UtilsEmbedFile {

    public static TGS_UnionExcuseVoid createEmbeddedFileExample(Path destFile) {
        return TGS_UnSafe.call(() -> {
            try (var doc = new PDDocument()) {
                var page = new PDPage();
                doc.addPage(page);
                var font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                try (var contentStream = new PDPageContentStream(doc, page)) {
                    contentStream.beginText();
                    contentStream.setFont(font, 12);
                    contentStream.newLineAtOffset(100, 700);
                    contentStream.showText("Go to Document->File Attachments to View Embedded Files");
                    contentStream.endText();
                }

                //embedded files are stored in a named tree
                var efTree = new PDEmbeddedFilesNameTreeNode();

                //first create the file specification, which holds the embedded file
                var fs = new PDComplexFileSpecification();

                // use both methods for backwards, cross-platform and cross-language compatibility.
                fs.setFile("Test.txt");
                fs.setFileUnicode("Test.txt");

                //create a dummy file stream, this would probably normally be a FileInputStream
                var data = "This is the contents of the embedded file".getBytes(StandardCharsets.ISO_8859_1);
                var fakeFile = new ByteArrayInputStream(data);
                var ef = new PDEmbeddedFile(doc, fakeFile);
                //now lets some of the optional parameters
                ef.setSubtype("text/plain");
                ef.setSize(data.length);
                ef.setCreationDate(new GregorianCalendar());

                // use both methods for backwards, cross-platform and cross-language compatibility.
                fs.setEmbeddedFile(ef);
                fs.setEmbeddedFileUnicode(ef);

                // create a new tree node and add the embedded file
                var treeNode = new PDEmbeddedFilesNameTreeNode();
                treeNode.setNames(Collections.singletonMap("My first attachment", fs));
                // add the new node as kid to the root node
                List<PDEmbeddedFilesNameTreeNode> kids = TGS_ListUtils.of();
                kids.add(treeNode);
                efTree.setKids(kids);
                // add the tree to the document catalog
                var names = new PDDocumentNameDictionary(doc.getDocumentCatalog());
                names.setEmbeddedFiles(efTree);
                doc.getDocumentCatalog().setNames(names);

                // show attachments panel in some viewers 
                doc.getDocumentCatalog().setPageMode(PageMode.USE_ATTACHMENTS);

                doc.save(destFile.toFile());
                return TGS_UnionExcuseVoid.ofVoid();
            }
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

    public static TGS_UnionExcuseVoid extractEmbeddedFiles(Path pdfSrcFile) {
        return TS_FilePdfBox3UtilsLoad.use_basic(pdfSrcFile, doc -> {
            var namesDictionary = new PDDocumentNameDictionary(doc.getDocumentCatalog());
            var efTree = namesDictionary.getEmbeddedFiles();
            if (efTree != null) {
                extractFilesFromEFTree(efTree, pdfSrcFile.getParent());
            }
            for (var page : doc.getPages()) {
                extractFilesFromPage(page, pdfSrcFile.getParent());
            }
        });
    }

    private static void extractFilesFromPage(PDPage page, Path targetFolder) {
        TGS_UnSafe.run(() -> {
            for (var annotation : page.getAnnotations()) {
                if (annotation instanceof PDAnnotationFileAttachment annotationFileAttachment) {
                    var fileSpec = annotationFileAttachment.getFile();
                    if (fileSpec instanceof PDComplexFileSpecification complexFileSpec) {
                        var embeddedFile = getEmbeddedFile(complexFileSpec);
                        if (embeddedFile != null) {
                            extractFile(targetFolder, complexFileSpec.getFilename(), embeddedFile);
                        }
                    }
                }
            }
        });
    }

    private static void extractFilesFromEFTree(PDEmbeddedFilesNameTreeNode efTree, Path targetFolder) {
        TGS_UnSafe.run(() -> {
            var names = efTree.getNames();
            if (names != null) {
                extractFiles(names, targetFolder);
            } else {
                var kids = efTree.getKids();
                for (var node : kids) {
                    names = node.getNames();
                    extractFiles(names, targetFolder);
                }
            }
        });
    }

    private static void extractFiles(Map<String, PDComplexFileSpecification> names, Path targetFolder) {
        names.entrySet().stream()
                .map(entry -> entry.getValue())
                .forEachOrdered(fileSpec -> {
                    var embeddedFile = getEmbeddedFile(fileSpec);
                    if (embeddedFile != null) {
                        extractFile(targetFolder, fileSpec.getFilename(), embeddedFile);
                    }
                });
    }

    private static void extractFile(Path targetFolder, String filename, PDEmbeddedFile embeddedFile) {
        TGS_UnSafe.run(() -> {
            var file = targetFolder.resolve(filename);
            try (var fos = new FileOutputStream(file.toFile())) {
                fos.write(embeddedFile.toByteArray());
            }
        });
    }

    private static PDEmbeddedFile getEmbeddedFile(PDComplexFileSpecification fileSpec) {
        // search for the first available alternative of the embedded file
        PDEmbeddedFile embeddedFile = null;
        if (fileSpec != null) {
            embeddedFile = fileSpec.getEmbeddedFileUnicode();
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFileDos();
            }
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFileMac();
            }
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFileUnix();
            }
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFile();
            }
        }
        return embeddedFile;
    }
}
