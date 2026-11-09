package io.bitizens.pdf;

import org.json.JSONObject;

/**
 * ComposePlacement draws one page of an input PDF on a new page of a {@code compose} stage. Positions count in points
 * from the bottom-left corner of the new page, 72 points to the inch.
 */
public final class ComposePlacement {
    private final JSONObject json = new JSONObject();

    private ComposePlacement() {
    }

    /**
     * @return A placement of the first page of the only input PDF, on the first new page.
     */
    public static ComposePlacement create() {
        return new ComposePlacement();
    }

    /**
     * @param source The name of the input PDF to draw from; may be left out with one input. Every input PDF must be
     *               placed.
     * @return This placement.
     */
    public ComposePlacement source(String source) {
        json.put("source", source);
        return this;
    }

    /**
     * @param sourcePage The page of {@code source} to draw; 1 by default.
     * @return This placement.
     */
    public ComposePlacement sourcePage(int sourcePage) {
        json.put("sourcePage", sourcePage);
        return this;
    }

    /**
     * @param page The new page to draw on; 1 by default. A page no placement names comes out blank.
     * @return This placement.
     */
    public ComposePlacement page(int page) {
        json.put("page", page);
        return this;
    }

    /**
     * @param x Where the bottom-left corner of the drawn page goes, in points from the left.
     * @param y Where it goes, in points from the bottom.
     * @return This placement.
     */
    public ComposePlacement at(double x, double y) {
        json.put("x", x);
        json.put("y", y);
        return this;
    }

    /**
     * @param scale Greater than 0; 1 by default.
     * @return This placement.
     */
    public ComposePlacement scale(double scale) {
        json.put("scale", scale);
        return this;
    }

    /**
     * @param rotation Degrees, counter-clockwise around the placement's corner.
     * @return This placement.
     */
    public ComposePlacement rotation(double rotation) {
        json.put("rotation", rotation);
        return this;
    }

    JSONObject toJson() {
        return new JSONObject(json.toMap());
    }
}
