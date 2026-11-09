package io.bitizens.pdf;

import org.json.JSONObject;

/**
 * RedactRegion is a rectangle whose content a {@code redact} stage removes from the file, in points from the
 * bottom-left corner of the page, 72 points to the inch.
 *
 * @param page   The page, counting from 1.
 * @param x      The left edge.
 * @param y      The bottom edge.
 * @param width  The width.
 * @param height The height.
 */
public record RedactRegion(int page, double x, double y, double width, double height) {
    JSONObject toJson() {
        return new JSONObject()
                .put("page", page)
                .put("x", x)
                .put("y", y)
                .put("width", width)
                .put("height", height);
    }
}
