package dev.inaka.pdfengines;

/**
 * The PDFEnginesConversionOptions class is used to configure LibreOffice conversion options.
 */
public final class PDFEnginesConversionOptions extends PDFEnginesOptions {

    private PDFEnginesConversionOptions(Builder builder) {
        super(builder);
    }

    /**
     * The Builder class is used to construct instances of PDFEnginesConversionOptions with specific configuration options.
     */
    public static class Builder extends PDFEnginesOptions.Builder<PDFEnginesConversionOptions> {

        @Override
        public PDFEnginesConversionOptions build() {
            return new PDFEnginesConversionOptions(this);
        }
    }
}
