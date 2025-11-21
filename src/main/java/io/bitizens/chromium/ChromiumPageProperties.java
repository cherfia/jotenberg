package io.bitizens.chromium;

import io.bitizens.common.PdfFormat;
import io.bitizens.common.exceptions.MarginMalformedException;
import io.bitizens.common.exceptions.PageRangeMalformedException;

import java.io.IOException;

/**
 * ChromiumPageProperties is a class that represents various properties for configuring document conversion,
 * such as paper size, margins, and other formatting options.
 */
public class ChromiumPageProperties {
    private final String paperWidth;
    private final String paperHeight;
    private final String marginTop;
    private final String marginBottom;
    private final String marginLeft;
    private final String marginRight;
    private final String preferCssPageSize;
    private final String printBackground;
    private final String omitBackground;
    private final String landscape;
    private final String scale;
    private final String nativePageRanges;
    private final String pdfa;
    private final String nativePdfFormat;
    private final String singlePage;

    public ChromiumPageProperties(Builder builder) {
        paperWidth = builder.paperWidth;
        paperHeight = builder.paperHeight;
        marginTop = builder.marginTop;
        marginBottom = builder.marginBottom;
        marginLeft = builder.marginLeft;
        marginRight = builder.marginRight;
        preferCssPageSize = builder.preferCssPageSize;
        printBackground = builder.printBackground;
        omitBackground = builder.omitBackground;
        landscape = builder.landscape;
        scale = builder.scale;
        nativePageRanges = builder.nativePageRanges;
        pdfa = builder.pdfa;
        nativePdfFormat = builder.nativePdfFormat;
        singlePage = builder.singlePage;

    }


    /**
     * The Builder class is used to construct instances of ChromiumPageProperties with specific configuration options.
     */
    public static class Builder {
        private String nativePdfFormat = PdfFormat.A_1A.format();
        private String pdfa = null;
        private String paperWidth = "8.5";
        private String paperHeight = "11";
        private String marginTop = "0.39";
        private String marginBottom = "0.39";
        private String marginLeft = "0.39";
        private String marginRight = "0.39";
        private String preferCssPageSize = "false";
        private String printBackground = "false";
        private String omitBackground = "false";
        private String landscape = "false";
        private String scale = "1.0";
        private String nativePageRanges = null;
        private String singlePage = "false";

        /**
         * Sets the paper width for the ChromiumPageProperties being constructed.
         *
         * @param paperWidth The width of the paper.
         * @return The Builder instance for method chaining.
         * @throws IOException If the specified paper width is below the minimum printing requirement.
         */
        public Builder addPaperWidth(float paperWidth) throws IOException {
            if (paperWidth < 1.0f) {
                throw new IOException("Width is smaller than the minimum printing requirements (1.0 in)");
            }
            this.paperWidth = String.valueOf(paperWidth);
            return this;
        }

        /**
         * Sets the paper height for the ChromiumPageProperties being constructed.
         *
         * @param paperHeight The height of the paper.
         * @return The Builder instance for method chaining.
         * @throws IOException If the specified paper height is below the minimum printing requirement.
         */
        public Builder addPaperHeight(float paperHeight) throws IOException {
            if (paperHeight < 1.5f) {
                throw new IOException("paperHeight is smaller than the minimum printing requirements (1.5 in)");
            }
            this.paperHeight = String.valueOf(paperHeight);
            return this;
        }

        /**
         * Sets the top margin for the ChromiumPageProperties being constructed.
         *
         * @param marginTop The top margin.
         * @return The Builder instance for method chaining.
         * @throws MarginMalformedException If the specified margin is negative.
         */
        public Builder addMarginTop(float marginTop) throws MarginMalformedException {
            if (marginTop < 0f) {
                throw new MarginMalformedException();
            }
            this.marginTop = String.valueOf(marginTop);
            return this;
        }

        /**
         * Sets the bottom margin for the ChromiumPageProperties being constructed.
         *
         * @param marginBottom The bottom margin.
         * @return The Builder instance for method chaining.
         * @throws MarginMalformedException If the specified margin is negative.
         */
        public Builder addMarginBottom(float marginBottom) throws MarginMalformedException {
            if (marginBottom < 0f) {
                throw new MarginMalformedException();
            }
            this.marginBottom = String.valueOf(marginBottom);
            return this;
        }

        /**
         * Sets the left margin for the ChromiumPageProperties being constructed.
         *
         * @param marginLeft The left margin.
         * @return The Builder instance for method chaining.
         * @throws MarginMalformedException If the specified margin is negative.
         */
        public Builder addMarginLeft(float marginLeft) throws MarginMalformedException {
            if (marginLeft < 0f) {
                throw new MarginMalformedException();
            }
            this.marginLeft = String.valueOf(marginLeft);
            return this;
        }

        /**
         * Sets the right margin for the ChromiumPageProperties being constructed.
         *
         * @param marginRight The right margin.
         * @return The Builder instance for method chaining.
         * @throws MarginMalformedException If the specified margin is negative.
         */
        public Builder addMarginRight(float marginRight) throws MarginMalformedException {
            if (marginRight < 0f) {
                throw new MarginMalformedException();
            }

            this.marginRight = String.valueOf(marginRight);
            return this;
        }

        /**
         * Sets whether CSS page size is preferred for the ChromiumPageProperties being constructed.
         *
         * @param preferCssPageSize `true` if CSS page size is preferred, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addPreferCssPageSize(boolean preferCssPageSize) {
            this.preferCssPageSize = String.valueOf(preferCssPageSize);
            return this;
        }

        /**
         * Sets whether background should be printed for the ChromiumPageProperties being constructed.
         *
         * @param printBackground `true` if background should be printed, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addPrintBackground(boolean printBackground) {
            this.printBackground = String.valueOf(printBackground);
            return this;
        }

        /**
         * Sets whether background should be hidden for the ChromiumPageProperties being constructed.
         *
         * @param omitBackground `true` if background should be printed, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addOmitBackground(boolean omitBackground) {
            this.omitBackground = String.valueOf(omitBackground);
            return this;
        }

        /**
         * Sets whether the document is in landscape orientation for the ChromiumPageProperties being constructed.
         *
         * @param landscape `true` if the document is in landscape orientation, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addLandscape(boolean landscape) {
            this.landscape = String.valueOf(landscape);
            return this;
        }

        /**
         * Sets the scaling factor for the ChromiumPageProperties being constructed.
         *
         * @param scale The scaling factor.
         * @return The Builder instance for method chaining.
         */
        public Builder addScale(float scale) {
            this.scale = String.valueOf(scale);
            return this;
        }

        /**
         * Sets the native page ranges for the ChromiumPageProperties being constructed.
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
         * Sets the PDF format for the ChromiumPageProperties being constructed.
         *
         * @param pdfa The PDF format.
         * @return The Builder instance for method chaining.
         */
        public Builder addPdfa(String pdfa) {
            this.pdfa = pdfa;
            return this;
        }

        /**
         * Sets whether the document should be printed as single page for the ChromiumPageProperties being constructed.
         *
         * @param singlePage `true` if the document is printed as a single page, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder addSinglePage(boolean singlePage) {
            this.singlePage = String.valueOf(singlePage);
            return this;
        }

        /**
         * Builds and returns an instance of ChromiumPageProperties with the configured options.
         *
         * @return An instance of PageProperties.
         */
        public ChromiumPageProperties build() {
            return new ChromiumPageProperties(this);
        }
    }
}

