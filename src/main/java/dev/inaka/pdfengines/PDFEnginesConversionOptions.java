package dev.inaka.pdfengines;

/**
 * The PDFEnginesConversionOptions class is used to configure LibreOffice conversion options.
 */
public final class PDFEnginesConversionOptions extends PDFEnginesOptions {
    private final String downloadFrom;

    private PDFEnginesConversionOptions(Builder builder) {
        super(builder);
        downloadFrom = builder.downloadFrom;
    }

    /**
     * The Builder class is used to construct instances of PDFEnginesConversionOptions with specific configuration options.
     */
    public static class Builder extends PDFEnginesOptions.Builder<PDFEnginesConversionOptions> {
        private String downloadFrom = null;

        /**
         * Sets the download from configuration.
         *
         * @param downloadFrom Download from configuration as JSON string.
         * @return The Builder instance for method chaining.
         */
        public Builder addDownloadFrom(String downloadFrom) {
            this.downloadFrom = downloadFrom;
            return this;
        }

        @Override
        public PDFEnginesConversionOptions build() {
            return new PDFEnginesConversionOptions(this);
        }
    }
}
