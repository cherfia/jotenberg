package io.bitizens.pdfengines;

import org.json.JSONObject;

/**
 * The PDFEnginesMergeOptions class is used to configure LibreOffice conversion options.
 */
public final class PDFEnginesMergeOptions extends PDFEnginesOptions {
    private final String metadata;
    private final String flatten;
    private final String downloadFrom;

    private PDFEnginesMergeOptions(Builder builder) {
        super(builder);
        metadata = builder.metadata;
        flatten = builder.flatten;
        downloadFrom = builder.downloadFrom;
    }

    /**
     * The Builder class is used to construct instances of PDFEnginesMergeOptions with specific configuration options.
     */
    public static class Builder extends PDFEnginesOptions.Builder<PDFEnginesMergeOptions> {
        private String metadata = null;
        private String flatten = null;
        private String downloadFrom = null;

        /**
         * Sets the metadata to be used by PDF Engines.
         *
         * @param metadata The metadata.
         * @return The Builder instance for method chaining.
         */
        public Builder addMetadata(JSONObject metadata) {
            this.metadata = String.valueOf(metadata);
            return this;
        }

        /**
         * Sets whether to flatten the PDF document.
         *
         * @param flatten `true` to flatten the PDF document, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addFlatten(boolean flatten) {
            this.flatten = String.valueOf(flatten);
            return this;
        }

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
        public PDFEnginesMergeOptions build() {
            return new PDFEnginesMergeOptions(this);
        }
    }
}

