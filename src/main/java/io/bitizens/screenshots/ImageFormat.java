package io.bitizens.screenshots;

/**
 * ImageFormat is an enumeration of various image formats for screenshots.
 */
public enum ImageFormat {
    /**
     * png format.
     */
    PNG("png"),

    /**
     * jpeg format.
     */
    JPEG("jpeg"),

    /**
     * webp format.
     */
    WEBP("webp");

    private final String format;

    /**
     * Constructs an ImageFormat enum with the specified format string.
     *
     * @param format The image format string.
     */
    ImageFormat(String format) {
        this.format = format;
    }

    /**
     * Gets the image format string for this ImageFormat.
     *
     * @return The image format string.
     */
    public String format() {
        return this.format;
    }
}
