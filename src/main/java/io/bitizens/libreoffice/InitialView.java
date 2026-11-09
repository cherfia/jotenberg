package io.bitizens.libreoffice;

/**
 * InitialView is the side panel a viewer opens.
 */
public enum InitialView {
    DEFAULT("default"),
    OUTLINE("outline"),
    THUMBNAILS("thumbnails");

    private final String value;

    InitialView(String value) {
        this.value = value;
    }

    /**
     * @return The value Gotenberg takes.
     */
    public String value() {
        return value;
    }
}
