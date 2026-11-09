package io.bitizens.chromium;

import org.json.JSONObject;

import java.util.Objects;

/**
 * Cookie is a cookie Chromium sets before it loads the page.
 */
public final class Cookie {
    /**
     * The cookie's {@code SameSite} attribute.
     */
    public enum SameSite {Strict, Lax, None}

    private final JSONObject json = new JSONObject();

    private Cookie(String name, String value, String domain) {
        json.put("name", Objects.requireNonNull(name, "name"));
        json.put("value", Objects.requireNonNull(value, "value"));
        json.put("domain", Objects.requireNonNull(domain, "domain"));
    }

    /**
     * @param name   The cookie's name.
     * @param value  The cookie's value.
     * @param domain The domain it's sent to.
     * @return The cookie.
     */
    public static Cookie of(String name, String value, String domain) {
        return new Cookie(name, value, domain);
    }

    public Cookie path(String path) {
        json.put("path", path);
        return this;
    }

    public Cookie secure(boolean secure) {
        json.put("secure", secure);
        return this;
    }

    public Cookie httpOnly(boolean httpOnly) {
        json.put("httpOnly", httpOnly);
        return this;
    }

    public Cookie sameSite(SameSite sameSite) {
        json.put("sameSite", sameSite.name());
        return this;
    }

    /**
     * @return The cookie as Gotenberg's {@code cookies} field takes it.
     */
    JSONObject toJson() {
        return new JSONObject(json.toMap());
    }
}
