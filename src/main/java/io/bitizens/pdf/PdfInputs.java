package io.bitizens.pdf;

import io.bitizens.common.FileInput;

final class PdfInputs {
    private PdfInputs() {
    }

    static void check(FileInput file) {
        if (!file.extension().equals("pdf")) {
            throw new IllegalArgumentException("PDF routes take .pdf files, got " + file.name()
                    + "; send other files a stage needs as a StageFile");
        }
    }
}
