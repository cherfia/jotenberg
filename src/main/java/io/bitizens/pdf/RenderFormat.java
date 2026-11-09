package io.bitizens.pdf;

/**
 * RenderFormat is the image format {@code render} draws pages in.
 */
public enum RenderFormat {
    PNG("png"),
    JPEG("jpeg");

    private final String value;

    RenderFormat(String value) {
        this.value = value;
    }

    /**
     * @return The value Gotenberg takes.
     */
    public String value() {
        return value;
    }
}
