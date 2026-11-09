package io.bitizens.pdf;

import io.bitizens.common.DeliveredRequest;
import io.bitizens.common.FileInput;
import io.bitizens.core.Multipart;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * PdfRequest holds the PDFs a {@link PdfClient} stage method runs on, and the request options. Gotenberg takes the
 * PDFs in the alphanumeric order of their names, and stages and results refer to them by name.
 */
public final class PdfRequest extends DeliveredRequest<PdfRequest> {
    private final List<FileInput> files = new ArrayList<>();

    private PdfRequest() {
    }

    /**
     * @param files PDFs on disk, uploaded under their names.
     * @return The request.
     */
    public static PdfRequest of(Path... files) {
        PdfRequest request = new PdfRequest();
        for (Path file : files) {
            request.file(file);
        }
        return request;
    }

    /**
     * @param files PDFs, on disk or in memory.
     * @return The request.
     */
    public static PdfRequest of(FileInput... files) {
        PdfRequest request = new PdfRequest();
        for (FileInput file : files) {
            request.file(file);
        }
        return request;
    }

    /**
     * @param file A PDF on disk, uploaded under its name.
     * @return This request.
     */
    public PdfRequest file(Path file) {
        return file(FileInput.of(file));
    }

    /**
     * @param file A PDF, on disk or in memory; its name must end with {@code .pdf}.
     * @return This request.
     */
    public PdfRequest file(FileInput file) {
        PdfInputs.check(file);
        files.add(file);
        return this;
    }

    void addTo(Multipart form) {
        files.forEach(file -> file.addTo(form, "files"));
        addInputsTo(form);
    }
}
