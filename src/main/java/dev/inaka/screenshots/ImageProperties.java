package dev.inaka.screenshots;

/**
 * The ImageProperties class is used to store the configuration properties for generating a screenshot.
 */
public final class ImageProperties {
    private final String format;
    private final String quality;
    private final String omitBackground;

    private ImageProperties(Builder builder) {
        format = builder.format;
        quality = builder.quality;
        omitBackground = builder.omitBackground;
    }

    /**
     * The Builder class is used to construct instances of ImageProperties with specific configuration options.
     */
    public static class Builder {

        private String format = ImageFormat.PNG.format();
        private String quality = null;
        private String omitBackground = "false";


        public Builder addFormat(String format) {
            this.format = format;
            return this;
        }

        public Builder addQuality(Integer quality) {
            if (quality < 0 || quality > 100) {
                throw new IllegalArgumentException("Quality must be between 0 and 100");
            }
            this.quality = quality.toString();
            return this;
        }

        public Builder addOmitBackground(boolean omitBackground) {
            this.omitBackground = String.valueOf(omitBackground);
            return this;
        }

        /**
         * Builds an instance of ImageProperties with the specified configuration options.
         *
         * @return An instance of ImageProperties with the specified configuration options.
         */
        public ImageProperties build() {
            return new ImageProperties(this);
        }
    }
}

