package io.bitizens.pdf;

import io.bitizens.common.FileInput;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;

/**
 * StageFile is a file a stage needs, such as an image to stamp or a certificate to sign with. Jotenberg uploads it
 * with the request and puts its name in the stage; {@link #named(String)} instead refers to a file the request already
 * carries, through an upload, {@code archives} or {@code downloadFrom}.
 */
public final class StageFile {
    private final String name;
    private final FileInput upload;

    private StageFile(String name, FileInput upload) {
        this.name = name;
        this.upload = upload;
    }

    /**
     * @param path A file on disk, uploaded under its name.
     * @return The stage file.
     */
    public static StageFile of(Path path) {
        return of(FileInput.of(path));
    }

    /**
     * @param file A file on disk, uploaded under its name.
     * @return The stage file.
     */
    public static StageFile of(File file) {
        return of(FileInput.of(file));
    }

    /**
     * @param content The file content.
     * @param name    The name to upload it under, extension included.
     * @return The stage file.
     */
    public static StageFile of(byte[] content, String name) {
        return of(FileInput.of(content, name));
    }

    /**
     * @param content The file content, read to its end and closed.
     * @param name    The name to upload it under, extension included.
     * @return The stage file.
     */
    public static StageFile of(InputStream content, String name) {
        return of(FileInput.of(content, name));
    }

    /**
     * @param input The file to upload.
     * @return The stage file.
     */
    public static StageFile of(FileInput input) {
        return new StageFile(input.name(), Objects.requireNonNull(input, "input"));
    }

    /**
     * Refers to a file the request already carries, without uploading it again: an HTML asset, an entry of an
     * archive, or a downloaded file.
     *
     * @param name The file's name.
     * @return The stage file.
     */
    public static StageFile named(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A stage file needs a name");
        }
        return new StageFile(name, null);
    }

    /**
     * @return The name the stage refers to.
     */
    public String name() {
        return name;
    }

    /**
     * @return The file to upload, or {@code null} for a file the request already carries.
     */
    FileInput upload() {
        return upload;
    }
}
