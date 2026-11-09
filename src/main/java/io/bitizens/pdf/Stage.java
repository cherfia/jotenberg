package io.bitizens.pdf;

import io.bitizens.common.PaperSize;
import io.bitizens.internal.Multipart;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stage is one operation on a PDF: merge, stamp, sign, make PDF/A, and so on. Gotenberg runs a list of stages in
 * order, each on what the previous one produced. Conversions and {@link PdfClient#pipe} take a list; each stage also
 * has its own {@link PdfClient} method.
 * <p>
 * Create stages with the static factories, such as {@code Stage.stampText("DRAFT").at(72, 72)}. Options of type
 * {@code pages} take a page selection such as {@code "1-3"}, {@code "odd"} or {@code "!last"}.
 *
 * @see <a href="https://gotenberg.dev/docs/pdf/pipe">Gotenberg – Run stages</a>
 */
public abstract class Stage {
    private final String op;
    private final Map<String, Object> options = new LinkedHashMap<>();
    private final Map<String, StageFile> files = new LinkedHashMap<>();

    Stage(String op) {
        this.op = op;
    }

    /**
     * @return The stage's operation, such as {@code stamp-text}; its route is {@code /forms/pdf/<op>}.
     */
    public String op() {
        return op;
    }

    final void put(String key, Object value) {
        options.put(key, value);
    }

    final void putFile(String key, StageFile file) {
        files.put(key, file);
    }

    /**
     * Checks the options before the stage is sent.
     *
     * @throws IllegalArgumentException If Gotenberg would refuse them.
     */
    void validate() {
    }

    /**
     * @param uploads The request's stage files, which this stage's files join.
     * @return The stage as an entry of Gotenberg's {@code stages} list.
     */
    JSONObject toJson(StageUploads uploads) {
        validate();
        JSONObject json = new JSONObject();
        json.put("op", op);
        options.forEach(json::put);
        files.forEach((key, file) -> json.put(key, uploads.register(file)));
        return json;
    }

    /**
     * Adds the stage's options as form fields, as its own route takes them: lists and objects as JSON.
     *
     * @param form    The form.
     * @param uploads The request's stage files, which this stage's files join.
     */
    void addFieldsTo(Multipart form, StageUploads uploads) {
        validate();
        options.forEach((key, value) -> form.field(key, value instanceof JSONArray || value instanceof JSONObject
                ? value.toString()
                : String.valueOf(value)));
        files.forEach((key, file) -> form.field(key, uploads.register(file)));
    }

    // ---------------------------------------------------------------- factories

    /** @return A stage that appends the PDFs into one, in the alphanumeric order of their names. */
    public static Merge merge() {
        return new Merge();
    }

    /** @return A stage that cuts each PDF into several; set {@link Split#every} or {@link Split#ranges}. */
    public static Split split() {
        return new Split();
    }

    /** @param pages The pages to keep, in the order listed. @return A stage that keeps those pages. */
    public static Select select(String pages) {
        return new Select(pages);
    }

    /** @param pages The pages to remove. @return A stage that removes those pages. */
    public static Delete delete(String pages) {
        return new Delete(pages);
    }

    /** @param source The PDF whose pages to insert. @return A stage that inserts its pages. */
    public static Insert insert(StageFile source) {
        return new Insert(source);
    }

    /** @param placements One per drawn page. @return A stage that draws pages on new pages of a chosen size. */
    public static Compose compose(ComposePlacement... placements) {
        return new Compose(placements);
    }

    /** @param angle A multiple of 90, such as 90 or -90. @return A stage that sets the rotation of pages. */
    public static Rotate rotate(int angle) {
        return new Rotate(angle);
    }

    /** @param text The text to draw. @return A stage that draws a line of text over pages. */
    public static StampText stampText(String text) {
        return new StampText(text);
    }

    /** @param image A PNG or JPEG image. @return A stage that draws the image over pages. */
    public static StampImage stampImage(StageFile image) {
        return new StampImage(image);
    }

    /** @param source The PDF to draw. @return A stage that draws a page of it over pages. */
    public static StampPdf stampPdf(StageFile source) {
        return new StampPdf(source);
    }

    /** @return A stage that writes the document information; a field left out keeps its value. */
    public static SetMetadata setMetadata() {
        return new SetMetadata();
    }

    /** @param bookmarks The new outline. @return A stage that replaces the bookmarks. */
    public static SetBookmarks setBookmarks(Bookmark... bookmarks) {
        return new SetBookmarks(List.of(bookmarks));
    }

    /** @return A stage that fills form fields; add them with {@link SetForm#field}. */
    public static SetForm setForm() {
        return new SetForm();
    }

    /** @return A stage that burns the form fields into the pages. */
    public static FlattenForm flattenForm() {
        return new FlattenForm();
    }

    /** @return A stage that replaces transparent content with opaque content. */
    public static FlattenTransparency flattenTransparency() {
        return new FlattenTransparency();
    }

    /** @param target The color space. @return A stage that converts every color to it. */
    public static ConvertColors convertColors(ColorTarget target) {
        return new ConvertColors(target);
    }

    /** @param regions The regions to clear. @return A stage that removes their content from the file itself. */
    public static Redact redact(RedactRegion... regions) {
        return new Redact(regions);
    }

    /** @return A stage that infers a structure tree for an untagged PDF. */
    public static Autotag autotag() {
        return new Autotag();
    }

    /** @param file The file to embed. @return A stage that embeds it in the PDF. */
    public static Attach attach(StageFile file) {
        return new Attach(file);
    }

    /** @param name The name the PDF lists the file under. @return A stage that removes the embedded file. */
    public static Detach detach(String name) {
        return new Detach(name);
    }

    /** @return A stage that returns the embedded files; it comes last. */
    public static ExtractAttachments extractAttachments() {
        return new ExtractAttachments();
    }

    /** @return A stage that returns the text the pages show; it comes last. */
    public static ExtractText extractText() {
        return new ExtractText();
    }

    /** @return A stage that shrinks the PDF without lowering image quality. */
    public static Optimize optimize() {
        return new Optimize();
    }

    /** @return A stage that protects the PDF with passwords; set a user or owner password. */
    public static Encrypt encrypt() {
        return new Encrypt();
    }

    /** @return A stage that opens encrypted PDFs and removes their encryption; it comes first. */
    public static Decrypt decrypt() {
        return new Decrypt();
    }

    /** @param profile A PDF/A, PDF/UA or Factur-X profile. @return A stage that makes the PDF conform to it. */
    public static Remediate remediate(ConformanceProfile profile) {
        return new Remediate(profile, null);
    }

    /**
     * @param archival   A PDF/A or Factur-X profile.
     * @param accessible The PDF/UA profile of its PDF version.
     * @return A stage that makes the PDF conform to both.
     */
    public static Remediate remediate(ConformanceProfile archival, ConformanceProfile accessible) {
        return new Remediate(archival, accessible);
    }

    /** @param certificate A PKCS#12 file, or a PEM certificate with a key. @return A stage that signs the PDF. */
    public static Sign sign(StageFile certificate) {
        return new Sign(certificate);
    }

    /** @param timestampUrl An RFC 3161 authority's URL. @return A stage that adds a document timestamp. */
    public static Timestamp timestamp(String timestampUrl) {
        return new Timestamp(timestampUrl);
    }

    /** @return A stage that draws pages as images; it comes last. */
    public static Render render() {
        return new Render();
    }

    // ---------------------------------------------------------------- stages

    public static final class Merge extends Stage {
        Merge() {
            super("merge");
        }
    }

    public static final class Split extends Stage {
        Split() {
            super("split");
        }

        /** @param every Cuts every this many pages, at least 1. @return This stage. */
        public Split every(int every) {
            put("every", every);
            return this;
        }

        /** @param ranges One PDF per entry, each a page selection such as {@code "1-3"}. @return This stage. */
        public Split ranges(String... ranges) {
            put("ranges", new JSONArray(Arrays.asList(ranges)));
            return this;
        }

        @Override
        void validate() {
            if (options().containsKey("every") == options().containsKey("ranges")) {
                throw new IllegalArgumentException("split takes exactly one of every or ranges");
            }
        }
    }

    public static final class Select extends Stage {
        Select(String pages) {
            super("select");
            put("pages", pages);
        }
    }

    public static final class Delete extends Stage {
        Delete(String pages) {
            super("delete");
            put("pages", pages);
        }
    }

    public static final class Insert extends Stage {
        Insert(StageFile source) {
            super("insert");
            putFile("source", source);
        }

        /** @param at Inserts after this page; 0, the default, inserts before the first. @return This stage. */
        public Insert at(int at) {
            put("at", at);
            return this;
        }

        /** @param sourcePages The pages of the source to insert; all by default. @return This stage. */
        public Insert sourcePages(String sourcePages) {
            put("sourcePages", sourcePages);
            return this;
        }
    }

    public static final class Compose extends Stage {
        Compose(ComposePlacement[] placements) {
            super("compose");
            JSONArray array = new JSONArray();
            for (ComposePlacement placement : placements) {
                array.put(placement.toJson());
            }
            put("placements", array);
        }

        /** @param paperSize The size of the new pages. @return This stage. */
        public Compose paperSize(PaperSize paperSize) {
            put("paperSize", paperSize.value());
            return this;
        }

        /** @param paperWidth A length such as {@code "6in"} or {@code "15cm"}, over the paper size. @return This stage. */
        public Compose paperWidth(String paperWidth) {
            put("paperWidth", paperWidth);
            return this;
        }

        /** @param paperHeight A length such as {@code "5in"}, over the paper size. @return This stage. */
        public Compose paperHeight(String paperHeight) {
            put("paperHeight", paperHeight);
            return this;
        }

        @Override
        void validate() {
            if (((JSONArray) options().get("placements")).isEmpty()) {
                throw new IllegalArgumentException("compose takes at least one placement");
            }
            boolean sized = options().containsKey("paperSize")
                    || (options().containsKey("paperWidth") && options().containsKey("paperHeight"));
            if (!sized) {
                throw new IllegalArgumentException("compose takes a paperSize, or both paperWidth and paperHeight");
            }
        }
    }

    public static final class Rotate extends Stage {
        Rotate(int angle) {
            super("rotate");
            if (angle % 90 != 0) {
                throw new IllegalArgumentException("rotate takes a multiple of 90, got " + angle);
            }
            put("angle", angle);
        }

        /** @param pages The pages to rotate; all by default. @return This stage. */
        public Rotate pages(String pages) {
            put("pages", pages);
            return this;
        }
    }

    /**
     * The options every stamp takes. Positions count in points from the bottom-left corner of the page.
     */
    public abstract static class Stamp<T extends Stamp<T>> extends Stage {
        Stamp(String op) {
            super(op);
        }

        @SuppressWarnings("unchecked")
        private T self() {
            return (T) this;
        }

        /** @param pages The pages to stamp; all by default. @return This stage. */
        public T pages(String pages) {
            put("pages", pages);
            return self();
        }

        /** @param x Points from the left edge. @param y Points from the bottom edge. @return This stage. */
        public T at(double x, double y) {
            put("x", x);
            put("y", y);
            return self();
        }

        /** @param opacity From 0 to 1; unset, or 0, draws opaque. @return This stage. */
        public T opacity(double opacity) {
            if (opacity < 0 || opacity > 1) {
                throw new IllegalArgumentException("opacity runs from 0 to 1, got " + opacity);
            }
            put("opacity", opacity);
            return self();
        }

        /** @param rotation Degrees, counter-clockwise around the stamp's position. @return This stage. */
        public T rotation(double rotation) {
            put("rotation", rotation);
            return self();
        }
    }

    public static final class StampText extends Stamp<StampText> {
        StampText(String text) {
            super("stamp-text");
            put("text", text);
        }

        /** @param fontSize In points, greater than 0; 12 by default. @return This stage. */
        public StampText fontSize(double fontSize) {
            put("fontSize", fontSize);
            return this;
        }

        /** @param font One of the 14 standard PDF fonts; Helvetica by default. @return This stage. */
        public StampText font(StandardFont font) {
            put("font", font.value());
            return this;
        }

        /** @param color {@code #rrggbb}; black by default. @return This stage. */
        public StampText color(String color) {
            put("color", color);
            return this;
        }
    }

    public static final class StampImage extends Stamp<StampImage> {
        StampImage(StageFile image) {
            super("stamp-image");
            putFile("image", image);
        }

        /** @param width In points; set width or height to keep the proportions. @return This stage. */
        public StampImage width(double width) {
            put("width", width);
            return this;
        }

        /** @param height In points. @return This stage. */
        public StampImage height(double height) {
            put("height", height);
            return this;
        }
    }

    public static final class StampPdf extends Stamp<StampPdf> {
        StampPdf(StageFile source) {
            super("stamp-pdf");
            putFile("source", source);
        }

        /** @param sourcePage The page of the source to draw; 1 by default. @return This stage. */
        public StampPdf sourcePage(int sourcePage) {
            put("sourcePage", sourcePage);
            return this;
        }

        /** @param scale Greater than 0; 1 by default. @return This stage. */
        public StampPdf scale(double scale) {
            put("scale", scale);
            return this;
        }
    }

    public static final class SetMetadata extends Stage {
        SetMetadata() {
            super("set-metadata");
        }

        public SetMetadata title(String title) {
            put("title", title);
            return this;
        }

        public SetMetadata author(String author) {
            put("author", author);
            return this;
        }

        public SetMetadata subject(String subject) {
            put("subject", subject);
            return this;
        }

        public SetMetadata keywords(String keywords) {
            put("keywords", keywords);
            return this;
        }

        /** @param creator The application that made the original document. @return This stage. */
        public SetMetadata creator(String creator) {
            put("creator", creator);
            return this;
        }

        /** @param producer The application that made the PDF. @return This stage. */
        public SetMetadata producer(String producer) {
            put("producer", producer);
            return this;
        }

        /** @param creationDate A PDF date, such as {@code D:20260101120000Z}. @return This stage. */
        public SetMetadata creationDate(String creationDate) {
            put("creationDate", creationDate);
            return this;
        }

        /** @param modDate A PDF date, such as {@code D:20260101120000Z}. @return This stage. */
        public SetMetadata modDate(String modDate) {
            put("modDate", modDate);
            return this;
        }
    }

    public static final class SetBookmarks extends Stage {
        SetBookmarks(List<Bookmark> bookmarks) {
            super("set-bookmarks");
            JSONArray array = new JSONArray();
            bookmarks.forEach(bookmark -> array.put(bookmark.toJson()));
            put("bookmarks", array);
        }
    }

    public static final class SetForm extends Stage {
        private final JSONObject fields = new JSONObject();

        SetForm() {
            super("set-form");
            put("fields", fields);
        }

        /**
         * @param name   The field's fully qualified name, as {@link PdfClient#form} lists it.
         * @param values The value, or several for a list that selects several.
         * @return This stage.
         */
        public SetForm field(String name, String... values) {
            fields.put(name, values.length == 1 ? values[0] : new JSONArray(Arrays.asList(values)));
            return this;
        }

        @Override
        void validate() {
            if (fields.isEmpty()) {
                throw new IllegalArgumentException("set-form takes at least one field");
            }
        }
    }

    public static final class FlattenForm extends Stage {
        FlattenForm() {
            super("flatten-form");
        }
    }

    public static final class FlattenTransparency extends Stage {
        FlattenTransparency() {
            super("flatten-transparency");
        }
    }

    public static final class ConvertColors extends Stage {
        ConvertColors(ColorTarget target) {
            super("convert-colors");
            put("target", target.value());
        }
    }

    public static final class Redact extends Stage {
        Redact(RedactRegion[] regions) {
            super("redact");
            if (regions.length == 0) {
                throw new IllegalArgumentException("redact takes at least one region");
            }
            JSONArray array = new JSONArray();
            for (RedactRegion region : regions) {
                array.put(region.toJson());
            }
            put("regions", array);
        }
    }

    public static final class Autotag extends Stage {
        Autotag() {
            super("autotag");
        }
    }

    public static final class Attach extends Stage {
        Attach(StageFile file) {
            super("attach");
            putFile("file", file);
        }

        /** @param name The name the PDF lists the file under; the file's name by default. @return This stage. */
        public Attach name(String name) {
            put("name", name);
            return this;
        }

        /** @param mimeType The file's MIME type; from the name by default. @return This stage. */
        public Attach mimeType(String mimeType) {
            put("mimeType", mimeType);
            return this;
        }

        /** @param relationship Makes the file a PDF/A-3 associated file. @return This stage. */
        public Attach relationship(AttachmentRelationship relationship) {
            put("relationship", relationship.value());
            return this;
        }
    }

    public static final class Detach extends Stage {
        Detach(String name) {
            super("detach");
            put("name", name);
        }
    }

    public static final class ExtractAttachments extends Stage {
        ExtractAttachments() {
            super("extract-attachments");
        }

        /** @param name The one file to return; all by default. @return This stage. */
        public ExtractAttachments name(String name) {
            put("name", name);
            return this;
        }
    }

    public static final class ExtractText extends Stage {
        ExtractText() {
            super("extract-text");
        }

        /** @param pages The pages to read, in the order given; all by default. @return This stage. */
        public ExtractText pages(String pages) {
            put("pages", pages);
            return this;
        }

        /** @param invisible Adds the text layer a searchable scan carries under its image. @return This stage. */
        public ExtractText invisible(boolean invisible) {
            put("invisible", invisible);
            return this;
        }

        /** @param artifacts Adds headers, footers, page numbers and stamps marked as artifacts. @return This stage. */
        public ExtractText artifacts(boolean artifacts) {
            put("artifacts", artifacts);
            return this;
        }

        /** @param annotations Adds the text of annotations, such as comments. @return This stage. */
        public ExtractText annotations(boolean annotations) {
            put("annotations", annotations);
            return this;
        }
    }

    public static final class Optimize extends Stage {
        Optimize() {
            super("optimize");
        }

        /** @param enabled Packs objects into compressed object streams; on by default. @return This stage. */
        public Optimize objectStreams(boolean enabled) {
            put("objectStreams", enabled);
            return this;
        }

        /** @param enabled Stores identical streams once; on by default. @return This stage. */
        public Optimize dedup(boolean enabled) {
            put("dedup", enabled);
            return this;
        }

        /** @param enabled Recompresses images without losing quality; on by default. @return This stage. */
        public Optimize recompressImages(boolean enabled) {
            put("recompressImages", enabled);
            return this;
        }

        /** @param enabled Keeps only the glyphs the document shows; on by default. @return This stage. */
        public Optimize subsetFonts(boolean enabled) {
            put("subsetFonts", enabled);
            return this;
        }
    }

    public static final class Encrypt extends Stage {
        Encrypt() {
            super("encrypt");
        }

        /** @param userPassword The password to open the PDF. @return This stage. */
        public Encrypt userPassword(String userPassword) {
            put("userPassword", userPassword);
            return this;
        }

        /** @param ownerPassword The password that lifts the permissions. @return This stage. */
        public Encrypt ownerPassword(String ownerPassword) {
            put("ownerPassword", ownerPassword);
            return this;
        }

        /** @param algorithm AES-256 by default. @return This stage. */
        public Encrypt algorithm(EncryptAlgorithm algorithm) {
            put("algorithm", algorithm.value());
            return this;
        }

        /** @param permissions What a reader without the owner password may do. @return This stage. */
        public Encrypt permissions(EncryptPermissions permissions) {
            put("permissions", permissions.toJson());
            return this;
        }

        @Override
        void validate() {
            if (!options().containsKey("userPassword") && !options().containsKey("ownerPassword")) {
                throw new IllegalArgumentException("encrypt takes a userPassword, an ownerPassword, or both");
            }
        }
    }

    public static final class Decrypt extends Stage {
        Decrypt() {
            super("decrypt");
        }

        /** @param password The password that opens the PDFs. @return This stage. */
        public Decrypt password(String password) {
            put("password", password);
            return this;
        }
    }

    public static final class Remediate extends Stage {
        Remediate(ConformanceProfile profile, ConformanceProfile accessible) {
            super("remediate");
            if (accessible == null) {
                put("profile", profile.value());
            } else {
                ConformanceProfile.checkPair(profile, accessible);
                put("profile", new JSONArray(List.of(profile.value(), accessible.value())));
            }
        }

        /** @param language A BCP 47 tag such as {@code de-DE}; {@code en} by default. @return This stage. */
        public Remediate language(String language) {
            put("language", language);
            return this;
        }

        /** @param aggressive Allows fixes that may change how pages look. @return This stage. */
        public Remediate aggressive(boolean aggressive) {
            put("aggressive", aggressive);
            return this;
        }

        /** @param figureAltText The alternate text for figures that lack one. @return This stage. */
        public Remediate figureAltText(String figureAltText) {
            put("figureAltText", figureAltText);
            return this;
        }

        /**
         * Embeds a Factur-X invoice. Without it, a Factur-X remediation repairs the invoice the PDF already carries.
         *
         * @param xml   The Cross Industry Invoice XML.
         * @param level The level the XML declares.
         * @return This stage.
         */
        public Remediate facturX(StageFile xml, FacturXLevel level) {
            putFile("xml", xml);
            put("level", level.value());
            return this;
        }

        /** @param documentType The document type written in the metadata; INVOICE by default. @return This stage. */
        public Remediate documentType(String documentType) {
            put("documentType", documentType);
            return this;
        }
    }

    public static final class Sign extends Stage {
        Sign(StageFile certificate) {
            super("sign");
            putFile("certificate", certificate);
        }

        /** @param key The PEM private key of a PEM certificate. @return This stage. */
        public Sign key(StageFile key) {
            putFile("key", key);
            return this;
        }

        /** @param certificatePassword Opens a PKCS#12 certificate. @return This stage. */
        public Sign certificatePassword(String certificatePassword) {
            put("certificatePassword", certificatePassword);
            return this;
        }

        /** @param reason Why the PDF is signed, shown in the viewer's signature panel. @return This stage. */
        public Sign reason(String reason) {
            put("reason", reason);
            return this;
        }

        /** @param location Where it is signed. @return This stage. */
        public Sign location(String location) {
            put("location", location);
            return this;
        }

        /** @param contactInfo How to reach the signer. @return This stage. */
        public Sign contactInfo(String contactInfo) {
            put("contactInfo", contactInfo);
            return this;
        }

        /** @param name The signer's name. @return This stage. */
        public Sign name(String name) {
            put("name", name);
            return this;
        }

        /** @param pades A PAdES signature, the format eIDAS recognizes. @return This stage. */
        public Sign pades(boolean pades) {
            put("pades", pades);
            return this;
        }

        /** @param timestampUrl An RFC 3161 authority to timestamp the signature with. @return This stage. */
        public Sign timestampUrl(String timestampUrl) {
            put("timestampUrl", timestampUrl);
            return this;
        }
    }

    public static final class Timestamp extends Stage {
        Timestamp(String timestampUrl) {
            super("timestamp");
            put("timestampUrl", timestampUrl);
        }
    }

    public static final class Render extends Stage {
        Render() {
            super("render");
        }

        /** @param pages The pages to draw; all by default. @return This stage. */
        public Render pages(String pages) {
            put("pages", pages);
            return this;
        }

        /** @param dpi The resolution, at least 1; 150 by default. @return This stage. */
        public Render dpi(int dpi) {
            put("dpi", dpi);
            return this;
        }

        /** @param format PNG by default. @return This stage. */
        public Render format(RenderFormat format) {
            put("format", format.value());
            return this;
        }

        /** @param quality JPEG only, from 1 to 100; 100 by default. @return This stage. */
        public Render quality(int quality) {
            if (quality < 1 || quality > 100) {
                throw new IllegalArgumentException("render quality runs from 1 to 100, got " + quality);
            }
            put("quality", quality);
            return this;
        }

        @Override
        void validate() {
            if (options().containsKey("quality") && !RenderFormat.JPEG.value().equals(options().get("format"))) {
                throw new IllegalArgumentException("render takes a quality with the JPEG format only");
            }
        }
    }

    final Map<String, Object> options() {
        return options;
    }
}
