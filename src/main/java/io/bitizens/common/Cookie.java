package io.bitizens.common;

/**
 * Represents a cookie to be used in Chromium conversions.
 */
public class Cookie {
    private final String name;
    private final String value;
    private final String domain;
    private final String path;
    private final Boolean secure;
    private final Boolean httpOnly;
    private final String sameSite;

    private Cookie(Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
        this.domain = builder.domain;
        this.path = builder.path;
        this.secure = builder.secure;
        this.httpOnly = builder.httpOnly;
        this.sameSite = builder.sameSite;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDomain() {
        return domain;
    }

    public String getPath() {
        return path;
    }

    public Boolean getSecure() {
        return secure;
    }

    public Boolean getHttpOnly() {
        return httpOnly;
    }

    public String getSameSite() {
        return sameSite;
    }

    public static class Builder {
        private String name;
        private String value;
        private String domain;
        private String path;
        private Boolean secure;
        private Boolean httpOnly;
        private String sameSite;

        public Builder(String name, String value, String domain) {
            this.name = name;
            this.value = value;
            this.domain = domain;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder secure(Boolean secure) {
            this.secure = secure;
            return this;
        }

        public Builder httpOnly(Boolean httpOnly) {
            this.httpOnly = httpOnly;
            return this;
        }

        public Builder sameSite(String sameSite) {
            this.sameSite = sameSite;
            return this;
        }

        public Cookie build() {
            return new Cookie(this);
        }
    }
}

