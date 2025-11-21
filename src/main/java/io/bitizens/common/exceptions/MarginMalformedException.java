package io.bitizens.common.exceptions;

/**
 * MarginMalformedException is an exception class that is thrown when a margin value is found to be negative.
 */
public class MarginMalformedException extends ArithmeticException {
    /**
     * Constructs a MarginMalformedException with a default error message.
     */
    public MarginMalformedException() {
        super("Negative margins are not allowed.");
    }
}
