package dev.inaka.libreoffice;

import dev.inaka.common.PdfFormat;

/**
 * The LibreOfficeOptions class is used to configure LibreOffice conversion options.
 */
public final class LibreOfficeOptions {
    private final String merge;
    private final String pdfa;
    private final String pdfua;
    private final String losslessImageCompression;
    private final String reduceImageResolution;

    private LibreOfficeOptions(Builder builder) {
        merge = builder.merge;
        pdfa = builder.pdfa;
        pdfua = builder.pdfua;
        losslessImageCompression = builder.losslessImageCompression;
        reduceImageResolution = builder.reduceImageResolution;
    }

    /**
     * The Builder class is used to construct instances of ChromiumPageProperties with specific configuration options.
     */
    public static class Builder {
        private String losslessImageCompression = "false";
        private String merge = "false";
        private String pdfa = null;
        private String pdfua = "false";
        private String reduceImageResolution = "true";

        /**
         * Sets whether to enable lossless image compression to tweak image conversion performance.
         *
         * @param losslessImageCompression Whether to enable lossless image compression.
         * @return The Builder instance for method chaining.
         */
        public Builder addLosslessImageCompression(boolean losslessImageCompression) {
            this.losslessImageCompression = String.valueOf(losslessImageCompression);
            return this;
        }

        /**
         * Sets whether to merge the conversion result by LibreOffice.
         *
         * @param merge Whether to merge the conversion result.
         * @return The Builder instance for method chaining.
         */
        public Builder addMerge(boolean merge) {
            this.merge = String.valueOf(merge);
            return this;
        }

        /**
         * Sets the PDF/A format to be used by LibreOffice.
         *
         * @param pdfa The PDF/A format.
         * @return The Builder instance for method chaining.
         */
        public Builder addPdfa(PdfFormat pdfa) {
            this.pdfa = String.valueOf(pdfa);
            return this;
        }

        /**
         * Sets whether to use PDF/UA.
         *
         * @param pdfua Whether to use PDF/UA.
         * @return The Builder instance for method chaining.
         */
        public Builder addPdfua(String pdfua) {
            this.pdfua = pdfua;
            return this;
        }

        /**
         * Sets whether to reduce image resolution during conversion to tweak image conversion performance.
         *
         * @param reduceImageResolution Whether to reduce image resolution.
         * @return The Builder instance for method chaining.
         */
        public Builder addReduceImageResolution(boolean reduceImageResolution) {
            this.reduceImageResolution = String.valueOf(reduceImageResolution);
            return this;
        }

        /**
         * Builds and returns an instance of LibreOfficeOptions with the configured options.
         *
         * @return An instance of LibreOfficeOptions with the configured options.
         */
        public LibreOfficeOptions build() {
            return new LibreOfficeOptions(this);
        }
    }


}

