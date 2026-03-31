package io.bitizens.pdfengines;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The PDFEnginesMergeOptions class is used to configure LibreOffice conversion options.
 */
public final class PDFEnginesMergeOptions extends PDFEnginesOptions {
    private final String metadata;
    private final String flatten;
    private final String bookmarks;
    private final String autoIndexBookmarks;
    private final String downloadFrom;

    private PDFEnginesMergeOptions(Builder builder) {
        super(builder);
        metadata = builder.metadata;
        flatten = builder.flatten;
        bookmarks = builder.bookmarks;
        autoIndexBookmarks = builder.autoIndexBookmarks;
        downloadFrom = builder.downloadFrom;
    }

    /**
     * The Builder class is used to construct instances of PDFEnginesMergeOptions with specific configuration options.
     */
    public static class Builder extends PDFEnginesOptions.Builder<PDFEnginesMergeOptions> {
        private String metadata = null;
        private String flatten = null;
        private String bookmarks = null;
        private String autoIndexBookmarks = "false";
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
         * Sets custom bookmarks JSON for merge operation.
         *
         * @param bookmarks The bookmarks JSON.
         * @return The Builder instance for method chaining.
         */
        public Builder addBookmarks(JSONArray bookmarks) {
            this.bookmarks = String.valueOf(bookmarks);
            return this;
        }

        /**
         * Sets custom bookmarks JSON map for merge operation.
         *
         * @param bookmarks The bookmarks JSON map.
         * @return The Builder instance for method chaining.
         */
        public Builder addBookmarks(JSONObject bookmarks) {
            this.bookmarks = String.valueOf(bookmarks);
            return this;
        }

        /**
         * Sets whether existing bookmarks should be auto-indexed from input files.
         *
         * @param autoIndexBookmarks `true` to auto-index bookmarks, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addAutoIndexBookmarks(boolean autoIndexBookmarks) {
            this.autoIndexBookmarks = String.valueOf(autoIndexBookmarks);
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

