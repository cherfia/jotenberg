package io.bitizens.pdf;

/**
 * AttachmentRelationship is the {@code /AFRelationship} of an embedded file, which makes it a PDF/A-3 associated file.
 */
public enum AttachmentRelationship {
    SOURCE("Source"),
    DATA("Data"),
    ALTERNATIVE("Alternative"),
    SUPPLEMENT("Supplement"),
    UNSPECIFIED("Unspecified");

    private final String value;

    AttachmentRelationship(String value) {
        this.value = value;
    }

    /**
     * @return The value Gotenberg takes.
     */
    public String value() {
        return value;
    }

    /**
     * @param value A value Gotenberg answers, such as {@code Data}.
     * @return The relationship, or {@code null} for an unknown value.
     */
    public static AttachmentRelationship fromValue(String value) {
        for (AttachmentRelationship relationship : values()) {
            if (relationship.value.equals(value)) {
                return relationship;
            }
        }
        return null;
    }
}
