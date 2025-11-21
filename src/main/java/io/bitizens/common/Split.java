package io.bitizens.common;

/**
 * Represents a split configuration for PDF operations.
 */
public class Split {
    private final String mode; // 'pages' or 'intervals'
    private final String span;
    private final Boolean unify;
    private final Boolean flatten;

    private Split(Builder builder) {
        this.mode = builder.mode;
        this.span = builder.span;
        this.unify = builder.unify;
        this.flatten = builder.flatten;
    }

    public String getMode() {
        return mode;
    }

    public String getSpan() {
        return span;
    }

    public Boolean getUnify() {
        return unify;
    }

    public Boolean getFlatten() {
        return flatten;
    }

    public static class Builder {
        private String mode;
        private String span;
        private Boolean unify;
        private Boolean flatten;

        public Builder(String mode, String span) {
            this.mode = mode;
            this.span = span;
        }

        public Builder unify(Boolean unify) {
            this.unify = unify;
            return this;
        }

        public Builder flatten(Boolean flatten) {
            this.flatten = flatten;
            return this;
        }

        public Split build() {
            return new Split(this);
        }
    }
}

