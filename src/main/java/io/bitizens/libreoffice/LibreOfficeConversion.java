package io.bitizens.libreoffice;

import io.bitizens.common.DeliveredRequest;
import io.bitizens.common.FileInput;
import io.bitizens.common.PaperSize;
import io.bitizens.internal.Lengths;
import io.bitizens.internal.Multipart;
import io.bitizens.pdf.ConformanceProfile;
import io.bitizens.pdf.Stage;
import io.bitizens.pdf.StageUploads;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * LibreOfficeConversion converts office documents to PDF. Gotenberg names each PDF after its document,
 * {@code report.docx.pdf}; several documents answer a ZIP, unless a {@link Stage#merge()} stage joins them.
 * <p>
 * A field whose name starts with {@code native} asks LibreOffice to do the work while it exports; a stage does it
 * afterwards.
 *
 * @see <a href="https://gotenberg.dev/docs/libreoffice/convert">Gotenberg – LibreOffice</a>
 */
public final class LibreOfficeConversion extends DeliveredRequest<LibreOfficeConversion> {
    private final List<FileInput> documents = new ArrayList<>();
    private final Map<String, String> fields = new LinkedHashMap<>();
    private final List<Stage> stages = new ArrayList<>();

    private LibreOfficeConversion() {
    }

    /**
     * @param documents Documents on disk, uploaded under their names.
     * @return The conversion.
     */
    public static LibreOfficeConversion of(Path... documents) {
        LibreOfficeConversion conversion = new LibreOfficeConversion();
        for (Path document : documents) {
            conversion.document(FileInput.of(document));
        }
        return conversion;
    }

    /**
     * @param documents Documents, on disk or in memory, named with their extension.
     * @return The conversion.
     */
    public static LibreOfficeConversion of(FileInput... documents) {
        LibreOfficeConversion conversion = new LibreOfficeConversion();
        for (FileInput document : documents) {
            conversion.document(document);
        }
        return conversion;
    }

    /**
     * @param document A document whose extension LibreOffice converts, such as {@code .docx} or {@code .xlsx}.
     * @return This conversion.
     */
    public LibreOfficeConversion document(FileInput document) {
        if (!LibreOfficeExtensions.SUPPORTED.contains(document.extension())) {
            throw new IllegalArgumentException("LibreOffice doesn't convert ." + document.extension() + " files: "
                    + document.name());
        }
        documents.add(document);
        return this;
    }

    private LibreOfficeConversion set(String name, Object value) {
        if (value == null) {
            fields.remove(name);
        } else {
            fields.put(name, String.valueOf(value));
        }
        return this;
    }

    /** @param password Opens a password-protected document. @return This conversion. */
    public LibreOfficeConversion password(String password) {
        return set("password", password);
    }

    /** @param paperSize Every page's size; without it, each document keeps its own. @return This conversion. */
    public LibreOfficeConversion paperSize(PaperSize paperSize) {
        return set("paperSize", paperSize.value());
    }

    /** @param width Overrides the width, such as {@code "210mm"}; without a paper size, set the height too. @return This conversion. */
    public LibreOfficeConversion paperWidth(String width) {
        return set("paperWidth", width == null ? null : Lengths.check("paperWidth", width));
    }

    /** @param height Overrides the height, such as {@code "297mm"}. @return This conversion. */
    public LibreOfficeConversion paperHeight(String height) {
        return set("paperHeight", height == null ? null : Lengths.check("paperHeight", height));
    }

    /** @param landscape Turns the pages sideways. @return This conversion. */
    public LibreOfficeConversion landscape(boolean landscape) {
        return set("landscape", landscape);
    }

    /** @param pages Pages to export, in LibreOffice's syntax: {@code "1-4"} or {@code "1,3,5-7"}. @return This conversion. */
    public LibreOfficeConversion nativePages(String pages) {
        return set("nativePages", pages);
    }

    /** @param singlePage Puts each sheet of a spreadsheet on one page. @return This conversion. */
    public LibreOfficeConversion singlePage(boolean singlePage) {
        return set("singlePage", singlePage);
    }

    /** @param skip Drops the blank pages a text document inserts on its own. @return This conversion. */
    public LibreOfficeConversion skipEmptyPages(boolean skip) {
        return set("skipEmptyPages", skip);
    }

    /** @param update Updates the tables of contents and other indexes; true by default. @return This conversion. */
    public LibreOfficeConversion updateIndexes(boolean update) {
        return set("updateIndexes", update);
    }

    /** @param export Exports fields a reader can fill; {@code false} prints their values only. @return This conversion. */
    public LibreOfficeConversion exportFormFields(boolean export) {
        return set("exportFormFields", export);
    }

    /** @param allow Lets several form fields share a name. @return This conversion. */
    public LibreOfficeConversion allowDuplicateFieldNames(boolean allow) {
        return set("allowDuplicateFieldNames", allow);
    }

    /** @param bookmarks Exports bookmarks built from the headings; true by default. @return This conversion. */
    public LibreOfficeConversion nativeBookmarks(boolean bookmarks) {
        return set("nativeBookmarks", bookmarks);
    }

    /** @param export Also exports the document's own bookmarks as named destinations. @return This conversion. */
    public LibreOfficeConversion exportBookmarksToPdfDestination(boolean export) {
        return set("exportBookmarksToPdfDestination", export);
    }

    /** @param export Shows the placeholder fields' markings. @return This conversion. */
    public LibreOfficeConversion exportPlaceholders(boolean export) {
        return set("exportPlaceholders", export);
    }

    /** @param export Exports comments as PDF annotations. @return This conversion. */
    public LibreOfficeConversion exportNotes(boolean export) {
        return set("exportNotes", export);
    }

    /** @param export Prints comments in the page margin. @return This conversion. */
    public LibreOfficeConversion exportNotesInMargin(boolean export) {
        return set("exportNotesInMargin", export);
    }

    /** @param export Adds a presentation's notes pages. @return This conversion. */
    public LibreOfficeConversion exportNotesPages(boolean export) {
        return set("exportNotesPages", export);
    }

    /** @param export Exports the notes pages only, with {@link #exportNotesPages}. @return This conversion. */
    public LibreOfficeConversion exportOnlyNotesPages(boolean export) {
        return set("exportOnlyNotesPages", export);
    }

    /** @param export Includes the slides hidden from the slide show. @return This conversion. */
    public LibreOfficeConversion exportHiddenSlides(boolean export) {
        return set("exportHiddenSlides", export);
    }

    /** @param use Exports a presentation's slide transitions; true by default. @return This conversion. */
    public LibreOfficeConversion useTransitionEffects(boolean use) {
        return set("useTransitionEffects", use);
    }

    /** @param convert Points links to ODF files at PDF files of the same name. @return This conversion. */
    public LibreOfficeConversion convertOooTargetToPdfTarget(boolean convert) {
        return set("convertOooTargetToPdfTarget", convert);
    }

    /** @param relative Exports {@code file://} links relative to the document. @return This conversion. */
    public LibreOfficeConversion exportLinksRelativeFsys(boolean relative) {
        return set("exportLinksRelativeFsys", relative);
    }

    /** @param add Embeds the document in the PDF, re-saved as ODF. @return This conversion. */
    public LibreOfficeConversion addOriginalDocumentAsStream(boolean add) {
        return set("addOriginalDocumentAsStream", add);
    }

    /** @param pdfa Exports PDF/A directly. @return This conversion. */
    public LibreOfficeConversion nativePdfa(NativePdfa pdfa) {
        return set("nativePdfa", pdfa.value());
    }

    /**
     * Exports PDF/UA-1 directly. With {@link #nativePdfa}, the document needs a title, and PDF/A-4 is refused: pair
     * them in a {@link Stage#remediate(ConformanceProfile, ConformanceProfile)} stage instead.
     *
     * @return This conversion.
     */
    public LibreOfficeConversion nativePdfua() {
        return set("nativePdfua", ConformanceProfile.PDF_UA_1.value());
    }

    /** @param tagged Writes a tagged PDF without declaring PDF/UA. @return This conversion. */
    public LibreOfficeConversion nativeTaggedPdf(boolean tagged) {
        return set("nativeTaggedPdf", tagged);
    }

    /** @param quality The JPEG quality of the images, from 1 to 100; 90 by default. @return This conversion. */
    public LibreOfficeConversion nativeQuality(int quality) {
        if (quality < 1 || quality > 100) {
            throw new IllegalArgumentException("nativeQuality runs from 1 to 100, got " + quality);
        }
        return set("nativeQuality", quality);
    }

    /** @param lossless Compresses images losslessly instead of as JPEG. @return This conversion. */
    public LibreOfficeConversion nativeLosslessImageCompression(boolean lossless) {
        return set("nativeLosslessImageCompression", lossless);
    }

    /**
     * Downsamples images.
     *
     * @param dpi 75, 150, 300, 600 or 1200.
     * @return This conversion.
     */
    public LibreOfficeConversion nativeReduceImageResolution(int dpi) {
        if (!List.of(75, 150, 300, 600, 1200).contains(dpi)) {
            throw new IllegalArgumentException("nativeMaxImageResolution takes 75, 150, 300, 600 or 1200, got " + dpi);
        }
        set("nativeReduceImageResolution", true);
        return set("nativeMaxImageResolution", dpi);
    }

    /**
     * Draws text on every page.
     *
     * @param text The watermark text.
     * @return This conversion.
     */
    public LibreOfficeConversion nativeWatermarkText(String text) {
        fields.remove("nativeTiledWatermarkText");
        return set("nativeWatermarkText", text);
    }

    /**
     * Repeats text across every page, in a grid.
     *
     * @param text The watermark text.
     * @return This conversion.
     */
    public LibreOfficeConversion nativeTiledWatermarkText(String text) {
        fields.remove("nativeWatermarkText");
        return set("nativeTiledWatermarkText", text);
    }

    /** @param color {@code #rrggbb}. @return This conversion. */
    public LibreOfficeConversion nativeWatermarkColor(String color) {
        return set("nativeWatermarkColor", color);
    }

    /** @param size In points; without it, the text spans the page. @return This conversion. */
    public LibreOfficeConversion nativeWatermarkFontSize(int size) {
        return set("nativeWatermarkFontSize", size);
    }

    /** @param degrees Counter-clockwise. @return This conversion. */
    public LibreOfficeConversion nativeWatermarkRotation(double degrees) {
        return set("nativeWatermarkRotation", degrees);
    }

    /** @param font The font name; Helvetica by default. @return This conversion. */
    public LibreOfficeConversion nativeWatermarkFont(String font) {
        return set("nativeWatermarkFont", font);
    }

    /** @param view The side panel a viewer opens. @return This conversion. */
    public LibreOfficeConversion initialView(InitialView view) {
        return set("initialView", view.value());
    }

    /** @param page The page a viewer opens at. @return This conversion. */
    public LibreOfficeConversion initialPage(int page) {
        return set("initialPage", page);
    }

    /** @param magnification How a viewer fits the page. @return This conversion. */
    public LibreOfficeConversion magnification(Magnification magnification) {
        return set("magnification", magnification.value());
    }

    /** @param percent The zoom, with {@link Magnification#ZOOM}. @return This conversion. */
    public LibreOfficeConversion zoom(int percent) {
        return set("zoom", percent);
    }

    /** @param layout How a viewer lays the pages out. @return This conversion. */
    public LibreOfficeConversion pageLayout(PageLayout layout) {
        return set("pageLayout", layout.value());
    }

    /** @param levels How many bookmark levels a viewer expands, 1 to 10, or -1 for all. @return This conversion. */
    public LibreOfficeConversion openBookmarkLevels(int levels) {
        if (levels != -1 && (levels < 1 || levels > 10)) {
            throw new IllegalArgumentException("openBookmarkLevels takes -1, or 1 to 10, got " + levels);
        }
        return set("openBookmarkLevels", levels);
    }

    public LibreOfficeConversion resizeWindowToInitialPage(boolean resize) {
        return set("resizeWindowToInitialPage", resize);
    }

    public LibreOfficeConversion centerWindow(boolean center) {
        return set("centerWindow", center);
    }

    public LibreOfficeConversion openInFullScreenMode(boolean fullScreen) {
        return set("openInFullScreenMode", fullScreen);
    }

    public LibreOfficeConversion displayPDFDocumentTitle(boolean display) {
        return set("displayPDFDocumentTitle", display);
    }

    public LibreOfficeConversion hideViewerMenubar(boolean hide) {
        return set("hideViewerMenubar", hide);
    }

    public LibreOfficeConversion hideViewerToolbar(boolean hide) {
        return set("hideViewerToolbar", hide);
    }

    public LibreOfficeConversion hideViewerWindowControls(boolean hide) {
        return set("hideViewerWindowControls", hide);
    }

    /**
     * Adds stages to run on the exported PDFs, in order, such as {@link Stage#merge()}.
     *
     * @param stages The stages.
     * @return This conversion.
     */
    public LibreOfficeConversion stages(Stage... stages) {
        this.stages.addAll(List.of(stages));
        return this;
    }

    void addTo(Multipart form) {
        documents.forEach(document -> document.addTo(form, "files"));
        addInputsTo(form);
        boolean remoteInputs = form.fieldNames().contains("downloadFrom") || form.fieldNames().contains("archives");
        if (documents.isEmpty() && !remoteInputs) {
            throw new IllegalArgumentException("A LibreOffice conversion takes at least one document");
        }
        fields.forEach(form::field);
        StageUploads.addStagesTo(form, stages);
    }
}
