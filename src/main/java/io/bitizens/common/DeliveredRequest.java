package io.bitizens.common;

import io.bitizens.internal.Headers;

import java.util.Map;

/**
 * DeliveredRequest is a request whose result is a file: it can be named, and delivered to a webhook.
 *
 * @param <T> The concrete request type, returned by the fluent setters.
 */
public abstract class DeliveredRequest<T extends DeliveredRequest<T>> extends Request<T> {
    private Webhook webhook;
    private String outputFilename;

    /**
     * Uploads the result to a webhook instead of answering with it; the method then returns an empty result.
     *
     * @param webhook The webhook.
     * @return This request.
     */
    public T webhook(Webhook webhook) {
        this.webhook = webhook;
        return self();
    }

    /**
     * Names the result. Gotenberg adds the extension.
     *
     * @param outputFilename The name, without extension, in printable ASCII; sent as
     *                       {@code Gotenberg-Output-Filename}. For a non-ASCII name, write the result under it with
     *                       {@link io.bitizens.core.GotenbergResult#writeTo}.
     * @return This request.
     */
    public T outputFilename(String outputFilename) {
        this.outputFilename = outputFilename == null ? null : Headers.check("Gotenberg-Output-Filename", outputFilename);
        return self();
    }

    @Override
    public Map<String, String> headers() {
        Map<String, String> headers = super.headers();
        if (outputFilename != null) {
            headers.put("Gotenberg-Output-Filename", outputFilename);
        }
        if (webhook != null) {
            headers.putAll(webhook.headers());
        }
        return headers;
    }
}
