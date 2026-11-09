package io.bitizens.chromium;

import io.bitizens.common.FileInput;
import io.bitizens.internal.Multipart;

import java.nio.file.Path;

/**
 * MarkdownConversion prints a PDF of markdown files, placed in an {@code index.html} with {@code {{ toHTML "file.md" }}}.
 */
public final class MarkdownConversion extends PdfConversion<MarkdownConversion> {
    private final PageSource source;

    private MarkdownConversion(FileInput index) {
        this.source = PageSource.html(index);
    }

    /**
     * @param index     The template, uploaded as {@code index.html} whatever its name.
     * @param markdowns The markdown files, uploaded under their names.
     * @return The conversion.
     */
    public static MarkdownConversion of(Path index, Path... markdowns) {
        MarkdownConversion request = new MarkdownConversion(FileInput.of(index));
        for (Path markdown : markdowns) {
            request.markdown(FileInput.of(markdown));
        }
        return request;
    }

    /**
     * @param index The template, uploaded as {@code index.html} whatever its name.
     * @return The conversion; add markdown files with {@link #markdown}.
     */
    public static MarkdownConversion of(FileInput index) {
        return new MarkdownConversion(index);
    }

    /** @param markdown A markdown file the template places by name. @return This conversion. */
    public MarkdownConversion markdown(FileInput markdown) {
        source.markdown(markdown);
        return this;
    }

    /** @param asset An image, stylesheet, font or script the page references by name. @return This conversion. */
    public MarkdownConversion asset(Path asset) {
        source.asset(FileInput.of(asset));
        return this;
    }

    /** @param asset An image, stylesheet, font or script the page references by name. @return This conversion. */
    public MarkdownConversion asset(FileInput asset) {
        source.asset(asset);
        return this;
    }

    void addTo(Multipart form) {
        source.checkMarkdown();
        source.addTo(form);
        addFieldsTo(form);
    }
}
