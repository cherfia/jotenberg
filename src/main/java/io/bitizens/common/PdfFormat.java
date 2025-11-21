package io.bitizens.common;

/**
 * PdfFormat is an enumeration of various PDF formats for document conversion.
 */
public enum PdfFormat {
    /**
     * PDF/A-1a format.
     */
    A_1A("PDF/A-1a"),

    /**
     * PDF/A-2b format.
     */
    A_2B("PDF/A-2b"),

    /**
     * PDF/A-3b format.
     */
    A_3B("PDF/A-3b");

    private final String format;

    /**
     * Constructs a PdfFormat enum with the specified format string.
     *
     * @param format The PDF format string.
     */
    PdfFormat(String format) {
        this.format = format;
    }

    /**
     * Gets the PDF format string for this PdfFormat.
     *
     * @return The PDF format string.
     */
    public String format() {
        return this.format;
    }
}
