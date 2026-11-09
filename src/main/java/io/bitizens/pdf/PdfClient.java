package io.bitizens.pdf;

import io.bitizens.core.JotenbergException;
import io.bitizens.internal.Multipart;
import io.bitizens.internal.Transport;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * PdfClient wraps Gotenberg's {@code /forms/pdf} routes: it runs stages on PDFs you already have, one by one or as a
 * pipe, and reads PDFs. The stage methods return the result: a PDF, or another file or a ZIP where the stage makes
 * one. A non-2xx answer throws a {@link io.bitizens.core.GotenbergException}.
 *
 * @see <a href="https://gotenberg.dev/docs/pdf/pipe">Gotenberg – PDF routes</a>
 */
public final class PdfClient {
    private static final String PATH = "forms/pdf/";

    private final Transport transport;

    /**
     * @param transport The transport to send with.
     */
    public PdfClient(Transport transport) {
        this.transport = transport;
    }

    /**
     * Runs stages on the PDFs, in order.
     *
     * @param request The PDFs and the request options.
     * @param stages  The stages, at least one.
     * @return The PDF, or a ZIP when several files come out.
     */
    public byte[] pipe(PdfRequest request, Stage... stages) {
        return pipe(request, List.of(stages));
    }

    /**
     * Runs stages on the PDFs, in order.
     *
     * @param request The PDFs and the request options.
     * @param stages  The stages, at least one.
     * @return The PDF, or a ZIP when several files come out.
     */
    public byte[] pipe(PdfRequest request, List<Stage> stages) {
        if (stages.isEmpty()) {
            throw new IllegalArgumentException("pipe takes at least one stage");
        }
        Multipart form = new Multipart();
        request.addTo(form);
        StageUploads.addStagesTo(form, stages);
        return transport.post(PATH + "pipe", form, request.headers());
    }

    /**
     * Runs one stage on its own route, {@code /forms/pdf/<op>}.
     *
     * @param request The PDFs and the request options.
     * @param stage   The stage.
     * @return The result.
     */
    public byte[] run(PdfRequest request, Stage stage) {
        Multipart form = new Multipart();
        request.addTo(form);
        StageUploads.addStageFieldsTo(form, stage);
        return transport.post(PATH + stage.op(), form, request.headers());
    }

    // ---------------------------------------------------------------- one method per stage

    /** Appends the PDFs into one, in the alphanumeric order of their names. */
    public byte[] merge(PdfRequest request) {
        return run(request, Stage.merge());
    }

    /** Cuts each PDF into several: a ZIP of the parts, or the one part as a PDF. */
    public byte[] split(PdfRequest request, Stage.Split split) {
        return run(request, split);
    }

    /** Keeps the pages listed, in the order listed. */
    public byte[] select(PdfRequest request, String pages) {
        return run(request, Stage.select(pages));
    }

    /** Removes the pages listed. */
    public byte[] delete(PdfRequest request, String pages) {
        return run(request, Stage.delete(pages));
    }

    /** Inserts the pages of another PDF. */
    public byte[] insert(PdfRequest request, Stage.Insert insert) {
        return run(request, insert);
    }

    /** Draws pages of the PDFs on new pages of a chosen size. */
    public byte[] compose(PdfRequest request, Stage.Compose compose) {
        return run(request, compose);
    }

    /** Sets the rotation of pages. */
    public byte[] rotate(PdfRequest request, Stage.Rotate rotate) {
        return run(request, rotate);
    }

    /** Draws a line of text over pages. */
    public byte[] stampText(PdfRequest request, Stage.StampText stamp) {
        return run(request, stamp);
    }

    /** Draws a PNG or JPEG image over pages. */
    public byte[] stampImage(PdfRequest request, Stage.StampImage stamp) {
        return run(request, stamp);
    }

    /** Draws a page of another PDF over pages. */
    public byte[] stampPdf(PdfRequest request, Stage.StampPdf stamp) {
        return run(request, stamp);
    }

    /** Writes the document information. */
    public byte[] setMetadata(PdfRequest request, Stage.SetMetadata metadata) {
        return run(request, metadata);
    }

    /** Replaces the bookmarks. */
    public byte[] setBookmarks(PdfRequest request, Stage.SetBookmarks bookmarks) {
        return run(request, bookmarks);
    }

    /** Fills form fields. */
    public byte[] setForm(PdfRequest request, Stage.SetForm form) {
        return run(request, form);
    }

    /** Burns the form fields into the pages. */
    public byte[] flattenForm(PdfRequest request) {
        return run(request, Stage.flattenForm());
    }

    /** Replaces transparent content with opaque content. */
    public byte[] flattenTransparency(PdfRequest request) {
        return run(request, Stage.flattenTransparency());
    }

    /** Converts every color to one color space. */
    public byte[] convertColors(PdfRequest request, ColorTarget target) {
        return run(request, Stage.convertColors(target));
    }

    /** Removes the content under each region from the file itself. */
    public byte[] redact(PdfRequest request, RedactRegion... regions) {
        return run(request, Stage.redact(regions));
    }

    /** Infers a structure tree for an untagged PDF. */
    public byte[] autotag(PdfRequest request) {
        return run(request, Stage.autotag());
    }

    /** Embeds a file in the PDF. */
    public byte[] attach(PdfRequest request, Stage.Attach attach) {
        return run(request, attach);
    }

    /** Removes an embedded file by the name the PDF lists it under. */
    public byte[] detach(PdfRequest request, String name) {
        return run(request, Stage.detach(name));
    }

    /** Returns the embedded files: the file, or a ZIP of several. */
    public byte[] extractAttachments(PdfRequest request, Stage.ExtractAttachments extract) {
        return run(request, extract);
    }

    /**
     * Returns the text the pages show, as UTF-8 with its pages separated by a form feed; several PDFs answer a ZIP of
     * the text files.
     */
    public byte[] extractText(PdfRequest request, Stage.ExtractText extract) {
        return run(request, extract);
    }

    /** Rewrites the PDF to shrink it, without lowering image quality. */
    public byte[] optimize(PdfRequest request, Stage.Optimize optimize) {
        return run(request, optimize);
    }

    /** Protects the PDF with passwords. */
    public byte[] encrypt(PdfRequest request, Stage.Encrypt encrypt) {
        return run(request, encrypt);
    }

    /** Opens encrypted PDFs and removes their encryption. */
    public byte[] decrypt(PdfRequest request, Stage.Decrypt decrypt) {
        return run(request, decrypt);
    }

    /**
     * Repairs the PDF until it conforms to PDF/A, PDF/UA or Factur-X. Gotenberg answers even when violations remain:
     * check the result with {@link #validate}.
     */
    public byte[] remediate(PdfRequest request, Stage.Remediate remediate) {
        return run(request, remediate);
    }

    /** Signs the PDF with a certificate. */
    public byte[] sign(PdfRequest request, Stage.Sign sign) {
        return run(request, sign);
    }

    /** Adds an RFC 3161 document timestamp. */
    public byte[] timestamp(PdfRequest request, String timestampUrl) {
        return run(request, Stage.timestamp(timestampUrl));
    }

    /** Draws pages as images: the image, or a ZIP of several. */
    public byte[] render(PdfRequest request, Stage.Render render) {
        return run(request, render);
    }

    // ---------------------------------------------------------------- read routes

    /** Reads the page count, the PDF version, and whether each PDF is encrypted, tagged, linearized or has forms. */
    public Map<String, PdfInfo> info(PdfRead request) {
        return read("info", request, Map.of(), value -> PdfInfo.fromJson((JSONObject) value));
    }

    /** Reads the document information of each PDF. */
    public Map<String, PdfMetadata> metadata(PdfRead request) {
        return read("metadata", request, Map.of(), value -> PdfMetadata.fromJson((JSONObject) value));
    }

    /** Reads the bookmarks of each PDF. */
    public Map<String, List<Bookmark>> bookmarks(PdfRead request) {
        return read("bookmarks", request, Map.of(), value -> List.copyOf(Bookmark.fromJson((JSONArray) value)));
    }

    /** Lists the files embedded in each PDF. */
    public Map<String, List<PdfAttachment>> attachments(PdfRead request) {
        return read("attachments", request, Map.of(), value -> list((JSONArray) value, PdfAttachment::fromJson));
    }

    /** Lists the form fields of each PDF. */
    public Map<String, List<PdfFormField>> form(PdfRead request) {
        return form(request, null);
    }

    /** Lists the form fields on some pages of each PDF. */
    public Map<String, List<PdfFormField>> form(PdfRead request, String pages) {
        Map<String, String> fields = pages == null ? Map.of() : Map.of("pages", pages);
        return read("form", request, fields, value -> list((JSONArray) value, PdfFormField::fromJson));
    }

    /** Checks each PDF against a PDF/A, PDF/UA or Factur-X profile, and lists the violations. */
    public Map<String, PdfValidation> validate(PdfRead request, ConformanceProfile profile) {
        return read("validate", request, Map.of("profile", profile.value()),
                value -> PdfValidation.fromJson((JSONObject) value));
    }

    /** Checks each PDF against a PDF/A (or Factur-X) profile and a PDF/UA profile together. */
    public Map<String, PdfValidation> validate(PdfRead request, ConformanceProfile archival, ConformanceProfile accessible) {
        ConformanceProfile.checkPair(archival, accessible);
        String profiles = new JSONArray(List.of(archival.value(), accessible.value())).toString();
        return read("validate", request, Map.of("profile", profiles),
                value -> PdfValidation.fromJson((JSONObject) value));
    }

    private <T> Map<String, T> read(String route, PdfRead request, Map<String, String> fields,
                                    Function<Object, T> parse) {
        Multipart form = new Multipart();
        request.addTo(form);
        fields.forEach(form::field);
        byte[] body = transport.post(PATH + route, form, request.headers());
        try {
            JSONObject json = new JSONObject(new String(body, StandardCharsets.UTF_8));
            Map<String, T> result = new LinkedHashMap<>();
            for (String name : json.keySet()) {
                result.put(name, parse.apply(json.get(name)));
            }
            return result;
        } catch (JSONException | ClassCastException e) {
            throw new JotenbergException("Gotenberg answered " + route + " with unexpected JSON", e);
        }
    }

    private static <T> List<T> list(JSONArray array, Function<JSONObject, T> parse) {
        List<T> items = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            items.add(parse.apply(array.getJSONObject(i)));
        }
        return List.copyOf(items);
    }
}
