package io.bitizens.pdf;

import io.bitizens.common.FileInput;
import io.bitizens.common.Request;
import io.bitizens.internal.Multipart;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * PdfRead holds the PDFs a {@link PdfClient} read method reads. The read routes answer JSON, so they take no webhook
 * and no output filename.
 */
public final class PdfRead extends Request<PdfRead> {
    private final List<FileInput> files = new ArrayList<>();
    private String password;

    private PdfRead() {
    }

    /**
     * @param files PDFs on disk, read under their names.
     * @return The request.
     */
    public static PdfRead of(Path... files) {
        PdfRead request = new PdfRead();
        for (Path file : files) {
            request.file(file);
        }
        return request;
    }

    /**
     * @param files PDFs, on disk or in memory.
     * @return The request.
     */
    public static PdfRead of(FileInput... files) {
        PdfRead request = new PdfRead();
        for (FileInput file : files) {
            request.file(file);
        }
        return request;
    }

    /**
     * @param file A PDF on disk.
     * @return This request.
     */
    public PdfRead file(Path file) {
        return file(FileInput.of(file));
    }

    /**
     * @param file A PDF, on disk or in memory; its name must end with {@code .pdf}.
     * @return This request.
     */
    public PdfRead file(FileInput file) {
        PdfInputs.check(file);
        files.add(file);
        return this;
    }

    /**
     * @param password Opens encrypted PDFs.
     * @return This request.
     */
    public PdfRead password(String password) {
        this.password = password;
        return this;
    }

    void addTo(Multipart form) {
        files.forEach(file -> file.addTo(form, "files"));
        addInputsTo(form);
        if (password != null) {
            form.field("password", password);
        }
    }
}
