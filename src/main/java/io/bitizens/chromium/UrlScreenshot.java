package io.bitizens.chromium;

import io.bitizens.core.Multipart;

/**
 * UrlScreenshot captures an image of a web page.
 */
public final class UrlScreenshot extends Screenshot<UrlScreenshot> {
    private final PageSource source;

    private UrlScreenshot(String url) {
        this.source = PageSource.url(url);
    }

    /**
     * @param url The page to load, an {@code http} or {@code https} URL.
     * @return The screenshot.
     */
    public static UrlScreenshot of(String url) {
        return new UrlScreenshot(url);
    }

    void addTo(Multipart form) {
        source.addTo(form);
        addFieldsTo(form);
    }
}
