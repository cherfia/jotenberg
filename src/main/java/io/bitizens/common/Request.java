package io.bitizens.common;

import io.bitizens.internal.Headers;
import io.bitizens.internal.Multipart;
import org.json.JSONArray;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Request holds what every Gotenberg route takes besides its own fields: ZIP archives to unpack, URLs to download,
 * and a trace id.
 *
 * @param <T> The concrete request type, returned by the fluent setters.
 */
public abstract class Request<T extends Request<T>> {
    private final List<FileInput> archives = new ArrayList<>();
    private final List<DownloadFrom> downloadFrom = new ArrayList<>();
    private String trace;

    @SuppressWarnings("unchecked")
    protected final T self() {
        return (T) this;
    }

    /**
     * Uploads a ZIP that Gotenberg unpacks; each entry counts as an uploaded file.
     *
     * @param archive The ZIP file.
     * @return This request.
     */
    public T archive(FileInput archive) {
        archives.add(archive);
        return self();
    }

    /**
     * Uploads a ZIP that Gotenberg unpacks; each entry counts as an uploaded file.
     *
     * @param archive The ZIP file.
     * @return This request.
     */
    public T archive(Path archive) {
        return archive(FileInput.of(archive));
    }

    /**
     * Has Gotenberg download a file first, as if it had been uploaded.
     *
     * @param download The URL to download.
     * @return This request.
     */
    public T downloadFrom(DownloadFrom download) {
        downloadFrom.add(download);
        return self();
    }

    /**
     * Identifies the request in Gotenberg's logs, and in webhook events as {@code correlationId}.
     *
     * @param trace The request id, in printable ASCII; sent as {@code Gotenberg-Trace}.
     * @return This request.
     */
    public T trace(String trace) {
        this.trace = trace == null ? null : Headers.check("Gotenberg-Trace", trace);
        return self();
    }

    /**
     * Adds the archives and downloads to a form.
     *
     * @param form The form.
     */
    protected void addInputsTo(Multipart form) {
        archives.forEach(archive -> archive.addTo(form, "archives"));
        if (!downloadFrom.isEmpty()) {
            JSONArray entries = new JSONArray();
            downloadFrom.forEach(download -> entries.put(download.toJson()));
            form.field("downloadFrom", entries.toString());
        }
    }

    /**
     * @return The headers of this request.
     */
    public Map<String, String> headers() {
        Map<String, String> headers = new LinkedHashMap<>();
        if (trace != null) {
            headers.put("Gotenberg-Trace", trace);
        }
        return headers;
    }
}
