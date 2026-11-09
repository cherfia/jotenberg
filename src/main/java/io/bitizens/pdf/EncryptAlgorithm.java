package io.bitizens.pdf;

/**
 * EncryptAlgorithm is the algorithm {@code encrypt} protects a PDF with. Keep AES-256 unless an old reader can't open it.
 */
public enum EncryptAlgorithm {
    AES_256("aes256"),
    AES_128("aes128"),
    RC4_128("rc4-128"),
    RC4_40("rc4-40");

    private final String value;

    EncryptAlgorithm(String value) {
        this.value = value;
    }

    /**
     * @return The value Gotenberg takes.
     */
    public String value() {
        return value;
    }
}
