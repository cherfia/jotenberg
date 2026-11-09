package io.bitizens.pdf;

import org.json.JSONObject;

/**
 * PdfInfo is what {@link PdfClient#info} reads from a PDF.
 *
 * @param pages      The page count.
 * @param version    The PDF version, such as {@code 1.7}.
 * @param encrypted  Whether it's encrypted.
 * @param tagged     Whether it carries a structure for screen readers.
 * @param linearized Whether it's built for fast web view.
 * @param formFields Whether it holds form fields.
 */
public record PdfInfo(int pages, String version, boolean encrypted, boolean tagged, boolean linearized,
                      boolean formFields) {
    static PdfInfo fromJson(JSONObject json) {
        return new PdfInfo(
                json.getInt("pages"),
                json.getString("version"),
                json.getBoolean("encrypted"),
                json.getBoolean("tagged"),
                json.getBoolean("linearized"),
                json.getBoolean("formFields"));
    }
}
