package io.bitizens.common.exceptions;

import java.io.FileNotFoundException;

/**
 * EmptyFileListException is an exception class that is thrown when a list of files is found to be empty.
 */
public class EmptyFileListException extends FileNotFoundException {
    /**
     * Constructs an EmptyFileListException with a default error message.
     */
    public EmptyFileListException() {
        super("Files should not be empty.");
    }
}
