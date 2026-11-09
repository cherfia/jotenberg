package io.bitizens.internal;

import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Internal: not part of Jotenberg's public API, and not exported by its module.
 * <p>
 * Multipart is a builder for a {@code multipart/form-data} request body. Files on disk are streamed, not
 * read into memory.
 */
public final class Multipart {
    private final String boundary = "jotenberg-" + UUID.randomUUID();
    private final List<BodyPublisher> parts = new ArrayList<>();
    private final List<String> fieldNames = new ArrayList<>();
    private final Set<String> fileNames = new LinkedHashSet<>();

    /**
     * Adds a text field.
     *
     * @param name  The field name.
     * @param value The field value.
     * @return This builder.
     */
    public Multipart field(String name, String value) {
        parts.add(BodyPublishers.ofString(
                "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"" + escape(name) + "\"\r\n"
                        + "Content-Type: text/plain; charset=UTF-8\r\n\r\n"
                        + value + "\r\n",
                StandardCharsets.UTF_8));
        fieldNames.add(name);
        return this;
    }

    /**
     * Adds a file from disk under the given form field, streaming its content.
     *
     * @param fieldName The form field, such as {@code files} or {@code archives}.
     * @param fileName  The name Gotenberg sees.
     * @param path      The file to send.
     * @return This builder.
     */
    public Multipart file(String fieldName, String fileName, Path path) {
        if (!Files.isReadable(path)) {
            throw new UncheckedIOException(new FileNotFoundException(path.toString()));
        }
        parts.add(fileHeader(fieldName, fileName));
        try {
            parts.add(BodyPublishers.ofFile(path));
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
        parts.add(BodyPublishers.ofString("\r\n"));
        return this;
    }

    /**
     * Adds in-memory content under the given form field.
     *
     * @param fieldName The form field, such as {@code files} or {@code archives}.
     * @param fileName  The name Gotenberg sees.
     * @param content   The file content.
     * @return This builder.
     */
    public Multipart file(String fieldName, String fileName, byte[] content) {
        parts.add(fileHeader(fieldName, fileName));
        parts.add(BodyPublishers.ofByteArray(content));
        parts.add(BodyPublishers.ofString("\r\n"));
        return this;
    }

    private BodyPublisher fileHeader(String fieldName, String fileName) {
        if (fieldName.equals("files") && !fileNames.add(fileName)) {
            throw new IllegalArgumentException("Two uploaded files are named " + fileName
                    + "; rename one of them, or have a stage reuse the upload with StageFile.named(\"" + fileName + "\")");
        }
        fieldNames.add(fieldName);
        return BodyPublishers.ofString(
                "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"" + escape(fieldName) + "\"; filename=\"" + escape(fileName) + "\"\r\n"
                        + "Content-Type: application/octet-stream\r\n\r\n",
                StandardCharsets.UTF_8);
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
    }

    /**
     * @return The names of the files uploaded under {@code files}, in order.
     */
    public Set<String> fileNames() {
        return Set.copyOf(fileNames);
    }

    /**
     * @return The names of every part, fields and files, in order. Useful for tests and logging.
     */
    public List<String> fieldNames() {
        return List.copyOf(fieldNames);
    }

    /**
     * @return The {@code Content-Type} header value, with the boundary.
     */
    public String contentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    /**
     * @return The request body.
     */
    public BodyPublisher body() {
        List<BodyPublisher> all = new ArrayList<>(parts);
        all.add(BodyPublishers.ofString("--" + boundary + "--\r\n"));
        return BodyPublishers.concat(all.toArray(BodyPublisher[]::new));
    }
}
