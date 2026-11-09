package io.bitizens.pdf;

/**
 * FacturXLevel is the conformance level of a Factur-X invoice. It must match the level the invoice XML declares.
 */
public enum FacturXLevel {
    MINIMUM("MINIMUM"),
    BASIC_WL("BASIC WL"),
    BASIC("BASIC"),
    EN_16931("EN 16931"),
    EXTENDED("EXTENDED"),
    XRECHNUNG("XRECHNUNG");

    private final String value;

    FacturXLevel(String value) {
        this.value = value;
    }

    /**
     * @return The value Gotenberg takes.
     */
    public String value() {
        return value;
    }
}
