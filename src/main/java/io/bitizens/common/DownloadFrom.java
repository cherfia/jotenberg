package io.bitizens.common;

import org.json.JSONObject;

import java.util.Map;

/**
 * Represents a download from URL configuration.
 */
public class DownloadFrom {
    private final String url;
    private final Map<String, String> extraHttpHeaders;
    private final String field;

    private DownloadFrom(Builder builder) {
        this.url = builder.url;
        this.extraHttpHeaders = builder.extraHttpHeaders;
        this.field = builder.field;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getExtraHttpHeaders() {
        return extraHttpHeaders;
    }

    public String getField() {
        return field;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("url", url);
        if (extraHttpHeaders != null) {
            json.put("extraHttpHeaders", new JSONObject(extraHttpHeaders));
        }
        if (field != null) {
            json.put("field", field);
        }
        return json;
    }

    public static class Builder {
        private String url;
        private Map<String, String> extraHttpHeaders;
        private String field;

        public Builder(String url) {
            this.url = url;
        }

        public Builder extraHttpHeaders(Map<String, String> extraHttpHeaders) {
            this.extraHttpHeaders = extraHttpHeaders;
            return this;
        }

        public Builder field(String field) {
            this.field = field;
            return this;
        }

        public DownloadFrom build() {
            return new DownloadFrom(this);
        }
    }
}

