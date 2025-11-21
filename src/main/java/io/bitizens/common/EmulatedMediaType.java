package io.bitizens.common;

/**
 * PdfFormat is an enumeration of various PDF formats for document conversion.
 */
public enum EmulatedMediaType {
    /**
     * screen media type
     */
    SCREEN("screen"),

    /**
     * print media type
     */
    PRINT("print");

    private final String mediaType;

    /**
     * Constructs a EmulatedMediaType enum with the specified format string.
     *
     * @param mediaType The emulated media type string.
     */
    EmulatedMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Gets the emulated media type string for this EmulatedMediaType.
     *
     * @return The emulated media type string.
     */
    public String mediaType() {
        return this.mediaType;
    }
}
