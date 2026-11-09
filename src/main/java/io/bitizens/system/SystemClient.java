package io.bitizens.system;

import io.bitizens.core.GotenbergException;
import io.bitizens.core.Transport;
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
            return Health.fromJson(new JSONObject(text(transport.fetch("health", "GET", Map.of()))));
        } catch (GotenbergException e) {
            if (e.status() == 503) {
                return Health.fromJson(new JSONObject(e.body()));
            }
            throw e;
        }
    }

    /**
     * Checks that Gotenberg is up with a lightweight {@code HEAD /health}.
     *
     * @return Whether Gotenberg answered 200.
     */
    public boolean isUp() {
        try {
            return transport.fetch("health", "HEAD", Map.of()).statusCode() == 200;
        } catch (GotenbergException e) {
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
     * @return The debug report.
     */
    public JSONObject debug() {
        return new JSONObject(text(transport.fetch("debug", "GET", Map.of())));
    }

    private static String text(HttpResponse<byte[]> response) {
        return new String(response.body(), StandardCharsets.UTF_8);
    }
}
