package io.bitizens.pdf;

import org.json.JSONObject;

/**
 * PdfAttachment is a file embedded in a PDF, as {@link PdfClient#attachments} lists it.
 *
 * @param name         The name the PDF lists it under.
 * @param mimeType     Its media type, or {@code null} when the PDF doesn't state one.
 * @param relationship Its relationship to the document, or {@code null} when the PDF doesn't state one.
 */
public record PdfAttachment(String name, String mimeType, AttachmentRelationship relationship) {
    static PdfAttachment fromJson(JSONObject json) {
        return new PdfAttachment(
                json.getString("name"),
                json.optString("mimeType", null),
                AttachmentRelationship.fromValue(json.optString("relationship", "")));
    }
}
