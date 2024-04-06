package dev.inaka.screenshots;

import dev.inaka.common.AbstractOptions;

/**
 * ScreenshotOptions is a class that represents the options for taking a screenshot.
 */
public final class ScreenshotOptions extends AbstractOptions {

    private final boolean optimizeForSpeed;

    private ScreenshotOptions(Builder builder) {
        super(builder);
        optimizeForSpeed = builder.optimizeForSpeed;
    }

    public static class Builder extends AbstractOptions.Builder<ScreenshotOptions> {

        private boolean optimizeForSpeed = false;

        public Builder() {
        }

        /**
         * Sets whether to optimize the screenshot for speed.
         *
         * @param optimizeForSpeed Whether to optimize the screenshot for speed.
         * @return
         */
        public Builder addOptimizeForSpeed(boolean optimizeForSpeed) {
            this.optimizeForSpeed = optimizeForSpeed;
            return this;
        }

        @Override
        public ScreenshotOptions build() {
            return new ScreenshotOptions(this);
        }
    }
}
