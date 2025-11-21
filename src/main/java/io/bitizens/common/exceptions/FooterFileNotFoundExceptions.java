package io.bitizens.common.exceptions;

import java.io.FileNotFoundException;

/**
 * FooterFileNotFoundExceptions is an exception class that is thrown when a footer.html file is not found.
 */
public class FooterFileNotFoundExceptions extends FileNotFoundException {
    /**
     * Constructs a FooterFileNotFoundExceptions with a default error message.
     */
    public FooterFileNotFoundExceptions() {
        super("No footer.html file found.");
    }
}
