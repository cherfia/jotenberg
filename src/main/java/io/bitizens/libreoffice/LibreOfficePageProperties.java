package io.bitizens.libreoffice;

import io.bitizens.common.exceptions.PageRangeMalformedException;

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
    private final String initialView;
    private final String initialPage;
    private final String magnification;
    private final String zoom;
    private final String pageLayout;
    private final String firstPageOnLeft;
    private final String resizeWindowToInitialPage;
    private final String centerWindow;
    private final String openInFullScreenMode;
    private final String displayPDFDocumentTitle;
    private final String hideViewerMenubar;
    private final String hideViewerToolbar;
    private final String hideViewerWindowControls;
    private final String useTransitionEffects;
    private final String openBookmarkLevels;
    private final String nativeWatermarkText;
    private final String nativeWatermarkColor;
    private final String nativeWatermarkFontHeight;
    private final String nativeWatermarkRotateAngle;
    private final String nativeWatermarkFontName;
    private final String nativeTiledWatermarkText;
    private final String password;

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
        initialView = builder.initialView;
        initialPage = builder.initialPage;
        magnification = builder.magnification;
        zoom = builder.zoom;
        pageLayout = builder.pageLayout;
        firstPageOnLeft = builder.firstPageOnLeft;
        resizeWindowToInitialPage = builder.resizeWindowToInitialPage;
        centerWindow = builder.centerWindow;
        openInFullScreenMode = builder.openInFullScreenMode;
        displayPDFDocumentTitle = builder.displayPDFDocumentTitle;
        hideViewerMenubar = builder.hideViewerMenubar;
        hideViewerToolbar = builder.hideViewerToolbar;
        hideViewerWindowControls = builder.hideViewerWindowControls;
        useTransitionEffects = builder.useTransitionEffects;
        openBookmarkLevels = builder.openBookmarkLevels;
        nativeWatermarkText = builder.nativeWatermarkText;
        nativeWatermarkColor = builder.nativeWatermarkColor;
        nativeWatermarkFontHeight = builder.nativeWatermarkFontHeight;
        nativeWatermarkRotateAngle = builder.nativeWatermarkRotateAngle;
        nativeWatermarkFontName = builder.nativeWatermarkFontName;
        nativeTiledWatermarkText = builder.nativeTiledWatermarkText;
        password = builder.password;

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
        private String initialView = "0";
        private String initialPage = "1";
        private String magnification = "0";
        private String zoom = "100";
        private String pageLayout = "0";
        private String firstPageOnLeft = "false";
        private String resizeWindowToInitialPage = "false";
        private String centerWindow = "false";
        private String openInFullScreenMode = "false";
        private String displayPDFDocumentTitle = "true";
        private String hideViewerMenubar = "false";
        private String hideViewerToolbar = "false";
        private String hideViewerWindowControls = "false";
        private String useTransitionEffects = "true";
        private String openBookmarkLevels = "-1";
        private String nativeWatermarkText = null;
        private String nativeWatermarkColor = "8388223";
        private String nativeWatermarkFontHeight = "0";
        private String nativeWatermarkRotateAngle = "0";
        private String nativeWatermarkFontName = "Helvetica";
        private String nativeTiledWatermarkText = null;
        private String password = null;


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

        public Builder addInitialView(int initialView) {
            this.initialView = String.valueOf(initialView);
            return this;
        }

        public Builder addInitialPage(int initialPage) {
            this.initialPage = String.valueOf(initialPage);
            return this;
        }

        public Builder addMagnification(int magnification) {
            this.magnification = String.valueOf(magnification);
            return this;
        }

        public Builder addZoom(int zoom) {
            this.zoom = String.valueOf(zoom);
            return this;
        }

        public Builder addPageLayout(int pageLayout) {
            this.pageLayout = String.valueOf(pageLayout);
            return this;
        }

        public Builder addFirstPageOnLeft(boolean firstPageOnLeft) {
            this.firstPageOnLeft = String.valueOf(firstPageOnLeft);
            return this;
        }

        public Builder addResizeWindowToInitialPage(boolean resizeWindowToInitialPage) {
            this.resizeWindowToInitialPage = String.valueOf(resizeWindowToInitialPage);
            return this;
        }

        public Builder addCenterWindow(boolean centerWindow) {
            this.centerWindow = String.valueOf(centerWindow);
            return this;
        }

        public Builder addOpenInFullScreenMode(boolean openInFullScreenMode) {
            this.openInFullScreenMode = String.valueOf(openInFullScreenMode);
            return this;
        }

        public Builder addDisplayPDFDocumentTitle(boolean displayPDFDocumentTitle) {
            this.displayPDFDocumentTitle = String.valueOf(displayPDFDocumentTitle);
            return this;
        }

        public Builder addHideViewerMenubar(boolean hideViewerMenubar) {
            this.hideViewerMenubar = String.valueOf(hideViewerMenubar);
            return this;
        }

        public Builder addHideViewerToolbar(boolean hideViewerToolbar) {
            this.hideViewerToolbar = String.valueOf(hideViewerToolbar);
            return this;
        }

        public Builder addHideViewerWindowControls(boolean hideViewerWindowControls) {
            this.hideViewerWindowControls = String.valueOf(hideViewerWindowControls);
            return this;
        }

        public Builder addUseTransitionEffects(boolean useTransitionEffects) {
            this.useTransitionEffects = String.valueOf(useTransitionEffects);
            return this;
        }

        public Builder addOpenBookmarkLevels(int openBookmarkLevels) {
            this.openBookmarkLevels = String.valueOf(openBookmarkLevels);
            return this;
        }

        public Builder addNativeWatermarkText(String nativeWatermarkText) {
            this.nativeWatermarkText = nativeWatermarkText;
            return this;
        }

        public Builder addNativeWatermarkColor(int nativeWatermarkColor) {
            this.nativeWatermarkColor = String.valueOf(nativeWatermarkColor);
            return this;
        }

        public Builder addNativeWatermarkFontHeight(int nativeWatermarkFontHeight) {
            this.nativeWatermarkFontHeight = String.valueOf(nativeWatermarkFontHeight);
            return this;
        }

        public Builder addNativeWatermarkRotateAngle(int nativeWatermarkRotateAngle) {
            this.nativeWatermarkRotateAngle = String.valueOf(nativeWatermarkRotateAngle);
            return this;
        }

        public Builder addNativeWatermarkFontName(String nativeWatermarkFontName) {
            this.nativeWatermarkFontName = nativeWatermarkFontName;
            return this;
        }

        public Builder addNativeTiledWatermarkText(String nativeTiledWatermarkText) {
            this.nativeTiledWatermarkText = nativeTiledWatermarkText;
            return this;
        }

        /**
         * Sets the password to open the document.
         *
         * @param password Password to open the document.
         * @return The Builder instance for method chaining.
         */
        public Builder addPassword(String password) {
            this.password = password;
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

