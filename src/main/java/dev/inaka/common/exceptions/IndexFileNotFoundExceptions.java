package dev.inaka.common.exceptions;

import java.io.FileNotFoundException;

/**
 * IndexFileNotFoundExceptions is an exception class that is thrown when an index.html file is not found.
 */
public class IndexFileNotFoundExceptions extends FileNotFoundException {
    /**
     * Constructs an IndexFileNotFoundExceptions with a default error message.
     */
    public IndexFileNotFoundExceptions() {
        super("No index.html file found.");
    }
}
