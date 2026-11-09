package io.bitizens.pdf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * PdfFormField is a form field, as {@link PdfClient#form} lists it, in the shape {@link Stage#setForm()} fills.
 *
 * @param name        The fully qualified name.
 * @param type        {@code text}, {@code checkbox}, {@code radio}, {@code button}, {@code combo}, {@code list} or
 *                    {@code signature}.
 * @param values      The value; several for a list that selects several.
 * @param pages       The pages the field sits on.
 * @param states      What a checkbox or radio group takes besides {@code Off}; empty otherwise.
 * @param options     What a choice field takes; empty otherwise.
 * @param readOnly    Whether it's read-only.
 * @param required    Whether it's required.
 * @param multiSelect Whether a list selects several.
 * @param editable    Whether a combo box takes free text.
 * @param maxLength   The maximum length of a text field, or {@code null}.
 */
public record PdfFormField(String name, String type, List<String> values, List<Integer> pages, List<String> states,
                           List<String> options, boolean readOnly, boolean required, boolean multiSelect,
                           boolean editable, Integer maxLength) {
    static PdfFormField fromJson(JSONObject json) {
        Object value = json.opt("value");
        List<String> values = value instanceof JSONArray array ? strings(array)
                : value == null ? List.of() : List.of(String.valueOf(value));
        List<Integer> pages = new ArrayList<>();
        JSONArray pagesJson = json.optJSONArray("pages");
        if (pagesJson != null) {
            pagesJson.forEach(page -> pages.add(((Number) page).intValue()));
        }
        return new PdfFormField(
                json.getString("name"),
                json.getString("type"),
                values,
                List.copyOf(pages),
                strings(json.optJSONArray("states")),
                strings(json.optJSONArray("options")),
                json.optBoolean("readOnly"),
                json.optBoolean("required"),
                json.optBoolean("multiSelect"),
                json.optBoolean("editable"),
                json.has("maxLength") ? json.getInt("maxLength") : null);
    }

    private static List<String> strings(JSONArray array) {
        List<String> list = new ArrayList<>();
        if (array != null) {
            array.forEach(item -> list.add(String.valueOf(item)));
        }
        return List.copyOf(list);
    }
}
