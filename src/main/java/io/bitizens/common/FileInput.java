package io.bitizens.common;

import io.bitizens.internal.Multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * FileInput is a file to upload: a file on disk, kept under its name or another, or in-memory content under a name.
 * Gotenberg tells uploads apart by name, and takes files in the alphanumeric order of their names.
 */
public final class FileInput {
    private final String name;
    private final Path path;
    private final byte[] content;

    private FileInput(String name, Path path, byte[] content) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A file to upload needs a name");
        }
        this.name = name;
        this.path = path;
        this.content = content;
    }

    /**
     * @param path A file on disk, uploaded under its name.
     * @return The input.
     */
    public static FileInput of(Path path) {
        Objects.requireNonNull(path, "path");
        return new FileInput(path.getFileName().toString(), path, null);
    }

    /**
     * @param file A file on disk, uploaded under its name.
     * @return The input.
     */
    public static FileInput of(File file) {
        return of(Objects.requireNonNull(file, "file").toPath());
    }

    /**
     * @param path A file on disk.
     * @param name The name to upload it under, such as {@code 1-cover.pdf}.
     * @return The input.
     */
    public static FileInput of(Path path, String name) {
        return new FileInput(name, Objects.requireNonNull(path, "path"), null);
    }

    /**
     * @param content The file content.
     * @param name    The name to upload it under, extension included, such as {@code report.docx}.
     * @return The input.
     */
    public static FileInput of(byte[] content, String name) {
        return new FileInput(name, null, Objects.requireNonNull(content, "content").clone());
    }

    /**
     * Reads a stream to its end and closes it.
     *
     * @param content The file content.
     * @param name    The name to upload it under, extension included.
     * @return The input.
     */
    public static FileInput of(InputStream content, String name) {
        try (InputStream in = Objects.requireNonNull(content, "content")) {
            return new FileInput(name, null, in.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * @return The name Gotenberg sees.
     */
    public String name() {
        return name;
    }

    /**
     * @return The file's extension, lowercase, without the dot; empty if it has none.
     */
    public String extension() {
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(dot + 1).toLowerCase();
    }

    /**
     * Adds this file to a form.
     *
     * @param form      The form.
     * @param fieldName The form field, such as {@code files}.
     */
    public void addTo(Multipart form, String fieldName) {
        if (path != null) {
            form.file(fieldName, name, path);
        } else {
            form.file(fieldName, name, content);
        }
    }

    /**
     * @param newName The name to upload the file under.
     * @return The same file under another name.
     */
    public FileInput withName(String newName) {
        return new FileInput(newName, path, content);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FileInput other)) {
            return false;
        }
        return name.equals(other.name) && Objects.equals(path, other.path) && java.util.Arrays.equals(content, other.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, java.util.Arrays.hashCode(content));
    }

    @Override
    public String toString() {
        return "FileInput[" + name + "]";
    }
}
