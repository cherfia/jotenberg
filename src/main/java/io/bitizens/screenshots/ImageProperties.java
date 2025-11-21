package io.bitizens.screenshots;

/**
 * The ImageProperties class is used to store the configuration properties for generating a screenshot.
 */
public final class ImageProperties {
    private final String format;
    private final String quality;
    private final String omitBackground;
    private final String width;
    private final String height;
    private final String clip;

    private ImageProperties(Builder builder) {
        format = builder.format;
        quality = builder.quality;
        omitBackground = builder.omitBackground;
        width = builder.width;
        height = builder.height;
        clip = builder.clip;
    }

    /**
     * The Builder class is used to construct instances of ImageProperties with specific configuration options.
     */
    public static class Builder {

        private String format = ImageFormat.PNG.format();
        private String quality = null;
        private String omitBackground = "false";
        private String width = "800";
        private String height = "600";
        private String clip = "false";

        /**
         * Sets the image compression format to the ImageProperties being constructed.
         *
         * @param format The image compression format, either "png", "jpeg" or "webp".
         * @return The Builder instance for method chaining.
         */
        public Builder addFormat(String format) {
            this.format = format;
            return this;
        }

        /**
         * Sets the image compression quality to the ImageProperties being constructed.
         *
         * @param quality The compression quality from range 0 to 100 (jpeg only).
         * @return The Builder instance for method chaining.
         * @throws IllegalArgumentException if the quality is not within the valid range (0 to 100).
         */
        public Builder addQuality(Integer quality) {
            if (quality < 0 || quality > 100) {
                throw new IllegalArgumentException("Quality must be between 0 and 100");
            }
            this.quality = quality.toString();
            return this;
        }

        /**
         * Sets whether to omit the default white background from the ImageProperties being constructed.
         *
         * @param omitBackground Hide the default white background and allow generating screenshots with transparency.
         * @return The Builder instance for method chaining.
         */
        public Builder addOmitBackground(boolean omitBackground) {
            this.omitBackground = String.valueOf(omitBackground);
            return this;
        }

        /**
         * Sets the device screen width in pixels.
         *
         * @param width The width of the device screen.
         * @return The Builder instance for method chaining.
         */
        public Builder addWidth(Integer width) {
            this.width = String.valueOf(width);
            return this;
        }

        /**
         * Sets the device screen height in pixels.
         *
         * @param height The height of the device screen.
         * @return The Builder instance for method chaining.
         */
        public Builder addHeight(Integer height) {
            this.height = String.valueOf(height);
            return this;
        }

        /**
         * Sets whether to clip the screenshot according to the device dimensions.
         *
         * @param clip Whether to clip the image to the viewport.
         * @return The Builder instance for method chaining.
         */
        public Builder addClip(boolean clip) {
            this.clip = String.valueOf(clip);
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

