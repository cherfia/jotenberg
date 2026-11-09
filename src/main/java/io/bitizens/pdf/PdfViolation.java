package io.bitizens.pdf;

import org.json.JSONObject;

/**
 * PdfViolation is a rule a PDF breaks. A {@code fatal} violation makes it non-compliant; a {@code warning}, which only
 * the Factur-X Schematron rules raise, doesn't.
 *
 * @param specification The standard it breaks, such as {@code ISO_19005_2}.
 * @param clause        The clause.
 * @param testNumber    The test of the clause.
 * @param description   What the rule says.
 * @param context       Where the PDF breaks it.
 * @param severity      {@code fatal} or {@code warning}.
 */
public record PdfViolation(String specification, String clause, int testNumber, String description, String context,
                           String severity) {
    static PdfViolation fromJson(JSONObject json) {
        return new PdfViolation(
                json.optString("specification", null),
                json.optString("clause", null),
                json.optInt("testNumber"),
                json.optString("description", null),
                json.optString("context", null),
                json.optString("severity", null));
    }

    /**
     * @return Whether this violation makes the PDF non-compliant.
     */
    public boolean isFatal() {
        return "fatal".equals(severity);
    }
}
