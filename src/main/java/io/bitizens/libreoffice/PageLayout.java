package io.bitizens.libreoffice;

/**
 * PageLayout is how a viewer lays the pages out.
 */
public enum PageLayout {
    DEFAULT("default"),
    SINGLE_PAGE("singlePage"),
    ONE_COLUMN("oneColumn"),
    TWO_COLUMN_RIGHT("twoColumnRight");

    private final String value;

    PageLayout(String value) {
        this.value = value;
    }

    /**
     * @return The value Gotenberg takes.
     */
    public String value() {
        return value;
    }
}
