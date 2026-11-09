package io.bitizens.internal;

/**
 * Internal: not part of Jotenberg's public API, and not exported by its module.
 * <p>
 * Headers checks request header values. The JDK's HTTP client sends header values as ASCII and replaces any other
 * character with {@code ?}, so a non-ASCII value would reach Gotenberg corrupted.
 */
public final class Headers {
    private Headers() {
    }

    /**
     * @param name  The header, named in the error.
     * @param value The value to send.
     * @return The value.
     * @throws IllegalArgumentException If it holds a character the HTTP client can't send.
     */
    public static String check(String name, String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c > 0x7e || (c < 0x20 && c != '\t')) {
                throw new IllegalArgumentException(name + " can only hold printable ASCII characters, got \"" + value
                        + "\"; for a non-ASCII file name, write the result under it with GotenbergResult.writeTo");
            }
        }
        return value;
    }

    /**
     * Escapes the characters outside ASCII in a JSON text as {@code \\uXXXX}, which keeps it valid JSON.
     *
     * @param json A JSON text.
     * @return The same JSON, in ASCII.
     */
    public static String asciiJson(String json) {
        StringBuilder out = new StringBuilder(json.length());
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c > 0x7e) {
                out.append(String.format("\\u%04x", (int) c));
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
