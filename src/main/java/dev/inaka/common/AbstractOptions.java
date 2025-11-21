package dev.inaka.common;

import dev.inaka.common.exceptions.FooterFileNotFoundExceptions;
import dev.inaka.common.exceptions.HeaderFileNotFoundExceptions;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


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
    protected final String failOnResourceHttpStatusCodes;
    protected final String failOnResourceLoadingFailed;
    protected final String skipNetworkIdleEvent;
    protected final String generateDocumentOutline;
    protected final String cookies;
    protected final String downloadFrom;
    protected final String metadata;
    protected final String split;
    protected final String userPassword;
    protected final String ownerPassword;
    protected final List<File> embeds;

    protected AbstractOptions(Builder builder) {
        header = builder.header;
        footer = builder.footer;
        emulatedMediaType = builder.emulatedMediaType;
        waitDelay = builder.waitDelay;
        waitForExpression = builder.waitForExpression;
        extraHttpHeaders = builder.extraHttpHeaders;
        failOnConsoleExceptions = builder.failOnConsoleExceptions;
        failOnHttpStatusCodes = builder.failOnHttpStatusCodes;
        failOnResourceHttpStatusCodes = builder.failOnResourceHttpStatusCodes;
        failOnResourceLoadingFailed = builder.failOnResourceLoadingFailed;
        skipNetworkIdleEvent = builder.skipNetworkIdleEvent;
        generateDocumentOutline = builder.generateDocumentOutline;
        cookies = builder.cookies;
        downloadFrom = builder.downloadFrom;
        metadata = builder.metadata;
        split = builder.split;
        userPassword = builder.userPassword;
        ownerPassword = builder.ownerPassword;
        embeds = builder.embeds;
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
        protected String failOnResourceHttpStatusCodes = null;
        protected String failOnResourceLoadingFailed = null;
        protected String skipNetworkIdleEvent = "false";
        protected String generateDocumentOutline = null;
        protected String cookies = null;
        protected String downloadFrom = null;
        protected String metadata = null;
        protected String split = null;
        protected String userPassword = null;
        protected String ownerPassword = null;
        protected List<File> embeds = null;

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
            if (!CommonUtils.isFooter(footer)) {
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
        public Builder<T> addWaitForExpression(String waitForExpression) {
            this.waitForExpression = waitForExpression;
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
         * Sets the HTTP status codes to fail on for resources.
         *
         * @param failOnResourceHttpStatusCodes HTTP status codes to fail on for resources.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addFailOnResourceHttpStatusCodes(ArrayList<Integer> failOnResourceHttpStatusCodes) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < failOnResourceHttpStatusCodes.size(); i++) {
                sb.append(failOnResourceHttpStatusCodes.get(i));
                if (i < failOnResourceHttpStatusCodes.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");
            this.failOnResourceHttpStatusCodes = sb.toString();
            return this;
        }

        /**
         * Sets whether to fail on resource loading failed.
         *
         * @param failOnResourceLoadingFailed `true` to fail on resource loading failed, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addFailOnResourceLoadingFailed(boolean failOnResourceLoadingFailed) {
            this.failOnResourceLoadingFailed = String.valueOf(failOnResourceLoadingFailed);
            return this;
        }

        /**
         * Sets whether to generate document outline.
         *
         * @param generateDocumentOutline `true` to generate document outline, `false` otherwise.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addGenerateDocumentOutline(boolean generateDocumentOutline) {
            this.generateDocumentOutline = String.valueOf(generateDocumentOutline);
            return this;
        }

        /**
         * Sets the cookies to be used by Chromium.
         *
         * @param cookies List of cookies as JSON string.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addCookies(String cookies) {
            this.cookies = cookies;
            return this;
        }

        /**
         * Sets the download from configuration.
         *
         * @param downloadFrom Download from configuration as JSON string.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addDownloadFrom(String downloadFrom) {
            this.downloadFrom = downloadFrom;
            return this;
        }

        /**
         * Sets the metadata to be written.
         *
         * @param metadata Metadata as JSON string.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addMetadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Sets the split configuration.
         *
         * @param split Split configuration as JSON string.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addSplit(String split) {
            this.split = split;
            return this;
        }

        /**
         * Sets the user password for opening the resulting PDF(s).
         *
         * @param userPassword User password.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addUserPassword(String userPassword) {
            this.userPassword = userPassword;
            return this;
        }

        /**
         * Sets the owner password for full access on the resulting PDF(s).
         *
         * @param ownerPassword Owner password.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addOwnerPassword(String ownerPassword) {
            this.ownerPassword = ownerPassword;
            return this;
        }

        /**
         * Sets the files to embed in the generated PDF.
         *
         * @param embeds List of files to embed.
         * @return The Builder instance for method chaining.
         */
        public Builder<T> addEmbeds(List<File> embeds) {
            this.embeds = embeds;
            return this;
        }

        /**
         * Builds an instance of ChromiumOptions with the specified configuration options.
         **/
        public abstract T build();
    }


}

