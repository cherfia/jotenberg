package io.bitizens.core;

/**
 * JotenbergException is thrown when a request can't reach Gotenberg or is interrupted.
 */
public class JotenbergException extends RuntimeException {
    /**
     * @param message What failed.
     * @param cause   The underlying error.
     */
    public JotenbergException(String message, Throwable cause) {
        super(message, cause);
    }
}
