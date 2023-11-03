package dev.inaka.common.exceptions;

/**
 * PageRangeMalformedException is an exception class that is thrown when a page range is having negative values.
 */
public class PageRangeMalformedException extends ArithmeticException {
    /**
     * Constructs a PageRangeMalformedException with a default error message.
     */
    public PageRangeMalformedException() {
        super("Negative page ranges are not allowed.");
    }
}
