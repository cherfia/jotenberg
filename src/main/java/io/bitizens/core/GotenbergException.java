package io.bitizens.core;

/**
 * GotenbergException is thrown when Gotenberg answers a request with an error status.
 */
public class GotenbergException extends RuntimeException {
    private final int status;
    private final String trace;
    private final String body;

    /**
     * @param status The HTTP status Gotenberg answered.
     * @param trace  The request's {@code Gotenberg-Trace}, or {@code null}.
     * @param body   The error message Gotenberg answered.
     * @param route  The route that answered.
     */
    public GotenbergException(int status, String trace, String body, String route) {
        super("Gotenberg answered " + status + " on " + route + (trace == null ? "" : " (trace " + trace + ")") + ": " + body);
        this.status = status;
        this.trace = trace;
        this.body = body;
    }

    /**
     * @return The HTTP status Gotenberg answered, such as 400 or 409.
     */
    public int status() {
        return status;
    }

    /**
     * @return The request's {@code Gotenberg-Trace}, to find it in Gotenberg's logs; {@code null} if absent.
     */
    public String trace() {
        return trace;
    }

    /**
     * @return The error message Gotenberg answered, which says what failed and how to fix it.
     */
    public String body() {
        return body;
    }
}
