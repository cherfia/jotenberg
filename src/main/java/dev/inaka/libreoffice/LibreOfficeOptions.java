package dev.inaka.libreoffice;

import dev.inaka.common.PdfFormat;

import java.io.File;
import java.util.List;
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
    private final String downloadFrom;
    private final String split;
    private final String flatten;
    private final String userPassword;
    private final String ownerPassword;
    private final List<File> embeds;

    private LibreOfficeOptions(Builder builder) {
        merge = builder.merge;
        pdfa = builder.pdfa;
        pdfua = builder.pdfua;
        losslessImageCompression = builder.losslessImageCompression;
        reduceImageResolution = builder.reduceImageResolution;
        quality = builder.quality;
        maxImageResolution = builder.maxImageResolution;
        downloadFrom = builder.downloadFrom;
        split = builder.split;
        flatten = builder.flatten;
        userPassword = builder.userPassword;
        ownerPassword = builder.ownerPassword;
        embeds = builder.embeds;
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
        private String downloadFrom = null;
        private String split = null;
        private String flatten = null;
        private String userPassword = null;
        private String ownerPassword = null;
        private List<File> embeds = null;

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
         * Sets the download from configuration.
         *
         * @param downloadFrom Download from configuration as JSON string.
         * @return The Builder instance for method chaining.
         */
        public Builder addDownloadFrom(String downloadFrom) {
            this.downloadFrom = downloadFrom;
            return this;
        }

        /**
         * Sets the split configuration.
         *
         * @param split Split configuration as JSON string.
         * @return The Builder instance for method chaining.
         */
        public Builder addSplit(String split) {
            this.split = split;
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
         * Sets the user password for opening the resulting PDF(s).
         *
         * @param userPassword User password.
         * @return The Builder instance for method chaining.
         */
        public Builder addUserPassword(String userPassword) {
            this.userPassword = userPassword;
            return this;
        }

        /**
         * Sets the owner password for full access on the resulting PDF(s).
         *
         * @param ownerPassword Owner password.
         * @return The Builder instance for method chaining.
         */
        public Builder addOwnerPassword(String ownerPassword) {
            this.ownerPassword = ownerPassword;
            return this;
        }

        /**
         * Sets the files to embed in the generated PDF.
         *
         * @param embeds List of files to embed.
         * @return The Builder instance for method chaining.
         */
        public Builder addEmbeds(List<File> embeds) {
            this.embeds = embeds;
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

