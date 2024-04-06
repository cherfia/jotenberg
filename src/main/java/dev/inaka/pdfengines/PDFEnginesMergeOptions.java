package dev.inaka.pdfengines;

import org.json.JSONObject;

/**
 * The PDFEnginesMergeOptions class is used to configure LibreOffice conversion options.
 */
public final class PDFEnginesMergeOptions extends PDFEnginesOptions {
    private final String metadata;

    private PDFEnginesMergeOptions(Builder builder) {
        super(builder);
        metadata = builder.metadata;
    }

    /**
     * The Builder class is used to construct instances of PDFEnginesMergeOptions with specific configuration options.
     */
    public static class Builder extends PDFEnginesOptions.Builder<PDFEnginesMergeOptions> {
        private String metadata = null;

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

        @Override
        public PDFEnginesMergeOptions build() {
            return new PDFEnginesMergeOptions(this);
        }
    }
}

