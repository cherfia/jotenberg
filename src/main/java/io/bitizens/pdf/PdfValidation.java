package io.bitizens.pdf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * PdfValidation is what {@link PdfClient#validate} answers for a PDF.
 *
 * @param profiles   The profile, or the PDF/A–PDF/UA pair, checked.
 * @param compliant  Whether the PDF conforms to every profile.
 * @param violations The rules it breaks.
 */
public record PdfValidation(List<ConformanceProfile> profiles, boolean compliant, List<PdfViolation> violations) {
    static PdfValidation fromJson(JSONObject json) {
        List<ConformanceProfile> profiles = new ArrayList<>();
        Object profile = json.get("profile");
        if (profile instanceof JSONArray array) {
            array.forEach(item -> profiles.add(ConformanceProfile.fromValue(String.valueOf(item))));
        } else {
            profiles.add(ConformanceProfile.fromValue(String.valueOf(profile)));
        }
        List<PdfViolation> violations = new ArrayList<>();
        JSONArray violationsJson = json.optJSONArray("violations");
        if (violationsJson != null) {
            for (int i = 0; i < violationsJson.length(); i++) {
                violations.add(PdfViolation.fromJson(violationsJson.getJSONObject(i)));
            }
        }
        return new PdfValidation(List.copyOf(profiles), json.getBoolean("compliant"), List.copyOf(violations));
    }
}
