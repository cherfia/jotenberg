package io.bitizens.chromium;

import io.bitizens.core.Multipart;

/**
 * UrlConversion prints a PDF of a web page.
 */
public final class UrlConversion extends PdfConversion<UrlConversion> {
    private final PageSource source;

    private UrlConversion(String url) {
        this.source = PageSource.url(url);
    }

    /**
     * @param url The page to load, an {@code http} or {@code https} URL.
     * @return The conversion.
     */
    public static UrlConversion of(String url) {
        return new UrlConversion(url);
    }

    void addTo(Multipart form) {
        source.addTo(form);
        addFieldsTo(form);
    }
}
