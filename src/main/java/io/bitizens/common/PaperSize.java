package io.bitizens.common;

/**
 * PaperSize is a paper size Gotenberg knows by name.
 */
public enum PaperSize {
    A3("A3"),
    A4("A4"),
    A5("A5"),
    B4("B4"),
    B5("B5"),
    LETTER("Letter"),
    LEGAL("Legal"),
    TABLOID("Tabloid");

    private final String value;

    PaperSize(String value) {
        this.value = value;
    }

    /**
     * @return The name Gotenberg takes.
     */
    public String value() {
        return value;
    }
}
