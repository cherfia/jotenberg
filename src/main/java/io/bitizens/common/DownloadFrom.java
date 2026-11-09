package io.bitizens.common;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * DownloadFrom is a URL Gotenberg downloads before it starts, as if the file had been uploaded.
 */
public final class DownloadFrom {
    private final String url;
    private final Map<String, String> extraHttpHeaders = new LinkedHashMap<>();
    private String filename;

    private DownloadFrom(String url) {
        this.url = Objects.requireNonNull(url, "url");
    }

    /**
     * @param url An absolute {@code http} or {@code https} URL.
     * @return The download.
     */
    public static DownloadFrom of(String url) {
        return new DownloadFrom(url);
    }

    /**
     * Sends a header with the download.
     *
     * @param name  The header name.
     * @param value The header value.
     * @return This download.
     */
    public DownloadFrom extraHttpHeader(String name, String value) {
        extraHttpHeaders.put(name, value);
        return this;
    }

    /**
     * Names the downloaded file, over the server's {@code Content-Disposition} header and the URL path.
     *
     * @param filename The file name, extension included.
     * @return This download.
     */
    public DownloadFrom filename(String filename) {
        this.filename = filename;
        return this;
    }

    /**
     * @return The entry as Gotenberg's {@code downloadFrom} field takes it.
     */
    JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("url", url);
        if (!extraHttpHeaders.isEmpty()) {
            json.put("extraHttpHeaders", new JSONObject(extraHttpHeaders));
        }
        if (filename != null) {
            json.put("filename", filename);
        }
        return json;
    }
}
