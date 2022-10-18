package dev.inaka.common.exceptions;

import java.io.FileNotFoundException;

public class EmptyFileListException extends FileNotFoundException {
    public EmptyFileListException() {
        super("Files should not be empty.");
    }
}
