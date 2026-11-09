package io.bitizens.libreoffice;

/**
 * NativePdfa is a PDF/A level LibreOffice exports directly. The {@code remediate} stage reaches the other levels.
 */
public enum NativePdfa {
    PDF_A_1B("PDF/A-1b"),
    PDF_A_2B("PDF/A-2b"),
    PDF_A_3B("PDF/A-3b"),
    PDF_A_4("PDF/A-4");

    private final String value;

    NativePdfa(String value) {
        this.value = value;
    }

    /**
     * @return The value Gotenberg takes.
     */
    public String value() {
        return value;
    }
}
