package dev.inaka.common;

import dev.inaka.common.exceptions.FooterFileNotFoundExceptions;
import dev.inaka.common.exceptions.HeaderFileNotFoundExceptions;
import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


/**
 * ChromiumOptions is a class that provides a builder pattern for configuring Chromium options.
 */
public abstract class AbstractOptions {
    protected final File header;
    protected final File footer;
    protected final String emulatedMediaType;
    protected final String waitDelay;
    protected final String waitForExpression;
    protected final String extraHttpHeaders;
    protected final String failOnConsoleExceptions;
    protected final String failOnHttpStatusCodes;
    protected final String skipNetworkIdleEvent;

    protected AbstractOptions(Builder builder) {
        header = builder.header;
        footer = builder.footer;
        emulatedMediaType = builder.emulatedMediaType;
        waitDelay = builder.waitDelay;
        waitForExpression = builder.waitForExpression;
        extraHttpHeaders = builder.extraHttpHeaders;
        failOnConsoleExceptions = builder.failOnConsoleExceptions;
        failOnHttpStatusCodes = builder.failOnHttpStatusCodes;
        skipNetworkIdleEvent = builder.skipNetworkIdleEvent;
    }


    /**
     * The Builder class is used to construct instances of ChromiumOptions with specific configuration options.
     */
    public abstract static class Builder<T extends AbstractOptions> {
        protected File header = null;
        protected File footer = null;
        protected String emulatedMediaType = EmulatedMediaType.PRINT.mediaType();
        protected String waitDelay = null;
        protected String waitForExpression = null;
        protected String extraHttpHeaders = null;
        protected String failOnConsoleExceptions = "false";
        protected String failOnHttpStatusCodes = "[499,599]";
        protected String skipNetworkIdleEvent = "false";

        /**
         * Sets the header file to be used by Chromium.
         *
         * @param header Header file.
         * @return The Builder instance for method chaining.
         * @throws HeaderFileNotFoundExceptions if header file is not found.
         */
        public Builder<T> addHeader(File header) throws HeaderFileNotFoundExceptions {
            if (!CommonUtils.isHeader(header)) {
                throw new HeaderFileNotFoundExceptions();
            }
            this.header = header;
            return this;
        }

        /**
         * Sets the footer file to be used by Chromium.
         *
         * @param footer Footer file.
         * @return The Builder instance for method chaining.
         * @throws FooterFileNotFoundExceptions if footer file is not found.
         */
        public Builder<T> addFooter(File footer) throws FooterFileNotFoundExceptions {
            if (!CommonUtils.isHeader(footer)) {
                throw new FooterFileNotFoundExceptions();
            }
            this.footer = footer;
            return this;
        }

        /**
         * Sets the emulated media type to be used by Chromium.
         *
         * @param emulatedMediaType Emulated media type (i.e. screen or print).
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addEmulatedMediaType(EmulatedMediaType emulatedMediaType) {
            this.emulatedMediaType = emulatedMediaType.mediaType();
            return this;
        }

        /**
         * Sets the wait delay to be used by Chromium.
         *
         * @param waitDelay Duration (e.g, '5s') to wait when loading an HTML document before conversion.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addWaitDelay(Integer waitDelay) {
            this.waitDelay = waitDelay + "s";
            return this;
        }

        /**
         * Sets the wait expression to be used by Chromium.
         *
         * @param waitForExpression JavaScript's expression to wait before converting a document into PDF until it returns true.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addWaitForExpression(JSObject waitForExpression) {
            this.waitForExpression = waitForExpression.toString();
            return this;
        }

        /**
         * Sets the extra HTTP headers to be used by Chromium.
         *
         * @param extraHttpHeaders Extra HTTP headers as a JSON object.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addExtraHttpHeaders(JSONObject extraHttpHeaders) {
            this.extraHttpHeaders = String.valueOf(extraHttpHeaders);
            return this;
        }

        /**
         * Sets whether to fail on console exceptions.
         *
         * @param failOnConsoleExceptions `true` if there are console exceptions, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addFailOnConsoleExceptions(boolean failOnConsoleExceptions) {
            this.failOnConsoleExceptions = String.valueOf(failOnConsoleExceptions);
            return this;
        }

        /**
         * Sets the HTTP status codes to fail on.
         *
         * @param failOnHttpStatusCodes HTTP status codes to fail on.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addFailOnHttpStatusCodes(ArrayList<Integer> failOnHttpStatusCodes) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < failOnHttpStatusCodes.size(); i++) {
                sb.append(failOnHttpStatusCodes.get(i));
                if (i < failOnHttpStatusCodes.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");

            this.failOnHttpStatusCodes = sb.toString();

            return this;
        }

        /**
         * Sets whether to skip the network idle event.
         *
         * @param skipNetworkIdleEvent `true` to skip the network idle event, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addSkipNetworkIdleEvent(boolean skipNetworkIdleEvent) {
            this.skipNetworkIdleEvent = String.valueOf(skipNetworkIdleEvent);
            return this;
        }

        /**
         * Builds an instance of ChromiumOptions with the specified configuration options.
         **/
        public abstract T build();
    }


}

