package io.bitizens.common.exceptions;

import java.io.FileNotFoundException;

/**
 * HeaderFileNotFoundExceptions is an exception class that is thrown when a header.html file is not found.
 */
public class HeaderFileNotFoundExceptions extends FileNotFoundException {
    /**
     * Constructs a HeaderFileNotFoundExceptions with a default error message.
     */
    public HeaderFileNotFoundExceptions() {
        super("No header.html file found.");
    }
}
