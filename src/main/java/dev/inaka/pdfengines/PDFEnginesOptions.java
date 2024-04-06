package dev.inaka.pdfengines;

import dev.inaka.common.PdfFormat;

/**
 * The PDFEnginesOptions class is used to configure LibreOffice conversion options.
 */
public abstract class PDFEnginesOptions {
    protected final String pdfa;
    protected final String pdfua;

    protected PDFEnginesOptions(Builder builder) {
        pdfa = builder.pdfa;
        pdfua = builder.pdfua;
    }

    /**
     * The Builder class is used to construct instances of PDFEnginesOptions with specific configuration options.
     */
    public static abstract class Builder<T extends PDFEnginesOptions> {
        protected String pdfa = null;
        protected String pdfua = "false";

        /**
         * Sets the PDF/A format to be used by PDF Engines.
         *
         * @param pdfa The PDF/A format.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addPdfa(PdfFormat pdfa) {
            this.pdfa = String.valueOf(pdfa);
            return this;
        }

        /**
         * Sets whether to use PDF/UA.
         *
         * @param pdfua Whether to use PDF/UA.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addPdfua(String pdfua) {
            this.pdfua = pdfua;
            return this;
        }

        /**
         * Builds and returns an instance of PDFEnginesOptions with the configured options.
         *
         * @return An instance of PDFEnginesOptions with the configured options.
         */
        public abstract T build();
    }
}

