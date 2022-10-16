package dev.gotenberg.common;

public enum PdfFormat {
    A_1A("PDF/A-1a"),
    A_2B("PDF/A-2b"),
    A_3B("PDF/A-3b"),
    ;

    private final String format;

    PdfFormat(String format) {
        this.format = format;
    }


    public String format() {
        return this.format;
    }
}
