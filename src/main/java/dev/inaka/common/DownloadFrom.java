package dev.inaka.common;

import org.json.JSONObject;

import java.util.Map;

/**
 * Represents a download from URL configuration.
 */
public class DownloadFrom {
    private final String url;
    private final Map<String, String> extraHttpHeaders;

    private DownloadFrom(Builder builder) {
        this.url = builder.url;
        this.extraHttpHeaders = builder.extraHttpHeaders;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getExtraHttpHeaders() {
        return extraHttpHeaders;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("url", url);
        if (extraHttpHeaders != null) {
            json.put("extraHttpHeaders", new JSONObject(extraHttpHeaders));
        }
        return json;
    }

    public static class Builder {
        private String url;
        private Map<String, String> extraHttpHeaders;

        public Builder(String url) {
            this.url = url;
        }

        public Builder extraHttpHeaders(Map<String, String> extraHttpHeaders) {
            this.extraHttpHeaders = extraHttpHeaders;
            return this;
        }

        public DownloadFrom build() {
            return new DownloadFrom(this);
        }
    }
}

