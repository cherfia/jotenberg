package io.bitizens.pdfengines;

/**
 * The PDFEnginesEncryptOptions class is used to configure PDF encryption options.
 */
public final class PDFEnginesEncryptOptions {
    private final String userPassword;
    private final String ownerPassword;

    private PDFEnginesEncryptOptions(Builder builder) {
        this.userPassword = builder.userPassword;
        this.ownerPassword = builder.ownerPassword;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getOwnerPassword() {
        return ownerPassword;
    }

    /**
     * The Builder class is used to construct instances of PDFEnginesEncryptOptions with specific configuration options.
     */
    public static class Builder {
        private String userPassword;
        private String ownerPassword;

        /**
         * Sets the user password for opening the PDF.
         *
         * @param userPassword User password (required).
         * @return The Builder instance for method chaining.
         */
        public Builder addUserPassword(String userPassword) {
            this.userPassword = userPassword;
            return this;
        }

        /**
         * Sets the owner password for full access on the PDF.
         *
         * @param ownerPassword Owner password (optional).
         * @return The Builder instance for method chaining.
         */
        public Builder addOwnerPassword(String ownerPassword) {
            this.ownerPassword = ownerPassword;
            return this;
        }

        public PDFEnginesEncryptOptions build() {
            return new PDFEnginesEncryptOptions(this);
        }
    }
}

