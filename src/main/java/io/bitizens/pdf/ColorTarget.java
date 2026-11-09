package io.bitizens.pdf;

/**
 * ColorTarget is the color space {@code convert-colors} converts every color to.
 */
public enum ColorTarget {
    CMYK("cmyk"),
    RGB("rgb"),
    GRAY("gray"),
    OUTPUT_INTENT("output-intent");

    private final String value;

    ColorTarget(String value) {
        this.value = value;
    }

    /**
     * @return The value Gotenberg takes.
     */
    public String value() {
        return value;
    }
}
