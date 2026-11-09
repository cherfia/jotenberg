package io.bitizens.chromium;

import io.bitizens.common.FileInput;
import io.bitizens.internal.Multipart;

import java.nio.file.Path;

/**
 * HtmlScreenshot captures an image of an HTML page, sent as {@code index.html} with the assets it references.
 */
public final class HtmlScreenshot extends Screenshot<HtmlScreenshot> {
    private final PageSource source;

    private HtmlScreenshot(FileInput index) {
        this.source = PageSource.html(index);
    }

    /**
     * @param index The page, uploaded as {@code index.html} whatever its name.
     * @return The screenshot.
     */
    public static HtmlScreenshot of(Path index) {
        return new HtmlScreenshot(FileInput.of(index));
    }

    /**
     * @param index The page, uploaded as {@code index.html} whatever its name.
     * @return The screenshot.
     */
    public static HtmlScreenshot of(FileInput index) {
        return new HtmlScreenshot(index);
    }

    /** @param asset An image, stylesheet, font or script the page references by name. @return This screenshot. */
    public HtmlScreenshot asset(Path asset) {
        source.asset(FileInput.of(asset));
        return this;
    }

    /** @param asset An image, stylesheet, font or script the page references by name. @return This screenshot. */
    public HtmlScreenshot asset(FileInput asset) {
        source.asset(asset);
        return this;
    }

    void addTo(Multipart form) {
        source.addTo(form);
        addFieldsTo(form);
    }
}
