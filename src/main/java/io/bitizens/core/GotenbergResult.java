package io.bitizens.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLDecoder;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GotenbergResult is what Gotenberg answered: the file, and the headers that describe it. One result is a single file,
 * such as a PDF or an image; several come back in a ZIP, which {@link #isZip()} tells apart.
 */
public final class GotenbergResult {
    private static final Pattern FILENAME_EXTENDED = Pattern.compile("filename\\*\\s*=\\s*UTF-8''([^;]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern FILENAME = Pattern.compile("filename\\s*=\\s*\"((?:[^\"\\\\]|\\\\.)*)\"|filename\\s*=\\s*([^;\\s]+)", Pattern.CASE_INSENSITIVE);

    private final byte[] bytes;
    private final HttpHeaders headers;

    /**
     * @param bytes   The body Gotenberg answered.
     * @param headers The headers Gotenberg answered.
     */
    public GotenbergResult(byte[] bytes, HttpHeaders headers) {
        this.bytes = bytes;
        this.headers = headers;
    }

    /**
     * @return The file's content; empty when the request went to a webhook.
     */
    public byte[] bytes() {
        return bytes.clone();
    }

    /**
     * @return The file's content as UTF-8 text, such as the result of {@code extract-text} on one PDF.
     */
    public String text() {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Writes the file to disk.
     *
     * @param path Where to write it.
     * @return The path.
     */
    public Path writeTo(Path path) {
        try {
            return Files.write(path, bytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * @return The media type, such as {@code application/pdf}, {@code image/png} or {@code application/zip}.
     */
    public Optional<String> contentType() {
        return headers.firstValue("Content-Type").map(value -> value.split(";")[0].trim());
    }

    /**
     * @return Whether several files came back in a ZIP.
     */
    public boolean isZip() {
        return contentType().map("application/zip"::equals).orElse(false);
    }

    /**
     * @return The file name Gotenberg gave the result, such as {@code report.pdf}.
     */
    public Optional<String> filename() {
        return headers.firstValue("Content-Disposition").flatMap(GotenbergResult::filename);
    }

    static Optional<String> filename(String disposition) {
        Matcher extended = FILENAME_EXTENDED.matcher(disposition);
        if (extended.find()) {
            return Optional.of(URLDecoder.decode(extended.group(1).trim().replace("+", "%2B"), StandardCharsets.UTF_8));
        }
        Matcher plain = FILENAME.matcher(disposition);
        if (plain.find()) {
            String quoted = plain.group(1);
            return Optional.of(quoted != null ? quoted.replaceAll("\\\\(.)", "$1") : plain.group(2));
        }
        return Optional.empty();
    }

    /**
     * @return The request's {@code Gotenberg-Trace}, to find it in Gotenberg's logs.
     */
    public Optional<String> trace() {
        return headers.firstValue("Gotenberg-Trace");
    }

    /**
     * @return The version of Gotenberg that answered.
     */
    public Optional<String> gotenbergVersion() {
        return headers.firstValue("Gotenberg-Version");
    }

    /**
     * After a {@code remediate} stage: the violations left, summed over the documents and the profiles. {@code 0}
     * means every document conforms to every profile.
     *
     * @return The count, or empty without a {@code remediate} stage.
     */
    public OptionalInt remediationResidual() {
        return intHeader("Gotenberg-Remediation-Residual");
    }

    /**
     * After a {@code remediate} stage: the fixes Gotenberg undid because they lost pages, text or images.
     *
     * @return The count, or empty without a {@code remediate} stage.
     */
    public OptionalInt remediationRolledBack() {
        return intHeader("Gotenberg-Remediation-Rolled-Back");
    }

    /**
     * @param name A response header, such as {@code Content-Length}.
     * @return Its values; empty if absent.
     */
    public List<String> header(String name) {
        return headers.allValues(name);
    }

    private OptionalInt intHeader(String name) {
        return headers.firstValue(name)
                .map(String::trim)
                .filter(value -> value.matches("-?\\d+"))
                .map(value -> OptionalInt.of(Integer.parseInt(value)))
                .orElse(OptionalInt.empty());
    }

    @Override
    public String toString() {
        return "GotenbergResult[" + contentType().orElse("?") + ", " + bytes.length + " bytes"
                + filename().map(name -> ", " + name).orElse("") + "]";
    }
}
