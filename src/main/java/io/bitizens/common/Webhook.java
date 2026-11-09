package io.bitizens.common;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Webhook has Gotenberg upload the result to a URL instead of answering with it. Gotenberg answers
 * {@code 204 No Content} right away, uploads the result to the webhook URL, then posts a {@code webhook.success}
 * or {@code webhook.error} event to the events URL.
 */
public final class Webhook {
    /**
     * The method of the upload.
     */
    public enum Method {POST, PUT, PATCH}

    private final String url;
    private final String eventsUrl;
    private final Map<String, String> extraHttpHeaders = new LinkedHashMap<>();
    private Method method;

    private Webhook(String url, String eventsUrl) {
        this.url = Objects.requireNonNull(url, "url");
        this.eventsUrl = Objects.requireNonNull(eventsUrl, "eventsUrl");
    }

    /**
     * @param url       Receives the result.
     * @param eventsUrl Receives the success or error event. Gotenberg requires it with the webhook URL.
     * @return The webhook.
     */
    public static Webhook of(String url, String eventsUrl) {
        return new Webhook(url, eventsUrl);
    }

    /**
     * @param method The method of the upload; {@code POST} by default.
     * @return This webhook.
     */
    public Webhook method(Method method) {
        this.method = method;
        return this;
    }

    /**
     * Sends a header with the upload and the event, such as {@code Authorization}.
     *
     * @param name  The header name.
     * @param value The header value.
     * @return This webhook.
     */
    public Webhook extraHttpHeader(String name, String value) {
        extraHttpHeaders.put(name, value);
        return this;
    }

    /**
     * @return The request headers that turn a request into a webhook.
     */
    public Map<String, String> headers() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Gotenberg-Webhook-Url", url);
        headers.put("Gotenberg-Webhook-Events-Url", eventsUrl);
        if (method != null) {
            headers.put("Gotenberg-Webhook-Method", method.name());
        }
        if (!extraHttpHeaders.isEmpty()) {
            headers.put("Gotenberg-Webhook-Extra-Http-Headers", new JSONObject(extraHttpHeaders).toString());
        }
        return headers;
    }
}
