package io.bitizens.chromium;

import io.bitizens.common.AbstractOptions;

/**
 * The ChromiumOptions class is used to configure Chromium conversion options.
 */
public final class ChromiumOptions extends AbstractOptions {

    private ChromiumOptions(Builder builder) {
        super(builder);
    }

    /**
     * The Builder class is used to construct instances of ChromiumOptions with specific configuration options.
     */
    public static class Builder extends AbstractOptions.Builder<ChromiumOptions> {

        @Override
        public ChromiumOptions build() {
            return new ChromiumOptions(this);
        }
    }
}
