package io.bitizens.system;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Health is Gotenberg's health report.
 *
 * @param up      Whether every engine is up.
 * @param engines The status of each engine, such as {@code chromium} or {@code libreoffice}: {@code up} or
 *                {@code down}.
 */
public record Health(boolean up, Map<String, String> engines) {
    static Health fromJson(JSONObject json) {
        Map<String, String> engines = new LinkedHashMap<>();
        JSONObject details = json.optJSONObject("details");
        if (details != null) {
            for (String engine : details.keySet()) {
                engines.put(engine, details.getJSONObject(engine).optString("status"));
            }
        }
        return new Health("up".equals(json.optString("status")), Map.copyOf(engines));
    }
}
