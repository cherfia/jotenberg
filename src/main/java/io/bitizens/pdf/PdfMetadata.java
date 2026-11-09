package io.bitizens.pdf;

import org.json.JSONObject;

/**
 * PdfMetadata is the document information {@link PdfClient#metadata} reads. A field the PDF doesn't set is
 * {@code null}. A date comes back as the PDF stores it: a PDF date ({@code D:20260101120000Z}) or an XMP date
 * ({@code 2026-01-01T12:00:00Z}).
 */
public record PdfMetadata(String title, String author, String subject, String keywords, String creator,
                          String producer, String creationDate, String modDate) {
    static PdfMetadata fromJson(JSONObject json) {
        return new PdfMetadata(
                json.optString("title", null),
                json.optString("author", null),
                json.optString("subject", null),
                json.optString("keywords", null),
                json.optString("creator", null),
                json.optString("producer", null),
                json.optString("creationDate", null),
                json.optString("modDate", null));
    }
}
