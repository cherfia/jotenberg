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
    private final String allowDuplicateFieldNames;
    private final String exportBookmarks;
    private final String exportBookmarksToPdfDestination;
    private final String exportPlaceholders;
    private final String exportNotes;
    private final String exportNotesPages;
    private final String exportOnlyNotesPages;
    private final String exportNotesInMargin;
    private final String convertOooTargetToPdfTarget;
    private final String exportLinksRelativeFsys;
    private final String exportHiddenSlides;
    private final String skipEmptyPages;
    private final String addOriginalDocumentAsStream;

    private LibreOfficePageProperties(Builder builder) {
        landscape = builder.landscape;
        nativePageRanges = builder.nativePageRanges;
        exportFormFields = builder.exportFormFields;
        singlePageSheets = builder.singlePageSheets;
        allowDuplicateFieldNames = builder.allowDuplicateFieldNames;
        exportBookmarks = builder.exportBookmarks;
        exportBookmarksToPdfDestination = builder.exportBookmarksToPdfDestination;
        exportPlaceholders = builder.exportPlaceholders;
        exportNotes = builder.exportNotes;
        exportNotesPages = builder.exportNotesPages;
        exportOnlyNotesPages = builder.exportOnlyNotesPages;
        exportNotesInMargin = builder.exportNotesInMargin;
        convertOooTargetToPdfTarget = builder.convertOooTargetToPdfTarget;
        exportLinksRelativeFsys = builder.exportLinksRelativeFsys;
        exportHiddenSlides = builder.exportHiddenSlides;
        skipEmptyPages = builder.skipEmptyPages;
        addOriginalDocumentAsStream = builder.addOriginalDocumentAsStream;

    }

    /**
     * The Builder class is used to construct instances of LibreOfficePageProperties with specific configuration options.
     */
    public static class Builder {
        private String landscape = "false";
        private String exportFormFields = "true";
        private String nativePageRanges = "";
        private String singlePageSheets = "false";
        private String allowDuplicateFieldNames = "false";
        private String exportBookmarks = "true";
        private String exportBookmarksToPdfDestination = "false";
        private String exportPlaceholders = "false";
        private String exportNotes = "false";
        private String exportNotesPages = "false";
        private String exportOnlyNotesPages = "false";
        private String exportNotesInMargin = "false";
        private String convertOooTargetToPdfTarget = "false";
        private String exportLinksRelativeFsys = "false";
        private String exportHiddenSlides = "false";
        private String skipEmptyPages = "false";
        private String addOriginalDocumentAsStream = "false";


        /**
         * Sets whether the document is in landscape orientation.
         *
         * @param landscape `true` if the document is in landscape orientation, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addLandscape(boolean landscape) {
            this.landscape = String.valueOf(landscape);
            return this;
        }

        /**
         * Sets whether form fields should be exported.
         *
         * @param exportFormFields `true` if form fields should be exported, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addExportFormFields(boolean exportFormFields) {
            this.exportFormFields = String.valueOf(exportFormFields);
            return this;
        }

        /**
         * Sets the native page ranges.
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
         * Sets whether single page sheets should be used.
         *
         * @param singlePageSheets `true` if single page sheets should be used to render the entire spreadsheet as a single page, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addSinglePageSheets(boolean singlePageSheets) {
            this.singlePageSheets = String.valueOf(singlePageSheets);
            return this;
        }

        /**
         * Sets whether multiple form fields exported should be allowed to have the same field name.
         *
         * @param allowDuplicateFieldNames `true` if duplicate field names should be allowed, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addAllowDuplicateFieldNames(boolean allowDuplicateFieldNames) {
            this.allowDuplicateFieldNames = String.valueOf(allowDuplicateFieldNames);
            return this;
        }

        /**
         * Sets whether bookmarks should be exported.
         *
         * @param exportBookmarks `true` if bookmarks should be exported, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addExportBookmarks(boolean exportBookmarks) {
            this.exportBookmarks = String.valueOf(exportBookmarks);
            return this;
        }

        /**
         * Sets whether that the bookmarks contained in the source LibreOffice file should be exported to the PDF file as Named Destination.
         *
         * @param exportBookmarksToPdfDestination `true` if bookmarks should be exported to PDF destination, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addExportBookmarksToPdfDestination(boolean exportBookmarksToPdfDestination) {
            this.exportBookmarksToPdfDestination = String.valueOf(exportBookmarksToPdfDestination);
            return this;
        }

        /**
         * Sets whether placeholders should be exported.
         *
         * @param exportPlaceholders `true` if placeholders should be exported, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addExportPlaceholders(boolean exportPlaceholders) {
            this.exportPlaceholders = String.valueOf(exportPlaceholders);
            return this;
        }

        /**
         * Sets whether notes should be exported.
         *
         * @param exportNotes `true` if notes should be exported, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addExportNotes(boolean exportNotes) {
            this.exportNotes = String.valueOf(exportNotes);
            return this;
        }

        /**
         * Sets whether notes pages should be exported. Notes pages are available in Impress documents only.
         *
         * @param exportNotesPages `true` if notes pages should be exported, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addExportNotesPages(boolean exportNotesPages) {
            this.exportNotesPages = String.valueOf(exportNotesPages);
            return this;
        }

        /**
         * Sets whether only notes pages should be exported.
         *
         * @param exportOnlyNotesPages `true` if only notes pages should be exported, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addExportOnlyNotesPages(boolean exportOnlyNotesPages) {
            this.exportOnlyNotesPages = String.valueOf(exportOnlyNotesPages);
            return this;
        }

        /**
         * Sets whether notes in margin should be exported to PDF.
         *
         * @param exportNotesInMargin `true` if notes in margin should be exported, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addExportNotesInMargin(boolean exportNotesInMargin) {
            this.exportNotesInMargin = String.valueOf(exportNotesInMargin);
            return this;
        }

        /**
         * Sets whether the target documents with .od[tpgs] extension, should have that extension changed to .pdf when the link is exported to PDF.
         *
         * @param convertOooTargetToPdfTarget `true` if the target documents should be converted to a PDF, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addConvertOooTargetToPdfTarget(boolean convertOooTargetToPdfTarget) {
            this.convertOooTargetToPdfTarget = String.valueOf(convertOooTargetToPdfTarget);
            return this;
        }

        /**
         * Sets whether the file system related hyperlinks (file:// protocol) present in the document should be exported as relative to the source document location.
         *
         * @param exportLinksRelativeFsys `true` if links should be exported with relative file system paths, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addExportLinksRelativeFsys(boolean exportLinksRelativeFsys) {
            this.exportLinksRelativeFsys = String.valueOf(exportLinksRelativeFsys);
            return this;
        }

        /**
         * Sets whether hidden slides should be exported.
         *
         * @param exportHiddenSlides `true` if hidden slides should be exported, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addExportHiddenSlides(boolean exportHiddenSlides) {
            this.exportHiddenSlides = String.valueOf(exportHiddenSlides);
            return this;
        }

        /**
         * Sets whether empty pages should be skipped.
         *
         * @param skipEmptyPages `true` if empty pages should be skipped, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addSkipEmptyPages(boolean skipEmptyPages) {
            this.skipEmptyPages = String.valueOf(skipEmptyPages);
            return this;
        }

        /**
         * Sets whether the original document should be added as a stream for archiving purposes.
         *
         * @param addOriginalDocumentAsStream `true` if the original document should be added as a stream, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addAddOriginalDocumentAsStream(boolean addOriginalDocumentAsStream) {
            this.addOriginalDocumentAsStream = String.valueOf(addOriginalDocumentAsStream);
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

