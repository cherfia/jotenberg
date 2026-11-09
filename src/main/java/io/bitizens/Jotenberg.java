package io.bitizens;

import io.bitizens.chromium.ChromiumClient;
import io.bitizens.core.Transport;
import io.bitizens.libreoffice.LibreOfficeClient;
import io.bitizens.pdf.PdfClient;
import io.bitizens.system.SystemClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Jotenberg is a client for Gotenberg's HTTP API. It's thread-safe: create one and reuse it.
 *
 * <pre>{@code
 * Jotenberg client = new Jotenberg("http://localhost:3000");
 * byte[] pdf = client.chromium().convert(UrlConversion.of("https://example.com"));
 * }</pre>
 *
 * Every method returns the result, and throws a {@link io.bitizens.core.GotenbergException} when Gotenberg answers an
 * error, or a {@link io.bitizens.core.JotenbergException} when it can't be reached.
 */
public final class Jotenberg implements AutoCloseable {
    /**
     * The Chromiumly hosted API, used when only an API key is configured.
     */
    public static final String HOSTED_API_ENDPOINT = "https://api.chromiumly.dev";

    private final Transport transport;
    private final ChromiumClient chromium;
    private final LibreOfficeClient libreOffice;
    private final PdfClient pdf;
    private final SystemClient system;

    /**
     * @param endpoint The Gotenberg endpoint, such as {@code http://localhost:3000}.
     */
    public Jotenberg(String endpoint) {
        this(builder().endpoint(endpoint));
    }

    private Jotenberg(Builder builder) {
        this.transport = new Transport(builder.httpClient(), builder.resolveEndpoint(), builder.headers(), builder.timeout);
        this.chromium = new ChromiumClient(transport);
        this.libreOffice = new LibreOfficeClient(transport);
        this.pdf = new PdfClient(transport);
        this.system = new SystemClient(transport);
    }

    /**
     * @return A builder, to set authentication, headers and timeouts.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Configures a client from the environment: {@code GOTENBERG_ENDPOINT}, {@code GOTENBERG_API_BASIC_AUTH_USERNAME}
     * and {@code GOTENBERG_API_BASIC_AUTH_PASSWORD}, or {@code CHROMIUMLY_API_KEY} for the hosted API.
     *
     * @return The client.
     */
    public static Jotenberg fromEnvironment() {
        return fromEnvironment(System::getenv);
    }

    static Jotenberg fromEnvironment(Function<String, String> env) {
        Builder builder = builder();
        String endpoint = env.apply("GOTENBERG_ENDPOINT");
        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpoint(endpoint);
        }
        String username = env.apply("GOTENBERG_API_BASIC_AUTH_USERNAME");
        String password = env.apply("GOTENBERG_API_BASIC_AUTH_PASSWORD");
        if (username != null && password != null) {
            builder.basicAuth(username, password);
        }
        String apiKey = env.apply("CHROMIUMLY_API_KEY");
        if (apiKey != null && !apiKey.isBlank()) {
            builder.apiKey(apiKey);
        }
        return builder.build();
    }

    /**
     * @return The Chromium routes: URL, HTML and Markdown to PDF, and screenshots.
     */
    public ChromiumClient chromium() {
        return chromium;
    }

    /**
     * @return The LibreOffice route: office documents to PDF.
     */
    public LibreOfficeClient libreOffice() {
        return libreOffice;
    }

    /**
     * @return The PDF routes: stages on existing PDFs, and reading PDFs.
     */
    public PdfClient pdf() {
        return pdf;
    }

    /**
     * @return The system routes: health, version and debug.
     */
    public SystemClient system() {
        return system;
    }

    /**
     * @return The Gotenberg endpoint, ending with a slash.
     */
    public String endpoint() {
        return transport.endpoint();
    }

    /**
     * Kept for try-with-resources; the client holds no resources that need closing.
     */
    @Override
    public void close() {
    }

    /**
     * Builder configures a {@link Jotenberg} client.
     */
    public static final class Builder {
        private final Map<String, String> headers = new LinkedHashMap<>();
        private String endpoint;
        private String basicAuth;
        private String apiKey;
        private Duration timeout;
        private HttpClient httpClient;

        private Builder() {
        }

        /**
         * @param endpoint The Gotenberg endpoint, such as {@code http://localhost:3000}. Optional with an API key.
         * @return This builder.
         */
        public Builder endpoint(String endpoint) {
            URI uri;
            try {
                uri = URI.create(endpoint);
            } catch (IllegalArgumentException | NullPointerException e) {
                throw new IllegalArgumentException("Invalid Gotenberg endpoint: " + endpoint, e);
            }
            if (!"http".equals(uri.getScheme()) && !"https".equals(uri.getScheme()) || uri.getHost() == null) {
                throw new IllegalArgumentException("The Gotenberg endpoint must be an absolute http or https URL, got " + endpoint);
            }
            this.endpoint = endpoint;
            return this;
        }

        /**
         * Authenticates with basic auth, for Gotenberg started with {@code --api-enable-basic-auth}.
         *
         * @param username The username.
         * @param password The password.
         * @return This builder.
         */
        public Builder basicAuth(String username, String password) {
            this.basicAuth = Transport.basicAuth(username, password);
            return this;
        }

        /**
         * Authenticates with an {@code X-Api-Key}, over basic auth. Without an endpoint, requests go to the hosted
         * API at {@value #HOSTED_API_ENDPOINT}.
         *
         * @param apiKey The API key.
         * @return This builder.
         */
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Sends a header with every request, such as a bearer token for a proxy in front of Gotenberg.
         *
         * @param name  The header name.
         * @param value The header value.
         * @return This builder.
         */
        public Builder header(String name, String value) {
            headers.put(name, value);
            return this;
        }

        /**
         * @param timeout The timeout of each request; none by default.
         * @return This builder.
         */
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * @param httpClient The HTTP client to send with, for proxies, SSL or executors.
         * @return This builder.
         */
        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        /**
         * @return The client.
         */
        public Jotenberg build() {
            return new Jotenberg(this);
        }

        private String resolveEndpoint() {
            if (endpoint != null) {
                return endpoint;
            }
            if (apiKey != null) {
                return HOSTED_API_ENDPOINT;
            }
            throw new IllegalStateException("Set a Gotenberg endpoint, or an API key for the hosted API");
        }

        private Map<String, String> headers() {
            Map<String, String> all = new LinkedHashMap<>(headers);
            if (apiKey != null) {
                all.put("X-Api-Key", apiKey);
            } else if (basicAuth != null) {
                all.put("Authorization", basicAuth);
            }
            return all;
        }

        private HttpClient httpClient() {
            return httpClient != null ? httpClient : HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
        }
    }
}
