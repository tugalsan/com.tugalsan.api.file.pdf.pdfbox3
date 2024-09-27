package com.tugalsan.api.file.pdf.pdfbox3.server;

import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class TS_FilePdfBox3UtilsPageGet {

    public static TGS_UnionExcuse<Integer> count(Path pdfSrc) {
        return TS_FilePdfBox3UtilsDocument.call_randomAccess(pdfSrc, doc -> {
            return TGS_UnionExcuse.of(doc.getNumberOfPages());
        });
    }

    public static int count(PDDocument doc) {
        return doc.getDocumentCatalog().getPages().getCount();
    }

    public static Optional<PDPage> getPage(PDDocument doc, int pageIdx) {
        var count = count(doc);
        if (pageIdx >= 0 && pageIdx < count) {
            return Optional.of(doc.getDocumentCatalog().getPages().get(pageIdx));
        }
        return Optional.empty();
    }

    public static IntStream streamPageIdx(PDDocument doc, int... pageIdx_optional) {
        var count = count(doc);
        IntPredicate filter = filterIdx -> filterIdx >= 0 && filterIdx < count;
        return IntStream.range(0, count(doc)).filter(filter);
    }

    public static Stream<PDPage> streamPages(PDDocument doc, int... pageIdx_optional) {
        IntPredicate filter = filterIdx -> {
            if (pageIdx_optional == null || pageIdx_optional.length == 0) {//All
                return true;
            }
            return Arrays.stream(pageIdx_optional).filter(streamIdx -> streamIdx == filterIdx).findAny().isPresent();
        };
        return streamPageIdx(doc).filter(filter).mapToObj(pageIdx -> getPage(doc, pageIdx).orElseThrow());
    }

    public static List<PDPage> getPages(PDDocument doc, int... pageIdx_optional) {
        return TGS_StreamUtils.toLst(streamPages(doc, pageIdx_optional));
    }
}
