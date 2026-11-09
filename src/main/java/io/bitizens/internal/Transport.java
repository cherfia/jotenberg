package io.bitizens.internal;

import io.bitizens.core.GotenbergException;
import io.bitizens.core.GotenbergResult;
import io.bitizens.core.JotenbergException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Internal: not part of Jotenberg's public API, and not exported by its module.
 * <p>
 * Transport sends requests to Gotenberg: it resolves routes against the endpoint, adds the authentication and
 * custom headers, and turns error answers into {@link GotenbergException}s.
 */
public final class Transport {
    private final HttpClient httpClient;
    private final String endpoint;
    private final Map<String, String> headers;
    private final Duration timeout;

    /**
     * @param httpClient The HTTP client to send with.
     * @param endpoint   The Gotenberg endpoint, such as {@code http://localhost:3000}.
     * @param headers    Headers sent with every request, authentication included.
     * @param timeout    The timeout of each request, or {@code null} for none.
     */
    public Transport(HttpClient httpClient, String endpoint, Map<String, String> headers, Duration timeout) {
        this.httpClient = httpClient;
        this.endpoint = endpoint.endsWith("/") ? endpoint : endpoint + "/";
        this.headers = Map.copyOf(headers);
        this.timeout = timeout;
    }

    /**
     * Builds the {@code Authorization} header value for basic authentication.
     *
     * @param username The username.
     * @param password The password.
     * @return The header value.
     */
    public static String basicAuth(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @return The Gotenberg endpoint, ending with a slash.
     */
    public String endpoint() {
        return endpoint;
    }

    /**
     * Posts a multipart form to a route.
     *
     * @param route          The route, such as {@code forms/pdf/merge}.
     * @param form           The form to send.
     * @param requestHeaders Headers of this request only, such as {@code Gotenberg-Trace}.
     * @return What Gotenberg answered.
     */
    public GotenbergResult post(String route, Multipart form, Map<String, String> requestHeaders) {
        HttpRequest.Builder request = request(route, requestHeaders)
                .header("Content-Type", form.contentType())
                .POST(form.body());
        HttpResponse<byte[]> response = send(route, request.build());
        return new GotenbergResult(response.body(), response.headers());
    }

    /**
     * Sends a request without a body.
     *
     * @param route          The route, such as {@code health}.
     * @param method         {@code GET} or {@code HEAD}.
     * @param requestHeaders Headers of this request only.
     * @return The answer.
     */
    public HttpResponse<byte[]> fetch(String route, String method, Map<String, String> requestHeaders) {
        HttpRequest.Builder request = request(route, requestHeaders).method(method, HttpRequest.BodyPublishers.noBody());
        return send(route, request.build());
    }

    private HttpRequest.Builder request(String route, Map<String, String> requestHeaders) {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(endpoint + route));
        if (timeout != null) {
            request.timeout(timeout);
        }
        Map<String, String> all = new LinkedHashMap<>(headers);
        all.putAll(requestHeaders);
        all.forEach(request::header);
        return request;
    }

    private HttpResponse<byte[]> send(String route, HttpRequest request) {
        HttpResponse<byte[]> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException e) {
            throw new JotenbergException("Could not reach Gotenberg at " + request.uri(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new JotenbergException("Interrupted while waiting for Gotenberg at " + request.uri(), e);
        }

        if (response.statusCode() >= 400) {
            throw new GotenbergException(
                    response.statusCode(),
                    response.headers().firstValue("Gotenberg-Trace").orElse(null),
                    new String(response.body(), StandardCharsets.UTF_8).trim(),
                    route);
        }
        return response;
    }
}
