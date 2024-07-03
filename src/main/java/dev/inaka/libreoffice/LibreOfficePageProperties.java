package dev.inaka.libreoffice;

import dev.inaka.common.exceptions.PageRangeMalformedException;

/**
 * The LibreOfficePageProperties is a class that represents various properties for configuring LibreOffice document conversion,
 * such as landscape, native page ranges, and other formatting options.
 */
public final class LibreOfficePageProperties {
    private final String landscape;
    private final String nativePageRanges;
    private final String exportFormFields;
    private final String singlePageSheets;

    private LibreOfficePageProperties(Builder builder) {
        landscape = builder.landscape;
        nativePageRanges = builder.nativePageRanges;
        exportFormFields = builder.exportFormFields;
        singlePageSheets = builder.singlePageSheets;

    }

    /**
     * The Builder class is used to construct instances of LibreOfficePageProperties with specific configuration options.
     */
    public static class Builder {
        private String landscape = "false";
        private String exportFormFields = "true";
        private String nativePageRanges = "";
        private String singlePageSheets = "false";


        /**
         * Sets whether the document is in landscape orientation for the LibreOfficePageProperties being constructed.
         *
         * @param landscape `true` if the document is in landscape orientation, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addLandscape(boolean landscape) {
            this.landscape = String.valueOf(landscape);
            return this;
        }

        /**
         * Sets whether form fields should be exported for the LibreOfficePageProperties being constructed.
         *
         * @param exportFormFields `true` if form fields should be exported, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addExportFormFields(boolean exportFormFields) {
            this.exportFormFields = String.valueOf(exportFormFields);
            return this;
        }

        /**
         * Sets the native page ranges for the LibreOfficePageProperties being constructed.
         *
         * @param start The start page number.
         * @param end   The end page number.
         * @return The Builder instance for method chaining.
         * @throws PageRangeMalformedException If the page range is malformed.
         */
        public Builder addNativePageRanges(int start, int end) throws PageRangeMalformedException {
            if (start < 0 || end < 0 || end > start) {
                throw new PageRangeMalformedException();
            }
            this.nativePageRanges = start + "-" + end;
            return this;
        }

        /**
         * Sets whether single page sheets should be used for the LibreOfficePageProperties being constructed.
         *
         * @param singlePageSheets `true` if single page sheets should be used to render the entire spreadsheet as a single page, `false` otherwise.
         * @return
         */
        public Builder addSinglePageSheets(boolean singlePageSheets) {
            this.singlePageSheets = String.valueOf(singlePageSheets);
            return this;
        }


        /**
         * Builds and returns an instance of LibreOfficePageProperties with the configured options.
         *
         * @return An instance of LibreOfficePageProperties.
         */
        public LibreOfficePageProperties build() {
            return new LibreOfficePageProperties(this);
        }
    }


}

