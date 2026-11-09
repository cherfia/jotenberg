package io.bitizens.system;

import io.bitizens.core.GotenbergException;
import io.bitizens.core.JotenbergException;
import io.bitizens.internal.Transport;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * SystemClient wraps Gotenberg's system routes: health, version and debug.
 */
public final class SystemClient {
    private final Transport transport;

    /**
     * @param transport The transport to send with.
     */
    public SystemClient(Transport transport) {
        this.transport = transport;
    }

    /**
     * Reads the health report. An engine that is down answers 503, which is reported here, not thrown.
     *
     * @return The health report.
     */
    public Health health() {
        try {
            return Health.fromJson(parse(text(transport.fetch("health", "GET", Map.of()))));
        } catch (GotenbergException e) {
            if (e.status() == 503) {
                return Health.fromJson(parse(e.body()));
            }
            throw e;
        }
    }

    /**
     * Checks that Gotenberg is up with a lightweight {@code HEAD /health}.
     *
     * @return Whether Gotenberg answered 200; {@code false} when it answered an error or couldn't be reached.
     */
    public boolean isUp() {
        try {
            return transport.fetch("health", "HEAD", Map.of()).statusCode() == 200;
        } catch (GotenbergException | JotenbergException e) {
            return false;
        }
    }

    /**
     * @return The version of Gotenberg, such as {@code 9.2.0}.
     */
    public String version() {
        return text(transport.fetch("version", "GET", Map.of())).trim();
    }

    /**
     * Reads the modules, the engine versions and every flag. Gotenberg answers 404 until
     * {@code --api-enable-debug-route} is on.
     *
     * @return The debug report, as nested maps and lists.
     */
    public Map<String, Object> debug() {
        return parse(text(transport.fetch("debug", "GET", Map.of()))).toMap();
    }

    private static JSONObject parse(String body) {
        try {
            return new JSONObject(body);
        } catch (JSONException e) {
            throw new JotenbergException("Gotenberg answered something other than JSON: " + body, e);
        }
    }

    private static String text(HttpResponse<byte[]> response) {
        return new String(response.body(), StandardCharsets.UTF_8);
    }
}
