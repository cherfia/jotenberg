package io.bitizens.pdf;

/**
 * ConformanceProfile is a standard Gotenberg makes a PDF conform to with {@code remediate}, or checks with
 * {@code validate}.
 */
public enum ConformanceProfile {
    PDF_A_1A("PDF/A-1a", 1),
    PDF_A_1B("PDF/A-1b", 1),
    PDF_A_2A("PDF/A-2a", 2),
    PDF_A_2B("PDF/A-2b", 2),
    PDF_A_2U("PDF/A-2u", 2),
    PDF_A_3A("PDF/A-3a", 3),
    PDF_A_3B("PDF/A-3b", 3),
    PDF_A_3U("PDF/A-3u", 3),
    PDF_A_4("PDF/A-4", 4),
    PDF_A_4E("PDF/A-4e", 4),
    PDF_A_4F("PDF/A-4f", 4),
    PDF_UA_1("PDF/UA-1", 0),
    PDF_UA_2("PDF/UA-2", 0),
    FACTUR_X("Factur-X", 3);

    private final String value;
    private final int pdfaPart;

    ConformanceProfile(String value, int pdfaPart) {
        this.value = value;
        this.pdfaPart = pdfaPart;
    }

    /**
     * @return The name Gotenberg takes, such as {@code PDF/A-2b}.
     */
    public String value() {
        return value;
    }

    /**
     * @return Whether this is a PDF/UA profile.
     */
    public boolean isPdfua() {
        return this == PDF_UA_1 || this == PDF_UA_2;
    }

    /**
     * Checks that two profiles may go together: a PDF/A profile, or Factur-X, with the PDF/UA profile of its PDF
     * version — PDF/A-1 to PDF/A-3 and Factur-X with PDF/UA-1, PDF/A-4 with PDF/UA-2.
     *
     * @param archival   The PDF/A or Factur-X profile.
     * @param accessible The PDF/UA profile.
     * @throws IllegalArgumentException If Gotenberg doesn't take the pair.
     */
    static void checkPair(ConformanceProfile archival, ConformanceProfile accessible) {
        boolean valid = !archival.isPdfua() && accessible.isPdfua()
                && (archival.pdfaPart == 4 ? accessible == PDF_UA_2 : accessible == PDF_UA_1);
        if (!valid) {
            throw new IllegalArgumentException("Gotenberg pairs PDF/A-1 to PDF/A-3 and Factur-X with PDF/UA-1, "
                    + "and PDF/A-4 with PDF/UA-2; got " + archival.value + " with " + accessible.value);
        }
    }

    /**
     * @param value A name Gotenberg answers, such as {@code PDF/A-2b}.
     * @return The profile.
     */
    public static ConformanceProfile fromValue(String value) {
        for (ConformanceProfile profile : values()) {
            if (profile.value.equals(value)) {
                return profile;
            }
        }
        throw new IllegalArgumentException("Unknown conformance profile: " + value);
    }
}
