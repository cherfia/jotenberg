package dev.inaka.libreoffice;

import dev.inaka.common.PdfFormat;

import java.util.Set;

/**
 * The LibreOfficeOptions class is used to configure LibreOffice conversion options.
 */
public final class LibreOfficeOptions {
    private final String quality;
    private final String merge;
    private final String pdfa;
    private final String pdfua;
    private final String losslessImageCompression;
    private final String reduceImageResolution;
    private final String maxImageResolution;

    private LibreOfficeOptions(Builder builder) {
        merge = builder.merge;
        pdfa = builder.pdfa;
        pdfua = builder.pdfua;
        losslessImageCompression = builder.losslessImageCompression;
        reduceImageResolution = builder.reduceImageResolution;
        quality = builder.quality;
        maxImageResolution = builder.maxImageResolution;
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
        private String quality = "90";
        private String maxImageResolution = "300";

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
         * Sets the quality of the JPG export. A higher value produces a higher-quality image and a larger file.
         *
         * @param quality The quality of the output file.
         * @return The Builder instance for method chaining.
         * @throws IllegalArgumentException if the quality is not within the valid range (0 to 100).
         */
        public Builder addQuality(Integer quality) {
            if (quality < 0 || quality > 100) {
                throw new IllegalArgumentException("Quality must be between 0 and 100");
            }
            this.quality = String.valueOf(quality);
            return this;
        }

        /**
         * Sets the maximum resolution for images in the output file.
         *
         * @param maxImageResolution The maximum resolution for images in the output file.
         * @return The Builder instance for method chaining.
         * @throws IllegalArgumentException if the maximum image resolution is not one of the valid values (75, 150, 300, 600, and 1200).
         */
        public Builder addMaxImageResolution(Integer maxImageResolution) {
            Set<Integer> validValues = Set.of(75, 150, 300, 600, 1200);
            if (!validValues.contains(maxImageResolution)) {
                throw new IllegalArgumentException("Invalid maximum image resolution. Valid values are 75, 150, 300, 600, and 1200.");
            }
            this.maxImageResolution = String.valueOf(maxImageResolution);
            return this;
        }

        /**
         * Returns the configured quality of the output file.
         *
         * @return The configured quality of the output file.
         */
        public String getQuality() {
            return quality;
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

