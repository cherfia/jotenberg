package io.bitizens.libreoffice;

/**
 * Magnification is how a viewer fits the page.
 */
public enum Magnification {
    DEFAULT("default"),
    FIT_WINDOW("fitWindow"),
    FIT_WIDTH("fitWidth"),
    FIT_VISIBLE("fitVisible"),
    ZOOM("zoom");

    private final String value;

    Magnification(String value) {
        this.value = value;
    }

    /**
     * @return The value Gotenberg takes.
     */
    public String value() {
        return value;
    }
}
