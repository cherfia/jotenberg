package io.bitizens.chromium;

import io.bitizens.common.FileInput;
import io.bitizens.internal.Multipart;

import java.nio.file.Path;

/**
 * MarkdownScreenshot captures an image of markdown files, placed in an {@code index.html} with {@code {{ toHTML "file.md" }}}.
 */
public final class MarkdownScreenshot extends Screenshot<MarkdownScreenshot> {
    private final PageSource source;

    private MarkdownScreenshot(FileInput index) {
        this.source = PageSource.html(index);
    }

    /**
     * @param index     The template, uploaded as {@code index.html} whatever its name.
     * @param markdowns The markdown files, uploaded under their names.
     * @return The screenshot.
     */
    public static MarkdownScreenshot of(Path index, Path... markdowns) {
        MarkdownScreenshot request = new MarkdownScreenshot(FileInput.of(index));
        for (Path markdown : markdowns) {
            request.markdown(FileInput.of(markdown));
        }
        return request;
    }

    /**
     * @param index The template, uploaded as {@code index.html} whatever its name.
     * @return The screenshot; add markdown files with {@link #markdown}.
     */
    public static MarkdownScreenshot of(FileInput index) {
        return new MarkdownScreenshot(index);
    }

    /** @param markdown A markdown file the template places by name. @return This screenshot. */
    public MarkdownScreenshot markdown(FileInput markdown) {
        source.markdown(markdown);
        return this;
    }

    /** @param asset An image, stylesheet, font or script the page references by name. @return This screenshot. */
    public MarkdownScreenshot asset(Path asset) {
        source.asset(FileInput.of(asset));
        return this;
    }

    /** @param asset An image, stylesheet, font or script the page references by name. @return This screenshot. */
    public MarkdownScreenshot asset(FileInput asset) {
        source.asset(asset);
        return this;
    }

    void addTo(Multipart form) {
        source.checkMarkdown();
        source.addTo(form);
        addFieldsTo(form);
    }
}
