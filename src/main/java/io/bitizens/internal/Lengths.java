package io.bitizens.internal;

import java.util.Locale;

/**
 * Internal: not part of Jotenberg's public API, and not exported by its module.
 * <p>
 * Lengths checks the lengths Gotenberg takes for paper sizes and margins.
 */
public final class Lengths {
    private Lengths() {
    }

    /**
     * @param field The field, named in the error.
     * @param value A number in inches, or a number with a unit: {@code pt}, {@code px}, {@code in}, {@code mm},
     *              {@code cm} or {@code pc}.
     * @return The length, trimmed and lowercased.
     * @throws IllegalArgumentException If Gotenberg would refuse it.
     */
    public static String check(String field, String value) {
        String trimmed = value.trim().toLowerCase(Locale.ROOT);
        if (!trimmed.matches("\\d+(\\.\\d+)?(pt|px|in|mm|cm|pc)?")) {
            throw new IllegalArgumentException(field + " takes a length such as \"8.5in\", \"210mm\" or \"72pt\", got " + value);
        }
        return trimmed;
    }
}
