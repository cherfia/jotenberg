package io.bitizens.chromium;

/**
 * Screenshot holds the fields of the Chromium screenshot routes. Screenshots make an image, so they take no stages.
 *
 * @param <T> The concrete screenshot type, returned by the fluent setters.
 */
public abstract class Screenshot<T extends Screenshot<T>> extends ChromiumRequest<T> {
    /** @param format PNG by default. @return This screenshot. */
    public T format(ImageFormat format) {
        return set("format", format.format());
    }

    /** @param quality The JPEG quality, from 1 to 100; other formats ignore it. @return This screenshot. */
    public T quality(int quality) {
        if (quality < 1 || quality > 100) {
            throw new IllegalArgumentException("quality runs from 1 to 100, got " + quality);
        }
        return set("quality", quality);
    }

    /** @param width The viewport width, in CSS pixels; 800 by default. @return This screenshot. */
    public T width(int width) {
        return set("width", width);
    }

    /** @param height The viewport height, in CSS pixels; 600 by default. @return This screenshot. */
    public T height(int height) {
        return set("height", height);
    }

    /** @param clip Captures the viewport only, instead of the whole page. @return This screenshot. */
    public T clip(boolean clip) {
        return set("clip", clip);
    }

    /** @param selector Captures the first element matching this CSS selector. @return This screenshot. */
    public T selector(String selector) {
        return set("selector", selector);
    }

    /** @param factor Image pixels per CSS pixel; 2 doubles the resolution. @return This screenshot. */
    public T deviceScaleFactor(double factor) {
        return set("deviceScaleFactor", factor);
    }

    /** @param optimize Encodes faster, for a larger file. @return This screenshot. */
    public T optimizeForSpeed(boolean optimize) {
        return set("optimizeForSpeed", optimize);
    }
}
