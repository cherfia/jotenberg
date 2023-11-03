package dev.inaka.common;

import dev.inaka.common.exceptions.MarginMalformedException;
import dev.inaka.common.exceptions.PageRangeMalformedException;

import java.io.IOException;

/**
 * PageProperties is a class that represents various properties for configuring document conversion,
 * such as paper size, margins, and other formatting options.
 */
public final class PageProperties {
    private final float paperWidth;
    private final float paperHeight;
    private final float marginTop;
    private final float marginBottom;
    private final float marginLeft;
    private final float marginRight;
    private final boolean preferCssPageSize;

    private final boolean printBackground;
    private final boolean landscape;
    private final float scale;
    private final String nativePageRanges;
    private final String pdfFormat;
    private final String nativePdfFormat;

    private PageProperties(Builder builder) {
        paperWidth = builder.paperWidth;
        paperHeight = builder.paperHeight;
        marginTop = builder.marginTop;
        marginBottom = builder.marginBottom;
        marginLeft = builder.marginLeft;
        marginRight = builder.marginRight;
        preferCssPageSize = builder.preferCssPageSize;
        printBackground = builder.printBackground;
        landscape = builder.landscape;
        scale = builder.scale;
        nativePageRanges = builder.nativePageRanges;
        pdfFormat = builder.pdfFormat;
        nativePdfFormat = builder.nativePdfFormat;

    }

    /**
     * The Builder class is used to construct instances of PageProperties with specific configuration options.
     */
    public static class Builder {
        private float paperWidth = 8.5f;
        private float paperHeight = 11f;
        private float marginTop = 0.39f;
        private float marginBottom = 0.39f;
        private float marginLeft = 0.39f;
        private float marginRight = 0.39f;
        private boolean preferCssPageSize = false;
        private boolean printBackground = false;
        private boolean landscape = false;
        private float scale = 1f;
        private String nativePageRanges = "";
        private String pdfFormat = "";
        private String nativePdfFormat = PdfFormat.A_1A.format();

        /**
         * Sets the paper width for the PageProperties being constructed.
         *
         * @param paperWidth The width of the paper.
         * @return The Builder instance for method chaining.
         * @throws IOException If the specified paper width is below the minimum printing requirement.
         */
        public Builder addPaperWidth(float paperWidth) throws IOException {
            if (paperWidth < 1.0f) {
                throw new IOException("Width is smaller than the minimum printing requirements (1.0 in)");
            }
            this.paperWidth = paperWidth;
            return this;
        }

        /**
         * Sets the paper height for the PageProperties being constructed.
         *
         * @param paperHeight The height of the paper.
         * @return The Builder instance for method chaining.
         * @throws IOException If the specified paper height is below the minimum printing requirement.
         */
        public Builder addPaperHeight(float paperHeight) throws IOException {
            if (paperHeight < 1.5f) {
                throw new IOException("paperHeight is smaller than the minimum printing requirements (1.5 in)");
            }
            this.paperHeight = paperHeight;
            return this;
        }

        /**
         * Sets the top margin for the PageProperties being constructed.
         *
         * @param marginTop The top margin.
         * @return The Builder instance for method chaining.
         * @throws MarginMalformedException If the specified margin is negative.
         */
        public Builder addMarginTop(float marginTop) throws MarginMalformedException {
            if (marginTop < 0f) {
                throw new MarginMalformedException();
            }
            this.marginTop = marginTop;
            return this;
        }

        /**
         * Sets the bottom margin for the PageProperties being constructed.
         *
         * @param marginBottom The bottom margin.
         * @return The Builder instance for method chaining.
         * @throws MarginMalformedException If the specified margin is negative.
         */
        public Builder addMarginBottom(float marginBottom) throws MarginMalformedException {
            if (marginBottom < 0f) {
                throw new MarginMalformedException();
            }
            this.marginBottom = marginBottom;
            return this;
        }

        /**
         * Sets the left margin for the PageProperties being constructed.
         *
         * @param marginLeft The left margin.
         * @return The Builder instance for method chaining.
         * @throws MarginMalformedException If the specified margin is negative.
         */
        public Builder addMarginLeft(float marginLeft) throws MarginMalformedException {
            if (marginLeft < 0f) {
                throw new MarginMalformedException();
            }
            this.marginLeft = marginLeft;
            return this;
        }

        /**
         * Sets the right margin for the PageProperties being constructed.
         *
         * @param marginRight The right margin.
         * @return The Builder instance for method chaining.
         * @throws MarginMalformedException If the specified margin is negative.
         */
        public Builder addMarginRight(float marginRight) throws MarginMalformedException {
            if (marginRight < 0f) {
                throw new MarginMalformedException();
            }

            this.marginRight = marginRight;
            return this;
        }

        /**
         * Sets whether CSS page size is preferred for the PageProperties being constructed.
         *
         * @param preferCssPageSize `true` if CSS page size is preferred, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addPreferCssPageSize(boolean preferCssPageSize) {
            this.preferCssPageSize = preferCssPageSize;
            return this;
        }

        /**
         * Sets whether background should be printed for the PageProperties being constructed.
         *
         * @param printBackground `true` if background should be printed, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addPrintBackground(boolean printBackground) {
            this.printBackground = printBackground;
            return this;
        }

        /**
         * Sets whether the document is in landscape orientation for the PageProperties being constructed.
         *
         * @param landscape `true` if the document is in landscape orientation, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addLandscape(boolean landscape) {
            this.landscape = landscape;
            return this;
        }

        /**
         * Sets the scaling factor for the PageProperties being constructed.
         *
         * @param scale The scaling factor.
         * @return The Builder instance for method chaining.
         */
        public Builder addScale(float scale) {
            this.scale = scale;
            return this;
        }

        /**
         * Sets the native page ranges for the PageProperties being constructed.
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
         * Sets the PDF format for the PageProperties being constructed.
         *
         * @param pdfFormat The PDF format.
         * @return The Builder instance for method chaining.
         */
        public Builder addPdfFormat(String pdfFormat) {
            this.pdfFormat = pdfFormat;
            return this;
        }

        /**
         * Builds and returns an instance of PageProperties with the configured options.
         *
         * @return An instance of PageProperties.
         */
        public PageProperties build() {
            return new PageProperties(this);
        }
    }

    /**
     * Returns the width of the paper.
     *
     * @return The paper width.
     */
    public float getPaperWidth() {
        return paperWidth;
    }

    /**
     * Returns the height of the paper.
     *
     * @return The paper height.
     */
    public float getPaperHeight() {
        return paperHeight;
    }

    /**
     * Returns the top margin.
     *
     * @return The top margin.
     */
    public float getMarginTop() {
        return marginTop;
    }

    /**
     * Returns the bottom margin.
     *
     * @return The bottom margin.
     */
    public float getMarginBottom() {
        return marginBottom;
    }

    /**
     * Returns the left margin.
     *
     * @return The left margin.
     */
    public float getMarginLeft() {
        return marginLeft;
    }

    /**
     * Returns the right margin.
     *
     * @return The right margin.
     */
    public float getMarginRight() {
        return marginRight;
    }

    /**
     * Checks if CSS page size is preferred.
     *
     * @return `true` if CSS page size is preferred, `false` otherwise.
     */
    public boolean isPreferCssPageSize() {
        return preferCssPageSize;
    }

    /**
     * Checks if background should be printed.
     *
     * @return `true` if background should be printed, `false` otherwise.
     */
    public boolean isPrintBackground() {
        return printBackground;
    }

    /**
     * Checks if the document is in landscape orientation.
     *
     * @return `true` if the document is in landscape orientation, `false` otherwise.
     */
    public boolean isLandscape() {
        return landscape;
    }

    /**
     * Returns the scaling factor.
     *
     * @return The scaling factor.
     */
    public float getScale() {
        return scale;
    }

    /**
     * Returns the native page ranges.
     *
     * @return The native page ranges.
     */
    public String getNativePageRanges() {
        return nativePageRanges;
    }

    /**
     * Returns the PDF format.
     *
     * @return The PDF format.
     */
    public String getPdfFormat() {
        return pdfFormat;
    }

    /**
     * Returns the native PDF format.
     *
     * @return The native PDF format.
     */
    public String getNativePdfFormat() {
        return nativePdfFormat;
    }
}

