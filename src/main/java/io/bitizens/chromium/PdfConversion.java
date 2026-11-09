package io.bitizens.chromium;

import io.bitizens.common.FileInput;
import io.bitizens.common.PaperSize;
import io.bitizens.internal.Lengths;
import io.bitizens.internal.Multipart;
import io.bitizens.pdf.Stage;
import io.bitizens.pdf.StageUploads;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * PdfConversion holds the fields of the Chromium routes that print a PDF: the page layout, the header and footer,
 * the native fields, and the stages to run on the printed PDF.
 *
 * @param <T> The concrete conversion type, returned by the fluent setters.
 */
public abstract class PdfConversion<T extends PdfConversion<T>> extends ChromiumRequest<T> {
    private final List<Stage> stages = new ArrayList<>();
    private FileInput header;
    private FileInput footer;

    /** @param paperSize A named paper size; Letter by default. @return This conversion. */
    public T paperSize(PaperSize paperSize) {
        return set("paperSize", paperSize.value());
    }

    /** @param width Overrides the width the paper size sets, such as {@code "210mm"}. @return This conversion. */
    public T paperWidth(String width) {
        return set("paperWidth", length("paperWidth", width));
    }

    /** @param height Overrides the height the paper size sets, such as {@code "297mm"}. @return This conversion. */
    public T paperHeight(String height) {
        return set("paperHeight", length("paperHeight", height));
    }

    /**
     * Sets every margin; 0.39in by default.
     *
     * @param margin A length such as {@code "1cm"}.
     * @return This conversion.
     */
    public T margins(String margin) {
        return margins(margin, margin, margin, margin);
    }

    /**
     * @param top    The top margin, where the header prints.
     * @param right  The right margin.
     * @param bottom The bottom margin, where the footer prints.
     * @param left   The left margin.
     * @return This conversion.
     */
    public T margins(String top, String right, String bottom, String left) {
        set("marginTop", length("marginTop", top));
        set("marginRight", length("marginRight", right));
        set("marginBottom", length("marginBottom", bottom));
        return set("marginLeft", length("marginLeft", left));
    }

    /** @param prefer Uses the size the page's CSS {@code @page} rule sets. @return This conversion. */
    public T preferCssPageSize(boolean prefer) {
        return set("preferCssPageSize", prefer);
    }

    /** @param print Prints the page's own background, set on {@code html} and {@code body}. @return This conversion. */
    public T printBackground(boolean print) {
        return set("printBackground", print);
    }

    /** @param landscape Turns the paper sideways. @return This conversion. */
    public T landscape(boolean landscape) {
        return set("landscape", landscape);
    }

    /** @param scale From 0.1 to 2; 1 by default. @return This conversion. */
    public T scale(double scale) {
        if (scale < 0.1 || scale > 2) {
            throw new IllegalArgumentException("scale runs from 0.1 to 2, got " + scale);
        }
        return set("scale", scale);
    }

    /** @param singlePage Prints the whole page on one page, as tall as the content. @return This conversion. */
    public T singlePage(boolean singlePage) {
        return set("singlePage", singlePage);
    }

    /**
     * @param pages Pages to print, in Chromium's syntax: {@code "1-5, 8, 11-13"}. To pick pages after printing, use a
     *              {@link Stage#select} stage.
     * @return This conversion.
     */
    public T nativePages(String pages) {
        return set("nativePages", pages);
    }

    /** @param bookmarks Builds bookmarks from the headings; implies a tagged PDF. @return This conversion. */
    public T nativeBookmarks(boolean bookmarks) {
        return set("nativeBookmarks", bookmarks);
    }

    /** @param tagged Writes a tagged PDF, whose structure screen readers follow. @return This conversion. */
    public T nativeTaggedPdf(boolean tagged) {
        return set("nativeTaggedPdf", tagged);
    }

    /**
     * @param header An HTML document printed at the top of each page, uploaded as {@code header.html}. Elements with
     *               the class {@code pageNumber}, {@code totalPages}, {@code date}, {@code title} or {@code url}
     *               receive that value.
     * @return This conversion.
     */
    public T header(Path header) {
        this.header = FileInput.of(header);
        return self();
    }

    /** @param header An HTML document printed at the top of each page. @return This conversion. */
    public T header(FileInput header) {
        this.header = header;
        return self();
    }

    /** @param footer An HTML document printed at the bottom of each page, uploaded as {@code footer.html}. @return This conversion. */
    public T footer(Path footer) {
        this.footer = FileInput.of(footer);
        return self();
    }

    /** @param footer An HTML document printed at the bottom of each page. @return This conversion. */
    public T footer(FileInput footer) {
        this.footer = footer;
        return self();
    }

    /**
     * Adds stages to run on the printed PDF, in order: merge, stamp, encrypt, sign, PDF/A, and more.
     *
     * @param stages The stages.
     * @return This conversion.
     */
    public T stages(Stage... stages) {
        this.stages.addAll(List.of(stages));
        return self();
    }

    @Override
    void addFieldsTo(Multipart form) {
        if (header != null) {
            header.withName("header.html").addTo(form, "files");
        }
        if (footer != null) {
            footer.withName("footer.html").addTo(form, "files");
        }
        super.addFieldsTo(form);
        StageUploads.addStagesTo(form, stages);
    }

    private static String length(String field, String value) {
        return Lengths.check(field, value);
    }
}
