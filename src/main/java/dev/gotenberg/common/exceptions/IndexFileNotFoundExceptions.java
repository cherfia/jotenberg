package dev.gotenberg.common.exceptions;

import java.io.FileNotFoundException;

public class IndexFileNotFoundExceptions extends FileNotFoundException {
    public IndexFileNotFoundExceptions() {
        super("No index.html file found.");
    }
}
