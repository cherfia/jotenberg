package io.bitizens.chromium;

import io.bitizens.common.FileInput;
import io.bitizens.internal.Multipart;

import java.nio.file.Path;

/**
 * HtmlConversion prints a PDF of an HTML page, sent as {@code index.html} with the assets it references.
 */
public final class HtmlConversion extends PdfConversion<HtmlConversion> {
    private final PageSource source;

    private HtmlConversion(FileInput index) {
        this.source = PageSource.html(index);
    }

    /**
     * @param index The page, uploaded as {@code index.html} whatever its name.
     * @return The conversion.
     */
    public static HtmlConversion of(Path index) {
        return new HtmlConversion(FileInput.of(index));
    }

    /**
     * @param index The page, uploaded as {@code index.html} whatever its name.
     * @return The conversion.
     */
    public static HtmlConversion of(FileInput index) {
        return new HtmlConversion(index);
    }

    /** @param asset An image, stylesheet, font or script the page references by name. @return This conversion. */
    public HtmlConversion asset(Path asset) {
        source.asset(FileInput.of(asset));
        return this;
    }

    /** @param asset An image, stylesheet, font or script the page references by name. @return This conversion. */
    public HtmlConversion asset(FileInput asset) {
        source.asset(asset);
        return this;
    }

    void addTo(Multipart form) {
        source.addTo(form);
        addFieldsTo(form);
    }
}
